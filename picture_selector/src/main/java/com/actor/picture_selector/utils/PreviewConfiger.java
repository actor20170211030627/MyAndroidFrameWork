package com.actor.picture_selector.utils;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.luck.picture.lib.basic.PictureSelectionPreviewModel;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.language.LanguageConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 预览图片/视频/音频 (视频没有进度条) (不包括网络视频)
 *
 * @author : ldf
 * date       : 2023/4/2 on 13
 * @version 1.0
 */
public class PreviewConfiger {

    protected PictureSelectionPreviewModel selectionPreviewModel;
    protected ArrayList<LocalMedia> selectionData;
    protected OnExternalPreviewEventListener externalPreviewEventListener;

    //预览缩放效果模式(共享元素动画)
    protected ViewGroup list$recy;
    protected boolean isPreviewFullScreenMode = true;
    protected boolean isAutoVideoPlay = true;
    protected boolean isHidePreviewDownload = true;

    /**
     * @param selectionDatas 预览集合, if这儿传null, 那你需要在其它地方传入数据.
     *                       为何一定要ArrayList, 可能怕Array.asList()增加&删除的时候报错...
     */
    public PreviewConfiger(PictureSelectionPreviewModel selectionPreviewModel, @Nullable List<LocalMedia> selectionDatas) {
        this.selectionPreviewModel = selectionPreviewModel;
        if (selectionDatas instanceof ArrayList) {
            this.selectionData = (ArrayList<LocalMedia>) selectionDatas;
        } else if (selectionDatas != null && !selectionDatas.isEmpty()) {
            this.selectionData = new ArrayList<>(selectionDatas);
        }
    }

    /**
     * 预览缩放效果模式(共享元素动画)
     * @param list$recy 传入ListView 或 RecyclerView
     */
    public PreviewConfiger setPreviewZoomEffect(@Nullable ViewGroup list$recy) {
        if (list$recy instanceof ListView || list$recy instanceof RecyclerView) {
            this.list$recy = list$recy;
        } else {
            this.list$recy = null;
        }
        return this;
    }

    /**
     * 全屏预览(点击的时候)(仅对图片有效), 默认true
     * @param isPreviewFullScreenMode 预览的时候是否全屏
     */
    public PreviewConfiger setIsPreviewFullScreenMode(boolean isPreviewFullScreenMode) {
        this.isPreviewFullScreenMode = isPreviewFullScreenMode;
        return this;
    }

    /**
     * 自动播放视频(点击视频预览的时候才自动播放, 预览左右滑到时不会自动部分), 默认true
     * @param isAutoVideoPlay 是否自动播放
     */
    public PreviewConfiger setIsAutoVideoPlay(boolean isAutoVideoPlay) {
        this.isAutoVideoPlay = isAutoVideoPlay;
        return this;
    }

    /**
     * 是否隐藏"下载"按钮, 默认true
     * @param isHidePreviewDownload 是否隐藏"下载"按钮
     */
    public PreviewConfiger setIsHidePreviewDownload(boolean isHidePreviewDownload) {
        this.isHidePreviewDownload = isHidePreviewDownload;
        return this;
    }

    /**
     * @param externalPreviewEventListener 外部预览监听事件(删除, 长按下载), if不关心这些事件, 可传null
     */
    public PreviewConfiger setPreviewEventListener(@Nullable OnExternalPreviewEventListener externalPreviewEventListener) {
        this.externalPreviewEventListener = externalPreviewEventListener;
        return this;
    }

    /**
     * 开始预览
     */
    public void preview(int currentPosition, boolean isDisplayDelete) {
        preview(currentPosition, isDisplayDelete, defaultConfigPictureSelectionPreviewModel(selectionPreviewModel));
    }

    /**
     * 开始预览
     * @deprecated 传入自定义配置后调用.
     */
    @Deprecated
    public void preview(int currentPosition, boolean isDisplayDelete, @NonNull PictureSelectionPreviewModel selectionPreviewModel) {
        if (selectionData == null || selectionData.isEmpty()) {
            return;
        }
        selectionPreviewModel.startActivityPreview(currentPosition, isDisplayDelete, selectionData);
//        selectionPreviewModel.startFragmentPreview(currentPosition, isDisplayDelete, selectionData);
//        selectionPreviewModel.startFragmentPreview(PictureSelectorPreviewFragment.newInstance(), currentPosition, isDisplayDelete, selectionData);
        //
        this.selectionPreviewModel = null;
        this.selectionData = null;
        this.externalPreviewEventListener = null;
    }

    /**
     * 默认配置
     * @deprecated 获取自定义配置
     */
    @Deprecated
    public PictureSelectionPreviewModel defaultConfigPictureSelectionPreviewModel(@Nullable PictureSelectionPreviewModel selectionPreviewModel) {
        if (selectionPreviewModel == null) {
            selectionPreviewModel = this.selectionPreviewModel;
        }
        selectionPreviewModel
                .isPreviewFullScreenMode(isPreviewFullScreenMode)   //全屏预览(点击的时候)(仅对图片有效)
                .isAutoVideoPlay(isAutoVideoPlay)                   //自动播放视频(点击视频预览的时候才自动播放, 预览左右滑到时不会自动部分)
                .isHidePreviewDownload(isHidePreviewDownload)       //是否隐藏"下载"按钮
        ;
        //预览缩放效果模式(共享元素动画)
        if (list$recy != null) {
            selectionPreviewModel.isPreviewZoomEffect(true, list$recy);
//            selectionPreviewModel.isPreviewZoomEffect(isPreviewZoomEffect, true, list$recy);
        }
        selectionPreviewModel
                .isLoopAutoVideoPlay(false)     //循环播放
                //It is forbidden to correct or synchronize the width and height of the video 禁止更正或同步视频的宽度和高度
                .isSyncWidthAndHeight(true)
                .isVideoPauseResumePlay(true)   //视频暂停与继续(false: 播放后点击视频会退出预览)

                .setDefaultLanguage(LanguageConfig.SYSTEM_LANGUAGE)
//                .setAttachViewLifecycle()     //View lifecycle listener
                .setImageEngine(PictureSelectorUtils.getImageEngine())
//                .setCustomLoadingListener()   //自定义预览Dialog
                .setLanguage(LanguageConfig.UNKNOWN_LANGUAGE)
                .setExternalPreviewEventListener(externalPreviewEventListener)  //外部预览监听事件(删除, 长按下载)
//                .setInjectActivityPreviewFragment()   //startActivityPreview(); Preview mode, custom preview callback
//                .setInjectLayoutResourceListener()    //拦截自定义注入布局事件，用户可以在视图ID必须一致的前提下实现自己的布局
                .setSelectorUIStyle(PictureSelectorUtils.getPictureSelectorStyle())
                .setVideoPlayerEngine(null)             //默认MediaPlayer
        ;
        return selectionPreviewModel;
    }



    ///////////////////////////////////////////////////////////////////////////
    // 预览图片/视频(不包括网络视频)
    ///////////////////////////////////////////////////////////////////////////
    public PreviewConfiger setPath(@NonNull String path) {
        if (TextUtils.isEmpty(path)) return this;
        LocalMedia localMedia = LocalMedia.generateLocalMedia(ConfigUtils.APPLICATION, path);
        setLocalMedia(localMedia);
        return this;
    }
    public PreviewConfiger setLocalMedia(LocalMedia media) {
        if (media == null) return this;
        ArrayList<LocalMedia> medias = new ArrayList<>(1);
        medias.add(media);
        selectionData = medias;
        return this;
    }
    public PreviewConfiger setPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) return this;
        ArrayList<LocalMedia> medias = new ArrayList<>(paths.size());
        for (String path : paths) {
            if (!TextUtils.isEmpty(path)) {
                LocalMedia localMedia = LocalMedia.generateLocalMedia(ConfigUtils.APPLICATION, path);
                medias.add(localMedia);
            }
        }
        selectionData = medias;
        return this;
    }
}
