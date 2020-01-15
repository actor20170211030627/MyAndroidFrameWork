package com.actor.myandroidframework.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.SparseArrayCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Description: getText(),判空,显示/隐藏键盘
 * Date       : 2018/4/20 on 11:14
 * @version 1.0
 */
public class TextUtil {

    /**
     * 获取text
     * @param obj
     * @return
     */
    public static String getText(Object obj) {
        if (obj == null) return null;
        if (obj instanceof TextView) {
            return getText((TextView) obj);
        } else if (obj instanceof TextInputLayout) {
            return getText((TextInputLayout) obj);
        } else if (obj instanceof GetTextAble) {//实现本类的接口
            return getText((GetTextAble) obj);
        } else return obj.toString();
    }

    private static String getText(TextView textView){
        return textView.getText().toString().trim();
    }

    private static String getText(@NonNull TextInputLayout textInputLayout) {
        return getText(textInputLayout.getEditText());
    }

    private static String getText(GetTextAble getTextAble){
        return getTextAble.getText().toString().trim();
    }

    /**
     * @return 都不为空, 返回true
     */
    public static boolean isNoEmpty(Object... objs) {
        if (objs == null) return false;
        boolean isNoEmpty;
        for (Object obj : objs) {
            if (obj instanceof TextView) {
                isNoEmpty = isNoEmpty((TextView) obj, ((TextView) obj).getHint());
            } else if (obj instanceof GetTextAble) {
                isNoEmpty = isNoEmpty((GetTextAble) obj, ((GetTextAble) obj).getHint());
            } else if (obj instanceof TextInputLayout) {
                isNoEmpty = isNoEmpty((TextInputLayout) obj, ((TextInputLayout) obj).getHint());
            } else isNoEmpty = isNoEmpty(obj, null);
            if (!isNoEmpty) return false;
        }
        return true;
    }

    /**
     * @param obj 判断对象是否不为空, 如果是 EditText/TextInputLayout/GetTextAble,
     *            且为空, 就跳到相应的EditText, 包括如下类型:
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
     *      <li>{@link android.support.v4.util.SparseArrayCompat}</li>
     * </ol>
     * @param notify 如果为空 & notify != null, toast(notify)
     * @return 是否不为空
     */
    public static boolean isNoEmpty(Object obj, CharSequence notify) {
        if (obj == null) {
            if (notify != null) ToastUtils.show(notify);
            return false;
        }else if (obj instanceof CharSequence) {//字符序列
            boolean isEmpty = ((CharSequence) obj).length() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        } else if (obj.getClass().isArray()) {//数组
            boolean isEmpty = Array.getLength(obj) == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        } else if (obj instanceof Collection) {//List, Set, Queue
            boolean isEmpty = ((Collection) obj).isEmpty();
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        } else if (obj instanceof Map) {//Map
            boolean isEmpty = ((Map) obj).isEmpty();
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        } else if (obj instanceof TextView) {
            return isNoEmpty((TextView) obj, notify);
        } else if (obj instanceof GetTextAble) {
            return isNoEmpty((GetTextAble) obj, notify);
        } else if (obj instanceof TextInputLayout) {
            return isNoEmpty((TextInputLayout) obj, notify);
        } else if (obj instanceof SparseArray) {//稀疏数组<int, Object>, android特有, 主要是替换Map
            boolean isEmpty = ((SparseArray) obj).size() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        } else if (obj instanceof SparseBooleanArray) {//<int, boolean>
            boolean isEmpty = ((SparseBooleanArray) obj).size() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        } else if (obj instanceof SparseIntArray) {//<int, int>
            boolean isEmpty = ((SparseIntArray) obj).size() == 0;
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//18, android4.3
            if (obj instanceof SparseLongArray) {//<int, long>
                boolean isEmpty = ((SparseLongArray) obj).size() == 0;
                if (isEmpty && notify != null) ToastUtils.show(notify);
                return !isEmpty;
            }
        } else if (obj instanceof SparseArrayCompat) {//v4包, <int, Object>
            boolean isEmpty = ((SparseArrayCompat) obj).isEmpty();
            if (isEmpty && notify != null) ToastUtils.show(notify);
            return !isEmpty;
        }
        return true;
    }

    private static boolean isNoEmpty(TextView textView, CharSequence notify) {
        if (TextUtils.isEmpty(getText(textView))) {
            if (!TextUtils.isEmpty(notify)) ToastUtils.show(notify);
            if (textView instanceof EditText) KeyBoardUtils.showOrHideSoftInput((EditText) textView,true);
            return false;
        }
        return true;
    }

    /**
     * 判断TextInputLayout是否为空,并在下方提示
     * @param textInputLayout
     * @param notify 如果为空,下方提示的信息,如果不提示,传null.
     * @return
     */
    private static boolean isNoEmpty(final TextInputLayout textInputLayout, final CharSequence notify) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(getText(textInputLayout.getEditText()))) {
                    textInputLayout.setErrorEnabled(true);
                    if (!TextUtils.isEmpty(notify)) {
                        textInputLayout.setError(notify);
                        ToastUtils.show(notify);
                    }
                } else {
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (TextUtils.isEmpty(getText(textInputLayout.getEditText()))) {
            if (!TextUtils.isEmpty(notify)) {
                textInputLayout.setError(notify);
                ToastUtils.show(notify);
            }
            KeyBoardUtils.showOrHideSoftInput(textInputLayout.getEditText(),true);
            return false;
        }
        return true;
    }

    private static boolean isNoEmpty(GetTextAble getTextAble, CharSequence nofify) {
        return isNoEmpty(getTextAble.getEditText(), nofify);
    }

    /**
     * 判断是否equals
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(@Nullable String a, @Nullable String b) {
        return TextUtils.equals(a, b);
    }

    public interface GetTextAble {
        CharSequence getText();
        CharSequence getHint();
        EditText getEditText();
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
     * @param str
     * @return
     */
    public static boolean isDigitsOnly(CharSequence str) {
        return TextUtils.isDigitsOnly(str);
    }

    /**
     * 获取反转字符串
     * @param source
     * @return
     */
    public static CharSequence getReverse(CharSequence source) {
        if (source == null) return null;
        return TextUtils.getReverse(source, 0, source.length());
    }

    /**
     * 获取trim后字符长度
     * @param s
     * @return
     */
    public static int getTrimmedLength(CharSequence s) {
        if (s == null) return 0;
        return TextUtils.getTrimmedLength(s);
    }

    /**
     * 获取html格式的字符串,https://blog.csdn.net/yang28242687/article/details/64967167
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
