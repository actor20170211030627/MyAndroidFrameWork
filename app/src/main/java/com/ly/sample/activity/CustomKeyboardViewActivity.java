package com.ly.sample.activity;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

import com.actor.myandroidframework.widget.keyboard.CustomKeyboardEditText;
import com.ly.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description: 主页->自定义KeyBoardView
 * Author     : 李大发
 * Date       : 2019/10/12 on 16:02
 */
public class CustomKeyboardViewActivity extends BaseActivity {

    @BindView(R.id.custom_keyboard_edittext)
    CustomKeyboardEditText customKeyBoardEditText;
    @BindView(R.id.key_board_view)
    KeyboardView           keyboardView;

    private boolean isChange = true;//软键盘切换判断

    private Keyboard province_keyboard;//车牌省键盘
    private Keyboard number_keyboar;//车牌数字键盘

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_keyboard_view);
        ButterKnife.bind(this);

        customKeyBoardEditText.setKeyboardView(keyboardView, R.xml.keyboard_province_for_car_license,
                customKeyBoardEditText.new OnKeyboardActionListener() {
                    @Override
                    public void onKey(int primaryCode, int[] keyCodes) {
                        if (primaryCode == Keyboard.KEYCODE_SHIFT) {//切换输入法
                            changeKeyboard();
                        } else super.onKey(primaryCode, keyCodes);
                    }

                    //还可以重写其它方法, override other methods...
                });
        province_keyboard = keyboardView.getKeyboard();
        number_keyboar = new Keyboard(this, R.xml.keyboard_abc123_for_car_license);
    }

    /**
     * 按切换键时切换软键盘
     */
    public void changeKeyboard() {
        if (isChange) {
            keyboardView.setKeyboard(number_keyboar);
        } else {
            keyboardView.setKeyboard(province_keyboard);
        }
        isChange = !isChange;
    }
}
