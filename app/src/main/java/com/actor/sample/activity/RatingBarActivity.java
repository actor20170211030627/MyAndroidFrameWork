package com.actor.sample.activity;

import android.os.Bundle;

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
public class RatingBarActivity extends BaseActivity<ActivityRatingBarBinding> implements BaseRatingBar.OnRatingBarChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->自定义RatingBar");
        viewBinding.brb0.setOnStarChangeListener(this);
        viewBinding.brb1.setOnStarChangeListener(this);
        viewBinding.brb2.setOnStarChangeListener(this);
        viewBinding.brb3.setOnStarChangeListener(this);
        viewBinding.brb4.setOnStarChangeListener(this);
        viewBinding.brb5.setOnStarChangeListener(this);
        viewBinding.brb6.setOnStarChangeListener(this);
    }

    @Override
    public void onRatingChanged(BaseRatingBar baseRatingBar, float rating, boolean fromUser) {
        float stepSize = baseRatingBar.getStepSize();
        String format = getStringFormat("step = %f, rating=%f, fromUser=%b", stepSize, rating, fromUser);
        LogUtils.error(format);
        ToasterUtils.info(format);
    }
}