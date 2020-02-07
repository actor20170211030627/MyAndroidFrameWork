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

import java.util.List;

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
 * Author     : 李大发
 * Date       : 2019/3/27 on 10:12
 * @version 1.1
 */
public abstract class BaseFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    protected int                   sizeOfMyFragmentStatePagerAdapter;
    protected SparseArray<Fragment> fragmentsOfMyFragmentStatePagerAdapter;
    protected String[]              titles;

    public BaseFragmentStatePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.sizeOfMyFragmentStatePagerAdapter = size;
        fragmentsOfMyFragmentStatePagerAdapter = new SparseArray<>(size);
    }

    public BaseFragmentStatePagerAdapter(FragmentManager fm, @NonNull String[] titles) {
        super(fm);
        this.sizeOfMyFragmentStatePagerAdapter = titles.length;
        fragmentsOfMyFragmentStatePagerAdapter = new SparseArray<>(sizeOfMyFragmentStatePagerAdapter);
        this.titles = titles;
    }

    public BaseFragmentStatePagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        if (titles != null) {
            this.sizeOfMyFragmentStatePagerAdapter = titles.size();
            fragmentsOfMyFragmentStatePagerAdapter = new SparseArray<>(sizeOfMyFragmentStatePagerAdapter);
            this.titles = (String[]) titles.toArray();
        }
    }

    /**
     * 务必返回: XxxFragment.newInstance(Xxx... xxx); ,这样系统恢复时, 会重新调用Fragment的onCreate
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
        if (fragmentsOfMyFragmentStatePagerAdapter.size() > position) {
            return (T) fragmentsOfMyFragmentStatePagerAdapter.get(position);
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
        return sizeOfMyFragmentStatePagerAdapter;
    }

    //实例化
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentsOfMyFragmentStatePagerAdapter.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        fragmentsOfMyFragmentStatePagerAdapter.remove(position);
        super.destroyItem(container, position, object);
    }

    /**
     * @param container
     * @param position 切换到了某个position
     * @param object ViewPager切换到了 position, position 位置的 Fragment
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
//        Fragment currentFragment = (Fragment) object;
    }

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
