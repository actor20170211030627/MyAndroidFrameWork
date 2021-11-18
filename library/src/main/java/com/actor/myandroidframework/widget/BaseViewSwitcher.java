package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.actor.myandroidframework.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: View切换, 水平切换效果 & 垂直切换效果
 * Author     : ldf
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
 * @BindView(R.id.view_switcher)//消息轮播
 * BaseViewSwitcher<T> viewSwitcher;
 *
 * List<T> datas = new ArrayList<>();//任意数据类型
 * baseViewSwitcher.init(R.layout.item_base_view_switcher, new BaseViewSwitcher.OnSwitcherListener<T>() {
 *     @Override
 *     public void onSwitch(View view, int position, T item) {
 *         TextView textView = view.findViewById(R.id.tv);//找到你自己需要填充数据的view
 *         textView.setText(item);
 *         logFormat("onSwitch: view=%s, pos=%d, item=%s", view, position, item);
 *     }
 * });
 * baseViewSwitcher.setOnItemClickListener(new BaseViewSwitcher.OnItemClickListener<T>() {
 *     @Override
 *     public void onItemClick(View view, int position, T item) {
 *         logFormat("pos=%d, str=%s", position, item);
 *     }
 * });
 * baseViewSwitcher.setDataSource(datas);
 *
 * @version 1.0
 */
public class BaseViewSwitcher<T> extends ViewSwitcher implements ViewSwitcher.ViewFactory {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    protected List<T>  itemsForViewSwitcher   = new ArrayList<>();
    protected Handler  handlerForViewSwitcher = new Handler();
    protected int      posForViewSwitcher     = 0;
    protected Runnable runnableForViewSwitcher;
    protected int                    switchIntervalMsForViewSwitcher = 3_000;//动画切换间隔
    protected int                    orientationForViewSwitcher      = VERTICAL;
    protected OnItemClickListener<T> onItemClickListenerForViewSwitcher;//item点击事件
    protected OnSwitcherListener<T>  onSwitcherListenerForViewSwitcher;//切换监听
    protected int          layoutResIdForViewSwitcher;
    protected LayoutParams layoutParamsForViewSwitcher;

    public BaseViewSwitcher(Context context) {
        this(context, null);
    }

    public BaseViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseViewSwitcher);
            int interval = typedArray.getInt(R.styleable.BaseViewSwitcher_bvsSwitchIntervalMs, -1);
            orientationForViewSwitcher = typedArray.getInt(R.styleable.BaseViewSwitcher_bvsOrientation, VERTICAL);
            if (interval >= 100) switchIntervalMsForViewSwitcher = interval;
            typedArray.recycle();
        }

        runnableForViewSwitcher = new Runnable() {
            @Override
            public void run() {
                handlerForViewSwitcher.removeCallbacks(runnableForViewSwitcher);
                handlerForViewSwitcher.postDelayed(runnableForViewSwitcher, switchIntervalMsForViewSwitcher);
                showNextView();
            }
        };
    }

    /**
     * 初始化, 设置View & 切换监听, 默认垂直滚动
     * @param layoutResId view的布局id
     * @param onSwitcherListener 切换监听
     */
    public void init(@LayoutRes int layoutResId, @NonNull OnSwitcherListener<T> onSwitcherListener) {
        init(layoutResId, orientationForViewSwitcher, onSwitcherListener);
    }

    /**
     * 初始化, 设置View & 切换监听
     * @param layoutResId 布局id
     * @param orientation 切换方向
     * @param onSwitcherListener 切换监听
     */
    public void init(@LayoutRes int layoutResId, @OrientationMode int orientation, @NonNull OnSwitcherListener<T> onSwitcherListener) {
        this.layoutResIdForViewSwitcher = layoutResId;
        setFactory(this);
        this.onSwitcherListenerForViewSwitcher = onSwitcherListener;
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

    @Override
    public View makeView() {
        View view = LayoutInflater.from(getContext()).inflate(layoutResIdForViewSwitcher, null);//每次new, 否则报错
        if (layoutParamsForViewSwitcher == null) layoutParamsForViewSwitcher = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParamsForViewSwitcher);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemsForViewSwitcher.size() > 0 && posForViewSwitcher < itemsForViewSwitcher.size() && onItemClickListenerForViewSwitcher != null) {
                    onItemClickListenerForViewSwitcher.onItemClick(v, posForViewSwitcher, itemsForViewSwitcher.get(posForViewSwitcher));
                }
            }
        });
        return view;
    }

    /**
     * @param dataSource 设置数据源
     */
    public void setDataSource(List<T> dataSource) {
        if (dataSource == null) return;
        itemsForViewSwitcher.clear();
        itemsForViewSwitcher.addAll(dataSource);
    }

    /**
     * 展示下一个
     *
     */
    public void showNextView() {
        showNext();
        int size = itemsForViewSwitcher.size();
        if (size > 0) {
            if (posForViewSwitcher == size - 1) {
                posForViewSwitcher = 0;
            } else ++posForViewSwitcher;
            if (onSwitcherListenerForViewSwitcher != null) onSwitcherListenerForViewSwitcher.onSwitch(getCurrentView(), posForViewSwitcher, itemsForViewSwitcher.get(posForViewSwitcher));
        }
    }

    /**
     * 开始切换, 一般在Activity/Fragment的 onStart() 中调用
     * 也可以不调用这个方法, 自己定时调用showNextView(), 比如和轮播图同步展示时
     */
    public void startSwitch() {
        handlerForViewSwitcher.removeCallbacks(runnableForViewSwitcher);
        handlerForViewSwitcher.postDelayed(runnableForViewSwitcher, switchIntervalMsForViewSwitcher);
    }

    /**
     * 停止切换, 一般在Activity/Fragment的 onStop() 中调用
     */
    public void stopSwitch() {
        handlerForViewSwitcher.removeCallbacks(runnableForViewSwitcher);
        handlerForViewSwitcher.removeCallbacksAndMessages(null);
    }

    public interface OnSwitcherListener<T> {
        /**
         * 需要自己实现数据的填充
         * @param view 切换到现在的view
         * @param position 切换到第几条
         * @param item 第position条数据
         */
        void onSwitch(View view, int position, T item);
    }

    /**
     * 设置item点击事件
     */
    public void setOnItemClickListenerForViewSwitcher(OnItemClickListener<T> onItemClickListenerForViewSwitcher) {
        this.onItemClickListenerForViewSwitcher = onItemClickListenerForViewSwitcher;
    }

    public interface OnItemClickListener<T> {
        /**
         * @param view 切换到现在的view
         * @param position 点击的哪一个item
         * @param item item的值
         */
        void onItemClick(View view, int position, T item);
    }

    //只有2个孩子
    @Override
    public View getChildAt(@IntRange(from = 0, to = 1) int index) {
        return super.getChildAt(index);
    }
}
