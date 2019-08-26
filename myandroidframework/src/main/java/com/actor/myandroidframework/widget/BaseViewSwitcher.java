package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import com.actor.myandroidframework.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: View切换, 水平切换效果 & 垂直切换效果
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/8/24 on 14:51
 *
 * @see android.widget.ViewAnimator 里的3个属性:
 * @attr ref android.R.styleable#ViewAnimator_inAnimation 设置进入动画, 如果不设置, 默认从底部进入
 * @attr ref android.R.styleable#ViewAnimator_outAnimation 设置退出动画, 如果不设置, 默认从顶部退出
 * @attr ref android.R.styleable#ViewAnimator_animateFirstView 第一次进入是否有动画, 默认true
 *
 * 下方是自定义属性:
 * bvsSwitchIntervalMs  切换间隔, 单位ms, 至少100ms, 否则不生效. 默认3000ms
 * bvsOrientation       切换方向, 水平/垂直
 *
 * 怎么使用:
 * 1.在布局文件件中:
 * <com.actor.myandroidframework.widget.BaseViewSwitcher
 *     android:id="@+id/bvs"
 *     android:layout_width="match_parent"
 *     android:layout_height="50dp"
 *     app:bvsOrientation="horizontal"
 *     app:bvsSwitchIntervalMs="1000">
 * </com.actor.myandroidframework.widget.BaseViewSwitcher>
 *
 * 2.在Activity/Fragment中:
 * List<CharSequence> datas = new ArrayList<>();//可以是其它任意数据类型
 * baseViewSwitcher.init(R.layout.item_base_view_switcher, new BaseViewSwitcher.OnSwitcherListener<CharSequence>() {
 *     @Override
 *     public void onSwitch(View view, int position, CharSequence data) {
 *         TextView textView = view.findViewById(R.id.tv);//找到你自己需要填充数据的view
 *         textView.setText(data);
 *         logFormat("onSwitch: view=%s, pos=%d, item=%s", view, position, data);
 *     }
 * });
 * baseViewSwitcher.setOnItemClickListener(new BaseViewSwitcher.OnItemClickListener<CharSequence>() {
 *     @Override
 *     public void onItemClick(View view, int position, CharSequence data) {
 *         logFormat("pos=%d, str=%s", position, data);
 *     }
 * });
 * baseViewSwitcher.setDataSource(datas);
 *
 * @version 1.0
 */
public class BaseViewSwitcher extends ViewSwitcher {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Context context;
    protected List items = new ArrayList<>();
    private Handler handler = new Handler();
    private int pos = 0;
    private Runnable runnable;
    private   int                                  switchIntervalMs = 3_000;
    private   int                                  orientation = VERTICAL;
    private OnItemClickListener onItemClickListener;//item点击事件
    private OnSwitcherListener onSwitcherListener;//切换监听
    private LayoutParams layoutParams;

    public BaseViewSwitcher(Context context) {
        this(context, null);
    }

    public BaseViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseViewSwitcher);
            int interval = typedArray.getInt(R.styleable.BaseViewSwitcher_bvsSwitchIntervalMs, -1);
            orientation = typedArray.getInt(R.styleable.BaseViewSwitcher_bvsOrientation, VERTICAL);
            if (interval >= 100) switchIntervalMs = interval;
            typedArray.recycle();
        }

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
     * 初始化, 设置View & 切换监听, 默认垂直滚动
     * @param layoutResId view的布局id
     * @param onSwitcherListener 切换监听
     */
    public <T> void init(@LayoutRes int layoutResId, @NonNull OnSwitcherListener<T> onSwitcherListener) {
        init(layoutResId, orientation, onSwitcherListener);
    }

    /**
     * 初始化, 设置View & 切换监听
     * @param layoutResId
     * @param orientation
     * @param onSwitcherListener
     */
    public <T> void init(@LayoutRes int layoutResId, @OrientationMode int orientation, @NonNull OnSwitcherListener<T> onSwitcherListener) {
        setView(layoutResId);
        this.onSwitcherListener = onSwitcherListener;
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
     * @param dataSource 设置数据源
     */
    public <T> void setDataSource(List<T> dataSource) {
        if (dataSource == null) return;
        items.clear();
        items.addAll(dataSource);
    }

    protected void setView(@LayoutRes int layoutResId) {
        removeAllViews();
        for (int i = 0; i < 2; i++) {//最多只能添加2个View
            View view = LayoutInflater.from(context).inflate(layoutResId, null);//每次new, 否则报错
            if (layoutParams == null) layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (items.size() > 0 && pos < items.size() && onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, pos, items.get(pos));
                    }
                }
            });
            addView(view, i, layoutParams);
        }
    }

    /**
     * 展示下一个
     *
     */
    public void showNextView() {
        showNext();
        int size = items.size();
        if (size > 0) {
            if (pos == size - 1) {
                pos = 0;
            } else ++ pos;
            if (onSwitcherListener != null) onSwitcherListener.onSwitch(getCurrentView(), pos, items.get(pos));
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

    public interface OnSwitcherListener<T> {
        /**
         * 需要自己实现数据的填充
         * @param view 切换到现在的view
         * @param position 切换到第几条
         * @param data 第position条数据
         */
        void onSwitch(View view, int position, T data);
    }

    /**
     * 设置item点击事件
     */
    public <T> void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {
        /**
         * @param view 切换到现在的view
         * @param position 点击的哪一个item
         * @param data item的值
         */
        void onItemClick(View view, int position, T data);
    }

    //只有2个孩子
    @Override
    public View getChildAt(@IntRange(from = 0, to = 1) int index) {
        return super.getChildAt(index);
    }
}
