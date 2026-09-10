package com.actor.myandroidframework.adapter_viewpager;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.actor.myandroidframework.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 适用于 ViewPager 的 pager 是 View, 而不是 Fragment 的情况, 比如轮播图示例: <br />
 * 1.用法
 * <pre>
 * viewPager.setAdapter(new BasePagerAdapter(titles));
 * </pre>
 * 2.如果 Page 对应的 View 不想被回收, 可以设置: {@link ViewPager#setOffscreenPageLimit(int)}; <br />
 * @author     : ldf
 * @date       : 2019/3/27 on 20:03
 */
public abstract class BasePagerAdapter extends PagerAdapter {

    protected final List<CharSequence> titles                    = new ArrayList<>();
    protected final List<View>         mPageViews                = new ArrayList<>();
    protected       boolean            isRemoveOffscreenPosition = false;
    //是否可打印日志
    protected boolean                  loggable                  = false;

    public BasePagerAdapter(@IntRange(from = 0) int size) {
        if (size <= 0) return;
        for (int i = 0; i < size; i++) {
            titles.add(null);
            mPageViews.add(null);
        }
    }

    public BasePagerAdapter(CharSequence[] titles) {
        if (titles != null) {
            Collections.addAll(this.titles, titles);
            for (int i = 0; i < titles.length; i++) mPageViews.add(null);
        }
    }

    public BasePagerAdapter(List<String> titles) {
        if (titles != null) {
            this.titles.addAll(titles);
            for (int i = 0; i < titles.size(); i++) mPageViews.add(null);
        }
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    /**
     * 当显示的页面将要发生变化时调用。
     * @param container ViewPager
     */
    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        super.startUpdate(container);
    }

    /**
     * 初始化 [{@link ViewPager#getCurrentItem()} ± {@link ViewPager#getOffscreenPageLimit()}] 范围内的Page <br />
     * 添加了Page缓存, 不要重写此方法
     * @param container 包裹item的容器, 例: ViewPager
     */
    @NonNull
    @Override
    public final Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mPageViews.get(position);
        if (loggable) LogUtils.errorFormat("position = %d, mPageViews.size() = %d, view = %s", position, mPageViews.size(), view);
        if (view == null) {
            view = getItem(container, position);
            mPageViews.set(position, view);
        }
        if (container != view.getParent()) {
            if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);
            //container 默认3个child, 指定position很容易索引越界
//            container.addView(view, position);
            container.addView(view);
        }
        return view;
    }

    /**
     * 实例化 Page 对应的 View, 例:
     * <pre>
     *      ImageView iv = new ImageView(container.getContext());
     *      //container.addView(iv);    //不用addView了, 直接返回view就行
     *      return iv;
     * </pre>
     * @param container 包裹item的容器, 例: ViewPager
     * @param position 第几个Item
     * @return
     */
    @NonNull
    public abstract View getItem(@NonNull ViewGroup container, int position);

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return position >= 0 && titles.size() > position ? titles.get(position) : null;
    }

    /**
     * 获取Page宽度
     * @param position 第几个page
     * @return (0.f-1.f]
     */
    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    /**
     * Called to inform the adapter of which item is currently considered to be the "primary",
     * that is the one show to the user as the current page.
     * This method will not be invoked when the adapter contains no items.
     * @param container ViewPager
     * @param position 切换到了某个position
     * @param object ViewPager切换到了 position, position 位置的 Object, 就是 {@link #instantiateItem(ViewGroup, int)} 返回的值
     */
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }

    /**
     * 销毁Item, 并不是移除Item
     * @param container ViewPager
     * @param position 要销毁的position, 在 [{@link ViewPager#getCurrentItem()} ± {@link ViewPager#getOffscreenPageLimit()}] 范围之内, 超出范围的并不会调用此方法
     * @param object {@link #getItem(ViewGroup, int)} 返回的View
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        if (loggable) LogUtils.errorFormat("position = %d, isRemoveOffscreenPosition = %b", position, isRemoveOffscreenPosition);
        container.removeView((View) object);
        if (!isRemoveOffscreenPosition) mPageViews.set(position, null);
        isRemoveOffscreenPosition = false;
    }

    /**
     * @param view {@link #getItem(ViewGroup, int)} 返回的View
     * @param object ↑
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * 处理数据变化时的位置映射, 这个方法的返回值决定了 ViewPager 如何应对数据变化
     * @param object {@link #getItem(ViewGroup, int)} 返回的View
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
//        return super.getItemPosition(object);
        int index = mPageViews.indexOf(object);
        if (loggable) LogUtils.errorFormat("object = %s, index = %d", object, index);
        // 该 View 已不在数据源中（即已被 removePage 移除）, 返回 POSITION_NONE，告诉 ViewPager 销毁它
        if (index == -1) return POSITION_NONE;
        // 返回该 View 现在的实际位置, 如果位置没变，ViewPager 会复用；如果变了，ViewPager 会尝试重新布局
        return index;
    }

    /**
     * 在指定位置添加页面, 在 {@link #getItem(ViewGroup, int)} 初始化你添加的Page
     * @param position 插入的位置（0 到 size）
     * @param title    该页面标题
     * @return 返回真正的插入位置
     */
    public int addPage(int position, @Nullable CharSequence title) {
        if (position < 0) {
            position = 0;
        } else if (position > titles.size()) position = titles.size();
        titles.add(position, title);
        mPageViews.add(position, null);
        notifyDataSetChanged();
        return position;
    }

    /**
     * 移除指定位置的页面
     * @param position 要移除的位置
     * @return 返回已经移除的Page
     */
    @Nullable
    public View removePage(@NonNull ViewPager viewPager, int position) {
        int offscreenPageLimit = viewPager.getOffscreenPageLimit();
        int currentItem = viewPager.getCurrentItem();
        if (loggable) LogUtils.errorFormat("offscreenPageLimit = %d, currentItem = %d, position = %d, mPageViews.size() = %d", offscreenPageLimit, currentItem, position, mPageViews.size());
        if (position < 0 || position >= titles.size()) return null;
        titles.remove(position);
        View view = mPageViews.remove(position);
        isRemoveOffscreenPosition = Math.abs(position - currentItem) <= offscreenPageLimit;
        notifyDataSetChanged();
        return view;
    }

    /**
     * 交换两个页面的位置
     * @param fromPosition 源位置
     * @param toPosition   目标位置
     */
    public void exchangePage(int fromPosition, int toPosition) {
        int size = titles.size();
        if (fromPosition < 0 || fromPosition >= size || toPosition < 0 || toPosition >= size) {
            if (loggable) LogUtils.errorFormat("索引越界, 不能交换位置: fromPosition = %d, toPosition = %d, titles.size() = %d", fromPosition, toPosition, size);
            return;
        }
        if (fromPosition == toPosition) return;
        // 交换列表中的两个元素
        Collections.swap(titles, fromPosition, toPosition);
        Collections.swap(mPageViews, fromPosition, toPosition);
        // 通知 ViewPager 数据变了，它会根据 getItemPosition 重新映射
        notifyDataSetChanged();
    }

    public BasePagerAdapter setLoggable(boolean loggable) {
        this.loggable = loggable;
        return this;
    }
}
