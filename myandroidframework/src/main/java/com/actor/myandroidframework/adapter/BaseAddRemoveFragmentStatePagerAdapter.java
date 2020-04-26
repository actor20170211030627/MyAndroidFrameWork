package com.actor.myandroidframework.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 能动态增删Fragment的Adapter
 *
 * @author : 李大发
 * date    : 2020/4/25 on 19:10
 * @version 1.0
 */
// TODO: 2020/4/26 还没用过, 待测试...
public abstract class BaseAddRemoveFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    protected List<String>          titles    = new ArrayList<>();
//    protected SparseArray<Fragment> fragments = new SparseArray<>();

    public BaseAddRemoveFragmentStatePagerAdapter(FragmentManager fm, @Nullable List<String> titles) {
        super(fm);
        if (titles != null) this.titles.addAll(titles);
    }

    /**
     * 务必返回: XxxFragment.newInstance(Xxx... xxx); ,这样系统恢复时, 会重新调用Fragment的onCreate
     */
    @Override
    public abstract Fragment getItem(int position);

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return getCount() > position ? titles.get(position) : null;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    /**
     * 增加Fragment, 务必保证 {@link #getItem(int)} 要有新增加的 Fragment 返回
     */
    public void addFragment(String title) {
        titles.add(title);
        notifyDataSetChanged();
//        viewPager.setCurrentItem(getCount() - 1, true);//跳到最后一个页面
//        viewPager.setOffscreenPageLimit(getCount());//每次动态添加页面后重设viewpager缓存数量
    }

    /**
     * 删除Fragment
     */
    public void removeFragment(int position/*, ViewPager viewPager*/) {
        if (getCount() > position) {
            titles.remove(position);
//            viewPager.removeViewAt(position);
            notifyDataSetChanged();
//        viewPager.setOffscreenPageLimit(getCount());//每次动态删除页面后重设viewpager缓存数量
        }
    }

    //重写此方法, 让Adapter能动态增删Fragment
    @Override
    public int getItemPosition(@NonNull Object object) {
//        return super.getItemPosition(object);
        //返回这个是强制ViewPager不缓存，每次滑动都刷新视图
        return POSITION_NONE;
    }
}
