package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

import com.actor.myandroidframework.R;
import com.google.android.material.card.MaterialCardView;


/**
 * description: 可自定义圆角的CardView, 可参考: <a href="https://blog.csdn.net/jingzz1/article/details/104985448" target="_blank">这儿</a>
 * @author : ldf <br/>
 * Date    : 2020/6/2 on 16:31 <br/>
 *
 * <br/>
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <th>一. {@link androidx.cardview.widget.CardView} 属性</th>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_cardBackgroundColor cardBackgroundColor}</td>
 *         <td nowrap="nowrap">@color/trans</td>
 *         <td>1. 背景色, 默认灰白色</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_cardCornerRadius cardCornerRadius}</td>
 *         <td>2dp</td>
 *         <td>2. 4个角的圆角半径</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_cardElevation cardElevation}</td>
 *         <td>3dp</td>
 *         <td>3. z轴的海拔(形成阴影)</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_cardMaxElevation cardMaxElevation}</td>
 *         <td>3dp</td>
 *         <td>4. z轴最大海拔</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_cardUseCompatPadding cardUseCompatPadding}</td>
 *         <td>false</td>
 *         <td>5. 是否使用CompatPadding 设置内边距用来绘制阴影, 5.0以下的默认是true, 在5.0中默认false</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link androidx.cardview.R.styleable#CardView_cardPreventCornerOverlap cardPreventCornerOverlap}</td>
 *         <td>true</td>
 *         <td>6. 是否裁剪边界以防止边界和内容重叠. 默认false, 有一个默认内边距</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_contentPadding contentPadding}</td>
 *         <td>0dp</td>
 *         <td>7. 卡片内的内容距离卡片边界的距离, 默认0dp</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_contentPaddingLeft contentPaddingLeft}</td>
 *         <td>0dp</td>
 *         <td>8. 卡片内的内容距离卡片左边界的距离, 默认0dp</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_contentPaddingTop contentPaddingTop}</td>
 *         <td>0dp</td>
 *         <td>9. 卡片内的内容距离卡片顶部边界的距离, 默认0dp</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_contentPaddingRight contentPaddingRight}</td>
 *         <td>0dp</td>
 *         <td>10. 卡片内的内容距离卡片右边界的距离, 默认0dp</td>
 *     </tr>
 *     <tr>
 *         <td>{@link androidx.cardview.R.styleable#CardView_contentPaddingBottom contentPaddingBottom}</td>
 *         <td>0dp</td>
 *         <td>11. 卡片内的内容距离卡片底部边界的距离, 默认0dp</td>
 *     </tr>
 *
 *     <tr />
 *     <tr>
 *         <th>二. {@link MaterialCardView} 属性</th>
 *     </tr>
 *     <tr>
 *         <td>{@link com.google.android.material.R.styleable#MaterialCardView_strokeColor strokeColor}</td>
 *         <td>@color/trans</td>
 *         <td>1. 描边颜色</td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.google.android.material.R.styleable#MaterialCardView_strokeWidth strokeWidth}</td>
 *         <td>0dp</td>
 *         <td>2. 描边宽度</td>
 *     </tr>
 *
 *
 *     <tr />
 *     <tr>
 *         <th>三. {@link RoundCardView} 属性</th>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#RoundCardView_rcvTopLeftRadius rcvTopLeftRadius}</td>
 *         <td>0dp</td>
 *         <td>1. ↖角圆角大小</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#RoundCardView_rcvTopRightRadius rcvTopRightRadius}</td>
 *         <td>0dp</td>
 *         <td>2. ↗角圆角大小</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#RoundCardView_rcvBottomRightRadius rcvBottomRightRadius}</td>
 *         <td>0dp</td>
 *         <td>3. ↘角圆角大小</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#RoundCardView_rcvBottomLeftRadius rcvBottomLeftRadius}</td>
 *         <td>0dp</td>
 *         <td>4. ↙角圆角大小</td>
 *     </tr>
 * </table>
 *
 * @version 1.0
 */

public class RoundCardView extends CardView {
/**
 * if extends MaterialCardView, 有些Theme会报错: <br />
 *      java.lang.IllegalArgumentException: The style on this component requires your app theme to be Theme.MaterialComponents (or a descendant). <br />
 * 参考: <a href="https://github.com/material-components/material-components-android/issues/3164">material-components-android #3164</a>
 */
//public class RoundCardView extends MaterialCardView {

    protected float tlRadius, trRadius, brRadius, blRadius;
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
//            float radius = getRadius();//8.0
//            int dp = ConvertUtils.px2dp(radius);//2dp
//            LogUtils.errorFormat("radius = %f, dp = %d", radius, dp);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCardView);
            tlRadius = a.getDimension(R.styleable.RoundCardView_rcvTopLeftRadius, 0);
            trRadius = a.getDimension(R.styleable.RoundCardView_rcvTopRightRadius, 0);
            blRadius = a.getDimension(R.styleable.RoundCardView_rcvBottomLeftRadius, 0);
            brRadius = a.getDimension(R.styleable.RoundCardView_rcvBottomRightRadius, 0);
            a.recycle();
            //如果自定义圆角
            if (tlRadius > 0 || trRadius > 0 || brRadius > 0 || blRadius > 0) {
                initPathAndRect();
            }
        }
        //默认有1个背景: androidx.cardview.widget.RoundRectDrawable
//        Drawable background = getBackground();
//        LogUtils.errorFormat("background = %s", background);

        //设置绿色#0f0: ColorStateList{mThemeAttrs=null mChangingConfigurations=0 mStateSpecs=[[]] mColors=[-16711936] mDefaultColor=-16711936}
//        ColorStateList cardBackgroundColor = getCardBackgroundColor();
        //if不设置, 默认白色(-1): Color.WHITE
//        int defaultColor = cardBackgroundColor.getDefaultColor();
//        LogUtils.errorFormat("cardBackgroundColor = %s", cardBackgroundColor);
//        LogUtils.errorFormat("defaultColor = %s", defaultColor);
        /**
         * 需要在代码中设置背景色, 用于去除小圆角
         */
        setBackgroundColor(Color.TRANSPARENT);
//        setBackgroundColor(defaultColor);
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
     * @param tlRadius ↖
     * @param trRadius ↗
     * @param brRadius ↘
     * @param blRadius ↙
     */
    public void setRadius(float tlRadius, float trRadius, float brRadius, float blRadius) {
        this.tlRadius = tlRadius;
        this.trRadius = trRadius;
        this.brRadius = brRadius;
        this.blRadius = blRadius;
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
        super.onDraw(canvas);
        if (path != null && rect != null && rectF != null) {
            path.reset();
//            rect.setEmpty();
            getDrawingRect(rect);
            rectF.set(rect);
            float[] radius8 = {tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius};
            //在路径中添加一个圆角矩形
            path.addRoundRect(rectF, radius8, Path.Direction.CW);
            //使用路径对画布进行裁剪
            canvas.clipPath(path, Region.Op.INTERSECT);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
}
