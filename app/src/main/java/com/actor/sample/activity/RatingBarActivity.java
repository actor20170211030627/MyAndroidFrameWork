package com.actor.sample.activity;

import android.os.Bundle;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.widget.BaseRatingBar;
import com.actor.sample.databinding.ActivityRatingBarBinding;

/**
 * description: 自定义RatingBar
 * @author    : ldf
 * date       : 2020/7/14 on 11:23
 * @version 1.0
 */
public class RatingBarActivity extends BaseActivity<ActivityRatingBarBinding> implements BaseRatingBar.OnRatingBarChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->自定义RatingBar");
        BaseRatingBar brb1 = viewBinding.brb1;
        BaseRatingBar brb2 = viewBinding.brb2;
        BaseRatingBar brb3 = viewBinding.brb3;
        BaseRatingBar brb4 = viewBinding.brb4;
        BaseRatingBar brb5 = viewBinding.brb5;

        brb1.setOnStarChangeListener(this);
        brb2.setOnStarChangeListener(this);
        brb3.setOnStarChangeListener(this);
        brb4.setOnStarChangeListener(this);
        brb5.setOnStarChangeListener(this);
    }

    @Override
    public void onRatingChanged(BaseRatingBar baseRatingBar, float rating, boolean fromUser) {
        float stepSize = baseRatingBar.getStepSize();
        String format = getStringFormat("step = %f, rating=%f, fromUser=%b", stepSize, rating, fromUser);
        LogUtils.error(format);
        showToast(format);
    }
}