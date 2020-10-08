package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.R;

/**
 * description: 从左侧弹出的Dialog
 *
 * @author : 李大发
 * date       : 2020/5/19 on 16:09
 * @version 1.0
 */
public abstract class BaseLeftDialog extends BaseDialog {

    public BaseLeftDialog(@NonNull Context context) {
        super(context, R.style.BaseLeftDialog);
    }

    public BaseLeftDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseLeftDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes != null) {
                attributes.gravity = Gravity.START | Gravity.CENTER_VERTICAL;//重心位置改为靠左居中显示
            }
        }
    }
}
