package com.actor.sample.fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.actor.sample.databinding.FragmentViewPagerDetailBinding;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Map;

/**
 * description: ViewPager详情页
 * company    :
 * @author    : ldf
 * date       : 2025/5/10 on 14:23
 */
public class ViewPagerDetailFragment extends BaseFragment<FragmentViewPagerDetailBinding> {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private int position;
    private int startPosition;
    private boolean isChangeTransition;

    public static ViewPagerDetailFragment newInstance(int position, int startPosition, boolean isChangeTransition) {
        ViewPagerDetailFragment fragment = new ViewPagerDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        args.putInt(ARG_PARAM2, startPosition);
        args.putBoolean(ARG_PARAM3, isChangeTransition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);
            startPosition = getArguments().getInt(ARG_PARAM2);
            isChangeTransition = getArguments().getBoolean(ARG_PARAM3);
        }

        BaseSharedElementCallback enterSharedElementCallback = new BaseSharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }
        }.setLogPageTag("Fragment B Enter");
        //Fragment中设置, 不会回调!
        setEnterSharedElementCallback(enterSharedElementCallback);

        BaseSharedElementCallback exitSharedElementCallback = new BaseSharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }
        }.setLogPageTag("Fragment B Enter");
        //Fragment中设置, 不会回调!
        setExitSharedElementCallback(exitSharedElementCallback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (position == startPosition) {
                viewBinding.iv.setTransitionName(Global.getListTransitionName(position, isChangeTransition));
            }
        }
        viewBinding.tv.setText(getStringFormat("ViewPager, position = %d(请左右滑动)", position));

        Glide.with(this)
                .load(ImageConstants.IMAGE_SOURCE[position])
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startAnimation();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startAnimation();
                        return false;
                    }
                }).into(viewBinding.iv);
    }

    private void startAnimation() {
        if (position == startPosition) {
            //开始延时共享动画
            SharedElementUtils.startPostponedEnterTransition(mActivity);

            //因为是在Activity中暂停的动画, 所以调用↑代码重新开始动画, 而不是调用这句代码传入fragment对象, 否则没效果
//            SharedElementUtils.startPostponedEnterTransition(this);
        }
    }

    public View getSharedElementView() {
        return viewBinding.iv;
    }
}
