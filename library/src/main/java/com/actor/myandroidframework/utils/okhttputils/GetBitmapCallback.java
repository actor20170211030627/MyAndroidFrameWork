package com.actor.myandroidframework.utils.okhttputils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 获取Bitmap
 * Author     : ldf
 * Date       : 2019/4/17 on 18:03
 */
public abstract class GetBitmapCallback extends BaseCallback<Bitmap> {

    public GetBitmapCallback(@Nullable LifecycleOwner tag) {
        super(tag);
    }

    public GetBitmapCallback(@Nullable LifecycleOwner tag, boolean isShowLoadingDialog) {
        super(tag, isShowLoadingDialog);
    }

    public GetBitmapCallback(@Nullable LifecycleOwner tag, boolean isShowLoadingDialog, int requestId) {
        super(tag, isShowLoadingDialog, false, requestId);
    }

    @Override
    public Bitmap parseNetworkResponse(Response response, int id) {
        if (response != null) {
            ResponseBody body = response.body();
            if (body != null) {
                return BitmapFactory.decodeStream(body.byteStream());
            }
        }
        return null;
    }
}
