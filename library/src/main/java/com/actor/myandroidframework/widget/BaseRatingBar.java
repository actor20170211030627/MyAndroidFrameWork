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
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.RatingBar;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

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
 *         <td align="center">№</td>
 *         <td align="center">属性attrs</td>
 *         <td align="center">示例exams</td>
 *         <td align="center">说明docs</td>
 *     </tr>
 *     <tr>
 *         <td>1</td>
 *         <td>{@link R.styleable#BaseRatingBar_brbStarInterval brbStarInterval}</td>
 *         <td>0.0</td>
 *         <td>星星间距</td>
 *     </tr>
 *     <tr>
 *         <td>2</td>
 *         <td>{@link R.styleable#BaseRatingBar_brbNumStars brbNumStars}</td>
 *         <td>5</td>
 *         <td>星星总的显示个数</td>
 *     </tr>
 *     <tr>
 *         <td>3</td>
 *         <td nowrap="nowrap">{@link R.styleable#BaseRatingBar_brbEmptyDrawable brbEmptyDrawable}</td>
 *         <td nowrap="nowrap">@drawable/star_empty_for_base_rating_bar</td>
 *         <td>空的星星图片</td>
 *     </tr>
 *     <tr>
 *         <td>4</td>
 *         <td>{@link R.styleable#BaseRatingBar_brbFullDrawable brbFullDrawable}</td>
 *         <td>@drawable/star_full_for_base_rating_bar</td>
 *         <td>满的星星图片</td>
 *     </tr>
 *     <tr>
 *         <td>5</td>
 *         <td>{@link R.styleable#BaseRatingBar_brbRating brbRating}</td>
 *         <td>0</td>
 *         <td>设置默认显示多少星星</td>
 *     </tr>
 *     <tr>
 *         <td>6</td>
 *         <td>{@link R.styleable#BaseRatingBar_brbStepSize brbStepSize}</td>
 *         <td>0.1</td>
 *         <td>步长</td>
 *     </tr>
 *     <tr>
 *         <td>7</td>
 *         <td>{@link R.styleable#BaseRatingBar_brbIsIndicator brbIsIndicator}</td>
 *         <td>false</td>
 *         <td>是否只是起到指示作用(默认false)</td>
 *     </tr>
 *     <tr>
 *         <td>8</td>
 *         <td>{@link R.styleable#BaseRatingBar_brbRatio brbRatio}</td>
 *         <td>1</td>
 *         <td>单个星星的宽高比 ratio = 宽/高, 默认=1</td>
 *     </tr>
 * </table>
 */
public class BaseRatingBar extends View {

    //星星间距
    protected int      starInterval = 0;
    //总的星星个数
    protected int      starCount    = 5;
    //单个星星宽高
    protected float    starWidthF = 0;
    protected int      starHeight = 0;
    //目前绘制的星星数量
    protected float    starRating   = 0.0F;
    //步长
    protected float    starStepSize = 0.1F;
    //空的星星图片
    protected Drawable starEmptyDrawable;
    //满的星星图片
    protected Bitmap   starFullBitmap;
    //是否只是起到指示器作用
    protected boolean  starIsIndicator = false;
    //单个星星的宽高比例, 默认1: ratio = 宽/高
    protected float    starRatio = 1;
    //全部星星⭐✨️🌟的总宽度(包括间距, 不包括padding)
    protected int allStarsWidth = 0;
    // 系统判定滑动的最小距离（避免误判）, ≈8dp
    protected int mTouchSlop;
    //按下位置的坐标
    protected float pressedX = 0, pressedY = 0;
    //是否是垂直滚动
    protected Boolean isVerticalScroll = null;
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
     */
    private void init(Context context, @Nullable AttributeSet attrs) {
        //必须设置, 否则不流畅
        setClickable(true);
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseRatingBar);
        //间隔
        starInterval = typedArray.getDimensionPixelSize(R.styleable.BaseRatingBar_brbStarInterval, 0);
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

        if (starEmptyDrawable == null) {
//            starEmptyDrawable = getResources().getDrawable(R.drawable.star_empty_for_base_rating_bar);
            starEmptyDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_empty_for_base_rating_bar, context.getTheme());
        }
        if (drawable != null) {
            starFullBitmap = ImageUtils.drawable2Bitmap(drawable);
        } else {
            starFullBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_full_for_base_rating_bar);
        }
        if (starStepSize <= 0) {
            starStepSize = 0.1F;
        } else if (starStepSize > starCount) {
            starStepSize = 1;
        }
        //先算 starStepSize, 再算 starRating
        starRating = calcRatingBaseOnStep(starRating);

        if (starRatio <= 0) starRatio = 1;

        paintFullStar = new Paint();
        paintFullStar.setAntiAlias(true);
        // 获取系统默认的滑动判定阈值（不同设备适配）
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int starHeightNew = heightSize - getPaddingTop() - getPaddingBottom();

        //if高度发生了变化
        if (starHeight != starHeightNew) {
            starHeight = starHeightNew;
            starWidthF = starHeight * starRatio;

//            Bitmap rec = Bitmap.createScaledBitmap(starFullBitmap, (int) starWidthF, starHeight, true);
//            if (rec != starFullBitmap) {
                /**
                 * 1个xml布局中有多个BaseRatingBar的时候, 不知道抽什么疯:
                 * java.lang.IllegalArgumentException: cannot use a recycled source in createBitmap
                 */
//                starFullBitmap.recycle();
//                starFullBitmap = rec;
//            }
            starFullBitmap = Bitmap.createScaledBitmap(starFullBitmap, (int) starWidthF, starHeight, true);
            paintFullStar.setShader(new BitmapShader(starFullBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        allStarsWidth = (int) (starWidthF * starCount) + starInterval * (starCount - 1);
        if (widthMode == MeasureSpec.AT_MOST) {
            int measureSpec = MeasureSpec.makeMeasureSpec(allStarsWidth + getPaddingStart() + getPaddingEnd(), widthMode);
            super.onMeasure(measureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        //设置总的控件的宽高
//        setMeasuredDimension(allStarsWidth + getPaddingStart() + getPaddingEnd(), heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingStart = getPaddingStart();
        int paddingTop = getPaddingTop();
        for (int i = 0;i < starCount; i++) {
            int left = (int) ((starWidthF + starInterval) * i) + paddingStart;
            starEmptyDrawable.setBounds(left, paddingTop, left + (int) starWidthF, starHeight + paddingTop);
            starEmptyDrawable.draw(canvas);
        }
        if (starRating <= 0) return;
        //需要画🖼️几个整数星星⭐✨️🌟
        int drawCount = (int) starRating;
        //不足1颗星星🌟的剩余小数部分[0, 1)
        float starRemain = starRating - drawCount;
        canvas.translate(paddingStart, paddingTop);
        if (drawCount >= 1) {
            canvas.drawRect(0, 0, starWidthF, starHeight, paintFullStar);
            for (int i = 1; i < drawCount; i++) {
                canvas.translate(starWidthF + starInterval, 0);
                canvas.drawRect(0, 0, starWidthF, starHeight, paintFullStar);
            }
            if (starRating > 1f) canvas.translate(starWidthF + starInterval, 0);
        }
        if (starRemain > 0) {
            canvas.drawRect(0, 0, starWidthF * starRemain, starHeight, paintFullStar);
        }
    }

    /**
     * 触摸事件, 使 {@link BaseRatingBar} 的手势处理方式和 {@link RatingBar} 一样
     * @param event 触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (starIsIndicator) return super.onTouchEvent(event);
//        GestureDetector
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isVerticalScroll = null;
                pressedX = event.getX();
                pressedY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
//                float orientation = event.getOrientation();
                //上下滑动(只判断1次, 判断多次当滑动回来的时候重新计算, isVerticalScroll有可能得出相反值)
                if (isVerticalScroll == null) {
                    float absX = Math.abs(event.getX() - pressedX);
                    float absY = Math.abs(event.getY() - pressedY);
                    if (absX > mTouchSlop || absY > mTouchSlop) {
                        isVerticalScroll = absX < absY;
                        if (isVerticalScroll) {
                            ViewParent parent = getParent();
                            //垂直滑动, 请求父类拦截, 否则父类的ScrollView不能上下滚动
                            if (parent != null) parent.requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        //还不知道朝哪个方向滑动, 请求父类不拦截
                        ViewParent parent = getParent();
                        if (parent != null) parent.requestDisallowInterceptTouchEvent(true);
                        return super.onTouchEvent(event);
                    }
                }
                if (isVerticalScroll) {
                    return super.onTouchEvent(event);
                }
                fromUser = true;
                setRatingBaseOnMotionEvent(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //if是点击
                if (isVerticalScroll == null) {
                    fromUser = true;
                    setRatingBaseOnMotionEvent(event);
                }
            default:
                fromUser = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据 MotionEvent 设置rating
     */
    protected void setRatingBaseOnMotionEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        int realX = (int) x - getPaddingStart();
        if (realX < 0) realX = 0;
        //getMeasuredWidth()有误, ∵宽度有可能是match_parent
//        int width = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
        if (realX > allStarsWidth) realX = allStarsWidth;
        if (realX >= allStarsWidth) {
            setRating(starCount);
        } else {
            if (starCount <= 1) {
                setRating(realX / starWidthF);
            } else {
                float starWidthAndInterval = starWidthF + starInterval;
                //整数星星🌟
                int starIntervalCount = (int) (realX / starWidthAndInterval);
                //剩余部分实际宽度
                float remainActualWidth = realX - starIntervalCount * starWidthAndInterval;
                if (remainActualWidth >= starWidthF) {
                    setRating(starIntervalCount + 1);
                } else {
                    setRating(starIntervalCount + remainActualWidth / starWidthF);
                }
            }
        }
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
    public void setNumStars(@IntRange(from = 1) int numStars) {
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
        if (rating == starRating) return;
        float starRatingOld = starRating;
        starRating = calcRatingBaseOnStep(rating);
        //滑动一点点, 但是没有1个stepSize大小时, 就会出现这种情况
        if (starRating == starRatingOld) return;
        //低版本的JDK浮点运算有可能会出现这种情况: 0.3 * 3 = 0.90000004 (步长0.3)
        if (onRatingBarChangeListener != null) {
            onRatingBarChangeListener.onRatingChanged(this, starRating, fromUser);
        }
        invalidate();
    }

    /**
     * 依据步长 starStepSize 计算应该绘制的星星数量
     * @param rating 得到的未经处理的星星🌟数量
     * @return 返回符合逻辑的, 符合步数的星星数量
     */
    protected float calcRatingBaseOnStep(float rating) {
        if (rating <= 0) return 0;
        if (rating >= starCount) return starCount;
        /**
         * 为何要向上取整?
         * 因为要考虑点击星星🌟的情况, 比如step=1且点击星星的时候, 只要点击到了这颗星星🌟, 那么这1整颗星星都要算上
         */
        rating = (float) (Math.ceil(rating / starStepSize) * starStepSize);
        if (rating >= starCount) return starCount;
        return rating;
    }

    /**
     * 获取绘制的星星数量
     */
    public float getRating() {
        return starRating;
    }

    /**
     * 设置步长
     * @see RatingBar#setStepSize(float)
     */
    public void setStepSize(@FloatRange(from = 0f) float stepSize) {
        if (stepSize > 0) this.starStepSize = stepSize;
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
        void onRatingChanged(@NonNull BaseRatingBar baseRatingBar, float rating, boolean fromUser);
    }
}