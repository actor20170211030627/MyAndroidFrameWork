package com.actor.myandroidframework.utils.toaster;

import androidx.annotation.IntRange;

import com.actor.myandroidframework.utils.LogUtils;
import com.hjq.toast.ToastLogInterceptor;

/**
 * description: 修复Toaster封装成Utils工具类后, 打印位置偏差问题
 *
 * @author : ldf
 * date       : 2024/1/29 on 11
 * @version 1.0
 */
public class MyToastLogInterceptor extends ToastLogInterceptor {

    protected int mStackPosition = 0;

    /**
     * 设置打印堆栈位置.
     *
     * @param stackPosition 堆栈位置
     */
    public void setStackPosition(@IntRange(from = 0) final int stackPosition) {
        if (stackPosition >= 0) mStackPosition = stackPosition;
    }

    public int getStackPosition() {
        return mStackPosition;
    }

    @Override
    protected void printToast(CharSequence text) {
        if (!isLogEnable()) {
            return;
        }

        LogUtils.info(text, null, mStackPosition);
        mStackPosition = 0;

        // 获取调用的堆栈信息
//        StackTraceElement[] stackTraces = new Throwable().getStackTrace();
//        for (StackTraceElement stackTrace : stackTraces) {
//            Log.e("TAG", stackTrace.toString());
//        }
//        Log.e("TAG", "\n-------------------\n");
//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        for (int i = 0; i < stackTraceElements.length; i++) {
//            Log.e("TAG", "Pos" + i + ": " + stackTraceElements[i].toString());
//        }

//        super.printToast(text);
    }
}
