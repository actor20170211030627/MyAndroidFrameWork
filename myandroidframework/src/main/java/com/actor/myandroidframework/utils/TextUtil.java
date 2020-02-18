package com.actor.myandroidframework.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.SparseArrayCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.widget.EditText;
import android.widget.TextView;

import com.actor.myandroidframework.R;
import com.blankj.utilcode.util.KeyboardUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Description: 如下功能:
 * <ol>
 *     <li>{@link #getText(Object)} 获取Text</li>
 *     <li>{@link #isNoEmpty(Object...)} 判断Objects是否为空</li>
 *     <li>{@link #isNoEmpty(Object, CharSequence)} 判断Object是否为空, 如果为空: toast(charsequence);</li>
 *     <li>{@link #equals(CharSequence, CharSequence)} 判断2个字符序列equals</li>
 *     <li>{@link #getStringFormat(String, Object...)} 获取格式化后的String</li>
 *     <li>{@link #concat(CharSequence...)} 连接多个字符序列</li>
 *     <li>{@link #isDigitsOnly(CharSequence)} 是否只包含数字</li>
 *     <li>{@link #getReverse(CharSequence)} 获取反转字符串</li>
 *     <li>{@link #getTrimmedLength(CharSequence)} 获取trim后字符长度</li>
 *     <li>{@link #htmlEncode(String)} 获取html格式的字符串</li>
 * </ol>
 * Date       : 2018/4/20 on 11:14
 * @version 1.0
 */
public class TextUtil {

    /**
     * 获取text
     * @param obj 参数类型, 包括:
     *            <ol>
     *                <li>{@link GetTextAble}</li>
     *                <li>{@link TextView}</li>
     *                <li>{@link TextInputLayout}</li>
     *                <li>{@link Object#toString()}</li>
     *            </ol>
     * @return 返回获取的text
     */
    public static String getText(@NonNull Object obj) {
        if (obj instanceof GetTextAble)     return getText((GetTextAble) obj);//实现本类的接口
        if (obj instanceof TextView)        return getText((TextView) obj);
        if (obj instanceof TextInputLayout) return getText((TextInputLayout) obj);
        return obj.toString();
    }

    protected static String getText(GetTextAble getTextAble){
        CharSequence text = getTextAble.getText();
        if (text != null) return text.toString().trim();
        return null;
    }

    protected static String getText(TextView textView){
        return textView.getText().toString().trim();
    }

    protected static String getText(TextInputLayout textInputLayout) {
        EditText editText = textInputLayout.getEditText();
        if (editText != null) return getText(editText);
        return null;
    }

    /**
     * 判断obj是否为空
     * @param obj 参数类型, 包括:
     *            <ol>
     *                <li>{@link GetTextAble}</li>
     *                <li>{@link TextView}</li>
     *                <li>{@link TextInputLayout}</li>
     *                <li>others...</li>
     *            </ol>
     * @return obj是否不为空
     */
    protected static boolean isNoEmpty(@NonNull Object obj) {
        //实现本类的接口
        if (obj instanceof GetTextAble)     return isNoEmpty((GetTextAble) obj, ((GetTextAble) obj).getHint());
        if (obj instanceof TextView)        return isNoEmpty((TextView) obj, ((TextView) obj).getHint());
        if (obj instanceof TextInputLayout) return isNoEmpty((TextInputLayout) obj, ((TextInputLayout) obj).getHint());
        return isNoEmpty(obj, (CharSequence) null);
    }

    /**
     * @return 都不为空, 返回true
     */
    public static boolean isNoEmpty(@NonNull Object... objs) {
        for (Object obj : objs) {
            if (!isNoEmpty(obj)) return false;
        }
        return true;
    }

    /**
     * @param obj 判断对象是否不为空
     *            1.如果是 EditText/TextInputLayout, 且输入为空, 就将光标定位到相应的EditText且弹出系统键盘.
     *            2.如果是 {@link GetTextAble}
     *              且 {@link GetTextAble#getEditText()}!=null
     *              且 {@link GetTextAble#keyboardShowAbleIfEditText()},
     *              且 输入为空, 就将光标定位到相应的EditText且弹出系统键盘
     *            obj 包括如下类型:
     * <ol>
     *      <li>{@link CharSequence}</li>
     *      <li>{@link java.lang.reflect.Array}</li>
     *      <li>{@link java.util.Collection Collection(包括: List, Set, Queue)}</li>
     *      <li>{@link java.util.Map}</li>
     *      <li>{@link android.widget.TextView}</li>
     *      <li>{@link com.actor.myandroidframework.utils.TextUtil.GetTextAble}</li>
     *      <li>{@link android.support.design.widget.TextInputLayout}</li>
     *      <li>{@link android.util.SparseArray}</li>
     *      <li>{@link android.util.SparseBooleanArray}</li>
     *      <li>{@link android.util.SparseIntArray}</li>
     *      <li>{@link android.util.SparseLongArray}</li>
     *      <li>{@link LongSparseArray}</li>
     *      <li>{@link android.support.v4.util.SparseArrayCompat}</li>
     *      <li>{@link android.support.v4.util.LongSparseArray}</li>
     *      <li>{@link Object#toString()}</li>
     * </ol>
     * @param notify 如果为空 & notify != null, toast(notify);
     * @return 是否不为空
     */
    public static boolean isNoEmpty(Object obj, CharSequence notify) {
        if (obj == null) {
            if (notify != null) ToastUtils.show(notify);
            return false;
        }
        if (obj instanceof CharSequence) {//字符序列
            boolean isEmpty = ((CharSequence) obj).length() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj.getClass().isArray()) {//数组
            boolean isEmpty = Array.getLength(obj) == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj instanceof Collection) {//List, Set, Queue
            boolean isEmpty = ((Collection) obj).isEmpty();
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj instanceof Map) {//Map
            boolean isEmpty = ((Map) obj).isEmpty();
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj instanceof GetTextAble)     return isNoEmpty((GetTextAble) obj, notify);
        if (obj instanceof TextView)        return isNoEmpty((TextView) obj, notify);
        if (obj instanceof TextInputLayout) return isNoEmpty((TextInputLayout) obj, notify);

        if (obj instanceof SparseArray) {//稀疏数组<int, Object>, android特有, 主要是替换Map
            boolean isEmpty = ((SparseArray) obj).size() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj instanceof SparseBooleanArray) {//<int, boolean>
            boolean isEmpty = ((SparseBooleanArray) obj).size() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj instanceof SparseIntArray) {//<int, int>
            boolean isEmpty = ((SparseIntArray) obj).size() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//18, android4.3
            if (obj instanceof SparseLongArray) {//<int, long>
                boolean isEmpty = ((SparseLongArray) obj).size() == 0;
                if (isEmpty && notify != null) ToastUtils.show(notify);
                return !isEmpty;
            }
        }
        if (obj instanceof LongSparseArray) {//<long, Object>
            boolean isEmpty = ((LongSparseArray) obj).size() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj instanceof SparseArrayCompat) {//v4包, <int, Object>
            boolean isEmpty = ((SparseArrayCompat) obj).isEmpty();
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        if (obj instanceof android.support.v4.util.LongSparseArray) {//v4包, <long, Object>
            boolean isEmpty = ((android.support.v4.util.LongSparseArray) obj).isEmpty();
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        return !TextUtils.isEmpty(obj.toString());
    }

    protected static boolean isNoEmpty(GetTextAble getTextAble, CharSequence nofify) {
        boolean noEmpty = isNoEmpty(getTextAble.getText(), nofify);
        //输入内容为空
        if (!noEmpty) {
            EditText editText = getTextAble.getEditText();
            //EditText!=null & 键盘能弹出
            if (editText != null && getTextAble.keyboardShowAbleIfEditText()) {
                editText.requestFocus();//先获取焦点
                KeyboardUtils.showSoftInput(editText);
            }
        }
        return noEmpty;
    }

    protected static boolean isNoEmpty(TextView textView, CharSequence notify) {
        if (TextUtils.isEmpty(getText(textView))) {
            if (notify != null) ToastUtils.show(notify);
            if (textView instanceof EditText) {
                textView.requestFocus();//先获取焦点
                KeyboardUtils.showSoftInput(textView);
            }
            return false;
        }
        return true;
    }

    /**
     * 判断TextInputLayout是否为空, 并在下方提示
     * @param textInputLayout Android 5.0出现的一个MD风格控件
     * @param notify 如果为空, 下方提示的信息, 如果不提示, 传null.
     */
    protected static boolean isNoEmpty(TextInputLayout textInputLayout, CharSequence notify) {
        EditText editText = textInputLayout.getEditText();
        if (editText != null) {
            TextWatcher textWatcher = (TextWatcher) textInputLayout.getTag(R.id.tag_to_get_textwatcher);
            if (textWatcher == null) {
                textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (TextUtils.isEmpty(getText(textInputLayout))) {
                            textInputLayout.setErrorEnabled(true);
                            if (notify != null) {
                                textInputLayout.setError(notify);
                                ToastUtils.show(notify);
                            }
                        } else textInputLayout.setErrorEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };
                textInputLayout.setTag(R.id.tag_to_get_textwatcher, textWatcher);
                editText.addTextChangedListener(textWatcher);
            }
        }
        if (TextUtils.isEmpty(getText(textInputLayout))) {
            if (notify != null) {
                textInputLayout.setError(notify);
                ToastUtils.show(notify);
            }
            if (editText != null) {
                editText.requestFocus();//先获取焦点
                KeyboardUtils.showSoftInput(editText);
            }
            return false;
        }
        return true;
    }

    /**
     * @return 返回是否equals
     */
    public static boolean equals(@Nullable CharSequence a, @Nullable CharSequence b) {
        return TextUtils.equals(a, b);
    }

    /**
     * 返回格式化后的String, 示例: ("name = %s, age = %d", "张三", 23) => (name = 张三, age = 23)
     * 具体格式化方式可参考:
     * GitHub:
     * https://github.com/actor20170211030627/TestApplication/blob/master/app/src/test/java/com/actor/testapplication/StringFormatTest.java
     * 码云:
     * https://gitee.com/actor2017/TestApplication/blob/master/app/src/test/java/com/actor/testapplication/StringFormatTest.java
     */
    public static String getStringFormat(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    public interface GetTextAble {

        /**
         * @return 返回输入的内容, 用于判断是否已经有输入的内容
         */
        @Nullable CharSequence getText();

        /**
         * @return 返回提示信息, 用于没有输入内容时, toast的提示内容
         */
        @Nullable CharSequence getHint();

        /**
         * @return 如果有EditText就返回EditText(判断输入内容为空时, 光标定位到这个EditText & 默认弹出系统键盘),
         *         否则返回 null
         */
        @Nullable EditText getEditText();

        /**
         * 如果{@link #getEditText()} 返回的EditText!=null, 并且EditText输入内容为空,
         * toast(getHint()) 后,  系统键盘是否自动弹出
         * @return 默认true 可以弹出, 可重写此方法
         */
        default boolean keyboardShowAbleIfEditText() {
            return true;
        }
    }

    /**==============================其它方法===================================*/
    /**
     * 连接字符串
     * @param text
     * @return
     */
    public static CharSequence concat(CharSequence... text) {
        return TextUtils.concat(text);
    }

    /**
     * 返回是否只包含数字
     */
    public static boolean isDigitsOnly(CharSequence str) {
        return TextUtils.isDigitsOnly(str);
    }

    /**
     * @return 获取反转字符串
     */
    public static CharSequence getReverse(CharSequence source) {
        if (source != null) return TextUtils.getReverse(source, 0, source.length());
        return null;
    }

    /**
     * 获取trim后字符长度
     */
    public static int getTrimmedLength(CharSequence s) {
        if (s == null) return 0;
        return TextUtils.getTrimmedLength(s);
    }

    /**
     * 获取html格式的字符串, https://blog.csdn.net/yang28242687/article/details/64967167
     * <ol>
     *     <li>< 转换为 &#038;lt;</li>
     *     <li>> 转换为 &#038;gt;</li>
     *     <li>& 转换为 &#038;amp;</li>
     *     <li>' 转换为 &#038;#39;</li>
     *     <li>" 转换为 &#038;quot;</li>
     * </ol>
     * @param s
     * @return
     */
    public static String htmlEncode(String s) {
        if (s == null) return null;
        return TextUtils.htmlEncode(s);
    }
}
