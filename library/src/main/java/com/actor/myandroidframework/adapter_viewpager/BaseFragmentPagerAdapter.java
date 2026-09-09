package com.actor.myandroidframework.adapter_viewpager;

import android.os.Parcelable;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 适用于 <code>Fragment</code> 页面较少的情况（如 2~4 个 Tab 导航页）
 * <ol>
 *     <li>
 *         当 Fragment 滑出屏幕时，{@link FragmentPagerAdapter#destroyItem(ViewGroup, int, Object)} 调用的是 <code>mCurTransaction.detach(fragment)</code>（分离），而不是 remove。<br />
 *         <b>意味着</b>：Fragment 的实例（对象）一直驻留在 FragmentManager 中，没有被销毁。
 *     </li>
 *     <li>当用户滑回来时，{@link FragmentPagerAdapter#instantiateItem(ViewGroup, int)} 调用 <code>mCurTransaction.attach(fragment)</code> 重新绑定视图，无需重新创建实例。</li>
 *     <li>由于 Fragment 实例被保留，开发者不需要手动保存<b>状态</b>。Fragment 内部的 <code>onSaveInstanceState</code> 和 <code>onViewStateRestored</code> 会正常伴随 detach/attach 工作。</li>
 *     <li>{@link null 缺点:} 如果页面非常多，内存中会同时持有所有 Fragment 实例，容易导致内存压力。</li>
 *     <li>生命周期: 重复执行 onCreateView -> onDestroyView, 不会执行onDestroy</li>
 *     <li>如果 Fragment 不想被回收导致重走生命周期, 可以设置:viewpager.setOffscreenPageLimit(int limit);</li>
 * </ol>
 * <br />
 * ★注意事项★:(以前的注意事项, 现在不一定适用) <br />
 * 1.如果在Pager中嵌套ListView ,GridView ...要在ListView ,GridView ...的"Adapter"中重写下面方法,
 *   否则报错:java.lang.IllegalArgumentException: The observer is null.
 *   <pre>
 *   <code>@</code>Override
 *   public void unregisterDataSetObserver(DataSetObserver observer) {
 *       if (observer != null) {
 *           super.unregisterDataSetObserver(observer);
 *       }
 *   }
 *   </pre>
 *
 * 2.ExpandableListView不用重写上面的方法(以前的注意事项, 现在不一定适用)<br />
 * <br />
 * 3.{@link null 推荐使用: }<br />
 * {@link androidx.viewpager2.widget.ViewPager2} & {@link androidx.viewpager2.adapter.FragmentStateAdapter},
 * 它内部有统一的回收机制，不再区分 {@link FragmentPagerAdapter} 和 {@link androidx.fragment.app.FragmentStatePagerAdapter} 这两者，且性能更好、动画更平滑。
 *
 * @author     : ldf
 * @date       : 2019/3/27 on 19:50
 * @version 1.1
 */
public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    protected final List<CharSequence>     titles                    = new ArrayList<>();
    protected final List<Fragment>         fragments                 = new ArrayList<>();
    protected final SparseIntArray         itemIds                   = new SparseIntArray();
    protected       boolean                isRemoveOffscreenPosition = false;
    //是否可打印日志
    protected       boolean                loggable                  = false;
    protected       int                    itemId;
    protected       FragmentTransaction    mCurTransaction2;
    protected       OnFragmentInitListener onFragmentInitListener;

    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, int size) {
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
    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior, int size) {
        super(fm, behavior);
        for (int i = 0; i < size; i++) {
            this.titles.add(null);
            this.fragments.add(null);
            this.itemIds.put(i, i);
            this.itemId = size;
        }
    }

    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, CharSequence[] titles) {
        this(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, titles);
    }
    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior, CharSequence[] titles) {
        super(fm, behavior);
        if (titles != null) {
            Collections.addAll(this.titles, titles);
            for (int i = 0; i < titles.length; i++) {
                this.fragments.add(null);
                this.itemIds.put(i, i);
                this.itemId = titles.length;
            }
        }
    }

    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, List<String> titles) {
        this(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, titles);
    }
    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior, List<String> titles) {
        super(fm, behavior);
        if (titles != null) {
            this.titles.addAll(titles);
            for (int i = 0; i < titles.size(); i++) {
                this.fragments.add(null);
                this.itemIds.put(i, i);
                this.itemId = titles.size();
            }
        }
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
    public <T extends Fragment> T getFragment(int position) {
        return position >= 0 && fragments.size() > position ? (T) fragments.get(position) : null;
    }

    //获取每个pager的title
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
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        if (loggable) LogUtils.errorFormat("position = %d, fragments.size() = %d, fragment = %s", position, fragments.size(), fragment);
        fragments.set(position, fragment);
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
            FragmentTransaction mCurTransaction = getParentFragmentTransaction();
            //彻底移除销毁
            if (mCurTransaction != null) mCurTransaction.remove((Fragment) object);
        } else {
            fragments.set(position, null);
        }
        isRemoveOffscreenPosition = false;
    }

    /**
     * @param container ViewPager
     * @param position 切换到了某个position
     * @param object ViewPager切换到了 position, position 位置的 Fragment
     */
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
//        Fragment currentFragment = (Fragment) object;
    }

    @Override
    public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
        super.restoreState(state, loader);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        return super.getItemPosition(object);
        int index = fragments.indexOf(object);
        if (loggable) LogUtils.errorFormat("object = %s, index = %d", object, index);
        // 该 View 已不在数据源中（即已被 removePage 移除）, 返回 POSITION_NONE，告诉 ViewPager 销毁它
        if (index == -1) return POSITION_NONE;
        // 返回该 View 现在的实际位置, 如果位置没变，ViewPager 会复用；如果变了，ViewPager 会尝试重新布局
        return index;
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        if (loggable) LogUtils.errorFormat("position = %d, itemId = %d", position, itemIds.get(position));
        return itemIds.get(position);
    }

    /**
     * 在指定位置添加页面, 在 {@link #getItem(int)} 初始化你添加的Page
     * @param position 插入的位置（0 到 size）
     * @param title    该页面标题
     * @return 返回真正的插入位置
     */
    public int addFragment(int position, @Nullable CharSequence title) {
        if (position < 0) {
            position = 0;
        } else if (position > titles.size()) position = titles.size();
        titles.add(position, title);
        fragments.add(position, null);
        for (int i = itemIds.size() - 1; i >= position; i--) itemIds.put(i + 1, itemIds.get(i));
        itemIds.put(position, itemId ++);
        notifyDataSetChanged();
        return position;
    }

    /**
     * 移除指定位置的页面
     * @param position 要移除的位置
     * @return 返回已经移除的Fragment
     */
    @Nullable
    public Fragment removeFragment(@NonNull ViewPager viewPager, int position) {
        if (position < 0 || position >= titles.size()) return null;
        int offscreenPageLimit = viewPager.getOffscreenPageLimit();
        int currentItem = viewPager.getCurrentItem();
        if (loggable) LogUtils.errorFormat("offscreenPageLimit = %d, currentItem = %d, position = %d, fragments.size() = %d", offscreenPageLimit, currentItem, position, fragments.size());
        titles.remove(position);
        Fragment fragment = fragments.remove(position);
        for (int i = position; i < itemIds.size() - 1; i++) itemIds.put(i, itemIds.get(i + 1));
        itemIds.removeAt(itemIds.size() - 1);
        isRemoveOffscreenPosition = Math.abs(position - currentItem) <= offscreenPageLimit;
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
        int fromItemId = itemIds.get(fromPosition);
        int toItemId = itemIds.get(toPosition);
        itemIds.put(fromPosition, toItemId);
        itemIds.put(toPosition, fromItemId);
        // 通知 ViewPager 数据变了，它会根据 getItemPosition 重新映射
        notifyDataSetChanged();
    }

    public BaseFragmentPagerAdapter setLoggable(boolean loggable) {
        this.loggable = loggable;
        return this;
    }

    /**
     * 设置Fragment初始化监听
     */
    public void setOnFragmentInitListener(OnFragmentInitListener onFragmentInitListener) {
        this.onFragmentInitListener = onFragmentInitListener;
    }


    @Nullable
    protected FragmentTransaction getParentFragmentTransaction() {
        if (mCurTransaction2 != null) return mCurTransaction2;
        Field[] declaredFields = FragmentPagerAdapter.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            // 原始类型必须是 FragmentTransaction
            if (FragmentTransaction.class.isAssignableFrom(declaredField.getType())) {
                declaredField.setAccessible(true); // 突破private
                try {
                    FragmentTransaction fragmentTransaction = (FragmentTransaction) declaredField.get(this);
                    if (fragmentTransaction != null) return mCurTransaction2 = fragmentTransaction;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mCurTransaction2 = ReflectUtils.reflect(this).field("mCurTransaction").get();
    }
}
