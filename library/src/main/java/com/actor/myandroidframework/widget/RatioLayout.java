package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;

/**
 * <pre>
 * Created by zhengping on 2017/4/6,15:17.
 * 按照比例，动态计算宽度/高度的帧布局
 * 自定义属性的使用：
 * 1、给自定义属性起名字
 *      a、自定义属性集合的名称
 *      b、自定义属性的名称
 *              format
 * 2、使用这个自定义属性
 *      a、命名空间
 *      b、自定义属性只能给自定义控件使用
 * 3、在自定义控件中获取自定义属性的值
 *      a、通过attrs，在所有的属性中进行查找
 *      b、通过attrs获取自定义属性集合，然后通过下标索引的方式获取自定义属性的值
 *
 * 4.使用示例:
 * ⚫&lt;com.actor.myandroidframework.widget.RatioLayout
 *     android:layout_width="100dp"
 *     android:layout_height="wrap_content"
 *     app:ratio="1">
 * ⚫or
 * &lt;com.actor.myandroidframework.widget.RatioLayout
 *     android:layout_width="wrap_content"
 *     android:layout_height="100dp"
 *     app:ratio="2">
 * ⚫or
 * &lt;com.actor.myandroidframework.widget.RatioLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:ratio="3">
 * ⚫or
 * &lt;com.actor.myandroidframework.widget.RatioLayout
 *     android:layout_width="wrap_content"
 *     android:layout_height="match_parent"
 *     app:ratio="0.4">
 * ⚫or
 * &lt;LinearLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:orientation="horizontal">
 *     &lt;com.actor.myandroidframework.widget.RatioLayout
 *         android:layout_width="0dp"
 *         android:layout_height="wrap_content"
 *         android:layout_weight="1"
 *         app:ratio="5">
 *         ...
 *     &lt;/com.actor.myandroidframework.widget.RatioLayout>
 *     &lt;View
 *         android:layout_width="30dp"
 *         android:layout_height="30dp"
 *         android:background="@drawable/black" /&gt;
 * &lt;/LinearLayout>
 * </pre>
 * @version 1.0
 */

public class RatioLayout extends FrameLayout {

    //宽高比例, ratio = 宽/高
    protected float ratio;

    // 监听子View尺寸变化
    private final OnLayoutChangeListener mChildLayoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            // 如果子View尺寸发生变化，重新测量
            if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
                requestLayout();
            }
        }
    };

    //new对象的时候调用
    public RatioLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    //加载布局的时候
    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    //布局文件中有style的时候
    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
            ratio = typedArray.getFloat(R.styleable.RatioLayout_ratio, 0.0F);
            typedArray.recycle();
        }
    }

    public float getRatio() {
        return ratio;
    }

    /**
     * 设置宽高比
     * @param ratio width/height
     */
    public void setRatio(float ratio) {
        if (this.ratio != ratio && ratio > 0.0F) {
            this.ratio = ratio;
            requestLayout();
        }
    }

    /**
     * <pre>
     * AT_MOST, 最多, 例         : android:layout_width="wrap_content"
     * EXACTLY, 精确地, 确切地, 例: android:layout_width="300dp", or
     *                            android:layout_width="match_parent"
     * UNSPECIFIED, 未指明的, 例  :
     *                            &lt;LinearLayout
     *                                &lt;RatioLayout
     *                                    android:layout_width="0dp" 👈=====
     *                                    android:layout_weight="1"
     * </pre>
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio <= 0.0F) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        //整个帧布局的宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //宽布局的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //宽度确定，高度不确定
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            //排除左/右边距的影响
            int innerWidthSize = widthSize - getPaddingLeft() - getPaddingRight();
            //重新计算heightSize
            heightSize = (int) (innerWidthSize / ratio + 0.5F);//此时的heightSize我们想把它当作啥？当作图片的高度
            heightSize  = heightSize + getPaddingTop() + getPaddingBottom();

//           setMeasuredDimension(widthSize, heightSize);//仅仅只是确定了RatioLayout的大小，但是RatioLayout的孩子没有走measure方法
            //重新生成measureSpec
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        //高度确定，宽度不确定
        if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            int innerHeightSize = heightSize - getPaddingTop() - getPaddingBottom();
            widthSize = (int) (innerHeightSize * ratio + 0.5F);
            widthSize  = widthSize + getPaddingLeft() + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        //宽高都是wrap_content
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            // 1. 先测量所有子View
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            // 2. 计算最大子View尺寸（包含padding）
            int maxChildWidth = getMaxChildWidth() + getPaddingLeft() + getPaddingRight();
            int maxChildHeight = getMaxChildHeight() + getPaddingTop() + getPaddingBottom();
            if (maxChildWidth > 0 && maxChildHeight > 0) {
                float ratioChildren = maxChildWidth * 1.0F / maxChildHeight;
                if (ratioChildren > ratio) {
                    maxChildHeight = (int) (maxChildWidth / ratio + 0.5F);
                } else if (ratioChildren < ratio) {
                    maxChildWidth = (int) (maxChildHeight * ratio + 0.5F);
                }
                // 3. 设置最终测量尺寸
                setMeasuredDimension(
                        resolveSize(maxChildWidth, widthMeasureSpec),
                        resolveSize(maxChildHeight, heightMeasureSpec)
                );
            }
            return;
        }

        //其它情况
        if (!isInEditMode()) {
            LogUtils.errorFormat("widthMode = %d, widthSize = %d \t heightMode = %d, heightSize = %d", widthMode, widthSize, heightMode, heightSize);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 获取所有子View中的最大宽度（包含margin）
     */
    protected int getMaxChildWidth() {
        int maxWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                maxWidth = Math.max(maxWidth, childWidth);
            }
        }
        return maxWidth;
    }

    /**
     * 获取所有子View中的最大高度（包含margin）
     */
    protected int getMaxChildHeight() {
        int maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                maxHeight = Math.max(maxHeight, childHeight);
            }
        }
        return maxHeight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 监听子View尺寸变化 (RecyclerView中复用的时候, 也会走这方法)
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.addOnLayoutChangeListener(mChildLayoutChangeListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 移除监听
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).removeOnLayoutChangeListener(mChildLayoutChangeListener);
        }
    }
}
