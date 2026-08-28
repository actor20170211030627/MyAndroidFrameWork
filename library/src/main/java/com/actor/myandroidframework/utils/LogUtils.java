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
//    @NonNull
//    protected static final String        LINE_SEP       = System.getProperty("line.separator", "\n");
    protected static final char          LINE_SEP       = '\n'; // /r/n  &  /n
    protected static final String        TOP_LINE       = "┌────────────────────────────────────────────────────────────────────────────────────────────────────";
    protected static final String        TOP_LINE2      = "├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    protected static final String        LEFT_BORDER    = "│ ";
    protected static final String        BOTTOM_LINE    = "└────────────────────────────────────────────────────────────────────────────────────────────────────";
//    protected static final int           MAX_LEN        = 1100;// fit for Chinese character (经实测, 500, 1100感觉太长了)
    protected static final int           MAX_LEN        = TOP_LINE.length() * 2 - LEFT_BORDER.length();
    protected static       int           mStackPosition = 4;
    protected static final StringBuilder SB             = new StringBuilder(MAX_LEN);
    protected static final StringBuilder SB2            = new StringBuilder(MAX_LEN);

    /**
     * 如果你发现打印堆栈位置不对, 可重新设置位置.
     * @param stackPosition 堆栈位置
     */
    public static void setStackPosition(@IntRange(from = 0) final int stackPosition) {
        if (stackPosition >= 0) mStackPosition = stackPosition;
    }

    public static int getStackPosition() {
        return mStackPosition;
    }

    /**
     * 打印格式化后的字符串
     */
    public static void verboseFormat(@NonNull String format, @Nullable Object... args) {
        //直接调用, 否则堆栈位置不正确
        if (IS_DEBUG_MODE) printlnLogInfo(Log.VERBOSE, TextUtils2.getStringFormat(format, args), null, mStackPosition);
    }
    public static void verboseFormat(@Nullable Throwable tr, @NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.VERBOSE, TextUtils2.getStringFormat(format, args), tr, mStackPosition);
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
    public static void debugFormat(@Nullable Throwable tr, @NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.DEBUG, TextUtils2.getStringFormat(format, args), tr, mStackPosition);
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
    public static void infoFormat(@Nullable Throwable tr, @NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.INFO, TextUtils2.getStringFormat(format, args), tr, mStackPosition);
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
    public static void warnFormat(@Nullable Throwable tr, @NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.WARN, TextUtils2.getStringFormat(format, args), tr, mStackPosition);
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
    public static void errorFormat(@Nullable Throwable tr, @NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.ERROR, TextUtils2.getStringFormat(format, args), tr, mStackPosition);
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
     * 最高严重级别常量，用于记录‌断言失败‌导致的致命错误——即“理论上绝不应发生”的逻辑条件被违反。
     */
    public static void assertFormat(@NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.ASSERT, TextUtils2.getStringFormat(format, args), null, mStackPosition);
    }
    public static void assertFormat(@Nullable Throwable tr, @NonNull String format, @Nullable Object... args) {
        if (IS_DEBUG_MODE) printlnLogInfo(Log.ASSERT, TextUtils2.getStringFormat(format, args), tr, mStackPosition);
    }
    public static void assert_(@Nullable Object msg) {
        printlnLogInfo(Log.ASSERT, msg, null, mStackPosition);
    }
    public static void assert_(@Nullable Object msg, @Nullable Throwable tr) {
        printlnLogInfo(Log.ASSERT, msg, tr, mStackPosition);
    }
    public static void assert_(@Nullable Object msg, @Nullable Throwable tr, @IntRange(from = 0) int stackPosition) {
        printlnLogInfo(Log.ASSERT, msg, tr, stackPosition);
    }


    /**
     * 打印调用方法的所有堆栈跟踪
     * @param level 日志级别: Log.VERBOSE ~ Log.ASSERT
     */
    public static void printlnStackTrance(@IntRange(from = Log.VERBOSE, to = Log.ASSERT) int level) {
        if (!IS_DEBUG_MODE) return;
        synchronized (TAG) {
            if (mStackPosition < 1) mStackPosition = 1;
            if (level < Log.VERBOSE || level > Log.ASSERT) level = Log.ERROR;
            Thread thread = Thread.currentThread();
            StackTraceElement[] stes = thread.getStackTrace();
            String pre = getStackTraceElementInfo(stes[Math.min(stes.length - 1, mStackPosition - 1)]);
            SB.append(LEFT_BORDER).append(thread.getName()).append(", ").append(pre).append(" 打印堆栈跟踪:");
            print2Console(level, TOP_LINE);
            print2Console(level, SB.toString());
            print2Console(level, TOP_LINE2);
            SB.setLength(0);
            for (StackTraceElement ste : stes) {
                SB.append(LEFT_BORDER).append("\tat ").append(getStackTraceElementInfo(ste));
                print2Console(level, SB.toString());
                SB.setLength(0);
            }
            print2Console(level, BOTTOM_LINE);
        }
    }


    /**
     * 如果是debug模式, 就输出日志所包含的信息
     */
    protected static void printlnLogInfo(@IntRange(from = Log.VERBOSE, to = Log.ASSERT) int level,
                                         @Nullable Object msg, @Nullable Throwable tr,
                                         @IntRange(from = 0) int stackPosition) {
        if (!IS_DEBUG_MODE) return;
        synchronized (TAG) {
            if (stackPosition < 0) stackPosition = mStackPosition;
            if (level < Log.VERBOSE || level > Log.ASSERT) level = Log.ERROR;
            Thread thread = Thread.currentThread();
            StackTraceElement[] stes = thread.getStackTrace();
            String pre = getStackTraceElementInfo(stes[Math.min(stes.length - 1, stackPosition)]);
            String msgS = String.valueOf(msg);
            //if msg比较短 & 只有1行, 就和pre一起打印. 和tr无关
            boolean msgWithTop = msgS.length() <= MAX_LEN && msgS.indexOf(LINE_SEP) < 0;
            //if msg没有和pre一起打印 || tr!=null, 就打印虚线
            boolean printTopLines = !msgWithTop || tr != null;
            if (printTopLines) print2Console(level, TOP_LINE);
            SB.ensureCapacity(LEFT_BORDER.length() + thread.getName().length() + 2 + pre.length() + (msgWithTop ? 1 + msgS.length() : 0));
            SB.append(LEFT_BORDER).append(thread.getName()).append(", ").append(pre);
            if (msgWithTop) SB.append(" ").append(msgS);
            print2Console(level, SB.toString());
            SB.setLength(0);
            if (printTopLines) {
                print2Console(level, TOP_LINE2);
            } else return;
            //Log message
            if (!msgWithTop) {
                int start = 0, end, segEnd;
                while ((end = msgS.indexOf(LINE_SEP, start)) != -1) {
                    segEnd = end;
                    if (end > 0 && msgS.charAt(end - 1) == '\r') segEnd = end - 1;
                    printMaxLen(level, msgS, start, segEnd);
                    start = end + 1;
                }
                printMaxLen(level, msgS, start, msgS.length());
            }
            //Log throwable
//            String stackTraceString = Log.getStackTraceString(tr);
            if (tr != null) {
                if (!msgWithTop) print2Console(level, TOP_LINE2);
                print2Console(level, SB.append(LEFT_BORDER).append(tr.toString()).toString());
                SB.setLength(0);
                for (StackTraceElement ste : tr.getStackTrace()) {
                    SB.append(LEFT_BORDER).append("\tat ").append(getStackTraceElementInfo(ste));
                    print2Console(level, SB.toString());
                    SB.setLength(0);
                }
            }
            print2Console(level, BOTTOM_LINE);
        }
    }

    protected static void print2Console(@IntRange(from = Log.VERBOSE, to = Log.ASSERT) int level, @NonNull String msg) {
        Log.println(level, TAG, msg);
    }

    /**
     * 获取堆栈跟踪信息
     * @param ste 堆栈跟踪元素
     * @return
     */
    @NonNull
    protected static String getStackTraceElementInfo(StackTraceElement ste) {
        if (ste == null) return "";
        //                   ClassName : com.actor.xxx.LogUtils,    MethodName: onCreate
        SB2.append(ste.getClassName()).append(".").append(ste.getMethodName());
        if (ste.isNativeMethod()) {
            SB2.append("(Native Method)");
        } else if (ste.getFileName() != null) {
            /**
             * @param fileName 文件名, eg: LogUtils.java <br />
             *                 1.即使写个项目中(不是在jar/aar中)不相关的文件名, 也能跳转... <br />
             *                 2.如果项目中(不是在jar/aar中)有2个同名文件, 点击的时候会弹框自选跳转... <br />
             *                 3.如果文件名固定写成jar/aar中的Xxx.java, 没有点击效果
             */
            if (ste.getLineNumber() >= 0) {
                SB2.append("(").append(ste.getFileName()).append(":").append(ste.getLineNumber()).append(")");
            } else {
                SB2.append("(").append(ste.getFileName()).append(")");
            }
        } else {
            if (ste.getLineNumber() >= 0) {
                SB2.append("(Unknown Source:").append(ste.getLineNumber()).append(")");
            } else {
                SB2.append("(Unknown Source)");
            }
        }
        String value = SB2.toString();
        SB2.setLength(0);
        return value;
    }

    /**
     * 按照 <code>MAX_LEN</code> 打印没有 <code>\n</code> 的子字符串
     * @param str 子字符串
     */
    protected static void printMaxLen(@IntRange(from = Log.VERBOSE, to = Log.ASSERT) int level, String str, int start, int end) {
        if (start == end) {
            print2Console(level, LEFT_BORDER);
            return;
        }
        while (start < end) {
            int end2 = Math.min(start + MAX_LEN, end);
            print2Console(level, SB2.append(LEFT_BORDER).append(str, start, end2).toString());
            SB2.setLength(0);
            start = end2;
        }
    }
}
