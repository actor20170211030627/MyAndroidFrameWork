package com.actor.myandroidframework.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.MyOkhttpUtils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.ToastUtils;
import com.actor.myandroidframework.widget.LoadingDialog;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Description: Activity基类
 * Copyright  : Copyright (c) 2017
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/5/27 on 12:45.
 * @version 1.0
 */
public class ActorBaseActivity extends AppCompatActivity {

    protected Activity            activity;
    protected Intent              intent;
    protected Map<String, Object> params = new LinkedHashMap<>();
//    protected ACache              aCache = ActorApplication.instance.aCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        logError(getClass().getName());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置屏幕朝向,在setContentView之前
        //设置状态栏默认颜色,如果不要这个颜色,可在自己的onCreate中重写下面一句,自己设颜色
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.color_00CCFF));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
//        View baseView = getLayoutInflater().inflate(R.layout.activity_base, null);//加载基类布局
//        flContent = baseView.findViewById(R.id.fl_content);
//        llLoading = baseView.findViewById(R.id.ll_loading);
//        tvLoading = findViewById(R.id.tv_loading);
//        llEmpty = findViewById(R.id.ll_empty);
//        View childView = getLayoutInflater().inflate(layoutResID, null);//加载子类布局
//        flContent.addView(childView);//将子布局添加到空的帧布局
//        super.setContentView(baseView);
        super.setContentView(layoutResID);
    }

    //是否显示加载中...
//    protected void showLoading(boolean isShow) {
//        llLoading.setVisibility(isShow ? View.VISIBLE : View.GONE);
//    }

    //设置加载中...
//    protected void setLoadingText(CharSequence loading) {
//        tvLoading.setText(loading);
//    }

    //是否显示empty图片
//    protected void showEmpty(boolean isShow) {
//        llEmpty.setVisibility(isShow ? View.VISIBLE : View.GONE);
//    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pre_enter, R.anim.next_exit);
    }


    //返回String区=============================================
    protected String getNoNullString(String s) {
        return s == null? "" : s;
    }

    protected String getNoNullString(String s, String defaultStr) {
        return s == null? defaultStr : s;
    }

    protected String getStringFormat(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    public static String getText(Object obj){
        return TextUtil.getText(obj);
    }


    //判空区=============================================
    protected boolean isEmpty(Object obj) {
        return !isNoEmpty(obj);
    }

    protected boolean isEmpty(Object obj, CharSequence notify) {
        return !isNoEmpty(obj, notify);
    }

    /**
     * @param objs 参数的类型为:
     * <ol>
     *      <li>{@link android.widget.TextView}</li>
     *      <li>{@link android.support.design.widget.TextInputLayout}</li>
     *      <li>{@link TextUtil.GetTextAble}</li>
     *      <li>{@link CharSequence}</li>
     *      <li>{@link java.lang.reflect.Array}</li>
     *      <li>{@link java.util.Collection}</li>
     *      <li>{@link java.util.Map}</li>
     * </ol>
     * @return 都不为空,返回true
     */
    protected boolean isNoEmpty(Object... objs) {
        return TextUtil.isNoEmpty(objs);
    }

    protected boolean isNoEmpty(Object obj, CharSequence notify) {
        return TextUtil.isNoEmpty(obj, notify);
    }


    //打印日志区=============================================
    protected void logError(String msg) {
        LogUtils.Error(msg, false);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }


    //toast区=============================================
    protected void toast(CharSequence notify){
        ToastUtils.show(notify);
    }


    //显示加载Diaong=============================================
    private LoadingDialog loadingDialog;
    protected void showLoadingDialog() {
        getLoadingDialog().show();
    }

    protected LoadingDialog getLoadingDialog() {
        if (loadingDialog == null) loadingDialog = new LoadingDialog(this);
        return loadingDialog;
    }

    //隐藏加载Diaong
    protected void dismissLoadingDialog() {
        if (loadingDialog != null) loadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyOkHttpUtils.cancelTag(this);//取消网络请求
    }
}
