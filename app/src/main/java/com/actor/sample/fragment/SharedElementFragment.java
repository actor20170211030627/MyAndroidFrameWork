package com.actor.sample.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.sample.R;
import com.actor.sample.activity.ViewPagerActivity;
import com.actor.sample.databinding.FragmentSharedElementBinding;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;
import com.bumptech.glide.Glide;

/**
 * Description: 元素共享中的Fragment
 * Author     : ldf
 * Date       : 2020/2/6 on 18:42
 */
public class SharedElementFragment extends BaseFragment<FragmentSharedElementBinding> {

    private ImageView iv;

    private int position = 1;//ImageConstants中第几张图片

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv = viewBinding.iv;
        viewBinding.btnStartActivity.setOnClickListener(this::onViewClicked);
        viewBinding.btnStartActivityForResult.setOnClickListener(this::onViewClicked);

        String url = ImageConstants.IMAGE_SOURCE[position];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setTransitionName(url);
        }
        Glide.with(this).load(url).into(iv);
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_activity://点击跳转Activity
                startActivity(new Intent(activity, ViewPagerActivity.class)
                        .putExtra(ViewPagerActivity.START_POSITION, position), iv);
                break;
            case R.id.btn_start_activity_for_result://
                startActivityForResult(new Intent(activity, ViewPagerActivity.class)
                        .putExtra(ViewPagerActivity.START_POSITION, position), 0, iv);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            showToast(data.getStringExtra(Global.CONTENT));
        }
    }
}
