package com.actor.myandroidframework.utils;

import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Description: 简单的log日志输出, 如果字符串过长, 会自动在框内多行输出. <br />
 *              如果想输出xml/json/漂亮格式, 或输出到文件, 请使用: {@link com.blankj.utilcode.util.LogUtils} <br />
 * @author     : ldf <br />
 * Date       : 2018/4/18 on 11:07
 * @version 1.0.3
 */
public class LogUtils {
    protected static final boolean       IS_DEBUG_MODE  = ConfigUtils.IS_APP_DEBUG;
    protected static final String        TAG            = "LogUtils";
    @NonNull
    protected static final String        LINE_SEP       = System.getProperty("line.separator", "\n");
    protected static final String        TOP_LINE       = "┌────────────────────────────────────────────────────────" + LINE_SEP;
    protected static final String        LEFT_BORDER    = "│ ";
    protected static final String        BOTTOM_LINE    = "└────────────────────────────────────────────────────────" + LINE_SEP;
//    protected static final int           MAX_LEN        = 1100;// fit for Chinese character
    protected static final int           MAX_LEN        = 500;// 经实测, 1100感觉太长了
    protected static       int           mStackPosition = 4;
    protected static final StringBuilder SB = new StringBuilder(MAX_LEN);

    /**
     * 如果你发现打印堆栈位置不对, 可重新设置位置.
     * @param stackPosition 堆栈位置
     */
    public static void setStackPosition(@IntRange(from = 0) final int stackPosition) {
        if (stackPosition >= 0) mStackPosition = stackPosition;
    }


    /**
     * 打印格式化后的字符串
     */
    public static void verboseFormat(@NonNull String format, @Nullable Object... args) {
        //直接调用, 否则堆栈位置不正确
        if (IS_DEBUG_MODE) printlnLogInfo(Log.VERBOSE, TextUtils2.getStringFormat(format, args), null, mStackPosition);
    }
    public static void verbose(@Nullable Object msg) {
        printlnLogInfo(Log.VERBOSE, msg, null, mStackPosition);
    }
    public static void verbose(@Nullable Object msg, @Nullable Throwable tr) {
        printlnLogInfo(Log.VERBOSE, msg, tr, mStackPosition);
    }
    public static void verbose(@Nullable Object msg, @Nullable Throwable tr, @IntRange(from = 0) int stackPosition) {
        printlnLogInfo(Log.VERBOSE, msg, tr, stackPosition);
    }


    public static void debugFormat(@NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.DEBUG, TextUtils2.getStringFormat(format, args), null, mStackPosition);
    }
    public static void debug(@Nullable Object msg) {
        printlnLogInfo(Log.DEBUG, msg, null, mStackPosition);
    }
    public static void debug(@Nullable Object msg, @Nullable Throwable tr) {
        printlnLogInfo(Log.DEBUG, msg, tr, mStackPosition);
    }
    public static void debug(@Nullable Object msg, @Nullable Throwable tr, @IntRange(from = 0) int stackPosition) {
        printlnLogInfo(Log.DEBUG, msg, tr, stackPosition);
    }


    public static void infoFormat(@NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.INFO, TextUtils2.getStringFormat(format, args), null, mStackPosition);
    }
    public static void info(@Nullable Object msg) {
        printlnLogInfo(Log.INFO, msg, null, mStackPosition);
    }
    public static void info(@Nullable Object msg, @Nullable Throwable tr) {
        printlnLogInfo(Log.INFO, msg, tr, mStackPosition);
    }
    public static void info(@Nullable Object msg, @Nullable Throwable tr, @IntRange(from = 0) int stackPosition) {
        printlnLogInfo(Log.INFO, msg, tr, stackPosition);
    }


    public static void warnFormat(@NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.WARN, TextUtils2.getStringFormat(format, args), null, mStackPosition);
    }
    public static void warn(@Nullable Object msg) {
        printlnLogInfo(Log.WARN, msg, null, mStackPosition);
    }
    public static void warn(@Nullable Object msg, @Nullable Throwable tr) {
        printlnLogInfo(Log.WARN, msg, tr, mStackPosition);
    }
    public static void warn(@Nullable Object msg, @Nullable Throwable tr, @IntRange(from = 0) int stackPosition) {
        printlnLogInfo(Log.WARN, msg, tr, stackPosition);
    }


    public static void errorFormat(@NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.ERROR, TextUtils2.getStringFormat(format, args), null, mStackPosition);
    }
    public static void error(@Nullable Object msg) {
        printlnLogInfo(Log.ERROR, msg, null, mStackPosition);
    }
    public static void error(@Nullable Object msg, @Nullable Throwable tr) {
        printlnLogInfo(Log.ERROR, msg, tr, mStackPosition);
    }
    public static void error(@Nullable Object msg, @Nullable Throwable tr, @IntRange(from = 0) int stackPosition) {
        printlnLogInfo(Log.ERROR, msg, tr, stackPosition);
    }
    /**
     * 打印调用方法的所有堆栈信息
     * @param level 日志级别: Log.VERBOSE ~ Log.ASSERT
     */
    public static void printlnStackTrance(@IntRange(from = Log.VERBOSE, to = Log.ASSERT) int level) {
        if (!IS_DEBUG_MODE) return;
        if (level < Log.VERBOSE || level > Log.ASSERT) level = Log.ERROR;
        synchronized (TAG) {
            print2Console(level, TOP_LINE);
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            int pos = Math.min(stackTraceElements.length - 1, mStackPosition - 1);
            print2Console(level, LEFT_BORDER + stackTraceElements[pos] + " 打印堆栈信息:");
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                print2Console(level, LEFT_BORDER + "\tat " + stackTraceElement + LINE_SEP);
            }
            print2Console(level, BOTTOM_LINE);
        }
    }


    /**
     * 如果是debug模式, 就输出日志所包含的信息
     * 示例: MyActivity.java: 125行, 方法名:onActivityResult, 输出: 选择文件返回, path=xxx.jpg
     */
    protected static void printlnLogInfo(int level, @Nullable Object msg, @Nullable Throwable tr, @IntRange(from = 0) int stackPosition) {
        if (!IS_DEBUG_MODE) return;
        if (stackPosition < 0) stackPosition = mStackPosition;
        //堆栈跟踪
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        StackTraceElement stackTraceElement = stackTraceElements[Math.min(stackTraceElements.length - 1, stackPosition)];
//        //类名: com.actor.myandroidframework.utils.LogUtils
//        String className = stackTraceElement.getClassName();
//        //文件名: LogUtils.java
//        String fileName = stackTraceElement.getFileName();
//        //方法名称: onCreate
//        String methodName = stackTraceElement.getMethodName();
//        //日志输出行数
//        int lineNumber = stackTraceElement.getLineNumber();
//        /**
//         * @param fileName 文件名 <br />
//         *                 1.即使写个项目中(不是在jar/aar中)不相关的文件名, 也能跳转... <br />
//         *                 2.如果项目中(不是在jar/aar中)有2个同名文件, 点击的时候会弹框自选跳转... <br />
//         *                 3.如果文件名固定写成jar/aar中的Xxx.java, 没有点击效果 <br />
//         */
//        String pre = TextUtils2.getStringFormat("%s.%s(%s:%d) ", className, methodName, fileName, lineNumber);
        String pre = stackTraceElements[Math.min(stackTraceElements.length - 1, stackPosition)] + " ";
        String msgS = String.valueOf(msg);
        String stackTraceString = Log.getStackTraceString(tr);
        //                           ↓防止下方 SB.replace()的时候, ' ' 的长度比 LINE_SEP 的长度短, 导致字符串多复制1遍
        int length = pre.length() + LINE_SEP.length() + msgS.length() + LINE_SEP.length() + stackTraceString.length();
        SB.ensureCapacity(length);
        SB.append(pre).append(msgS).append(LINE_SEP).append(stackTraceString);
        if (tr == null && SB.length() <= MAX_LEN) {
            printSubMsg(level, SB.toString(), true);
            SB.setLength(0);
            return;
        }
        synchronized (TAG) {
            //if(tr != null 或者 字符串太长) => pre 和 后面的内容 分开打印
            SB.replace(pre.length() - 1, pre.length(), LINE_SEP);
            print2Console(level, TOP_LINE);
            int index = 0, lineLength;
            for (String line : SB.toString().split(LINE_SEP)) {
                lineLength = line.length();
                for (int i = 0, lineCount = lineLength / MAX_LEN; i < lineCount; i++, index += MAX_LEN) {
                    printSubMsg(level, line.substring(index, index + MAX_LEN), false);
                }
                if (index != lineLength) printSubMsg(level, line.substring(index, lineLength), false);
                index = 0;
            }
            print2Console(level, BOTTOM_LINE);
            SB.setLength(0);
        }
    }

    protected static void printSubMsg(final int level, @NonNull final String msg, boolean needSplit) {
        if (needSplit) {
            //split会去掉msg末尾换行\n
            String[] lines = msg.split(LINE_SEP);
            for (String line : lines) print2Console(level, LEFT_BORDER + line);
        } else print2Console(level, LEFT_BORDER + msg);
    }

    protected static void print2Console(int level, @NonNull String msg) {
        Log.println(level, TAG, msg);
    }
}
