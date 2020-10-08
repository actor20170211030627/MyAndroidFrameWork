package com.actor.myandroidframework.utils.okhttputils;

import androidx.annotation.Nullable;

import okhttp3.Request;

/**
 * description: 请求的时候, 不默认显示LoadingDialog
 *
 * @author : 李大发
 * date       : 2020/10/6 on 15
 * @version 1.0
 */
public abstract class BaseCallbackNotShowDialog<T> extends BaseCallback<T> {

    public BaseCallbackNotShowDialog(@Nullable Object tag) {
        super(tag);
    }

    public BaseCallbackNotShowDialog(@Nullable Object tag, int id) {
        super(tag, id);
    }

    public BaseCallbackNotShowDialog(@Nullable Object tag, boolean isRefresh) {
        super(tag, isRefresh);
    }

    //注释掉, 不显示LoadingDialog
    @Override
    public void onBefore(@Nullable Request request, int id) {
//        super.onBefore(request, id);
    }
}
