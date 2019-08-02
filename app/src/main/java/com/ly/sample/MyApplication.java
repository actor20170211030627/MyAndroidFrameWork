package com.ly.sample;

import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.LogUtils;

import okhttp3.OkHttpClient;

/**
 * Description: 类的描述
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/7/27 on 02:35
 */
public class MyApplication extends ActorApplication {

    //初始化okhttpclient
    @Override
    protected OkHttpClient getOkHttpClient(OkHttpClient.Builder builder) {
        return null;
    }

    @Override
    protected String getBaseUrl() {
        return null;
    }

    @Override
    protected void onUncaughtException(Thread thread, Throwable e) {
        LogUtils.formatError("onUncaughtException: thread=%s, e=%s", true, thread, e);
    }
}
