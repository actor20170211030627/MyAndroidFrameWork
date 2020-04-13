package com.actor.sample.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.widget.EditText;

import com.actor.myandroidframework.dialog.BaseBottomDialog;
import com.actor.sample.R;
import com.blankj.utilcode.util.ToastUtils;

/**
 * description: 从底部弹出, 可输入的Dialog, 不遮挡键盘.
 * company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * author     : 李大发
 * date       : 2020/4/13 on 17:46
 *
 * @version 1.0
 */
public class BottomFloatEditorDialog extends BaseBottomDialog {

    private EditText         etContent;
    private OnResultListener listener;

    public BottomFloatEditorDialog(@NonNull Context context, OnResultListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_float_editor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            ToastUtils.showShort("dismiss");
            dismiss();
        });
        findViewById(R.id.tv_send).setOnClickListener(v -> {
            Editable text = etContent.getText();
            ToastUtils.showShort(text);
            if (listener != null) listener.onResult(text);
            dismiss();
        });
        etContent = findViewById(R.id.et_content);
    }

    public interface OnResultListener {
        void onResult(CharSequence content);
    }
}
