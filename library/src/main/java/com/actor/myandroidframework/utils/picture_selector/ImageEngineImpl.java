package com.actor.myandroidframework.utils.picture_selector;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.listener.OnImageCompleteCallback;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

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
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, SubsamplingScaleImageView longImageView, OnImageCompleteCallback callback) {
        Glide.with(context).load(url).into(imageView);
    }

    //加载网络长图Adapter
    @Override
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, SubsamplingScaleImageView longImageView) {
        Glide.with(context).load(url).into(imageView);
    }

    //加载相册目录图片
    @Override
    public void loadFolderImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void loadAsGifImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).asGif().load(url).into(imageView);
    }

    //加载图片列表
    @Override
    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }
}
