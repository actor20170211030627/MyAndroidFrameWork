package com.actor.sample.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.dialog.BaseDialog;
import com.actor.sample.R;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Description: 测试Dialog
 * Author     : ldf
 * Date       : 2020-1-21 on 14:45
 *
 * @version 1.0
 */
public class TestDialog extends BaseDialog {

    public TestDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.tv_tips).setVisibility(View.INVISIBLE);
        TextView tv = findViewById(R.id.tv_content);
        tv.setText("this is TestDialog");
        findViewById(R.id.btn_dismiss).setOnClickListener(v -> dismiss());
        findViewById(R.id.btn_ok).setOnClickListener(v -> ToastUtils.showShort("u clicked TestDialog"));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_base_bottom_sheet_dialog;
    }
}
