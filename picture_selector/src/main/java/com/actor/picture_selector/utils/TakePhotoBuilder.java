package com.actor.picture_selector.utils;

import androidx.annotation.Nullable;

import com.luck.picture.lib.engine.CropFileEngine;

/**
 * description: 拍照
 * company    :
 * @author    : ldf
 * date       : 2025/1/22 on 11:47
 */
public class TakePhotoBuilder {

    /**
     * 是否裁剪图片
     */
    public TakePhotoBuilder setCrop(boolean crop) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isCrop = crop;
        return this;
    }

    /**
     * 设置裁剪, if 不设置, 会使用默认的 {@link CropFileEngineImpl}
     */
    public TakePhotoBuilder setCropFileEngine(@Nullable CropFileEngine cropFileEngine) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.cropFileEngine = cropFileEngine;
        return this;
    }

    public PictureSelectorUtils build() {
        return PictureSelectorUtils.getInstance(false);
    }
}
