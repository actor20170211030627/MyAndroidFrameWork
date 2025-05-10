package com.actor.picture_selector.utils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2024/9/5 on 14
 * @version 1.0
 */
public class SelectVideoBuilder {

    /**
     * 选择视频 是否显示录制视频图标
     */
    public SelectVideoBuilder setShowCamera(boolean showCamera) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.showCamera = showCamera;
        return this;
    }

    /**
     * 视频 是否单选
     */
    public SelectVideoBuilder setSingleSelect(boolean singleSelect) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.singleSelect = singleSelect;
        return this;
    }

    /**
     * 设置多选的时候, 视频 最多选择多少个
     */
    public SelectVideoBuilder setMaxSelect(@IntRange(from = 2) int maxSelect) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.maxSelect = maxSelect;
        return this;
    }

    /**
     * 设置: "选择or录制 视频" 的时长
     * @param maxSecond 最大时长,单位秒. 不做限制可传0
     * @param minSecond 最小时长,单位秒. 不做限制可传0
     */
    public SelectVideoBuilder setSecondLimit(int maxSecond, int minSecond) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.maxSecond = maxSecond;
        utils.minSecond = minSecond;
        return this;
    }

    /**
     * 是否 开启原图☑选项
     */
    public SelectVideoBuilder setIsShowOriginal(boolean isShowOriginal) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isShowOriginal = isShowOriginal;
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
