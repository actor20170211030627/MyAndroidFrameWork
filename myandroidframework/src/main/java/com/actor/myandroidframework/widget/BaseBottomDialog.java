package com.actor.myandroidframework.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.actor.myandroidframework.R;

/**
 * Description: 从底部弹出的Dialog, 不能上下拖拽滑动
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/8/11 on 23:26
 * @version 1.0
 */
public abstract class BaseBottomDialog extends Dialog {

    private static final String TAG = "BaseBottomDialog";
    protected            Window mWindow;
    protected float               dimAmount = 0.5F;//背景灰度, [0, 1]

    public BaseBottomDialog(@NonNull Context context) {
        super(context, R.style.BaseBottomDialog);//给dialog设置样式, 去掉标题栏

        setContentView(getLayoutResId());
    }

    /**
     * 设置你自定义Dialog的layout
     */
    protected abstract  @LayoutRes int getLayoutResId();

    //只会创建一次
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindow = getWindow();//获取当前dialog所在的窗口对象
        WindowManager.LayoutParams params = mWindow.getAttributes();//获取当前窗口的属性, 布局参数
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;//重心位置改为靠下居中显示
        params.dimAmount = dimAmount;
        mWindow.setAttributes(params);//将修改后的布局参数作用到窗口上
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//FLAG_BLUR_BEHIND模糊, FLAG_DIM_BEHIND暗淡

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

    /**
     * 设置窗口后面灰色大背景的亮度[0-1]
     * @param dimAmount 昏暗的数量
     */
    public void setDimAmount(@FloatRange(from = 0, to = 1) float dimAmount) {
        this.dimAmount = dimAmount;
    }

    protected void logError(String text) {
        Log.e(TAG, "logError: ".concat(text));
    }
}
