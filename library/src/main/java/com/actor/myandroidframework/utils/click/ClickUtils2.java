package com.actor.myandroidframework.utils.click;

import android.view.View;

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
    protected static final int  DEBOUNCING_DEFAULT_VALUE = 200;
    protected static       long globalClickTime          = 0;

    /**
     * 对View应用防抖点击, 或者直接 {@link OnClickListenerDebouncing view.setOnClickListener(new OnClickListenerDebouncing() { })}
     * @param listener 点击监听
     * @see com.blankj.utilcode.util.ClickUtils#applySingleDebouncing(View, View.OnClickListener)
     */
    public static void applySingleDebouncing(@NonNull View view, @NonNull OnClickListenerDebouncing listener) {
        view.setOnClickListener(listener);
    }


    /**
     * 是否在 规定时间 内, 点击了 m 次
     * @param multiClickListener 多次点击监听, 需要传入1个/2个参数:
     *                           <ul>
     *                               <li>{@link ClickUtils.OnMultiClickListener#mTriggerClickCount triggerClickCount} 参1: 点击次数</li>
     *                               <li>{@link ClickUtils.OnMultiClickListener#mClickInterval clickInterval} 参2: 点击间隔. if 点击间隔 < clickInterval, 会重新开始计次数. 比如1秒(1000ms)内需要点击5次, 值: 1000/5 = 200</li>
     *                           </ul>
     *                           需要重写2个方法:
     *                           <ul>
     *                               <li>{@link com.blankj.utilcode.util.ClickUtils.OnMultiClickListener#onTriggerClick(View) onTriggerClick(View)}: 当规定时间内点击了m次, 会回调这个方法</li>
     *                               <li>{@link com.blankj.utilcode.util.ClickUtils.OnMultiClickListener#onBeforeTriggerClick(View, int) onBeforeTriggerClick(View, int)}: 发生了点击, 但没有触发↑的事件(点击间隔>clickInterval or 已回调 onTriggerClick(View) 并重新开始计数)</li>
     *                           </ul>
     */
    public static void setMultiClicksInSends(@NonNull View view, @NonNull ClickUtils.OnMultiClickListener multiClickListener) {
        view.setOnClickListener(multiClickListener);
    }
}
