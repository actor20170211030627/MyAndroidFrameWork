package com.actor.myandroidframework.utils;

import android.app.Application;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Description: 整个项目常量配置
 * Author     : 李大发
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

    public static String baseUrl = "";//需要自己设置baseUrl
}
