package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.R;

/**
 * description: 线条View: 渐变, 实线, 虚线, 斜线, 自定义角度线等 <br />
 * {@link null 注意:}
 * <ul>
 *     <li>
 *         if是在
 *         {@link androidx.recyclerview.widget.RecyclerView RecyclerView}
 *         中的Item列表布局中使用本控件{@link  LineView}, 而Item布局又是
 *         {@link androidx.constraintlayout.widget.ConstraintLayout ConstraintLayout}的话, <br />
 *         一定要确保Item布局中{@link  LineView &lt;com.actor.myandroidframework.widget.LineView} <br />
 *          的<b>android:layout_height{@link null ≠}"match_parent"</b>, 否则获取到的高度{@link #getHeight()}=0, <br />
 *          就会造成绘制不出来的情况...
 *     </li>
 * </ul>
 *
 * @author : ldf
 * @date   : 2024/6/4 on 11
 */
public class LineView extends View {

    public static final int HORIZONTAL = 0, VERTICAL = 1,
            TOPLEFT_2_BOTTOMRIGHT = 2, BOTTOMLEFT_2_TOPRIGHT = 3, FREE_ROTATE = 4;    //自由旋转

    protected Paint mPaint;
    protected int     startColor = 0;
    protected Integer centerColor = null;
    protected int     endColor = 0;
    protected int   lineWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    protected int   lineDashWidth = 0, lineDashGap = 0; //虚线
    protected int   lineOrientation = HORIZONTAL;
    protected float lineAngle = 0f;
    protected LinearGradient linearGradient;

    public LineView(Context context) {
        super(context);
        init(context, null);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        //关闭硬件加速(低版本手机(Honor 7A[Android 8.0])需要设置, 否则只能画实线, 虚线画不出来): android:layerType="software"
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.LineView);
            //线颜色
            int lineColor = t.getColor(R.styleable.LineView_lvLineColor, Color.BLACK);
            startColor = t.getColor(R.styleable.LineView_lvGradientStartColor, lineColor);
            endColor = t.getColor(R.styleable.LineView_lvGradientEndColor, startColor);
            if (t.hasValue(R.styleable.LineView_lvGradientCenterColor)) {
                centerColor = t.getColor(R.styleable.LineView_lvGradientCenterColor, startColor);
            }
            //线宽
            lineWidth = t.getDimensionPixelSize(R.styleable.LineView_lvLineWidth, lineWidth);
            //虚线线段的长度
            lineDashWidth = t.getDimensionPixelSize(R.styleable.LineView_lvDashWidth, lineDashWidth);
            //虚线间隔长度
            lineDashGap = t.getDimensionPixelSize(R.styleable.LineView_lvDashGap, lineDashGap);
            //方向
            lineOrientation = t.getInt(R.styleable.LineView_lvOrientation, lineOrientation);
            //旋转角度
            lineAngle = t.getFloat(R.styleable.LineView_lvAngle, lineAngle);
            t.recycle();
        }

        mPaint = new Paint();
        //抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //线帽: 不能设置圆形/矩形线帽, 否则画出来看起来会是实线!
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        //矫正&设置线宽
        validateLineWidthAndSetPaint(lineWidth);
//        mPaint.setColor(lineColor);
        //虚线
        mPaint.setPathEffect(new DashPathEffect(new float[]{lineDashWidth, lineDashGap}, 0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        switch (lineOrientation) {
            case HORIZONTAL:  //→
                setLinearGradient(0f, height / 2f, width, height / 2f);
                canvas.drawLine(0f, height / 2f, width, height / 2f, mPaint);
                break;
            case VERTICAL:  //↓
                setLinearGradient(width / 2f, 0f, width / 2f, height);
                canvas.drawLine(width / 2f, 0f, width / 2f, height, mPaint);
                break;
            case TOPLEFT_2_BOTTOMRIGHT: //↘
                setLinearGradient(0f, 0f, width, height);
                canvas.drawLine(0f, 0f, width, height, mPaint);
                break;
            case BOTTOMLEFT_2_TOPRIGHT: //↗
                setLinearGradient(width, 0f, 0f, height);
                canvas.drawLine(width, 0f, 0f, height, mPaint);
                break;
            case FREE_ROTATE:   //🕛
                canvas.rotate(lineAngle, width / 2f, height / 2f);
                setLinearGradient(0f, height / 2f, width, height / 2f);
                canvas.drawLine(0f, height / 2f, width, height / 2f, mPaint);
                break;
            default:
                break;
        }
    }

    //矫正线宽&设置Paint
    protected void validateLineWidthAndSetPaint(int lineWidth) {
        if (this.lineWidth != lineWidth) {
            //矫正线宽
            if (lineWidth < 0 && lineWidth != ViewGroup.LayoutParams.MATCH_PARENT) {
                lineWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
        if (lineWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            //线度再大的话, 在低版本手机(Honor 7A[Android 8.0])就画不出来横线&竖线, 原因是什么??
            mPaint.setStrokeWidth(1_073_741_791);//1073741791 = 0x3FFFFFDF
        } else {
            mPaint.setStrokeWidth(lineWidth);
        }
    }

    //设置渐变
    protected void setLinearGradient(float startX, float startY, float endX, float endY) {
        if (linearGradient == null) {
            if (centerColor == null) {
                linearGradient = new LinearGradient(startX, startY, endX, endY, startColor, endColor, Shader.TileMode.CLAMP);
            } else {
                int[] colors = new int[] {startColor, centerColor, endColor};
                float[] position = {0f, 0.5f, 1.0f};
                linearGradient = new LinearGradient(startX, startY, endX, endY, colors, position, Shader.TileMode.CLAMP);
            }
            mPaint.setShader(linearGradient);
        }
    }

    /**
     * 设置线宽
     * @param lineWidth 线宽, 单位px
     */
    public void setLineWidth(int lineWidth) {
        validateLineWidthAndSetPaint(lineWidth);
        invalidate();
    }

    /**
     * 设置虚线
     * @param dashPathEffect 虚线路径效果
     */
    public void setDashLine(@Nullable DashPathEffect dashPathEffect) {
        mPaint.setPathEffect(dashPathEffect);
        invalidate();
    }

    /**
     * 设置线的绘制方向
     * @param orientation 请参考顶部的方向
     */
    public void setOrientation(int orientation) {
        if (lineOrientation != orientation) {
            this.lineOrientation = orientation;
            linearGradient = null;
            invalidate();
        }
    }

    /**
     * 设置渐变颜色
     * @param startColor 渐变开始颜色
     * @param centerColor 渐变中间颜色, 没有可传null
     * @param endColor 渐变结束颜色
     */
    public void setLineGradientColors(int startColor, Integer centerColor, int endColor) {
        this.startColor = startColor;
        this.centerColor = centerColor;
        this.endColor = endColor;
        linearGradient = null;
        invalidate();
    }

    /**
     * 设置渐变颜色
     * @param linearGradient 可自定义n种颜色
     */
    public void setLinearGradient(@NonNull LinearGradient linearGradient) {
        if (linearGradient != null) {
            this.linearGradient = linearGradient;
            mPaint.setShader(linearGradient);
            invalidate();
        }
    }

    /**
     * 设置旋转角度
     * @param lineAngle 旋转角度
     */
    public void setLineAngle(float lineAngle) {
        if (lineOrientation == FREE_ROTATE && this.lineAngle == lineAngle) return;
        this.lineAngle = lineAngle;
        if (lineOrientation != FREE_ROTATE) {
            lineOrientation = FREE_ROTATE;
            linearGradient = null;
        }
        invalidate();
    }
}
