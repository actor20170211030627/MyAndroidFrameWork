package com.actor.myandroidframework.utils.okhttputils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * description: 不需要关心结果的回调
 *
 * @author : 李大发
 * date       : 2020/7/3 on 15:48
 * @version 1.0
 */
class NullCallback implements Callback {

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
    }
}
