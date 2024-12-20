package com.actor.myandroidframework.utils.glide;

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

    /**
     * 加载Uri图片
     * @param uri 例: Uri.parse("https://...")
     */
    public static void loadUri(@NonNull ImageView iv, @Nullable Uri uri) {
        Glide.with(iv).load(uri).into(iv);
    }

    /**
     * 加载Uri图片
     * @param model 图片字节
     */
    public static void loadBytes(@NonNull ImageView iv, @Nullable byte[] model) {
        Glide.with(iv).load(model).into(iv);
    }

    /**
     * 加载Uri图片
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
     * @param string url或本地文件路径
     */
    public static void loadCircleCrop(@NonNull ImageView iv, @NonNull String string) {
        Glide.with(iv).load(string).circleCrop().into(iv);
    }

    /**
     * 加载圆角图片
     * @param string url或本地文件路径
     * @param roundingRadius 圆角大小, 单位像素
     */
    public static void loadRoundedCorners(@NonNull ImageView iv,
                                                                     @NonNull String string,
                                                                     @Px @IntRange(from = 1) int roundingRadius) {
        if (roundingRadius <= 0) roundingRadius = 1;
        Glide.with(iv).load(string)
                //xml中CenterCrop和RoundedCorners冲突,导致没有圆角. 把CenterCrop写在前面
                .transform(new CenterCrop(), new RoundedCorners(roundingRadius))
                .into(iv);
    }

    /**
     * 加载本地Gif图片, 并指定播放次数
     * @param placeholder 预加载图片, 一般是第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
     * @param resourceId 本地gif图片
     * @param requestListener gif播放监听
     */
    public static void loadGifLoop(@NonNull ImageView iv,
                                   @Nullable @DrawableRes Integer placeholder,
                                   @RawRes @DrawableRes   int resourceId,
                                   @Nullable              MyRequestListener requestListener) {
        RequestBuilder<GifDrawable> gifDrawableRequestBuilder = Glide.with(iv).asGif()
                //防止Gif重复播放时, 会先显示最后1帧的图片
                .skipMemoryCache(true);
        if (placeholder != null) {
            //第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
            gifDrawableRequestBuilder.placeholder(placeholder);
        }
        gifDrawableRequestBuilder.load(resourceId)
                .listener(requestListener)
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
