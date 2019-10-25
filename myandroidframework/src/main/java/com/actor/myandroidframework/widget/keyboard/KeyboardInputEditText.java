package com.actor.myandroidframework.widget.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.KeyboardUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * <com.actor.myandroidframework.widget.KeyboardInputEditText
 *     android:id="@+id/keyboard_input_edit_text"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content">
 *
 *     <!--EditText不需要写id-->
 *     <EditText
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         android:hint="请输入车牌号" />
 * </com.actor.myandroidframework.widget.KeyboardInputEditText>
 *
 * keyboardInputEditText.setKeyboardView(keyboardView, R.xml.keyboard_province_for_car_license,
 *         keyboardInputEditText.new OnKeyboardActionListener2() {
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
 * @version 1.0.2
 *      1.增加方法:
 *        {{@link #setKeyboardView(KeyboardView, Keyboard, OnKeyboardActionListener)}}
 *      2.这个类不能 implements TextUtil.GetTextAble, 不能用TextUtil.isNoEmpty()判空,
 *        因为会主动弹出系统键盘.
 *      3.增加方法 {@link #setOnFocusChangeListener(OnFocusChangeListener)},
 *        {@link #getOnKeyboardActionListener()}
 *      4.解决一个页面多个EditText共用一个KeyboardView, 导致输入时只能显示在最后一个EditText的问题
 */
public class KeyboardInputEditText extends FrameLayout {

    private static final String TAG = "KeyboardInputEditText";
    private EditText              editText;
    private KeyboardView          keyboardView;//键盘View
    private OnFocusChangeListener onFocusChangeListener;//EditText焦点改名的监听
    private OnKeyboardActionListener onKeyboardActionListener;

    public KeyboardInputEditText(Context context) {
        super(context);
    }

    public KeyboardInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        editText = (EditText) getChildAt(0);//android.support.v7.widget.AppCompatEditText
        if (editText == null) {
            throw new RuntimeException("请在.xml布局文件的 <KeyboardInputEditText 中添加EditText");
        }
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (onFocusChangeListener != null) onFocusChangeListener.onFocusChange(v, hasFocus);
                if (hasFocus) {
                    hideSystemShowCustomKeyBoard(null);
                    //因为一个页面有可能会多个EditText共用一个KeyboardView, 所以获取每个EditText绑定的监听
                    OnKeyboardActionListener listener =
                            (OnKeyboardActionListener) v.getTag(R.id.tag_to_get_okkeyboardlistener);
                    if (listener != null) {
                        onKeyboardActionListener = listener;
                        keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
                    }
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
     * @param keyboardView 键盘View
     * @param xmlLayoutResId 键盘布局, R.xml.xxx
     * @param onKeyboardActionListener 键盘事件监听, 可传null, 可重写一些你想重写的方法, 传入示例:
     *                                 keyboardInputEditText.new OnKeyboardActionListener2() {}
     */
    public void setKeyboardView(@NonNull KeyboardView keyboardView, @XmlRes int xmlLayoutResId,
                                @Nullable OnKeyboardActionListener onKeyboardActionListener) {
        Keyboard keyboard = new Keyboard(getContext(), xmlLayoutResId);
        setKeyboardView(keyboardView, keyboard, onKeyboardActionListener);
    }

    /**
     * 设置键盘
     * @param keyboardView 键盘View
     * @param keyboard 键盘, 可传null
     * @param onKeyboardActionListener 键盘事件监听, 可传null, 可重写一些你想重写的方法, 传入示例:
     *                                 keyboardInputEditText.new OnKeyboardActionListener2() {}
     */
    public void setKeyboardView(@NonNull KeyboardView keyboardView, @Nullable Keyboard keyboard,
                                @Nullable OnKeyboardActionListener onKeyboardActionListener) {
        this.keyboardView = keyboardView;
        if (onKeyboardActionListener == null) {
            onKeyboardActionListener = getOnKeyboardActionListenerByReflect();
            if (onKeyboardActionListener == null) {
                onKeyboardActionListener = new OnKeyboardActionListener2();
//                keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
            }
        } else {
//            keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
        }
        this.onKeyboardActionListener = onKeyboardActionListener;
        //因为一个页面有可能会多个EditText共用一个KeyboardView, 所以给每个EditText绑定一个监听
        editText.setTag(R.id.tag_to_get_okkeyboardlistener, onKeyboardActionListener);
        if (keyboard != null) {
            List<Keyboard.Key> modifierKeys = keyboard.getModifierKeys();//isModifier=true时, ABC
            keyboardView.setKeyboard(keyboard);
        }
        keyboardView.setVisibility(View.GONE);
        hideSoftInputMethod(editText);
    }

    //设置监听: new keyboardInputEditText.new OnKeyboardActionListener2() {}
    public class OnKeyboardActionListener2 implements KeyboardView.OnKeyboardActionListener {

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
        LogUtils.error(String.valueOf(object), false);
    }


    /**
     * @param onFocusChangeListener EditText焦点改名的监听
     */
    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
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
     * @return 输入框的内容
     */
    public Editable getText() {
        return getEditText().getText();
    }

    public void setText(CharSequence text) {
        getEditText().setText(text);
    }

    /**
     * @return 输入框提示文字
     */
    public CharSequence getHint() {
        return getEditText().getHint();
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
        Keyboard keyboard = getKeyboard();
        if (keyboard != null) return keyboard.getKeys();
        return null;
    }

    /**
     * @return 键盘行为现在的监听
     */
    public OnKeyboardActionListener getOnKeyboardActionListener() {
        return onKeyboardActionListener;
    }

    //通过反射获取键盘现在绑定的监听
    private OnKeyboardActionListener getOnKeyboardActionListenerByReflect() {
        try {
            Method method = keyboardView.getClass().getDeclaredMethod("getOnKeyboardActionListener");
            method.setAccessible(true);
            return (OnKeyboardActionListener) method.invoke(keyboardView);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清空输入框焦点
     */
    @Override
    public void clearFocus() {
        setFocusableInTouchMode(true);//设置父类focusableInTouchMode
        setFocusable(true);//设置父类focusable
        requestFocus();//设置父类获取focus
    }
}
