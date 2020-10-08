package com.actor.myandroidframework.widget.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * Description: 类的描述
 * Author     : 李大发
 * Date       : 2019/3/11 on 11:24
 * @version 1.0 修改{@link #shouldOverrideUrlLoading(WebView, String)}方法, 判断http/s
 */
public class BaseWebViewClient extends WebViewClient {

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
        super.onPageStarted(view, url, favicon);
        LogUtils.formatError("开始加载网页: url = %s", false, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {//页面加载完成
        super.onPageFinished(view, url);
        LogUtils.formatError("网页加载完成: url = %s", false, url);
    }

    /**
     * 过时方法
     * @param url 我们需要跳转的网址
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.formatError("跳转网页: url=%s", false, url);
        //百家号url: https://baijiahao.baidu.com/s?id=1642175663200722386&wfr=spider&for=pc
        //百家号加载过程中的url: baiduboxapp://utils?action=sendIntent&minver=7.4&params=%7B%22intent%22%3A%22intent%3A%23Intent%3Baction%3Dcom.baidu.searchbox.action.HOME%3Bpackage%3Dcom.baidu.searchbox%3BS.targetCommand%3D%257B%2522mode%2522%253A%25220%2522%252C%2522intent%2522%253A%2522intent%253A%2523Intent%253BS.toolbaricons%253D%25257B%252522toolids%252522%25253A%25255B%2525224%252522%25252C%2525221%252522%25252C%2525222%252522%25252C%2525223%252522%25255D%25257D%253BS.actionBarConfig%253D%25257B%252522extCase%252522%25253A%2525220%252522%25257D%253Bcomponent%253Dcom.baidu.searchbox%252F.home.feed.FeedDetailActivity%253BS.menumode%253D2%253BS.sfrom%253Dfeed%253BS.context%253D%25257B%252522nid%252522%25253A%252522news_9138480105384862915%252522%25257D%253BS.tpl_id%253Dlanding_app.html%253BS.ch_url%253Dhttps%25253A%25252F%25252Fmbd.baidu.com%25252Fnewspage%25252Fdata%25252Flandingreact%25253FpageType%25253D2%252526sourceFrom%25253DlandingShare%252526nid%25253Dnews_9138480105384862915%253BS.commentInfo%253D%25257B%252522topic_id%252522%25253A%2525221052000023481251%252522%25252C%252522opentype%252522%25253A%2525222%252522%25257D%253Bend%2522%252C%2522min_v%2522%253A%252216787968%2522%257D%3Bend%22%7D&needlog=1&logargs=%7B%22source%22%3A%221020418p%22%2C%22from%22%3A%22openbox%22%2C%22page%22%3A%22chrome%22%2C%22type%22%3A%22%22%2C%22value%22%3A%22%22%2C%22channel%22%3A%221020418o%22%2C%22extlog%22%3A%22%22%2C%22app_now%22%3A%22chrome_1566267888375_6692356467%22%2C%22yyb_pkg%22%3A%22com.baidu.searchbox%22%2C%22idmData%22%3A%7B%7D%2C%22matrix%22%3A%22main%22%7D
        if (url != null) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url);
                return true;//确保是使用自己的webView来进行网址的跳转
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    //进行网址跳转的时候，会进行的回调,默认会跳转浏览器
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        LogUtils.formatError("跳转网页: request=%s", false, request);
        view.loadUrl(request.getUrl().toString());
        return super.shouldOverrideUrlLoading(view, request);
    }

    /**
     * 加载网页发生证书认证错误,不显示图片,还有正确解决方法:https://www.jianshu.com/p/56e2b0bf9ab2
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        LogUtils.formatError("加载网页发生证书认证错误: handler=%s, error=%s", false, handler, error);
        super.onReceivedSslError(view, handler, error);//取消
//        handler.cancel(); //默认的处理方式，WebView变成空白页
//        handler.proceed();//忽略错误
    }

    /**
     * 当我们使用WebView加载一个html页面时，通常会在WebViewClient 的onReceivedHttpError()与onReceivedError()
     * 去做一些错误响应的处理，但是有时候虽然页面加载成功，onReceivedHttpError()
     * 这个方法却会返回404，为什么会返回404呢？WevView是Android系统内置的一个浏览器，同别的浏览器一样，WebView
     * 在请求加载一个页面的同时，还会发送一个请求图标文件的请求。
     * 比如我们采用WebView去加载一个页面：
     * webView.loadUrl("http://192.168.5.40:9006/sso_web/html/H5/doctor/aboutUs.html");
     * 同时还会发送一个请求图标文件的请求
     * http://192.168.5.40:9006/favicon.ico
     * onReceivedHttpError这个方法主要用于响应服务器返回的Http错误(状态码大于等于400)
     * ，这个回调将被调用任何资源（IFRAME，图像等），而不仅仅是主页面。所以就会出现主页面虽然加载成功，但由于网站没有favicon.ico文件导致返回404错误。
     */
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    /**
     * 向主机应用程序报告web资源加载错误。这些错误通常表明无法连接到服务器
     */
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
//        tvError.setVisibility(View.VISIBLE);//正在维护中...
//        view.setVisibility(View.GONE);
    }
}
