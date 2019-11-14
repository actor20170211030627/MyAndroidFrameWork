package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.actor.myandroidframework.R;
import com.blankj.utilcode.util.ConvertUtils;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 快速查找条
 * Author     : 李大发
 * Date       : 2017/3/22 on 20:55.
 *
 * 用法:
 * 1.由于pinyin不常用, 所以如果使用本类, 需要在自己project中添加依赖:
 * //https://github.com/promeG/TinyPinyin
 * implementation 'com.github.promeg:tinypinyin:2.0.3'//TinyPinyin核心包，约80KB
 * //implementation 'com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3'//可选，适用于Android的中国地区词典
 *
 * 1. 布局文件中
 * <com.yunweipei.aftersale.widget.QuickSearchBar
 *     android:id="@+id/quicksearchbar"
 *     android:layout_width="30dp"
 *     android:layout_height="match_parent"
 *     app:qsbBackgroundPressed="@drawable/shape_rec_cor_for_quicksearchbar" //这是默认背景(default)
 *     app:qsbShowTimeMs="2000"                     //默认2s(default)
 *     app:qsbTextColorNormal="@color/gray_8c8c8c"  //默认#8c8c8c
 *     app:qsbTextColorPressed="@color/colorAccent" //默认colorAccent
 *     app:qsbTextSize="10sp" />
 *
 *
 * 2.Java文件中
 * 注意: 列表中的数据必须 extends {@link PinYinSortAble}, 否则报错
 *
 * 设置字母监听: {@link #setOnLetterChangedListener(RecyclerView, OnLetterChangedListener)}
 *
 * 设置字母显示时间, 单位ms: {@link #setShowTime(int)}
 *
 * 设置字体正常颜色: {@link #setTextColorNormal(int)}
 *
 * 设置字体按下时颜色: {@link #setTextColorPressed(int)}}
 *
 * 设置字体大小: {@link #setTextSize(int)}}
 *
 * 设置按下时背景资源: {@link #setBackgroundPressed(int)}}
 *
 *
 * 3.在RecyclerView列表中控制Letter字母的显示/隐藏:
 * int position = helper.getAdapterPosition();
 * helper.setText(R.Id.tv_letter, item.letter)//显示letter: "A", "B" ...
 *         .setGone(R.Id.tv_letter, position == 0 ||
 *                 !TextUtils.equals(item.letter, items.get(position - 1).letter));
 *
 * @version 1.0
 */
public class QuickSearchBar extends View {

    private              Rect                        rect;
    private              Paint                       paint;
    private              float                       mCellWidth;       //控件宽度
    private              float                       mCellHeight;      //控件高度 / 26
    private              int                         showTimeMs = 2000;//字母显示时间
    private              int                         mCurrentIndex = -1; //现在所触摸的索引
    private              int                         textColorNormal;//正常字体颜色
    private              int                         textColorPressed;//按下时的字体颜色, 默认colorAccent
    private              int                         textSize;//字体大小, 默认10sp
    private              Drawable                    pressedBackground;//按下时背景
    private              RecyclerView                recyclerView;
    private              Map<String, Integer>        letterMap      = new HashMap<>();
    private              OnLetterChangedListener     letterChangedListener;
    private              List<PinYinSortAble>        temps = new ArrayList<>();
    private              RecyclerView.SmoothScroller smoothScroller;
    private static final String[]                    SECTIONS = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public static final  int                         POSITION_A = 1;//"A"在上方的position, 从0开始

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHandler.removeMessages(0);
            if (letterChangedListener != null) {
                letterChangedListener.onTimeIsOver();
            }
        }
    };

    public QuickSearchBar(Context context) {
        this(context,null);
    }

    public QuickSearchBar(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public QuickSearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pressedBackground = getResources().getDrawable(R.drawable.shape_rec_cor_for_quicksearchbar);
        textColorNormal = Color.parseColor("#8c8c8c");
        textColorPressed = context.getResources().getColor(R.color.colorAccent);
        textSize = ConvertUtils.sp2px(10);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuickSearchBar);
            Drawable background = typedArray.getDrawable(R.styleable.QuickSearchBar_qsbBackgroundPressed);
            int showTime = typedArray.getInt(R.styleable.QuickSearchBar_qsbShowTimeMs, -1);
            int textColorNormal = typedArray.getColor(R.styleable.QuickSearchBar_qsbTextColorNormal,
                    this.textColorNormal);
            int textColorPressed = typedArray.getColor(R.styleable.QuickSearchBar_qsbTextColorPressed,
                    this.textColorPressed);
            int textSize = typedArray.getDimensionPixelSize(R.styleable.QuickSearchBar_qsbTextSize, -1);
            typedArray.recycle();
            if (background != null) pressedBackground = background;
            if (showTime >= 0) showTimeMs = showTime;
            this.textColorNormal = textColorNormal;
            this.textColorPressed = textColorPressed;
            if (textSize != -1) this.textSize = textSize;
        }
        paint = new Paint();
        rect = new Rect();              //矩形
        paint.setAntiAlias(true);       //抗锯齿
        paint.setColor(textColorNormal);
        paint.setTextSize(textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCellWidth = getMeasuredWidth();    //控件宽度
        mCellHeight = getMeasuredHeight() * 1.0f / SECTIONS.length;//控件高度/26
    }

    @Override
    protected void onDraw(Canvas canvas) {//canvas帆布
        super.onDraw(canvas);
        for (int i = 0; i < SECTIONS.length; i++) {
            //绘制图片:起点在左上角
            //绘制文字:起点在左下角
            paint.getTextBounds(SECTIONS[i] , 0, 1, rect);
            int textWidth = rect.width();   //字体宽度
            int textHeight = rect.height(); //字体高度
            float x = mCellWidth / 2 - textWidth / 2;//字体从小rect的左下角开始画
            float y = mCellHeight / 2 + textHeight / 2 + mCellHeight * i;//字体从小rext的做小脚开始画
            if (i == mCurrentIndex) {
                paint.setColor(textColorPressed);
            }else {
                paint.setColor(textColorNormal);
            }
            canvas.drawText(SECTIONS[i], x, y, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            float y = event.getY();     //相对与这个控件左上角的y坐标
            int oldIndex = mCurrentIndex;
            mCurrentIndex = (int) (y / mCellHeight);
            if (mCurrentIndex > SECTIONS.length - 1) {
                mCurrentIndex = SECTIONS.length - 1;
            }
            if (mCurrentIndex < 0) {
                mCurrentIndex = 0;
            }
            if (oldIndex != mCurrentIndex) {
                String letter = SECTIONS[mCurrentIndex];
                if (letterChangedListener != null) {
                    letterChangedListener.onLetterChanged(letter);
                }
                if (recyclerView != null) {
                        Integer integer = letterMap.get(letter);
                        if(integer != null) {
                            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                            if (layoutManager != null) {
                                if (layoutManager instanceof LinearLayoutManager) {
                                    ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(integer, 0);
                                } else {
                                    //也可以用于 LinearLayoutManager
                                    getSmoothScroller().setTargetPosition(integer);
                                    layoutManager.startSmoothScroll(getSmoothScroller());
                                }
                                //当这个letter在屏幕中已经显示时, 不会滚动
//                            recyclerView.smoothScrollToPosition(integer);
                            }
                    }
                }
                invalidate();//请求重新绘制，此方法会触发系统再次调用onDraw方法
            }
            setBackground(pressedBackground);//当选中右侧条目时背景色
            break;
        case MotionEvent.ACTION_UP:
        default:
            mCurrentIndex = -1;
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, showTimeMs);
            setBackground(null);
            //颜色
//            setBackgroundColor(Color.TRANSPARENT);//设置透明的颜色背景
//            setBackgroundDrawable(new ColorDrawable());//这个也可以设置透明
            invalidate();
            break;
        }
        return true;
    }

    /**
     * 在recyclerview.setAdapter 或 notifydasetchanged之前调用, 排序
     * 注意, List里的数据必须 extends PinYinSortAble, 否则报错. {@link PinYinSortAble}
     */
    public void sortData(List items) {
        if (items != null) {
            //1.添加拼音Letter
            for (int i = 0; i < items.size(); i++) {
                PinYinSortAble item = (PinYinSortAble) items.get(i);
                if (item != null) {
                    String sortString = item.getSortString();//要拼音排序的字符串/字母
                    if (TextUtils.isEmpty(sortString)) {
                        item.letter = SECTIONS[0];
                    } else {
                        char c = sortString.charAt(0);//第一个字母
                        if (c >= 'A' && c <= 'Z') {
                            item.letter = SECTIONS[c - 'A' + POSITION_A];
                        } else if (c >= 'a' && c <= 'z') {
                            item.letter = SECTIONS[c - 'a' + POSITION_A];
                        } else {
                            boolean isChinese = Pinyin.isChinese(c);//是数字, 字母, 等
                            if (isChinese) {
                                String pinyin = Pinyin.toPinyin(sortString, "");
                                if (TextUtils.isEmpty(pinyin)) {
                                    item.letter = SECTIONS[0];
                                } else {
                                    item.letter = pinyin.substring(0, 1);
                                }
                            } else {
                                item.letter = SECTIONS[0];
                            }
                        }
                    }
                }
            }
            //2.根据 SECTIONS 排序数据
            temps.clear();
            for (String section : SECTIONS) {
                for (int i = 0; i < items.size(); i++) {
                    PinYinSortAble item = (PinYinSortAble) items.get(i);
                    if (item != null && section.equals(item.letter)) {
                        temps.add(item);
                    }
                }
            }
            //3.记录每种分类第一个position
            letterMap.clear();
            items.clear();
            for (int i = 0; i < temps.size(); i++) {
                PinYinSortAble temp = temps.get(i);
                items.add(temp);
                String letter = temp.letter;
                if (letterMap.get(letter) == null) letterMap.put(letter, i);
            }
            temps.clear();
        }
    }

    public void setOnLetterChangedListener(RecyclerView recyclerView, OnLetterChangedListener letterChangedListener){
        this.recyclerView = recyclerView;
        this.letterChangedListener = letterChangedListener;
    }

    /**
     * 设置正常字体颜色
     */
    public void setTextColorNormal(@ColorInt int colorNormal){
        this.textColorNormal = colorNormal;
        invalidate();
    }

    /**
     * 设置选中文字按下时颜色
     * @param colorPressed
     */
    public void setTextColorPressed(@ColorInt int colorPressed) {
        this.textColorPressed = colorPressed;
    }

    /**
     * 设置字体大小, 默认10sp
     */
    public void setTextSize(int textSizeSp){
        this.textSize = ConvertUtils.sp2px(textSizeSp);
        invalidate();
    }

    /**
     * 设置字母显示时间, 单位ms
     */
    public void setShowTime(@IntRange(from = 0) int showTimeMs){
        this.showTimeMs = showTimeMs;
        invalidate();
    }

    /**
     * @param backgroundPressed 设置按下时背景资源, 示例: R.drawable.xxx
     */
    public void setBackgroundPressed(@DrawableRes int backgroundPressed) {
        this.pressedBackground = getResources().getDrawable(backgroundPressed);
    }

    public static abstract class PinYinSortAble {

        /**
         * letter 每一条数据经过拼音排序后返回的字符串, 例: "A", "B" ...
         */
        public String letter;

        /**
         * @return 返回要拼音排序的字符串, 可返回:
         * 1.汉字
         * 2.字母A B C...
         */
        public abstract String getSortString();
    }

    public interface OnLetterChangedListener {
        /**
         * @param letter 滑动到这个字母
         */
        void onLetterChanged(String letter);

        /**
         * 显示时间已到
         */
        void onTimeIsOver();
    }

    //https://www.jianshu.com/p/d1471c11e78b
    private RecyclerView.SmoothScroller getSmoothScroller() {
        if (smoothScroller == null) {
            smoothScroller = new LinearSmoothScroller(getContext()) {

                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }

                //计算滚动速度
                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return super.calculateSpeedPerPixel(displayMetrics);
//            return 40F;//调慢一点可将这个值调大一点
                }
            };
        }
        return smoothScroller;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
    }
}
