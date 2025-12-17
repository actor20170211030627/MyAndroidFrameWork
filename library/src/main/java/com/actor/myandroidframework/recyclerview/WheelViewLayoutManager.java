package com.actor.myandroidframework.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * description: WheelView 效果的LayoutManager, edit from: <a href="https://github.com/DingMouRen/LayoutManagerGroup/blob/master/LayoutManagerGroup/src/main/java/com/dingmouren/layoutmanagergroup/picker/PickerLayoutManager.java" target="_blank">PickerLayoutManager.java</a> <br />
 * @author : ldf
 * date       : 2025/5/6 on 15
 * @version 1.0
 */
public class WheelViewLayoutManager extends LinearLayoutManager {

    protected float                  mScale     = 0.5f;
    protected boolean                mIsAlpha   = true;
    protected int                    mShowItemCount = 3;
    protected LinearSnapHelper       mLinearSnapHelper = new LinearSnapHelper();
    protected OnItemSelectedListener mOnItemSelectedListener;
    protected RecyclerView mRecyclerView;
    protected boolean loggable = false;

    public WheelViewLayoutManager(Context context) {
        super(context);
    }

    /**
     * @param orientation 滚动方向: {@link LinearLayoutManager#VERTICAL}, {@link LinearLayoutManager#HORIZONTAL}
     * @param reverseLayout 是否反向布局
     */
    public WheelViewLayoutManager(Context context, @RecyclerView.Orientation int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * 写在xml中的RecyclerView的属性: <br />
     * <code>app:layoutManager="com.actor.myandroidframework.recyclerview.WheelViewLayoutManager"</code>
     */
    public WheelViewLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * @param orientation 滚动方向: {@link LinearLayoutManager#VERTICAL}, {@link LinearLayoutManager#HORIZONTAL}
     * @param itemCount 显示几个item, 例: 3
     * @param scale item缩放比例, 例: [0f ~ 1f]
     * @param isAlpha item缩放的时候是否透明渐变
     */
    public WheelViewLayoutManager(Context context, @RecyclerView.Orientation int orientation, int itemCount,
                                   @FloatRange(from = 0f, to = 1f) float scale, boolean isAlpha) {
        super(context, orientation, false);
        setShowItemCount(itemCount);
        setScale(scale);
        setIsAlpha(isAlpha);
//        if (mShowItemCount > 0) setAutoMeasureEnabled(false);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 方法
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 设置方向, 默认: {@link LinearLayoutManager#VERTICAL}
     * @param orientation {@link LinearLayoutManager#VERTICAL}, {@link LinearLayoutManager#HORIZONTAL}
     */
    @Override
    public void setOrientation(@RecyclerView.Orientation int orientation) {
        super.setOrientation(orientation);
    }

    /**
     * 设置是否反向布局
     * @param reverseLayout 是否反向布局, 默认false
     */
    @Override
    public void setReverseLayout(boolean reverseLayout) {
        super.setReverseLayout(reverseLayout);
    }

    /**
     * 设置显示几个item
     * @param showItemCount 显示几个item, ≧3的单数
     */
    public WheelViewLayoutManager setShowItemCount(int showItemCount) {
        if (showItemCount % 2 == 1) this.mShowItemCount = showItemCount;
        return this;
    }

    /**
     * item缩放比例
     * @param scale 例: [0f ~ 1f]
     */
    public WheelViewLayoutManager setScale(@FloatRange(from = 0f, to = 1f) float scale) {
        this.mScale = scale;
        return this;
    }

    /**
     * item缩放的时候是否透明渐变
     * @param isAlpha
     */
    public WheelViewLayoutManager setIsAlpha(boolean isAlpha) {
        this.mIsAlpha = isAlpha;
        return this;
    }

    public WheelViewLayoutManager setLoggable(boolean loggable) {
        this.loggable = loggable;
        return this;
    }



    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        this.mRecyclerView = view;
        mLinearSnapHelper.attachToRecyclerView(view);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        this.mRecyclerView = null;
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        if (loggable) {
            LogUtils.errorFormat("getItemCount() = %d, state.getItemCount() = %d, widthSpec = %d, heightSpec = %d, state = %s",
                    getItemCount(), state.getItemCount(), widthSpec, heightSpec, state);
        }

        int childCount = getChildCount();//        0                                                            0
        if (loggable) LogUtils.errorFormat("childCount = %d, getScrapList().size() = %d", childCount, recycler.getScrapList().size());

        //if有数据
        if (getItemCount() > 0 && mShowItemCount > 0) {
            View view = recycler.getViewForPosition(0);
            measureChildWithMargins(view, widthSpec, heightSpec);

            int mItemViewWidth = view.getMeasuredWidth();
            int mItemViewHeight = view.getMeasuredHeight();
//            mItemViewWidth = view.getMeasuredWidth() + getLeftDecorationWidth(view) + getRightDecorationWidth(view);
//            mItemViewHeight = view.getMeasuredHeight() + getTopDecorationHeight(view) + getBottomDecorationHeight(view);

            ViewParent viewParent = view.getParent();//null
            View focusedChild = getFocusedChild();//   null
            if (loggable) {
                LogUtils.errorFormat("mItemViewWidth = %d, mItemViewHeight = %d, childCount = %d, viewParent = %s, focusedChild = %s",
                        mItemViewWidth, mItemViewHeight, childCount, viewParent, focusedChild);
            }


            int orientation = getOrientation();
            if (orientation == HORIZONTAL) {
                int paddingHorizontal = (mShowItemCount - 1) / 2 * mItemViewWidth;
                //RecyclerView不滑动时有padding. 滑动时, item可以滚动到RecyclerView的padding内
                mRecyclerView.setClipToPadding(false);
                //if只显示3个item: 让第0个原生不显示, 第1个item居中, 第2个item绘制在padding内
                mRecyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);

                //设置RecyclerView的尺寸
                setMeasuredDimension(mItemViewWidth * mShowItemCount, mItemViewHeight);
                //要设置, 否则在Dialog中的RecyclerView滑动后, RecyclerView会显示空白, 原因???
                mRecyclerView.setHasFixedSize(true);
            } else if (orientation == VERTICAL) {
                int paddingVertical = (mShowItemCount - 1) / 2 * mItemViewHeight;
                mRecyclerView.setClipToPadding(false);
                mRecyclerView.setPadding(0, paddingVertical, 0, paddingVertical);

                setMeasuredDimension(mItemViewWidth, mItemViewHeight * mShowItemCount);
                //要设置, 否则在Dialog中的RecyclerView滑动后, RecyclerView会显示空白, 原因???
                mRecyclerView.setHasFixedSize(true);
            }
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (loggable) {
            LogUtils.errorFormat("getItemCount() = %d, state.isPreLayout() = %b", getItemCount(), state.isPreLayout());
        }

        if (getItemCount() <= 0 || state.isPreLayout()) return;

        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            scaleHorizontalChildView();
        } else if (orientation == VERTICAL) {
            scaleVerticalChildView();
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleHorizontalChildView();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleVerticalChildView();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 水平缩放
     */
    protected void scaleHorizontalChildView() {
        float mid = getWidth() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null) continue;
            if (loggable) {
                //getDecoratedLeft(child) = child.getLeft() - getLeftDecorationWidth(child)
                //getDecoratedRight(child) = child.getRight() + getRightDecorationWidth(child)
                LogUtils.errorFormat("childAt(%d): child.getLeft() = %d, getLeftDecorationWidth = %d", i, child.getLeft(), getLeftDecorationWidth(child));
                LogUtils.errorFormat("childAt(%d): child.getRight() = %d, getRightDecorationWidth = %d", i, child.getRight(), getRightDecorationWidth(child));
            }
            //childMid: (child的宽度 + Decoration宽度) / 2
            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            if (loggable) LogUtils.errorFormat("childAt(%d) mid = %f, childMid = %f", i, mid, childMid);
//            float scale = 1.0f + (-1 * (1 - mScale)) * Math.min(mid, Math.abs(mid - childMid)) / mid;
            float scale = 1.0f - (1 - mScale) * Math.min(mid, Math.abs(mid - childMid)) / mid;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mIsAlpha) {
                child.setAlpha(scale);
            }
        }
    }

    /**
     * 竖向缩放
     */
    protected void scaleVerticalChildView() {
        float mid = getHeight() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null) continue;
            if (loggable) {
                //getDecoratedTop(child) = child.getTop() - getTopDecorationHeight(child)
                //getDecoratedBottom(child) = child.getBottom() + getBottomDecorationHeight(child)
                LogUtils.errorFormat("childAt(%d): child.getTop() = %d, getTopDecorationHeight = %d", i, child.getTop(), getTopDecorationHeight(child));
                LogUtils.errorFormat("childAt(%d): child.getBottom() = %d, getBottomDecorationHeight = %d", i, child.getBottom(), getBottomDecorationHeight(child));
            }
            //childMid: (child的宽度 + Decoration宽度) / 2
            float childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f;
            if (loggable) LogUtils.errorFormat("childAt(%d) mid = %f, childMid = %f", i, mid, childMid);
            /**
             * 从RecyclerView的顶部开始:
             * Math.abs(mid - childMid)                            : item中心离RecyclerView中心的距离, mid~0
             * Math.min(mid, Math.abs(mid - childMid))             : 实际就是↑
             * Math.abs(mid - childMid) / mid                      : 1 ~ 0
             * (1 - mScale) * Math.abs(mid - childMid) / mid       : (1 - mScale) ~ 0
             * 1.0f - (1 - mScale) * Math.abs(mid - childMid) / mid: mScale ~ 1
             */
//            float scale = 1.0f + (-1 * (1 - mScale)) * Math.min(mid, Math.abs(mid - childMid)) / mid;
            float scale = 1.0f - (1 - mScale) * Math.min(mid, Math.abs(mid - childMid)) / mid;
            //缩放比例, scale = 1: 不缩放
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mIsAlpha) {
                child.setAlpha(scale);
            }
        }
    }

    /**
     * 当滑动停止时触发回调
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mOnItemSelectedListener != null && mLinearSnapHelper != null) {
                View view = mLinearSnapHelper.findSnapView(this);
                if (view != null) {
                    int position = getPosition(view);
                    mOnItemSelectedListener.onItemSelected(view, position);
                }
            }
        }
    }

    /**
     * 修复bug: https://github.com/DingMouRen/LayoutManagerGroup/issues/16
     * @return
     */
    @Override
    public boolean isAutoMeasureEnabled() {
        if (this.mShowItemCount > 0) {
            return false;
        }
        return super.isAutoMeasureEnabled();
    }

    public void setOnSelectedViewListener(@Nullable OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    /**
     * 停止时，显示在中间的View的监听
     */
    public interface OnItemSelectedListener {
        /**
         * @param view 停止时，显示在中间的View
         * @param position 停止时，显示在中间View的position
         */
        void onItemSelected(@NonNull View view, int position);
    }
}
