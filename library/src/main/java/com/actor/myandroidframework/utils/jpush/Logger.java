package com.actor.myandroidframework.utils.jpush;

import android.util.Log;

import com.actor.myandroidframework.utils.ConfigUtils;

/**
 * Created by efan on 2017/4/13.
 * @deprecated 这是jpush的logger, 不要用这个. edited 修改过
 */
@Deprecated
public class Logger {

    //设为false关闭日志
    private static final boolean LOG_ENABLE = ConfigUtils.IS_APP_DEBUG/*true*/;//edited 修改过

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
