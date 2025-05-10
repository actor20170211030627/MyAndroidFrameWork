package com.actor.sample.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.glide.DrawableRequestListener;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivitySharedElement2Binding;
import com.actor.sample.utils.Global;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;

/**
 * description: 共享元素 单元素跳转示例
 * company    :
 * @author    : ldf
 * date       : 2025/5/10 on 18:07
 */
public class SharedElement2Activity extends BaseActivity<ActivitySharedElement2Binding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->元素共享跳转->Activity");

        //清除默认的渐变动画
        SharedElementUtils.cleanTransitionInDestinationActivity(getWindow());

        /**
         * 推迟 Activity 或者 Fragment 的进入过渡动画。
         */
        SharedElementUtils.postponeEnterTransition(this);

        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .listener(new DrawableRequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        /**
                         * 启动之前被推迟的进入过渡动画。
                         */
                        SharedElementUtils.startPostponedEnterTransition(mActivity);
                        return super.onLoadFailed(e, model, target, isFirstResource);
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        /**
                         * 启动之前被推迟的进入过渡动画。
                         */
                        SharedElementUtils.startPostponedEnterTransition(mActivity);
                        return super.onResourceReady(resource, model, target, dataSource, isFirstResource);
                    }
                })
                .into(viewBinding.iv);
    }
}