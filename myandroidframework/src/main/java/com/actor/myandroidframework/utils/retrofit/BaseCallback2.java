package com.actor.myandroidframework.utils.retrofit;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ToastUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Description: retrofit2 的 Callback
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/5/9 on 10:27
 */
public abstract class  BaseCallback2<T> implements Callback<T> {

    private boolean isStatusCodeError = false;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onOk(call, response);
        } else {
            onStatusCodeError(response.code(), call, response);
            onFailure(call, new HttpException(response));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (isStatusCodeError) return;
        t.printStackTrace();
        logError("onError:" + t.getMessage());
        if (t.getClass() == SocketTimeoutException.class) {
            ToastUtils.show("连接服务器超时,请联系管理员或稍后重试!");
        } else if (t.getClass() == ConnectException.class) {
            ToastUtils.show("网络连接失败,请检查网络是否打开!");
        } else {
            //e.getMessage()示例:failed to connect to /111.11.111.111 (port 8080) after 10000ms
            ToastUtils.show("错误信息:".concat(t.getMessage()).concat(",请联系管理员!"));
        }
    }

    public abstract void onOk(Call<T> call, Response<T> response);

    /**
     * 自己方法, 状态码错误
     * @param errCode
     * @param response
     * @param id
     */
    public void onStatusCodeError(int errCode, Call<T> response, Response<T> id) {
        isStatusCodeError = true;
        String s = String.format(Locale.getDefault(), "错误码:%d,请联系管理员!", errCode);
        logError(s);
        ToastUtils.show(s);
    }

    protected static void logError(String msg) {
        LogUtils.Error(msg, false);
    }

    protected static void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }
}
