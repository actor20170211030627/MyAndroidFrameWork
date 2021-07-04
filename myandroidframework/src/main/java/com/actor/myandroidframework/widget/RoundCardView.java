package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;

import com.actor.myandroidframework.R;
import com.google.android.material.card.MaterialCardView;

/**
 * description: 可自定义圆角的CardView, https://blog.csdn.net/jingzz1/article/details/104985448
 * @author : ldf
 * date       : 2020/6/2 on 16:31
 *
 * CardView 属性:
 * 1.背景色, 默认灰白色
 * @see R.styleable#CardView_cardBackgroundColor    //@color/trans
 * 2.4个角的圆角半径, 默认2dp
 * @see R.styleable#CardView_cardCornerRadius       //2dp
 * 3.z轴的海拔(形成阴影)
 * @see R.styleable#CardView_cardElevation          //3dp
 * 4.z轴最大海拔
 * @see R.styleable#CardView_cardMaxElevation       //3dp
 * 5.是否使用CompatPadding 设置内边距用来绘制阴影, 5.0以下的默认是true, 在5.0中默认false
 * @see R.styleable#CardView_cardUseCompatPadding   //false
 * 6.是否裁剪边界以防止边界和内容重叠. 默认false, 有一个默认内边距
 * @see R.styleable#CardView_cardPreventCornerOverlap//true
 * 7.卡片内的内容距离卡片边界的距离, 默认0dp
 * @see R.styleable#CardView_contentPadding         //0dp
 * @see R.styleable#CardView_contentPaddingLeft     //0dp
 * @see R.styleable#CardView_contentPaddingTop      //0dp
 * @see R.styleable#CardView_contentPaddingRight    //0dp
 * @see R.styleable#CardView_contentPaddingBottom   //0dp
 *
 * MaterialCardView 属性:
 * 8.描边颜色
 * @see R.styleable#MaterialCardView_strokeColor    //@color/trans
 * 9.描边宽度
 * @see R.styleable#MaterialCardView_strokeWidth    //0dp
 *
 * RoundCardView 属性:
 * 10.设置4个圆角的大小
 * @see R.styleable#RoundCardView_rcvTopLeftRadius      //0dp
 * @see R.styleable#RoundCardView_rcvTopRightRadius     //0dp
 * @see R.styleable#RoundCardView_rcvBottomRightRadius  //0dp
 * @see R.styleable#RoundCardView_rcvBottomLeftRadius   //0dp
 *
 * @version 1.0
 */
public class RoundCardView extends MaterialCardView {

    protected float tlRadiu, trRadiu, brRadiu, blRadiu;
    protected Path path;
    protected Rect rect;
    protected RectF rectF;

    public RoundCardView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundCardView(Context context, AttributeSet attrs) {
        super(context, attrs/*, R.attr.materialCardViewStyle*/);
        init(context, attrs);
    }

    public RoundCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
//            float radius = getRadius();//8.0
//            float density = Resources.getSystem().getDisplayMetrics().density;//4.0
//            int dp = ConvertUtils.px2dp(radius);//2dp
//            LogUtils.formatError("radius = %f, density = %f, dp = %d", radius, density, dp);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCardView);
            tlRadiu = a.getDimension(R.styleable.RoundCardView_rcvTopLeftRadius, 0);
            trRadiu = a.getDimension(R.styleable.RoundCardView_rcvTopRightRadius, 0);
            blRadiu = a.getDimension(R.styleable.RoundCardView_rcvBottomLeftRadius, 0);
            brRadiu = a.getDimension(R.styleable.RoundCardView_rcvBottomRightRadius, 0);
            a.recycle();
            //如果自定义圆角
            if (tlRadiu != 0 || trRadiu != 0 || brRadiu != 0 || blRadiu != 0) {
                initPathAndRect();
            }
        }
    }

    /**
     * 设置4个角的圆角半径
     */
    @Override
    public void setRadius(float radius) {
        super.setRadius(radius);
    }

    /**
     * 设置圆角半径
     * @param tlRadiu ↖
     * @param trRadiu ↗
     * @param brRadiu ↘
     * @param blRadiu ↙
     */
    public void setRadius(float tlRadiu, float trRadiu, float brRadiu, float blRadiu) {
        this.tlRadiu = tlRadiu;
        this.trRadiu = trRadiu;
        this.brRadiu = brRadiu;
        this.blRadiu = blRadiu;
        initPathAndRect();
        invalidate();
    }

    protected void initPathAndRect() {
        if (path == null) path = new Path();
        if (rect == null) rect = new Rect();
        if (rectF == null) rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path != null && rect != null && rectF != null) {
            path.reset();
//            rect.setEmpty();
            getDrawingRect(rect);
            rectF.set(rect);
            float[] readius = {tlRadiu, tlRadiu, trRadiu, trRadiu, brRadiu, brRadiu, blRadiu, blRadiu};
            path.addRoundRect(rectF, readius, Path.Direction.CW);
            canvas.clipPath(path, Region.Op.INTERSECT);
        }
        super.onDraw(canvas);
    }
}
