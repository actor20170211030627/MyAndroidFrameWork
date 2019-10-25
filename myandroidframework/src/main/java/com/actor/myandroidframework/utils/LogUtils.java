package com.actor.myandroidframework.utils;

import android.os.Looper;
import android.util.Log;

import com.actor.myandroidframework.application.ActorApplication;

/**
 * Description: println,log
 * 注意:在正式环境中获取行号=-1,没时间研究为什么.
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2018/4/18 on 11:07
 * @version 1.0
 */
public class LogUtils {
    private static boolean isDebugMode = ActorApplication.instance.isDebugMode;

    private LogUtils(){
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 如果是debug模式,就打印输出
     * @param msg
     * @param isDirectCall 是否直接调用本方法
     */
    public static void println(Object msg, boolean isDirectCall){
        if (isDebugMode) privatePrintln(isDirectCall, msg);
    }

    /**
     * 打印线程
     * @param isDirectCall 是否直接调用本方法
     * @param text
     */
    public static void printlnThread(boolean isDirectCall, CharSequence text){
        if (isDebugMode) {
            CharSequence string = text == null ? "null" : text;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                privatePrintln(isDirectCall, "主线程:" + string);
            } else {
                privatePrintln(isDirectCall, "子线程:" + string);
            }
        }
    }

    private static void privatePrintln(boolean isDirectCall, Object msg) {
        int eleNum = isDirectCall?4 : 5;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[eleNum];
        // 获取线程名
//            String threadName = Thread.currentThread().getName();
        // 获取线程ID
//            long threadID = Thread.currentThread().getId();
        // 获取文件名.即xxx.java
        String fileName = stackTraceElement.getFileName();
        // 获取类名.即包名+类名
//            String className = stackTraceElement.getClassName();
        //文件名,例:LoginActivity
//            String simpleName = stackTraceElement.getClass().getSimpleName();
        // 获取方法名称
        String methodName = stackTraceElement.getMethodName();
        // 获取日志输出行数
        int lineNumber = stackTraceElement.getLineNumber();
        //getClass()不能在静态方法中调用
        System.out.println(fileName + ":"+lineNumber + "行,方法名:" + methodName + ",输出:" + String.valueOf(msg));
    }

    //=====================================下面是Log区=======================================
    public static void verbose(String message, boolean isDirectCall) {//v
        if (isDebugMode) printlnLogInfo(Level.Verbose, message, isDirectCall);
    }

    public static void debug(String message, boolean isDirectCall) {//d
        if (isDebugMode) printlnLogInfo(Level.Debug, message, isDirectCall);
    }

    public static void info(String message, boolean isDirectCall) {//i
        if (isDebugMode) printlnLogInfo(Level.Info, message, isDirectCall);
    }

    public static void warn(String message, boolean isDirectCall) {//w
        if (isDebugMode) printlnLogInfo(Level.Warn, message, isDirectCall);
    }

    public static void error(String message, boolean isDirectCall) {//e
        if (isDebugMode) printlnLogInfo(Level.Error, message, isDirectCall);
    }

    /**
     * String.format
     */
    public static void formatError(String format, boolean isDirectCall, Object... args) {
        if (isDebugMode) printlnLogInfo(Level.Error, String.format(format, args), isDirectCall);
    }

    /**
     * 输出日志所包含的信息
     */
    private static void printlnLogInfo(Level level, String msg, boolean isDirectCall) {
        int eleNum = isDirectCall?4 : 5;
        if(msg == null) msg = "null";
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[eleNum];
        StringBuilder logInfoStringBuilder = new StringBuilder();
        // 获取线程名
        //String threadName = Thread.currentThread().getName();
        // 获取线程ID
        //long threadID = Thread.currentThread().getId();
        // 获取文件名.即xxx.java
        String fileName = stackTraceElement.getFileName();
        // 获取类名.即包名+类名
        //String className = stackTraceElement.getClassName();
        // 获取方法名称
        String methodName = stackTraceElement.getMethodName();
        // 获取日志输出行数
        int lineNumber = stackTraceElement.getLineNumber();
        logInfoStringBuilder.append(lineNumber);
//        logInfoStringBuilder.append("threadID=" + threadID).append(SEPARATOR);
//        logInfoStringBuilder.append("threadName=" + threadName).append(SEPARATOR);
        logInfoStringBuilder.append("行,方法名:");
//        logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
        logInfoStringBuilder.append(methodName);
        logInfoStringBuilder.append(",输出:");
        logInfoStringBuilder.append(msg);
        switch (level) {
            case Verbose:
                Log.v(fileName, logInfoStringBuilder.toString());
            break;
            case Debug:
                Log.d(fileName, logInfoStringBuilder.toString());
                break;
            case Info:
                Log.i(fileName, logInfoStringBuilder.toString());
                break;
            case Warn:
                Log.w(fileName, logInfoStringBuilder.toString());
                break;
            case Error:
                Log.e(fileName, logInfoStringBuilder.toString());
                break;
        }

    }

    private enum Level {
        Verbose,Debug,Info,Warn,Error
    }
}
