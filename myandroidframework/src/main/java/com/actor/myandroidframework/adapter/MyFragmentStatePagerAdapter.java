package com.actor.myandroidframework.adapter;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Description: ViewPager的Adapter, 填充Fragment
 * 如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用(对fragment进行完全的添加和删除操)
 * 或者闪屏页面, 几张大图的时候, 也可以选择这个, 管理占用更少内存
 * 在ViewPager左右滑动过程中, 会重复至少执行4个生命周期:
 * onCreate -> onCreateView -> onDestoryView -> onDestroy
 * 即, Fragment在滑动过程中被销毁了
 * 很多页面的时候, 推荐使用
 * 如果viewpager中的Fragment不想重复请求网络, 可以设置:viewpager.setOffscreenPageLimit(int limit);
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/27 on 10:12
 * @version 1.1
 */
public abstract class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private int sizeForMyFragmentStatePagerAdapter;
    private SparseArray<Fragment> fragmentsForMyFragmentStatePagerAdapter;
    private String[] titles;

    public MyFragmentStatePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.sizeForMyFragmentStatePagerAdapter = size;
        fragmentsForMyFragmentStatePagerAdapter = new SparseArray<>(size);
    }

    public MyFragmentStatePagerAdapter(FragmentManager fm, @NonNull String[] titles) {
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
    public @Nullable <T extends Fragment> T getFragment(int position) {
        if (fragmentsForMyFragmentStatePagerAdapter.size() > position) {
            return (T) fragmentsForMyFragmentStatePagerAdapter.get(position);
        }
        return null;
    }

    //获取每个pager的title
    @Nullable
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

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        fragmentsForMyFragmentStatePagerAdapter.remove(position);
        super.destroyItem(container, position, object);
    }

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
    public void registerDataSetObserver(@NonNull DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }
}
