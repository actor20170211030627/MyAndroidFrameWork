package com.actor.myandroidframework.adapter;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Description:
 * FragmentPagerAdapter基类, 处理系统 系统恢复页面数据 & 旋转屏幕 等
 * FragmentPagerAdapter主要用于页面较少的情况, 会使Fragment重复走生命周期:
 * onCreateView -> onDestroyView
 * 即, 没有执行onDestroy, Fragment并没有被销毁
 * 只有几个页面的时候, 推荐使用.
 * 如果viewpager中的Fragment不想重复请求网络, 可以设置:viewpager.setOffscreenPageLimit(int limit);
 *
 * 1.★注意事项★:(以前的注意事项, 现在不一定适用)
 *   如果在ViewPager中嵌套ListView ,GridView ...要在ListView ,GridView ...的"Adapter"中重写下面方法,
 *   否则报错:java.lang.IllegalArgumentException: The observer is null.
 *   @Override
 *   public void unregisterDataSetObserver(DataSetObserver observer) {
 *       if (observer != null) {
 *           super.unregisterDataSetObserver(observer);
 *       }
 *   }
 *
 * 2.ExpandableListView不用重写上面的方法(以前的注意事项, 现在不一定适用)
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/27 on 19:50
 * @version 1.1
 */
public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private int sizeForMyFragmentStatePagerAdapter;
    private SparseArray<Fragment> fragmentsForMyFragmentStatePagerAdapter;
    private String[] titles;

    public BaseFragmentPagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.sizeForMyFragmentStatePagerAdapter = size;
        fragmentsForMyFragmentStatePagerAdapter = new SparseArray<>();
    }

    public BaseFragmentPagerAdapter(FragmentManager fm, @NonNull String[] titles) {
        super(fm);
        this.sizeForMyFragmentStatePagerAdapter = titles.length;
        fragmentsForMyFragmentStatePagerAdapter = new SparseArray<>(sizeForMyFragmentStatePagerAdapter);
        this.titles = titles;
    }

    /**
     * 务必返回: XxxFragment.newInstance(Xxx... xxx); ,这样系统恢复时, 会重写调用Fragment的onCreate
     * @param position
     * @return
     */
    @Override
    public abstract Fragment getItem(int position);

    /**
     * 获取Fragment
     * @param position 第几个Fragment
     */
    public @Nullable
    <T extends Fragment> T  getFragment(int position) {
        if (fragmentsForMyFragmentStatePagerAdapter.size() > position) {
            return (T) fragmentsForMyFragmentStatePagerAdapter.get(position);
        }
        return null;
    }

    //获取每个pager的title
    @Override
    public CharSequence getPageTitle(int position) {
        return titles == null ? null : titles.length > position ? titles[position] : null;
    }

    @Override
    public int getCount() {
        return sizeForMyFragmentStatePagerAdapter;
    }

    //实例化
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentsForMyFragmentStatePagerAdapter.put(position, fragment);
        return fragment;
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = fragments.get(position);
//        //判断当前的fragment是否已经被添加进入Fragmentanager管理器中
//        if (!fragment.isAdded()) {
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.add(fragment, fragment.getClass().getSimpleName());
//            //不保存系统参数，自己控制加载的参数
//            transaction.commitAllowingStateLoss();
//            //手动调用,立刻加载Fragment片段
//            manager.executePendingTransactions();
//        }
//        //必须判空,否则报错
//        if (fragment.getView() != null && fragment.getView().getParent() == null) {
//            //添加布局
//            container.addView(fragment.getView());
//        }
//        return fragment.getView();
//    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        fragmentsForMyFragmentStatePagerAdapter.remove(position);
        super.destroyItem(container, position, object);
    }

//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
    //移除布局,如果什么都不写,就不移除布局,就不会重复请求网络
//        if (fragments.get(position).getView() != null) {
//            container.removeView(fragments.get(position).getView());
//        }
//    }



    /**
     * (以前的注意事项, 现在不一定适用)
     * 当"切换账号"的时候, 如果FragmentFactory不clear(); fragment就不会销毁, 调用下方super会空指针:
     * java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Object android
     * .util.SparseArray.get(int)' on a null object reference
     */
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }

    /**
     * (以前的注意事项, 现在不一定适用)
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}
