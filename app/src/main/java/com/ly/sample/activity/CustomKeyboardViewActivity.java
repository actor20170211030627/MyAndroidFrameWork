package com.ly.sample.activity;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actor.myandroidframework.widget.keyboard.KeyboardInputEditText;
import com.ly.sample.R;
import com.ly.sample.dialog.KeyboardBottomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->自定义KeyBoardView
 * Author     : 李大发
 * Date       : 2019/10/12 on 16:02
 */
public class CustomKeyboardViewActivity extends BaseActivity {

    @BindView(R.id.custom_keyboard_edittext)
    KeyboardInputEditText keyboardInputEditText;
    @BindView(R.id.custom_keyboard_edittext2)
    KeyboardInputEditText keyboardInputEditText2;
    @BindView(R.id.custom_keyboard_edittext3)
    KeyboardInputEditText keyboardInputEditText3;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.keyboard_view)
    KeyboardView     keyboardView;

    private KeyboardBottomDialog keyboardBottomDialog;//键盘Dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_keyboard_view);
        ButterKnife.bind(this);

        setTitle("主页->自定义KeyBoardView");
        keyboardInputEditText2.setText("贵ABC789789");

        //第一种方式, 键盘在Dialog里
        keyboardBottomDialog = new KeyboardBottomDialog(this);
        keyboardInputEditText.setupWithDialog(keyboardBottomDialog);
        keyboardInputEditText.setKeyboardView(keyboardBottomDialog.getKeyboardView(),
                R.xml.keyboard_province_for_car_license,
                R.xml.keyboard_abc123_for_car_license,
                keyboardInputEditText.new OnKeyboardActionListener2() {

                    @Override
                    public void onKey(int primaryCode, int[] keyCodes) {
                        switch (primaryCode) {
//                            case Keyboard.KEYCODE_SHIFT://切换输入法
//                                keyboardInputEditText.switchKeyboard();
//                                break;
                            default:
                                super.onKey(primaryCode, keyCodes);
                                break;
                        }
                        //没有输入内容时软键盘重置为省份简称软键盘
                        if (keyboardInputEditText.getText().length() == 0) {
                            keyboardInputEditText.switchKeyboard(true);
                        }
                        // FIXME: 2019/12/23 会索引越界
//                        else if (keyboardInputEditText.getText().length() == 1) {
//                            keyboardInputEditText.switchKeyboard(false);
//                        }
                    }

                    //还可以重写其它方法, override other methods...
                });


        //第2种方式, 键盘在xml布局中
        keyboardInputEditText2.setKeyboardView(keyboardView,
                R.xml.keyboard_province_for_car_license,
                R.xml.keyboard_abc123_for_car_license,
                keyboardInputEditText2.new OnKeyboardActionListener2() {
                    @Override
                    public void onKey(int primaryCode, int[] keyCodes) {
                        super.onKey(primaryCode, keyCodes);

                        //没有输入内容时软键盘重置为省份简称软键盘
                        if (keyboardInputEditText2.getText().length() == 0) {
                            keyboardInputEditText2.switchKeyboard(true);
                        }
//                        else if (keyboardInputEditText2.getText().length() == 1) {
//                            keyboardInputEditText2.switchKeyboard(false);
//                        }
                    }

                    //还可以重写其它方法, override other methods...
                });
        //第2种方式, 键盘在xml布局中
        keyboardInputEditText3.setKeyboardView(keyboardView,
                R.xml.keyboard_province_for_car_license,
                R.xml.keyboard_abc123_for_car_license,
                keyboardInputEditText3.new OnKeyboardActionListener2() {
                    @Override
                    public void onKey(int primaryCode, int[] keyCodes) {
                        super.onKey(primaryCode, keyCodes);

                        //没有输入内容时软键盘重置为省份简称软键盘
//                        if (keyboardInputEditText3.getText().length() == 0) {
//                            keyboardInputEditText3.switchKeyboard(true);
//                        }
//                        else if (keyboardInputEditText3.getText().length() == 1) {
//                            keyboardInputEditText3.switchKeyboard(false);
//                        }
                    }

                    //还可以重写其它方法, override other methods...
                });
    }

    @OnClick(R.id.btn)
    public void onViewClicked(View view) {
    }
}
