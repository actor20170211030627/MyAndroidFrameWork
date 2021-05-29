package com.actor.myandroidframework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Description: 下拉刷新, 解决嵌套中的ViewPager横向滑动时, 非常容易把下拉刷新的小球拉下来的问题
 * https://blog.csdn.net/ding19972431/article/details/82114531
 * Author     : ldf
 * Date       : 2019/3/8 on 11:22
 * @version 1.0
 */
public class SwipeRefreshLayoutCompatViewPager extends SwipeRefreshLayout {

    private       float   startX;
    private       float   startY;
    private       boolean mIsVpDragger;// 记录viewPager是否拖拽的标记
    private final int     mTouchSlop;

    public SwipeRefreshLayoutCompatViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * @see SwipeRefreshLayout#startDragging(float), 当下拉超过一定像素之后, 就开始下拉刷新逻辑
         * 默认8像素
         */
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                startX = ev.getX();
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDragger) return false;

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsVpDragger = false;
                break;
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev);
    }
}
