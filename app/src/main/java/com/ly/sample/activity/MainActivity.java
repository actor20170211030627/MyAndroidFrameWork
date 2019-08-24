package com.ly.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ly.sample.R;
import com.ly.sample.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.iv)
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setTitle("主页");
        Glide.with(this).load(Global.girl).into(iv);
    }

    @OnClick({R.id.btn_internet, R.id.btn_switch, R.id.btn_bottom_sheet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_internet://网络&图片
                startActivity(new Intent(this, NetWorkAndImageActivity.class), iv);
                break;
            case R.id.btn_switch://切换
                startActivity(new Intent(this, SwitcherActivity.class), view);
                break;
            case R.id.btn_bottom_sheet://从底部弹出的Dialog & DialogFragment等
                startActivity(new Intent(this, BottomSheetDialogActivity.class), view);
                break;
        }
    }
}
