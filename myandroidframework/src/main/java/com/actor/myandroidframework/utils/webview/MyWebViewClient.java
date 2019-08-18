package com.actor.myandroidframework.utils.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * Description: 类的描述
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/11 on 11:24
 */
public class MyWebViewClient extends WebViewClient {

    /**
     * 加载网页发生证书认证错误,不显示图片,还有正确解决方法:https://www.jianshu.com/p/56e2b0bf9ab2
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//        super.onReceivedSslError(view, handler, error);//取消
        LogUtils.Error("加载网页发生证书认证错误", false);
        handler.proceed();//忽略错误
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {//页面加载完成
        super.onPageFinished(view, url);
    }

    /**
     * 过时方法
     * @param view
     * @param url 我们需要跳转的网址
     * @return
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        return super.shouldOverrideUrlLoading(view, url);
        view.loadUrl(url);
        return true;//确保是使用自己的webView来进行网址的跳转
    }

    //进行网址跳转的时候，会进行的回调,默认会跳转浏览器
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        //使用当前的WEbView来跳转新的网址,api>=21
        view.loadUrl(request.getUrl().toString());
        return super.shouldOverrideUrlLoading(view, request);
    }
}
