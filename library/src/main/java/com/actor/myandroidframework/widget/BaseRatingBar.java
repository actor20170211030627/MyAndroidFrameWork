package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.actor.myandroidframework.R;
import com.blankj.utilcode.util.ImageUtils;

/**
 * Description: 自定义RatingBar <br/>
 * Author     : ldf <br/>
 * Date       : 2019-8-27 on 15:34 <br/>
 * <br/>
 * 全部属性都是brb开头: <br/>
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <td align="center">属性attrs</td>
 *         <td align="center">示例exams</td>
 *         <td align="center">说明docs</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbStarInterval brbStarInterval}</td>
 *         <td nowrap="nowrap">0.0</td>
 *         <td>1.星星间距</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbStarHeight brbStarHeight}</td>
 *         <td nowrap="nowrap">0.0</td>
 *         <td>2.星星高度</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbNumStars brbNumStars}</td>
 *         <td nowrap="nowrap">5</td>
 *         <td>3.星星总的显示个数</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbEmptyDrawable brbEmptyDrawable}</td>
 *         <td nowrap="nowrap">@drawable/star_empty_for_base_rating_bar</td>
 *         <td>4.空的星星图片</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbFullDrawable brbFullDrawable}</td>
 *         <td nowrap="nowrap">@drawable/star_full_for_base_rating_bar</td>
 *         <td>5.满的星星图片</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbRating brbRating}</td>
 *         <td nowrap="nowrap">0</td>
 *         <td>6.设置默认显示多少星星</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbStepSize brbStepSize}</td>
 *         <td nowrap="nowrap">0.1</td>
 *         <td>7.步长</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbIsIndicator brbIsIndicator}</td>
 *         <td nowrap="nowrap">false</td>
 *         <td>8.是否只是起到指示作用(是否能编辑)</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbRatio brbRatio}</td>
 *         <td nowrap="nowrap">1</td>
 *         <td>9.星星宽高比例, 默认1: ratio = 宽/高</td>
 *     </tr>
 * </table>
 *
 * TODO 还需参考系统控件 {@link RatingBar}
 */
public class BaseRatingBar extends View {

    //星星间距
    protected int      starInterval = 0;
    //总的星星个数
    protected int      starCount    = 5;
    //星星高度大小，星星一般正方形，宽度等于高度
    protected int      starHeight   = 0;
    //目前绘制的星星数量
    protected float    starRating   = 0.0F;
    //步长
    protected float    starStepSize = 0.1F;
    //空的星星图片
    protected Drawable starEmptyDrawable;
    //满的星星图片
    protected Bitmap   starFullBitmap;
    //是否只是起到指示器作用
    protected boolean  starIsIndicator;
    //宽高比例, 默认1: ratio = 宽/高
    protected float    starRatio = 1;
    //是否用户在操作
    protected boolean  fromUser = false;

    //绘制'满星'的画笔
    protected Paint                     paintFullStar;
    //监听星星变化接口
    protected OnRatingBarChangeListener onRatingBarChangeListener;

    public BaseRatingBar(Context context) {
        super(context);
        init(context, null);
    }

    public BaseRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * 初始化UI组件
     * @param context
     * @param attrs
     */
    private void init(Context context, @Nullable AttributeSet attrs) {
        //必须设置, 否则不流畅
        setClickable(true);
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseRatingBar);
        //间隔
        starInterval = typedArray.getDimensionPixelSize(R.styleable.BaseRatingBar_brbStarInterval, 0);
        //星星高度
        starHeight = typedArray.getDimensionPixelSize(R.styleable.BaseRatingBar_brbStarHeight, 0);
        //总的星星个数
        starCount = typedArray.getInteger(R.styleable.BaseRatingBar_brbNumStars, 5);
        //空的星星图片
        starEmptyDrawable = typedArray.getDrawable(R.styleable.BaseRatingBar_brbEmptyDrawable);
        //满的星星图片
        Drawable drawable = typedArray.getDrawable(R.styleable.BaseRatingBar_brbFullDrawable);
        //目前绘制的星星数量
        starRating = typedArray.getFloat(R.styleable.BaseRatingBar_brbRating, 0);
        //步长
        starStepSize = typedArray.getFloat(R.styleable.BaseRatingBar_brbStepSize, 0.1F);
        //是否只是起到指示器作用
        starIsIndicator = typedArray.getBoolean(R.styleable.BaseRatingBar_brbIsIndicator, false);
        //星星宽高比例
        starRatio = typedArray.getFloat(R.styleable.BaseRatingBar_brbRatio, 1);
        typedArray.recycle();

        if (starInterval < 0) starInterval = 0;
        if (starCount <= 0) starCount = 5;

        if (starEmptyDrawable == null) starEmptyDrawable = getResources().getDrawable(R.drawable.star_empty_for_base_rating_bar);
        if (drawable != null) {
            starFullBitmap = ImageUtils.drawable2Bitmap(drawable);
        } else {
            starFullBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_full_for_base_rating_bar);
        }
        if (starRating < 0) starRating = 0;
        if (starStepSize <= 0) starStepSize = 0.1F;
        if (starRatio <= 0) starRatio = 1;

        paintFullStar = new Paint();
        paintFullStar.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (starHeight <= 0) {
            starHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        //设置总的控件的宽高
        setMeasuredDimension((int) (starHeight * starRatio * starCount) + starInterval * (starCount - 1), starHeight);

        starFullBitmap = Bitmap.createScaledBitmap(starFullBitmap, (int) (starHeight * starRatio), starHeight, true);
        paintFullStar.setShader(new BitmapShader(starFullBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0;i < starCount; i++) {
            int left = (int) ((starHeight * starRatio + starInterval) * i);
            starEmptyDrawable.setBounds(left, 0, (int) (left + starHeight * starRatio), starHeight);
            starEmptyDrawable.draw(canvas);
        }
        if (starRating > 1) {
            canvas.drawRect(0, 0, starHeight * starRatio, starHeight, paintFullStar);
            if(starRating-(int)(starRating) == 0) {
                //整数星星
                for (int i = 1; i < starRating; i++) {
                    canvas.translate(starInterval + starHeight * starRatio, 0);
                    canvas.drawRect(0, 0, starHeight * starRatio, starHeight, paintFullStar);
                }
            }else {
                for (int i = 1; i < starRating - 1; i++) {
                    canvas.translate(starInterval + starHeight * starRatio, 0);
                    canvas.drawRect(0, 0, starHeight * starRatio, starHeight, paintFullStar);
                }
                canvas.translate(starInterval + starHeight * starRatio, 0);
//                canvas.drawRect(0, 0, starHeight * (Math.round((starRating - (int) (starRating))*10)*1.0f/10) * starRatio, starHeight, paintFullStar);
                canvas.drawRect(0, 0, starHeight * (Math.round((starRating - (int) (starRating)) / starStepSize) * starStepSize) * starRatio, starHeight, paintFullStar);
            }
        }else {
            canvas.drawRect(0, 0, starHeight * starRatio * starRating, starHeight, paintFullStar);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!starIsIndicator) {
            int x = (int) event.getX();
            if (x < 0) x = 0;
            int measuredWidth = getMeasuredWidth();
            if (x > measuredWidth) x = measuredWidth;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    fromUser = true;
                    setRating(x * 1.0f / (measuredWidth * 1.0f / starCount));
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    fromUser = false;
                    break;
            }
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否只是起到指示器作用(if true, 滑动时星星没反应)
     * @see RatingBar#setIsIndicator(boolean)
     */
    public void setIsIndicator(boolean isIndicator) {
        this.starIsIndicator = isIndicator;
    }

    /**
     * 是否只是起到指示器作用(if true, 滑动时星星没反应)
     * @see RatingBar#isIndicator()
     */
    public boolean isIndicator() {
        return starIsIndicator;
    }

    /**
     * 设置总的星星个数
     * @see RatingBar#setNumStars(int)
     */
    public void setNumStars(@IntRange(from = 1) int numStars){
        if (numStars < 1) return;
        starCount = numStars;
        requestLayout();
//        invalidate();
    }

    /**
     * 获取总的星星的数目
     * @see RatingBar#getNumStars()
     */
    public int getNumStars() {
        return starCount;
    }

    /**
     * @param rating 设置目前绘制的星星数量, {@link RatingBar#setRating(float)}
     */
    public void setRating(float rating) {
        if (rating < 0) return;
//        if (starStepSize == 1) {
        if (starStepSize % 1 == 0) {
            //步长是整数星星
            starRating = (int) Math.ceil(rating);
        } else {
            //小数星星
            //比如步长0.7的时候, 此时会出现4.9一直不到5的情况
            if (rating >= starCount) {
                starRating = starCount;
            } else {
                starRating = Math.round(rating / starStepSize) * starStepSize;
            }
            //比如步长0.3时, 会出现5.1的情况
            if (starRating > starCount) starRating = starCount;
        }
        //浮点运算会出现这种情况: 0.3 * 3 = 0.90000004 (步长0.3)
        if (onRatingBarChangeListener != null) {
            onRatingBarChangeListener.onRatingChanged(this, starRating, fromUser);
        }
        invalidate();
    }

    public float getRating() {
        return starRating;
    }

    /**
     * 设置步长
     * @see RatingBar#setStepSize(float)
     */
    public void setStepSize(@FloatRange(from = 0.0F, to = 1.0F) float stepSize) {
        if (stepSize > 0 && stepSize <= 1) {
            this.starStepSize = stepSize;
        }
    }

    /**
     * 获取步长
     * @see RatingBar#getStepSize()
     */
    public float getStepSize() {
        return starStepSize;
    }

    /**
     * 设置星星改变监听
     */
    public void setOnStarChangeListener(@Nullable OnRatingBarChangeListener onRatingBarChangeListener) {
        this.onRatingBarChangeListener = onRatingBarChangeListener;
    }
    public interface OnRatingBarChangeListener {
        /**
         * @param baseRatingBar 当前控件
         * @param rating 目前的星星
         * @param fromUser 是否用户在操作
         */
        void onRatingChanged(BaseRatingBar baseRatingBar, float rating, boolean fromUser);
    }
}