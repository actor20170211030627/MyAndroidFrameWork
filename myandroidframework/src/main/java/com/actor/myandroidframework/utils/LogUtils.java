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

    protected LogUtils() {
    }

    public static void verbose(Object msg) {
        verbose(msg, false);
    }

    /**
     * @param isDirectCall 是否直接调用本方法, 用于定位堆栈信息中元素
     */
    public static void verbose(Object msg, boolean isDirectCall) {//v
        printlnLogInfo(Level.Verbose, msg, isDirectCall);
    }

    public static void debug(Object msg) {
        debug(msg, false);
    }

    public static void debug(Object msg, boolean isDirectCall) {//d
        printlnLogInfo(Level.Debug, msg, isDirectCall);
    }

    public static void info(Object msg) {
        info(msg, false);
    }

    public static void info(Object msg, boolean isDirectCall) {//i
        printlnLogInfo(Level.Info, msg, isDirectCall);
    }

    public static void warn(Object msg) {
        warn(msg, false);
    }

    public static void warn(Object msg, boolean isDirectCall) {//w
        printlnLogInfo(Level.Warn, msg, isDirectCall);
    }

    public static void error(Object msg) {
        error(msg, false);
    }

    public static void error(Object msg, boolean isDirectCall) {//e
        printlnLogInfo(Level.Error, msg, isDirectCall);
    }

    //编译不通过.
//    public static void formatError(String format, Object... args) {
//        formatError(format, false, args);
//    }

    /**
     * 打印格式化后的字符串
     */
    public static void formatError(String format, boolean isDirectCall, Object... args) {
        printlnLogInfo(Level.Error, TextUtils2.getStringFormat(format, args), isDirectCall);
    }

    /**
     * 如果是debug模式, 就输出日志所包含的信息
     * 示例: MyActivity.java: 125行, 方法名:onActivityResult, 输出: 选择文件返回, path=xxx.jpg
     */
    protected static void printlnLogInfo(Level level, Object msg, boolean isDirectCall) {
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
