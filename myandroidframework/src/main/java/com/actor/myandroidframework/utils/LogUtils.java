package com.actor.myandroidframework.utils;

import android.util.Log;

/**
 * Description: println,log
 * 注意:在正式环境中获取行号=-1,没时间研究为什么.
 * Author     : 李大发
 * Date       : 2018/4/18 on 11:07
 * @version 1.0
 */
public class LogUtils {
    private static boolean isDebugMode = ConfigUtils.isDebugMode;

    private LogUtils(){
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 如果是debug模式,就打印输出
     * @param msg
     * @param isDirectCall 是否直接调用本方法, 用于定位堆栈信息中元素
     */
    public static void println(Object msg, boolean isDirectCall){
        if (isDebugMode) privatePrintln(isDirectCall, msg);
    }

    protected static void privatePrintln(boolean isDirectCall, Object msg) {
        int eleNum = isDirectCall ? 4 : 5;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[eleNum];
        //获取线程名
//        String threadName = Thread.currentThread().getName();
        //获取线程ID
//        long threadID = Thread.currentThread().getId();
        //获取文件名.即xxx.java
        String fileName = stackTraceElement.getFileName();
        //获取类名.即包名+类名
//        String className = stackTraceElement.getClassName();
        //文件名,例:LoginActivity
//        String simpleName = stackTraceElement.getClass().getSimpleName();
        // 获取方法名称
        String methodName = stackTraceElement.getMethodName();
        // 获取日志输出行数
        int lineNumber = stackTraceElement.getLineNumber();
        System.out.printf("%s %d行, 方法名:%s, 输出:%s", fileName, lineNumber, methodName, msg);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 下面是Log区
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 如果是debug模式,就打印输出
     * @param msg
     * @param isDirectCall 是否直接调用本方法, 用于定位堆栈信息中元素
     */
    public static void verbose(String msg, boolean isDirectCall) {//v
        if (isDebugMode) printlnLogInfo(Level.Verbose, msg, isDirectCall);
    }

    public static void debug(String msg, boolean isDirectCall) {//d
        if (isDebugMode) printlnLogInfo(Level.Debug, msg, isDirectCall);
    }

    public static void info(String msg, boolean isDirectCall) {//i
        if (isDebugMode) printlnLogInfo(Level.Info, msg, isDirectCall);
    }

    public static void warn(String msg, boolean isDirectCall) {//w
        if (isDebugMode) printlnLogInfo(Level.Warn, msg, isDirectCall);
    }

    public static void error(String msg, boolean isDirectCall) {//e
        if (isDebugMode) printlnLogInfo(Level.Error, msg, isDirectCall);
    }

    /**
     * 打印格式化后的字符串
     */
    public static void formatError(String format, boolean isDirectCall, Object... args) {
        if (isDebugMode) printlnLogInfo(Level.Error, TextUtil.getStringFormat(format, args), isDirectCall);
    }

    /**
     * 输出日志所包含的信息
     */
    protected static void printlnLogInfo(Level level, String msg, boolean isDirectCall) {
        int eleNum = isDirectCall ? 4 : 5;
        if(msg == null) msg = "null";
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[eleNum];
        //获取线程名
//        String threadName = Thread.currentThread().getName();
        //获取线程ID
//        long threadID = Thread.currentThread().getId();
        //获取文件名.即xxx.java
        String fileName = stackTraceElement.getFileName();
        //获取类名.即包名+类名
//        String className = stackTraceElement.getClassName();
        //获取方法名称
        String methodName = stackTraceElement.getMethodName();
        //获取日志输出行数
        int lineNumber = stackTraceElement.getLineNumber();
        String stringFormat = TextUtil.getStringFormat("%d行, 方法名:%s, 输出:%s", lineNumber, methodName, msg);
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
