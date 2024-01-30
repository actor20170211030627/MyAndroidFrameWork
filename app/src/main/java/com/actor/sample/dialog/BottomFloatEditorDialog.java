package com.actor.sample.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.dialog.ViewBindingDialog;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.databinding.DialogFloatEditorBinding;

/**
 * description: 从底部弹出, 可输入的Dialog, 不遮挡键盘.
 * author     : ldf
 * date       : 2020/4/13 on 17:46
 *
 * @version 1.0
 */
public class BottomFloatEditorDialog extends ViewBindingDialog<DialogFloatEditorBinding> {

    private final OnResultListener listener;

    public BottomFloatEditorDialog(@NonNull Context context, OnResultListener listener) {
        super(context);
        this.listener = listener;
        setGravityAndAnimation(Gravity.BOTTOM, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.tvCancel.setOnClickListener(v -> {
            ToasterUtils.warning("dismiss");
            dismiss();
        });
        viewBinding.tvSend.setOnClickListener(v -> {
            Editable text = viewBinding.etContent.getText();
            ToasterUtils.success(text);
            if (listener != null) listener.onResult(text);
            dismiss();
        });
    }

    public interface OnResultListener {
        void onResult(CharSequence content);
    }
}
