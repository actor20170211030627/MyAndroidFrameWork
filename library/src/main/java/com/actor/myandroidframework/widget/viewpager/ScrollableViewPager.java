package com.actor.myandroidframework.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Description: 设置能否左右滑动的ViewPager
 * Author     : ldf
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
     * 当能左右滑动时, 才有页面切换动画, 否则没有切换动画(如果=false, TabLayout点击时, 没有切换动画)
     */
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, scrollable);
    }

    /**
     * @return 是否能水平滑动
     */
    public boolean isHorizontalScrollble() {
        return scrollable;
    }

    /**
     * 设置是否能水平滑动
     * @param scrollble
     */
    public void setHorizontalScrollble(boolean scrollble) {
        this.scrollable = scrollble;
    }
}
