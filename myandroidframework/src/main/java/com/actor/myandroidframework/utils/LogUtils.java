package com.actor.myandroidframework.utils;

import android.util.Log;

/**
 * Description: log日志打印, 本工具输出示例: {@link #printlnLogInfo(Level, String, boolean)}
 *
 * @see com.blankj.utilcode.util.LogUtils, 这个工具类输出后可以点击跳转.
 *
 * Author     : 李大发
 * Date       : 2018/4/18 on 11:07
 * @version 1.0.1
 */
public class LogUtils {
    protected static final boolean IS_DEBUG_MODE = ConfigUtils.IS_APP_DEBUG;

    protected LogUtils(){
    }

    /**
     * 如果是debug模式,就打印输出
     * @param msg
     * @param isDirectCall 是否直接调用本方法, 用于定位堆栈信息中元素
     */
    public static void verbose(String msg, boolean isDirectCall) {//v
        if (IS_DEBUG_MODE) printlnLogInfo(Level.Verbose, msg, isDirectCall);
    }

    public static void debug(String msg, boolean isDirectCall) {//d
        if (IS_DEBUG_MODE) printlnLogInfo(Level.Debug, msg, isDirectCall);
    }

    public static void info(String msg, boolean isDirectCall) {//i
        if (IS_DEBUG_MODE) printlnLogInfo(Level.Info, msg, isDirectCall);
    }

    public static void warn(String msg, boolean isDirectCall) {//w
        if (IS_DEBUG_MODE) printlnLogInfo(Level.Warn, msg, isDirectCall);
    }

    public static void error(String msg, boolean isDirectCall) {//e
        if (IS_DEBUG_MODE) printlnLogInfo(Level.Error, msg, isDirectCall);
    }

    /**
     * 打印格式化后的字符串
     */
    public static void formatError(String format, boolean isDirectCall, Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Level.Error, TextUtils2.getStringFormat(format, args), isDirectCall);
    }

    /**
     * 输出日志所包含的信息
     * 输出示例: MyActivity.java: 125行, 方法名:onActivityResult, 输出: 选择文件返回, path=xxx.jpg
     */
    protected static void printlnLogInfo(Level level, String msg, boolean isDirectCall) {
        int eleNum = isDirectCall ? 4 : 5;
        if(msg == null) msg = "null";
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[eleNum];
        //获取文件名: ActorBaseActivity.java
        String fileName = stackTraceElement.getFileName();
        //ClassName = 包名 + 类名: com.google.package.activity.ActorBaseActivity
//        String className = stackTraceElement.getClassName();
        //获取方法名称: onCreate
        String methodName = stackTraceElement.getMethodName();
        //获取日志输出行数
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
        Verbose,Debug,Info,Warn,Error
    }
}
