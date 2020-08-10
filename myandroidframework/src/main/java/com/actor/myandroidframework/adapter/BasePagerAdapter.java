package com.actor.myandroidframework.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Description: 适用于 ViewPager 的 pager 不是 Fragment 的情况, 比如轮播图示例:
 *
 * 1.用法
 * viewPager.setAdapter(new BasePagerAdapter(imageRes));
 *
 * Author     : 李大发
 * Date       : 2019/3/27 on 20:03
 */
public abstract class BasePagerAdapter extends PagerAdapter {

    protected int                   sizeForAdapter;
    protected String[]              titlesForFragments;

    public BasePagerAdapter(int size) {
        this.sizeForAdapter = size;
    }

    public BasePagerAdapter(@NonNull String[] titles) {
        this.sizeForAdapter = titles.length;
        this.titlesForFragments = titles;
    }

    public BasePagerAdapter(List<String> titles) {
        if (titles != null) {
            this.sizeForAdapter = titles.size();
            //java.lang.ClassCastException: java.lang.Object[] cannot be cast to java.lang.String[]
//            this.titlesForFragments = (String[]) titles.toArray();

            titlesForFragments = new String[sizeForAdapter];
            titlesForFragments = titles.toArray(titlesForFragments);
        }
    }

    @Override
    public int getCount() {
        return sizeForAdapter;
    }

    /**
     * 初始化Item, 子类实现, 例:
     *      ImageView iv = new ImageView(container.getContext());
     *      container.addView(iv);
     *      return iv;
     */
    @Override
    public abstract Object instantiateItem(ViewGroup container, int position);

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titlesForFragments == null ? null : titlesForFragments.length > position ? titlesForFragments[position] : null;
    }

    /**
     * @param container
     * @param position 切换到了某个position
     * @param object ViewPager切换到了 position, position 位置的 Object
     */
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }

    //移除布局
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
