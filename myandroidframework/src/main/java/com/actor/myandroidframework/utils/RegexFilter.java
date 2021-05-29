package com.actor.myandroidframework.utils;

import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: TextView/EditText 正则输入过滤器
 * 资料来自: https://blog.csdn.net/huanghunhou705/article/details/84233432
 *
 * @author : ldf
 * date       : 2021/4/21 on 13
 * @version 1.0
 */
public class RegexFilter implements InputFilter {

    protected String  regex;//能显示内容的匹配正则
    protected Pattern pattern;

    /**
     * @param regex 能显示内容的匹配正则
     */
    public RegexFilter(String regex) {
        setRegex(regex);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        pattern = Pattern.compile(regex);
    }

    /**
     * 当缓冲区要使用source的[start - end)范围内的内容替换dest的[dstart - dend)范围内的内容时调用该方法。
     * 返回值是你最终想要添加的文本。如果返回空字符""，则不添加新内容。
     *  如果返回空(null)，则添加本次输入的全部内容(即source)(不建议使用return source)
     *  返回指定有内容的字符串：你希望的输入（如"12345"）。   次将指定的字符串添加到输入框中（如"12345"）
     *
     * 当你在删除已有文本时，source的长度为0。不要以为是错误而过滤这种清空。
     * 不要直接修改dest的内容，它的内容只是用来查看的。
     * Note:如果source是{@link Spanned}或的实例{@link Spannable}，source中的span对象应该
     * 复制到过滤后的结果中(即非空返回值)。
     * {@link TextUtils#copySpansFrom(Spanned, int, int, Class, Spannable, int)}可以使用方便，如果
     * 跨度边界指数相对于source将保持相同。
     *
     * @param source 本次输入内容. 当你在删除已有文本时，source.length()=0。不要以为是错误而过滤这种清空。
     * @param start  本次输入被选择的起始位置
     * @param end    本次输入被选择的结束位置(不包含)
     * @param dest   当前输入框中的内容. 不要直接修改dest的内容，它的内容只是用来查看的。
     * @param dstart 被替换内容在输入框中的起始位置
     * @param dend   被替换内容在输入框中的结束位置(不包含)
     * @return       你最终想要添加的文本。如果返回空字符""，则不添加新内容。
     *               如果返回空(null)，则添加本次输入的全部内容(即source)(不建议使用return source)
     *               返回指定有内容的字符串：你希望的输入（如"12345"）。   次将指定的字符串添加到输入框中（如"12345"）
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//        return source.toString().replaceAll(regex, "");//只对输入的内容判断, 不对!

//        LogUtils.formatError("source=%s, start=%d, end=%d, dest=%s, dstart=%d, dend=%d", true, source, start, end, dest, dstart, dend);
        //如果是删除
        if (TextUtils.isEmpty(source)) {
            return null;
        }
        //如果是富文本
        if (source instanceof SpannableStringBuilder) {
//            SpannableStringBuilder stringBuilder = (SpannableStringBuilder)source;
            return null;
        } else {
            String dprefix = dest.toString().substring(0, dstart);//被替换部分的前面部分
            String dsuffix = dest.toString().substring(dend, dest.length());
            String allInput = dprefix.concat(source.toString()).concat(dsuffix);//所有输入内容
//            LogUtils.formatError("dprefix=%s, dsuffix=%s, all=%s", true, dprefix, dsuffix, allInput);
            //如果匹配
            if (pattern.matcher(allInput).matches()) {
                return null;
            }
            //不匹配
            return "";
        }
    }

    /**
     * 将指定内容中不匹配的过滤掉
     * @param text 指定内容
     * @return 过滤掉不匹配内容后的值
     */
    public CharSequence filterCharSequence(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb;
    }
}
