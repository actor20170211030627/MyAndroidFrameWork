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
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/5/9 on 10:27
 * @version 1.0.1
 */
public abstract class  BaseCallback2<T> implements Callback<T> {

    protected boolean isStatusCodeError = false;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onOk(call, response);
        } else {
            isStatusCodeError = true;
            onStatusCodeError(response.code(), call, response);
            onFailure(call, new HttpException(response));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        logFormat("onError: call=%s, throwable=%s, id=%d", call, t);
        if (isStatusCodeError) return;
        t.printStackTrace();
        if (t.getClass() == SocketTimeoutException.class) {
            toast("连接服务器超时,请联系管理员或稍后重试!");
        } else if (t.getClass() == ConnectException.class) {
            toast("网络连接失败,请检查网络是否打开!");
        } else {
            //e.getMessage()示例:failed to connect to /111.11.111.111 (port 8080) after 10000ms
            toast("错误信息:".concat(t.getMessage()).concat(",请联系管理员!"));
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
        logFormat("状态码错误: errCode=%d, response=%s, id=%d", errCode, response, id);
        toast(String.format(Locale.getDefault(), "错误码:%d,请联系管理员!", errCode));
    }

    protected static void logError(String msg) {
        LogUtils.Error(msg, false);
    }

    protected static void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    protected void toast(String msg) {
        ToastUtils.show(msg);
    }
}
