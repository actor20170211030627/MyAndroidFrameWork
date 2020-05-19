package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;

import com.actor.myandroidframework.R;

/**
 * description: 从右侧弹出的Dialog
 *
 * @author : 李大发
 * date       : 2020/5/19 on 16:09
 * @version 1.0
 */
public abstract class BaseRightDialog extends BaseDialog {

    public BaseRightDialog(@NonNull Context context) {
        super(context, R.style.BaseRightDialog);
    }

    public BaseRightDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseRightDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes != null) {
                attributes.gravity = Gravity.END | Gravity.CENTER_VERTICAL;//重心位置改为靠右居中显示
            }
        }
    }
}
