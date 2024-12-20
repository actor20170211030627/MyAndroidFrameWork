package com.actor.myandroidframework.utils.glide;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * description: Glide加载Gif图片, 循环播放次数, 监听加载的开始和结束 <br />
 * 使用示例:
 * <pre>
 * private MyRequestListener requestListener;
 *
 * //初始化
 * requestListener = new MyRequestListener(2, new Animatable2Compat.AnimationCallback() {
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
 *     .listener(requestListener)
 *     .into(imageView);
 * </pre>
 *
 * @author : ldf
 * @date   : 2024/6/2 on 01
 */
public class MyRequestListener implements RequestListener<GifDrawable> {

    protected final int loopCount;
    @Nullable
    protected final Animatable2Compat.AnimationCallback animationCallback;

    /**
     * @param loopCount 循环播放次数
     * @param animationCallback 播放监听
     */
    public MyRequestListener(int loopCount, @Nullable Animatable2Compat.AnimationCallback animationCallback) {
        this.loopCount = loopCount;
        this.animationCallback = animationCallback;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
        if (resource != null) {
            resource.setLoopCount(loopCount);
            resource.registerAnimationCallback(animationCallback);
        }
        return false;
    }
}
