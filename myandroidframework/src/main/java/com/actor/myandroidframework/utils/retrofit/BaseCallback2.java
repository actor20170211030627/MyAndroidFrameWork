package com.actor.myandroidframework.utils.retrofit;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.ToastUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

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
 * @version 1.0.2 修改Format错误导致崩溃问题 & 修改取消请求后, onFailure崩溃问题(增加call.isCanceled()判断)
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
            onError(call, new HttpException(response));//主要作用是调用子类的onError方法
        }
    }

    public abstract void onOk(Call<T> call, Response<T> response);

    /**
     * 请求出错
     * 为何是final? 因为:
     * 如果是调用call.cancel();主动取消请求并且退出了页面的话,
     * 这个onError方法还是会调用, 如果你在ui层重写了这个onError方法并且做了ui修改并且没判断是否cancel的话,
     * 那么很可能会造成错误!
     * 所以这个方法修饰了final(子类不能重写).
     * 如果要重写, 请重写这个方法: {@link #onError(Call, Throwable)}
     */
    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        logFormat("onError: call=%s, throwable=%s", call, t);
        if (call == null || call.isCanceled() || t == null) return;
        onError(call, t);
    }

    public void onError(Call<T> call, Throwable t) {
        if (isStatusCodeError) return;
        if (t instanceof SocketTimeoutException) {
            toast("连接服务器超时,请联系管理员或稍后重试!");
        } else if (t instanceof ConnectException) {
            toast("网络连接失败,请检查网络是否打开!");
        } else {
            toast("错误信息:".concat(t.getMessage()).concat(",请联系管理员!"));
        }
    }

    /**
     * 状态码错误
     */
    public void onStatusCodeError(int errCode, Call<T> call, Response<T> response) {
        logFormat("状态码错误: errCode=%d, call=%s, response=%s", errCode, call, response);
        toast(TextUtil.getStringFormat("错误码:%d,请联系管理员!", errCode));
    }

    protected void logError(String msg) {
        LogUtils.error(msg, false);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    protected void toast(String msg) {
        ToastUtils.show(msg);
    }
}
