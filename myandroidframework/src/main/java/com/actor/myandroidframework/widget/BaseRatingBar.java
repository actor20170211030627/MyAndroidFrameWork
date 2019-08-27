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
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;

import com.actor.myandroidframework.R;
import com.blankj.utilcode.util.ImageUtils;

/**
 * Description: 自定义RatingBar
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-8-27 on 15:34
 *
 * @version 1.0
 * TODO 还需参考系统控件 {@link RatingBar}
 */
public class BaseRatingBar extends View {

    private int                  starInterval = 0; //星星间距
    private int                  starCount    = 5;  //总的星星个数
    private int                  starHeight   = 0;     //星星高度大小，星星一般正方形，宽度等于高度
    private float                starRating     = 0.0F;   //目前绘制的星星数量
    private float                starStepSize = 0.1F;//步长
    private Drawable             starEmptyDrawable; //空的星星图片
    private Bitmap               starFullBitmap; //满的星星图片
    private boolean starIsIndicator;//是否只是起到指示器作用

    private Paint                               paintFullStar;         //绘制'满星'的画笔
    private OnRatingBarChangeListener onRatingBarChangeListener;//监听星星变化接口
    private float starRatio = 1;//宽高比例, 默认1: ratio = 宽/高
    private boolean fromUser = false;//是否用户在操作

    public BaseRatingBar(Context context) {
        super(context);
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
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs){
        setClickable(true);//必须设置, 否则不流畅
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseRatingBar);
        starInterval = typedArray.getDimensionPixelSize(R.styleable.BaseRatingBar_brbStarInterval, 0);//间隔
        starHeight = typedArray.getDimensionPixelSize(R.styleable.BaseRatingBar_brbStarHeight, 0);//星星高度
        starCount = typedArray.getInteger(R.styleable.BaseRatingBar_brbNumStars, 5);//总的星星个数
        starEmptyDrawable = typedArray.getDrawable(R.styleable.BaseRatingBar_brbEmptyDrawable);//空的星星图片
        Drawable drawable = typedArray.getDrawable(R.styleable.BaseRatingBar_brbFullDrawable);//满的星星图片
        starRating = typedArray.getFloat(R.styleable.BaseRatingBar_brbRating, 0);//目前绘制的星星数量
        starStepSize = typedArray.getFloat(R.styleable.BaseRatingBar_brbStepSize, 0.1F);//步长
        starIsIndicator = typedArray.getBoolean(R.styleable.BaseRatingBar_brbIsIndicator, false);//是否只是起到指示器作用
        starRatio = typedArray.getFloat(R.styleable.BaseRatingBar_brbRatio, 1);
        typedArray.recycle();

        if (starInterval < 0) starInterval = 0;
        if (starCount <= 0) starCount = 5;

        if (starEmptyDrawable == null) starEmptyDrawable = getResources().getDrawable(R.drawable.star_empty_for_base_rating_bar);
        if (drawable != null) starFullBitmap = ImageUtils.drawable2Bitmap(drawable);
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

        if (starFullBitmap == null) {
            starFullBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_full_for_base_rating_bar);
        }
        starFullBitmap = Bitmap.createScaledBitmap(starFullBitmap, (int) (starHeight * starRatio), starHeight, true);
        paintFullStar.setShader(new BitmapShader(starFullBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (starFullBitmap == null || starEmptyDrawable == null) return;
        for (int i = 0;i < starCount; i++) {
            int left = (int) ((starHeight * starRatio + starInterval) * i);
            starEmptyDrawable.setBounds(left, 0, (int) (left + starHeight * starRatio), starHeight);
            starEmptyDrawable.draw(canvas);
        }
        if (starRating > 1) {
            canvas.drawRect(0, 0, starHeight * starRatio, starHeight, paintFullStar);
            if(starRating-(int)(starRating) == 0) {//整数星星
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
//        if (isIndicator) {
//            setFocusable(FOCUSABLE_AUTO);
//        } else {
//            setFocusable(FOCUSABLE);
//        }
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
    public void setNumStars(@IntRange(from = 0) int numStars){
        if (numStars <= 0) return;
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
     * 设置目前绘制的星星数量
     * @see RatingBar#setRating(float)
     */
    public void setRating(float rating){
        if (starStepSize == 1) {//整数星星
            starRating = (int) Math.ceil(rating);
        }else {
            if (rating == starCount) {//比如步长0.7的时候, 大于0.5, 此时会出现4.9一直不到5的情况
                starRating = rating;
            } else {
//                starRating = Math.round(rating * 10) * 1.0f / 10;
                starRating = Math.round(rating / starStepSize) * starStepSize;
            }
            if (starRating > starCount) starRating = starCount;//比如步长0.3时, 会出现5.1的情况
            //FIXME 浮点运算会出现这种情况: 0.3 * 3 = 0.90000004 (步长0.3)
        }
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
    public void setOnStarChangeListener(OnRatingBarChangeListener onRatingBarChangeListener){
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