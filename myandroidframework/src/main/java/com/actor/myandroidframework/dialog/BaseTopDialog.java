package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.R;

/**
 * description: 从顶部弹出的Dialog, 不能上下拖拽滑动
 *
 * @author : 李大发
 * date       : 2021/1/13 on 16
 * @version 1.0
 */
public abstract class BaseTopDialog extends BaseDialog {

    public BaseTopDialog(@NonNull Context context) {
        super(context, R.style.BaseTopDialog);
    }

    public BaseTopDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseTopDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes != null) {
                attributes.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;//重心位置改为靠顶居中显示
            }
        }
    }
}
