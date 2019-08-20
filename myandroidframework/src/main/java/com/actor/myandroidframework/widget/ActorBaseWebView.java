package com.actor.myandroidframework.widget;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.actor.myandroidframework.utils.webview.MyWebChromeClient;
import com.actor.myandroidframework.utils.webview.MyWebViewClient;

/**
 * Description: WebView常用初始化
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-8-20 on 10:34
 *
 * @version 1.0
 */
public class ActorBaseWebView extends WebView {

    public ActorBaseWebView(Context context) {
        super(context);
    }

    /**
     * 初始化
     * @param webViewClient 可传入new MyWebViewClient(), 或者写个类extends MyWebViewClient然后自定义一些自己的方法
     * @param webChromeClient 可传入new MyWebChromeClient(), 或者写个类extends MyWebChromeClient然后自定义一些自己的方法
     */
    public void init(MyWebViewClient webViewClient, MyWebChromeClient webChromeClient) {
        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);

        WebSettings webSettings = getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小,支持双击缩放，同时支持手势操作放大和缩小?
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕.NARROW_COLUMNS:适应内容大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //图片
        webSettings.setBlockNetworkImage(false);//不阻塞网络图片,解决图片不显示
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片

        //其他细节操作
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //字体
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);//设置网页字体大小, 过时, 用下面方法↓
        webSettings.setTextZoom(100);//设置页面的文本缩放百分比, 默认100

        //缓存cache
        webSettings.setDomStorageEnabled(false);//设置缓存,没数据也可加载
//        webSettings.setAppCacheEnabled(true);//页面,图片,脚本,css,js...
//        webSettings.setAppCachePath("path");//设置缓存目录
//        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);//过时,自动管理
//        webSettings.setCacheMode(WebSettings.LOAD...);//设置缓存的模式,见WebView详解.md
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//安卓5.0以上的权限
            /**
             * MIXED_CONTENT_ALWAYS_ALLOW：允许从任何来源加载内容，即使起源是不安全的；
             * MIXED_CONTENT_NEVER_ALLOW：不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
             * MIXED_CONTENT_COMPATIBILITY_MODE：当涉及到混合式内容时，WebView 会尝试去兼容最新Web浏览器的风格。
             **/
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /**
     * 网页加载h5文档
     * @param data html文档 字符串
     */
    public void loadData(String data) {
        loadData(data, "text/html; charset=UTF-8", "UTF-8");
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
