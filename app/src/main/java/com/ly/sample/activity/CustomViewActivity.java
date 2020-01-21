package com.ly.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.actor.myandroidframework.widget.BaseRatingBar;
import com.actor.myandroidframework.widget.BaseSpinner;
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
    @BindView(R.id.base_spinner)
    BaseSpinner   baseSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        ButterKnife.bind(this);

        setTitle("主页->自定义View");
        baseRatingBar.setOnStarChangeListener(new BaseRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(BaseRatingBar baseRatingBar, float rating, boolean fromUser) {
                String format = getStringFormat("rating=%.2f, fromUser=%b", rating, fromUser);
                logFormat(format);
                toast(format);//String.valueOf(rating)
            }
        });
        baseSpinner.setOnItemSelectedListener(new BaseSpinner.OnItemSelectedListener2() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                logError("选中了: " + position);
                toast("选中了: " + position);
            }

            @Override
            public void onItemReSelected(AdapterView<?> parent, View view, int position, long id) {
                logError("重复选中了: " + position);
                toast("重复选中了: " + position);
            }
        });
    }
}
