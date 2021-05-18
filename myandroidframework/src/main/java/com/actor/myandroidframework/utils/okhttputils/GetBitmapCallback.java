package com.actor.myandroidframework.utils.okhttputils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 获取Bitmap
 * Author     : ldf
 * Date       : 2019/4/17 on 18:03
 */
public abstract class GetBitmapCallback extends BaseCallback<Bitmap> {

    public GetBitmapCallback(Object tag) {
        super(tag);
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        super.inProgress(progress, total, id);
//        logFormat("获取Bitmap: progress=%f, total=%d, id=%d", progress, total, id);
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
