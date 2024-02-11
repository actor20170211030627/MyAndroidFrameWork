package com.actor.myandroidframework.widget.webview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.blankj.utilcode.util.GsonUtils;

/**
 * Description: WebView常用初始化 <br />
 * 更多配置: <a href="https://github.com/googlearchive/chromium-webview-samples">chromium-webview-samples - Github</a>
 * <ul>
 *     不显示滚动条: <code>android:scrollbars="none"</code>
 * </ul>
 *
 * <br />
 * Author     : ldf
 * Date       : 2019-8-20 on 10:34
 *
 * @version 1.0
 */
public class BaseWebView extends WebView {

    protected long threadId;

    public BaseWebView(Context context) {
        super(context);
        init(context, null);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        threadId = ThreadUtils.getCurrentThreadId();
    }

    /**
     * 初始化
     * @param webViewClient 可传入{@link BaseWebViewClient new BaseWebViewClient()}, 或者写个类extends {@link BaseWebViewClient}然后自定义一些自己的方法
     * @param webChromeClient 可传入{@link BaseWebChromeClient new BaseWebChromeClient()}, 或者写个类extends {@link BaseWebChromeClient}然后自定义一些自己的方法
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
//        addJavascriptInterface(object, "android");

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
     * 加载网页, 调用方法, 加载js方法等
     * @param url "网页url" or "本地H5" or "javascript方法", 例:
     *            <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *                <tr>
     *                    <th align="center">传参示例</th>
     *                    <th align="center">说明</th>
     *                </tr>
     *                <tr>
     *                    <td>https://www.baidu.com/</td>
     *                    <td>网页url</td>
     *                </tr>
     *                <tr>
     *                    <td>file:///android_asset/test.html</td>
     *                    <td>本项目h5, 项目路径: src/main/assets/test.html</td>
     *                </tr>
     *                <tr>
     *                    <td>javascript: alert('你好呀h5!')</td>
     *                    <td>调用js原生方法, 自定义方法等</td>
     *                </tr>
     *                <tr>
     *                    <td>javascript:(function(b){...})(window)</td>
     *                    <td>安卓加载自定义js方法</td>
     *                </tr>
     *            </table>
     */
    @Override
    public void loadUrl(String url) {
        /**
         * js和java对象在webview的后台、私有线程交互的，所以要注意线程安全, 否则可能报错:
         * java.lang.Throwable: A WebView method was called on thread 'JavaBridge'. All WebView methods must be called on the same thread.
         */
        if (threadId == ThreadUtils.getCurrentThreadId()) {
            super.loadUrl(url);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    BaseWebView.super.loadUrl(url);
                }
            });
        }
    }

    /**
     * 调用h5方法, 并传入参数 <br />
     * {@link null 注意:}
     * <ol>
     *     <li>确保调用的h5方法没有返回值(return xxx;), 如果这个h5方法有返回值的话, 第2次再使用loadUrl()方式调用这方法h5会报错. (实测, 原因未探究)</li>
     *     <li>如果你调用的h5方法有返回值, 请调用方法: {@link #callH5Method(String, ValueCallback, Object...)}</li>
     * </ol>
     * @param h5MethodName 要调用的h5方法名称
     * @param params 可变参数(不要直接传数组, ∵传给h5的就是多个参数而不是数组对象)
     */
    @Deprecated
    public void callH5Method(@NonNull String h5MethodName, Object... params) {
        String param = params2String(params);
        loadUrl(TextUtils2.getStringFormat("javascript:%s(%s)", h5MethodName, param));
    }

    /**
     * 调用h5方法, 并传入对象json
     * @param methodName 要调用的h5方法名称
     * @param json 请务必是json, 可以由数组[], Collection(列表), Map, Object 等通过{@link GsonUtils#toJson(Object)}转换而来
     */
    @Deprecated
    public void callH5MethodByJson(@NonNull String methodName, String json) {
        loadUrl(TextUtils2.getStringFormat("javascript:%s(%s)", methodName, json));
    }

    /**
     * 调用h5方法, 并传入参数 <br />
     * {@link null 注意: 确保调用的h5方法没有返回值(return xxx;), 否则如果这个h5方法有返回值的话, 第2次再调用这方法h5会报错}
     * @param h5MethodName 要调用的h5方法名称
     * @param callback 回调
     * @param params 参数
     */
    public void callH5Method(@NonNull String h5MethodName, @Nullable ValueCallback<String> callback, Object... params) {
        String param = params2String(params);
        evaluateJavascript(TextUtils2.getStringFormat("javascript:%s(%s)", h5MethodName, param), callback);
    }

    /**
     * 调用h5方法, 并传入参数 <br />
     * {@link null 注意: 确保调用的h5方法没有返回值(return xxx;), 否则如果这个h5方法有返回值的话, 第2次再调用这方法h5会报错}
     * @param h5MethodName 要调用的h5方法名称
     * @param callback 回调
     * @param json 请务必是json, 可以由数组[], Collection(列表), Map, Object 等通过{@link GsonUtils#toJson(Object)}转换而来
     */
    public void callH5MethodByJson(@NonNull String h5MethodName, String json, @Nullable ValueCallback<String> callback) {
        evaluateJavascript(TextUtils2.getStringFormat("javascript:%s(%s)", h5MethodName, json), callback);
    }

    /**
     * 调用JS方法获, 并取返回值
     * @param script js方法和参数, 例: "javascript:JSMethod(参数)"
     * @param resultCallback js的回调
     */
    @Override
    public void evaluateJavascript(String script, @Nullable ValueCallback<String> resultCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (threadId == ThreadUtils.getCurrentThreadId()) {
                super.evaluateJavascript(script, resultCallback);
            } else {
                post(new Runnable() {
                    @Override
                    public void run() {
                        BaseWebView.super.evaluateJavascript(script, resultCallback);
                    }
                });
            }
        } else {
            LogUtils.error("SDK >= Android 4.4 调用evaluateJavascript(...)才有回调!");
        }
    }

    /**
     * 将参数转换成String, 可传入: <br />
     * byte, short, int, long, float, double, boolean, char, null, String, <br />
     * {@link char 注意:} 如果传入char类型的参数, 有些低版本手机可能会报错, 例: 传入{@link Character#MAX_VALUE}, Honor 7A(Android 8.0.0) 会报错)
     */
    @Nullable
    protected String params2String(@Nullable Object... params) {
        if (params == null) return null;
        if (params.length == 0) return "";
        String json = GsonUtils.toJson(params);
        if (true) {
            //去掉{}, 返回参数组成的字符串
            return json.substring(1, json.length() - 1);
        }
        //if你发现转换的不对, 可重写此方法
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null || param instanceof Byte || param instanceof Short || param instanceof Integer
                    || param instanceof Long || param instanceof Float || param instanceof Double
                    || param instanceof Boolean
            ) {
                str.append(param);
            } else if (param.getClass().isArray()) {
//                str.append(Arrays.toString((Object[]) param));  //转换有误, 字符串没""
                str.append(GsonUtils.toJson(param));
            } else {
                //char(要转成String, 否则传到h5不认识甚至会报错), String, Object, Others...
                str.append("'").append(param).append("'");
            }
            if (i != params.length - 1) str.append(",");
        }
        return str.toString();
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
     * 获取WebView所有加载栈
     * <ol>
     *     <li>{@link WebBackForwardList#getSize()} 获取当前加载栈的长度</li>
     *     <li>{@link WebBackForwardList#getCurrentItem()} 获取webview当前所加载的界面, 可以获得url, title等内容</li>
     *     <li>{@link WebBackForwardList#getCurrentIndex()} 获取当前加载在加载栈中的位置</li>
     *     <li>{@link WebBackForwardList#getItemAtIndex(int)} 获取加载栈中第index页面</li>
     * </ol>
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
