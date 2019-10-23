package com.actor.myandroidframework.widget.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.KeyboardUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Description: 自定义键盘的EditText
 * 1.屏蔽系统键盘
 * 2.原生EditText长按会弹出"选择, 全选, 粘贴"
 * 3.原生EditText有文字时, 双击会选中文字
 *
 * 示例用法:
 * <com.actor.myandroidframework.widget.CustomKeyBoardEditText
 *     android:id="@+id/custom_keyboard_edittext"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content">
 *
 *     <!--EditText不需要写id-->
 *     <EditText
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         android:hint="请输入车牌号" />
 * </com.actor.myandroidframework.widget.CustomKeyBoardEditText>
 *
 * customKeyBoardEditText.setKeyboardView(keyboardView, R.xml.keyboard_province_for_car_license,
 *         customKeyBoardEditText.new OnKeyboardActionListener() {
 *             @Override
 *             public void onKey(int primaryCode, int[] keyCodes) {
 *                 if (primaryCode == Keyboard.KEYCODE_SHIFT) {//切换输入法
 *                     changeKeyboard();
 *                 } else super.onKey(primaryCode, keyCodes);
 *             }
 *
 *             //还可以重写其它方法, override other methods...
 *         });
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/10/10 on 16:22
 *
 * @version 1.0
 * @version 1.0.1 把上层View改成TextView, 修复了换行时, 上层View不能及时遮盖下层EditText,
 *          导致能点击到下方EditText的bug
 */
public class CustomKeyboardEditText extends FrameLayout {

    private EditText           editText;
    private KeyboardView       keyboardView;//键盘View

    public CustomKeyboardEditText(Context context) {
        super(context);
    }

    public CustomKeyboardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomKeyboardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        editText = (EditText) getChildAt(0);//android.support.v7.widget.AppCompatEditText
        if (editText == null) {
            throw new RuntimeException("请在布局文件xml <CustomKeyBoardEditText 中添加EditText");
        }
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideSystemShowCustomKeyBoard(null);
                } else keyboardView.setVisibility(View.GONE);
            }
        });
        TextView tv = new TextView(getContext());//上方覆盖一层
        tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        tv.setBackgroundColor(getResources().getColor(R.color.red_trans_CC99));//标记范围
        tv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSystemShowCustomKeyBoard(event);
                return true;
            }
        });
        addView(tv);
    }

    //隐藏系统键盘, 显示自定义键盘
    protected void hideSystemShowCustomKeyBoard(MotionEvent event) {
        editText.requestFocus();
        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            int offsetForPosition = editText.getOffsetForPosition(x, y);
            editText.setSelection(offsetForPosition);
        }
        hideSoftInputMethod(editText);
        keyboardView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置键盘
     * @param keyboardView 键盘
     * @param xmlLayoutResId 键盘布局, R.xml.xxx
     * @param onKeyboardActionListener 键盘事件监听, 可传null, 可重写一些你想重写的方法, 传入示例:
     *                                 customKeyBoardEditText.new OnKeyboardActionListener() {}
     */
    public void setKeyboardView(@NonNull KeyboardView keyboardView, @XmlRes int xmlLayoutResId,
                                @Nullable OnKeyboardActionListener onKeyboardActionListener) {
        this.keyboardView = keyboardView;
        Keyboard keyboard = new Keyboard(getContext(), xmlLayoutResId);
        if (onKeyboardActionListener == null) {
            onKeyboardActionListener = new OnKeyboardActionListener();
        }
        List<Keyboard.Key> modifierKeys = keyboard.getModifierKeys();//isModifier=true时, ABC
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
        keyboardView.setVisibility(View.GONE);
        hideSoftInputMethod(editText);
    }

    //设置监听: new customKeyBoardEditText.new OnKeyboardActionListener() {}
    public class OnKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {

        @Override
        public void onPress(int primaryCode) {//按下key时执行s
            logError(getStringFormat("按下key时执行, primaryCode = %d", primaryCode));
            keyboardView.setPreviewEnabled(primaryCode >= 0);
        }

        @Override
        public void onRelease(int primaryCode) {
            //释放key时执行
            logError(getStringFormat("释放key时执行, primaryCode = %d", primaryCode));
        }

        /**
         * 点击key时执行(点击并松开), 可重写此方法
         * @param primaryCode 按键的Unicode编码, 比如:
         *      Keyboard.KEYCODE_SHIFT = -1;
         *      Keyboard.KEYCODE_MODE_CHANGE = -2;
         *      Keyboard.KEYCODE_CANCEL = -3;
         *      Keyboard.KEYCODE_DONE = -4;
         *      Keyboard.KEYCODE_DELETE = -5;
         *      Keyboard.KEYCODE_ALT = -6;
         *      自定义...
         * @param keyCodes
         */
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            logError(getStringFormat("primaryCode=%d, keyCodes=%s", primaryCode, Arrays.toString(keyCodes)));
            switch (primaryCode) {
                case Keyboard.KEYCODE_SHIFT:// 设置shift状态然后刷新页面
//                    pinyin26KB.setShifted(!pinyin26KB.isShifted());
//                    keyboardView.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DELETE://点击删除键，长按连续删除
//                    int start1 = editText.getSelectionStart();
//                    if (editText.length() > 0 && start1 > 0) {
//                        editText.getText().delete(start1 - 1, start1);
//                    }
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    editText.dispatchKeyEvent(event);
                    break;
                case Keyboard.KEYCODE_DONE:
                    keyboardView.setVisibility(GONE);
                    break;
                default:// 按下字母键
                    int start = editText.getSelectionStart();
                    int end = editText.getSelectionEnd();
                    boolean isInsert = false;
                    for (Keyboard.Key key : getKeys()) {
                        if (keyCodes[0] == key.codes[0]) {
                            editText.getText().insert(start, key.label);
                            isInsert = true;
                            break;
                        }
                    }
                    //其他code值, 比如小键盘没有在keys里面, 所以在这儿判断
                    if (!isInsert) {
                        String s = Character.toString((char) primaryCode);
                        editText.getText().insert(start, s);
                    }
                    break;
            }
        }

        //设置了 keyOutputText 属性后执行
        @Override
        public void onText(CharSequence text) {
            logError(text);
        }

        @Override
        public void swipeLeft() {
            logError("swipeLeft");
        }

        @Override
        public void swipeRight() {
            logError("swipeRight");
        }

        @Override
        public void swipeDown() {
            logError("swipeDown");
        }

        @Override
        public void swipeUp() {
            logError("swipeUp");
        }
    }

    protected String getStringFormat(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    protected void logError(Object object) {
        LogUtils.Error(String.valueOf(object), false);
    }


    /**
     * 禁掉系统软键盘
     */
    public void hideSoftInputMethod(TextView textView) {
        KeyboardUtils.hideSoftInput(textView);
    }

    /**
     * @return 输入框
     */
    public EditText getEditText() {
        return editText;
    }

    /**
     * @return 键盘
     */
    public Keyboard getKeyboard() {
        return keyboardView.getKeyboard();
    }

    /**
     * @return 当前键盘的keys
     */
    public List<Keyboard.Key> getKeys() {
        return getKeyboard().getKeys();
    }
}
