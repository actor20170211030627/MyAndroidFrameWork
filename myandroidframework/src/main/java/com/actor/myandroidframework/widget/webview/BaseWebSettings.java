package com.actor.myandroidframework.widget.webview;

import android.os.Build;
import android.webkit.WebSettings;

import com.actor.myandroidframework.utils.ConfigUtils;

/**
 * description: WebView默认设置
 *
 * @author : ldf
 * date       : 2020/10/6 on 15
 * @version 1.0
 */
public class BaseWebSettings {

    /**
     * 默认初始化
     */
    public static void defaultInit(WebSettings webSettings) {
        /**
         * 设置js
         */
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        /**
         * 设置自适应屏幕，两者合用
         */
        //将图片调整到适合webview的大小,支持双击缩放，同时支持手势操作放大和缩小?
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //自适应屏幕.NARROW_COLUMNS:适应内容大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        /**
         * 缩放操作
         */
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        /**
         * 图片
         */
        webSettings.setBlockNetworkImage(false);//不阻塞网络图片,解决图片不显示
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片

        /**
         * 字体
         */
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式,utf-8, GBK
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);//设置网页字体大小, 过时, 用下面方法↓
        webSettings.setTextZoom(100);//设置页面的文本缩放百分比, 默认100

        /**
         * 缓存cache
         */
        webSettings.setDomStorageEnabled(false);//设置缓存,没数据也可加载
        webSettings.setAppCacheEnabled(true);//页面,图片,脚本,css,js...
        webSettings.setAppCachePath(ConfigUtils.APPLICATION.getCacheDir().getAbsolutePath());//设置缓存目录
//        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);//过时,自动管理
        /*
         * 设置缓存的模式
         * 什么是cache-control?
         *   cache-control是在请求网页时服务器的响应头,此响应头用于决定网页的缓存策略.
         *   常见的取值有public(所有内容都将被缓存),
         *   private(内容只缓存到私有缓存中),
         *   no-cache(所有内容都不会被缓存),
         *   max-age=xxx(缓存的内容将在 xxx 秒后失效)等等
         *
         * WebSettings.LOAD_CACHE_ELSE_NETWORK  只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
         * WebSettings.LOAD_CACHE_ONLY          只加载缓存
         * WebSettings.LOAD_DEFAULT             根据cache-control决定是否从网络上取数据
         * WebSettings.LOAD_NO_CACHE            不加载缓存
         * WebSettings.LOAD_NORMAL
         */
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        /**
         * 数据库
         */
        webSettings.setDatabaseEnabled(true);
        //已过时, 自动管理
//        webSettings.setDatabasePath(getContext().getDatabasePath("database").getAbsolutePath());
        //地理位置数据库的路径, 已过时, 自动管理
//        webSettings.setGeolocationDatabasePath(getContext().getDir("geolocation", Context.MODE_PRIVATE).getPath());


        /**
         * 加载来源
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//安卓5.0以上的权限
            /**
             * MIXED_CONTENT_ALWAYS_ALLOW：允许从任何来源加载内容，即使起源是不安全的；
             * MIXED_CONTENT_NEVER_ALLOW：不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
             * MIXED_CONTENT_COMPATIBILITY_MODE：当涉及到混合式内容时，WebView 会尝试去兼容最新Web浏览器的风格。
             **/
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        /**
         * 其他操作
         */
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        //保存密码
        webSettings.setSavePassword(false);
        //设置是否启用地理位置
        webSettings.setGeolocationEnabled(true);
        /*
         * 告诉WebView按需启用、禁用或使用插件(swf?)。在 ON_DEMAND 模式意味着如果插件存在，
         * 就可以处理嵌入的插件内容，一个占位符图标将显示而不是插件。
         * 当点击占位符，插件将被启用. 默认OFF.
         */
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        /*
         * UserAgent, 例:
         * Mozilla/5.0 (Linux; Android 9; COL-AL10 Build/HUAWEICOL-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/69.0.3497.100 Mobile Safari/537.36
         */
//        String userAgentString = webSettings.getUserAgentString();
//        webSettings.setUserAgentString(userAgentString);
    }
}
