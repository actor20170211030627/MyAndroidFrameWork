package com.ly.sample.activity;

import android.os.Bundle;

import com.actor.myandroidframework.widget.BaseRatingBar;
import com.ly.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description: 主页->自定义View
 * Author     : 李大发
 * Date       : 2019-8-27 on 17:37
 */
public class CustomViewActivity extends BaseActivity {

    @BindView(R.id.base_rating_bar)
    BaseRatingBar baseRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        ButterKnife.bind(this);

        baseRatingBar.setOnStarChangeListener(new BaseRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(BaseRatingBar baseRatingBar, float rating, boolean fromUser) {
                logFormat("baseRatingBar: rating=%f, fromUser=%b", rating, fromUser);
                toast(String.valueOf(rating));
            }
        });
    }
}
