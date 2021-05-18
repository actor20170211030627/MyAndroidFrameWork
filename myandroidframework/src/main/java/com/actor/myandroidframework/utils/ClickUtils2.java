package com.actor.myandroidframework.utils;

/**
 * description: 双击工具类
 * @see com.blankj.utilcode.util.ClickUtils
 *
 * @author : ldf
 * date       : 2021/2/7 on 13
 * @version 1.0
 */
public class ClickUtils2 {

    //最后点击时间
    protected static long lastClickTime  = 0L;

    public static boolean isFirstClick() {
        return isFirstClick(500);
    }

    /**
     * 防止快速点击
     * @param clickInterval 点击间隔, 单位ms
     * @return 是否是第1次点击
     */
    public static boolean isFirstClick(int clickInterval) {
        boolean isFirstClick = false;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > clickInterval) isFirstClick = true;
        lastClickTime = currentTime;
        return isFirstClick;
    }
}
