package com.actor.myandroidframework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Description: 下拉刷新, 解决嵌套中的ViewPager横向滑动时, 非常容易把下拉刷新的小球拉下来的问题, 参考: <a href="https://blog.csdn.net/ding19972431/article/details/82114531" target="_blank">这儿</a>
 * <br />
 * Author     : ldf <br />
 * Date       : 2019/3/8 on 11:22 <br />
 * @version 1.0
 */
public class SwipeRefreshLayoutCompatViewPager extends SwipeRefreshLayout {

    private       float   startX;
    private       float   startY;
    private       boolean isHorizontalMove;
    private final int     mTouchSlop;

    public SwipeRefreshLayoutCompatViewPager(@NonNull Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SwipeRefreshLayoutCompatViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
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
                startX = ev.getX();
                startY = ev.getY();
                isHorizontalMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果父类正在拖拽中，那么不拦截它的事件，直接return false；
                if (isHorizontalMove) return false;
                float distanceX = Math.abs(ev.getX() - startX);
                float distanceY = Math.abs(ev.getY() - startY);
                // 如果X轴位移大于Y轴位移，那么将事件交给父类处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    isHorizontalMove = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isHorizontalMove = false;
                break;
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev);
    }
}
