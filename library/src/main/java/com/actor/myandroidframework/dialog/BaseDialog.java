package com.actor.myandroidframework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
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
import com.actor.myandroidframework.utils.WindowUtils;
import com.blankj.utilcode.util.ScreenUtils;

/**
 * Description: Dialog 封装 <br />
 * 各Dialog类型:
 * <ol>
 *     <li>{@link Dialog}</li>
 *     <li>{@link android.app.AlertDialog} extends Dialog: setIcon, title, message, button x 3, setView, setContentView</li>
 *     <li>{@link androidx.appcompat.app.AppCompatDialog} extends Dialog</li>
 *     <li>{@link androidx.appcompat.app.AlertDialog} extends AppCompatDialog: setIcon, title, message, button x 3, setView, setContentView</li>
 * </ol>
 *
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

    //按返回键的时候, 是否让Dialog cancel
    protected boolean mCancelableOnBackPressed = true;
    protected boolean mCancelableOnTouchOutside = true;
    //Widow宽度
    protected int windowWidth = WindowManager.LayoutParams.MATCH_PARENT;
    protected int windowHeight = WindowManager.LayoutParams.WRAP_CONTENT;
    //窗口偏移
    protected int xOffset = 0, yOffset = 0;
    //StatusBar & NavigationBar
    protected boolean isDrawIntoStatusBar = false, isHideStatusBar = false, isDrawIntoNavigationBar = false, isHideNavigationBar = false;
    //onCreate的时候, 是否打印这个Dialog的名称
    protected boolean loggable = true;

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
        if (loggable) LogUtils.error(this.getClass().getName());
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes(); //获取当前窗口的属性, 布局参数
            params.width = windowWidth;              //设置宽度, 默认全屏
            params.height = windowHeight;            //设置高度, 默认包裹内容
            params.x = xOffset;
            params.y = yOffset;//相对上方的偏移,负值忽略.

            //FLAG_BLUR_BEHIND模糊(毛玻璃效果)
//            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

            //当 width = height = match_parent 的时候:
            //  1.if <item name="android:windowFullscreen">true</item>, 1.隐藏顶部状态栏, 并填充进去 2.不隐藏底部状态栏, 并填充进去 (导致布局底部被导航栏遮挡)
            //  2.if <item name="android:windowFullscreen">false</item>, 1.顶部状态栏不会被隐藏      2.顶部状态栏&底部导航栏会被系统绘制成黑色...
            // 所以让宽度不要 = match_parent(-1) 或 边距+1
            if (windowWidth == WindowManager.LayoutParams.MATCH_PARENT && windowHeight == WindowManager.LayoutParams.MATCH_PARENT) {
//                params.width = ScreenUtils.getAppScreenWidth();   //设置固定宽度不好, 防止: 万一旋转屏幕 or app宽度发生了改变
                if (xOffset <= 0) {
                    xOffset = 1;
                    params.x = 1;
                }
            }
            // 监听布局变化（旋转屏幕会触发）
//            window.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                    LogUtils.errorFormat("v = %s, left = %d, top = %d, right = %d, bottom = %d", v, left, top, right, bottom);
//                    LogUtils.errorFormat("oldLeft = %d, oldTop = %d, oldRight = %d, oldBottom = %d", oldLeft, oldTop, oldRight, oldBottom);
//                    LogUtils.errorFormat("newWidth = %d, oldWidth = %d", right - left, oldRight - oldLeft);
//                }
//            });
        }

        super.setOnShowListener(this);
        super.setOnDismissListener(this);
        applyDrawIntoStatusBarNavigationBar();
//        findViewById();//子类可以初始化控件等
    }

    /**
     * 设置宽度
     * @param width {@link WindowManager.LayoutParams#MATCH_PARENT} or {@link WindowManager.LayoutParams#WRAP_CONTENT} or 具体宽度
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
     * 设置高度
     * @param height {@link WindowManager.LayoutParams#MATCH_PARENT} or {@link WindowManager.LayoutParams#WRAP_CONTENT} or 具体高度
     */
    public BaseDialog setHeight(@Px int height) {
        this.windowHeight = height;
        return this;
    }

    /**
     * <ul>
     *     <li>{@link #setCancelable(boolean)}: '点击Dialog外部' or '按返回键' 是否让Dialog cancel</li>
     *     <li>{@link #setCanceledOnTouchOutside(boolean)}: '点击Dialog外部' 是否让Dialog cancel</li>
     * </ul>
     * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *     <tr>
     *          <th>№</th>
     *          <th>先设置这个方法</th>
     *          <th>再设置这个方法</th>
     *          <th align="center" nowrap="nowrap">按返回键<br />是否能dismiss</th>
     *          <th>点击Dialog外部是否能dismiss</th>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>setCancelable(<b>true</b>);</td>
     *         <td>setCanceledOnTouchOutside(<b>true</b>);</td>
     *         <td align="center">✔</td>
     *         <td>✔</td>
     *     </tr>
     *     <tr>
     *         <td>2</td>
     *         <td>setCancelable(<b>true</b>);</td>
     *         <td nowrap="nowrap">setCanceledOnTouchOutside(<b>false</b>);</td>
     *         <td align="center">✔</td>
     *         <td>✘</td>
     *     </tr>
     *     <tr>
     *         <td>3</td>
     *         <td nowrap="nowrap">setCancelable(<b>false</b>);</td>
     *         <td>setCanceledOnTouchOutside(<b>true</b>);</td>
     *         <td align="center">{@link null <b>✔</b>}</td>
     *         <td>✔</td>
     *     </tr>
     *     <tr>
     *         <td>4</td>
     *         <td>setCancelable(<b>false</b>);</td>
     *         <td>setCanceledOnTouchOutside(<b>false</b>);</td>
     *         <td align="center">✘</td>
     *         <td>✘</td>
     *     </tr>
     *     <tr></tr>
     *     <tr>
     *         <td>5</td>
     *         <td>setCanceledOnTouchOutside(<b>true</b>);</td>
     *         <td>setCancelable(<b>true</b>);</td>
     *         <td align="center">✔</td>
     *         <td>✔</td>
     *     </tr>
     *     <tr>
     *         <td>6</td>
     *         <td>setCanceledOnTouchOutside(<b>true</b>);</td>
     *         <td>setCancelable(<b>false</b>);</td>
     *         <td align="center">✘</td>
     *         <td>{@link null <b>✘</b>}</td>
     *     </tr>
     *     <tr>
     *         <td>7</td>
     *         <td nowrap="nowrap">setCanceledOnTouchOutside(<b>false</b>);</td>
     *         <td nowrap="nowrap">setCancelable(<b>true</b>);</td>
     *         <td align="center">✔</td>
     *         <td>✘</td>
     *     </tr>
     *     <tr>
     *         <td>8</td>
     *         <td>setCanceledOnTouchOutside(<b>false</b>);</td>
     *         <td>setCancelable(<b>false</b>);</td>
     *         <td align="center">✘</td>
     *         <td>✘</td>
     *     </tr>
     * </table>
     */
    public BaseDialog setCancelAble(boolean cancelAble) {
        setCancelAble(cancelAble, cancelAble);
        return this;
    }

    public BaseDialog setCancelAble(boolean cancelableOnBackPressed, boolean cancelableOnTouchOutside) {
        this.mCancelableOnBackPressed = cancelableOnBackPressed;
//        setCancelable(cancelAble);
        setCanceledOnTouchOutside(cancelableOnTouchOutside);
        return this;
    }

    /**
     * 设置 '点击Dialog外部' or '按返回键' 是否让Dialog cancel
     * @deprecated 不要直接调用这个方法, 应该去调用{@link #setCancelAble(boolean)} or {@link #setCancelAble(boolean, boolean)}
     */
    @Deprecated
    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        this.mCancelableOnBackPressed = flag;
    }

    /**
     * 设置 '点击Dialog外部' 是否让Dialog cancel
     * @deprecated 不要直接调用这个方法, 应该去调用{@link #setCancelAble(boolean)} or {@link #setCancelAble(boolean, boolean)}
     */
    @Deprecated
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        this.mCancelableOnTouchOutside = cancel;
    }

    /**
     * 设置重心 & 动画
     * @param gravity 重心: <br />
     *        &emsp;&emsp; {@link Gravity#CENTER}(默认), {@link Gravity#LEFT}, {@link Gravity#TOP}, {@link Gravity#RIGHT}, {@link Gravity#BOTTOM} <br />
     *        &emsp;&emsp; {@link GravityCompat#START}, {@link GravityCompat#END}
     * @param windowAnimations Dialog显示/隐藏 的动画: <br />
     *        <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *            <tr>
     *                <th align="center">动画</th>
     *                <th align="center">说明</th>
     *            </tr>
     *            <tr>
     *                <td>{@link AnimAction#ANIM_DEFAULT}</td>
     *                <td>使用系统默认Dialog动画</td>
     *            </tr>
     *            <tr>
     *                <td>{@link AnimAction#ANIM_EMPTY}</td>
     *                <td>没有动画效果</td>
     *            </tr>
     *            <tr>
     *                <td>{@link AnimAction}</td>
     *                <td>更多动画见 AnimAction</td>
     *            </tr>
     *            <tr>
     *                <td>{@link R.style#YourCustomAnim R.style.YourCustomAnim}</td>
     *                <td>也阔以自定义动画</td>
     *            </tr>
     *        </table>
     */
    public BaseDialog setGravityAndAnimation(int gravity, @StyleRes int windowAnimations) {
        Window window = getWindow();
        WindowUtils.setGravity(window, gravity);
        WindowUtils.setWindowAnimations(window, windowAnimations);
        return this;
    }

    /**
     * Dialog弹起后, 在当前窗口（Dialog）后面的所有内容上，覆盖一层半透明的黑色遮罩（调光效果）。
     * @param isDimEnable 是否变暗, 默认=true <br />
     *                    if=true, {@link #setDimAmount(float)}才有效。<br />
     *                    if=false, {@link #setDimAmount(float)}无效, 背景会全亮
     */
    public BaseDialog setDimEnable(boolean isDimEnable) {
        WindowUtils.setDimEnable(getWindow(), isDimEnable);
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
     * 设置点击穿透, 点击Dialog外部时, 是否将点击事件透传到Dialog的Window后面，默认是false
     * @param isClickThrough 是否点击穿透
     */
    public BaseDialog setClickThrough(boolean isClickThrough) {
        WindowUtils.setClickThrough(getWindow(), isClickThrough);
        return this;
    }

    /**
     * 设置窗口x偏移量
     * @param xOffset x方向偏移量
     *        <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *            <tr>
     *                <th align="center">Gravity</th>
     *                <th align="center">xOffset 的作用</th>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#LEFT}</td>
     *                <td>窗口<b>左边</b>相对于<b>屏幕左边</b>的距离</td>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#TOP}</td>
     *                <td>窗口<b>左边</b>相对于<b>屏幕左边</b>的距离</td>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#RIGHT}</td>
     *                <td>窗口<b>右边</b>相对于<b>屏幕右边</b>的距离</td>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#BOTTOM}</td>
     *                <td>窗口<b>左边</b>相对于<b>屏幕左边</b>的距离</td>
     *            </tr>
     *        </table>
     */
    public BaseDialog setXOffset(int xOffset) {
        this.xOffset = xOffset;
        return this;
    }

    /**
     * 设置窗口y偏移量
     * @param yOffset y方向偏移量
     *        <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *            <tr>
     *                <th align="center">Gravity</th>
     *                <th align="center">yOffset 的作用</th>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#LEFT}</td>
     *                <td>窗口<b>垂直中心</b>相对于<b>屏幕垂直中心</b>的y方向距离</td>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#TOP}</td>
     *                <td>窗口<b>顶边</b>相对于<b>屏幕顶边</b>的距离</td>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#RIGHT}</td>
     *                <td>窗口<b>垂直中心</b>相对于<b>屏幕垂直中心</b>的y方向距离</td>
     *            </tr>
     *            <tr>
     *                <td>{@link Gravity#BOTTOM}</td>
     *                <td>窗口<b>底边</b>相对于<b>屏幕底边</b>的距离</td>
     *            </tr>
     *        </table>
     */
    public BaseDialog setYOffset(int yOffset) {
        this.yOffset = yOffset;
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

    public BaseDialog setDrawIntoStatusBar(boolean isDrawIntoStatusBar, boolean isHideStatusBar) {
        this.isDrawIntoStatusBar = isDrawIntoStatusBar;
        this.isHideStatusBar = isHideStatusBar;
        return this;
    }

    public BaseDialog setDrawIntoNavigationBar(boolean isDrawIntoNavigationBar, boolean isHideNavigationBar) {
        this.isDrawIntoNavigationBar = isDrawIntoNavigationBar;
        this.isHideNavigationBar = isHideNavigationBar;
        return this;
    }

    protected boolean applyDrawIntoStatusBarNavigationBar() {
        if (loggable) {
            LogUtils.errorFormat("isDrawIntoStatusBar = %b;\nisHideStatusBar = %b;\nisDrawIntoNavigationBar = %b;\nisHideNavigationBar = %b",
                    isDrawIntoStatusBar, isHideStatusBar, isDrawIntoNavigationBar, isHideNavigationBar);
        }
        if (!isDrawIntoStatusBar && !isDrawIntoNavigationBar) return false;
        return WindowUtils.drawIntoStatusBarAndNavigationBar(getWindow(), isDrawIntoStatusBar, isHideStatusBar, isDrawIntoNavigationBar, isHideNavigationBar, yOffset);
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (mCancelableOnTouchOutside) dismiss();
            return true;
        }
        return super.onTouchEvent(event);
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

    @Override
    public void onBackPressed() {
        if (mCancelableOnBackPressed) super.onBackPressed();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }
}
