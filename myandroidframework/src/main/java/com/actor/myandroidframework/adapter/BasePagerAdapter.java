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

    protected int                   sizeOfItem;
    protected String[]              titles;

    public BasePagerAdapter(int size) {
        this.sizeOfItem = size;
    }

    public BasePagerAdapter(@NonNull String[] titles) {
        this.sizeOfItem = titles.length;
        this.titles = titles;
    }

    public BasePagerAdapter(List<String> titles) {
        if (titles != null) {
            this.sizeOfItem = titles.size();
            this.titles = (String[]) titles.toArray();
        }
    }

    @Override
    public int getCount() {
        return sizeOfItem;
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
        return titles == null ? null : titles.length > position ? titles[position] : null;
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
