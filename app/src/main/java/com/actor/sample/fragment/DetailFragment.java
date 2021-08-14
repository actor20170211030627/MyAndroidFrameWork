package com.actor.sample.fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.sample.databinding.FragmentDetailBinding;
import com.actor.sample.utils.ImageConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class DetailFragment extends BaseFragment<FragmentDetailBinding> {

    private View      viewBackground;
    private ImageView iv;
    private TextView  tv;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int position;
    private int startPosition;


    public DetailFragment() {
    }

    public static DetailFragment newInstance(int position, int startPosition) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        args.putInt(ARG_PARAM2, startPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);
            startPosition = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBackground = viewBinding.viewBackground;
        iv = viewBinding.iv;
        tv = viewBinding.tv;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setTransitionName(ImageConstants.IMAGE_SOURCE[position]);//setTransitionName
        }
        tv.setText(String.valueOf(position));
        Glide.with(this).load(ImageConstants.IMAGE_SOURCE[position]).listener(new RequestListener<Drawable>() {
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
        }).into(iv);
    }

    private void startAnimation() {
        if (position == startPosition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().startPostponedEnterTransition();//开始延时共享动画
        }
    }

    public View getSharedElementView() {
        return iv;
    }
}
