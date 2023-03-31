package com.actor.myandroidframework.utils.picture_selector;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.engine.ImageEngine;

/**
 * description: 描述<br />
 * Author     : ldf<br />
 * date       : 2020/10/8 on 11
 *
 * @version 1.0
 */
public class ImageEngineImpl implements ImageEngine {

    @Override
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, String url, int maxWidth, int maxHeight) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void loadAlbumCover(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    //加载图片列表
    @Override
    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void pauseRequests(Context context) {

    }

    @Override
    public void resumeRequests(Context context) {

    }
}
