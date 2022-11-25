package com.actor.myandroidframework.utils.okhttputils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.ThreadUtils;

import okhttp3.Request;

/**
 * Description: 上传文件<br />
 * Author     : ldf<br />
 * Date       : 2019/4/17 on 17:46
 */
public abstract class PostFileCallback<T> extends BaseCallback<T> {

    public PostFileCallback(@Nullable LifecycleOwner tag) {
        super(tag);
    }

    public PostFileCallback(@Nullable LifecycleOwner tag, boolean isShowLoadingDialog) {
        super(tag, isShowLoadingDialog);
    }

    public PostFileCallback(@Nullable LifecycleOwner tag, boolean isShowLoadingDialog, int requestId) {
        super(tag, isShowLoadingDialog, false, requestId);
    }

    //其实运行在子线程,加了final,子类不能重写此方法
    @Override
    public final void onBefore(@Nullable Request request, int id) {
//        super.onBefore(request, id);
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onBeforeTransmit(request, id);
            }
        });
    }

    //开始请求前. 可重写此方法, 运行在主线程了
    public void onBeforeTransmit(@Nullable Request request, int id) {
        super.onBefore(request, id);
    }

    @Override
    public void inProgress(float progress, long total, int id) {//UI Thread
        super.inProgress(progress, total, id);
//        LogUtils.errorFormat("上传文件: progress=%f, total=%d, id=%d", progress, total, id);
    }
}
