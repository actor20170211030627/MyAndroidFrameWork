package com.actor.picture_selector.utils;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

/**
 * description: 选择图片
 * company    :
 *
 * @author : ldf
 * date       : 2024/9/5 on 14
 * @version 1.0
 */
public class SelectImageBuilder {

    /**
     * 选择图片/视频 是否压缩, 默认 false <br />
     * 如果要压缩视频, 需要在回调后自己手动调用代码压缩, 可使用:
     * @see com.actor.myandroidframework.utils.video.VideoProcessorUtils#compressVideo(Context, String, com.actor.myandroidframework.utils.video.VideoProcessorUtils.OnCompressListener)
     */
    public SelectImageBuilder setCompress(boolean isCompress) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isCompress = isCompress;
        return this;
    }

    /**
     * 选择图片 是否显示拍照图标, 默认 true
     */
    public SelectImageBuilder setShowCamera(boolean showCamera) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.showCamera = showCamera;
        return this;
    }

    /**
     * 图片 是否单选, 默认 false
     */
    public SelectImageBuilder setSingleSelect(boolean singleSelect) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.singleSelect = singleSelect;
        return this;
    }

    /**
     * 设置多选的时候, 图片 最多选择多少个, 默认9
     */
    public SelectImageBuilder setMaxSelect(@IntRange(from = 2) int maxSelect) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.maxSelect = maxSelect;
        return this;
    }

    /**
     * 选择图片 是否显示Gif, 默认 true
     */
    public SelectImageBuilder setShowGif(boolean showGif) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.showGif = showGif;
        return this;
    }

    /**
     * 是否裁剪图片, 默认 false
     */
    public SelectImageBuilder setCrop(boolean crop) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isCrop = crop;
        return this;
    }

    /**
     * 是否 开启原图☑选项, 默认 true
     */
    public SelectImageBuilder setIsShowOriginal(boolean isShowOriginal) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isShowOriginal = isShowOriginal;
        return this;
    }

    /**
     * 跳过裁剪图片的类型, 默认不裁剪Gif&Webp, 这2种都可能是动图.
     * @param skipCropMimeTypes {@link PictureMimeType#ofGIF()}, {@link PictureMimeType#ofWEBP()}
     */
    public SelectImageBuilder setSkipCropMimeTypes(@Nullable String... skipCropMimeTypes) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.skipCropMimeTypes = skipCropMimeTypes;
        return this;
    }

    /**
     * 设置裁剪, if 不设置, 会使用默认的 {@link CropFileEngineImpl}
     */
    public SelectImageBuilder setCropFileEngine(@Nullable CropFileEngine cropFileEngine) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.cropFileEngine = cropFileEngine;
        return this;
    }

    /**
     * 跳转选择
     * @param listener 回调
     */
    public void forResult(@NonNull OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectorUtils.getInstance(false).forResult(listener);
    }
}
