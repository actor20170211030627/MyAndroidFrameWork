package com.actor.myandroidframework.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Description: 线程工具类,让程序运行在特定线程!
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/3/19 on 19:01.
 * @version 1.0
 *
 * @deprecated 使用这个: {@link com.blankj.utilcode.util.ThreadUtils}
 */
@Deprecated
public class ThreadUtils {

    //使用主线程looper初始化handler,保证handler发送的消息运行在主线程
    public static final Handler HANDLER = com.blankj.utilcode.util.ThreadUtils.getMainHandler();

    /**
     * 将任务运行到主线程, 通常用于处理UI操作
     */
    public static void runOnUiThread(Runnable runnable) {
        com.blankj.utilcode.util.ThreadUtils.runOnUiThread(runnable);
    }

    /**
     * 在"主线程"中延时任务
     * 注意: 如果在Activity/Fragment/Dialog等有生命周期的类中延时, 需要在run方法中判断这个类是否已经退出,
     *       如果已经被销毁就应该不要再调用run里面的代码, 否则可能引起异常.
     * @param delayMillis 延时多少毫秒后运行
     */
    public static void runOnUiThreadDelayed(Runnable runnable, long delayMillis) {
        com.blankj.utilcode.util.ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis);
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
    public static boolean isMainThread() {
        return com.blankj.utilcode.util.ThreadUtils.isMainThread();

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


    ///////////////////////////////////////////////////////////////////////////
    // 返回线程安全的 List, Map, Set, SortedMap, SortedSet
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 返回指定 collection 支持的同步（线程安全的）collection
     */
    public static <T> Collection<T> synchronizedCollection(Collection<T> c) {
        return Collections.synchronizedCollection(c);
    }

    /**
     * 返回指定列表支持的同步（线程安全的）列表
     */
    public static <T> List<T> synchronizedCollection(List<T> list) {
        return Collections.synchronizedList(list);
    }

    /**
     * 返回由指定映射支持的同步（线程安全的）映射
     */
    public static <K,V> Map<K,V> synchronizedMap(Map<K,V> m) {
        return Collections.synchronizedMap(m);
    }

    /**
     * 返回指定 set 支持的同步（线程安全的）set
     */
    public static <T> Set<T> synchronizedSet(Set<T> s) {
        return Collections.synchronizedSet(s);
    }

    /**
     * 返回指定有序映射支持的同步（线程安全的）有序映射
     */
    public static <K,V> SortedMap<K,V> synchronizedSortedMap(SortedMap<K,V> m) {
        return Collections.synchronizedSortedMap(m);
    }

    /**
     * 返回指定有序 set 支持的同步（线程安全的）有序 set
     */
    public static <T> SortedSet<T> synchronizedSortedSet(SortedSet<T> s) {
        return Collections.synchronizedSortedSet(s);
    }
}
