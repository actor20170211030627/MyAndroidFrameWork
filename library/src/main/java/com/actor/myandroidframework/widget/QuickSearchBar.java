package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.R;
import com.blankj.utilcode.util.ConvertUtils;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 快速查找条 <br/>
 * Author     : ldf <br />
 * Date       : 2017/3/22 on 20:55.
 *
 * <pre> {@code
 * 用法:
 * 1.由于pinyin不常用, 所以如果使用本类, 需要在自己project中添加依赖:
 * //https://github.com/promeG/TinyPinyin
 * //TinyPinyin核心包，约80KB
 * implementation 'com.github.promeg:tinypinyin:2.0.3'
 * //可选，适用于Android的中国地区词典
 * //implementation 'com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3'
 *
 * 2. 布局文件中
 * <com.actor.myandroidframework.widget.QuickSearchBar
 *     android:id="@+id/quicksearchbar"
 *     android:layout_width="30dp"
 *     android:layout_height="match_parent"
 *     app:qsbBackgroundPressed="@drawable/shape_rec_cor_for_quicksearchbar" //这是默认背景
 *     app:qsbTextColorNormal="@color/gray_8c8c8c"  //默认#8c8c8c
 *     app:qsbTextColorPressed="@color/colorAccent" //默认colorAccent
 *     app:qsbTextSize="10sp" />
 * }</pre>
 *
 * <pre>
 * 3.Java文件中
 *   注意: 列表中的数据必须 extends {@link PinYinSortAble PinYinSortAble}, 否则报错
 *   1.设置字母监听: {@link #setOnLetterChangedListener(RecyclerView, OnLetterChangedListener) setOnLetterChangedListener(RecyclerView, OnLetterChangedListener)}
 *   2.设置字体正常颜色: {@link #setTextColorNormal(int)}
 *   3.设置字体按下时颜色: {@link #setTextColorPressed(int)}
 *   4.设置字体大小: {@link #setTextSize(int)}
 *   5.设置按下时背景资源: {@link #setBackgroundPressed(int)}
 *
 * 4.在RecyclerView列表中控制 {@link #LETTERS} 字母的显示/隐藏:
 * int position = helper.getAdapterPosition();
 * helper.setText(R.id.tv_letter, item.letter)//显示letter: "A", "B" ...
 *         .setGone(R.id.tv_letter, position > 0 &&
 *                 TextUtils.equals(item.letter, items.get(position - 1).letter));
 *</pre>
 * @version 1.0
 */
public class QuickSearchBar<T extends QuickSearchBar.PinYinSortAble> extends View {

    protected static final int                         INDEX_NONE    = -1;
    protected              Rect                        rect;
    protected              Paint                       paint;
    //控件宽度
    protected              float                       mCellWidth;
    //控件高度 / 26
    protected              float                       mCellHeight;
    //现在所触摸的索引
    protected              int                         mCurrentIndex = INDEX_NONE;
    //正常字体颜色
    protected              int                         textColorNormal;
    //按下时的字体颜色, 默认colorAccent
    protected              int                         textColorPressed;
    //字体大小, 默认10sp
    protected              int                         textSize;
    //按下时背景
    protected              Drawable                    pressedBackground;
    protected              RecyclerView                recyclerView;
    protected              Map<String, Integer>        letterMap     = new HashMap<>();
    protected              OnLetterChangedListener     letterChangedListener;
    protected              List<T>                     temps         = new ArrayList<>();
    protected              RecyclerView.SmoothScroller smoothScroller;
    //字母
    protected              String[]                    LETTERS       = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    //"A"在上方的position, 从0开始
    public static final    int                         POSITION_A    = 1;

    public QuickSearchBar(Context context) {
        super(context);
        init(context, null);
    }

    public QuickSearchBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public QuickSearchBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public QuickSearchBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        pressedBackground = getResources().getDrawable(R.drawable.shape_rec_cor_for_quicksearchbar);
        textColorNormal = Color.parseColor("#8c8c8c");
        textColorPressed = context.getResources().getColor(R.color.colorAccent);
        textSize = ConvertUtils.sp2px(10);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuickSearchBar);
            Drawable background = typedArray.getDrawable(R.styleable.QuickSearchBar_qsbBackgroundPressed);
            int textColorNormal = typedArray.getColor(R.styleable.QuickSearchBar_qsbTextColorNormal,
                    this.textColorNormal);
            int textColorPressed = typedArray.getColor(R.styleable.QuickSearchBar_qsbTextColorPressed,
                    this.textColorPressed);
            textSize = typedArray.getDimensionPixelSize(R.styleable.QuickSearchBar_qsbTextSize, textSize);
            typedArray.recycle();
            if (background != null) pressedBackground = background;
            this.textColorNormal = textColorNormal;
            this.textColorPressed = textColorPressed;
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
        mCellHeight = getMeasuredHeight() * 1.0f / LETTERS.length;//控件高度/26
    }

    @Override
    protected void onDraw(Canvas canvas) {//canvas帆布
        super.onDraw(canvas);
        for (int i = 0; i < LETTERS.length; i++) {
            //绘制图片:起点在左上角
            //绘制文字:起点在左下角
            paint.getTextBounds(LETTERS[i], 0, 1, rect);
            int textWidth = rect.width();   //字体宽度
            int textHeight = rect.height(); //字体高度
            float x = mCellWidth / 2 - textWidth / 2F;//字体从小rect的左下角开始画
            float y = mCellHeight / 2 + textHeight / 2F + mCellHeight * i;//字体从小rext的做小脚开始画
            if (i == mCurrentIndex) {
                paint.setColor(textColorPressed);
            } else {
                paint.setColor(textColorNormal);
            }
            canvas.drawText(LETTERS[i], x, y, paint);
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
                if (mCurrentIndex > LETTERS.length - 1) {
                    mCurrentIndex = LETTERS.length - 1;
                }
                if (mCurrentIndex < 0) {
                    mCurrentIndex = 0;
                }
                if (oldIndex != mCurrentIndex) {
                    String letter = LETTERS[mCurrentIndex];
                    if (letterChangedListener != null) {
                        letterChangedListener.onLetterChanged(letter);
                    }
                    if (recyclerView != null) {
                        Integer integer = letterMap.get(letter);
                        if (integer != null) {
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
                mCurrentIndex = INDEX_NONE;
                if (letterChangedListener != null) {
                    letterChangedListener.onActionUp();
                }
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
     * 排序, 在recyclerview.setAdapter 或 notifydasetchanged之前调用.
     * 注意, List里的数据必须 extends PinYinSortAble, 否则报错. {@link PinYinSortAble}
     */
    public void sortData(List<T> items) {
        if (items != null) {
            //1.添加拼音Letter
            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                if (item != null) {
                    String sortString = item.getSortString();//要拼音排序的字符串/字母
                    if (TextUtils.isEmpty(sortString)) {
                        item.setLetter(LETTERS[0]);
                    } else {
                        char c = sortString.charAt(0);//第一个字母
                        if (c >= 'A' && c <= 'Z') {
                            item.setLetter(LETTERS[c - 'A' + POSITION_A]);
                        } else if (c >= 'a' && c <= 'z') {
                            item.setLetter(LETTERS[c - 'a' + POSITION_A]);
                        } else {
                            boolean isChinese = Pinyin.isChinese(c);//是数字, 字母, 等
                            if (isChinese) {
                                String pinyin = Pinyin.toPinyin(sortString, "");
                                if (TextUtils.isEmpty(pinyin)) {
                                    item.setLetter(LETTERS[0]);
                                } else {
                                    item.setLetter(pinyin.substring(0, 1));
                                }
                            } else {
                                item.setLetter(LETTERS[0]);
                            }
                        }
                    }
                }
            }
            //2.根据 LETTERS 排序数据
            temps.clear();
            for (String section : LETTERS) {
                for (int i = 0; i < items.size(); i++) {
                    T item = items.get(i);
                    if (item != null && section.equals(item.getLetter())) {
                        temps.add(item);
                    }
                }
            }
            //3.记录每种分类第一个position
            letterMap.clear();
            items.clear();
            for (int i = 0; i < temps.size(); i++) {
                T temp = temps.get(i);
                items.add(temp);
                String letter = temp.getLetter();
                if (letterMap.get(letter) == null) letterMap.put(letter, i);
            }
            temps.clear();
        }
    }

    public void setOnLetterChangedListener(RecyclerView recyclerView, OnLetterChangedListener letterChangedListener) {
        this.recyclerView = recyclerView;
        this.letterChangedListener = letterChangedListener;
    }

    /**
     * 设置正常字体颜色
     */
    public void setTextColorNormal(@ColorInt int colorNormal) {
        this.textColorNormal = colorNormal;
        invalidate();
    }

    /**
     * 设置选中文字按下时颜色
     *
     * @param colorPressed
     */
    public void setTextColorPressed(@ColorInt int colorPressed) {
        this.textColorPressed = colorPressed;
    }

    /**
     * 设置字体大小, 默认10sp
     */
    public void setTextSize(int textSizeSp) {
        this.textSize = ConvertUtils.sp2px(textSizeSp);
        invalidate();
    }

    /**
     * @param backgroundPressed 设置按下时背景资源, 示例: R.drawable.xxx
     */
    public void setBackgroundPressed(@DrawableRes int backgroundPressed) {
        this.pressedBackground = getResources().getDrawable(backgroundPressed);
    }

    public interface PinYinSortAble {

        //子类需要增加下面一行
//        private String letter;

        /**
         * @param letter 每一条数据经过拼音排序后返回的字符串, 例: "A", "B" ...
         *               照下放这样写↓
         */
        void setLetter(String letter) /*{this.letter = letter}*/;

        /**
         * @return 返回 letter  照下放这样写↓
         */
        String getLetter() /*{return letter}*/;

        /**
         * @return 返回要拼音排序的字符串, 可返回:
         * 1.汉字
         * 2.字母A B C...
         */
        String getSortString();
    }

    public interface OnLetterChangedListener {
        /**
         * @param letter 滑动到这个字母
         */
        void onLetterChanged(String letter);

        /**
         * 已经取消滑动, 松开手指
         */
        void onActionUp();
    }

    //https://www.jianshu.com/p/d1471c11e78b
    protected RecyclerView.SmoothScroller getSmoothScroller() {
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
}
