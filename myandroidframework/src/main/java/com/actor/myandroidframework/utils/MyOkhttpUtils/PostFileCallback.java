package com.actor.myandroidframework.utils.MyOkhttpUtils;

import com.actor.myandroidframework.utils.ThreadUtils;

import okhttp3.Request;

/**
 * Description: 上传文件
 * Author     : 李大发
 * Date       : 2019/4/17 on 17:46
 */
public abstract class PostFileCallback<T> extends BaseCallback<T> {

    public PostFileCallback(Object tag) {
        super(tag);
    }

    @Override
    public final void onBefore(Request request, int id) {//其实运行在子线程,加了final,子类不能重写此方法
        super.onBefore(request, id);
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onBeforeTransmit(request, id);
            }
        });
    }

    public void onBeforeTransmit(Request request, int id){//可重写此方法, 运行在主线程了
    }

    @Override
    public void inProgress(float progress, long total, int id) {//UI Thread
        super.inProgress(progress, total, id);
//        logFormat("上传文件: progress=%f, total=%d, id=%d", progress, total, id);
    }
}
