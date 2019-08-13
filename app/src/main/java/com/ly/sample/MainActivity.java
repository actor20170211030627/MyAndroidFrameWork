package com.ly.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends ActorBaseActivity {

    //林允儿
//    public static final String girl                  = "https://timgsa.baidu" +
//            ".com/timg?image&quality=80&size=b10000_10000&sec=1553570762&di" +
//            "=345ca57cc11ccf228e3ff8c2b33af03b&src=http://ww2.sinaimg" +
//            ".cn/large/9eb5883egw1euqvwfpmevj21kj2cok3p.jpg";
    public static final String girl = "http://ww2.sinaimg" +
            ".cn/large/9eb5883egw1euqvwfpmevj21kj2cok3p.jpg";

    @BindView(R.id.tv_hello)
    TextView  tvHello;
    @BindView(R.id.iv)
    ImageView iv;

    private MyBottomSheetDialogFragment bottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toast("yes~~");

        tvHello.setText("ButterKnife is ok~");
        Glide.with(this).load(girl).into(iv);
        bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
        bottomSheetDialogFragment.setPeekHeight(ConvertUtils.dp2px(100));//首次弹出高度, 可不设置
//        bottomSheetDialogFragment.setMaxHeight(ConvertUtils.dp2px(300));//最大弹出高度, 可不设置
        bottomSheetDialogFragment.setDimAmount(0.3F);//设置背景昏暗度
    }

    @OnClick({R.id.btn_bottom_sheet_dialog_fragment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bottom_sheet_dialog_fragment:
                bottomSheetDialogFragment.show(getSupportFragmentManager());
                iv.postDelayed(() -> bottomSheetDialogFragment.dismiss(), 5000);//5s后消失
                break;
        }
    }
}
