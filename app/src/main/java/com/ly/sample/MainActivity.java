package com.ly.sample;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends ActorBaseActivity {

    //林允儿
//    public static final String girl                  = "https://timgsa.baidu" +
//            ".com/timg?image&quality=80&size=b10000_10000&sec=1553570762&di" +
//            "=345ca57cc11ccf228e3ff8c2b33af03b&src=http://ww2.sinaimg" +
//            ".cn/large/9eb5883egw1euqvwfpmevj21kj2cok3p.jpg";
    public static final String girl = "http://ww2.sinaimg.cn/large/9eb5883egw1euqvwfpmevj21kj2cok3p.jpg";

    @BindView(R.id.tv_hello)
    TextView  tvHello;
    @BindView(R.id.iv)
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toast("yes~~");

        Glide.with(this).load(girl).into(iv);
    }
}
