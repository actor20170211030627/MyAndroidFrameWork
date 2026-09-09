package com.actor.myandroidframework.adapter_viewpager;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 适用于 Fragment 页面较多的情况（比如加载大图、复杂布局）
 * <ol>
 *     <li>
 *         当 Fragment 滑出屏幕时，{@link FragmentStatePagerAdapter#destroyItem(ViewGroup, int, Object)} 调用的是 <code>mCurTransaction.remove(fragment)</code>。<br />
 *         <b>意味着</b>：Fragment 的实例被彻底销毁（调用 onDestroy、onDetach），FragmentManager 不再持有引用。
 *     </li>
 *     <li>当用户滑回来时，会调用 {@link FragmentStatePagerAdapter#getItem(int)} 重新创建一个全新的 Fragment 实例。</li>
 *     <li>
 *         在销毁 Fragment 前，会自动调用 {@link Fragment#onSaveInstanceState(Bundle)}，并将这个用户的 Bundle 状态保存在 FragmentManager 的 SavedState 列表中。<br />
 *         当用户滑回来重建 Fragment 时，{@link #getItem(int)} 返回的 Fragment 在 onCreate 阶段会自动接收到之前保存的 Bundle，从而实现状态恢复（比如保持文本输入、滚动位置等）。
 *     </li>
 *     <li>生命周期: 在ViewPager左右滑动过程中, 至少执行4个生命周期, onCreate -> onCreateView -> onDestoryView -> onDestroy, 即 Fragment 在滑动过程中被销毁了</li>
 *     <li>如果 Fragment 不想被回收, 可以设置:viewpager.setOffscreenPageLimit(int limit);</li>
 * </ol>
 * <br />
 * {@link null 推荐使用: }<br />
 * {@link androidx.viewpager2.widget.ViewPager2} & {@link androidx.viewpager2.adapter.FragmentStateAdapter},
 * 它内部有统一的回收机制，不再区分 {@link androidx.fragment.app.FragmentPagerAdapter} 和 {@link FragmentStatePagerAdapter} 这两者，且性能更好、动画更平滑。
 *
 * @author     : ldf
 * @Date       : 2019/3/27 on 10:12
 * @version 1.1
 */
public abstract class BaseFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    protected final List<CharSequence>             titles                    = new ArrayList<>();
    protected final List<Fragment>                 fragments                 = new ArrayList<>();
    protected       boolean                        isRemoveOffscreenPosition = false;
    //是否可打印日志
    protected       boolean                        loggable                  = false;
    protected       ArrayList<Fragment>            mFragments2;
    protected       ArrayList<Fragment.SavedState> mSavedState2;
    protected       FragmentManager                fragmentManager;
    protected       OnFragmentInitListener         onFragmentInitListener;

    public BaseFragmentStatePagerAdapter(@NonNull FragmentManager fm, int size) {
        this(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, size);
    }
    /**
     * @param behavior 显/隐 行为
     * <ol>
     *     <li>{@link #BEHAVIOR_SET_USER_VISIBLE_HINT}: 使用已废弃的 {@link Fragment#setUserVisibleHint(boolean)} 来控制可见性。</li>
     *     <li>{@link #BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT}: 配合 Fragment 的 onResume / onPause 生命周期，当 Fragment 完全可见时才真正 Resume，不可见时仅处于 Pause 状态（但不销毁）。这能让生命周期更符合现代规范。</li>
     * </ol>
     * @param size page数量
     */
    public BaseFragmentStatePagerAdapter(@NonNull FragmentManager fm, int behavior, int size) {
        super(fm, behavior);
        for (int i = 0; i < size; i++) titles.add(null);
        init(fm);
    }

    public BaseFragmentStatePagerAdapter(@NonNull FragmentManager fm, CharSequence[] titles) {
        this(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, titles);
    }
    public BaseFragmentStatePagerAdapter(@NonNull FragmentManager fm, int behavior, CharSequence[] titles) {
        super(fm, behavior);
        if (titles != null) Collections.addAll(this.titles, titles);
        init(fm);
    }

    public BaseFragmentStatePagerAdapter(@NonNull FragmentManager fm, List<String> titles) {
        this(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, titles);
    }
    public BaseFragmentStatePagerAdapter(@NonNull FragmentManager fm, int behavior, List<String> titles) {
        super(fm, behavior);
        if (titles != null) this.titles.addAll(titles);
        init(fm);
    }

    protected void init(FragmentManager fm) {
//        this.fragmentManager = fm;
        for (int i = 0; i < titles.size(); i++) fragments.add(null);
        ArrayList<Fragment> mFragments = getParentFragments();
        ArrayList<Fragment.SavedState> mSavedState = getParentSavedState();
        if (mFragments != null) while (mFragments.size() < titles.size()) mFragments.add(null);
        if (mSavedState != null) while (mSavedState.size() < titles.size()) mSavedState.add(null);
    }

    /**
     * 务必返回: XxxFragment.newInstance(Xxx... xxx);
     */
    @NonNull
    @Override
    public abstract Fragment getItem(int position);

    /**
     * 获取Fragment
     * @param position 第几个Fragment
     */
    @Nullable
    public <T extends Fragment> T getFragment(@NonNull ViewPager viewPager, int position) {
        if (position < 0 || position >= titles.size()) return null;
        int offscreenPageLimit = viewPager.getOffscreenPageLimit();
        int currentItem = viewPager.getCurrentItem();
        if (Math.abs(position - currentItem) <= offscreenPageLimit) return (T) super.instantiateItem(viewPager, position);
        return (T) fragments.get(position); //安慰代码
    }

    //获取每个pager的title
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return position >= 0 && titles.size() > position ? titles.get(position) : null;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    /**
     * 实例化 or 从FragmentManager中获取
     * @param container 包裹item的容器, 例: ViewPager
     * @param position 第几个Fragment
     * @return Fragment
     */
    @NonNull
    @Override
    public Fragment instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        if (loggable) LogUtils.errorFormat("position = %d, fragments.size() = %d, fragment = %s", position, fragments.size(), fragment);
        fragments.set(position, fragment);
        if (fragment.getFragmentManager() == null) {
            //应该不会再出现这种情况了, if进来了, 说明父类 mFragments 对fragment有缓存, 请检查父类 mFragments 对象里的内容和 fragments 里的内容的差别!
            if (loggable) LogUtils.error("fragmentManager = null!!!");
//            boolean isSuccess = fragmentSetFragmentManager(fragment);
//            if (loggable) LogUtils.errorFormat("fragmentManager = null!!!, isSuccess = %b", isSuccess);
        }
        if (onFragmentInitListener != null) {
            onFragmentInitListener.onFragmentInited(container, position, fragment);
        }
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        if (loggable) LogUtils.errorFormat("position = %d, isRemoveOffscreenPosition = %b", position, isRemoveOffscreenPosition);
        if (isRemoveOffscreenPosition) {
            ArrayList<Fragment> mFragments = getParentFragments();
            ArrayList<Fragment.SavedState> mSavedState = getParentSavedState();
            if (mFragments != null) mFragments.remove(position);
            if (mSavedState != null) mSavedState.remove(position);
        } else fragments.set(position, null);
        isRemoveOffscreenPosition = false;
    }

    /**
     * @param container ViewPager
     * @param position 切换到了某个position
     * @param object ViewPager切换到了 position, position 位置的 Fragment
     */
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (loggable) LogUtils.errorFormat("position = %d, object = %s", position, object);
        //instantiateItem()里父类 mFragments.remove不干净的问题, 应该修复了, 所以下方代码用不上了
        /**
         * mCurTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED); =>
         * BackStackRecord.setMaxLifecycle(BackStackRecord.java:248) =>
         *      Caused by: java.lang.IllegalArgumentException: Cannot setMaxLifecycle for Fragment not attached to FragmentManager FragmentManager{aa246d1 in HostCallbacks{5138810}}}
         */
//        if (object instanceof Fragment && ((Fragment) object).getFragmentManager() == null) {
//            boolean isSuccess = fragmentSetFragmentManager((Fragment) object);
//            if (loggable) LogUtils.errorFormat("fragmentManager = null!!!, isSuccess = %b", isSuccess);
//            if (!isSuccess) return;
//        }
        super.setPrimaryItem(container, position, object);
//        Fragment currentFragment = (Fragment) object;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        return super.getItemPosition(object);
        int index = fragments.indexOf(object);
        if (loggable) LogUtils.errorFormat("index = %d, object = %s", index, object);
        // 该 View 已不在数据源中（即已被 removePage 移除）, 返回 POSITION_NONE，告诉 ViewPager 销毁它
        if (index == -1) return POSITION_NONE;
        // 返回该 View 现在的实际位置, 如果位置没变，ViewPager 会复用；如果变了，ViewPager 会尝试重新布局
        return index;
    }

    /**
     * 在指定位置添加Fragment, 在 {@link #getItem(int)} 初始化你添加的Fragment
     * @param position 插入的位置（0 到 size）
     * @param title    该页面标题
     * @return 返回真正的插入位置, -1插入失败
     */
    public int addFragment(int position, @Nullable CharSequence title) {
        if (position < 0) {
            position = 0;
        } else if (position > titles.size()) position = titles.size();
        ArrayList<Fragment> mFragments = getParentFragments();
        ArrayList<Fragment.SavedState> mSavedState = getParentSavedState();
        if (mFragments == null || mSavedState == null) {
            if (loggable) LogUtils.errorFormat("mFragments = %s\nmSavedState = %s", mFragments, mSavedState);
            return -1;
        }
        titles.add(position, title);
        fragments.add(position, null);
        mFragments.add(position, null);
        mSavedState.add(position, null);
        notifyDataSetChanged();
        return position;
    }

    /**
     * 移除指定位置的Fragment, 不能使用父类的 {@link #mFragments}, 因为:
     * <ol>
     *     <li>if 这儿调用 {@link #mFragments}.{@link ArrayList#remove(int) remove(int)} 方法, 虽然 {@link #getItemPosition(Object)} 正常, 但 {@link #destroyItem(ViewGroup, int, Object)} 的时候fragment已被移除</li>
     *     <li>if 这儿不调用 {@link #mFragments}.{@link ArrayList#remove(int) remove(int)} 方法, 而是调用 {@link #mFragments}.{@link ArrayList#set(int, Object) set(int, null)} 置空fragment, 这样连 {@link #getItemPosition(Object)} 都有问题</li>
     *     <li>所以: 还是自己额外维护1个 fragments</li>
     * </ol>
     * @param position 要移除的位置
     * @return 返回已经移除的Fragment
     */
    @Nullable
    public Fragment removeFragment(@NonNull ViewPager viewPager, int position) {
        int offscreenPageLimit = viewPager.getOffscreenPageLimit();
        int currentItem = viewPager.getCurrentItem();
        if (loggable) LogUtils.errorFormat("position = %d, currentItem = %d, offscreenPageLimit = %d, fragments.size() = %d", position, currentItem, offscreenPageLimit, fragments.size());
        if (position < 0 || position >= titles.size()) return null;
        titles.remove(position);
        Fragment fragment = fragments.remove(position);
        isRemoveOffscreenPosition = Math.abs(position - currentItem) <= offscreenPageLimit;
        if (!isRemoveOffscreenPosition) {
            ArrayList<Fragment> mFragments = getParentFragments();
            ArrayList<Fragment.SavedState> mSavedState = getParentSavedState();
            if (mFragments != null) mFragments.remove(position);
            if (mSavedState != null) mSavedState.remove(position);
        }
        notifyDataSetChanged();
        return fragment;
    }

    /**
     * 交换两个页面的位置
     * @param fromPosition 源位置
     * @param toPosition   目标位置
     */
    public void exchangeFragment(int fromPosition, int toPosition) {
        int size = titles.size();
        if (fromPosition < 0 || fromPosition >= size || toPosition < 0 || toPosition >= size) {
            if (loggable) LogUtils.errorFormat("索引越界, 不能交换位置: fromPosition = %d, toPosition = %d, titles.size() = %d", fromPosition, toPosition, size);
            return;
        }
        if (fromPosition == toPosition) return;
        // 交换列表中的两个元素
        Collections.swap(titles, fromPosition, toPosition);
        Collections.swap(fragments, fromPosition, toPosition);
        ArrayList<Fragment> mFragments = getParentFragments();
        ArrayList<Fragment.SavedState> mSavedState = getParentSavedState();
        if (mFragments != null) Collections.swap(mFragments, fromPosition, toPosition);
        if (mSavedState != null) Collections.swap(mSavedState, fromPosition, toPosition);
        // 通知 ViewPager 数据变了，它会根据 getItemPosition 重新映射
        notifyDataSetChanged();
    }

    public BaseFragmentStatePagerAdapter setLoggable(boolean loggable) {
        this.loggable = loggable;
        return this;
    }

    /**
     * 设置Fragment初始化监听
     */
    public void setOnFragmentInitListener(OnFragmentInitListener onFragmentInitListener) {
        this.onFragmentInitListener = onFragmentInitListener;
    }

    protected ArrayList<Fragment> getParentFragments() {
        if (mFragments2 != null) return mFragments2;
        Field[] declaredFields = FragmentStatePagerAdapter.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!ArrayList.class.isAssignableFrom(declaredField.getType())) continue;
            Type genericType = declaredField.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                if (actualTypeArguments.length == 1) {
                    Type arg = actualTypeArguments[0];
                    if (arg == Fragment.class) {
                        declaredField.setAccessible(true); // 突破private
                        try {
                            ArrayList<Fragment> mFragments = (ArrayList<Fragment>) declaredField.get(this);
                            if (mFragments != null) return mFragments2 = mFragments;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return this.mFragments2 = ReflectUtils.reflect(this).field("mFragments").get();
    }

    protected ArrayList<Fragment.SavedState> getParentSavedState() {
        if (mSavedState2 != null) return mSavedState2;
        Field[] declaredFields = FragmentStatePagerAdapter.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!ArrayList.class.isAssignableFrom(declaredField.getType())) continue;
            Type genericType = declaredField.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                if (actualTypeArguments.length == 1) {
                    Type arg = actualTypeArguments[0];
                    if (arg == Fragment.SavedState.class) {
                        declaredField.setAccessible(true); // 突破private
                        try {
                            ArrayList<Fragment.SavedState> mSavedState = (ArrayList<Fragment.SavedState>) declaredField.get(this);
                            if (mSavedState != null) return mSavedState2 = mSavedState;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return this.mSavedState2 = ReflectUtils.reflect(this).field("mSavedState").get();
    }

    //给 fragment 填充 fragmentManager
    @Deprecated
    protected synchronized boolean fragmentSetFragmentManager(Fragment fragment) {
        Field[] allFields = Fragment.class.getDeclaredFields();
        for (Field field : allFields) {
            if (FragmentManager.class.equals(field.getType())) {
                field.setAccessible(true);
                try {
                    FragmentManager fm = (FragmentManager) field.get(fragment);
                    if (fm != null) continue;
                    field.set(fragment, fragmentManager);
                    if (fragment.getFragmentManager() == null) {
                        field.set(fragmentManager, null);
                    } else return true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        ReflectUtils.reflect(fragment).field("mFragmentManager", fragment);
        return fragment.getFragmentManager() == null;
    }
}
