package com.actor.myandroidframework.popupwindow;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.CallSuper;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.widget.PopupWindowCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.actor.myandroidframework.action.ActivityAction;
import com.actor.myandroidframework.action.AnimAction;

import java.lang.ref.SoftReference;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/16
 *    desc   : PopupWindow 技术基类
 *
 * <br /> <br />
 * 在轮轮哥的基础上修改而来, 关于3种able的效果整理:
 *
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th>Focusable</th>           <th>Touchable</th>      <th>OutsideTouchable</th>
 *         <th>Pop内容是否能点击</th>    <th>内部点击后Pop是否自动消失</th>
 *         <th>Pop外部是否能点击</th>    <th>外部点击后Pop是否自动消失</th>
 *         <th>点击Pop里点击事件是否穿透</th> <th>点击Pop外点击事件是否穿透</th>
 *         <th>备注</th>
 *     </tr>
 *     <tr>
 *         <td>true</td>                <td>true</td>           <td>true</td>
 *         <td>✔</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td>✘</td>                   <td>✘</td>
 *         <td></td>
 *     </tr>
 *     <tr>
 *         <td>true</td>                <td>true</td>           <td>false</td>
 *         <td>✔</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td>✘</td>                   <td>✘</td>
 *         <td>本类默认配置</td>
 *     </tr>
 *     <tr>
 *         <td>true</td>                <td>false</td>           <td>true</td>
 *         <td>✘</td>                   <td>✔</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td></td>
 *     </tr>
 *     <tr>
 *         <td>true</td>                <td>false</td>           <td>false</td>
 *         <td>✘</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td></td>
 *     </tr>
 *
 *     <tr>
 *         <td>false</td>                <td>true</td>           <td>true</td>
 *         <td>✔</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td>✘</td>                   <td>✔</td>
 *         <td>可做点击穿透1</td>
 *     </tr>
 *     <tr>
 *         <td>false</td>                <td>true</td>           <td>false</td>
 *         <td>✔</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✘</td>
 *         <td>✘</td>                   <td>✔</td>
 *         <td>可做点击穿透2</td>
 *     </tr>
 *     <tr>
 *         <td>false</td>                <td>false</td>           <td>true</td>
 *         <td>✘</td>                   <td>✔</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td></td>
 *     </tr>
 *     <tr>
 *         <td>false</td>                <td>false</td>           <td>false</td>
 *         <td>✘</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✘</td>
 *         <td>✔</td>                   <td>✔</td>
 *         <td></td>
 *     </tr>
 * </table>
 */
public class BasePopupWindow extends PopupWindow
        implements LifecycleOwner, ActivityAction, PopupWindow.OnDismissListener {

    //增加生命周期
    protected final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    /**
     * Activity 对象
     */
    protected Activity mActivity;

    protected Context mContext;
    protected PopupBackground mPopupBackground;

    /**
     * 默认Gravity, {@link PopupWindow#DEFAULT_ANCHORED_GRAVITY}
     */
    protected static final int DEFAULT_ANCHORED_GRAVITY = Gravity.TOP | Gravity.START;

    /**
     * 宽度和高度
     */
    protected int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;


    protected OnShowListener mShowListener;
    protected OnDismissListener mDismissListener;

    public BasePopupWindow(@NonNull Context context) {
        super(context);
        mContext = context;
        mActivity = getActivity();

        //设置背景, 全透明
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(false);

        onCreate(getContext());
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 设置 PopupWindow 内容的背景, 默认有1个黑框
     * @deprecated 不是设置外面空白地方的背景, 所以用户一般不需要调用这个方法
     */
    @Deprecated
    @Override
    public void setBackgroundDrawable(@Nullable Drawable background) {
        super.setBackgroundDrawable(background);
    }

    /**
     * 设置是否能获取焦点, focusable=true时, if {@link #getContentView()} 包含可聚焦View, 那么这个View就获取焦点
     * @param focusable true if the popup should grab focus, false otherwise.
     *
     */
    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
    }

    /**
     * 设置布局
     */
    public void setContentView(@LayoutRes int id) {
        // 这里解释一下，为什么要传 new FrameLayout，因为如果不传的话，XML 的根布局获取到的 LayoutParams 对象会为空，也就会导致宽高解析不出来
        setContentView(LayoutInflater.from(mContext).inflate(id, new FrameLayout(mContext), false));
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        ViewGroup.LayoutParams layoutParams = getContentView().getLayoutParams();
        if (layoutParams != null &&
                mWidth == ViewGroup.LayoutParams.WRAP_CONTENT &&
                mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // 如果当前 PopupWindow 的宽高设置了自适应，就以布局中设置的宽高为主
            setWidth(layoutParams.width);
            setHeight(layoutParams.height);
        }
    }

    /**
     * 设置 ContentView 宽度
     */
    @Override
    public void setWidth(int width) {
        mWidth = width;
        super.setWidth(width);

        ViewGroup.LayoutParams params = getContentView() != null ? getContentView().getLayoutParams() : null;
        if (params != null) {
            params.width = width;
            getContentView().setLayoutParams(params);
        }
    }

    /**
     * 设置 ContentView 高度
     */
    @Override
    public void setHeight(int height) {
        mHeight = height;
        super.setHeight(height);

        // 这里解释一下为什么要重新设置 LayoutParams
        // 因为如果不这样设置的话，第一次显示的时候会按照 PopupWindow 宽高显示
        // 但是 Layout 内容变更之后就不会按照之前的设置宽高来显示
        // 所以这里我们需要对 View 的 LayoutParams 也进行设置
        ViewGroup.LayoutParams params = getContentView() != null ? getContentView().getLayoutParams() : null;
        if (params != null) {
            params.height = height;
            getContentView().setLayoutParams(params);
        }
    }

    /**
     * 设置重心位置
     */
//    public void setGravity(int gravity) {
//        // 适配布局反方向
//        mGravity = Gravity.getAbsoluteGravity(gravity, mContext.getResources().getConfiguration().getLayoutDirection());
////        return (B) this;
//    }

    /**
     * 设置动画，已经封装好几种样式，具体可见{@link AnimAction}类
     */
    @Override
    public void setAnimationStyle(@StyleRes int id) {
        super.setAnimationStyle(id);
    }

    /**
     * 设置一个显示监听器
     *
     * @param listener      监听器对象
     */
    public void setOnShowListener(@Nullable OnShowListener listener) {
        mShowListener = listener;
    }

    /**
     * 设置一个销毁监听器
     *
     * @param listener       销毁监听器对象
     * @deprecated           请使用 {@link #setOnDismissListener(OnDismissListener)}
     */
    @Deprecated
    @Override
    public void setOnDismissListener(@Nullable PopupWindow.OnDismissListener listener) {
        setOnDismissListener(new DismissListenerWrapper(listener));
    }

    /**
     * 设置一个销毁监听器
     *
     * @param listener      监听器对象
     */
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(this);
        mDismissListener = listener;
    }

    public <V extends View> V findViewById(@IdRes int id) {
        return getContentView().findViewById(id);
    }

    /**
     * 设置背景遮盖层的透明度
     * <br />
     * (在这里, 0最暗, 1最亮)
     */
    public void setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float dimAmount) {
        float alpha = 1 - dimAmount;
        if (isShowing()) {
            setActivityAlpha(alpha);
        }
        if (mPopupBackground == null && alpha != 1) {
            mPopupBackground = new PopupBackground();
//            setOnShowListener(mPopupBackground);
//            setOnDismissListener(mPopupBackground);
        }
        if (mPopupBackground != null) {
            mPopupBackground.setAlpha(alpha);
        }
    }

    /**
     * 是否覆盖附着View, 仅对 {@link #showAsDropDown(View) showAsDropDown(...)} 生效
     * @param overlapAnchor Whether the popup should overlap its anchor.
     *
     */
    @Override
    public void setOverlapAnchor(boolean overlapAnchor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setOverlapAnchor(overlapAnchor);
        } else {
            PopupWindowCompat.setOverlapAnchor(this, overlapAnchor);
        }
    }

    /**
     * 设置 Activity 窗口透明度
     */
    protected void setActivityAlpha(float alpha) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        WindowManager.LayoutParams params = activity.getWindow().getAttributes();

        final ValueAnimator animator = ValueAnimator.ofFloat(params.alpha, alpha);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (value != params.alpha) {
                    params.alpha = value;
                    activity.getWindow().setAttributes(params);
                }
            }
        });
        animator.start();
    }

    /**
     * 设置是否可触摸, 用于内容是否可点击等
     */
    @Override
    public void setTouchable(boolean touchable) {
        super.setTouchable(touchable);
    }

    /**
     * 控制是否会通知弹出窗口外的触摸事件。
     * 这只适用于可触摸但不可聚焦的弹出窗口，这意味着窗口外的触摸将传递到后面的窗口。默认为false。
     */
    @Override
    public void setOutsideTouchable(boolean outsideTouchable) {
        super.setOutsideTouchable(outsideTouchable);
    }

    /**
     * 当前 PopupWindow 是否显示了
     */
    @Override
    public boolean isShowing() {
        return super.isShowing();
    }

    /**
     * 销毁当前 PopupWindow
     */
    @Override
    public void dismiss() {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return;
        }
        super.dismiss();
    }




    /**
     * 销毁监听包装类
     */
    private static final class DismissListenerWrapper
            extends SoftReference<PopupWindow.OnDismissListener>
            implements OnDismissListener {

        private DismissListenerWrapper(PopupWindow.OnDismissListener referent) {
            super(referent);
        }

        @Override
        public void onDismiss(BasePopupWindow popupWindow) {
            // 在横竖屏切换后监听对象会为空
            if (get() == null) {
                return;
            }
            get().onDismiss();
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * 显示监听器
     */
    public interface OnShowListener {

        /**
         * PopupWindow 显示了
         */
        void onShow(BasePopupWindow popupWindow);
    }

    /**
     * 销毁监听器
     */
    public interface OnDismissListener {

        /**
         * PopupWindow 销毁了
         */
        void onDismiss(BasePopupWindow popupWindow);
    }

    /**
     * 显示为下拉
     * @param anchor 在 anchor 的下方显示, x&y偏移 = 0
     */
    @Override
    public void showAsDropDown(View anchor) {
//        super.showAsDropDown(anchor);
        showAsDropDown(anchor, 0, 0);
    }

    /**
     * @param anchor 在 anchor 的下方显示
     * @param xoff PopupWindow 左上角 x轴偏移量, 可为负数
     * @param yoff PopupWindow 左上角 y轴偏移量, 可为负数
     */
    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
//        super.showAsDropDown(anchor, xoff, yoff);
        showAsDropDown(anchor, xoff, yoff, DEFAULT_ANCHORED_GRAVITY);
    }

    /**
     * @param anchor 在 anchor 的下方显示
     * @param xoff x轴偏移量
     * @param yoff y轴偏移量
     * @param gravity PopupWindow 相对 anchor 位置
     *                @see Gravity#TOP
     *                @see Gravity#START 开始位置
     *                @see Gravity#END 结束位置
     */
    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return;
        }
        if (isShowing() || getContentView() == null) {
            return;
        }

        if (mShowListener != null) {
            mShowListener.onShow(this);
        }
        if (mPopupBackground != null) mPopupBackground.onShow(this);

        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    /**
     * 显示在指定位置
     * @param parent 任意view, 用于获取WindowToken
     * @param gravity 重心位置 相对于整个屏幕的重心(不是相对于view), 例: {@link Gravity#CENTER} 屏幕中心
     * @param x 基于重心位置的x偏移
     * @param y 基于重心位置的y偏移
     */
    @Override
    public void showAtLocation(@NonNull View parent, int gravity, int x, int y) {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return;
        }
        if (isShowing() || getContentView() == null) {
            return;
        }

        if (mShowListener != null) {
            mShowListener.onShow(this);
        }
        if (mPopupBackground != null) mPopupBackground.onShow(this);

        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        super.showAtLocation(parent, gravity, x, y);
    }



    @Override
    public void setWindowLayoutType(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setWindowLayoutType(type);
        } else {
            PopupWindowCompat.setWindowLayoutType(this, type);
        }
    }

    @Override
    public int getWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getWindowLayoutType();
        } else {
            return PopupWindowCompat.getWindowLayoutType(this);
        }
    }

    /**
     * if子类重写此方法, 可以初始化View等
     */
    @CallSuper
    protected void onCreate(Context context) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    /**
     * {@link PopupWindow.OnDismissListener}
     * final: 禁止重写此方法,  防止递归调用
     */
    @Override
    public final void onDismiss() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(this);
        }
        if (mPopupBackground != null) mPopupBackground.onDismiss(this);
    }
}