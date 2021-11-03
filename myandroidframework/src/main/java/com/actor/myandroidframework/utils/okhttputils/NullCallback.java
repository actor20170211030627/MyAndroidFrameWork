package com.actor.myandroidframework.utils.okhttputils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * description: 不需要关心结果的回调
 *
 * @author : ldf
 * date       : 2020/7/3 on 15:48
 * @version 1.0
 */
/*public */
class NullCallback implements Callback {

    @Override
    public final void onFailure(@NotNull Call call, @NotNull IOException e) {
    }

    @Override
    public final void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
    }
}
