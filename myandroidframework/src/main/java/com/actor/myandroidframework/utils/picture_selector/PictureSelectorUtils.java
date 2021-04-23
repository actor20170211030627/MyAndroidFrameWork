package com.actor.myandroidframework.utils.picture_selector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.video.VideoProcessorUtils;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 图片/视频/音频 选择, 拍照, 裁剪, 压缩... from https://github.com/LuckSiege/PictureSelector
 * PictureSelector Api说明, 见 {@link #selectFile(List, OnResultCallbackListener)}
 * 注意: 需要在清单文件中添加权限:
 *  <!-- 拍照, 录视频 -->
 *  <uses-permission android:name="android.permission.CAMERA" />
 *  <!-- 音频 -->
 *  <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *  <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
 *
 * @author : 李大发
 * date       : 2020/10/7 on 18
 * @version 1.1.0
 */
public class PictureSelectorUtils {

    private static ImageEngine imageEngine;

    ///////////////////////////////////////////////////////////////////////////
    // Activity单选图片
    ///////////////////////////////////////////////////////////////////////////
    public static void selectImage(Activity activity, boolean showCamera, OnResultCallbackListener<LocalMedia> listener) {
        selectImage(activity, showCamera, true, listener);
    }
    public static void selectImage(Activity activity, boolean showCamera, boolean showGif,
                                   OnResultCallbackListener<LocalMedia> listener) {
        selectImage(activity, showCamera, showGif, null, listener);
    }
    public static void selectImage(Activity activity, boolean showCamera, boolean showGif,
                                   List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selectImage(activity, showCamera, showGif, false, selectionData, listener);
    }
    public static void selectImage(Activity activity, boolean showCamera, boolean showGif, boolean isCompress,
                                   List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getPictureSelectionModel(PictureSelector.create(activity),
                showCamera, showGif, isCompress, selectionData, true, 1);
        selectImage$s(model, listener);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Activity多选图片
    ///////////////////////////////////////////////////////////////////////////
    public static void selectImages(Activity activity, boolean showCamera, @IntRange(from = 2) int maxSelect, OnResultCallbackListener<LocalMedia> listener) {
        selectImages(activity, showCamera, true, maxSelect, listener);
    }
    public static void selectImages(Activity activity, boolean showCamera, boolean showGif,
                                    @IntRange(from = 2) int maxSelect, OnResultCallbackListener<LocalMedia> listener) {
        selectImages(activity, showCamera, showGif, null, maxSelect, listener);
    }
    public static void selectImages(Activity activity, boolean showCamera, boolean showGif,
                                    List<LocalMedia> selectionData, @IntRange(from = 2) int maxSelect,
                                    OnResultCallbackListener<LocalMedia> listener) {
        selectImages(activity, showCamera, showGif, false, selectionData, maxSelect, listener);
    }
    public static void selectImages(Activity activity, boolean showCamera, boolean showGif, boolean isCompress,
                                    List<LocalMedia> selectionData, @IntRange(from = 2) int maxSelect,
                                    OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getPictureSelectionModel(PictureSelector.create(activity),
                showCamera, showGif, isCompress, selectionData, false, maxSelect);
        selectImage$s(model, listener);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Fragment单选图片
    ///////////////////////////////////////////////////////////////////////////
    public static void selectImage(Fragment fragment, boolean showCamera, OnResultCallbackListener<LocalMedia> listener) {
        selectImage(fragment, showCamera, true, listener);
    }
    public static void selectImage(Fragment fragment, boolean showCamera, boolean showGif,
                                   OnResultCallbackListener<LocalMedia> listener) {
        selectImage(fragment, showCamera, showGif, null, listener);
    }
    public static void selectImage(Fragment fragment, boolean showCamera, boolean showGif,
                                   List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selectImage(fragment, showCamera, showGif, false, selectionData, listener);
    }
    public static void selectImage(Fragment fragment, boolean showCamera, boolean showGif, boolean isCompress,
                                   List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getPictureSelectionModel(PictureSelector.create(fragment),
                showCamera, showGif, isCompress, selectionData, true, 1);
        selectImage$s(model, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fragment多选图片
    ///////////////////////////////////////////////////////////////////////////
    public static void selectImages(Fragment fragment, boolean showCamera, @IntRange(from = 2) int maxSelect, OnResultCallbackListener<LocalMedia> listener) {
        selectImages(fragment, showCamera, true, maxSelect, listener);
    }
    public static void selectImages(Fragment fragment, boolean showCamera, boolean showGif,
                                    @IntRange(from = 2) int maxSelect, OnResultCallbackListener<LocalMedia> listener) {
        selectImages(fragment, showCamera, showGif, null, maxSelect, listener);
    }
    public static void selectImages(Fragment fragment, boolean showCamera, boolean showGif,
                                    List<LocalMedia> selectionData, @IntRange(from = 2) int maxSelect,
                                    OnResultCallbackListener<LocalMedia> listener) {
        selectImages(fragment, showCamera, showGif, false, selectionData, maxSelect, listener);
    }
    public static void selectImages(Fragment fragment, boolean showCamera, boolean showGif, boolean isCompress,
                                    List<LocalMedia> selectionData, @IntRange(from = 2) int maxSelect,
                                    OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getPictureSelectionModel(PictureSelector.create(fragment),
                showCamera, showGif, isCompress, selectionData, false, maxSelect);
        selectImage$s(model, listener);
    }

    /**
     * 单选/多选 图片
     * @param model 图片选择配置, 可自定义
     * @param listener 回调监听
     *
     *  @see LocalMedia#getPath();          // content://media/external/file/116272
     *  @see LocalMedia#getRealPath();      // /storage/emulated/0/news_article/a9f5fb.png
     *  @see LocalMedia#getAndroidQToPath();// null, 只有 "Version >= Android Q(Android 10.0, API Level 29)" 才返回
     *  @see LocalMedia#getCompressPath();  // null, "压缩"后才有值
     *  @see LocalMedia#getCutPath();       // null, "裁剪"后才有值
     *  @see LocalMedia#getOriginalPath();  // null, 选择"原图"后才有值
     */
    public static void selectImage$s(PictureSelectionModel model, OnResultCallbackListener<LocalMedia> listener) {
        model.forResult(/*requestCode, */listener);//结果回调分两种方式onActivityResult()和OnResultCallbackListener方式
    }

    /**
     * 获取默认的图片选择配置, 默认配置见: {@link PictureSelectionConfig#initDefaultValue()}
     * @param showCamera 是否显示相机
     * @param showGif 是否显示Gif图片
     * @param isCompress 是否压缩图片, 压缩后通过 {@link LocalMedia#getCompressPath()} 获取压缩后的图片地址
     * @param selectionData 传入已选图片(可同步勾选状态)
     * @param singleSelect 是否是单选
     * @param maxSelect 最多选几张图片(包括 selectionData 里的图片也会计算在内)
     */
    public static PictureSelectionModel getPictureSelectionModel(PictureSelector selector, boolean showCamera,
                                                                 boolean showGif, boolean isCompress,
                                                                 List<LocalMedia> selectionData, boolean singleSelect,
                                                                 int maxSelect) {
        PictureSelectionModel model = selector.openGallery(PictureMimeType.ofImage())//相册 媒体类型 PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
                .imageEngine(getImageEngine())
                .selectionMode(singleSelect ? PictureConfig.SINGLE : PictureConfig.MULTIPLE)//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
                .isCamera(showCamera)//列表是否显示拍照按钮
//                .imageFormat(PictureMimeType.JPEG)//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
//                .maxSelectNum(9)//最大选择数量,默认9张
//                .minSelectNum(0)// 最小选择数量
//                .imageSpanCount(4)//列表每行显示个数
//                .selectionMedia(selectionData)//@Deprecated 是否传入已选图片
                .selectionData(selectionData)
                .isGif(showGif)//是否显示gif, 默认false
                .isCompress(isCompress)//是否压缩图片, 默认false
                .synOrAsy(false);//同步true或异步false 压缩 默认同步
        //如果多选
        if (!singleSelect) {
            model.maxSelectNum(maxSelect);//最大选择数量,默认9张
        }

        //屏幕旋转方向, 默认: ActivityInfo.SCREEN_ORIENTATION_SENSOR
        model.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//屏幕旋转方向 ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ...
//                .isOriginalImageControl(false)//开启原图选项
                .isAutomaticTitleRecyclerTop(false)//图片列表超过一屏连续点击顶部标题栏快速回滚至顶部, 默认true
//                .setOutputCameraPath("")// 自定义相机输出目录只针对Android Q以下版本，具体参考Demo
        ;
        return model;
    }



    ///////////////////////////////////////////////////////////////////////////
    // Activity单选视频
    ///////////////////////////////////////////////////////////////////////////
    public static void selectVideo(Activity activity, boolean showCamera, OnResultCallbackListener<LocalMedia> listener) {
        selectVideo(activity, showCamera, null, listener);
    }
    public static void selectVideo(Activity activity, boolean showCamera, List<LocalMedia> selectionData,
                            OnResultCallbackListener<LocalMedia> listener) {
        selectVideo(activity, showCamera, selectionData, 0, 0, listener);
    }
    public static void selectVideo(Activity activity, boolean showCamera, List<LocalMedia> selectionData,
                            int maxSecond, int minSecond, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getVideoSelectionModel(PictureSelector.create(activity),
                showCamera, true, selectionData, 1, maxSecond, minSecond);
        selectVideo$s(model, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity多选视频
    ///////////////////////////////////////////////////////////////////////////
    public static void selectVideos(Activity activity, boolean showCamera, @IntRange(from = 2) int maxSelect, OnResultCallbackListener<LocalMedia> listener) {
        selectVideos(activity, showCamera, maxSelect, null, listener);
    }
    public static void selectVideos(Activity activity, boolean showCamera, @IntRange(from = 2) int maxSelect,
                             List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selectVideos(activity, showCamera, maxSelect, selectionData, 0, 0, listener);
    }
    public static void selectVideos(Activity activity, boolean showCamera, @IntRange(from = 2) int maxSelect, List<LocalMedia> selectionData,
                            int maxSecond, int minSecond, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getVideoSelectionModel(PictureSelector.create(activity),
                showCamera, false, selectionData, maxSelect, maxSecond, minSecond);
        selectVideo$s(model, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fragment单选视频
    ///////////////////////////////////////////////////////////////////////////
    public static void selectVideo(Fragment fragment, boolean showCamera, OnResultCallbackListener<LocalMedia> listener) {
        selectVideo(fragment, showCamera, null, listener);
    }
    public static void selectVideo(Fragment fragment, boolean showCamera, List<LocalMedia> selectionData,
                            OnResultCallbackListener<LocalMedia> listener) {
        selectVideo(fragment, showCamera, selectionData, 0, 0, listener);
    }
    public static void selectVideo(Fragment fragment, boolean showCamera, List<LocalMedia> selectionData,
                            int maxSecond, int minSecond, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getVideoSelectionModel(PictureSelector.create(fragment),
                showCamera, true, selectionData, 1, maxSecond, minSecond);
        selectVideo$s(model, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity多选视频
    ///////////////////////////////////////////////////////////////////////////
    public static void selectVideos(Fragment fragment, boolean showCamera, @IntRange(from = 2) int maxSelect, OnResultCallbackListener<LocalMedia> listener) {
        selectVideos(fragment, showCamera, maxSelect, null, listener);
    }
    public static void selectVideos(Fragment fragment, boolean showCamera, @IntRange(from = 2) int maxSelect,
                             List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selectVideos(fragment, showCamera, maxSelect, selectionData, 0, 0, listener);
    }
    public static void selectVideos(Fragment fragment, boolean showCamera, @IntRange(from = 2) int maxSelect, List<LocalMedia> selectionData,
                             int maxSecond, int minSecond, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getVideoSelectionModel(PictureSelector.create(fragment),
                showCamera, false, selectionData, maxSelect, maxSecond, minSecond);
        selectVideo$s(model, listener);
    }

    /**
     * 单选/多选 视频. 如果要压缩视频, 可使用:{@link VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)}
     * @param model 视频选择配置, 可自定义
     * @param listener 回调监听
     */
    public static void selectVideo$s(PictureSelectionModel model, OnResultCallbackListener<LocalMedia> listener) {
        model.forResult(/*requestCode, */listener);//结果回调分两种方式onActivityResult()和OnResultCallbackListener方式
    }

    /**
     * 获取默认的图片选择配置, 默认配置见: {@link PictureSelectionConfig#initDefaultValue()}
     * @param showCamera 是否显示相机
     * @param singleSelect 是否是单选
     * @param selectionData 传入已选视频(可同步勾选状态)
     * @param maxSelect 最多选几个视频(包括 selectionData 里的视频也会计算在内)
     * @param maxSecond 查询多少秒以内的视频, 默认0
     * @param minSecond 查询多少秒以上的视频, 默认0
     */
    public static PictureSelectionModel getVideoSelectionModel(PictureSelector selector, boolean showCamera, boolean singleSelect,
                                                               List<LocalMedia> selectionData, int maxSelect, int maxSecond,
                                                               int minSecond) {
        PictureSelectionModel selectionModel = selector.openGallery(PictureMimeType.ofVideo())//相册 媒体类型 PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
                .imageEngine(getImageEngine())
                .selectionMode(singleSelect ? PictureConfig.SINGLE : PictureConfig.MULTIPLE)//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
                .isCamera(showCamera)//列表是否显示拍照按钮
//                .isCompress()     //视频不支持压缩, 请自行压缩
                ;
        if (!singleSelect) {
            selectionModel.maxVideoSelectNum(maxSelect);//视频最大选择数量,默认4
        }
//                .minVideoSelectNum(0)//视频最小选择数量
        selectionModel.videoMaxSecond(maxSecond)// 查询多少秒以内的视频, 默认0
                .videoMinSecond(minSecond)// 查询多少秒以内的视频, 默认0
//                .selectionMedia(selectionData)//@Deprecated 是否传入已选图片
                .selectionData(selectionData)
//                .recordVideoSecond(60)//录制视频秒数 默认60s
//                .previewVideo(true)//是否预览视频

//                .queryMaxFileSize(-1)//查询指定大小内的图片、视频、音频大小，单位M

                //屏幕旋转方向, 默认: ActivityInfo.SCREEN_ORIENTATION_SENSOR
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//屏幕旋转方向 ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ...
//                .bindCustomPlayVideoCallback(null)//自定义视频播放拦截
//                .cameraFileName("")//自定义拍照文件名，如果是相册内拍照则内部会自动拼上当前时间戳防止重复
//                .isUseCustomCamera(false)// 开启自定义相机
//                .setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 自定义相机按钮状态,CustomCameraView.BUTTON_STATE_BOTH
                .isAutomaticTitleRecyclerTop(false)//图片列表超过一屏连续点击顶部标题栏快速回滚至顶部, 默认true
//                .setOutputCameraPath("")// 自定义相机输出目录只针对Android Q以下版本，具体参考Demo
        ;
        return selectionModel;
    }



    ///////////////////////////////////////////////////////////////////////////
    // Activity & Fragment拍照
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 拍照, 需要添加权限: <uses-permission android:name="android.permission.CAMERA" />
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    public static void takePhoto(Activity activity, OnResultCallbackListener<LocalMedia> listener) {
        takePhoto(activity, false, listener);
    }
    @RequiresPermission(Manifest.permission.CAMERA)
    public static void takePhoto(Activity activity, boolean isCompress, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getOpenCameraModel(PictureSelector.create(activity), true, isCompress, 0);
        openCamera(model, listener);
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public static void takePhoto(Fragment fragment, OnResultCallbackListener<LocalMedia> listener) {
        takePhoto(fragment, false, listener);
    }
    @RequiresPermission(Manifest.permission.CAMERA)
    public static void takePhoto(Fragment fragment, boolean isCompress, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getOpenCameraModel(PictureSelector.create(fragment), true, isCompress, 0);
        openCamera(model, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity & Fragment拍视频(默认60秒)
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 录视频, 需要添加权限:
     * <uses-permission android:name="android.permission.CAMERA" />
     * <uses-permission android:name="android.permission.RECORD_AUDIO" />
     * <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
     */
    @RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordVideo(Activity activity, OnResultCallbackListener<LocalMedia> listener) {
        recordVideo(activity, 60, listener);
    }

    @RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordVideo(Activity activity, int videoSecond, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getOpenCameraModel(PictureSelector.create(activity), false, false, videoSecond);
        openCamera(model, listener);
    }

    @RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordVideo(Fragment fragment, OnResultCallbackListener<LocalMedia> listener) {
        recordVideo(fragment, 60, listener);
    }

    @RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordVideo(Fragment fragment, int videoSecond, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelectionModel model = getOpenCameraModel(PictureSelector.create(fragment), false, false, videoSecond);
        openCamera(model, listener);
    }

    /**
     * 打开相机拍照. 如果要压缩视频, 可使用:{@link VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)}
     * @param model "拍照/录视频" 配置, 可自定义
     * @param listener 回调
     */
    public static void openCamera(PictureSelectionModel model, OnResultCallbackListener<LocalMedia> listener) {
        model.forResult(/*requestCode, */listener);//结果回调分两种方式onActivityResult()和OnResultCallbackListener方式
    }

    /**
     * 获取默认的 "拍照/录视频" 选择配置, 默认配置见: {@link PictureSelectionConfig#initDefaultValue()}
     * @param isTakePhoto 是拍照还是拍视频
     * @param isCompress 是否压缩图片, 压缩后通过 {@link LocalMedia#getCompressPath()} 获取压缩后的图片地址
     * @param videoSecond 如果是拍视频, 录制视频秒数
     */
    public static PictureSelectionModel getOpenCameraModel(PictureSelector selector, boolean isTakePhoto, boolean isCompress, int videoSecond) {
        PictureSelectionModel selectionModel = selector
                .openCamera(isTakePhoto ? PictureMimeType.ofImage() : PictureMimeType.ofVideo())//单独使用相机 媒体类型 PictureMimeType.ofImage()、ofVideo()
                .imageEngine(getImageEngine());

//                .imageFormat(PictureMimeType.JPEG)//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q

        if (isTakePhoto) {
            selectionModel.isCompress(isCompress)
                    .synOrAsy(false);//同步true或异步false 压缩 默认同步
        } else {
            selectionModel.recordVideoSecond(videoSecond);//录制视频秒数 默认60s
        }

        //屏幕旋转方向, 默认: ActivityInfo.SCREEN_ORIENTATION_SENSOR
        selectionModel.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//屏幕旋转方向 ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ...
//                .isOriginalImageControl(false)//开启原图选项
//                .bindCustomPlayVideoCallback(null)//自定义视频播放拦截
//                .bindCustomCameraInterfaceListener(null)//自定义拍照回调接口
//                .bindCustomPreviewCallback(null)// 自定义图片预览回调接口
//                .cameraFileName("")//自定义拍照文件名，如果是相册内拍照则内部会自动拼上当前时间戳防止重复
//                .renameCompressFile("")//自定义压缩文件名，多张压缩情况下内部会自动拼上当前时间戳防止重复
//                .renameCropFileName("")//自定义裁剪文件名，多张裁剪情况下内部会自动拼上当前时间戳防止重复
//                .isUseCustomCamera(false)// 开启自定义相机
//                .setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 自定义相机按钮状态,CustomCameraView.BUTTON_STATE_BOTH
//                .setOutputCameraPath("")// 自定义相机输出目录只针对Android Q以下版本，具体参考Demo
        ;
        return selectionModel;
    }



    ///////////////////////////////////////////////////////////////////////////
    // Activity & Fragment录音频(默认60秒)
    ///////////////////////////////////////////////////////////////////////////
    public static final int RECORD_REQUEST_CODE = 1001;
    /**
     * 录音
     * 1. 需要添加权限:
     * <uses-permission android:name="android.permission.RECORD_AUDIO" />
     * <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
     *
     * 2. 在onActivityResult中获取返回值:
     * if (requestCode == PictureSelectorUtils.RECORD_REQUEST_CODE && data != null) {
     *     File file = UriUtils.uri2File(data.getData();//获取录音文件
     * }
//     * @param selectionData 已选中文件, 这儿没用, 传null
//     * @param listener 返回监听, 这儿没用, 传null
     */
    @RequiresPermission(allOf = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordAudio(Activity activity/*, List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener*/) {
//        recordAudio(PictureSelector.create(activity), selectionData, listener);

        Intent intentRecord = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentRecord.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//对目标应用临时授权该Uri所代表的文件
        }
        activity.startActivityForResult(intentRecord, RECORD_REQUEST_CODE);
    }

    /**
     * 录音, 在onActivityResult中获取返回值:
     * if (requestCode == PictureSelectorUtils.RECORD_REQUEST_CODE && data != null) {
     *     File file = UriUtils.uri2File(data.getData();//获取录音文件
     * }
//     * @param selectionData 已选中文件, 这儿没用, 传null
//     * @param listener 返回监听, 这儿没用, 传null
     */
    @RequiresPermission(allOf = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordAudio(Fragment fragment/*, List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener*/) {
//        recordAudio(PictureSelector.create(fragment), selectionData, listener);

        Intent intentRecord = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentRecord.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//对目标应用临时授权该Uri所代表的文件
        }
        intentRecord.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        fragment.startActivityForResult(intentRecord, RECORD_REQUEST_CODE);
    }

    /**
     * 录音 TODO: 2021/2/8 代码有bug, 无返回值...
     * @param selectionData 已选中文件, 这儿没用, 传null
     * @param listener 返回监听, 这儿没用, 传null
     */
    protected static void recordAudio(PictureSelector selector, List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selector.openCamera(PictureMimeType.ofAudio())
//                .selectionMode(PictureConfig.SINGLE)
                .selectionData(selectionData)
                .forResult(listener);
    }

    /**
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    public static void selectAudio(Activity activity, @IntRange(from = 1) int maxSelect, List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selectAudio$s(PictureSelector.create(activity), maxSelect, selectionData, listener);
    }

    /**
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     * @param maxSelect 最多选几个音频(包括 selectionData 里的音频也会计算在内)
     * @param selectionData 传入已选音频(可同步勾选状态)
     * @param listener 回调监听
     */
    public static void selectAudio(Fragment fragment, @IntRange(from = 1) int maxSelect, List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selectAudio$s(PictureSelector.create(fragment), maxSelect, selectionData, listener);
    }

    protected static void selectAudio$s(PictureSelector selector, @IntRange(from = 1) int maxSelect, List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
        selector.openGallery(PictureMimeType.ofAudio())
                .selectionMode(maxSelect <= 1 ? PictureConfig.SINGLE : PictureConfig.MULTIPLE)//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
//                .selectionMedia(selectionData)//@Deprecated 是否传入已选音频
                .selectionData(selectionData)
//                .enablePreviewAudio(true)//是否预览音频
//                .isEnablePreviewAudio(true)
                .forResult(/*requestCode, */listener);//结果回调分两种方式onActivityResult()和OnResultCallbackListener方式
    }



    ///////////////////////////////////////////////////////////////////////////
    // 预览图片/视频
    ///////////////////////////////////////////////////////////////////////////
    public static void previewImageVideo(Activity activity, String path) {
        LocalMedia localMedia = new LocalMedia();
        localMedia.setPath(path);
        previewImageVideo(activity, localMedia);
    }
    public static void previewImageVideosS(Activity activity, int position, List<String> paths) {
        if (paths != null && !paths.isEmpty()) {
            List<LocalMedia> medias = new ArrayList<>();
            for (String path : paths) {
                if (!TextUtils.isEmpty(path)) {
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(path);
                    medias.add(localMedia);
                }
            }
            previewImageVideosL(activity, position, medias);
        }
    }
    public static void previewImageVideo(Activity activity, LocalMedia media) {
        List<LocalMedia> medias = new ArrayList<>();
        medias.add(media);
        previewImageVideosL(activity, 0, medias);
    }
    public static void previewImageVideosL(Activity activity, int position, List<LocalMedia> medias) {
        previewImageVideos(activity, false, position, medias);
    }
    /**
     * 预览多张图片/视频(不包括网络视频) 可自定长按保存路径
     * 注意 .themeStyle(R.style.theme)；里面的参数不可删，否则闪退...
     * @param longPressDownload 长按图片时是否显示下载对话框(仅对图片有效)
     * @param position 预览第几张图片
     * @param medias 图片集合, 如果是一张图片, 请务必传入: java.util.ArrayList<>();
     */
    public static void previewImageVideos(Activity activity, boolean longPressDownload, int position, List<LocalMedia> medias) {
        PictureSelector.create(activity)
                .themeStyle(R.style.picture_default_style)
                .isNotPreviewDownload(longPressDownload)         //预览不显示下载
//                .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .imageEngine(getImageEngine())
                .openExternalPreview(position, medias);
    }



    /**
     * 预览视频
     */
    public static void previewVideo(Activity activity, String path) {
        PictureSelector.create(activity)
//                .themeStyle(R.style.picture_default_style)
//                .isNotPreviewDownload(true)         //预览不显示下载
//                .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
//                .imageEngine(getImageEngine())
                .externalPictureVideo(path);
    }



    /**
     * 预览音频
     */
    public static void previewAudio(Activity activity, String path) {
        PictureSelector.create(activity)
                .externalPictureAudio(path);
    }



    /**
     * 清除指定类型缓存
     * 包括裁剪和压缩后的缓存，要在上传成功后调用，type 指的是图片or视频缓存取决于你设置的ofImage或ofVideo 注意：需要系统sd卡权限
     * @param type PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
     */
    @RequiresPermission(Manifest.permission_group.STORAGE)
    public static void clearCache(Context context, int type) {
        PictureFileUtils.deleteCacheDirFile(context, type);
    }



    /**
     * 清除所有缓存 例如：压缩、裁剪、视频、音频所生成的临时文件
     */
    public static void clearAll(Context context) {
        PictureFileUtils.deleteAllCacheDirFile(context);
    }



    /**
     * PictureSelector Api说明 https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
     * 括号内填的参数是 "默认参数"
     */
    private void selectFile(List<LocalMedia> selectionData, OnResultCallbackListener<LocalMedia> listener) {
//        PictureSelector.create(activity/fragment)
//                .openGallery("右侧选项->")//相册 媒体类型 PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
                //.openCamera()//单独使用相机 媒体类型 PictureMimeType.ofImage()、ofVideo()

//                .theme(R.style.picture_default_style)// xml样式配制 R.style.picture_default_style、picture_WeChat_style or 更多参考Demo
//                .loadImageEngine(getImageEngine())//@Deprecated 图片加载引擎 需要 implements ImageEngine接口
//                .imageEngine(getImageEngine())

//                .selectionMode(PictureConfig.SINGLE)//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
//                .isPageStrategy(true, false)//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
//                .isSingleDirectReturn(false)//PictureConfig.SINGLE模式下是否直接返回
//                .isWeChatStyle(false)//开启R.style.picture_WeChat_style样式
//                .setPictureStyle(null)//动态自定义相册主题
//                .setPictureCropStyle(null)//动态自定义裁剪主题
//                .setPictureWindowAnimationStyle(null)//相册启动退出动画
//                .isCamera(true)//列表是否显示拍照按钮
//                .isZoomAnim(true)//图片选择缩放效果???
//                .imageFormat(PictureMimeType.JPEG)//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
//                .maxSelectNum(9)//最大选择数量,默认9张
//                .minSelectNum(0)// 最小选择数量
//                .maxVideoSelectNum(4)//视频最大选择数量
//                .minVideoSelectNum(0)//视频最小选择数量
//                .videoMaxSecond(0)// 查询多少秒以内的视频
//                .videoMinSecond(0)// 查询多少秒以内的视频
//                .imageSpanCount(4)//列表每行显示个数
//                .openClickSound(false)//@Deprecated 是否开启点击声音
//                .isOpenClickSound(false)
//                .selectionMedia(selectionData)//@Deprecated 是否传入已选图片
//                .selectionData(selectionData)
//                .recordVideoSecond(60)//录制视频秒数 默认60s
//                .previewEggs(false)//预览图片时是否增强左右滑动图片体验
//                .cropCompressQuality()//@Deprecated 注：已废弃 改用cutOutQuality()
//                .cutOutQuality(90)      //裁剪压缩质量
//                .isGif(false)//是否显示gif, 默认false
//                .previewImage(true)//是否预览图片
//                .previewVideo(true)//是否预览视频
//                .enablePreviewAudio(true)//是否预览音频

                /**
                 * 下方是裁剪
                 */
//                .enableCrop(false)//@Deprecated 是否开启裁剪
//                .isEnableCrop(false)

//                .cropWH(0, 0)// 裁剪宽高比,已废弃，改用. cropImageWideHigh()方法
//                .cropImageWideHigh(0, 0)// 裁剪宽高比，设置如果大于图片本身宽高则无效
//                .withAspectRatio(0, 0)//裁剪比例
//                .cutOutQuality(90)// 裁剪输出质量 默认100(应该是90吧,写错了)
//                .freeStyleCropEnabled(false)//裁剪框是否可拖拽
//                .circleDimmedLayer(false)// 是否开启圆形裁剪
//                .setCircleDimmedColor(0)//@Deprecated 设置圆形裁剪背景色值
//                .setCircleDimmedBorderColor(0)//设置圆形裁剪边框色值
//                .setCircleStrokeWidth(1)//设置圆形裁剪边框粗细
//                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                .showCropGrid(true)//是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                .rotateEnabled(true)//裁剪是否可旋转图片
//                .scaleEnabled(true)//裁剪是否可放大缩小图片
//                .isDragFrame(true)//是否可拖动裁剪框(固定)
//                .hideBottomControls(true)//显示底部uCrop工具栏
//                .basicUCropConfig(null)//对外提供ucrop所有的配制项

                /**
                 * 下方是压缩
                 */
//                .compress(false)//@Deprecated 是否压缩
//                .isCompress(false)
//                .compressFocusAlpha(false)//压缩后是否保持图片的透明通道
//                .minimumCompressSize(PictureConfig.MAX_COMPRESS_SIZE/*100*/)// 小于多少kb的图片不压缩
//                .videoQuality(1)//视频录制质量 0 or 1
//                .compressQuality(80)//图片压缩后输出质量
//                .synOrAsy(true)//开启同步or异步压缩

//                .queryMaxFileSize(-1)//查询指定大小内的图片、视频、音频大小，单位M
//                .compressSavePath("")//自定义压缩图片保存地址，注意Q版本下的适配
//                .sizeMultiplier()//glide加载大小，已废弃
//                .glideOverride()//glide加载宽高，已废弃
//                .isMultipleSkipCrop(true)//多图裁剪是否支持跳过
//                .isMultipleRecyclerAnimation(true)// 多图裁剪底部列表显示动画效果
//                .querySpecifiedFormatSuffix("")//只查询指定后缀的资源，PictureMimeType.ofJPEG() ...
//                .isReturnEmpty(false)//未选择数据时按确定是否可以退出
//                .isAndroidQTransform(false)//Android Q版本下是否需要拷贝文件至应用沙盒内
//                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)//屏幕旋转方向 ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ...
//                .isOriginalImageControl(false)//开启原图选项
//                .bindCustomPlayVideoCallback(null)//自定义视频播放拦截
//                .bindCustomCameraInterfaceListener(null)//自定义拍照回调接口
//                .bindCustomPreviewCallback(null)// 自定义图片预览回调接口
//                .cameraFileName("")//自定义拍照文件名，如果是相册内拍照则内部会自动拼上当前时间戳防止重复
//                .renameCompressFile("")//自定义压缩文件名，多张压缩情况下内部会自动拼上当前时间戳防止重复
//                .renameCropFileName("")//自定义裁剪文件名，多张裁剪情况下内部会自动拼上当前时间戳防止重复
//                .setRecyclerAnimationMode(-1)//列表动画效果,AnimationType.ALPHA_IN_ANIMATION、SLIDE_IN_BOTTOM_ANIMATION
//                .isUseCustomCamera(false)// 开启自定义相机
//                .setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 自定义相机按钮状态,CustomCameraView.BUTTON_STATE_BOTH
//                .setLanguage(-1)//国际化语言 LanguageConfig.CHINESE、ENGLISH、JAPAN等
//                .isWithVideoImage(false)//图片和视频是否可以同选,只在ofAll模式下有效
//                .isMaxSelectEnabledMask(false)//选择条件达到阀时列表是否启用蒙层效果
//                .isAutomaticTitleRecyclerTop(true)//图片列表超过一屏连续点击顶部标题栏快速回滚至顶部
//                .loadCacheResourcesCallback(null)//获取ImageEngine缓存图片，参考Demo
//                .setOutputCameraPath("")// 自定义相机输出目录只针对Android Q以下版本，具体参考Demo
//                .forResult(/*requestCode, */listener);//结果回调分两种方式onActivityResult()和OnResultCallbackListener方式
    }


    /**
     * @return 图片/视频查看引擎
     */
    protected static ImageEngine getImageEngine() {
        if (imageEngine == null) {
            imageEngine = new ImageEngineImpl();
        }
        return imageEngine;
    }
}
