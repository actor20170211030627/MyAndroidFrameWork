package com.actor.myandroidframework.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Description: 线程工具类,让程序运行在特定线程!
 * Copyright  : Copyright (c) 2015
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/3/19 on 19:01.
 * @version 1.0
 */
public class ThreadUtils {
    public static Handler handler = new Handler(Looper.getMainLooper());//handler.postDelayed

    /**
     * 通常用于处理UI操作
     */
    public static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            //new Thread(r).start();启动子线程(注意和下面方法的区别)
            runnable.run();//直接方法的调用
        } else {
            handler.post(runnable);//将runnable这个任务，丢到了主线程的消息队列中
        }
    }

    /**
     * 处理耗时操作,通常用于处理网络访问
     */
    public static void runOnSubThread(Runnable runnable) {
        new Thread(runnable).start();//启动子线程
    }

    /**
     * 返回现在是否运行在主线程
     * @return
     */
    public static boolean isRunOnUiThread(){
        return Looper.myLooper() == Looper.getMainLooper();

        //int mainThreadId = getMainThreadId();//使用线程id的比较
        //int currentThreadId = android.os.Process.myTid();//当前线程的id
        //return mainThreadId == currentThreadId;
    }
}
