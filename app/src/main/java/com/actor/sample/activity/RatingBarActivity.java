package com.actor.sample.activity;

import android.os.Bundle;

import com.actor.myandroidframework.widget.BaseRatingBar;
import com.actor.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description: 自定义RatingBar
 * @author    : 李大发
 * date       : 2020/7/14 on 11:23
 * @version 1.0
 */
public class RatingBarActivity extends BaseActivity implements BaseRatingBar.OnRatingBarChangeListener {

    @BindView(R.id.brb_1)
    BaseRatingBar brb1;
    @BindView(R.id.brb_2)
    BaseRatingBar brb2;
    @BindView(R.id.brb_3)
    BaseRatingBar brb3;
    @BindView(R.id.brb_4)
    BaseRatingBar brb4;
    @BindView(R.id.brb_5)
    BaseRatingBar brb5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_bar);
        ButterKnife.bind(this);

        setTitle("主页->自定义RatingBar");
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
        logError(format);
        toast(format);
    }
}