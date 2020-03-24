package com.actor.myandroidframework.utils.jpush;

import android.content.Context;

import com.actor.myandroidframework.utils.LogUtils;

import cn.jpush.android.service.WakedResultReceiver;

/**
 * description: http://docs.jiguang.cn/jpush/client/Android/android_api/#receiver_2
 * 自定义 Receiver 接收被拉起回调
 * 自定义一个Receiver组件，继承cn.jpush.android.service.WakedResultReceiver类,复写onWake(int
 * wakeType)或onWake(Context context, int wakeType)方法(注：开发者二选一复写)以监听被拉起,直接在
 * AndroidManifest配置即可。 详细配置参考 AndroidManifest 示例。
 *
 * author     : 李大发
 * date       : 2020/3/24 on 17:54
 *
 * @version 1.0
 */
public class MyWakedResultReceiver extends WakedResultReceiver {

    /**
     * @param wakeType 拉起的类型: START_SERVICE=1, BIND_SERVICE=2, CONTENTPROVIDER=4
     */
    @Override
    public void onWake(int wakeType) {
        super.onWake(wakeType);
        LogUtils.formatError("被拉起了, 拉起类型(wakeType)=%d", true, wakeType);
    }

    @Override
    public void onWake(Context context, int wakeType) {
        super.onWake(context, wakeType);
        LogUtils.formatError("被拉起了, context=%s, 拉起类型(wakeType)=%d", true, context, wakeType);
    }
}
