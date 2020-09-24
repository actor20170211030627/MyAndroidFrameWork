package com.actor.myandroidframework.utils;

import android.app.Application;

import com.actor.myandroidframework.application.ActorApplication;
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
     * 是否是debug模式
     * @see ActorApplication#onCreate()
     */
    public static boolean isDebugMode;

    public static String baseUrl = "";//需要自己设置baseUrl
}
