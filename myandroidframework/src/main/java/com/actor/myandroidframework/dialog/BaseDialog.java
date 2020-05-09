package com.actor.myandroidframework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.actor.myandroidframework.R;

/**
 * Description: Dialog基类
 *      注意: 如果'背景使用的shape' & 'shape下方有圆角' & '下方圆角位置的view有背景色',
 *          有可能会造成 '下方圆角被颜色覆盖' 的问题! 解决方法:
 *          1. shape 加上 padding(bottom) 属性
 *          2. 下方圆角位置的view 加一个同样圆角的 shape
 * Author     : 李大发
 * Date       : 2020-1-21 on 16:49
 *
 * @version 1.0
 */
public abstract class BaseDialog extends Dialog {

    protected Window window;
    protected float  dimAmount = 0.5F;//背景灰度, [0, 1]

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);//给dialog设置样式, 去掉标题栏
        init();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    protected void init() {
        window = getWindow();//获取当前dialog所在的窗口对象
        int layoutResId = getLayoutResId();
        if (layoutResId != 0) setContentView(layoutResId);
    }

    /**
     * 设置你自定义Dialog的layout
     */
    protected abstract @LayoutRes int getLayoutResId();

    //只会创建一次
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

//        findViewById()//可以初始化控件等
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

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    /**
     * 设置点击返回键 & 外部, 是否能取消dialog
     * //如果 setCancelable = true, setCanceledOnTouchOutside = true/false, 设置都有效
     * //如果 setCancelable = false, setCanceledOnTouchOutside = true, 点击 '返回'&'外部' 都能取消!!!
     */
    public void setCancelAble(boolean cancelAble) {
        setCancelable(cancelAble);
        setCanceledOnTouchOutside(cancelAble);//外部点击是否能取消
    }

    /**
     * 设置窗口后面灰色大背景的亮度[0-1], 0最亮
     * @param dimAmount 昏暗的数量
     */
    public void setDimAmount(@FloatRange(from = 0, to = 1) float dimAmount) {
        this.dimAmount = dimAmount;
    }
}
