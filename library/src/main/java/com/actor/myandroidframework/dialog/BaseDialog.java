package com.actor.myandroidframework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.FloatRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public boolean isDismissError = false;  //dismiss的时候, 是否出错了
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
    public BaseDialog setWidth(int width) {
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
    public BaseDialog setWidthPercent(@FloatRange(from = 0f, to = 1f, fromInclusive = false) float widthPercent, int maxWidth) {
        return setWidth(Math.min((int) (ScreenUtils.getAppScreenWidth() * widthPercent), maxWidth));
    }

    /**
     * 设置高度 <br />
     * {@link null 注意:} if宽度&高度都设置MATCH_PARENT, 会自动全屏(包括状态栏), 非常无语...
     *                    解决方法: 宽度-1px
     */
    public BaseDialog setHeight(int height) {
        this.windowHeight = height;
        return this;
    }

    /**
     * 设置高度是否全屏, 包括状态栏
     * @param isFullScreen 高度是否全屏
     * @param isIncludeStatusBar if高度全屏, 是否包含状态栏
     */
    public BaseDialog setHeightFullScreen(boolean isFullScreen, boolean isIncludeStatusBar) {
        Window window = getWindow();
        if (window != null) {
            BarUtils.setNavBarVisibility(window, !(isFullScreen && isIncludeStatusBar));
        }
        //高度全屏(if没有↑, 还有状态栏会显示)
        return setHeight(isFullScreen ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT);
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

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            isDismissError = false;
        } catch (Exception e) {
            isDismissError = true;
            e.printStackTrace();
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
