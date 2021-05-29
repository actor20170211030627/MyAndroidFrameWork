package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.activity.BaseBottomActivity;
import com.actor.sample.R;
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 从底部弹出的Activity
 * Author     : ldf
 * Date       : 2019/8/24 on 21:58
 *
 * @version 1.0
 */
public class MyBaseBottomActivity extends BaseBottomActivity {

    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_base_bottom_sheet_dialog);
        ButterKnife.bind(this);
        tvTips.setVisibility(View.INVISIBLE);
        tvContent.setText("this is BaseBottomActivity, Click me(点击我试一下)");
        setDimAmount(0.5F);
    }

    @OnClick({R.id.btn_dismiss, R.id.btn_ok, R.id.tv_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_dismiss:
                onBackPressed();
                break;
            case R.id.btn_ok:
                ToastUtils.showShort("ok~");
                break;
            case R.id.tv_content:
                ToastUtils.showShort("you clicked me!!");
                break;
        }
    }
}
