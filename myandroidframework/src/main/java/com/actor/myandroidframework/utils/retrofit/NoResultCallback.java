package com.actor.myandroidframework.utils.retrofit;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description: 由于Call.enqueue(callback) 中callback不能=null, 所以写这个代替
 * Author     : ldf
 * Date       : 2019/6/8 on 17:52
 * @version 1.1
 * @version 1.1.1 将泛型中的Object改成T
 */
public class NoResultCallback<T> extends BaseCallback2<T> {

    private boolean isToastErrorInfo = false;

    public NoResultCallback() {
        this(false);
    }

    /**
     * 是否显示错误信息, 包括 状态码 & 网络错误
     * @param isToastErrorInfo 如果出错, 是否toast
     */
    public NoResultCallback(boolean isToastErrorInfo) {
        //tag=null, 不会显示LoadingDialog
        super(null);
        this.isToastErrorInfo = isToastErrorInfo;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, Response<T> response) {
        if (isToastErrorInfo) super.onResponse(call, response);
    }

    @Override
    public void onError(Call<T> call, Throwable t) {
        if (isToastErrorInfo) super.onError(call, t);
    }

    @Override
    public void onOk(Call<T> call, Response<T> response, int id, boolean isRefresh) {
    }
}
