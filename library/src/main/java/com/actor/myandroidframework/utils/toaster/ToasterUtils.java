package com.actor.myandroidframework.utils.toaster;

import android.app.Application;
import android.view.Gravity;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.TextUtils2;
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
    //打印拦截
    protected static MyToastLogInterceptor myToastLogInterceptor = new MyToastLogInterceptor();


    public static void init(@NonNull Application application) {
        Toaster.init(application);
        //设置全局样式, 靠底部, 并且y偏移 (全局样式不会影响到局部样式)
        Toaster.setGravity(Gravity.BOTTOM, 0, Y_OFFSET);
        //修复本工具类打印位置偏差问题
        Toaster.setInterceptor(myToastLogInterceptor);
    }


    /**
     * 正常show, 默认是黑色样式, 黑色半透明背景. <br />
     * 如果设置全局样式后, 就变成了全局样式.
     * @param id 文字资源id: R.string.xxx
     */
    public static void show(@StringRes int id) {
        myToastLogInterceptor.setStackPosition(10);
        Toaster.show(id);
    }
    public static void show(@Nullable Object object) {
        myToastLogInterceptor.setStackPosition(10);
        Toaster.show(object);
    }
    public static void show(@Nullable CharSequence text) {
        myToastLogInterceptor.setStackPosition(9);
        Toaster.show(text);
    }
    public static void showFormat(@Nullable CharSequence text, Object... args) {
        myToastLogInterceptor.setStackPosition(9);
        Toaster.show(TextUtils2.getStringFormat(String.valueOf(text), args));
    }


    /**
     * 黑色样式, 黑色半透明背景
     * @param id 文字资源id: R.string.xxx
     */
    public static void black(@StringRes int id) {
        myToastLogInterceptor.setStackPosition(9);
        black(StringUtils.getString(id));
    }
    public static void black(@Nullable Object object) {
        myToastLogInterceptor.setStackPosition(9);
        black(String.valueOf(object));
    }
    public static void black(@Nullable CharSequence text) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(8);
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
    public static void blackFormat(@Nullable CharSequence text, Object... args) {
        myToastLogInterceptor.setStackPosition(9);
        black(TextUtils2.getStringFormat(String.valueOf(text), args));
    }


    /**
     * 白色样式, 白色不透明背景
     * @param id 文字资源id: R.string.xxx
     */
    public static void white(@StringRes int id) {
        myToastLogInterceptor.setStackPosition(9);
        white(StringUtils.getString(id));
    }
    public static void white(@Nullable Object object) {
        myToastLogInterceptor.setStackPosition(9);
        white(String.valueOf(object));
    }
    public static void white(@Nullable CharSequence text) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(8);
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
    public static void whiteFormat(@Nullable CharSequence text, Object... args) {
        myToastLogInterceptor.setStackPosition(9);
        white(TextUtils2.getStringFormat(String.valueOf(text), args));
    }


    /**
     * 提示样式, 蓝色背景
     */
    public static void info(@StringRes int id) {
        myToastLogInterceptor.setStackPosition(10);
        info(StringUtils.getString(id));
    }
    public static void info(@Nullable Object object) {
        myToastLogInterceptor.setStackPosition(10);
        info(String.valueOf(object));
    }
    public static void info(@Nullable CharSequence text) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(9);
        custom(text, R.layout.toast_info);
    }
    public static void infoFormat(@Nullable CharSequence text, @Nullable Object... args) {
        myToastLogInterceptor.setStackPosition(10);
        info(TextUtils2.getStringFormat(String.valueOf(text), args));
    }


    /**
     * 警告样式, 橘色背景
     */
    public static void warning(@StringRes int id) {
        myToastLogInterceptor.setStackPosition(10);
        warning(StringUtils.getString(id));
    }
    public static void warning(@Nullable Object object) {
        myToastLogInterceptor.setStackPosition(10);
        warning(String.valueOf(object));
    }
    public static void warning(@Nullable CharSequence text) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(9);
        custom(text, R.layout.toast_warn);
    }
    public static void warningFormat(@Nullable CharSequence text, @Nullable Object... args) {
        myToastLogInterceptor.setStackPosition(10);
        warning(TextUtils2.getStringFormat(String.valueOf(text), args));
    }


    /**
     * 成功样式, 绿色背景
     */
    public static void success(@StringRes int id) {
        myToastLogInterceptor.setStackPosition(10);
        success(StringUtils.getString(id));
    }
    public static void success(@Nullable Object object) {
        myToastLogInterceptor.setStackPosition(10);
        success(String.valueOf(object));
    }
    public static void success(@Nullable CharSequence text) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(9);
        custom(text, R.layout.toast_success);
    }
    public static void successFormat(@Nullable CharSequence text, @Nullable Object... args) {
        myToastLogInterceptor.setStackPosition(10);
        success(TextUtils2.getStringFormat(String.valueOf(text), args));
    }


    /**
     * 错误样式, 红色背景
     */
    public static void error(@StringRes int id) {
        myToastLogInterceptor.setStackPosition(10);
        error(StringUtils.getString(id));
    }
    public static void error(@Nullable Object object) {
        myToastLogInterceptor.setStackPosition(10);
        error(String.valueOf(object));
    }
    public static void error(@Nullable CharSequence text) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(9);
        custom(text, R.layout.toast_error);
    }
    public static void errorFormat(@Nullable CharSequence text, @Nullable Object... args) {
        myToastLogInterceptor.setStackPosition(10);
        error(TextUtils2.getStringFormat(String.valueOf(text), args));
    }

    /**
     * 自定义样式
     */
    public static void custom(@StringRes int id, @LayoutRes int layoutResId) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(9);
        custom(StringUtils.getString(id), layoutResId);
    }
    public static void custom(@Nullable Object object, @LayoutRes int layoutResId) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(9);
        custom(String.valueOf(object), layoutResId);
    }
    public static void custom(@Nullable CharSequence text, @LayoutRes int layoutResId) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(8);
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new CustomToastStyle(layoutResId, Toaster.getStyle().getGravity(), 0, Toaster.getStyle().getYOffset());
        Toaster.show(params);
    }
    public static void customFormat(@Nullable CharSequence text, @LayoutRes int layoutResId, @Nullable Object... args) {
        if (myToastLogInterceptor.getStackPosition() == 0) myToastLogInterceptor.setStackPosition(9);
        custom(TextUtils2.getStringFormat(String.valueOf(text), args), layoutResId);
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
