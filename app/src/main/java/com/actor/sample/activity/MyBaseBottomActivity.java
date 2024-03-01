package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.activity.BaseBottomActivity;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.FragmentBaseBottomSheetDialogBinding;

/**
 * Description: 从底部弹出的Activity
 * Author     : ldf
 * Date       : 2019/8/24 on 21:58
 *
 * @version 1.0
 */
public class MyBaseBottomActivity extends BaseBottomActivity {

    private TextView tvTips;
    private TextView tvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentBaseBottomSheetDialogBinding viewBinding = FragmentBaseBottomSheetDialogBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        tvTips = viewBinding.tvTips;
        tvContent = viewBinding.tvContent;

        tvTips.setVisibility(View.INVISIBLE);
        tvContent.setText("this is BaseBottomActivity, Click me(点击我试一下)");
        setDimAmount(0.5F);
    }

//    @OnClick({R.id.btn_dismiss, R.id.btn_ok, R.id.tv_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_dismiss:
                finish();
                break;
            case R.id.btn_ok:
                ToasterUtils.success("ok~");
                break;
            case R.id.tv_content:
                ToasterUtils.info("you clicked me!!");
                break;
            default:
                break;
        }
    }
}
