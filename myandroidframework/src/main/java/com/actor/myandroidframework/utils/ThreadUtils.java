package com.actor.myandroidframework.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Description: 线程工具类,让程序运行在特定线程!
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/3/19 on 19:01.
 * @version 1.0
 */
public class ThreadUtils {

    //使用主线程looper初始化handler,保证handler发送的消息运行在主线程
    public static final Handler handler = new Handler(Looper.getMainLooper());//handler.postDelayed

    /**
     * 将任务运行到主线程, 通常用于处理UI操作
     */
    public static void runOnUiThread(Runnable runnable) {
        if (isRunOnUiThread()) {
            runnable.run();//直接方法的调用
        } else {
            handler.post(runnable);//将runnable这个任务，丢到了主线程的消息队列中
        }
    }

    /**
     * 将任务运行到子线程, 处理耗时操作 ,通常用于处理网络访问
     */
    public static void runOnSubThread(Runnable runnable) {
        new Thread(runnable).start();//启动子线程
    }

    /**
     * @return 返回现在是否运行在主线程
     */
    public static boolean isRunOnUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();

//        return Thread.currentThread() == Looper.getMainLooper().getThread();
//        return getCurrentThreadId() == getMainThreadId();
    }

    /**
     * @return 返回主线程id
     */
    public static long getMainThreadId() {
        return Looper.getMainLooper().getThread().getId();
    }

    /**
     * @return 返回当前线程id
     */
    public static long getCurrentThreadId() {
        //获取线程名
//        String threadName = Thread.currentThread().getName();

        return Thread.currentThread().getId();
//        int processId = android.os.Process.myTid();//当前"进程"的id


    }
}
