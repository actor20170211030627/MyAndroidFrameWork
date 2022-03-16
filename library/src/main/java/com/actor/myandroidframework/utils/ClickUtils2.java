package com.actor.myandroidframework.utils;

import android.view.View;

import androidx.annotation.IntRange;

/**
 * description: 双击工具类 <br />
 * {@link com.blankj.utilcode.util.ClickUtils#applySingleDebouncing(View, View.OnClickListener)}
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
    protected static final int                   DEBOUNCING_DEFAULT_VALUE = 200;
    /**
     * 点击的Tag
     * @see com.blankj.utilcode.util.ClickUtils#DEBOUNCING_TAG
     */
    protected static final int  DEBOUNCING_TAG           = -7;

    /**
     * @return 是否是消除抖动点击, 防止快速点击
     */
    public static boolean isDebouncingClick(View view) {
        return isDebouncingClick(view, DEBOUNCING_DEFAULT_VALUE);
    }

    /**
     * 是否是消除抖动点击, 防止快速点击
     * @param duration 点击间隔, 单位ms
     */
    public static boolean isDebouncingClick(View view, @IntRange(from = 0) int duration) {
        long curTime = System.currentTimeMillis();
        Object tag = view.getTag(DEBOUNCING_TAG);
        //如果第一次点击
        if (!(tag instanceof Long)) {
            view.setTag(DEBOUNCING_TAG, curTime);
            return true;
        }
        long preTime = (Long) tag;
        //如果时间错乱
        if (curTime < preTime) {
            //重设时间
            view.setTag(DEBOUNCING_TAG, curTime);
            return false;
        } else if (curTime - preTime <= duration) {
            //如果在间隔时间内
            return false;
        }
        view.setTag(DEBOUNCING_TAG, curTime);
        return true;
    }
}
