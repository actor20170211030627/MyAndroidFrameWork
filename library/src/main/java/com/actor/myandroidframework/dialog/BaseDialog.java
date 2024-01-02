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
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.BarUtils;

/**
 * Description: Dialog基类 <br />
 *      注意: 如果'背景使用的shape' & 'shape下方有圆角' & '下方圆角位置的view有背景色',
 *          有可能会造成 '下方圆角被颜色覆盖' 的问题! 解决方法: <br />
 *          1. shape 加上 padding(bottom) 属性 <br />
 *          2. 下方圆角位置的view 加一个同样圆角的 shape <br />
 * Author     : ldf
 * Date       : 2020-1-21 on 16:49
 */
public abstract class BaseDialog extends Dialog implements LifecycleOwner,
        DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener {

    protected Window window;
    protected float  dimAmount = 0.6F;//背景灰度[0, 1], 默认=0.6

    //增加生命周期
    protected final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);
    protected OnShowListener onShowListener;
    protected OnDismissListener onDismissListener;

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
        window = getWindow();//获取当前dialog所在的窗口对象

        //可设置Dialog所在Window的进入&退出动画
//        window.setWindowAnimations(R.style.dialog_bottom_in_bottom_out);

        int layoutResId = getLayoutResId();
        if (layoutResId != Resources.ID_NULL) setContentView(layoutResId);

//        findViewById();//可以初始化控件等
    }

    /**
     * 设置你自定义Dialog的layout, 如果不想设置, 可返回0
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    //只会创建一次
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.error("onCreate");
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();//获取当前窗口的属性, 布局参数
            if (params != null) {
                params.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度全屏
                params.x = 0;
                params.y = 0;//相对上方的偏移,负值忽略.
                params.dimAmount = dimAmount;
//                int windowAnimations = params.windowAnimations;
//                window.setAttributes(params);//将修改后的布局参数作用到窗口上
            }
//            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//FLAG_BLUR_BEHIND模糊(毛玻璃效果), FLAG_DIM_BEHIND暗淡
//            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

//        findViewById();//可以初始化控件等

        super.setOnShowListener(this);
        super.setOnDismissListener(this);
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
     *             <tr> <td>传 {@link 0 0}</td> <td>使用系统默认Dialog动画</td> </tr>
     *             <tr> <td>传 {@link null null}</td> <td>使用Gravity对应的动画</td> </tr>
     *             <tr>
     *                 <td>{@link R.style#Animation_AppCompat_Dialog R.style.Animation_AppCompat_Dialog}</td>
     *                 <td>系统默认Dialog动画(默认)</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#Base_Animation_AppCompat_Dialog R.style.Base_Animation_AppCompat_Dialog}</td>
     *                 <td>系统Dialog动画</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link android.R.style#Animation_Dialog android.R.style.Animation_Dialog}</td>
     *                 <td>系统Dialog动画</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link android.R.style#Animation_Toast android.R.style.Animation_Toast}</td>
     *                 <td>吐司动画</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#LeftAnimationStyle R.style.LeftAnimationStyle}</td>
     *                 <td>左边弹出动画</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#TopAnimationStyle R.style.TopAnimationStyle}</td>
     *                 <td>顶部弹出动画</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#RightAnimationStyle R.style.RightAnimationStyle}</td>
     *                 <td>右边弹出动画</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#BottomAnimationStyle R.style.BottomAnimationStyle}</td>
     *                 <td>底部弹出动画</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#YourCustomAnim R.style.YourCustomAnim}</td>
     *                 <td>也阔以自定义动画</td>
     *             </tr>
     *        </table>
     */
    public BaseDialog setGravityAndAnimation(int gravity, @Nullable Integer windowAnimations) {
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            if (params != null) {
                params.gravity = gravity;
                if (windowAnimations == null) {
                    //Gravity.START 里面包含 Gravity.LEFT, 所以不用另外判断Gravity.START
                    if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
                        params.windowAnimations = R.style.LeftAnimationStyle;
                    } else if ((gravity & Gravity.TOP) == Gravity.TOP) {
                        params.windowAnimations = R.style.TopAnimationStyle;
                    } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
                        params.windowAnimations = R.style.RightAnimationStyle;
                    } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                        params.windowAnimations = R.style.BottomAnimationStyle;
                    } else {
                        //其它情况, 默认居中.
                        params.windowAnimations = R.style.Animation_AppCompat_Dialog;
                    }
                } else if (windowAnimations == 0) {
                    params.windowAnimations = R.style.Animation_AppCompat_Dialog;
                } else {
                    params.windowAnimations = windowAnimations;
                }
            }
        }
        return this;
    }

    /**
     * 全屏, 包括状态栏
     */
    public BaseDialog setFullScreen() {
        if (window != null) {
            BarUtils.setNavBarVisibility(window, false);    //关键代码
            WindowManager.LayoutParams params = window.getAttributes();
            if (params != null) {
                //高度全屏(if没有↑, 还有状态栏会显示)
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
        }
        return this;
    }

    /**
     * 设置窗口后面灰色大背景的亮度[0-1], 0最亮.  show()之前设置
     * @param dimAmount 昏暗的数量
     */
    public BaseDialog setDimAmount(@FloatRange(from = 0.0f, to = 1.0f) float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
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

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    /**
     * 如果子类重写了本方法, 务必调用super.onShow(dialog); 因为会调生命周期.
     */
    @CallSuper
    @Override
    public void onShow(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        if (onShowListener != null) onShowListener.onShow(dialog);
    }

    /**
     * 如果子类重写了本方法, 务必调用super.onDismiss(dialog); 因为会调生命周期.
     */
    @CallSuper
    @Override
    public void onDismiss(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        if (onDismissListener != null) onDismissListener.onDismiss(dialog);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }
}
