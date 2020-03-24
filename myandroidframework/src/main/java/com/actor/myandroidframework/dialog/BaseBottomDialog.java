package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;

import com.actor.myandroidframework.R;

/**
 * Description: 从底部弹出的Dialog, 不能上下拖拽滑动
 * Author     : 李大发
 * Date       : 2019/8/11 on 23:26
 * @version 1.0
 */
public abstract class BaseBottomDialog extends BaseDialog {

    public BaseBottomDialog(@NonNull Context context) {
        super(context, R.style.BaseBottomDialog);
    }

    public BaseBottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseBottomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes != null) {
                attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;//重心位置改为靠下居中显示
            }
        }
    }
}
