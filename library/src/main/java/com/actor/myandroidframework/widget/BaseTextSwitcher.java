package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
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
import android.widget.ViewSwitcher;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: TextView切换, 水平切换效果 & 垂直切换效果(小喇叭通知消息翻滚) <br />
 * Author     : ldf <br />
 * Date       : 2019-8-23 on 16:39 <br />
 *
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <th>一. {@link android.widget.ViewAnimator ViewAnimator} 属性</th>
 *     </tr>
 *     <tr>
 *         <td>{@link com.android.internal.R.styleable#ViewAnimator_inAnimation inAnimation}</td>
 *         <td>@anim/xxx</td>
 *         <td>1.设置进入动画, 如果不设置, 默认从底部进入</td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.android.internal.R.styleable#ViewAnimator_outAnimation outAnimation}</td>
 *         <td>@anim/xxx</td>
 *         <td>2.设置退出动画, 如果不设置, 默认从顶部退出</td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.android.internal.R.styleable#ViewAnimator_animateFirstView animateFirstView}</td>
 *         <td>true</td>
 *         <td>3.第一次进入是否有动画, 默认true</td>
 *     </tr>
 *
 *     <tr />
 *     <tr>
 *         <th>二. {@link BaseTextSwitcher} 自定义属性</th>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#BaseTextSwitcher_btsTextColor btsTextColor}</td>
 *         <td>@color/xxx</td>
 *         <td>1.字体颜色, 默认系统灰</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#BaseTextSwitcher_btsTextSize btsTextSize}</td>
 *         <td>16sp</td>
 *         <td>2.字体大小, 默认系统字体大小</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#BaseTextSwitcher_btsTextStyle btsTextStyle}</td>
 *         <td>normal, bold, italic</td>
 *         <td>3.字体样式: 加粗, 斜体, 默认正常normal</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#BaseTextSwitcher_btsSwitchIntervalMs btsSwitchIntervalMs}</td>
 *         <td>3000</td>
 *         <td>4.切换间隔, 单位ms, 至少100ms, 否则不生效. 默认3000ms</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#BaseTextSwitcher_btsOrientation btsOrientation}</td>
 *         <td>vertical</td>
 *         <td>5.切换方向, 水平/垂直. 默认vertical</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#BaseTextSwitcher_btsSingleLineMarquee btsSingleLineMarquee}</td>
 *         <td>true</td>
 *         <td>6.是否是 "单行&跑马灯", 默认true</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#BaseTextSwitcher_btsMaxLinesNoMarquee btsMaxLinesNoMarquee}</td>
 *         <td>2</td>
 *         <td>
 *             7.最大行数 <br />
 *               1.如果{@link R.styleable#BaseTextSwitcher_btsSingleLineMarquee btsSingleLineMarquee}="true", 那么这个属性无效. <br />
 *               2.如果{@link R.styleable#BaseTextSwitcher_btsSingleLineMarquee btsSingleLineMarquee}="false": <br />
 *                  2.1.如果设置了这个属性, TextView将设置最大行数 <br />
 *                  2.2.如果没有设置这个属性, TextView有多少行就占多少行
 *         </td>
 *     </tr>
 * </table>
 *
 * <pre> {@code
 * 怎么使用:
 * 1.在布局文件件中:
 * <com.actor.myandroidframework.widget.BaseTextSwitcher
 *     android:id="@+id/bts"
 *     android:layout_width="match_parent"
 *     android:layout_height="100dp"
 *     //android:animateFirstView="true"    //默认true
 *     //android:inAnimation="@android:anim/slide_in_right"  //可不设置, 默认有动画
 *     //android:outAnimation="@android:anim/slide_out_left" //可不设置, 默认有动画
 *     //app:btsMaxLinesNoMarquee="1"       //最大行数, 可不设置
 *     //app:btsOrientation="vertical"      //默认vertical
 *     //app:btsSingleLineMarquee="true"    //默认true
 *     //app:btsSwitchIntervalMs="2000"     //默认3000
 *     app:btsTextColor="@color/colorAccent"
 *     app:btsTextSize="28sp"
 *     //app:btsTextStyle="bold|italic" />  //默认normal
 *
 * 2.在Activity/Fragment中:
 * List<T> datas = new ArrayList<>();//任意数据类型
 * baseTextSwitcher.setDataSource(datas);
 * baseTextSwitcher.setOnItemClickListener(new BaseTextSwitcher.OnItemClickListener<T>() {
 *     @Override
 *     public void onItemClick(int position, T item) {
 *         LogUtils.errorFormat("pos=%d, str=%s", position, item);
 *     }
 * });
 * }
 * //在onStart()方法中调用, 开始滚动
 * baseTextSwitcher.{@link #startSwitch()};
 * //在onStop()方法中调用, 停止滚动
 * baseTextSwitcher.{@link #stopSwitch()};
 * </pre>
 *
 * @version 1.0
 */
public class BaseTextSwitcher<T> extends TextSwitcher implements ViewSwitcher.ViewFactory {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    protected Handler handlerForTextSwitcher = new Handler();
    protected int     posForTextSwitcher     = 0;
    protected Runnable runnableForTextSwitcher;
    protected List<T> itemsForTextSwitcher     = new ArrayList<>();
    protected int textColorForTextSwitcher = -1;
    protected int textSizeForTextSwitcher              = -1;
    protected int textStypeForTextSwitcher             = 0;
    protected int     switchIntervalMsForTextSwitcher  = 3_000;//动画切换间隔
    protected int     orientationForTextSwitcher       = VERTICAL;
    protected boolean singleLineMarqueeForTextSwitcher = true;//单行跑马灯效果
    protected int                    maxLinesNoMarqueeForTextSwitcher = 0;//最大行数
    protected OnItemClickListener<T> onItemClickListenerForTextSwitcher;

    public BaseTextSwitcher(Context context) {
        super(context);
        init(context, null);
    }

    public BaseTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseTextSwitcher);
            textColorForTextSwitcher = typedArray.getColor(R.styleable.BaseTextSwitcher_btsTextColor, -1);
            textSizeForTextSwitcher = typedArray.getDimensionPixelSize(R.styleable.BaseTextSwitcher_btsTextSize, -1);
            textStypeForTextSwitcher = typedArray.getInt(R.styleable.BaseTextSwitcher_btsTextStyle, 0);
            int interval = typedArray.getInt(R.styleable.BaseTextSwitcher_btsSwitchIntervalMs, -1);
            orientationForTextSwitcher = typedArray.getInt(R.styleable.BaseTextSwitcher_btsOrientation, VERTICAL);
            singleLineMarqueeForTextSwitcher = typedArray.getBoolean(R.styleable.BaseTextSwitcher_btsSingleLineMarquee, true);
            if (interval >= 100) switchIntervalMsForTextSwitcher = interval;
            if (!singleLineMarqueeForTextSwitcher) {//如果不是单行跑马灯效果
                int max = typedArray.getInt(R.styleable.BaseTextSwitcher_btsMaxLinesNoMarquee, 0);
                if (max > 0) maxLinesNoMarqueeForTextSwitcher = max;//最大行数
            }
            typedArray.recycle();
        }
        setFactory(this);
        runnableForTextSwitcher = new Runnable() {
            @Override
            public void run() {
                handlerForTextSwitcher.removeCallbacks(runnableForTextSwitcher);
                handlerForTextSwitcher.postDelayed(runnableForTextSwitcher, switchIntervalMsForTextSwitcher);
                showNextView();
            }
        };
    }

    @Override
    public View makeView() {
        TextView tv = new TextView(getContext());
        if (textColorForTextSwitcher != -1) {//字体颜色
            tv.setTextColor(textColorForTextSwitcher);
        }
        if (textSizeForTextSwitcher != -1) {//字体大小
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeForTextSwitcher);
        }
        int style = 0;//字体style
        if ((textStypeForTextSwitcher & Typeface.BOLD) != 0) {//加粗
            style |= Typeface.BOLD;//AssistStructure.ViewNode.TEXT_STYLE_BOLD;
        }
        if ((textStypeForTextSwitcher & Typeface.ITALIC) != 0) {//斜体
            style |= Typeface.ITALIC;//AssistStructure.ViewNode.TEXT_STYLE_ITALIC;
        }
        if (style != 0) {//加粗|斜体
            tv.setTypeface(Typeface.defaultFromStyle(style));
        }
        tv.setGravity(Gravity.CENTER_VERTICAL);//字体垂直居中
        if (singleLineMarqueeForTextSwitcher) {
            tv.setSingleLine(true);
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            if (maxLinesNoMarqueeForTextSwitcher > 0) {
                tv.setMaxLines(maxLinesNoMarqueeForTextSwitcher);
            }
        }
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//点击事件
                if (!itemsForTextSwitcher.isEmpty() && posForTextSwitcher < itemsForTextSwitcher.size() && onItemClickListenerForTextSwitcher != null) {
                    onItemClickListenerForTextSwitcher.onItemClick((TextView) v, posForTextSwitcher, itemsForTextSwitcher.get(posForTextSwitcher));
                }
            }
        });
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(layoutParams);
        return tv;
    }

    /**
     * 设置数据源, 默认垂直滚动
     * @param dataSource 数据类型'T'可以是任意类型,
     *                   如果'T'是CharSequence类型, 直接显示.
     *                   如果'T'是其它类型Object, 会显示Object的toString()方法.
     */
    public void setDataSource(List<T> dataSource) {
        setDataSource(dataSource, orientationForTextSwitcher);
    }

    /**
     * 设置数据源
     */
    public void setDataSource(List<T> dataSource, @OrientationMode int orientation) {
        if (dataSource == null) return;
        itemsForTextSwitcher.clear();
        itemsForTextSwitcher.addAll(dataSource);
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
        int size = itemsForTextSwitcher.size();
        if (size > 0) {
            if (posForTextSwitcher == size - 1) {
                posForTextSwitcher = 0;
            } else ++posForTextSwitcher;
            T t = itemsForTextSwitcher.get(posForTextSwitcher);
            if (t instanceof CharSequence) {
                setText((CharSequence) t);
            } else {
                setText(String.valueOf(t));
            }
        }
    }

    /**
     * 获取item
     */
    @Nullable
    public T getItem(@IntRange(from = 0) int position) {
        if (!itemsForTextSwitcher.isEmpty() && position < itemsForTextSwitcher.size()) {
            return itemsForTextSwitcher.get(position);
        }
        return null;
    }

    /**
     * 开始切换
     * 也可以不调用这个方法, 自己定时调用showNextView(), 比如和轮播图同步展示时
     */
    public void startSwitch() {
        handlerForTextSwitcher.removeCallbacks(runnableForTextSwitcher);
        handlerForTextSwitcher.postDelayed(runnableForTextSwitcher, switchIntervalMsForTextSwitcher);
    }

    /**
     * 停止切换
     */
    public void stopSwitch() {
        handlerForTextSwitcher.removeCallbacks(runnableForTextSwitcher);
        handlerForTextSwitcher.removeCallbacksAndMessages(null);
    }

    /**
     * item点击事件
     */
    public void setOnItemClickListenerForTextSwitcher(OnItemClickListener<T> onItemClickListenerForTextSwitcher) {
        this.onItemClickListenerForTextSwitcher = onItemClickListenerForTextSwitcher;
    }

    public interface OnItemClickListener<T> {
        /**
         * @param textView 点击的TextView
         * @param position 点击的哪一个item
         * @param item     item的值
         */
        void onItemClick(TextView textView, int position, T item);
    }

    //只有2个孩子
    @Override
    public View getChildAt(@IntRange(from = 0, to = 1) int index) {
        return super.getChildAt(index);
    }
}
