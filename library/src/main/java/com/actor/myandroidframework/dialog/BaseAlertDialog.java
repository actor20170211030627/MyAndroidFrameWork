package com.actor.myandroidframework.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * Description: AlertDialog, 示例: <br />
 * 1.builder的方式
 * <pre>
 * AlertDialog alertDialog = new AlertDialog.Builder(this)
 *         .setTitle(title)         //不设置就没有
 *         .setMessage(message)     //不设置就没有
 *         .setCancelable(boolean)  //点击返回键 & 外部, 是否能取消dialog
 *         .setIcon(R.drawable.xxx/Drawable)
 *         .setCustomTitle(view)
 *         .setView(R.layout.xxx/View)
 *         .setContentView(R.layout.xxx/View)
 *
 *         //1.设置单选Items (RadioButton & 文字靠左)                     默认选中
 *         .setSingleChoiceItems(R.array.xxx/Cursor/CharSequence[]/ListAdapter, checkedItem, new DialogInterface.OnClickListener(){
 *             <code>@</code>Override
 *             public void onClick(DialogInterface dialog,int which){
 *                 //which: 点击后选中的哪一条
 *             }
 *         })
 *
 *         //2.多选(CheckBox & 文字靠左)
 *         .setMultiChoiceItems(R.array.xxx/Cursor/CharSequence[], boolean[] checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
 *             <code>@</code>Override
 *             public void onClick(DialogInterface dialog, int which, boolean isChecked) {
 *             }
 *         })
 *
 *         //3.设置Items(文字靠左)
 *         .setItems(R.array.xxx/CharSequence[], new DialogInterface.OnClickListener(){
 *             <code>@</code>Override
 *             public void onClick(DialogInterface dialog,int which){
 *             }
 *         })
 *
 *         //4.设置按钮(不设置就没有)
 *         .setPositiveButtonIcon(Drawable) //"确定"左侧的图标(下同)
 *         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
 *             <code>@</code>Override
 *             public void onClick(DialogInterface dialog, int which) {
 *             }
 *         })
 *         .setNeutralButton("中间/最左侧", new DialogInterface.OnClickListener() {
 *             <code>@</code>Override
 *             public void onClick(DialogInterface dialog, int which) {
 *             }
 *         })
 *         .setNegativeButton("取消", new DialogInterface.OnClickListener() {
 *             <code>@</code>Override
 *             public void onClick(DialogInterface dialog, int which) {
 *             }
 *         })
 *
 *         //点击 外部/返回 取消
 *         .setOnCancelListener(new DialogInterface.OnCancelListener() {
 *             <code>@</code>Override
 *             public void onCancel(DialogInterface dialog) {
 *             }
 *         })
 *         //只要消失都回调
 *         .setOnDismissListener(new DialogInterface.OnDismissListener() {
 *             <code>@</code>Override
 *             public void onDismiss(DialogInterface dialog) {
 *             }
 *         })
 *         //5.如果什么都不设置, 直接.create(), 会默认有一个Dialog灰色背景
 *         .create();
 * </pre>
 *
 * 2.写一个类继承本类, 重写onCreate() & findViewById()
 *
 * @Author     : ldf
 * @Date       : 2020-1-14 on 12:01
 *
 * @version 1.0
 */
public abstract class BaseAlertDialog extends AlertDialog {

    protected Window window;
    public boolean isDismissError = false;  //dismiss的时候, 是否出错了

    public BaseAlertDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseAlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected BaseAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    protected void init() {
        window = getWindow();
        if (window != null) {
            /**
             * 背景透明, 不然设置自定义view {@link AlertDialog#setView(View)} 后, 后面有一个白色背景
             */
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes != null) {
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度全屏
//                window.setAttributes(attributes);
            }
         }
 }

     //子类可重写onCreate() & findViewById()
     @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         LogUtils.error(this.getClass().getName());
         int layoutResId = getLayoutResId();
         if (layoutResId != 0) setContentView(layoutResId);
//        findViewById();
     }

    /**
     * 设置你自定义Dialog的layout
     */
    protected abstract @LayoutRes int getLayoutResId();


    /**
     * dialog.show之前使用, view.findViewById()
     * 1.AlertDialog的方法, 在AlertDialog中调用AlertController.setView();
     * 2.AlertController的setupView()函数中整个窗体包含: topPanel，contentPanel，buttonPanel这三个部分。
     * 3.这个setView()指的CustomView()中间内容的部分而不是整个窗体
     */
    @Deprecated
    @Override
    public void setView(View view) {
        super.setView(view);
    }
    @Deprecated
    @Override
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        super.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    /**
     * dialog.show之后可以findViewById()
     * 父类Dialog的方法，在父类Dialog方法中，是调用了Window.setContentView(), 对应整个对话框窗口的view.
     */
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    /**
     * Service中打开Dialog(有些手机可能会崩溃, 最好写一个Dialog主题的Activity)
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
     */
    public BaseAlertDialog typeSystemAlert() {
        if (window != null) {
            //系统界别的dialog，即全局性质的dialog(...)
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        return this;
    }

    /**
     * 设置点击返回键 & 外部, 是否能取消dialog
     * //如果 setCancelable = true, setCanceledOnTouchOutside = true/false, 设置都有效
     * //如果 setCancelable = false, setCanceledOnTouchOutside = true, 点击 '返回'&'外部' 都能取消!!!
     */
    public BaseAlertDialog setCancelAble(boolean cancelAble) {
        setCancelable(cancelAble);
        setCanceledOnTouchOutside(cancelAble);//外部点击是否能取消
        return this;
    }

    /**
     * 设置窗口后面灰色大背景的亮度[0-1], 0最亮
     */
    public BaseAlertDialog setDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        if (window != null) window.setDimAmount(amount);
        return this;
    }

    /**
     * 设置Dialog位置
     * @param gravity {@link android.view.Gravity}
     * @return
     */
    public BaseAlertDialog setGravity(int gravity) {
        if (window != null) window.setGravity(gravity);
        return this;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            isDismissError = false;
        } catch (Exception e) {
            isDismissError = true;
            e.printStackTrace();
        }
    }
}
