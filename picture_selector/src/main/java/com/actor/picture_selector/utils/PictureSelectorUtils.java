package com.actor.picture_selector.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;

import com.actor.myandroidframework.utils.video.VideoProcessorUtils;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.PictureSelectionCameraModel;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelectionQueryModel;
import com.luck.picture.lib.basic.PictureSelectionSystemModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.VideoQuality;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.manager.PictureCacheManager;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.ToastUtils;

import java.util.List;

/**
 * description: 图片/视频/音频 选择, 拍照, 裁剪, 压缩... <a href="https://github.com/LuckSiege/PictureSelector" target="_blank">PictureSelector的Github</a> <br />
 * <br />
 * // 图片压缩 (按需引入): 如果你需要使用压缩图片功能, 需要添加下面这行依赖 <br />
 *   implementation 'io.github.lucksiege:compress:v3.10.9' <br />
 * <br />
 *   // 图片裁剪 (按需引入) <br />
 *   implementation 'io.github.lucksiege:ucrop:v3.10.9' <br />
 * <br />
 *   // 自定义相机 (按需引入) <br />
 *   implementation 'io.github.lucksiege:camerax:v3.10.9' <br />
 */
public class PictureSelectorUtils {

    protected static ImageEngine imageEngine;
    protected static PictureSelectorStyle selectorStyle;

    protected static PictureSelectorUtils INSTANCE;
    protected PictureSelectionModel       selectionModel;
    protected PictureSelectionCameraModel selectionCameraModel;
    protected PictureSelectionSystemModel selectionSystemModel;

    //传入已选图片(可同步勾选状态)
    protected List<LocalMedia>            selectionData;

    //这个fragment 是否在<navagation 里面
    protected boolean isFragmentInNavigation;
    //调用系统相册/音频的时候, 是否是在Fragment 中调用的
    protected boolean isCallInFragment;
    /**
     * @param showCamera 是否显示相机
     * @param showGif 是否显示Gif图片
     * @param isCompress 是否压缩图片
     * @param isCrop 是否裁剪图片
     * @param singleSelect 是否是单选
     * @param maxSelect 最多选几张图片(包括 selectionData 里的图片也会计算在内)
     */
    protected boolean singleSelect;
    protected boolean showCamera;
    protected boolean showGif;
    protected boolean isCompress;
    protected boolean isCrop;
    //最多现在多少个
    protected int            maxSelect;
    //音视频, 最大时长 & 最小时长,单位秒
    protected int            maxSecond, minSecond;
    //裁剪引擎
    protected CropFileEngine cropFileEngine;
    //跳过裁剪图片的类型, 默认不裁剪Gif&Webp, 这2种都可能是动图.
    protected String[] skipCropMimeTypes;

    protected PictureSelectorUtils() {
        //do not new this.
    }

    protected static PictureSelectorUtils getInstance(boolean init) {
        if (INSTANCE == null) INSTANCE = new PictureSelectorUtils();
        //初始化
        if (init) {
            INSTANCE.isFragmentInNavigation = false;
            INSTANCE.isCallInFragment = false;
            INSTANCE.showCamera = true;
            INSTANCE.showGif = true;
            INSTANCE.singleSelect = false;
            INSTANCE.isCompress = false;
            INSTANCE.isCrop = false;
            INSTANCE.maxSelect = 9;
            INSTANCE.maxSecond = 0;
            INSTANCE.minSecond = 0;
            INSTANCE.selectionModel = null;
            INSTANCE.selectionCameraModel = null;
            INSTANCE.selectionSystemModel = null;
            INSTANCE.selectionData = null;
            INSTANCE.cropFileEngine = null;
            INSTANCE.skipCropMimeTypes = null;
        }
        return INSTANCE;
    }

    /**
     * @param selectionData 传入已选文件(可同步勾选状态). 预览的时候, if数据类型对不上, 可传null
     */
    public static Builder create(@NonNull Activity activity, @Nullable List<LocalMedia> selectionData) {
        PictureSelectorUtils utils = getInstance(true);
        utils.selectionData = selectionData;
        utils.isCallInFragment = false;
        return new Builder(PictureSelector.create(activity));
    }

    /**
     * @param isFragmentInNavigation 这个fragment 是否在<navagation 里面
     * @param selectionData 传入已选文件(可同步勾选状态). 预览的时候, if数据类型对不上, 可传null
     * @return
     */
    public static Builder create(@NonNull Fragment fragment, boolean isFragmentInNavigation, @Nullable List<LocalMedia> selectionData) {
        PictureSelectorUtils utils = getInstance(true);
        utils.selectionData = selectionData;
        utils.isFragmentInNavigation = isFragmentInNavigation;
        utils.isCallInFragment = true;
        return new Builder(PictureSelector.create(fragment));
    }

    public static class Builder {

//        protected static Builder getInstance() {
//
//        }

        protected PictureSelector selector;
        protected Builder(PictureSelector selector) {
            this.selector = selector;
        }

        /**
         * 选择图片
         * @param isCompress 图片是否压缩 <br />
         * 如果要压缩视频, 需要自己手动调用代码压缩, 可使用:
         * @see VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)
         */
        public PictureSelectorUtils selectImage(boolean isCompress) {
            PictureSelectorUtils utils = getInstance(false);
            utils.isCompress = isCompress;
            return selections(SelectMimeType.ofImage());
        }

        /**
         * 选择视频
         */
        public PictureSelectorUtils selectVideo() {
            return selections(SelectMimeType.ofVideo());
        }

        /**
         * 选择图片&视频
         * @param isCompress 图片是否压缩 <br />
         * 如果要压缩视频, 需要自己手动调用代码压缩, 可使用:
         * @see VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)
         */
        public PictureSelectorUtils selectImage$Video(boolean isCompress) {
            PictureSelectorUtils utils = getInstance(false);
            utils.isCompress = isCompress;
            return selections(SelectMimeType.ofAll());
        }

        /**
         * 选择音频. 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
         */
        public PictureSelectorUtils selectAudio() {
            return selections(SelectMimeType.ofAudio());
        }

        protected PictureSelectorUtils selections(int chooseMode) {
            PictureSelectorUtils utils = getInstance(false);
            utils.selectionModel = selector.openGallery(chooseMode);
            return utils;
        }

        /**
         * 拍照
         * @param isCompress 图片是否压缩 <br />
         * 如果要压缩视频, 需要自己手动调用代码压缩, 可使用:
         * @see VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)
         */
        public PictureSelectorUtils takePhoto(boolean isCompress) {
            PictureSelectorUtils utils = getInstance(false);
            utils.isCompress = isCompress;
            utils.selectionCameraModel = selector.openCamera(SelectMimeType.ofImage());
            return utils;
        }

        /**
         * 录视频
         */
        public PictureSelectorUtils recordVideo() {
            PictureSelectorUtils utils = getInstance(false);
            utils.selectionCameraModel = selector.openCamera(SelectMimeType.ofVideo());
            return utils;
        }

        /**
         * 录音频
         */
        public PictureSelectorUtils recordAudio() {
            PictureSelectorUtils utils = getInstance(false);
            utils.selectionCameraModel = selector.openCamera(SelectMimeType.ofAudio());
            return utils;
        }

        /**
         * 选择系统图库/视频/音频 (系统图库有些api不支持)
         * @param selectMimeType 选择文件类型: {@link SelectMimeType#ofAll()},
         * {@link SelectMimeType#ofImage()}, {@link SelectMimeType#ofVideo()}, {@link SelectMimeType#ofAudio()}
         */
        public PictureSelectorUtils selectSystemFile(int selectMimeType) {
            PictureSelectorUtils utils = getInstance(false);
            utils.selectionSystemModel = selector.openSystemGallery(selectMimeType);
            return utils;
        }

        /**
         * 预览图片/视频/音频
         */
        public PreviewConfiger openPreview() {
            PictureSelectorUtils utils = getInstance(false);
            return new PreviewConfiger(selector.openPreview(), utils.selectionData);
        }
    }

    /**
     * 图片/视频/音频 是否单选
     */
    public PictureSelectorUtils setSingleSelect(boolean singleSelect) {
        this.singleSelect = singleSelect;
        return this;
    }

    /**
     * 选择 图片/视频/音频 是否显示相机/录制音频图标
     */
    public PictureSelectorUtils setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
        return this;
    }

    /**
     * 选择 图片 是否显示Gif
     */
    public PictureSelectorUtils setShowGif(boolean showGif) {
        this.showGif = showGif;
        return this;
    }

    /**
     * 是否裁剪图片
     */
    public PictureSelectorUtils setCrop(boolean crop) {
        isCrop = crop;
        return this;
    }

    /**
     * 设置多选的时候, 图片/视频 最多选择多少个
     */
    public PictureSelectorUtils setMaxSelect(@IntRange(from = 2) int maxSelect) {
        this.maxSelect = maxSelect;
        return this;
    }

    /**
     * 设置: "选择or录制 视频/音频" 的时长
     * @param maxSecond 最大时长,单位秒. 不做限制可传0
     * @param minSecond 最小时长,单位秒. 不做限制可传0
     */
    public PictureSelectorUtils setAudioVideoSecond(int maxSecond, int minSecond) {
        this.maxSecond = maxSecond;
        this.minSecond = minSecond;
        return this;
    }

    /**
     * 设置裁剪, if 不设置, 会使用默认的 {@link CropFileEngineImpl}
     */
    public PictureSelectorUtils setCropFileEngine(@Nullable CropFileEngine cropFileEngine) {
        this.cropFileEngine = cropFileEngine;
        return this;
    }

    /**
     * 跳过裁剪图片的类型, 默认不裁剪Gif&Webp, 这2种都可能是动图.
     */
    public PictureSelectorUtils setSkipCropMimeTypes(@Nullable String... skipCropMimeTypes) {
        this.skipCropMimeTypes = skipCropMimeTypes;
        return this;
    }

    public void forResult(@NonNull OnResultCallbackListener<LocalMedia> listener) {
        //选图片/视频/音频
        if (selectionModel != null) {
            forResult(defaultConfigPictureSelectionModel(selectionModel), listener);
        } else if (selectionCameraModel != null) {
            //拍照/录视频/录音频
            forResult(defaultConfigPictureSelectionCameraModel(selectionCameraModel), listener);
        } else if (selectionSystemModel != null) {
            //选择系统图片/视频/音频
            forResult(defaultConfigPictureSelectionSystemModel(selectionSystemModel), listener);
        }
        //销毁本地变量
        selectionModel = null;
        selectionCameraModel = null;
        selectionSystemModel = null;
        selectionData = null;
    }

    /**
     * 开始跳转选择 图片/视频/音频
     * @param selectionModel2 可以自定义 <br />
     * @param listener 回调监听 <br />
     *
     * <a href="https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-LocalMedia%E8%AF%B4%E6%98%8E" target="_blank">PictureSelector 3.0 LocalMedia说明</a> <br />
     *
     *  <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *      <tr>
     *          <th align="center">方法</th>
     *          <th align="center">返回示例</th>
     *          <th align="center">说明</th>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getAvailablePath()}</td>
     *          <td></td>
     *          <td>SDK_INT为任意版本都返回直接可用地址(但SDK_INT >29且未开启压缩、裁剪或未实现setSandboxFileEngine，请参考getPath())，但如果你需要具体业务场景下的地址，请参考如上几种路径；</td>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getCompressPath()}</td>
     *          <td>null</td>
     *          <td>压缩路径；实现了setCompressEngine();时返回；</td>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getCutPath()}</td>
     *          <td>null</td>
     *          <td>裁剪或编辑路径；实现了setCropEngine();或setEditMediaInterceptListener();时返回；</td>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getOriginalPath()}</td>
     *          <td>null</td>
     *          <td>原图路径；isOriginalImageControl(true); 且勾选了原图选项时返回；但SDK_INT >=29且未实现.setSandboxFileEngine(); 直接使用会报FileNotFoundException异常；</td>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getPath()}</td>
     *          <td>content://media/external/file/116272</td>
     *          <td>指从MediaStore查询返回的路径；SDK_INT >=29 返回content://类型；其他情况返回绝对路径。</td>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getRealPath()}</td>
     *          <td>/storage/emulated/0/news_article/a9f5fb.png</td>
     *          <td>绝对路径；SDK_INT >=29且处于沙盒环境下直接使用会报FileNotFoundException异常；</td>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getSandboxPath()}</td>
     *          <td></td>
     *          <td>SDK_INT >=29且实现了.setSandboxFileEngine();返回；</td>
     *      </tr>
     *      <tr>
     *          <td nowrap="nowrap">{@link LocalMedia#getVideoThumbnailPath()}</td>
     *          <td></td>
     *          <td>视频缩略图，需要实现setVideoThumbnailListener接口</td>
     *      </tr>
     *      <tr>
     *          <td>{@link LocalMedia#getWatermarkPath()}</td>
     *          <td></td>
     *          <td>水印地址，需要实现setAddBitmapWatermarkListener接口</td>
     *      </tr>
     *  </table>
     * @deprecated 传入自定义配置后调用.
     */
    @Deprecated
    public void forResult(@NonNull PictureSelectionModel selectionModel2, @NonNull OnResultCallbackListener<LocalMedia> listener) {
//        selectionModel2.forResult(requestCode); //结果回调 onActivityResult()
//        selectionModel2.forResult(ActivityResultLauncher<Intent> launcher);
        //结果回调 OnResultCallbackListener
        selectionModel2.forResult(listener);
    }

    /**
     * 获取默认的图片选择配置, 默认配置见: {@link SelectorConfig#initDefaultValue()} <br />
     * <a href="https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/MainActivity.java" target="_blank">MainActivity.java (PictureSelector Api配置大全)</a> <br />
     * @deprecated 获取自定义配置
     */
    @Deprecated
    public PictureSelectionModel defaultConfigPictureSelectionModel(@Nullable PictureSelectionModel selectionModel) {
        if (selectionModel == null) {
            selectionModel = this.selectionModel;
        }
        selectionModel
//                .build()                      //返回选择图片的fragment
//                .buildLaunch(int containerViewId, OnResultCallbackListener<LocalMedia> call) //选择图片的fragment的container
                .isAutomaticTitleRecyclerTop(false) //图片列表超过一屏连续点击顶部标题栏快速回滚至顶部, 默认true
                .isAutoVideoPlay(false)         //预览的时候, 是否自动播放音视频
                .isBmp(true)                    //默认true, 是否打开.bmp
                .isCameraAroundState(false)     //设置相机方向
                .isCameraForegroundService(false)//拍照开启一个前台服务用于增强保活部分机型
                .isCameraRotateImage(true)      //相机图像旋转，自动校正
                .isDisplayCamera(showCamera)
                .isDirectReturnSingle(false)    //单选模式时, 点击图片/视频后, 是否不预览,直接返回
                .isDisplayTimeAxis(false)       //是否显示时间轴, 默认true (滑动的时候, 会在title下方显示1秒)
                .isEmptyResultReturn(false)     //没数据是否能回调
                .isFastSlidingSelect(true)      //快速滑动多选
                .isGif(showGif)                 //是否显示gif, 默认false
                .isMaxSelectEnabledMask(false)  //当达到最大选择数时，列表是否启用遮罩效果
                .isOriginalControl(true)        //开启原图☑选项
                .isOnlyObtainSandboxDir(false)  //查询指定目录
                .isOpenClickSound(false)        //是否打开点击声音
                .isPageStrategy(false)          //是否是分页查询战略
                .isQuickCapture(false)          //使用系统摄像头录制后，是否支持使用系统播放器立即播放视频
                .isSelectZoomAnim(false)        //选择资产时需要缩放动画
//                .isSyncCover(!SdkVersionUtils.isQ()) //同步选择目录文件夹的封面
                .isWebp(true)
                .isWithSelectVideoImage(false)  //同1次选择, 是否能选择图片&视频

//                .setAddBitmapWatermarkListener()//添加水印
//                .setAttachViewLifecycle()     //设置fragment依附的view的生命周期
                .setCameraImageFormat(PictureMimeType.JPEG)
                .setCameraImageFormatForQ(PictureMimeType.MIME_TYPE_IMAGE)
//                .setCameraInterceptListener() //拦截摄像头点击事件，用户可以实现自己的摄像头框架

                //if 需要压缩图片, 请添加依赖, 见顶部说明!
                .setCompressEngine(isCompress ? new CompressFileEngineImpl() : null)

                .setCameraVideoFormat(PictureMimeType.MP4)
                .setCameraVideoFormatForQ(PictureMimeType.MIME_TYPE_VIDEO)
                ;

        //if 需要裁剪图片, 请添加依赖, 见顶部说明!
        if (isCrop) {
            if (cropFileEngine == null) {
                cropFileEngine = new CropFileEngineImpl()
                        //设置哪些类型的图片不裁剪
                        .setSkipCropMimeTypes(skipCropMimeTypes)
                        .setCircleDimmed(false)         //圆形头像裁剪模式
                        .setDragAble(true)              //裁剪框or图片拖动
                        .setHideBottomControls(false)   //是否隐藏裁剪菜单栏
//                        .setRatio()                   //裁剪宽高比例
//                        .setOutputPath()              //输出路径
                ;
            }
        } else {
            cropFileEngine = null;
        }
        selectionModel.setCropEngine(cropFileEngine)

                .setDefaultAlbumName("")    //设置第一个默认相册名称
                .setFilterMaxFileSize(0)    //默认0. 图片最大多少kb, 这方法点进去看, 不严谨
                .setFilterMinFileSize(0)    //默认0. 图片最小多少kb, 这方法点进去看, 不严谨
                .setFilterVideoMaxSecond(maxSecond) //选择视频最大时长
                .setFilterVideoMinSecond(minSecond) //选择视频最小时长
                .setImageEngine(getImageEngine())
                .setImageSpanCount(4)       //列表每行显示个数
//                .setInjectLayoutResourceListener()    //拦截自定义注入布局事件，用户可以在视图ID必须一致的前提下实现自己的布局
                .setLanguage(LanguageConfig.UNKNOWN_LANGUAGE)
//                .setLoaderFactoryEngine()         //用户可以实现一些接口来访问自己的查询数据 前提是需要符合 PictureSelector IBridgeLoaderFactory/LocalMediaFolder/LocalMedia 的型号规范
                .setMaxSelectNum(maxSelect)         //最大选择数量, 默认9
                .setMaxVideoSelectNum(maxSelect)    //最大视频选择数量
                .setMinAudioSelectNum(0)            //最小音频选择数量
                .setMinSelectNum(0)
                .setMinVideoSelectNum(0)
                .setOfAllCameraType(SelectMimeType.ofAll()) //在“全部”模式下选择拍照和拍摄
                .setOutputAudioDir("")
                .setOutputAudioFileName("")
                .setOutputCameraDir("")
                .setOutputCameraImageFileName("")
                .setOutputCameraVideoFileName("")
//                .setPermissionDeniedListener()    //权限拒绝监听
//                .setPermissionDescriptionListener()
//                .setPermissionsInterceptListener()
//                .setPreviewInterceptListener()    //拦截预览点击事件，用户可以实现自己的预览框架
//                .setQueryFilterListener()         //您需要过滤掉不符合标准的内容
//                .setQueryOnlyMimeType()           //设置只查询一种图片/视频/音频
//                .setQuerySandboxDir()             //查询指定目录中的图片或视频
                .setQuerySortOrder("")              //图片排序sql

                /**
                 * 拦截录制音频点击事件，用户可以实现自己的录制音频框架 <br />
                 * 这儿必须要传参, 否则会报错:
                 * @see com.luck.picture.lib.basic.PictureCommonFragment#openSoundRecording()
                 */
                .setRecordAudioInterceptListener(new OnRecordAudioInterceptListenerImpl())
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//屏幕旋转方向, 默认: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                .setRecordVideoMaxSecond(maxSecond) //录制视频最大时长, 默认60
                .setRecordVideoMinSecond(minSecond)
                .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION)  //图片列表上划的时候的动画

                .setSelectionMode(singleSelect ? SelectModeConfig.SINGLE : SelectModeConfig.MULTIPLE) //单选/多选模式
                .setSelectedData(selectionData)     //已选择文件
                .setSandboxFileEngine(new UriToFileTransformEngineImpl())   //沙盒, uri转File
//                .setSelectFilterListener()        //您需要过滤掉不符合选择标准的内容
//                .setSelectLimitTipsListener()     //选择限制提示. 如果返回true，则用户需要自定义实现提示内容，否则使用系统默认提示
                .setSelectMaxDurationSecond(maxSecond)//选择视频或音频支持的最大秒数
                .setSelectMaxFileSize(0)              //选择最大文件大小
                .setSelectMinDurationSecond(minSecond)//选择视频或音频支持的最小秒数
                .setSelectMinFileSize(0)
//                .setSelectorUIStyle(getPictureSelectorStyle()) //UI主题, 微信主题最好看, 但配置较多, 懒得弄
                .setSkipCropMimeType(skipCropMimeTypes) //跳过剪切的图片类型 (传参要传第2次, 无语...)
//                .setVideoThumbnailListener()      //处理视频缩略图
                .setVideoQuality(VideoQuality.VIDEO_QUALITY_HIGH)
                .setVideoPlayerEngine(null)         //默认MediaPlayer
        ;
        return selectionModel;
    }



    /**
     * 打开相机拍照. 如果要压缩视频, 可使用:
     * @see VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)
     * @param cameraModel "拍照/录视频" 配置, 可自定义
     * @param listener 回调
     * @deprecated 传入自定义配置后调用.
     */
    @Deprecated
    public void forResult(@NonNull PictureSelectionCameraModel cameraModel, OnResultCallbackListener<LocalMedia> listener) {
        if (isFragmentInNavigation) {
            //使用Activity承载Camera相机
            //If you are in the Navigation Fragment scene, you must use this method
//            cameraModel.forResultActivity(int requestCode);
            //如果<fragment 在<navigation 场景中，则必须使用此方法
//            cameraModel.forResultActivity(ActivityResultLauncher<Intent> launcher);
            //如果<fragment 在<navigation 场景中，则必须使用此方法
            cameraModel.forResultActivity(listener);
        } else {
            /**
             * Activity or Fragment 需要实现IBridgePictureBehavior接口, 才能接收返回的结果.
             * If the navigation component manages fragments,
             * it is recommended to use PictureSelectionCameraModel.forResultActivity() in openCamera mode
             */
//            cameraModel.forResult();
            //If the navigation component manages fragments, it is recommended to use PictureSelectionCameraModel.forResultActivity() in openCamera mode
            cameraModel.forResult(listener);
        }
    }

    /**
     * 获取默认的 "拍照/录视频/录语音" 选择配置, 默认配置见: {@link SelectorConfig#initDefaultValue()} <br />
     * <a href="https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/MainActivity.java" target="_blank">MainActivity.java (PictureSelector Api配置大全)</a> <br />
     * @deprecated 获取自定义配置
     */
    @Deprecated
    public PictureSelectionCameraModel defaultConfigPictureSelectionCameraModel(@Nullable PictureSelectionCameraModel selectionCameraModel) {
        if (selectionCameraModel == null) {
            selectionCameraModel = this.selectionCameraModel;
        }
        selectionCameraModel
//                .build()                          //返回选择图片的fragment
//                .buildLaunch(int containerViewId, OnResultCallbackListener<LocalMedia> call)  //选择图片的fragment的container

                .isCameraAroundState(false)         //设置相机方向
                .isCameraForegroundService(false)   //拍照开启一个前台服务用于增强保活部分机型
                .isCameraRotateImage(true)          //相机图像旋转，自动校正
                .isOriginalControl(true)            //开启原图☑选项
                .isQuickCapture(false)              //使用系统摄像头录制后，是否支持使用系统播放器立即播放视频
                .isOriginalSkipCompress(!isCompress)//是否跳过压缩

//                .setAddBitmapWatermarkListener()  //添加水印
                .setCameraImageFormat(PictureMimeType.JPEG)
                .setCameraImageFormatForQ(PictureMimeType.MIME_TYPE_IMAGE)
//                .setCameraInterceptListener()     //自定义相机拍照, 参考: SimpleCameraX.of();
                .setCameraVideoFormat(PictureMimeType.MP4)
                .setCameraVideoFormatForQ(PictureMimeType.MIME_TYPE_VIDEO)

                //if 需要压缩图片, 请添加依赖, 见顶部说明!
                .setCompressEngine(isCompress ? new CompressFileEngineImpl() : null)
        ;

        //if 需要裁剪图片, 请添加依赖, 见顶部说明!
        if (isCrop) {
            if (cropFileEngine == null) {
                cropFileEngine = new CropFileEngineImpl()
                        //设置哪些类型的图片不裁剪
                        .setSkipCropMimeTypes(skipCropMimeTypes)
                        .setCircleDimmed(false)         //圆形头像裁剪模式
                        .setDragAble(true)              //裁剪框or图片拖动
                        .setHideBottomControls(false)   //是否隐藏裁剪菜单栏
//                        .setRatio()                   //裁剪宽高比例
//                        .setOutputPath()              //输出路径
                ;
            }
        } else {
            cropFileEngine = null;
        }
        selectionCameraModel.setCropEngine(cropFileEngine)
//                .setCustomLoadingListener() //自定义显示加载对话框
                .setDefaultLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                .setLanguage(LanguageConfig.UNKNOWN_LANGUAGE)
                .setOfAllCameraType(SelectMimeType.ofAll()) //在“全部”模式下选择拍照和拍摄
                .setOutputAudioDir("")
                .setOutputAudioFileName("")
                .setOutputCameraDir("")
                .setOutputCameraImageFileName("")
                .setOutputCameraVideoFileName("")
//                .setPermissionDeniedListener()        //权限拒绝监听
//                .setPermissionDescriptionListener()
//                .setPermissionsInterceptListener()

                /**
                 * 拦截录制音频点击事件，用户可以实现自己的录制音频框架 <br />
                 * 这儿必须要传参, 否则会报错:
                 * @see com.luck.picture.lib.basic.PictureCommonFragment#openSoundRecording()
                 */
                .setRecordAudioInterceptListener(new OnRecordAudioInterceptListenerImpl())
                .setRecordVideoMaxSecond(maxSecond)     //录制视频最大时长, 默认60
                .setRecordVideoMinSecond(minSecond)
                .setSandboxFileEngine(new UriToFileTransformEngineImpl())   //沙盒, uri转File
                .setSelectedData(selectionData)         //已选择文件
//                .setSelectLimitTipsListener()         //选择限制提示. 如果返回true，则用户需要自定义实现提示内容，否则使用系统默认提示
                .setSelectMaxDurationSecond(maxSecond)  //选择视频或音频支持的最大秒数
                .setSelectMaxFileSize(0)                //选择最大文件大小
                .setSelectMinDurationSecond(minSecond)  //选择视频或音频支持的最小秒数
                .setSelectMinFileSize(0)
                .setVideoQuality(VideoQuality.VIDEO_QUALITY_HIGH)
//                .setVideoThumbnailListener()          //处理视频缩略图
        ;
        return selectionCameraModel;
    }



    /**
     * 选择系统图片/视频/音频. 使用系统库，将不支持某些 API 函数. <br />
     * 如果要压缩视频, 可使用:
     * @see VideoProcessorUtils#compressVideo(Context, String, VideoProcessorUtils.OnCompressListener)
     * @param systemModel 配置, 可自定义
     * @param listener 回调
     * @deprecated 传入自定义配置后调用.
     */
    @Deprecated
    public void forResult(@NonNull PictureSelectionSystemModel systemModel, OnResultCallbackListener<LocalMedia> listener) {
        if (isCallInFragment) {
            //Activity or Fragment 需要实现IBridgePictureBehavior接口, 才能接收返回的结果.
//            systemModel.forSystemResult();
            systemModel.forSystemResult(listener);
        } else {
//            systemModel.forSystemResultActivity(int requestCode);
            //传入 Activity.registerForActivityResult( ActivityResultContract , ActivityResultCallback )
//            systemModel.forSystemResultActivity(ActivityResultLauncher<Intent> launcher);
            systemModel.forSystemResultActivity(listener);
        }
    }

    /**
     * 系统选择配置
     * @deprecated 获取自定义配置
     */
    @Deprecated
    public PictureSelectionSystemModel defaultConfigPictureSelectionSystemModel(@Nullable PictureSelectionSystemModel systemModel) {
        if (systemModel == null) {
            systemModel = this.selectionSystemModel;
        }
        systemModel
                .isOriginalControl(true)            //开启原图☑选项
                .isOriginalSkipCompress(!isCompress)

//                .setAddBitmapWatermarkListener()  //添加水印

                //if 需要压缩图片, 请添加依赖, 见顶部说明!
                .setCompressEngine(isCompress ? new CompressFileEngineImpl() : null)
                ;

        //if 需要裁剪图片, 请添加依赖, 见顶部说明!
        if (isCrop) {
            if (cropFileEngine == null) {
                cropFileEngine = new CropFileEngineImpl()
                        //设置哪些类型的图片不裁剪
                        .setSkipCropMimeTypes(skipCropMimeTypes)
                        .setCircleDimmed(false)         //圆形头像裁剪模式
                        .setDragAble(true)              //裁剪框or图片拖动
                        .setHideBottomControls(false)   //是否隐藏裁剪菜单栏
//                        .setRatio()                   //裁剪宽高比例
//                        .setOutputPath()              //输出路径
                ;
            }
        } else {
            cropFileEngine = null;
        }
        systemModel.setCropEngine(cropFileEngine)
//                .setCustomLoadingListener()       //自定义显示加载对话框
//                .setPermissionDeniedListener()    //权限拒绝监听
//                .setPermissionDescriptionListener()
//                .setPermissionsInterceptListener()
                .setSandboxFileEngine(new UriToFileTransformEngineImpl())   //沙盒, uri转File
//                .setSelectFilterListener()            //您需要过滤掉不符合选择标准的内容
                .setSelectionMode(singleSelect ? SelectModeConfig.SINGLE : SelectModeConfig.MULTIPLE) //单选/多选模式
//                .setSelectLimitTipsListener()         //选择限制提示. 如果返回true，则用户需要自定义实现提示内容，否则使用系统默认提示
                .setSelectMaxDurationSecond(maxSecond)  //选择视频或音频支持的最大秒数
                .setSelectMaxFileSize(0)                //选择最大文件大小
                .setSelectMinDurationSecond(minSecond)  //选择视频或音频支持的最小秒数
                .setSelectMinFileSize(0)
                .setSkipCropMimeType(skipCropMimeTypes) //跳过剪切的图片类型 (传参要传第2次, 无语...)
//                .setVideoThumbnailListener()          //处理视频缩略图
        ;
        return systemModel;
    }



    ///////////////////////////////////////////////////////////////////////////
    // Activity & Fragment录音频
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 录音, 使用前请先申请权限, 在onActivityResult中获取返回值:
     * if (requestCode == 你传入的requestCode && data != null) {
     *     File file = UriUtils.uri2File(data.getData();//获取录音文件
     * }
     */
    @RequiresPermission(allOf = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordAudio(Activity activity, int requestCode) {
        Intent intentRecord = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //对目标应用临时授权该Uri所代表的文件
            intentRecord.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (intentRecord.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intentRecord, requestCode);
        } else {
            ToastUtils.showToast(activity, "系统没有录音组件");
        }
    }

    /**
     * 录音, 使用前请先申请权限, 在onActivityResult中获取返回值:
     * if (requestCode == 你传入的requestCode && data != null) {
     *     File file = UriUtils.uri2File(data.getData();//获取录音文件
     * }
     */
    @RequiresPermission(allOf = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    public static void recordAudio(Fragment fragment, int requestCode) {
        Intent intentRecord = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //对目标应用临时授权该Uri所代表的文件
            intentRecord.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (intentRecord.resolveActivity(fragment.requireActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(intentRecord, requestCode);
        } else {
            ToastUtils.showToast(fragment.getContext(), "系统没有录音组件");
        }
    }


    /**
     * 查询系统图片/视频音频 (系统图库有些api不支持)
     * @param selectMimeType 选择文件类型: {@link SelectMimeType#ofAll()},
     * {@link SelectMimeType#ofImage()}, {@link SelectMimeType#ofVideo()}, {@link SelectMimeType#ofAudio()}
     */
    @Nullable
    public static PictureSelectionQueryModel queryDataSource(@Nullable Activity activity, @Nullable Fragment fragment, int selectMimeType) {
        if (activity != null) {
            return PictureSelector.create(activity).dataSource(selectMimeType);
        } else if (fragment != null) {
            return PictureSelector.create(fragment).dataSource(selectMimeType);
        }
        return null;
    }



    /**
     * 清除指定类型缓存
     * 包括裁剪和压缩后的缓存，要在上传成功后调用，type 指的是图片or视频缓存取决于你设置的ofImage或ofVideo 注意：需要系统sd卡权限
     * @param type PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
     */
    @RequiresPermission(Manifest.permission_group.STORAGE)
    public static void deleteCacheDirFile(Context context, int type) {
        PictureCacheManager.deleteCacheDirFile(context, type);
    }



    /**
     * 清除所有缓存 例如：压缩、裁剪、视频、音频所生成的临时文件
     */
    public static void deleteAllCacheDirFile(Context context) {
        PictureCacheManager.deleteAllCacheDirFile(context);
    }




    private static final String TAG = "PictureSelectorUtils";

    /**
     * 打印 LocalMedia 信息
     */
    public static void printLocalMedia(@Nullable LocalMedia media) {
        if (media == null) return;
        Log.i(TAG, "文件名getFileName()                             :" + media.getFileName());
        Log.i(TAG, "是否压缩isCompressed()                          :" + media.isCompressed());
        Log.i(TAG, "压缩路径getCompressPath()                       :" + media.getCompressPath());
        Log.i(TAG, "初始路径getPath()                               :" + media.getPath());
        Log.i(TAG, "绝对路径getRealPath()                           :" + media.getRealPath());
        Log.i(TAG, "是否裁剪isCut()                                 :" + media.isCut());
        Log.i(TAG, "裁剪路径getCutPath()                            :" + media.getCutPath());
        Log.i(TAG, "是否开启原图isOriginal()                        :" + media.isOriginal());
        Log.i(TAG, "原图路径getOriginalPath()                       :" + media.getOriginalPath());
        Log.i(TAG, "沙盒路径getSandboxPath()                        :" + media.getSandboxPath());
        Log.i(TAG, "水印路径getWatermarkPath()                      :" + media.getWatermarkPath());
        Log.i(TAG, "视频缩略图getVideoThumbnailPath()               :" + media.getVideoThumbnailPath());
        Log.i(TAG, "原始宽高getWidth(),getHeight()                  :" + media.getWidth() + "x" + media.getHeight());
        Log.i(TAG, "裁剪宽高getCropImageWidth(),getCropImageHeight():" + media.getCropImageWidth() + "x" + media.getCropImageHeight());
        Log.i(TAG, "文件大小getSize()                               :" + PictureFileUtils.formatAccurateUnitFileSize(media.getSize()));
        Log.i(TAG, "文件时长getDuration()                           :" + media.getDuration());
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

    /**
     * @return UI主题, 微信主题最好看, 但配置较多, 懒得弄
     */
    public static PictureSelectorStyle getPictureSelectorStyle() {
        if (selectorStyle == null) {
            selectorStyle = new PictureSelectorStyle();
        }
        return selectorStyle;
    }

    /**
     * 设置 UI主题
     * @param
     */
    public static void setPictureSelectorStyle(PictureSelectorStyle style) {
        if (style != null) {
            selectorStyle = style;
        }
    }
}
