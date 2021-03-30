package com.actor.myandroidframework.widget.webview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.actor.myandroidframework.utils.ConfigUtils;

/**
 * Description: WebView常用初始化
 *
 * android:scrollbars="none"    不显示滚动条
 *
 * Author     : 李大发
 * Date       : 2019-8-20 on 10:34
 *
 * @version 1.0
 */
public class BaseWebView extends WebView {

    public BaseWebView(Context context) {
        super(context);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    /**
     * 初始化
     * @param webViewClient 可传入new BaseWebViewClient(), 或者写个类extends MyWebViewClient然后自定义一些自己的方法
     * @param webChromeClient 可传入new BaseWebChromeClient(), 或者写个类extends MyWebChromeClient然后自定义一些自己的方法
     */
    public void init(BaseWebViewClient webViewClient, BaseWebChromeClient webChromeClient) {
        //初始化WebSettings
        BaseWebSettings.defaultInit(getSettings());

        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);

        //设置debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(ConfigUtils.IS_APP_DEBUG);
        }

        //添加js交互
//        addJavascriptInterface(new Object(), "android");

        //滚动条
//        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//View方法
        //水平滚动条
        setHorizontalScrollBarEnabled(false);
        //垂直滚动条
        setVerticalScrollBarEnabled(false);

        //获取当前URL
//        getUrl();

        //获取原始URL
//        getOriginalUrl();

        //添加下载监听
//        setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });
    }

    /**
     *
     * @param url "网页url" or "本地H5" or "javascript方法", 例:
     *            url:            "https://www.baidu.com/"
     *            本地h5:         "file:///android_asset/support.html" (本地路径: src/main/assets/support.html)
     *            javascript方法: "javascript:jsMethodName(params...)"
     */
    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    /**
     * 重新加载
     */
    @Override
    public void reload() {
        super.reload();
    }

    /**
     * 网页加载h5文档
     * @param data html文档 字符串
     */
    public void loadData(String data) {
        loadData(data, "text/html; charset=UTF-8", "UTF-8");
    }

    /**
     * 注意:如果图片老是显示不出来, 有可能是手机的问题...?
     * @param baseUrl
     * @param data 网页内容
     * @param mimeType text/html
     * @param encoding utf-8
     * @param historyUrl
     */
    @Override
    public void loadDataWithBaseURL(@Nullable String baseUrl, String data,
                                    @Nullable String mimeType, @Nullable String encoding,
                                    @Nullable String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    /**
     * 清除表单数据
     */
    @Override
    public void clearFormData() {
        super.clearFormData();
    }

    /**
     * 清除缓存(可用于 loadUrl() 前)
     * @param includeDiskFiles 是否包括硬盘文件
     */
    @Override
    public void clearCache(boolean includeDiskFiles) {
        super.clearCache(includeDiskFiles);
    }

    /**
     * 只清除当前页之前的历史记录
     */
    @Override
    public void clearHistory() {
        super.clearHistory();
    }

    /**
     * 清除数据&Cookie
     */
    public void deleteAllData$removeAllCookies(@Nullable ValueCallback<Boolean> callback) {
        WebStorage.getInstance().deleteAllData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(callback);
        } else {
            CookieManager.getInstance().removeAllCookie();
        }
    }


    /**
     * 判断是否有上一个网址
     */
    @Override
    public boolean canGoBack() {
        return super.canGoBack();
    }

    /**
     * 是否可以前进
     */
    @Override
    public boolean canGoForward() {
        return super.canGoForward();
    }

    @Override
    public boolean canGoBackOrForward(int steps) {
        return super.canGoBackOrForward(steps);
    }

    /**
     * 获取webview所有加载栈
     * @see WebBackForwardList#getSize() 获取当前加载栈的长度
     * @see WebBackForwardList#getCurrentItem() 获取webview当前所加载的界面, 可以获得url, title等内容
     * @see WebBackForwardList#getCurrentIndex() 获取当前加载在加载栈中的位置
     * @see WebBackForwardList#getItemAtIndex(int) 获取加载栈中第index页面
     */
    @Override
    public WebBackForwardList copyBackForwardList() {
        return super.copyBackForwardList();
    }

    /**
     * 回到上一个网址
     */
    @Override
    public void goBack() {
        super.goBack();
    }

    /**
     * 前进
     */
    @Override
    public void goForward() {
        super.goForward();
    }

    @Override
    public void goBackOrForward(int steps) {
        super.goBackOrForward(steps);
    }

    /**
     * 注意在Activity / Fragment的onResume() 方法中调用本方法
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 注意在Activity / Fragment的onPause() 方法中调用本方法
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 注意在Activity的onDestroy() / Fragment的onDestroyView() 方法中调用本方法
     */
    @Override
    public void destroy() {
        super.destroy();
    }
}
