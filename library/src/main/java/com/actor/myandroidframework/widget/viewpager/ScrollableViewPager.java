package com.actor.myandroidframework.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Description: 设置能否左右滑动的ViewPager <br />
 * Author     : ldf <br />
 * Date       : 2019/3/7 on 15:39
 * @version 1.1
 */
public class ScrollableViewPager extends ViewPager {

    protected boolean scrollable = true;

    public ScrollableViewPager(Context context) {
        super(context);
    }

    public ScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 返回空闲状态下, ViewPager <b>当前页面两侧</b>保留的页数。默认为1。
     * @return 默认 = {@link #DEFAULT_OFFSCREEN_PAGES} = 1
     */
    @Override
    public int getOffscreenPageLimit() {
        return super.getOffscreenPageLimit();
    }

    /**
     * 设置空闲状态下 ViewPager <b>当前页面两侧</b>应保留的页数。超过此限制的页面将在需要时从Adapter重新创建。
     * @param limit 必须 ≧ 1, 取值范围: [1, items.Size() - 1]
     */
    @Override
    public void setOffscreenPageLimit(int limit) {
        super.setOffscreenPageLimit(limit);
    }

    /**
     * 在onTouchEvent中不做任何事情，ViewPager就不能左右滑动(详情查看源码)
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollable) return false;
        return super.onTouchEvent(ev);
    }

    /**
     * 如果不重写此方法，子控件如果有触摸事件(例:有一个ViewPager)，
     * 在子类左滑到不能滑动后，return super.onInterceptTouchEvent(ev)
     * 中会有逻辑，父控件会响应侧滑事件(父控件能一点一点往左滑)，
     * 所以此处需要注掉return super.onInterceptTouchEvent(ev)，直接返回false
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollable) return false;
        return super.onInterceptTouchEvent(ev);
//        return true;//拦截, 子类不能获取响应触摸事件
    }

    /**
     * 获取当前显示Page的position, ≧0, if没有填充 = 0
     * @return ≧0
     */
    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }

    /**
     * 当能左右滑动时, 才有页面切换动画, 否则没有切换动画(如果=false, TabLayout点击时, 没有切换动画)
     */
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, scrollable);
    }

    /**
     * @return 是否能水平滑动
     */
    public boolean isHorizontalScrollable() {
        return scrollable;
    }

    /**
     * 设置是否能水平滑动
     * @param scrollable 是否能左右滑动
     */
    public void setHorizontalScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
}
