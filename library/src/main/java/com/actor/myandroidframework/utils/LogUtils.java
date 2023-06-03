package com.actor.myandroidframework.utils;

import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Description: log日志输出, 参考{@link com.blankj.utilcode.util.LogUtils}, 这个工具类输出后可以点击跳转. <br />
 * Author     : ldf <br />
 * Date       : 2018/4/18 on 11:07
 * @version 1.0.2
 */
public class LogUtils {
    protected static final boolean IS_DEBUG_MODE = ConfigUtils.IS_APP_DEBUG;

    public static void verbose(Object msg) {
        verbose(msg, null);
    }

    public static void verbose(Object msg, Throwable tr) {//v
        printlnLogInfo(Log.VERBOSE, msg, tr);
    }


    public static void debug(Object msg) {
        debug(msg, null);
    }

    public static void debug(Object msg, Throwable tr) {//d
        printlnLogInfo(Log.DEBUG, msg, tr);
    }

    /**
     * 打印格式化后的字符串
     */
    public static void debugFormat(String format, Object... args) {
        debug(TextUtils2.getStringFormat(format, args), null);
    }


    public static void info(Object msg) {
        info(msg, null);
    }

    public static void info(Object msg, Throwable tr) {//i
        printlnLogInfo(Log.INFO, msg, tr);
    }

    public static void infoFormat(String format, Object... args) {
        info(TextUtils2.getStringFormat(format, args), null);
    }


    public static void warn(Object msg) {
        warn(msg, null);
    }

    public static void warn(Object msg, Throwable tr) {//w
        printlnLogInfo(Log.WARN, msg, tr);
    }

    public static void warnFormat(String format, Object... args) {
        warn(TextUtils2.getStringFormat(format, args), null);
    }


    public static void error(Object msg) {
        error(msg, null);
    }

    public static void error(Object msg, Throwable tr) {//e
        printlnLogInfo(Log.ERROR, msg, tr);
    }

    /**
     * 打印格式化后的字符串
     */
    public static void errorFormat(String format, Object... args) {
        error(TextUtils2.getStringFormat(format, args), null);
    }


    /**
     * 如果是debug模式, 就输出日志所包含的信息
     * 示例: MyActivity.java: 125行, 方法名:onActivityResult, 输出: 选择文件返回, path=xxx.jpg
     */
    protected static void printlnLogInfo(int level, Object msg, @Nullable Throwable tr) {
        if (!IS_DEBUG_MODE) return;
        //堆栈跟踪
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[5];
        //文件名: Xxx.java
        String fileName = TextUtils2.getNoNullString(stackTraceElement.getFileName(), "TAG");
        //ClassName: 包名 + 类名
//        String className = stackTraceElement.getClassName();
        //方法名称: onCreate
        String methodName = stackTraceElement.getMethodName();
        //日志输出行数
        int lineNumber = stackTraceElement.getLineNumber();
        String stringFormat = TextUtils2.getStringFormat("%d行, 方法名:%s, 输出:%s", lineNumber, methodName, msg);
        switch (level) {
            case Log.VERBOSE:
                Log.v(fileName, stringFormat, tr);
                break;
            case Log.DEBUG:
                Log.d(fileName, stringFormat, tr);
                break;
            case Log.INFO:
                Log.i(fileName, stringFormat, tr);
                break;
            case Log.WARN:
                Log.w(fileName, stringFormat, tr);
                break;
            case Log.ERROR:
                Log.e(fileName, stringFormat, tr);
                break;
            default:
                break;
        }
    }
}
