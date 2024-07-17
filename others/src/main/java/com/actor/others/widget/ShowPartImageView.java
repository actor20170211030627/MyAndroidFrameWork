package com.actor.others.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
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
 * description: 仅显示图片指定部分的内容 or 填充屏幕宽/高显示
 * <ul>
 * <li>示例1: 指定高度, 显示内容和图片底部对齐, 将上面多余的图片裁剪掉
 * <pre>
 * &lt;com.actor.others.widget.ShowPartImageView
 *     android:layout_width="match_parent"
 *     android:layout_height="83dp"
 *     android:src="@drawable/xxx"
 *     app:spivShowGravity="bottom" /&gt;
 * </pre>
 * </li>
 * <li>示例2: 不指定高度. (原生ImageView如果是窄高图, 宽度死活不能填充满屏幕, 无语...)
 * <pre>
 * &lt;com.actor.others.widget.ShowPartImageView
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:adjustViewBounds="true"          //要设置这个!
 *     android:src="@drawable/xxx"
 * //↓ if缩放后图片drawable高度 &lt; ImageView高度, 就把drawable的底部和ImageView的底部对齐显示
 *     app:spivIsAlignGravityWhenIv2WidthOrIv2Height="true"
 *     app:spivShowGravity="bottom" /&gt;
 * </pre>
 * </li>
 * <li>具体示例见: <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/ShowPartImageViewActivity.java" target="_blank">ShowPartImageViewActivity.java</a></li>
 * </ul>
 *
 * @author : ldf
 * @date   : 2023/9/5 on 16
 */
public class ShowPartImageView extends AppCompatImageView {

    //显示哪部分, 0: top,  1: bottom,  2: left,  3: right
    protected int     showGravity                           = 0;
    /**
     * 当图片太宽的时候, Gravity=right 时, 图片drawable是否和ImageView的'右侧'对齐
     * 当图片太高的时候, Gravity=bottom 时, 图片drawable是否和ImageView的'底部'对齐
     */
    protected boolean isAlignGravityWhenIv2WidthOrIv2Height = false;

    protected int ivWidth, ivHeight;
    protected Matrix matrix = new Matrix();

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
        // 设置ImageView的ScaleType为matrix，自定义裁剪区域
        setScaleType(AppCompatImageView.ScaleType.MATRIX);
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
        calcMatrix();
    }

    /**
     * 计算Matrix
     */
    protected void calcMatrix() {
        Drawable drawable = getDrawable();
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
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            LogUtils.errorFormat("drawable'size not correct: intrinsicWidth=%d, intrinsicHeight=%d", intrinsicWidth, intrinsicHeight);
            return;
        }

        //图片宽度与实际宽度的比例
        float widthRatio = intrinsicWidth * 1.0f / ivWidth;
        float heightRatio = intrinsicHeight * 1.0f / ivHeight;
//        LogUtils.errorFormat("ivWidth=%d, ivHeight=%d", ivWidth, ivHeight);
//        LogUtils.errorFormat("intrinsicWidth=%d, intrinsicHeight=%d", intrinsicWidth, intrinsicHeight);
//        LogUtils.errorFormat("widthRatio=%f, heightRatio=%f", widthRatio, heightRatio);

        matrix.reset();
        if (showGravity == 0) {         //top
            matrix.setRectToRect(new RectF(0f, 0f, intrinsicWidth, ivHeight * widthRatio),
                    new RectF(0, 0, ivWidth, ivHeight), Matrix.ScaleToFit.FILL);
        } else if (showGravity == 1) {  //bottom
            if (isAlignGravityWhenIv2WidthOrIv2Height) {//当图片很高时, 图片的显示内容和图片底部对齐
                matrix.setRectToRect(new RectF(0f, intrinsicHeight - ivHeight * widthRatio, intrinsicWidth, intrinsicHeight),
                        new RectF(0, 0, ivWidth, ivHeight), Matrix.ScaleToFit.FILL);
            } else {
                matrix.setRectToRect(new RectF(0f, Math.max(0f, intrinsicHeight - ivHeight * widthRatio), intrinsicWidth, Math.max(intrinsicHeight, ivHeight * widthRatio)),
                        new RectF(0, 0, ivWidth, ivHeight), Matrix.ScaleToFit.FILL);
            }
        } else if (showGravity == 2) {  //left
            matrix.setRectToRect(new RectF(0f, 0f, ivWidth * heightRatio, intrinsicHeight),
                    new RectF(0, 0, ivWidth, ivHeight), Matrix.ScaleToFit.FILL);
        } else {                        //right
            if (isAlignGravityWhenIv2WidthOrIv2Height) {//当图片很宽时, 图片的显示内容和图片右侧对齐
                matrix.setRectToRect(new RectF(intrinsicWidth - ivWidth * heightRatio, 0f, intrinsicWidth, intrinsicHeight),
                        new RectF(0, 0, ivWidth, ivHeight), Matrix.ScaleToFit.FILL);
            } else {
                matrix.setRectToRect(new RectF(Math.max(0f, intrinsicWidth - ivWidth * heightRatio), 0f, Math.max(intrinsicWidth, ivWidth * heightRatio), intrinsicHeight),
                        new RectF(0, 0, ivWidth, ivHeight), Matrix.ScaleToFit.FILL);
            }
        }

        setImageMatrix(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogUtils.error("onDraw");
    }

    /**
     * 显示图片的哪部分
     * @param gravity 0: top,  1: bottom,  2: left,  3: right
     */
    public void setShowGravity(@IntRange(from = 0, to = 3) int gravity) {
        if (this.showGravity != gravity) {
            this.showGravity = gravity;
            calcMatrix();
        }
    }

    /**
     * 当图片太宽的时候, Gravity=right 时, 图片drawable是否和ImageView的'右侧'对齐
     * 当图片太高的时候, Gravity=bottom 时, 图片drawable是否和ImageView的'底部'对齐
     */
    public void setIsAlignGravityWhenIv2WidthOrIv2Height(boolean isAlignGravityWhenIv2WidthOrIv2Height) {
        if (this.isAlignGravityWhenIv2WidthOrIv2Height != isAlignGravityWhenIv2WidthOrIv2Height) {
            this.isAlignGravityWhenIv2WidthOrIv2Height = isAlignGravityWhenIv2WidthOrIv2Height;
            calcMatrix();
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
