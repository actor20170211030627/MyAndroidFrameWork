package com.actor.myandroidframework.widget.keyboard;

import android.app.Dialog;
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
import com.actor.myandroidframework.utils.TextUtil;
import com.blankj.utilcode.util.KeyboardUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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
 * keyboardInputEditText.setKeyboardView(keyboardView,
 *         R.xml.keyboard_province_for_car_license,
 *         R.xml.keyboard_abc123_for_car_license,//第2种键盘, 可用于切换
 *         keyboardInputEditText.new OnKeyboardActionListener2() {
 *             @Override
 *             public void onKey(int primaryCode, int[] keyCodes) {
 *                 super.onKey(primaryCode, keyCodes);
 *             }
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
 *        @see #setKeyboardView(KeyboardView, Keyboard, OnKeyboardActionListener)
 *      2.增加方法:
 *        @see #setOnFocusChangeListener(OnFocusChangeListener)
 *        @see #getOnKeyboardActionListener()
 *      3.解决一个页面多个EditText共用一个KeyboardView, 导致输入时只能显示在最后一个EditText的问题
 * @version 1.0.3
 *      增加方法:
 *          @see #setOnKeyboardViewVisibleChangeListener(OnKeyboardViewVisibleChangeListener)
 *          @see #setupWithView(View)
 *          @see #setupWithDialog(Dialog)
 * @version 1.0.5
 *      增加方法:
 *          @see #setKeyboardView(KeyboardView, int, int, OnKeyboardActionListener)
 *          @see #setKeyboardView(KeyboardView, Keyboard, Keyboard, OnKeyboardActionListener)
 *          @see #switchKeyboard()
 *          @see #switchKeyboard(boolean)
 */
public class KeyboardInputEditText extends FrameLayout implements TextUtil.GetTextAble {

    private              EditText                            editText;
    private              KeyboardView                        keyboardView;//键盘View
    private              Keyboard                            firstKeyboard;//第1种键盘
    private              Keyboard                            secondKeyboard;//第2种键盘, 可用于切换
    private              OnFocusChangeListener               onFocusChangeListener;//EditText焦点改名的监听
    private              OnKeyboardActionListener            onKeyboardActionListener;
    private              OnKeyboardViewVisibleChangeListener onKeyboardViewVisibleChangeListener;//键盘visible监听
    private Dialog keyboardViewDialog;//包含 KeyboardView 的Dialog, 控制show() & dismiss()
    private View keyboardViewContainer;//包含 KeyboardView 的View, 控制VISIBLE & GONE
    private boolean isFirstKeyboard = true;//是否是第1种键盘
    private              boolean                             isResetKeyboard;

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
                            (OnKeyboardActionListener) v.getTag(R.id.tag_to_get_onkeyboardlistener);
                    if (listener != null) {
                        onKeyboardActionListener = listener;
                        if (keyboardView != null) {
                            keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
                        }
                    }
                    //如果一个页面中其它EditText重新设置了键盘, 再设置回来
                    if (getKeyboard() != firstKeyboard && getKeyboard() != secondKeyboard) {
                        keyboardView.setKeyboard(isFirstKeyboard ? firstKeyboard : secondKeyboard);
                    }
                } else {
                    isFirstKeyboard = getKeyboard() == firstKeyboard;
                    showHideVisibleGoneNotify(false);
                }
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
        showHideVisibleGoneNotify(true);
    }

    /**
     * 设置键盘, 只有一种键盘, 不需要切换
     */
    public void setKeyboardView(@NonNull KeyboardView keyboardView, @XmlRes int firstKeyboard,
                                @Nullable OnKeyboardActionListener onKeyboardActionListener) {
        Keyboard keyboard1 = new Keyboard(getContext(), firstKeyboard);
        setKeyboardView(keyboardView, keyboard1, null, onKeyboardActionListener);
    }
    /**
     * 设置键盘
     *
     * @param keyboardView             键盘View
     * @param firstKeyboard            第1种键盘布局, R.xml.xxx
     * @param secondKeyboard           第2种键盘布局, R.xml.xxx, 可用于键盘切换
     * @param onKeyboardActionListener 键盘事件监听, 可传null, 可重写一些你想重写的方法, 传入示例:
     *                                 keyboardInputEditText.new OnKeyboardActionListener2() {}
     */
    public void setKeyboardView(@NonNull KeyboardView keyboardView, @XmlRes int firstKeyboard,
                                @XmlRes int secondKeyboard,
                                @Nullable OnKeyboardActionListener onKeyboardActionListener) {
        Keyboard keyboard1 = new Keyboard(getContext(), firstKeyboard);
        Keyboard keyboard2 = new Keyboard(getContext(), secondKeyboard);
        setKeyboardView(keyboardView, keyboard1, keyboard2, onKeyboardActionListener);
    }

    /**
     * 设置键盘, 只有一种键盘, 不需要切换
     */
    public void setKeyboardView(@NonNull KeyboardView keyboardView, @Nullable Keyboard firstKeyboard,
                                @Nullable OnKeyboardActionListener onKeyboardActionListener) {
        setKeyboardView(keyboardView, firstKeyboard, null, onKeyboardActionListener);
    }
    /**
     * 设置键盘
     *
     * @param keyboardView             键盘View
     * @param firstKeyboard            键盘, 可传null
     * @param secondKeyboard           第2种键盘布局, R.xml.xxx, 可用于键盘切换
     * @param onKeyboardActionListener 键盘事件监听, 可传null, 可重写一些你想重写的方法, 传入示例:
     *                                 keyboardInputEditText.new OnKeyboardActionListener2() {}
     */
    public void setKeyboardView(@NonNull KeyboardView keyboardView, @Nullable Keyboard firstKeyboard,
                                @Nullable Keyboard secondKeyboard,
                                @Nullable OnKeyboardActionListener onKeyboardActionListener) {
        this.keyboardView = keyboardView;
        this.firstKeyboard = firstKeyboard;
        this.secondKeyboard = secondKeyboard;
        setupWithView(keyboardView);
        if (onKeyboardActionListener == null) {
            onKeyboardActionListener = getOnKeyboardActionListenerByReflect();
            if (onKeyboardActionListener == null) {
                onKeyboardActionListener = new OnKeyboardActionListener2();
            }
        }
        this.onKeyboardActionListener = onKeyboardActionListener;
        //因为一个页面有可能会多个EditText共用一个KeyboardView, 所以给每个EditText绑定一个监听
        editText.setTag(R.id.tag_to_get_onkeyboardlistener, onKeyboardActionListener);
        if (firstKeyboard != null) {
            List<Keyboard.Key> modifierKeys = firstKeyboard.getModifierKeys();//isModifier=true时, ABC
            keyboardView.setKeyboard(firstKeyboard);
        }
        hideSoftInputMethod(editText);
    }

    //设置监听: new keyboardInputEditText.new OnKeyboardActionListener2() {}
    public class OnKeyboardActionListener2 implements KeyboardView.OnKeyboardActionListener {

        @Override
        public void onPress(int primaryCode) {//按下key时执行s
            logFormat("按下key时执行, primaryCode = %d", primaryCode);
            keyboardView.setPreviewEnabled(primaryCode >= 0);
        }

        @Override
        public void onRelease(int primaryCode) {
            //释放key时执行
            logFormat("释放key时执行, primaryCode = %d", primaryCode);
        }

        /**
         * 点击key时执行(点击并松开), 可重写此方法
         *
         * @param primaryCode 按键的Unicode编码, 比如:
         *                    Keyboard.KEYCODE_SHIFT = -1;
         *                    Keyboard.KEYCODE_MODE_CHANGE = -2;
         *                    Keyboard.KEYCODE_CANCEL = -3;
         *                    Keyboard.KEYCODE_DONE = -4;
         *                    Keyboard.KEYCODE_DELETE = -5;
         *                    Keyboard.KEYCODE_ALT = -6;
         *                    自定义...
         * @param keyCodes
         */
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            logFormat("primaryCode=%d, keyCodes=%s", primaryCode, Arrays.toString(keyCodes));
            switch (primaryCode) {
                case Keyboard.KEYCODE_SHIFT://-1 设置shift状态然后刷新页面
//                    pinyin26KB.setShifted(!pinyin26KB.isShifted());
                    switchKeyboard();
                    break;
                case Keyboard.KEYCODE_MODE_CHANGE://-2
                    break;
                case Keyboard.KEYCODE_CANCEL://-3 取消
                    break;
                case Keyboard.KEYCODE_DONE://-4
                    showHideVisibleGoneNotify(false);
                    break;
                case Keyboard.KEYCODE_DELETE://-5 点击删除键，长按连续删除
                    onDeletePressed();
                    break;
                case Keyboard.KEYCODE_ALT://-6
                    break;
                default:// 按下字母键
                    int start1 = editText.getSelectionStart();
//                    int end = editText.getSelectionEnd();
                    boolean isInsert = false;
                    for (Keyboard.Key key : getKeys()) {
                        if (primaryCode == key.codes[0]) {
                            editText.getText().insert(start1, key.label);
                            isInsert = true;
                            break;
                        }
                    }
                    //其他code值, 比如小键盘没有在keys里面, 所以在这儿判断
                    if (!isInsert) {
                        String s = Character.toString((char) primaryCode);
                        editText.getText().insert(start1, s);
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

    /**
     * 删除键
     */
    public void onDeletePressed() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
        editText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    protected void logError(Object object) {
        LogUtils.error(String.valueOf(object), false);
    }
    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(String.valueOf(format), false, args);
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
    @Override
    public EditText getEditText() {
        return editText;
    }

    /**
     * @return 输入框的内容
     */
    @Override
    public Editable getText() {
        return getEditText().getText();
    }

    public void setText(CharSequence text) {
        getEditText().setText(text);
    }

    /**
     * @return 输入框提示文字
     */
    @Override
    public CharSequence getHint() {
        return getEditText().getHint();
    }

    //如果输入内容为空, 不弹出系统键盘
    @Override
    public boolean keyboardShowAbleIfEditText() {
        return false;
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
    public @Nullable List<Keyboard.Key> getKeys() {
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
    //隐藏Dialog | View | notify
    private void showHideVisibleGoneNotify(boolean visible) {
        if (keyboardViewDialog != null) {
            if (visible) {
                keyboardViewDialog.show();
            } else keyboardViewDialog.dismiss();
        } else if (keyboardViewContainer != null) {//默认keyboardView
            keyboardViewContainer.setVisibility(visible ? VISIBLE : GONE);
        }
        if (onKeyboardViewVisibleChangeListener != null) {
            onKeyboardViewVisibleChangeListener.onKeyboardViewVisibleChanged(visible);
        }
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

    /**
     * @param keyboardViewContainer 包含 KeyboardView 的View, 控制VISIBLE & GONE, 默认keyboardView
     */
    public void setupWithView(View keyboardViewContainer) {
        this.keyboardViewContainer = keyboardViewContainer;
    }

    /**
     * @param keyboardViewDialog 包含 KeyboardView 的Dialog, 控制show() & dismiss()
     */
    public void setupWithDialog(Dialog keyboardViewDialog) {
        this.keyboardViewDialog = keyboardViewDialog;
    }

    /**
     * 按切换键时切换软键盘
     */
    public void switchKeyboard() {
        switchKeyboard(getKeyboard() == secondKeyboard);
    }

    /**
     * 指定切换软键盘
     * @param isFirstKeyboard 是否是第一种键盘
     */
    public void switchKeyboard(boolean isFirstKeyboard) {
        boolean isKeyboard1 = getKeyboard() == firstKeyboard;//当前是否是第1种键盘
        //根据当前键盘判断是否要重设键盘
        isResetKeyboard = isFirstKeyboard != isKeyboard1;
        if (isResetKeyboard) {
            keyboardView.setKeyboard(isFirstKeyboard ? firstKeyboard : secondKeyboard);
        }
    }
    /**
     * FIXME: 2019/12/23 未解决
     * 重新设置键盘后, 如果新键盘keys个数比原键盘keys个数少,
     * 并且目前有键盘key在点击, 而KeyboardView的onTouchEvent没走/完?,
     * 就不会更新KeyboardView中的mRepeatKeyIndex变量(这个变量指按下的按键在keys中的position)
     * 就非常容易出错, 比如:
     * 一直按住删除, 当text长度=1时, 从数字键盘(36个key)切换到省市键盘(39个key), 就会报错:
     * java.lang.ArrayIndexOutOfBoundsException: length=36; index=38
     * @see KeyboardView#repeatKey()
     */
    protected void reSetmRepeatKeyIndex(int index) {
        logError("index = " + index);
        try {
            Field field = keyboardView.getClass().getDeclaredField("mRepeatKeyIndex");
            field.setAccessible(true);
            field.set(keyboardView, index);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * KeyboardView visible改变监听, 如果设置了上方的 setupWithView | setupWithDialog, 不用再设置监听
     * @param listener new KeyboardInputEditText.OnKeyboardViewVisibleChangeListener() {}
     */
    public void setOnKeyboardViewVisibleChangeListener(OnKeyboardViewVisibleChangeListener listener) {
        onKeyboardViewVisibleChangeListener = listener;
    }

    public interface OnKeyboardViewVisibleChangeListener {
        void onKeyboardViewVisibleChanged(boolean visible);
    }
}
