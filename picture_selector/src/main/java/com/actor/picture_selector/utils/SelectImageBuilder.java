package com.actor.picture_selector.utils;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.luck.picture.lib.engine.CropFileEngine;

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
     * 选择图片 是否显示拍照图标
     */
    public SelectImageBuilder setShowCamera(boolean showCamera) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.showCamera = showCamera;
        return this;
    }

    /**
     * 图片 是否单选
     */
    public SelectImageBuilder setSingleSelect(boolean singleSelect) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.singleSelect = singleSelect;
        return this;
    }

    /**
     * 设置多选的时候, 图片 最多选择多少个
     */
    public SelectImageBuilder setMaxSelect(@IntRange(from = 2) int maxSelect) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.maxSelect = maxSelect;
        return this;
    }

    /**
     * 选择 图片 是否显示Gif
     */
    public SelectImageBuilder setShowGif(boolean showGif) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.showGif = showGif;
        return this;
    }

    /**
     * 是否裁剪图片
     */
    public SelectImageBuilder setCrop(boolean crop) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isCrop = crop;
        return this;
    }

    /**
     * 是否 开启原图☑选项
     */
    public SelectImageBuilder setIsShowOriginal(boolean isShowOriginal) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isShowOriginal = isShowOriginal;
        return this;
    }

    /**
     * 跳过裁剪图片的类型, 默认不裁剪Gif&Webp, 这2种都可能是动图.
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

    public PictureSelectorUtils build() {
        return PictureSelectorUtils.getInstance(false);
    }
}
