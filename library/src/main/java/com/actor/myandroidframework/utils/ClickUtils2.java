package com.actor.myandroidframework.utils;

import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ClickUtils;

/**
 * description: 点击工具类 <br />
 *
 * Author     : ldf <br />
 * date       : 2021/2/7 on 13
 * @version 1.0
 */
public class ClickUtils2 {

    /**
     * 点击间隔
     * @see com.blankj.utilcode.util.ClickUtils#DEBOUNCING_DEFAULT_VALUE
     */
    protected static final int        DEBOUNCING_DEFAULT_VALUE = 200;

    /**
     * 对单视图应用防抖点击. (请勿重复调用添加点击事件)
     * @param listener 点击监听
     */
    public static void applySingleDebouncing(View view, @NonNull View.OnClickListener listener) {
        applySingleDebouncing(view, DEBOUNCING_DEFAULT_VALUE, listener);
    }

    /**
     * 对单视图应用防抖点击. (请勿重复调用添加点击事件)
     * @see com.blankj.utilcode.util.ClickUtils#applySingleDebouncing(View, View.OnClickListener)
     * @param duration 点击间隔, 单位ms
     * @param listener 点击监听
     */
    public static void applySingleDebouncing(View view, @IntRange(from = 0) int duration, @NonNull View.OnClickListener listener) {
        ClickUtils.applySingleDebouncing(view, duration, listener);
    }


    /**
     * 是否在 规定时间 内, 点击了 m 次. (请勿重复调用添加点击事件)
     * @param multiClickListener 多次点击监听, 需要传入1个/2个参数: <br />
     *            参1: int triggerClickCount: 点击次数 <br />
     *            参2: long clickInterval   : 点击间隔. 比如1秒(1000ms)内需要点击5次, 值=1000/5=200 <br />
     *        需要重写2个方法: <br />
     * {@link com.blankj.utilcode.util.ClickUtils.OnMultiClickListener#onTriggerClick(View) onTriggerClick(View)}: 当规定时间内点击了m次, 会回调这个方法. <br />
     * {@link com.blankj.utilcode.util.ClickUtils.OnMultiClickListener#onBeforeTriggerClick(View, int) onBeforeTriggerClick(View, int)}: 发生了点击, 但没有触发↑的事件.
     */
    public static void setMultiClicksInSends(View view, ClickUtils.OnMultiClickListener multiClickListener) {
        view.setOnClickListener(multiClickListener);
    }
}
