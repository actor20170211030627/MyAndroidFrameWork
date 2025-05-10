package com.actor.myandroidframework.utils.glide;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * description: Glide加载普通图片监听 <br />
 * 使用示例:
 * <pre>
 * private DrawableRequestListener requestListener = new DrawableRequestListener() {
 *     //重写方法...
 * };
 *
 * //加载
 * Glide.with(this)
 *     .placeholder(R.drawable.icon_error)  //(可选)
 *     .error(R.drawable.icon_error)        //(可选)
 *     .load(url)
 *     .listener(requestListener)
 *     .into(imageView);
 * </pre>
 *
 * @author : ldf
 * @date   : 2024/6/2 on 01
 */
public class DrawableRequestListener implements RequestListener<Drawable> {

    Object requestTag;

    public DrawableRequestListener() {
    }

    /**
     * 设置tag
     * @param requestTag 可以标记一个tag, 比如position
     */
    public DrawableRequestListener(Object requestTag) {
        this.requestTag = requestTag;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        // 返回false允许Glide继续尝试其他资源
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        // 返回false允许Glide继续尝试其他资源
        return false;
    }

    @Nullable
    public <T extends Object> T getRequestTag() {
        return (T) requestTag;
    }
}
