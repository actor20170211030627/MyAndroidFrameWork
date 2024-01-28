package com.actor.sample.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.dialog.ViewBindingDialog;
import com.actor.sample.databinding.FragmentBaseBottomSheetDialogBinding;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Description: 测试Dialog
 * Author     : ldf
 * Date       : 2020-1-21 on 14:45
 *
 * @version 1.0
 */
public class TestDialog extends ViewBindingDialog<FragmentBaseBottomSheetDialogBinding> {

    public TestDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.tvTips.setVisibility(View.INVISIBLE);
        viewBinding.tvContent.setText("this is TestDialog");
        viewBinding.btnDismiss.setOnClickListener(v ->
                dismiss()
        );
        viewBinding.btnOk.setOnClickListener(v ->
                ToastUtils.showShort("u clicked TestDialog")
        );
    }
}
