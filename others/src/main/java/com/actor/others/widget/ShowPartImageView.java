package com.actor.others.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.others.R;

/**
 * description: 仅显示图片指定部分的内容
 *
 * @author : ldf
 * date       : 2023/9/5 on 16
 * update     : 2024/06/22
 */
public class ShowPartImageView extends AppCompatImageView {

    protected int ivWidth, ivHeight;
    protected Drawable drawable;
    protected boolean useMethod1 = true;

    //显示哪部分, 0: top,  1: bottom,  2: left,  3: right
    protected int     showGravity                           = 0;
    /**
     * 当图片太宽的时候, Gravity=right 时, 图片显示内容是否和图片的'右侧'对齐
     * 当图片太高的时候, Gravity=bottom 时, 图片显示内容是否和图片的'底部'对齐
     */
    protected boolean isAlignGravityWhenIv2WidthOrIv2Height = false;
    protected int balanceWidth, balanceHeight;

    public ShowPartImageView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ShowPartImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShowPartImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ShowPartImageView);
            showGravity = t.getInt(R.styleable.ShowPartImageView_spivShowGravity, showGravity);
            isAlignGravityWhenIv2WidthOrIv2Height = t.getBoolean(
                    R.styleable.ShowPartImageView_spivIsAlignGravityWhenIv2WidthOrIv2Height,
                    isAlignGravityWhenIv2WidthOrIv2Height
            );
            t.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ivWidth = MeasureSpec.getSize(widthMeasureSpec);
        ivHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    /**
     * 判断View是否发生变化，如果发生变化，那么将最新的l，t，r，b传递给View，然后刷新进行动态更新UI. 并且返回ture.没有变化返回false.
     */
    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        //                    setFrame(0, 0, 1080, 150) (宽度全屏, 高度50dp)
//        LogUtils.errorFormat("setFrame(%d, %d, %d, %d)", l, t, r, b);
        boolean changed = super.setFrame(l, t, r, b);
        return changed;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        LogUtils.errorFormat("onLayout(changed=%b, left=%d, top=%d, right=%d, bottom=%d)", changed, left, top, right, bottom);
        calcBounds();
    }

    /**
     * 计算图片Bounds
     */
    protected void calcBounds() {
        drawable = getDrawable();
        if (drawable == null) {
            LogUtils.error("drawable is null, did u had set 'android:src=\"R.xxx.xxx\" Attribute?");
            return;
        }
        if (ivWidth <= 0 || ivHeight <= 0) {
            LogUtils.errorFormat("iv'size not correct: ivWidth=%d, ivHeight=%d", ivWidth, ivHeight);
            return;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
//        LogUtils.errorFormat("intrinsicWidth=%d, intrinsicHeight=%d", intrinsicWidth, intrinsicHeight);
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            LogUtils.errorFormat("drawable'size not correct: intrinsicWidth=%d, intrinsicHeight=%d", intrinsicWidth, intrinsicHeight);
            return;
        }
        //图片宽度与实际宽度的比例
        float widthRatio = intrinsicWidth * 1.0f / ivWidth;
        float heightRatio = intrinsicWidth * 1.0f / ivHeight;
        int boundWidth = (int) (intrinsicWidth / heightRatio);
        int boundHeight = (int) (intrinsicHeight / widthRatio);
        balanceWidth = ivWidth - boundWidth;
        balanceHeight = ivHeight - boundHeight;
//        LogUtils.errorFormat("boundWidth=%d, boundHeight=%d, balanceWidth=%d, balanceHeight=%d",
//                boundWidth, boundHeight, balanceWidth, balanceHeight);

        if (useMethod1) {
            if (showGravity == 0) {        //top
                //drawable将在被绘制在canvas的哪个矩形区域内。
                drawable.setBounds(0, 0, ivWidth, boundHeight);
            } else if (showGravity == 1) { //bottom
                drawable.setBounds(0, 0, ivWidth, boundHeight);
            } else if (showGravity == 2) { //left
                drawable.setBounds(0, 0, boundWidth, ivHeight);
            } else {                    //right
                drawable.setBounds(0, 0, boundWidth, ivHeight);
            }
        } else {    //下方也可以, 不过看起来感觉有点抽象
            if (showGravity == 0) {        //top
                //drawable将在被绘制在canvas的哪个矩形区域内。
                drawable.setBounds(0, 0, ivWidth, boundHeight);
            } else if (showGravity == 1) { //bottom
                if (isAlignGravityWhenIv2WidthOrIv2Height) {//当图片很高时, 图片的显示内容和图片底部对齐
                    drawable.setBounds(0, balanceHeight, ivWidth, ivHeight);
                } else {
                    drawable.setBounds(0, Math.min(0, balanceHeight), ivWidth, Math.min(ivHeight, boundHeight));
                }
            } else if (showGravity == 2) { //left
                drawable.setBounds(0, 0, boundWidth, ivHeight);
            } else {                    //right
                if (isAlignGravityWhenIv2WidthOrIv2Height) {//当图片很宽时, 图片的显示内容和图片右侧对齐
                    drawable.setBounds(balanceWidth, 0, ivWidth, ivHeight);
                } else {
                    drawable.setBounds(Math.min(0, balanceWidth), 0, Math.min(ivWidth, boundWidth), ivHeight);
                }
            }
        }
//        setImageDrawable(drawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }
        if (useMethod1) {
            if (showGravity == 0) {        //top
                //do nothing
            } else if (showGravity == 1) { //bottom
                if (isAlignGravityWhenIv2WidthOrIv2Height) {
                    canvas.translate(0, balanceHeight); //当图片很高时, 图片的显示内容和图片底部对齐
                } else {
                    canvas.translate(0, Math.min(0, balanceHeight));
                }
            } else if (showGravity == 2) { //left
                //do nothing
            } else {                    //right
                if (isAlignGravityWhenIv2WidthOrIv2Height) {
                    canvas.translate(balanceWidth, 0);  //当图片很宽时, 图片的显示内容和图片右侧对齐
                } else {
                    canvas.translate(Math.min(0, balanceWidth), 0);
                }
            }
        }
        drawable.draw(canvas);
    }

    /**
     * 显示图片的哪部分
     * @param gravity 0: top,  1: bottom,  2: left,  3: right
     */
    public void setShowGravity(@IntRange(from = 0, to = 3) int gravity) {
        if (this.showGravity != gravity) {
            this.showGravity = gravity;
            invalidate();
        }
    }

    /**
     * 当图片太宽的时候, Gravity=right 时, 图片显示内容是否和图片的'右侧'对齐
     * 当图片太高的时候, Gravity=bottom 时, 图片显示内容是否和图片的'底部'对齐
     */
    public void setIsAlignGravityWhenIv2WidthOrIv2Height(boolean isAlignGravityWhenIv2WidthOrIv2Height) {
        if (this.isAlignGravityWhenIv2WidthOrIv2Height != isAlignGravityWhenIv2WidthOrIv2Height) {
            this.isAlignGravityWhenIv2WidthOrIv2Height = isAlignGravityWhenIv2WidthOrIv2Height;
            calcBounds();
            invalidate();
        }
    }

    //会调用 setImageDrawable
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    //会调用 setImageDrawable
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
    }

    //会调用 setImageDrawable
    @Override
    public void setImageIcon(@Nullable Icon icon) {
        super.setImageIcon(icon);
    }

    /**
     * 可设置各种显示特效
     * @param matrix The transformation parameters in matrix form.
     */
    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
    }

    @Override
    public void setImageState(int[] state, boolean merge) {
        super.setImageState(state, merge);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        LogUtils.error("onDetachedFromWindow()");
    }
}
