package com.actor.myandroidframework.utils.okhttputils;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.okhttputils.log.RequestInterceptor;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * description: OkHttp配置工具类
 * company    :
 *
 * @author : ldf
 * date       : 2024/3/11 on 17
 * @version 1.0
 */
public class OkHttpConfigUtils {

    /**
     * 配置okhttp
     */
    public static OkHttpClient.Builder initOkHttp(boolean isDebugMode) {
        return new OkHttpClient.Builder()
                //设置整个调用超时时间, 默认0
                .callTimeout(0L, TimeUnit.MILLISECONDS)
                //链接超时, 默认10s, 可不设置
                .connectTimeout(10_000L, TimeUnit.MILLISECONDS)
                //读超时, 默认10s, 可不设置
                .readTimeout(10_000L, TimeUnit.MILLISECONDS)
                //写超时, 默认10s, 可不设置
                .writeTimeout(10_000L, TimeUnit.MILLISECONDS)

                //连接失败重试, 连接失败有可能报错EOFException: \n not found: limit=0 content=…
                //参考: https://blog.csdn.net/jiangxiayouyu/article/details/121827079
                .retryOnConnectionFailure(true)
                //设置用于回收HTTP和HTTPS连接的连接池, 默认: 5, 5, TimeUnit.MINUTES
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))

//                .addInterceptor(new AddHeaderInterceptor())
                //添加自定义拦截器, 例: 401登陆过期重新获取token等...
//                .addInterceptor(new My401Error$RefreshTokenInterceptor(this))

                //cookie
//                .cookieJar(new CookieJarImpl(new PersistentCookieStore(ConfigUtils.APPLICATION)))
                //10Mb;
                .cache(new Cache(ConfigUtils.APPLICATION.getFilesDir(), 1024*1024*10));
    }

    /**
     * OkHttp配置完后, 再增加1个日志拦截器, 用于打印非常标准的请求日志
     * @return 最后添加完拦截器后, 就返回OkHttpClient
     */
    public static OkHttpClient addLogInterceptor(@NonNull OkHttpClient.Builder builder, boolean isDebugMode) {
        if (isDebugMode) {
            //最后才添加官方日志拦截器, 否则网络请求的Header等不会打印(因为Interceptor是装在List中, 有序的)
//            builder.addInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY));

            //改成这个日志拦截器, 打印更全面
            builder.addInterceptor(new RequestInterceptor());
        } else {
            builder.proxy(Proxy.NO_PROXY);
        }
        return builder.build();
    }

}
