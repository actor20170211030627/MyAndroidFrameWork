package com.ly.sample.dialog;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.view.View;

import com.actor.myandroidframework.widget.BaseBottomDialog;
import com.ly.sample.R;

/**
 * Description: 键盘输入Dialog
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/12/5 on 11:17
 *
 * @version 1.0
 */
public class KeyboardBottomDialog extends BaseBottomDialog {

    private KeyboardView keyboardView;

    public KeyboardBottomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_keyboard_bottom;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        keyboardView = findViewById(R.id.keyboard_view);
        keyboardView.setVisibility(View.VISIBLE);
        setDimAmount(0.5F);//背景昏暗程度
    }

    public KeyboardView getKeyboardView() {
        return keyboardView;
    }
}
