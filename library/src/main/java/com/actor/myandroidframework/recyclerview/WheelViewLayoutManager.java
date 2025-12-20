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

    //手动设置RecyclerView宽高
    protected boolean isSetRecyclerViewWidthHeightByUser = false;
    protected int recyclerViewWidth, recyclerViewHeight;

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
            LogUtils.errorFormat("recycler: recycler = %s \t recycler.getScrapList().size() = %d", recycler, recycler.getScrapList().size());
            LogUtils.errorFormat("state = %s", state);
            LogUtils.errorFormat("widthSpec = %d, heightSpec = %d", widthSpec, heightSpec);

            int widthMode = View.MeasureSpec.getMode(widthSpec);
            int heightMode = View.MeasureSpec.getMode(heightSpec);
            int widthSize = View.MeasureSpec.getSize(widthSpec);
            int heightSize = View.MeasureSpec.getSize(heightSpec);
            int unspecified = View.MeasureSpec.UNSPECIFIED; // 0
            int exactly = View.MeasureSpec.EXACTLY; // 1073741824
            int atMost = View.MeasureSpec.AT_MOST; // -2147483648
            LogUtils.errorFormat("widthMode = %d, widthSize = %d \t heightMode = %d, heightSize = %d",
                    widthMode, widthSize, heightMode, heightSize);

            int widthMode1 = getWidthMode();
            int width = getWidth();
            int heightMode1 = getHeightMode();
            int height = getHeight();
            LogUtils.errorFormat("LayoutManager: widthMode = %d, width = %d \t heightMode = %d, height = %d",
                    widthMode1, width, heightMode1, height);

            int width1 = mRecyclerView.getWidth();
            int measuredWidth = mRecyclerView.getMeasuredWidth();
            int height1 = mRecyclerView.getHeight();
            int measuredHeight = mRecyclerView.getMeasuredHeight();
            LogUtils.errorFormat("RecyclerView: width1 = %d, measuredWidth = %d \t height1 = %d, measuredHeight = %d",
                    width1, measuredWidth, height1, measuredHeight);

            LogUtils.errorFormat("getChildCount() = %d, getItemCount() = %d, state.getItemCount() = %d",
                    getChildCount(), getItemCount(), state.getItemCount());
        }

        //if有数据
        if (getItemCount() > 0 && mShowItemCount > 0) {
            View view = recycler.getViewForPosition(0);
            measureChildWithMargins(view, widthSpec, heightSpec);
//            measureChild();

            int mItemViewWidth = view.getMeasuredWidth();
            int mItemViewHeight = view.getMeasuredHeight();
//            mItemViewWidth = view.getMeasuredWidth() + getLeftDecorationWidth(view) + getRightDecorationWidth(view);
//            mItemViewHeight = view.getMeasuredHeight() + getTopDecorationHeight(view) + getBottomDecorationHeight(view);

            if (loggable) {
                int width1 = view.getWidth();
                int measuredWidthAndState = view.getMeasuredWidthAndState();
                int height1 = view.getHeight();
                int measuredHeightAndState = view.getMeasuredHeightAndState();
                LogUtils.errorFormat("recycler.getViewForPosition(0): width = %d, mItemViewWidth = %d, measuredWidthAndState = %d, height = %d, mItemViewHeight = %d, measuredHeightAndState = %d",
                        width1, mItemViewWidth, measuredWidthAndState, height1, mItemViewHeight, measuredHeightAndState);
                ViewParent viewParent = view.getParent();//null
                View focusedChild = getFocusedChild();//   null
                LogUtils.errorFormat("viewParent = %s, focusedChild = %s", viewParent, focusedChild);
            }


            int orientation = getOrientation();
            if (orientation == HORIZONTAL) {
                mRecyclerView.setClipToPadding(false);

                //if手动设置RecyclerView的宽高
                if (isSetRecyclerViewWidthHeightByUser) {
                    int paddingHorizontal;
                    if (mItemViewWidth > 0) {
                        paddingHorizontal = (recyclerViewWidth - mItemViewWidth) / 2;
                    } else {
                        paddingHorizontal = (int) (recyclerViewWidth * 1f / mShowItemCount * (mShowItemCount / 2));
                    }
                    if (loggable) {
                        LogUtils.errorFormat("recyclerViewWidth = %d, paddingHorizontal = %d", recyclerViewWidth, paddingHorizontal);
                    }
                    mRecyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
                    setWidthHeight(recyclerViewWidth, recyclerViewHeight);
                } else {
                    int paddingHorizontal = (mShowItemCount - 1) / 2 * mItemViewWidth;
                    //RecyclerView不滑动时有padding. 滑动时, item可以滚动到RecyclerView的padding内
                    //if只显示3个item: 让第0个原生不显示, 第1个item居中, 第2个item绘制在padding内
                    mRecyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);

                    /**
                     * 设置RecyclerView的尺寸
                     * android:layout_height="wrap_content": 高度设置wrap_content
                     *   ⚫在Activity中的RecyclerView中, RecyclerView会显示空白, 因为这时的mItemViewHeight=0的原因?
                     *   ⚫但是在Dialog中却没事....
                     */
                    setMeasuredDimension(mItemViewWidth * mShowItemCount, mItemViewHeight);
                    //android:layout_height="wrap_content": 这时候在Activity中也不行
//                  setMeasuredDimension(mItemViewWidth * mShowItemCount, heightSpec);

//                  // View.MeasureSpec.UNSPECIFIED => View.MeasureSpec.EXACTLY
//                  int widthSpec2 = View.MeasureSpec.makeMeasureSpec(mItemViewWidth * mShowItemCount, View.MeasureSpec.EXACTLY);
//                  int heightSpec2 = View.MeasureSpec.makeMeasureSpec(mItemViewHeight, View.MeasureSpec.EXACTLY);
//                  if (loggable) {
//                      int widthMode = View.MeasureSpec.getMode(mItemViewWidth * mShowItemCount);
//                      LogUtils.errorFormat("setMeasuredDimension: widthMode = %d, width = %d", widthMode, mItemViewWidth * mShowItemCount);
//                      int widthMode2 = View.MeasureSpec.getMode(widthSpec2);
//                      int widthSize2 = View.MeasureSpec.getSize(widthSpec2);
//                      LogUtils.errorFormat("setMeasuredDimension: widthMode2 = %d, widthSize2 = %d", widthMode2, widthSize2);
//                  }
//                  super.onMeasure(recycler, state, widthSpec2, heightSpec);

//                  mRecyclerView.setHasFixedSize(true);
                }
            } else if (orientation == VERTICAL) {
                mRecyclerView.setClipToPadding(false);

                //if手动设置RecyclerView的宽高
                if (isSetRecyclerViewWidthHeightByUser) {
                    int paddingVertical;
                    if (mItemViewHeight > 0) {
                        paddingVertical = (recyclerViewHeight - mItemViewHeight) / 2;
                    } else {
                        paddingVertical = (int) (recyclerViewHeight * 1f / mShowItemCount * (mShowItemCount / 2));
                    }
                    if (loggable) {
                        LogUtils.errorFormat("recyclerViewHeight = %d, paddingVertical = %d", recyclerViewHeight, paddingVertical);
                    }
                    mRecyclerView.setPadding(0, paddingVertical, 0, paddingVertical);
                    //在Dialog中最开始的宽度设置无效
                    setWidthHeight(recyclerViewWidth, recyclerViewHeight);
//                    int widthSpec2 = View.MeasureSpec.makeMeasureSpec(recyclerViewWidth, View.MeasureSpec.EXACTLY);
//                    int heightSpec2 = View.MeasureSpec.makeMeasureSpec(recyclerViewHeight, View.MeasureSpec.EXACTLY);
//                    super.onMeasure(recycler, state, widthSpec2, heightSpec2);
                } else {
                    int paddingVertical = (mShowItemCount - 1) / 2 * mItemViewHeight;
                    mRecyclerView.setPadding(0, paddingVertical, 0, paddingVertical);

                    /**
                     * 在Dialog中的RecyclerView滑动后, RecyclerView会显示空白, 因为这时的mItemViewWidth=0的原因?
                     */
//                  setMeasuredDimension(mItemViewWidth, mItemViewHeight * mShowItemCount);

                    //下面这行可以了
//                  setMeasuredDimension(widthSpec, mItemViewHeight * mShowItemCount);

                    // View.MeasureSpec.UNSPECIFIED => View.MeasureSpec.EXACTLY
                    int heightSpec2 = View.MeasureSpec.makeMeasureSpec(mItemViewHeight * mShowItemCount, View.MeasureSpec.EXACTLY);
                    if (loggable) {
                        int heightMode = View.MeasureSpec.getMode(mItemViewHeight * mShowItemCount);
                        LogUtils.errorFormat("setMeasuredDimension: heightMode = %d, height = %d", heightMode, mItemViewHeight * mShowItemCount);
                        int heightMode2 = View.MeasureSpec.getMode(heightSpec2);
                        int heightSize2 = View.MeasureSpec.getSize(heightSpec2);
                        LogUtils.errorFormat("setMeasuredDimension: heightMode2 = %d, heightSize2 = %d", heightMode2, heightSize2);
                    }
//                  setMeasuredDimension(widthSpec, heightSpec2);
                    super.onMeasure(recycler, state, widthSpec, heightSpec2);

//                  mRecyclerView.setHasFixedSize(true);
                }
            }
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (loggable) {
            LogUtils.errorFormat("recycler: recycler = %s \t recycler.getScrapList().size() = %d", recycler, recycler.getScrapList().size());
            LogUtils.errorFormat("state = %s", state);

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
//            float scale = 1.0f + (-1 * (1 - mScale)) * Math.min(mid, Math.abs(mid - childMid)) / mid;
            float scale = 1.0f - (1 - mScale) * Math.min(mid, Math.abs(mid - childMid)) / mid;
            if (loggable) {
                LogUtils.errorFormat("childAt(%d) mid = %f, childMid = %f, scale = %f", i, mid, childMid, scale);
            }
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
            if (loggable) {
                LogUtils.errorFormat("childAt(%d) mid = %f, childMid = %f, scale = %f", i, mid, childMid, scale);
            }
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
     * 手动设置RecyclerView的宽高
     * @param recyclerViewWidth RecyclerView的宽度
     * @param recyclerViewHeight RecyclerView的高度
     * @return
     */
    public WheelViewLayoutManager setWidthHeight(int recyclerViewWidth, int recyclerViewHeight) {
        isSetRecyclerViewWidthHeightByUser = true;
        this.recyclerViewWidth = recyclerViewWidth;
        this.recyclerViewHeight = recyclerViewHeight;
        if (mRecyclerView != null) setMeasuredDimension(recyclerViewWidth, recyclerViewHeight);
        return this;
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
