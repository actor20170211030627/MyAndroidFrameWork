package com.actor.myandroidframework.utils.okhttputils.lifecycle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;

import java.util.ArrayList;

/**
 * description: MyOkHttpUtils网络请求的 LifecycleEventObserver <br />
 * Author     : ldf <br />
 * date       : 2021/10/16 on 19
 *
 * @version 1.0
 */
public class MyOkHttpLifecycleUtils {

    protected static final ArrayList<String> LIFE_OWNERS = new ArrayList<>();
    protected static final MyOkHttpUtilsLifecycleEventObserver OBSERVER = new MyOkHttpUtilsLifecycleEventObserver();

    /**
     * 添加观察者
     * @param lifecycleOwner AppCompatActivity 或者 androidx.Fragment 子类
     */
    public static void addObserver(@Nullable LifecycleOwner lifecycleOwner) {
        if (lifecycleOwner != null) {
            //如果这个lifecycleOwner没有添加过Observer
            if (!LIFE_OWNERS.contains(lifecycleOwner.getClass().getName())) {
                lifecycleOwner.getLifecycle().addObserver(OBSERVER);
            }
        }
    }

    protected static class MyOkHttpUtilsLifecycleEventObserver implements LifecycleEventObserver {
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                //删除记录
                LIFE_OWNERS.remove(source.getClass().getName());
                //移除监听
                source.getLifecycle().removeObserver(OBSERVER);
                //取消请求
                MyOkHttpUtils.cancelTag(source);
            }
        }
    }

    /**
     * 判断被观察者是否还存在
     */
    public static boolean isLifecycleAvailable(LifecycleOwner lifecycleOwner) {
        return lifecycleOwner != null && lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED;
    }
}
