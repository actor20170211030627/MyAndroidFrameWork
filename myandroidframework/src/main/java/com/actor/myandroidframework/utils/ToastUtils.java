package com.actor.myandroidframework.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

    //使用主线程looper初始化handler,保证handler发送的消息运行在主线程
    public static final Handler handler = new Handler(Looper.getMainLooper());

    //toast.setGravity(Gravity.CENTER, 0, 0);用于设置toast在屏幕的位置

    private ToastUtils() {//私有构造函数,防止创建本类对象
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 返回现在是否运行在主线程
     */
    private static boolean isRunOnUiThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static Toast getToast(CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else toast.setText(text);//防止多个Toast重叠一直显示
        return toast;
    }

    /**
     * 本方法能防止了一直调用本方法后多个Toast重叠一直显示很长时间的问题
     */
    public static void show(final CharSequence text) {
        if (isRunOnUiThread()) {
            getToast(text).show();
        } else {
            //子线程,handler.sendEmptyMessage(0);//handler发送一个消息任务给队列
            handler.post(new Runnable() {
                @Override
                public void run() {//当Looper轮询到此任务时, 会在主线程运行此方法
                    getToast(text).show();
                }
            });
        }
    }

    /**
     * 富文本 & 顶部 Toast 示例
     */
//    private static Toast toast1;
//    public static void showTop(CharSequence text) {
//        SpannableStringBuilder spanStringBuilder = new SpannableStringBuilder();
//        Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);//图片
//        int width = drawable.getIntrinsicWidth();
//        int height = drawable.getIntrinsicHeight();
//        drawable.setBounds(0, 0, width, height);//宽高
//        spanStringBuilder.clear();
//        spanStringBuilder.append(" ");
//        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);//ALIGN_BOTTOM(默认),ALIGN_BASELINE
//        spanStringBuilder.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spanStringBuilder.append(text);
//        toast1 = getToast(spanStringBuilder);
//        toast1.setGravity(Gravity.TOP, 0, 0);
//        if (isRunOnUiThread()) {
//            toast1.show();
//        } else {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    toast1.show();
//                }
//            });
//        }
//    }

    /**
     * 这种Toast的方式不是单例的方式,即:你连续按几次之后,几个Toast排队.show();
     */
    public static void showDefault(final CharSequence text) {
        if (isRunOnUiThread()) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            });
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
//          for (StackTraceElement stackTraceElement:stackTrace) {//有一些信息,通常取第一条
            StackTraceElement stackTraceElement = stackTrace[0];
//          stackTraceElement.getClassName();//包名+类名,示例:com.kuchuan.wisdompolice.activity.ActorBaseActivity
            String fileName = stackTraceElement.getFileName();//这个class的名称,示例:ActorBaseActivity.java
            String methodName = stackTraceElement.getMethodName();
            int lineNumber = stackTraceElement.getLineNumber();
            System.out.println(fileName + "的" +methodName + "方法" + lineNumber + "行,异常");
            show(fileName + "的" +methodName + "方法" + lineNumber + "行,异常");
//          }
        } else show(text);
    }
}
