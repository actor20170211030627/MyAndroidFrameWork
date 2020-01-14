package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

/**
 * Description: 类的描述
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2020-1-14 on 12:01
 *
 * @version 1.0
 */
public class BaseAlertDialogV7 extends AlertDialog {

    protected Window window;
    protected View view;

    protected BaseAlertDialogV7(@NonNull Context context) {
        super(context);
        init();
    }

    protected BaseAlertDialogV7(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected BaseAlertDialogV7(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    protected void init() {
        window = getWindow();
        if (window != null) {//背景透明, 不然自定义view点后面有一个白色框框
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    /**
     * 设置点击返回键 & 点击外部, 是否能取消dialog
     * //默认是 setCancelable()一样的值
     * //如果 setCancelable = true, setCanceledOnTouchOutside = true/false, 设置都有效
     * //如果 setCancelable = false, setCanceledOnTouchOutside = true, 点击 '返回'&'外部' 都能取消!!!
     */
    public BaseAlertDialogV7 setCancelAble(boolean cancelAble) {
        setCancelable(cancelAble);
        setCanceledOnTouchOutside(cancelAble);//外部点击是否能取消
        return this;
    }

    /**
     * 设置窗口后面灰色大背景的亮度[0-1], 0最亮
     */
    public BaseAlertDialogV7 setDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        if (window != null) window.setDimAmount(amount);
        return this;
    }

    public BaseAlertDialogV7 setView(@LayoutRes int layout) {
        View view = LayoutInflater.from(getContext()).inflate(layout, null, false);
        setView(view);
        return this;
    }

    public void setView(View view) {
        // TODO: 2019-1-14
        super.setView(view);
        setContentView(view);
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return view.findViewById(id);
    }
}
