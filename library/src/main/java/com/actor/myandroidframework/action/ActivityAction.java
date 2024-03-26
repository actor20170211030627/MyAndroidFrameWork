package com.actor.myandroidframework.action;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/03/08
 *    desc   : Activity 相关意图
 */
public interface ActivityAction {

    /**
     * 获取 Context 对象
     */
    Context getContext();

    /**
     * 获取 Activity 对象
     */
    @Nullable
    default Activity getActivity() {
        Context context = getContext();
        do {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }

    /**
     * 跳转 Activity 简化版
     */
    default void startActivity(@NonNull Class<? extends Activity> clazz) {
        startActivity(new Intent(getContext(), clazz));
    }

    /**
     * 跳转 Activity
     */
    default void startActivity(@NonNull Intent intent) {
        if (!(getContext() instanceof Activity)) {
            // 如果当前的上下文不是 Activity，调用 startActivity 必须加入新任务栈的标记，否则会报错：android.util.AndroidRuntimeException
            // Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        getContext().startActivity(intent);
    }
}