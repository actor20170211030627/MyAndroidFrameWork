package com.actor.myandroidframework.utils;

import android.app.Application;

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

    public static boolean isDebugMode = false;//是否是debug模式

    public static String baseUrl = "https://www.baidu.com";//需要自己设置baseUrl
}
