package com.actor.others.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.others.R;

/**
 * description: if图片比较窄&比较高的话, 设置src的话, 就会导致宽度不能填充屏幕.
 *              if设置android:scaleType="centerCrop", 又会导致上下被裁剪.
 *              ∴使用本控件, if设置 android:layout_width="match_parent",
 *              就强制把宽度拉伸到MatchParent, 高度作相应增加
 * <br />
 * 示例使用:
 * <pre>
 * &lt;com.actor.others.widget.MatchParentImageView
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:adjustViewBounds="true"
 *     app:layout_constraintBottom_toBottomOf="parent"
 *     android:src="@drawable/bg_home_fragment_game2" />
 * </pre>
 * @author : ldf
 * date       : 2024/7/16 on 14
 */
public class MatchParentImageView extends AppCompatImageView {

    //和哪一面对齐, 0: top,  1: bottom,  2: left,  3: right
    protected int     alignGravity                          = 0;

    protected int ivWidth, ivHeight;
    protected Matrix matrix = new Matrix();

    public MatchParentImageView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public MatchParentImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MatchParentImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        // 设置ImageView的ScaleType为matrix，自定义裁剪区域
        setScaleType(ScaleType.MATRIX);
        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MatchParentImageView);
            alignGravity = t.getInt(R.styleable.MatchParentImageView_mpivAlign, alignGravity);
            t.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        MeasureSpec.getMode()
        ivWidth = MeasureSpec.getSize(widthMeasureSpec);
        ivHeight = MeasureSpec.getSize(heightMeasureSpec);
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
        if (drawable == null) return;
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
        LogUtils.errorFormat("ivWidth=%d, ivHeight=%d", ivWidth, ivHeight);
        LogUtils.errorFormat("intrinsicWidth=%d, intrinsicHeight=%d", intrinsicWidth, intrinsicHeight);
        LogUtils.errorFormat("widthRatio=%f, heightRatio=%f", widthRatio, heightRatio);

        matrix.reset();
        if (alignGravity == 0) {        //top
            matrix.setRectToRect(new RectF(0f, 0f, intrinsicWidth, intrinsicHeight),
                    new RectF(0f, 0f, ivWidth, intrinsicHeight / widthRatio), Matrix.ScaleToFit.FILL);
        } else if (alignGravity == 1) { //bottom
            matrix.setRectToRect(new RectF(0f, Math.max(0f, intrinsicHeight - ivHeight * widthRatio), intrinsicWidth, intrinsicHeight),
                    new RectF(0f, 0f, ivWidth, Math.min(ivHeight, intrinsicHeight / widthRatio)), Matrix.ScaleToFit.FILL);
        } else if (alignGravity == 2) { //left
        } else {                        //right
        }

        setImageMatrix(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogUtils.error("onDraw");
    }
}
