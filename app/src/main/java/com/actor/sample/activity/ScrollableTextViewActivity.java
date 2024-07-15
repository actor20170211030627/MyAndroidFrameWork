package com.actor.sample.activity;

import android.os.Bundle;

import com.actor.sample.R;
import com.actor.sample.databinding.ActivityScrollableTextViewBinding;

/**
 * description: 可滚动TextView
 * company    :
 * @author    : ldf
 * date       : 2024/7/15 on 11:50
 */
public class ScrollableTextViewActivity extends BaseActivity<ActivityScrollableTextViewBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ScrollableTextView");
        viewBinding.btnSetShort.setOnClickListener(v -> {
            viewBinding.stv0.setText(R.string.hello_blank_fragment);
            viewBinding.stv1.setText(R.string.hello_blank_fragment);
        });

        viewBinding.btnSetLong.setOnClickListener(v -> {
            viewBinding.stv0.setText(R.string.long_text);
            viewBinding.stv1.setText(R.string.long_text);
        });
    }
}