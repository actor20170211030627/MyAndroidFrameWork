package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ViewBindingUtils;

/**
 * description: 在Dialog中使用 ViewBinding
 * company    :
 *
 * @author : ldf
 * @date   : 2024/1/3 on 10
 * @version 1.0
 */
public class ViewBindingDialog<VB extends ViewBinding> extends BaseDialog {

    public VB viewBinding;

    public ViewBindingDialog(@NonNull Context context) {
        super(context);
    }

    public ViewBindingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ViewBindingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected int getLayoutResId() {
        viewBinding = ViewBindingUtils.initViewBinding(this);
        if (viewBinding == null) {
            LogUtils.errorFormat("%s 获取viewBinding失败, if直接new %s()就会获取失败, 应该写一个子类Dialog extends %s, 才能正确获取到。",
                    this.getClass().getName(),
                    ViewBindingDialog.class.getSimpleName(),
                    ViewBindingDialog.class.getSimpleName()
            );
        } else {
            setContentView(viewBinding.getRoot());
        }
        return Resources.ID_NULL;
    }
}
