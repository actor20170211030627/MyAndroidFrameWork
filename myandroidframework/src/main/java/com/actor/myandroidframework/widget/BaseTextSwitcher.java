package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.actor.myandroidframework.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: TextView切换, 水平切换效果 & 垂直切换效果(小喇叭通知消息翻滚)
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-8-23 on 16:39
 *
 * @see android.widget.ViewAnimator 里的3个属性:
 * @attr ref android.R.styleable#ViewAnimator_inAnimation 设置进入动画, 如果不设置, 默认从底部进入
 * @attr ref android.R.styleable#ViewAnimator_outAnimation 设置退出动画, 如果不设置, 默认从顶部退出
 * @attr ref android.R.styleable#ViewAnimator_animateFirstView 第一次进入是否有动画, 默认true
 *
 * 下方是自定义属性:
 * btsTextColor         字体颜色
 * btsTextSize          字体大小
 * btsTextStyle         normal, bold, italic, bold|italic(字体加粗|斜体, 默认正常normal)
 * btsSwitchIntervalMs  切换间隔, 单位ms, 至少100ms, 否则不生效. 默认3000ms
 * btsOrientation       切换方向, 水平/垂直
 * btsSingleLineMarquee 是否是 "单行&跑马灯", 默认true
 * btsMaxLinesNoMarquee 最大行数,
 *                          1.如果btsSingleLineMarquee="true", 那么这个属性无效.
 *                          2.如果btsSingleLineMarquee="false":
 *                             2.1.如果设置了这个属性, TextView将设置最大行数
 *                             2.2.如果没有设置这个属性, TextView有多少行就占多少行
 *
 * 怎么使用:
 * 1.在布局文件件中:
 * <com.actor.myandroidframework.widget.BaseTextSwitcher
 *     android:id="@+id/bts"
 *     android:layout_width="match_parent"
 *     android:layout_height="100dp"
 *     //android:animateFirstView="true"    //默认true
 *     //android:inAnimation="@anim/address_dialog_enter"   //可不设置, 默认有动画
 *     //android:outAnimation="@anim/address_dialog_enter"  //可不设置, 默认有动画
 *     //app:btsMaxLinesNoMarquee="1"       //最大行数, 可不设置
 *     //app:btsOrientation="vertical"      //默认vertical
 *     //app:btsSingleLineMarquee="true"    //默认true
 *     //app:btsSwitchIntervalMs="2000"     //默认3000
 *     app:btsTextColor="@color/colorAccent"
 *     app:btsTextSize="28sp"
 *     //app:btsTextStyle="bold|italic">    //默认normal
 * </com.actor.myandroidframework.widget.BaseTextSwitcher>
 *
 * 2.在Activity/Fragment中:
 * baseTextSwitcher.setDataSource(datas);
 * baseTextSwitcher.setOnItemClickListener(new BaseTextSwitcher.OnItemClickListener() {
 *     @Override
 *     public void onItemClick(int position, CharSequence charSequence) {
 *         logFormat("pos=%d, str=%s", position, charSequence);
 *     }
 * });
 * bts.startSwitch();//在onStart();方法中调用
 * bts.stopSwitcher();//在onStop();方法中调用
 *
 * @version 1.0
 */
public class BaseTextSwitcher extends TextSwitcher {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Handler handler = new Handler();
    private int pos = 0;
    private Runnable runnable;
    protected List<CharSequence> items = new ArrayList<>();
    private int textColor = -1;
    private int textSize = -1;
    private int textStype = 0;
    private int switchIntervalMs = 3_000;
    private int orientation = VERTICAL;
    private boolean singleLineMarquee = true;//单行跑马灯效果
    private int maxLinesNoMarquee = 0;//最大行数
    private OnItemClickListener onItemClickListener;

    public BaseTextSwitcher(Context context) {
        this(context, null);
    }

    public BaseTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseTextSwitcher);
            textColor = typedArray.getColor(R.styleable.BaseTextSwitcher_btsTextColor, -1);
            textSize = typedArray.getDimensionPixelSize(R.styleable.BaseTextSwitcher_btsTextSize, -1);
            textStype = typedArray.getInt(R.styleable.BaseTextSwitcher_btsTextStyle, 0);
            int interval = typedArray.getInt(R.styleable.BaseTextSwitcher_btsSwitchIntervalMs, -1);
            orientation = typedArray.getInt(R.styleable.BaseTextSwitcher_btsOrientation, VERTICAL);
            singleLineMarquee = typedArray.getBoolean(R.styleable.BaseTextSwitcher_btsSingleLineMarquee, true);
            if (interval >= 100) switchIntervalMs = interval;
            if (!singleLineMarquee) {//如果不是单行跑马灯效果
                int max = typedArray.getInt(R.styleable.BaseTextSwitcher_btsMaxLinesNoMarquee, 0);
                if (max > 0) maxLinesNoMarquee = max;//最大行数
            }
            typedArray.recycle();
        }
        TextView tv1 = new TextView(context);
        TextView tv2 = new TextView(context);
        if (textColor != -1) {//字体颜色
            tv1.setTextColor(textColor);
            tv2.setTextColor(textColor);
        }
        if (textSize != -1) {//字体大小
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        int style = 0;//字体style
        if ((textStype & Typeface.BOLD) != 0) {//加粗
            style |= Typeface.BOLD;//AssistStructure.ViewNode.TEXT_STYLE_BOLD;
        }
        if ((textStype & Typeface.ITALIC) != 0) {//斜体
            style |= Typeface.ITALIC;//AssistStructure.ViewNode.TEXT_STYLE_ITALIC;
        }
        if (style != 0) {//加粗|斜体
            tv1.setTypeface(Typeface.defaultFromStyle(style));
            tv2.setTypeface(Typeface.defaultFromStyle(style));
        }
        tv1.setGravity(Gravity.CENTER_VERTICAL);//字体垂直居中
        tv2.setGravity(Gravity.CENTER_VERTICAL);
        if (singleLineMarquee) {
            tv1.setSingleLine(true);
            tv2.setSingleLine(true);
            tv1.setFocusable(true);
            tv2.setFocusable(true);
            tv1.setFocusableInTouchMode(true);
            tv2.setFocusableInTouchMode(true);
            tv1.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv2.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            if (maxLinesNoMarquee > 0) {
                tv1.setMaxLines(maxLinesNoMarquee);
                tv2.setMaxLines(maxLinesNoMarquee);
            }
        }
        tv1.setOnClickListener(v -> {//点击事件
            if (items.size() > 0 && pos < items.size() && onItemClickListener != null) {
                onItemClickListener.onItemClick((TextView) v, pos, items.get(pos));
            }
        });
        tv2.setOnClickListener(v -> {
            if (items.size() > 0 && pos < items.size() && onItemClickListener != null) {
                onItemClickListener.onItemClick((TextView) v, pos, items.get(pos));
            }
        });
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(tv1, 0, layoutParams);
        addView(tv2, 1, layoutParams);

        runnable = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, switchIntervalMs);
                showNextView();
            }
        };
    }

    /**
     * 设置数据源, 默认垂直滚动
     */
    public void setDataSource(CharSequence[] dataSource) {
        if (dataSource != null) setDataSource(Arrays.asList(dataSource), orientation);
    }

    /**
     * 设置数据源, 默认垂直滚动
     */
    public void setDataSource(List<CharSequence> dataSource) {
        setDataSource(dataSource, orientation);
    }

    /**
     * 设置数据源
     */
    public void setDataSource(List<CharSequence> dataSource, @OrientationMode int orientation) {
        if (dataSource == null) return;
        items.clear();
        items.addAll(dataSource);
        Animation inAnimation = getInAnimation();//进入动画
        Animation outAnimation = getOutAnimation();//退出动画
        if (inAnimation == null) {
            if (orientation == HORIZONTAL) {
                setInAnimation(AnimationUtils.makeInAnimation(getContext(), false));
            } else setInAnimation(AnimationUtils.makeInChildBottomAnimation(getContext()));
        }
        if (outAnimation == null) {
            if (orientation == HORIZONTAL) {
                setOutAnimation(AnimationUtils.makeOutAnimation(getContext(), false));
            } else setOutAnimation(getContext(), R.anim.slide_out_child_top);
        }
    }

    /**
     * 展示下一个
     */
    public void showNextView() {
        int size = items.size();
        if (size > 0) {
            if (pos == size - 1) {
                pos = 0;
            } else ++ pos;
            setText(items.get(pos));
        }
    }

    /**
     * 开始切换
     * 也可以不调用这个方法, 自己定时调用showNextView(), 比如和轮播图同步展示时
     */
    public void startSwitch() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, switchIntervalMs);
    }

    /**
     * 停止切换
     */
    public void stopSwitcher() {
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * item点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * @param textView 点击的TextView
         * @param position 点击的哪一个item
         * @param charSequence item的值
         */
        void onItemClick(TextView textView, int position, CharSequence charSequence);
    }

    //只有2个孩子
    @Override
    public View getChildAt(@IntRange(from = 0, to = 1) int index) {
        return super.getChildAt(index);
    }
}
