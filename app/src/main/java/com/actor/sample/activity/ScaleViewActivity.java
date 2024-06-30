package com.actor.sample.activity;

import android.os.Bundle;

import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.databinding.ActivityScaleViewBinding;

/**
 * description: View缩放
 * company    :
 * @author    : ldf
 * date       : 2024/6/20 on 17:27
 */
public class ScaleViewActivity extends BaseActivity<ActivityScaleViewBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("View缩放");

        viewBinding.btn1.setOnClickListener(v -> {
            ToasterUtils.success("btn1");
        });
        viewBinding.btn2.setOnClickListener(v -> {
            ToasterUtils.success("btn2");
        });
//        viewBinding.zoomView.setMinScale(0.2f);
    }
}