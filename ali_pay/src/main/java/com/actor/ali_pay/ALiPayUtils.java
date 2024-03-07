package com.actor.ali_pay;

import android.Manifest;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.m.j.c;
import com.alipay.sdk.m.u.l;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.Map;

/**
 * description: 支付宝工具类 <br />
 * <a href="https://open.alipay.com/">支付宝开放平台</a>,
 * <a href="https://opendocs.alipay.com/support/01rfry">APP 支付 SDK 与 DEMO 下载</a>,
 * <a href="https://opendocs.alipay.com/open/204/105296/">Android 集成流程</a>
 *
 * <ol>
 *     <li>
 *         添加依赖 <br />
 *         <code>
 *             //https://opendocs.alipay.com/open/02no45 支付宝 <br />
 *             //implementation 'com.alipay.sdk:alipaysdk-android:+@aar' //直接获取最新版 <br />
 *             implementation 'com.alipay.sdk:alipaysdk-android:15.8.17' //指定版本(推荐)
 *         </code>
 *     </li>
 *     <li>权限已经添加, if调用{@link #payInterceptorWithUrl(Activity, String, boolean, H5PayCallback)}需要再额外添加2个权限.</li>
 *     <li>混淆已经添加</li>
 *     <li>不用初始化, 直接使用!</li>
 * </ol>
 *
 * @author : ldf
 * @date       : 2024/3/6 on 14
 */
public class ALiPayUtils {

    /**
     * 支付
     * @param orderInfo App 支付请求参数字符串，主要包含商家的订单信息，key=value 形式，以 & 连接。
     * @param isShowPayLoading 用户在商家 App 内部点击付款，是否需要一个 loading 做为在支付宝客户端唤起之前的过渡，
     *                         这个值设置为 true，将会在调用 pay 接口的时候直接唤起一个 loading，
     *                         直到唤起 H5 支付页面或者唤起外部的支付宝客户端付款页面 loading 才消失。
     *                         建议将该值设置为 true，优化点击付款到支付唤起支付页面的过渡过程。
     * @param listener 支付宝支付结果 <a href="https://opendocs.alipay.com/open/204/105302?pathHash=eab39489">同步通知说明</a>
     */
    public static void payV2(@NonNull Activity activity, @NonNull String orderInfo,
                             boolean isShowPayLoading, @NonNull OnALiPayResultListener listener) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Map<String, String>>() {
            @Override
            public Map<String, String> doInBackground() throws Throwable {
                PayTask alipay = new PayTask(activity);
                return alipay.payV2(orderInfo, isShowPayLoading);
            }

            @Override
            public void onSuccess(Map<String, String> result) {
                if (listener != null) listener.onALiPayResult(result, isSuccess(result));
            }
        });
    }

    /**
     * 根据结果, 判断是否成功
     * @param result 支付结果
     * @return 是否成功
     */
    public static boolean isSuccess(Map<String, String> result) {
        if (true) {
            return result != null && "9000".equals(result.get("resultStatus"));
        }
        //一样的, 但是要防止支付宝sdk下次更新后字段名称对不上...
        return result != null && String.valueOf(c.c.a).equals(result.get(l.a));
    }

    /**
     * 获取当前sdk版本
     */
    public static String getVersion(@NonNull Activity activity) {
        PayTask payTask = new PayTask(activity);
        return payTask.getVersion();
    }

    public interface OnALiPayResultListener {
        /**
         * @param result 支付宝返回的map
         * @param isSuccess 是否成功
         */
        void onALiPayResult(Map<String, String> result, boolean isSuccess);
    }


    /**
     * <a href="https://opendocs.alipay.com/open/218/105325?pathHash=193593d7">完整版授权 SDK 调用方法</a>
     * @param authInfo 授权信息串, 后台返回
     * @param isShowPayLoading 用户在商家 App 内部点击付款，是否需要一个 loading 做为在支付宝客户端唤起之前的过渡，
     * @param listener 回调, 还需判断回调Map里面的 <code>"200"==result.result_code</code> 的时候, 才算授权成功.
     */
    public static void authV2(@NonNull Activity activity, @NonNull String authInfo,
                              boolean isShowPayLoading, @NonNull OnALiPayResultListener listener) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Map<String, String>>() {
            @Override
            public Map<String, String> doInBackground() throws Throwable {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(activity);
                // 调用授权接口，获取授权结果
                return authTask.authV2(authInfo, isShowPayLoading);
            }

            @Override
            public void onSuccess(Map<String, String> result) {
                if (listener != null) listener.onALiPayResult(result, isSuccess(result));
            }
        });
    }

    /**
     * <a href="https://opendocs.alipay.com/open/204/105695?pathHash=0e5e0985">手机网站支付转 APP 支付 - 支付宝文档中心</a> <br />
     * 加载网页的时候, 重写拦截方法
     * {@link android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, String) WebViewClient.shouldOverrideUrlLoading(..)},
     * 或者{@link android.webkit.WebViewClient#shouldInterceptRequest(android.webkit.WebView, String) WebViewClient.shouldInterceptRequest(..)},
     * 然后调用此方法{@link #payInterceptorWithUrl(Activity, String, boolean, H5PayCallback)}判断是否应该拦截并跳转支付宝。<br />
     * <br />
     * {@link null 注意：}
     * <ol>
     *     <li>
     *         shouldOverrideUrlLoading(..) 无法拦截直接调用 loadUrl(url) 打开的第一个 url，
     *         如果需要拦截直接打开的支付宝网页支付 URL，可改为使用 shouldInterceptRequest(..) 。
     *     </li>
     *     <li>按照文档, 调用次接口还需要2个权限, 需要自己添加: READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE</li>
     * </ol>
     *
     * @param url 重写拦截方法后, 拦截的网站，在网站中下订单并唤起支付宝；
     * @param isShowPayLoading 用户在商家 App 内部点击付款，是否需要一个 loading 做为在支付宝客户端唤起之前的过渡，
     * @param h5PayCallback 支付回调: <br />
     *                      <code>
     *                          &emsp;&emsp; // 支付结果返回 <br />
     *                          &emsp;&emsp; String url1 = result.getReturnUrl(); <br />
     *                          &emsp;&emsp; if (!TextUtils.isEmpty(url1)) runOnUiThread {
     *                              webView.loadUrl(url1);
     *                          }
     *                      </code>
     * @return true：表示URL为支付宝支付URL，URL已经被拦截并支付转化；false：表示URL非支付宝支付URL；<br />
     *         <code>
     *             //如果没有拦截, 就继续加载url <br />
     *             if (!isIntercepted) webView.loadUrl(url);
     *         </code>
     */
    @RequiresPermission(allOf = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public static boolean payInterceptorWithUrl(@NonNull Activity activity, @NonNull String url,
                             boolean isShowPayLoading, @NonNull H5PayCallback h5PayCallback) {
        /**
         * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
         */
        final PayTask task = new PayTask(activity);
        return task.payInterceptorWithUrl(url, isShowPayLoading, h5PayCallback
//        new H5PayCallback() {
//            @Override
//            public void onPayResult(final H5PayResultModel result) {
//                final String url1 = result.getReturnUrl();
//                if (!TextUtils.isEmpty(url1)) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            webView.loadUrl(url1);
//                        }
//                    });
//                }
//            }
//        }
        );
    }
}
