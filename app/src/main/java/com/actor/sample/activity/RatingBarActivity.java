package com.actor.sample.activity;

import android.os.Bundle;
import android.widget.RatingBar;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.myandroidframework.widget.BaseRatingBar;
import com.actor.sample.databinding.ActivityRatingBarBinding;

/**
 * description: 自定义RatingBar
 * @author    : ldf
 * date       : 2020/7/14 on 11:23
 * @version 1.0
 */
public class RatingBarActivity extends BaseActivity<ActivityRatingBarBinding>
        implements RatingBar.OnRatingBarChangeListener, BaseRatingBar.OnRatingBarChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->自定义RatingBar");
        viewBinding.rb8.setOnRatingBarChangeListener(this);
        viewBinding.brb0.setOnRatingBarChangeListener(this);
        viewBinding.brb1.setOnRatingBarChangeListener(this);
        viewBinding.brb2.setOnRatingBarChangeListener(this);
        viewBinding.brb3.setOnRatingBarChangeListener(this);
        viewBinding.brb4.setOnRatingBarChangeListener(this);
        viewBinding.brb5.setOnRatingBarChangeListener(this);
        viewBinding.brb6.setOnRatingBarChangeListener(this);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        float stepSize = ratingBar.getStepSize();
        String format = getStringFormat("step = %f, rating=%f, fromUser=%b", stepSize, rating, fromUser);
        LogUtils.error(format);
        ToasterUtils.info(format);
    }

    @Override
    public void onRatingChanged(@NonNull BaseRatingBar ratingBar, float rating, boolean fromUser) {
        float stepSize = ratingBar.getStepSize();
        String format = getStringFormat("step = %f, rating=%f, fromUser=%b", stepSize, rating, fromUser);
        LogUtils.error(format);
        ToasterUtils.info(format);
    }
}