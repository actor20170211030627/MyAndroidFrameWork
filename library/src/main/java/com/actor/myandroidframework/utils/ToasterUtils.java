package com.actor.myandroidframework.utils;

import android.app.Application;
import android.view.Gravity;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.actor.myandroidframework.R;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hjq.toast.ToastParams;
import com.hjq.toast.Toaster;
import com.hjq.toast.style.BlackToastStyle;
import com.hjq.toast.style.CustomToastStyle;
import com.hjq.toast.style.WhiteToastStyle;

/**
 * description: <a href="https://github.com/getActivity/Toaster">轮子哥吐司</a>
 * @author : ldf
 * @update : 2023/8/2 on 16
 */
public class ToasterUtils {

    //x/屏幕h = (2340-2148)/2340  ==>  x = 屏幕h * (2340-2148)/2340  =  屏幕h * 192/2340
    protected static final int Y_OFFSET = (int) (ScreenUtils.getAppScreenHeight() * 0.0820512820512821F);


    public static void init(@NonNull Application application) {
        Toaster.init(application);
        //设置全局样式, 靠底部, 并且y偏移 (全局样式不会影响到局部样式)
        Toaster.setGravity(Gravity.BOTTOM, 0, Y_OFFSET);
    }


    /**
     * 正常show, 默认是黑色样式, 黑色半透明背景. <br />
     * 如果设置全局样式后, 就变成了全局样式.
     * @param id 文字资源id: R.string.xxx
     */
    public static void show(@StringRes int id) {
        Toaster.show(id);
    }
    public static void show(@Nullable Object object) {
        Toaster.show(object);
    }
    public static void show(@NonNull CharSequence text) {
        Toaster.show(text);
    }


    /**
     * 黑色样式, 黑色半透明背景
     * @param id 文字资源id: R.string.xxx
     */
    public static void black(@StringRes int id) {
        black(StringUtils.getString(id));
    }
    public static void black(@Nullable Object object) {
        black(TextUtils2.getNoNullString(object, "null"));
    }
    public static void black(@NonNull CharSequence text) {
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new BlackToastStyle() {
            @Override
            public int getGravity() { return Toaster.getStyle().getGravity(); }
            @Override
            public int getYOffset() { return Toaster.getStyle().getYOffset(); }
        };
        Toaster.show(params);
    }


    /**
     * 白色样式, 白色不透明背景
     * @param id 文字资源id: R.string.xxx
     */
    public static void white(@StringRes int id) {
        white(StringUtils.getString(id));
    }
    public static void white(@Nullable Object object) {
        white(TextUtils2.getNoNullString(object, "null"));
    }
    public static void white(@NonNull CharSequence text) {
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new WhiteToastStyle() {
            @Override
            public int getGravity() { return Toaster.getStyle().getGravity(); }
            @Override
            public int getYOffset() { return Toaster.getStyle().getYOffset(); }
        };
        Toaster.show(params);
    }


    /**
     * 提示样式, 蓝色背景
     */
    public static void info(@StringRes int id) {
        info(StringUtils.getString(id));
    }
    public static void info(@Nullable Object object) {
        info(TextUtils2.getNoNullString(object, "null"));
    }
    public static void info(@NonNull CharSequence text) {
        custom(text, R.layout.toast_info);
    }


    /**
     * 警告样式, 橘色背景
     */
    public static void warning(@StringRes int id) {
        warning(StringUtils.getString(id));
    }
    public static void warning(@Nullable Object object) {
        warning(TextUtils2.getNoNullString(object, "null"));
    }
    public static void warning(@NonNull CharSequence text) {
        custom(text, R.layout.toast_warn);
    }


    /**
     * 成功样式, 绿色背景
     */
    public static void success(@StringRes int id) {
        success(StringUtils.getString(id));
    }
    public static void success(@Nullable Object object) {
        success(TextUtils2.getNoNullString(object, "null"));
    }
    public static void success(@NonNull CharSequence text) {
        custom(text, R.layout.toast_success);
    }


    /**
     * 错误样式, 红色背景
     */
    public static void error(@StringRes int id) {
        error(StringUtils.getString(id));
    }
    public static void error(@Nullable Object object) {
        error(TextUtils2.getNoNullString(object, "null"));
    }
    public static void error(@NonNull CharSequence text) {
        custom(text, R.layout.toast_error);
    }

    /**
     * 自定义样式
     */
    public static void custom(@StringRes int id, @LayoutRes int layoutResId) {
        custom(StringUtils.getString(id), layoutResId);
    }
    public static void custom(@Nullable Object object, @LayoutRes int layoutResId) {
        custom(TextUtils2.getNoNullString(object, "null"), layoutResId);
    }
    public static void custom(@NonNull CharSequence text, @LayoutRes int layoutResId) {
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new CustomToastStyle(layoutResId, Toaster.getStyle().getGravity(), 0, Toaster.getStyle().getYOffset());
        Toaster.show(params);
    }


    /**
     * 设置全局样式示例
     */
    protected static void setGlobalStyleExam() {
//        Toaster.setView(R.layout.xxx); //默认是黑色背景样式的View
//        Toaster.setGravity(gravity, 0, Y_OFFSET, ...);
//        Toaster.setStyle(...);
//        Toaster.setInterceptor(...);
//        Toaster.setStrategy(...);
    }
}
