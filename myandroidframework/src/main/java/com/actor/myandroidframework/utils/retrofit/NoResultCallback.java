package com.actor.myandroidframework.utils.retrofit;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description: 由于Call.enqueue(callback) 中callback不能=null, 所以写这个代替
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/8 on 17:52
 */
public class NoResultCallback extends BaseCallback2<Object> {


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
    public void onResponse(Call<Object> call, Response<Object> response) {
        if (isShowErrorInfo) super.onResponse(call, response);
    }

    @Override
    public void onFailure(Call<Object> call, Throwable t) {
        if (isShowErrorInfo) super.onFailure(call, t);
    }

    @Override
    public void onOk(Call call, Response response) {
    }
}
