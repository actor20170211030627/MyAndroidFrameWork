package com.actor.myandroidframework.utils.easyhttp;

import android.content.Context;

import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.BaseSubscriber;

import okhttp3.ResponseBody;

/**
 * description: EasyHttp回调基类, 封装"tag" & "id"
 *
 * @author : 李大发
 * date       : 2020/4/17 on 18:32
 * @version 1.0
 */
public abstract class BaseCallBack6<T> extends DownloadProgressCallBack<T> {

    //tag: 用于取消网络请求
    public Object tag;
    //id: 每一次请求都可以设置不同的id, 可用于标记请求(多个地方调用同一请求, 可根据id分辨到底是哪儿调用.)
    public    int     id;

    public BaseCallBack6(Object tag) {
        this.tag = tag;
    }

    public BaseCallBack6(Object tag, int id) {
        this.tag = tag;
        this.id = id;
    }

    /**
     * Called once the single upstream Disposable is set via onSubscribe.
     * 一旦通过onSubscribe设置了单个上游可支配资源，就会调用它。
     * @see io.reactivex.observers.DisposableObserver#onStart()
     */
    @Override
    public void onStart() {
    }

    /**
     * 2.在onStart()没有网络时直接onCompleted();
     *   or 请求完成
     * @see BaseSubscriber#onStart()
     */
    @Override
    public void onCompleted() {
        //Toast.makeText(context, "无网络，读取缓存数据", Toast.LENGTH_SHORT).show();
    }

    /**
     * 进度, 注意: 只有下载文件才有进度回调... 见:
     * @see com.zhouyou.http.subsciber.DownloadSubscriber#writeResponseBodyToDisk(String, String, Context, ResponseBody)
     * @see com.zhouyou.http.request.DownloadRequest#execute(CallBack)
     *
     * @param bytesRead 已下载大小
     * @param contentLength 总大小
     * @param done 是否已经完成
     */
    @Override
    public void update(long bytesRead, long contentLength, boolean done) {
//        float progress = bytesRead * 1.0f / contentLength;//下载进度
//        LogUtils.formatError("bytesRead = %d, contentLength = %d, progress = %f, done = %b",
//        bytesRead, contentLength, progress, done);
    }

    /**
     * 注意: 只有下载文件才有文件 path 回调
     * @param path 已下载完成的文件路径
     */
    @Override
    public void onComplete(String path) {
    }

    @Override
    public abstract void onSuccess(T t);

    @Override
    public void onError(ApiException e) {
    }
}
