package com.actor.sample.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.dialog.ViewBindingDialog;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.databinding.DialogSpinnerShowingBinding;
import com.blankj.utilcode.util.SizeUtils;

/**
 * description: 测试 Spinner 在 悬浮窗 中显示
 * company    :
 *
 * @author : ldf
 * date       : 2026/9/11 on 13
 * @version 1.0
 */
public class SpinnerShowingDialog extends ViewBindingDialog<DialogSpinnerShowingBinding> {

    private String title;

    public SpinnerShowingDialog(@NonNull Context context, String title) {
        super(context);
        this.title = title;
        setWidthPercent(0.8647f, SizeUtils.dp2px(308f));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.tvTitle.setText(title);
        viewBinding.stv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToasterUtils.success(viewBinding.bs.getSelectedItem());
            }
        });
    }
}
