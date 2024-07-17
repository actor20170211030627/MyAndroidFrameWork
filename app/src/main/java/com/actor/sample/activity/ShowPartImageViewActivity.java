package com.actor.sample.activity;

import android.os.Bundle;

import com.actor.sample.R;
import com.actor.sample.databinding.ActivityShowPartImageViewBinding;

/**
 * description: 仅显示图片指定部分的内容
 * company    :
 * @author    : ldf
 * date       : 2024/7/12 on 20:35
 */
public class ShowPartImageViewActivity extends BaseActivity<ActivityShowPartImageViewBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ShowPartImageView");
        //top/bottom
        viewBinding.btnTopOrBottom.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                viewBinding.spiv40.setShowGravity(1);
                viewBinding.spiv42.setShowGravity(1);
            } else {
                viewBinding.spiv40.setShowGravity(0);
                viewBinding.spiv42.setShowGravity(0);
            }
        });
        //top/bottom
        viewBinding.btnLeftOrRight.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                viewBinding.spiv41.setShowGravity(3);
                viewBinding.spiv43.setShowGravity(3);
            } else {
                viewBinding.spiv41.setShowGravity(2);
                viewBinding.spiv43.setShowGravity(2);
            }
        });
        //
        viewBinding.switchAlignGravaty.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewBinding.spiv42.setIsAlignGravityWhenIv2WidthOrIv2Height(isChecked);
            viewBinding.spiv43.setIsAlignGravityWhenIv2WidthOrIv2Height(isChecked);
        });
        //换图
        viewBinding.btnSwitchImg.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                viewBinding.spiv40.setImageResource(R.drawable.location_camera);
                viewBinding.spiv41.setImageResource(R.drawable.location_camera);
                viewBinding.spiv42.setImageResource(R.drawable.location_camera);
                viewBinding.spiv43.setImageResource(R.drawable.location_camera);
            } else {
                viewBinding.spiv40.setImageResource(R.drawable.logo);
                viewBinding.spiv41.setImageResource(R.drawable.logo);
                viewBinding.spiv42.setImageResource(R.drawable.logo);
                viewBinding.spiv43.setImageResource(R.drawable.logo);
            }
        });
    }
}