package com.actor.myandroidframework.utils.picture_selector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.StyleUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;
import java.util.ArrayList;

/**
 * description: 图片裁剪 <br />
 * 注意: 需要裁剪图片, 请添加依赖! <br /> <br />
 *
 * // 图片裁剪 (按需引入): 如果你选择图片/拍照后, 需要使用裁剪图片功能, 需要添加下面这行依赖 <br />
 * implementation 'io.github.lucksiege:ucrop:v3.10.9' <br />
 *<br />
 * 具体更多的压缩API见: <a href="https://github.com/Yalantis/uCrop" target="_blank">https://github.com/Yalantis/uCrop</a>
 * <br />
 * @author : ldf
 * date       : 2023/4/1 on 13
 * @version 1.0
 */
public class CropFileEngineImpl implements CropFileEngine {

    protected boolean hideBottomControls = false;   //是否隐藏裁剪菜单栏(宽高比例, 旋转角度, 缩放比例 共3个按钮), 隐藏的话, 只能原比例缩放裁剪
    protected boolean dragAble = true;              //裁剪框or图片拖动(手动调整裁剪比例, 旋转, 缩放) (在裁剪框上不能操作)
    protected boolean isCircleDimmed = false;       //圆形头像裁剪模式
    protected float ratioX = -1, ratioY = -1;
    protected String outputPath;
    protected String[] skipCropMimeTypes = {PictureMimeType.ofGIF(), PictureMimeType.ofWEBP()};


    public void CropFileEngineImpl() {
    }

    /**
     * @param hideBottomControls 是否隐藏裁剪菜单栏(宽高比例, 旋转角度, 缩放比例 共3个按钮), 隐藏的话, 只能原比例缩放裁剪
     */
    public CropFileEngineImpl setHideBottomControls(boolean hideBottomControls) {
        this.hideBottomControls = hideBottomControls;
        return this;
    }

    /**
     * @param dragAble 裁剪框or图片拖动(手动调整裁剪比例, 旋转, 缩放) (在裁剪框上不能操作)
     */
    public CropFileEngineImpl setDragAble(boolean dragAble) {
        this.dragAble = dragAble;
        return this;
    }

    /**
     * @param isCircleDimmed 圆形头像裁剪模式
     */
    public CropFileEngineImpl setCircleDimmed(boolean isCircleDimmed) {
        this.isCircleDimmed = isCircleDimmed;
        return this;
    }


    /**
     * 裁剪宽高比例, 请设置>0的 float 数
     * @param ratioX 裁剪比例, 默认原始比例(-1, -1), 常用比例:(1:1, 3:4, 3:2, 16:9)
     * @param ratioY 裁剪比例
     */
    public CropFileEngineImpl setRatio(@FloatRange(from = 0, fromInclusive = false) float ratioX,
                                       @FloatRange(to = 0, toInclusive = false) float ratioY) {
        this.ratioX = ratioX;
        this.ratioY = ratioY;
        return this;
    }


    /**
     * @param outputPath 自定义输出目录, 可不传, 有默认值
     */
    public CropFileEngineImpl setOutputPath(@Nullable String outputPath) {
        this.outputPath = outputPath;
        return this;
    }


    /**
     * 跳过裁剪图片的类型, 默认不裁剪Gif&Webp, 这2种都可能是动图. <br />
     * 如果真要裁剪gif的话, 会裁剪第1帧, 返回第1帧的静态图! <br />
     * <br />
     * @param skipCropMimeTypes 跳过裁剪图片的类型
     * @see PictureMimeType#ofGIF()
     * @see PictureMimeType#ofWEBP()
     * 还有其它类型...
     */
    public CropFileEngineImpl setSkipCropMimeTypes(String... skipCropMimeTypes) {
        this.skipCropMimeTypes = skipCropMimeTypes;
        return this;
    }


    @Override
    public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
        // 注意* 如果你实现自己的裁剪库，需要在Activity的.setResult();
        // Intent中需要给MediaStore.EXTRA_OUTPUT，塞入裁剪后的路径；如果有额外数据也可以通过CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA字段存入；

        if (fragment == null) return;
        FragmentActivity activity = fragment.getActivity();
        if (activity == null) return;
        UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource)
                .withOptions(buildOptions(activity));
        uCrop.setImageEngine(new UCropImageEngine() {
            @Override
            public void loadImage(Context context, String url, ImageView imageView) {
                Glide.with(context).load(url)
                        .override(180, 180)
                        .into(imageView);
            }
            @Override
            public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (call != null) {
                            call.onCall(resource);
                        }
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (call != null) {
                            call.onCall(null);
                        }
                    }
                });
            }
        });
        uCrop.start(activity, fragment, requestCode);
    }


    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    protected UCrop.Options buildOptions(FragmentActivity activity) {
        if (TextUtils.isEmpty(outputPath)) {
            outputPath = getSandboxPath(activity);
        }
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(hideBottomControls);  //是否显示裁剪菜单栏(宽高比例, 旋转角度, 缩放比例 共3个按钮), 不显示的话, 只能原比例缩放裁剪
        options.setFreeStyleCropEnabled(dragAble);          //裁剪框or图片拖动(手动调整裁剪比例, 旋转, 缩放) (在裁剪框上不能操作)
        options.setShowCropFrame(true);                     //是否显示裁剪边框(裁剪框的4个边)
        options.setShowCropGrid(true);                      //是否显示裁剪框网格
        options.setCircleDimmedLayer(isCircleDimmed);       //圆形头像裁剪模式
        options.withAspectRatio(ratioX, ratioY);            //裁剪比例, 默认原始比例(-1, -1), 常用比例:(1:1, 3:4, 3:2, 16:9)
        options.setCropOutputPathDir(outputPath);           //自定义输出目录
        options.isCropDragSmoothToCenter(false);            //裁剪和拖动自动居中
        options.setSkipCropMimeType(skipCropMimeTypes);     //跳过裁剪图片的类型
        options.isForbidCropGifWebp(false);                 //是否禁止裁剪Gif & Webp (true的话, 裁剪Gif的时候, 会固定到裁剪第1帧)
        options.isForbidSkipMultipleCrop(true);             //切割多张图纸时禁止跳过
        options.setMaxScaleMultiplier(100F);                //最大放大倍数

        PictureSelectorStyle selectorStyle = PictureSelectorUtils.getPictureSelectorStyle();
        if (selectorStyle != null && selectorStyle.getSelectMainStyle().getStatusBarColor() != 0) {
            SelectMainStyle mainStyle = selectorStyle.getSelectMainStyle();
            boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
            int statusBarColor = mainStyle.getStatusBarColor();
            options.isDarkStatusBarBlack(isDarkStatusBarBlack);
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor);
                options.setToolbarColor(statusBarColor);
            } else {
                options.setStatusBarColor(ContextCompat.getColor(activity, com.luck.picture.lib.R.color.ps_color_grey));
                options.setToolbarColor(ContextCompat.getColor(activity, com.luck.picture.lib.R.color.ps_color_grey));
            }
            TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(activity, com.luck.picture.lib.R.color.ps_color_white));
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(activity, com.luck.picture.lib.R.color.ps_color_grey));
            options.setToolbarColor(ContextCompat.getColor(activity, com.luck.picture.lib.R.color.ps_color_grey));
            options.setToolbarWidgetColor(ContextCompat.getColor(activity, com.luck.picture.lib.R.color.ps_color_white));
        }
        return options;
    }


    /**
     * 创建自定义输出目录
     *
     * @return
     */
    protected String getSandboxPath(FragmentActivity activity) {
        File externalFilesDir = activity.getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }
}
