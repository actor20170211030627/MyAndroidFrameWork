package com.actor.picture_selector.utils;

import android.content.Context;

import com.actor.myandroidframework.utils.video.VideoProcessorUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2024/9/5 on 12
 * @version 1.0
 */
public class SelectCommonBuilder {
    protected PictureSelector selector;
    protected SelectCommonBuilder(PictureSelector selector) {
        this.selector = selector;
    }

    /**
     * 选择图片
     * @param isCompress 图片是否压缩
     */
    public SelectImageBuilder selectImage(boolean isCompress) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isCompress = isCompress;
        utils.selectionModel = selector.openGallery(SelectMimeType.ofImage());
        return new SelectImageBuilder();
    }

    /**
     * 选择视频 <br />
     * 如果要压缩视频, 需要自己手动调用代码压缩, 可使用:
     * @see VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)
     */
    public SelectVideoBuilder selectVideo() {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.selectionModel = selector.openGallery(SelectMimeType.ofVideo());
        return new SelectVideoBuilder();
    }

    /**
     * 选择图片&视频
     * @param isCompress 图片是否压缩 <br />
     * 如果要压缩视频, 需要自己手动调用代码压缩, 可使用:
     * @see VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)
     */
    public SelectImageBuilder selectImage$Video(boolean isCompress) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isCompress = isCompress;
        utils.selectionModel = selector.openGallery(SelectMimeType.ofAll());
        return new SelectImageBuilder();
    }

    /**
     * 选择音频. 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    public SelectAudioBuilder selectAudio() {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.selectionModel = selector.openGallery(SelectMimeType.ofAudio());
        utils.isShowOriginal = false;   //默认不 开启原图☑选项 (选择音频, 在这儿设置其实无用)
        return new SelectAudioBuilder();
    }

    /**
     * 拍照
     * @param isCompress 图片是否压缩
     */
    public TakePhotoBuilder takePhoto(boolean isCompress) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.isCompress = isCompress;
        utils.selectionCameraModel = selector.openCamera(SelectMimeType.ofImage());
        utils.isShowOriginal = false;   //默认不 开启原图☑选项 (拍照, 在这儿设置其实无用)
        return new TakePhotoBuilder();
    }

    /**
     * 录视频
     */
    public PictureSelectorUtils recordVideo() {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.selectionCameraModel = selector.openCamera(SelectMimeType.ofVideo());
        utils.isShowOriginal = false;   //默认不 开启原图☑选项 (录视频, 在这儿设置其实无用)
        return utils;
    }

    /**
     * 录音频
     */
    public PictureSelectorUtils recordAudio() {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.selectionCameraModel = selector.openCamera(SelectMimeType.ofAudio());
        utils.isShowOriginal = false;   //默认不 开启原图☑选项 (录音, 在这儿设置其实无用)
        return utils;
    }

    /**
     * 选择系统图库/视频/音频 (系统图库有些api不支持)
     * @param selectMimeType 选择文件类型: {@link SelectMimeType#ofAll()},
     * {@link SelectMimeType#ofImage()}, {@link SelectMimeType#ofVideo()}, {@link SelectMimeType#ofAudio()}
     */
    public PictureSelectorUtils selectSystemFile(int selectMimeType) {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        utils.selectionSystemModel = selector.openSystemGallery(selectMimeType);
        utils.isShowOriginal = false;   //默认不 开启原图☑选项 (系统选择, 在这儿设置其实无用)
        return utils;
    }

    /**
     * 预览图片/视频/音频
     */
    public PreviewConfiger openPreview() {
        PictureSelectorUtils utils = PictureSelectorUtils.getInstance(false);
        return new PreviewConfiger(selector.openPreview(), utils.selectionData);
    }
}
