package com.actor.myandroidframework.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Kevin.
 * Changed by actor.
 * @version 1.0
 */
public class ToastUtils {

    private static final boolean isDebugMode = ConfigUtils.isDebugMode;
    private static Toast toast;
    private static Context context = ConfigUtils.APPLICATION;

    private ToastUtils() {//私有构造函数,防止创建本类对象
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Toast getToast(CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else toast.setText(text);//防止多个Toast重叠一直显示
        return toast;
    }

    /**
     * 本方法能防止了一直调用本方法后多个Toast重叠一直显示很长时间的问题
     */
    public static void show(final CharSequence text) {
        if (ThreadUtils.isRunOnUiThread()) {
            getToast(text).show();
        } else {
            ThreadUtils.handler.post(() -> {
                getToast(text).show();//当Looper轮询到此任务时, 会在主线程运行此方法
            });
        }
    }

    /**
     * 富文本 & 顶部 Toast 示例
     */
//    public static void showTop(Drawable icon, CharSequence text) {
//        SpannableStringBuilder ssb = new SpannableStringBuilder();
//        ssb.clear();
//        ssb.append(" ");
//        int width = icon.getIntrinsicWidth();
//        int height = icon.getIntrinsicHeight();
//        icon.setBounds(0, 0, width, height);//宽高
//        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);//ALIGN_BOTTOM(默认), ALIGN_BASELINE
//        ssb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ssb.append(text);
//        toast = getToast(ssb);
//        toast.setGravity(Gravity.TOP, 0, 0);//用于设置toast在屏幕的位置
//        show(ssb);
//    }

    /**
     * 这种Toast的方式不是单例的方式, 即:你连续按几次之后,几个Toast排队.show();
     */
    public static void showDefault(final CharSequence text) {
        if (ThreadUtils.isRunOnUiThread()) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            ThreadUtils.handler.post(() -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Toast解析Json过程中发生的异常
     * @param e
     * @param text release环境下toast的错误信息
     */
    public static void showJsonParseException(Exception e, CharSequence text) {
        if (isDebugMode && e != null) {
            StackTraceElement[] stackTrace = e.getStackTrace();//堆栈轨迹
            StackTraceElement stackTraceElement = stackTrace[0];
            //字节名, 包名 + 类名: com.google.package.activity.ActorBaseActivity
//            String className = stackTraceElement.getClassName();
            //获取文件名.即xxx.java
            String fileName = stackTraceElement.getFileName();
            String methodName = stackTraceElement.getMethodName();
            int lineNumber = stackTraceElement.getLineNumber();
            String stringFormat = TextUtil.getStringFormat("%s的%d行, 方法名:%s, 异常:%s", fileName, lineNumber, methodName, text);
            LogUtils.error(stringFormat, false);
            show(stringFormat);
//          }
        } else show(text);
    }
}
