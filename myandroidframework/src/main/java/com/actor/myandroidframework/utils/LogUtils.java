package com.actor.myandroidframework.utils;

import android.util.Log;

/**
 * Description: log日志输出
 *
 * @version 1.0.2
 * @see com.blankj.utilcode.util.LogUtils, 这个工具类输出后可以点击跳转.
 * <p>
 * Author     : ldf
 * Date       : 2018/4/18 on 11:07
 */
public class LogUtils {
    protected static final boolean IS_DEBUG_MODE = ConfigUtils.IS_APP_DEBUG;

    public static void verbose(Object msg) {
        verbose(false, msg);
    }

    /**
     * @param isDirectCall 是否直接调用本方法, 用于定位堆栈信息中元素
     */
    public static void verbose(boolean isDirectCall, Object msg) {//v
        printlnLogInfo(isDirectCall, Level.Verbose, msg);
    }

    public static void debug(Object msg) {
        debug(false, msg);
    }

    public static void debug(boolean isDirectCall, Object msg) {//d
        printlnLogInfo(isDirectCall, Level.Debug, msg);
    }

    public static void info(Object msg) {
        info(false, msg);
    }

    public static void info(boolean isDirectCall, Object msg) {//i
        printlnLogInfo(isDirectCall, Level.Info, msg);
    }

    public static void warn(Object msg) {
        warn(false, msg);
    }

    public static void warn(boolean isDirectCall, Object msg) {//w
        printlnLogInfo(isDirectCall, Level.Warn, msg);
    }

    public static void error(Object msg) {
        error(false, msg);
    }

    public static void error(boolean isDirectCall, Object msg) {//e
        printlnLogInfo(isDirectCall, Level.Error, msg);
    }

    public static void formatError(String format, Object... args) {
        formatError(false, format, args);
    }

    /**
     * 打印格式化后的字符串
     */
    public static void formatError(boolean isDirectCall, String format, Object... args) {
        printlnLogInfo(isDirectCall, Level.Error, TextUtils2.getStringFormat(format, args));
    }

    /**
     * 如果是debug模式, 就输出日志所包含的信息
     * 示例: MyActivity.java: 125行, 方法名:onActivityResult, 输出: 选择文件返回, path=xxx.jpg
     */
    protected static void printlnLogInfo(boolean isDirectCall, Level level, Object msg) {
        if (!IS_DEBUG_MODE) return;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[isDirectCall ? 4 : 5];
        //文件名: ActorBaseActivity.java
        String fileName = stackTraceElement.getFileName();
        //ClassName = 包名 + 类名: com.google.package.activity.ActorBaseActivity
//        String className = stackTraceElement.getClassName();
        //方法名称: onCreate
        String methodName = stackTraceElement.getMethodName();
        //日志输出行数
        int lineNumber = stackTraceElement.getLineNumber();
        String stringFormat = TextUtils2.getStringFormat("%d行, 方法名:%s, 输出:%s", lineNumber, methodName, msg);
        switch (level) {
            case Verbose:
                Log.v(fileName, stringFormat);
                break;
            case Debug:
                Log.d(fileName, stringFormat);
                break;
            case Info:
                Log.i(fileName, stringFormat);
                break;
            case Warn:
                Log.w(fileName, stringFormat);
                break;
            case Error:
                Log.e(fileName, stringFormat);
                break;
        }
    }

    protected enum Level {
        Verbose, Debug, Info, Warn, Error
    }
}
