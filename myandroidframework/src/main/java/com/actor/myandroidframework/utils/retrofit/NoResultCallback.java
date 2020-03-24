package com.actor.myandroidframework.utils.retrofit;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description: 由于Call.enqueue(callback) 中callback不能=null, 所以写这个代替
 * Author     : 李大发
 * Date       : 2019/6/8 on 17:52
 * @version 1.1
 * @version 1.1.1 将泛型中的Object改成T
 */
public class NoResultCallback<T> extends BaseCallback2<T> {


    private boolean isShowErrorInfo = false;

    public NoResultCallback() {
    }

    /**
     * 是否显示错误信息, 包括 状态码 & 网络错误
     * @param isShowErrorInfo
     */
    public NoResultCallback(boolean isShowErrorInfo) {
        this.isShowErrorInfo = isShowErrorInfo;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (isShowErrorInfo) super.onResponse(call, response);
    }

    @Override
    public void onError(Call<T> call, Throwable t) {
        super.onError(call, t);
        if (isShowErrorInfo) super.onError(call, t);
    }

    @Override
    public void onOk(Call call, Response response) {
    }
}
