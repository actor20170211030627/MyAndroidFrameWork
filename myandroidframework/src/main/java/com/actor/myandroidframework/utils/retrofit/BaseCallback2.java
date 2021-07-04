package com.actor.myandroidframework.utils.retrofit;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.dialog.ShowLoadingDialogable;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.blankj.utilcode.util.ToastUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Description: retrofit2 的 Callback
 * Author     : ldf
 * Date       : 2019/5/9 on 10:27
 *
 * @version 1.0.2 修改Format错误导致崩溃问题 & 修改取消请求后, onFailure崩溃问题(增加call.isCanceled()判断)
 */
public abstract class BaseCallback2<T> implements Callback<T> {

    protected boolean isStatusCodeError     = false;
    protected boolean isShowedLoadingDialog = false;//本次请求LoadingDialog是否show
    private   int     requestId;
    protected boolean thisRequestIsRefresh  = false;//这次请求是否是(下拉)刷新
    public    Object  tag;

    public BaseCallback2(@Nullable Object tag) {
        this(tag, 0, false);
    }

    public BaseCallback2(@Nullable Object tag, int requestId) {
        this(tag, requestId, false);
    }

    public BaseCallback2(@Nullable Object tag, boolean isRefresh) {
        this(tag, 0, isRefresh);
    }

    /**
     * @param tag 如果 tag instanceof ShowLoadingDialogAble, 会自动show/dismiss LoadingDialog.
     * @param requestId  1.可传入"List/RecyclerView"的position或item对应的id,
     *              当你在List/RecyclerView中多个item"同时请求"时, 这个requestId可用于区别你这次请求是哪一个item发起的.
     *            2.也可用于需要"同时上传"多个文件, 但每次只能上传一个文件的情况. 传入文件对应的position,
     *              当上传成功后, 就可根据这个requestId判断是上传哪一个文件.
     * @param isRefresh 下拉刷新 or 上拉加载, 可用于列表请求时, 标记这次请求
     */
    public BaseCallback2(@Nullable Object tag, int requestId, boolean isRefresh) {
        this.tag = tag;
        this.requestId = requestId;
        this.thisRequestIsRefresh = isRefresh;
        onBefore(requestId);
    }

    /**
     * 开始请求, 默认显示LoadingDialog. 如果不想显示或自定义, 请重写此方法
     */
    public void onBefore(int requestId) {
        if (tag instanceof ShowLoadingDialogable) {
            ((ShowLoadingDialogable) tag).showLoadingDialog();
            isShowedLoadingDialog = true;
        }
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onOkDismissLoadingDialog(requestId);
            onOk(call, response, requestId, thisRequestIsRefresh);
        } else {
            isStatusCodeError = true;
            onStatusCodeError(response.code(), call, response);
            onError(call, new HttpException(response));//主要作用是调用子类的onError方法
        }
    }

    /**
     * 请求成功回调
     * @param response 返回对象
     * @param requestId 本次回调标记, 见构造方法: {@link BaseCallback2(Object, int)}
     * @param isRefresh 这次请求是否是(下拉)刷新, 需要在构造方法中传入: {@link BaseCallback2(Object, boolean)}
     */
    public abstract void onOk(Call<T> call, Response<T> response, int requestId, boolean isRefresh);

    /**
     * 请求成功后, 默认dismissLoadingDialog. 如果你不想dismiss, 可重写本方法
     */
    public void onOkDismissLoadingDialog(int requestId) {
        if (isShowedLoadingDialog && tag instanceof ShowLoadingDialogable) {
            ((ShowLoadingDialogable) tag).dismissLoadingDialog();
        }
    }

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
        //请求出错, 默认隐藏LoadingDialog. 如果不想隐藏或自定义, 请重写此方法
        if (isShowedLoadingDialog && tag instanceof ShowLoadingDialogable) {
            ((ShowLoadingDialogable) tag).dismissLoadingDialog();
        }
        if (isStatusCodeError) return;
        if (t instanceof SocketTimeoutException) {
            toast("连接服务器超时,请联系管理员或稍后再试!");
        } else if (t instanceof ConnectException) {
            toast("网络连接失败,请检查网络是否打开!");
        } else if (t != null) {
            toast("错误信息:".concat(t.getMessage()).concat(",请联系管理员!"));
        }
    }

    /**
     * 状态码错误, 默认会toast, 可以重写本方法
     */
    public void onStatusCodeError(int errCode, Call<T> call, Response<T> response) {
        logFormat("状态码错误: errCode=%d, call=%s, response=%s", errCode, call, response);
        toast(getStringFormat("状态码错误: %d", errCode));
    }

    public int getRequestId() {
        return requestId;
    }

    protected void logError(String msg) {
        LogUtils.error(false, msg);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(false, format, args);
    }

    protected String getStringFormat(String format, Object... args) {
        return TextUtils2.getStringFormat(format, args);
    }

    protected void toast(String msg) {
        ToastUtils.showShort(msg);
    }
}
