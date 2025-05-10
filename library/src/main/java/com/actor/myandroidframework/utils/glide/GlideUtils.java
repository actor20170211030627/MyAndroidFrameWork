package com.actor.myandroidframework.utils.glide;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RawRes;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * description: Glide加载图片常用方法
 *
 * @author : ldf
 * @date   : 2024/12/20 on 11
 * @version 1.0
 */
public class GlideUtils {

    /**
     * 加载assets图片
     * @param asset main/assets 目录下的图片, 例: logo.png
     */
    public static void loadAsset(@NonNull ImageView iv, @NonNull String asset) {
        Glide.with(iv).load("file:///android_asset/" + asset).into(iv);
    }



    public static void loadUri(@NonNull ImageView iv, @Nullable Uri uri) {
        loadUri(iv, null, null, uri);
    }

    public static void loadUri(@NonNull ImageView iv, @Nullable @DrawableRes Integer placeholder, @Nullable Uri uri) {
        loadUri(iv, placeholder, null, uri);
    }

    /**
     * 加载Uri图片
     * @param placeholder 预加载图片, R.drawable.xxx
     * @param error 加载出错后显示的图片
     * @param uri 例: Uri.parse("https://...")
     */
    public static void loadUri(@NonNull ImageView iv,
                               @Nullable @DrawableRes Integer placeholder,
                               @Nullable @DrawableRes Integer error,
                               @Nullable Uri uri) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(iv).load(uri);
        if (placeholder != null) requestBuilder = requestBuilder.placeholder(placeholder);
        if (error != null) requestBuilder = requestBuilder.error(error.intValue());
        requestBuilder.into(iv);
    }



    /**
     * 加载byte[]图片
     * @param model 图片字节
     */
    public static void loadBytes(@NonNull ImageView iv, @Nullable byte[] model) {
        Glide.with(iv).load(model).into(iv);
    }



    /**
     * 加载Raw图片
     * @param rawId res/raw 目录下的图片, 例: R.raw.logo
     */
    public static void loadRaw(@NonNull ImageView iv, @RawRes int rawId) {
        //这种也可以, rawName: res/raw 目录下的图片名称, 例: logo
//        String raw = TextUtils2.getStringFormat("android.resource://%s/raw/%s", iv.getContext().getPackageName(), "rawName");
        String raw = TextUtils2.getStringFormat("android.resource://%s/raw/%d", iv.getContext().getPackageName(), rawId);
        Glide.with(iv).load(raw).into(iv);
    }



    /**
     * 加载圆形图片
     * @param resourceId R.raw.xxx or R.drawable.xxx
     */
    public static void loadCircleCrop(@NonNull ImageView iv, @RawRes @DrawableRes int resourceId) {
        Glide.with(iv).load(resourceId).circleCrop().into(iv);
    }

    public static void loadCircleCrop(@NonNull ImageView iv, @NonNull String string) {
        loadCircleCrop(iv, null, null, string);
    }

    public static void loadCircleCrop(@NonNull ImageView iv, @Nullable @DrawableRes Integer placeholder, @NonNull String string) {
        loadCircleCrop(iv, placeholder, null, string);
    }

    /**
     * 加载圆形图片
     * @param placeholder 预加载图片, R.drawable.xxx
     * @param error 加载出错后显示的图片
     * @param string url或本地文件路径
     */
    public static void loadCircleCrop(@NonNull ImageView iv,
                                      @Nullable @DrawableRes Integer placeholder,
                                      @Nullable @DrawableRes Integer error,
                                      @NonNull String string) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(iv).load(string);
        if (placeholder != null) requestBuilder = requestBuilder.placeholder(placeholder);
        if (error != null) requestBuilder = requestBuilder.error(error.intValue());
        requestBuilder.circleCrop().into(iv);
    }



    /**
     * 加载圆角图片
     * @param resourceId 本地图片, R.raw.xxx or R.drawable.xxx
     * @param roundingRadius 圆角大小, 单位像素
     */
    public static void loadRoundedCorners(@NonNull ImageView iv,
                                          @RawRes @DrawableRes int resourceId,
                                          @Px @IntRange(from = 1) int roundingRadius) {
        if (roundingRadius <= 0) roundingRadius = 1;
        RequestBuilder<Drawable> requestBuilder = Glide.with(iv).load(resourceId);
        //默认: FIT_CENTER
        ImageView.ScaleType scaleType = iv.getScaleType();
        /**
         * xml中 android:scaleType="centerCrop" 和 RoundedCorners 冲突, 导致没有圆角. 要把CenterCrop写在前面
         */
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            requestBuilder = requestBuilder.transform(new CenterCrop(), new RoundedCorners(roundingRadius));
        } else {
            requestBuilder = requestBuilder.transform(new RoundedCorners(roundingRadius));
        }
        requestBuilder.into(iv);
    }

    /**
     * 加载圆角图片
     * @param string url或本地文件路径
     * @param roundingRadius 圆角大小, 单位像素
     */
    public static void loadRoundedCorners(@NonNull ImageView iv, @NonNull String string,
                                          @Px @IntRange(from = 1) int roundingRadius) {
        loadRoundedCorners(iv, null, null, string, roundingRadius);
    }

    public static void loadRoundedCorners(@NonNull ImageView iv,
                                          @Nullable @DrawableRes Integer placeholder,
                                          @NonNull String string,
                                          @Px @IntRange(from = 1) int roundingRadius) {
        loadRoundedCorners(iv, placeholder, null, string, roundingRadius);
    }

    /**
     * 加载圆角图片
     * @param placeholder 预加载图片, R.drawable.xxx
     * @param error 加载出错后显示的图片
     * @param string url或本地文件路径
     * @param roundingRadius 圆角大小, 单位像素
     */
    public static void loadRoundedCorners(@NonNull ImageView iv,
                                          @Nullable @DrawableRes Integer placeholder,
                                          @Nullable @DrawableRes Integer error,
                                          @NonNull String string,
                                          @Px @IntRange(from = 1) int roundingRadius) {
        if (roundingRadius <= 0) roundingRadius = 1;
        RequestBuilder<Drawable> requestBuilder = Glide.with(iv).load(string);
        if (placeholder != null) requestBuilder = requestBuilder.placeholder(placeholder);
        if (error != null) requestBuilder = requestBuilder.error(error.intValue());
        //默认: FIT_CENTER
        ImageView.ScaleType scaleType = iv.getScaleType();
        /**
         * xml中 android:scaleType="centerCrop" 和 RoundedCorners 冲突, 导致没有圆角. 要把CenterCrop写在前面
         */
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            requestBuilder = requestBuilder.transform(new CenterCrop(), new RoundedCorners(roundingRadius));
        } else {
            requestBuilder = requestBuilder.transform(new RoundedCorners(roundingRadius));
        }
        requestBuilder.into(iv);
    }



    /**
     * 加载本地Gif图片, 并指定播放次数
     * @param placeholder 预加载图片, 一般是第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
     * @param resourceId 本地gif图片, R.raw.xxx or R.drawable.xxx
     * @param requestListener gif播放监听
     */
    public static void loadGifLoop(@NonNull ImageView iv,
                                   @Nullable @DrawableRes Integer placeholder,
                                   @RawRes @DrawableRes   int resourceId,
                                   @Nullable GifRequestListener requestListener) {
        RequestBuilder<GifDrawable> requestBuilder = Glide.with(iv).asGif()
                //防止Gif重复播放时, 会先显示最后1帧的图片
                .skipMemoryCache(true);
        //第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
        if (placeholder != null) requestBuilder = requestBuilder.placeholder(placeholder);
        requestBuilder.load(resourceId)
                .listener(requestListener)
                .into(iv);
    }

    /**
     * 加载Gif图片, 并指定播放次数
     * @param placeholder 预加载图片, 一般是第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
     * @param string url或本地文件路径
     * @param requestListener gif播放监听
     */
    public static void loadGifLoop(@NonNull ImageView iv,
                                   @Nullable @DrawableRes Integer placeholder,
                                   @Nullable              String string,
                                   @Nullable GifRequestListener requestListener) {
        loadGifLoop(iv, placeholder, null, string, requestListener);
    }

    /**
     * 加载Gif图片, 并指定播放次数
     * @param placeholder 预加载图片, 一般是第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
     * @param error 加载出错后显示的图片
     * @param string url或本地文件路径
     * @param requestListener gif播放监听
     */
    public static void loadGifLoop(@NonNull ImageView iv,
                                   @Nullable @DrawableRes Integer placeholder,
                                   @Nullable @DrawableRes Integer error,
                                   @Nullable String string,
                                   @Nullable GifRequestListener requestListener) {
        RequestBuilder<GifDrawable> requestBuilder = Glide.with(iv).asGif()
                //防止Gif重复播放时, 会先显示最后1帧的图片
                .skipMemoryCache(true);
        //第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
        if (placeholder != null) requestBuilder = requestBuilder.placeholder(placeholder);
        if (error != null) requestBuilder = requestBuilder.error(error.intValue());
        requestBuilder.load(string)
                .listener(requestListener)
                .into(iv);
    }



    /**
     * 加载视频
     * @param string url或本地文件路径
     */
    public static void loadVideo(@NonNull ImageView iv, @Nullable String string) {
        loadVideoRound(iv, null, null, string, 0);
    }

    public static void loadVideo(@NonNull ImageView iv, @Nullable @DrawableRes Integer placeholder, @Nullable String string) {
        loadVideoRound(iv, placeholder, null, string, 0);
    }

    public static void loadVideo(@NonNull ImageView iv, @Nullable @DrawableRes Integer placeholder,
                                 @Nullable @DrawableRes Integer error, @Nullable String string) {
        loadVideoRound(iv, placeholder, error, string, 0);
    }



    /**
     * 加载视频, 带圆角
     * @param string url或本地文件路径
     * @param roundingRadius 圆角大小, 单位像素.
     */
    public static void loadVideoRound(@NonNull ImageView iv, @Nullable String string, @Px @IntRange(from = 1) int roundingRadius) {
        loadVideoRound(iv, null, null, string, roundingRadius);
    }

    public static void loadVideoRound(@NonNull ImageView iv, @Nullable @DrawableRes Integer placeholder,
                                 @Nullable String string, @Px @IntRange(from = 1) int roundingRadius) {
        loadVideoRound(iv, placeholder, null, string, roundingRadius);
    }

    /**
     * 加载视频, 带圆角
     * @param placeholder 预加载图片, R.drawable.xxx
     * @param error 加载出错后显示的图片
     * @param string url或本地文件路径
     * @param roundingRadius 圆角大小, 单位像素. 没圆角就传0
     */
    public static void loadVideoRound(@NonNull ImageView iv,
                                 @Nullable @DrawableRes Integer placeholder,
                                 @Nullable @DrawableRes Integer error,
                                 @Nullable String string,
                                 @Px @IntRange(from = 0) int roundingRadius) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(iv).load(string);
        if (placeholder != null) requestBuilder = requestBuilder.placeholder(placeholder);
        if (error != null) requestBuilder = requestBuilder.error(error);
        if (roundingRadius > 0) {
            ImageView.ScaleType scaleType = iv.getScaleType();
            /**
             * xml中 android:scaleType="centerCrop" 和 RoundedCorners 冲突, 导致没有圆角. 要把CenterCrop写在前面
             */
            if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                requestBuilder = requestBuilder.transform(new CenterCrop(), new RoundedCorners(roundingRadius));
            } else {
                requestBuilder = requestBuilder.transform(new RoundedCorners(roundingRadius));
            }
        }
        requestBuilder.into(iv);
    }



    /**
     * 加载视频第x帧
     * @param string url或本地文件路径
     * @param frameTimeMicros 所需帧的时间位置，单位微秒(1秒 = 1000毫秒 = 1_000_000微秒)
     */
    public static void loadVideoFrame(@NonNull ImageView iv, @Nullable String string,
                                      @IntRange(from = 0) long frameTimeMicros) {
        loadVideoFrame(iv, null, null, string, frameTimeMicros, 0);
    }

    public static void loadVideoFrame(@NonNull ImageView iv, @Nullable String string,
                                      @IntRange(from = 0) long frameTimeMicros,
                                      @Px @IntRange(from = 1) int roundingRadius) {
        loadVideoFrame(iv, null, null, string, frameTimeMicros, roundingRadius);
    }

    /**
     * 加载视频第x帧
     * @param placeholder 预加载图片, R.drawable.xxx
     * @param error 加载出错后显示的图片
     * @param string url或本地文件路径
     * @param frameTimeMicros 所需帧的时间位置，单位微秒(1秒 = 1000毫秒 = 1_000_000微秒)
     * @param roundingRadius 圆角大小, 单位像素. 没圆角就传0
     */
    public static void loadVideoFrame(@NonNull ImageView iv,
                                 @Nullable @DrawableRes Integer placeholder,
                                 @Nullable @DrawableRes Integer error,
                                 @Nullable String string,
                                 @IntRange(from = 0) long frameTimeMicros,
                                 @Px @IntRange(from = 0) int roundingRadius) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        if (placeholder != null) requestOptions = requestOptions.placeholder(placeholder);
        if (error != null) requestOptions = requestOptions.error(error);
        if (roundingRadius > 0) {
            ImageView.ScaleType scaleType = iv.getScaleType();
            /**
             * xml中 android:scaleType="centerCrop" 和 RoundedCorners 冲突, 导致没有圆角. 要把CenterCrop写在前面
             */
            if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(roundingRadius));
            } else {
                requestOptions = requestOptions.transform(new RoundedCorners(roundingRadius));
            }
        }
        Glide.with(iv).load(string)
                .apply(requestOptions)
                .into(iv);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 其它方法
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 清空磁盘缓存
     */
    public static void clearDiskCache() {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() throws Throwable {
                //清除磁盘缓存(必须在子线程中调用,否则报错)
                Glide.get(ConfigUtils.APPLICATION).clearDiskCache();
                return null;
            }
            @Override
            public void onSuccess(Void result) {
            }
        });
    }

    /**
     * 清空内存缓存
     */
    public static void clearMemory() {
        Glide.get(ConfigUtils.APPLICATION).clearMemory();
    }

    /**
     * 获取图片缓存目录
     */
    @Nullable
    public static File getPhotoCacheDir() {
        return Glide.getPhotoCacheDir(ConfigUtils.APPLICATION);
    }
}
