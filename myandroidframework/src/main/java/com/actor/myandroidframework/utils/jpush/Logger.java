package com.actor.myandroidframework.utils.jpush;

import android.util.Log;

import com.actor.myandroidframework.utils.ConfigUtils;

/**
 * Created by efan on 2017/4/13.
 */

public class Logger {

    //设为false关闭日志
    private static final boolean LOG_ENABLE = ConfigUtils.isDebugMode/*true*/;//edited 修改过

    public static void i(String tag, String msg){
        if (LOG_ENABLE){
            Log.e(tag, msg);
        }
    }
    public static void v(String tag, String msg){
        if (LOG_ENABLE){
            Log.e(tag, msg);
        }
    }
    public static void d(String tag, String msg){
        if (LOG_ENABLE){
            Log.e(tag, msg);
        }
    }
    public static void w(String tag, String msg){
        if (LOG_ENABLE){
            Log.e(tag, msg);
        }
    }
    public static void e(String tag, String msg){
        if (LOG_ENABLE){
            Log.e(tag, msg);
        }
    }

}
