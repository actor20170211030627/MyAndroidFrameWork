package com.actor.myandroidframework.utils;

import android.app.Application;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Description: 整个项目常量配置 <br />
 * Author     : ldf <br />
 * Date       : 2019/11/21 on 09:43
 *
 * @version 1.0
 */
public class ConfigUtils {

    public static final Application APPLICATION = Utils.getApp();

    /**
     * 当我们没在AndroidManifest.xml中设置其 debuggable="true" 属性时:
     * <application android:debuggable="true" tools:ignore="HardcodedDebugMode"
     *
     * 运行:                                这种方式打包时其debug属性为true,
     * Build->Generate Signed APK release: 这种方式打包时其debug属性为法false.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     */
    public static final boolean IS_APP_DEBUG = AppUtils.isAppDebug();

    /**
     * 状态栏高度
     */
    public static final int STATUS_BAR_HEIGHT = BarUtils.getStatusBarHeight();

    /**
     * App 的屏幕宽度, 和屏幕宽度不是一个概念
     */
    public static final int APP_SCREEN_WIDTH = ScreenUtils.getAppScreenWidth();
}
