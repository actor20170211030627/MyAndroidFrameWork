package com.actor.myandroidframework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.FloatRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.action.ActivityAction;
import com.actor.myandroidframework.action.AnimAction;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;

/**
 * Description: Dialog基类, 各Dialog类型:
 * <ol>
 *     <li>{@link Dialog}</li>
 *     <li>{@link android.app.AlertDialog} extends Dialog: setIcon, title, message, button x 3, setView, setContentView</li>
 *     <li>{@link androidx.appcompat.app.AppCompatDialog} extends Dialog</li>
 *     <li>{@link androidx.appcompat.app.AlertDialog} extends AppCompatDialog: setIcon, title, message, button x 3, setView, setContentView</li>
 * </ol>
 * {@link null 注意:} 如果'背景使用的shape' & 'shape下方有圆角' & '下方圆角位置的view有背景色',
 *     有可能会造成 '下方圆角被颜色覆盖' 的问题! 解决方法: <br />
 *     1. shape 加上 padding(bottom) 属性 <br />
 *     2. 下方圆角位置的view 加一个同样圆角的 shape <br />
 * <br />
 * @Author     : ldf
 * @Date       : 2020-1-21 on 16:49
 */
public abstract class BaseDialog extends Dialog implements ActivityAction, LifecycleOwner,
        DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener {

    //增加生命周期
    protected final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);
    protected OnActionErrorListener onShowErrorListener;  //show()的时候, 出错回调
    protected OnActionErrorListener onDismissErrorListener;  //dismiss()的时候, 出错回调
    protected OnShowListener onShowListener;
    protected OnDismissListener onDismissListener;

    //Widow宽度
    protected int windowWidth = WindowManager.LayoutParams.MATCH_PARENT;
    protected int windowHeight = WindowManager.LayoutParams.WRAP_CONTENT;
    //onCreate的时候, 是否打印这个Dialog的名称
    protected boolean isPrintNameOnCreate = true;

    public BaseDialog(@NonNull Context context) {
        //给dialog设置样式, 去掉标题栏, 宽度全屏
        super(context, R.style.BaseDialogTheme);
        init();
    }

    /**
     * @param themeResId 自定义样式
     */
    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }



    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
        super(context, R.style.BaseDialogTheme);    //确保有个样式
        setCancelAble(cancelable);
        setOnCancelListener(cancelListener);
        init();
    }

    protected void init() {
//        Window window = getWindow();//获取当前dialog所在的窗口对象
        int layoutResId = getLayoutResId();
        if (layoutResId != Resources.ID_NULL) setContentView(layoutResId);

//        findViewById();//子类可以初始化控件
    }

    /**
     * 设置你自定义Dialog的layout, 如果不想设置, 可返回0
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    //只会创建一次
    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isPrintNameOnCreate) LogUtils.error(this.getClass().getName());
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes(); //获取当前窗口的属性, 布局参数
            params.width = windowWidth;              //设置宽度, 默认全屏
            params.height = windowHeight;            //设置高度, 默认包裹内容
            params.x = 0;
            params.y = 0;//相对上方的偏移,负值忽略.
//            params.dimAmount = dimAmount;
//            int windowAnimations = params.windowAnimations;
//            window.setAttributes(params);

            //FLAG_BLUR_BEHIND模糊(毛玻璃效果)
//            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        super.setOnShowListener(this);
        super.setOnDismissListener(this);

//        findViewById();//子类可以初始化控件等
    }

    /**
     * 设置宽度 <br />
     * {@link null 注意:} if宽度&高度都设置MATCH_PARENT, 会自动全屏(包括状态栏), 非常无语...
     *                    解决方法: 宽度-1px
     */
    public BaseDialog setWidth(@Px int width) {
        this.windowWidth = width;
        return this;
    }

    /**
     * 设置宽度百分比
     * @param widthPercent 宽度百分比
     * @deprecated 使用 {@link #setWidthPercent(float, int)}
     */
    @Deprecated
    public BaseDialog setWidthPercent(@FloatRange(from = 0f, to = 1f, fromInclusive = false) float widthPercent) {
        return setWidth((int) (ScreenUtils.getAppScreenWidth() * widthPercent));
    }

    /**
     * 设置宽度百分比
     * @param widthPercent 宽度百分比
     * @param maxWidth 最大宽度(像素px) (∵有些lj平板竖屏的时候, 获取的宽度实际是高度, 导致Dialog超宽..., 所以建议设置最大宽度)
     */
    public BaseDialog setWidthPercent(@FloatRange(from = 0f, to = 1f, fromInclusive = false) float widthPercent, @Px int maxWidth) {
        return setWidth(Math.min((int) (ScreenUtils.getAppScreenWidth() * widthPercent), maxWidth));
    }

    /**
     * 设置高度 <br />
     * {@link null 注意:} if宽度&高度都设置MATCH_PARENT, 会自动全屏(包括状态栏), 非常无语...
     *                    解决方法: 宽度-1px
     */
    public BaseDialog setHeight(@Px int height) {
        this.windowHeight = height;
        return this;
    }

    /**
     * 设置状态栏透明(Dialog能绘制进状态栏) & 隐藏导航栏
     */
    public BaseDialog setStatusBarTransparent() {
        Window window = getWindow();
        if (window == null) return this;
        //TODO: 下面这句代码实际效果使状态栏透明, 而不是彻底隐藏状态栏...
        BarUtils.setStatusBarVisibility(window, false);
        //隐藏导航栏
        BarUtils.setNavBarVisibility(window, false);

        /**
         * 设置后, Dialog能绘制进状态栏了, 但是状态栏是透明的, 实际上还是存在能够看见的
         */
//        //允许绘制系统状态栏背景
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        //将状态栏设为透明，避免遮挡内容
//        window.setStatusBarColor(Color.TRANSPARENT);
//        // 允许内容延伸到状态栏区域（API 21+）
//        View decorView = window.getDecorView();
//        //                                                                  允许内容布局延伸到状态栏下方
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        //Android11, API 30+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//          //禁用系统窗口适配，确保内容全屏
//          window.setDecorFitsSystemWindows(false);
//        }
        return this;
    }

    /**
     * 隐藏状态栏 & 导航栏
     */
    public BaseDialog setStatusBarAndNavigationHide() {
        Window window = getWindow();
        if (window == null) return this;
            // 隐藏状态栏和导航栏的关键标志
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //允许内容布局延伸到状态栏下方
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // 沉浸式模式
            View decorView = window.getDecorView();
//            int systemUiVisibility = decorView.getSystemUiVisibility(); //0
//            LogUtils.errorFormat("systemUiVisibility=%d", systemUiVisibility);
            decorView.setSystemUiVisibility(uiOptions);

            // 适配 Android 11+ 的弹窗行为（Android11, API 30+）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //禁用系统窗口适配，确保内容全屏
                window.setDecorFitsSystemWindows(false);
                WindowInsetsController controller = decorView.getWindowInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            }
        return this;
    }

    /**
     * 设置点击返回键 & 外部, 是否能取消dialog
     * //如果 setCancelable = true, setCanceledOnTouchOutside = true/false, 设置都有效
     * //如果 setCancelable = false, setCanceledOnTouchOutside = true, 点击 '返回'&'外部' 都能取消!!!
     */
    public BaseDialog setCancelAble(boolean cancelAble) {
        setCancelable(cancelAble);              //点击返回键 是否能取消
        setCanceledOnTouchOutside(cancelAble);  //点击外部 是否能取消
        return this;
    }

    /**
     * 设置重心 & 动画
     * @param gravity 重心: <br />
     *        &emsp;&emsp; {@link Gravity#CENTER}(默认), {@link Gravity#LEFT}, {@link Gravity#TOP}, {@link Gravity#RIGHT}, {@link Gravity#BOTTOM} <br />
     *        &emsp;&emsp; {@link GravityCompat#START}, {@link GravityCompat#END}
     * @param windowAnimations Dialog显示/隐藏 的动画: <br />
     *        <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *             <tr>
     *                         <th align="center">动画</th>
     *                         <th align="center">说明</th>
     *             </tr>
     *             <tr> <td>{@link AnimAction#ANIM_DEFAULT}</td> <td>使用系统默认Dialog动画</td> </tr>
     *             <tr> <td>{@link AnimAction#ANIM_EMPTY}</td> <td>没有动画效果</td> </tr>
     *             <tr>
     *                 <td>{@link AnimAction}</td>
     *                 <td>更多动画见 AnimAction</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#YourCustomAnim R.style.YourCustomAnim}</td>
     *                 <td>也阔以自定义动画</td>
     *             </tr>
     *        </table>
     */
    public BaseDialog setGravityAndAnimation(int gravity, @StyleRes int windowAnimations) {
        Window window = getWindow();
        if (window == null) return this;
        window.setGravity(gravity);
        window.setWindowAnimations(windowAnimations);
        return this;
    }

    /**
     * 设置窗口后面的暗淡程度[0-1], 0最亮, 默认=0.6
     * @param dimAmount 昏暗的数量
     */
    public BaseDialog setDimAmount(@FloatRange(from = 0.0f, to = 1.0f) float dimAmount) {
        Window window = getWindow();
        if (window != null) window.setDimAmount(dimAmount);
        return this;
    }

    /**
     * Dialog弹起后, 状态栏是否变暗
     * @param isStatusBarDimmed 是否变暗, 默认=true <br />
     *                         if=true, {@link #setDimAmount(float)}才有效。<br />
     *                          if=false, {@link #setDimAmount(float)}无效, 背景会全亮
     */
    public BaseDialog isStatusBarDimmed(boolean isStatusBarDimmed) {
        Window window = getWindow();
        if (window == null) return this;
        if (isStatusBarDimmed) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return this;
    }

    /**
     * 点击弹窗外部时, 是否将点击事件透传到弹窗下，默认是false
     */
    public BaseDialog isClickThrough(boolean isClickThrough) {
        Window window = getWindow();
        if (window == null) return this;
        if (isClickThrough) {
            //将允许对话框外的事件被发送到后面的视图
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
//            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            /**
             * 允许对话框在被触摸时接收到外部的触摸事件, 示例代码:
             * window.getDecorView().setOnTouchListener((v, event) -> {
             *     if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
             *     }
             *     return false;
             * });
             */
            window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        }
        return this;
    }

    /**
     * show()的时候报错监听
     */
    public BaseDialog setOnShowErrorListener(OnActionErrorListener onShowErrorListener) {
        this.onShowErrorListener = onShowErrorListener;
        return this;
    }

    /**
     * dismiss()的时候报错监听
     */
    public BaseDialog setOnDismissErrorListener(OnActionErrorListener onDismissErrorListener) {
        this.onDismissErrorListener = onDismissErrorListener;
        return this;
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            if (onShowErrorListener == null) {
                e.printStackTrace();
            } else {
                onShowErrorListener.onActionError(e);
            }
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            if (onDismissErrorListener == null) {
                e.printStackTrace();
            } else {
                onDismissErrorListener.onActionError(e);
            }
        }
    }

    //每次show的时候都会调用
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    //每次dismiss的时候都会调用
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }



    ///////////////////////////////////////////////////////////////////////////
    // 重写监听, 用于回调生命周期
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
//        super.setOnShowListener(this);
        onShowListener = listener;
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
//        super.setOnDismissListener(listener);
        onDismissListener = listener;
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @CallSuper
    @Override
    protected void onStop() {
        super.onStop();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    /**
     * 如果子类重写了本方法, 务必调用super.onShow(dialog); 因为会调生命周期. <br />
     * final: {@link #setOnShowListener(OnShowListener)} 的时候请传入匿名内部类, 不要重写此方法.
     *        因为子类调用super的时候, 这儿又会调到子类去, 导致递归栈溢出!
     */
//    @CallSuper
    @Override
    public final void onShow(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        if (onShowListener != null) onShowListener.onShow(dialog);
    }

    /**
     * 如果子类重写了本方法, 务必调用super.onDismiss(dialog); 因为会调生命周期. <br />
     * final: {@link #setOnDismissListener(OnDismissListener)} 的时候请传入匿名内部类, 不要重写此方法.
     *        因为子类调用super的时候, 这儿又会调到子类去, 导致递归栈溢出!
     */
//    @CallSuper
    @Override
    public final void onDismiss(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        if (onDismissListener != null) onDismissListener.onDismiss(dialog);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }
}
