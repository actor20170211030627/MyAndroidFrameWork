package com.actor.myandroidframework.utils.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.actor.myandroidframework.utils.LogUtils;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Description: Glide加载Gif图片, 循环播放次数, 监听加载的开始和结束 <br />
 *
 * 示例使用:
 * <pre>
 * private MyImageViewTarget imageViewTarget;
 *
 * //初始化
 * imageViewTarget = new MyImageViewTarget(imageView, 2, new Animatable2Compat.AnimationCallback() {
 *     <b>@Override</b>
 *     public void onAnimationEnd(Drawable drawable) {
 *         super.onAnimationEnd(drawable);
 *           //imageView.setWillNotDraw(true);//清空图片
 *           //imageView.setImageResource(R.drawable.icon_frame_last);//设置最后1帧
 *     }
 * });
 *
 * //加载
 * Glide.with(this).asGif()
 *     .skipMemoryCache(true) //防止Gif重复播放时, 会先显示最后1帧的图片
 *     .placeholder(R.drawable.icon_frame_first) //第1帧的静态图片, 防止Gif重播时, 会闪动一下(可选)
 *     .load(R.drawable.gif_xxx)
 *     .into(imageViewTarget);
 * </pre>
 *
 * @author     : ldf
 * @date       : 2019/6/24 on 21:05
 * @version 1.0
 */
public class MyImageViewTarget extends ImageViewTarget<GifDrawable> {

    protected final int                                 loopCount;
    @Nullable
    protected final Animatable2Compat.AnimationCallback animationCallback;

    /**
     * @param imageView 图片
     * @param loopCount 循环次数
     */
    public MyImageViewTarget(ImageView imageView, @IntRange(from = 1) int loopCount, @Nullable Animatable2Compat.AnimationCallback animationCallback) {
        super(imageView);
        this.loopCount = loopCount;
        this.animationCallback = animationCallback;
    }

    //当前方法表示图片资源加载完成
    @Override
    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
        ImageView imageView = getView();
//        imageView.setWillNotDraw(false);    //设置图片
        resource.setLoopCount(loopCount);   //循环播放次数
        imageView.setImageDrawable(resource);
        resource.registerAnimationCallback(animationCallback);
        resource.start();
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {//开始加载图片
        super.onLoadStarted(placeholder);
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        LogUtils.errorFormat("gif加载失败, errorDrawable=%s", errorDrawable);
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
        super.onLoadCleared(placeholder);
    }

    @Nullable
    @Override
    public Drawable getCurrentDrawable() {//获取当前显示的 Drawable
        return super.getCurrentDrawable();
    }

    @Override
    public void onStart() {//Activity/Fragment 中的onStart
        super.onStart();
    }

    @Override
    public void onStop() {//Activity/Fragment 中的onStop
        super.onStop();
    }

    @Override
    protected void setResource(@Nullable GifDrawable resource) {
    }

    @Override
    public void onDestroy() {//Activity/Fragment 中的onDestroy
        super.onDestroy();
    }
}
