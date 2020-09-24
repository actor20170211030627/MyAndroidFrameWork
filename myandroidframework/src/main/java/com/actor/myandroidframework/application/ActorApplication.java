package com.actor.myandroidframework.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.album.GlideAlbumLoader;
import com.actor.myandroidframework.utils.okhttputils.log.RequestInterceptor;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.Utils;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.net.Proxy;
import java.util.Locale;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Description: 自定义的Application 继承本类, 然后在清单文件中注册
 *              https://github.com/actor20170211030627/MyAndroidFrameWork
 * Author     : 李大发
 * Date       : 2019/7/21 on 16:59
 */
public abstract class ActorApplication extends Application/*MultiDexApplication implements Thread.UncaughtExceptionHandler*/ {

    private static final String EXCEPTION   = "EXCEPTION_FOR_ActorApplication";
    public        boolean          isDebugMode = false;//配置 isDebug 模式
    public CacheDiskUtils aCache;                      //硬盘缓存

    /**
     * 在 onCreate 之前调用, 可以做一些较早的初始化
     * 常用于 MultiDex 以及插件化框架的初始化
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);

        //配置全局是 debug 模式
        ConfigUtils.isDebugMode = isDebugMode = isAppDebug();

        //2.如果是正式环境,在onCreate中设置默认未捕获异常线程
        if (!isDebugMode) {
            Thread.setDefaultUncaughtExceptionHandler(new MyHandler());
        }

        //配置BaseUrl
        String baseUrl = getBaseUrl();
        if (baseUrl != null) ConfigUtils.baseUrl = baseUrl;

        //配置硬盘缓存
        aCache = CacheDiskUtils.getInstance(getFilesDir());

        //配置okhttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .connectTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
//                .readTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
//                .writeTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
//                .addInterceptor(new AddHeaderInterceptor())
//                .addInterceptor(new My401Error$RefreshTokenInterceptor(this))//401登陆过期重新获取token等...
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(this)))
                .cache(new Cache(getFilesDir(), 1024*1024*10));//10Mb;

        OkHttpClient.Builder newBuilder = configOkHttpClientBuilder(builder);
        if (newBuilder == null) newBuilder = builder;
        if (isDebugMode) {
            //最后才添加官方日志拦截器, 否则网络请求的Header等不会打印(因为Interceptor是装在List中, 有序的)
//            newBuilder.addInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY));

            //改成这个日志拦截器, 打印更全面
            newBuilder.addInterceptor(new RequestInterceptor());
        } else {
            newBuilder.proxy(Proxy.NO_PROXY);
        }
        OkHttpUtils.initClient(newBuilder.build());//配置张鸿洋的OkHttpUtils

        //配置画廊(图片/视频选择)
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new GlideAlbumLoader()) // 设置Album加载器。
                .setLocale(Locale.getDefault()) //Locale.CHINA 比如强制设置在任何语言下都用中文显示。
                .build());
    }

//    /**
//     * 配置 EasyHttp, 更多配置见:
//     *   https://github.com/zhou-you/RxEasyHttp#%E9%AB%98%E7%BA%A7%E5%88%9D%E5%A7%8B%E5%8C%96
//     *
//     * //.setReadTimeOut(60 * 1000)//默认60秒, 以下三行可不需要设置
//     * //.setWriteTimeOut(60 * 1000)
//     * //.setConnectTimeout(60 * 1000)
//     * //.setRetryCount(3)//网络不好默认自动重试3次
//     * //.setRetryDelay(500)//每次延时500ms重试, 不需要可以设置为0
//     * //.setRetryIncreaseDelay(0)//每次延时叠加0ms
//     * //.setCacheMode(CacheMode.NO_CACHE)//默认NO_CACHE
//     *   .addCommonHeaders(headers)//设置全局公共头
//     *   .addCommonParams(params)//设置全局公共参数
//     */
//    protected abstract void configEasyHttp(EasyHttp easyHttp);

    /**
     * 配置Builder, 主要是添加'超时' & '拦截器' 等, 示例:
     * builder.connectTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
     *          .readTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
     *          .writeTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
     *          .addInterceptor(new AddHeaderInterceptor())//网络请求前添加请求头, 如果不添加可不设置
     *          .addInterceptor(new My401Error$RefreshTokenInterceptor(this));//在某个项目中,401表示token过期,需要刷新token并重新请求, 根据自己项目而定
     * @return 返回builder, 可以返回null
     */
    protected abstract @Nullable OkHttpClient.Builder configOkHttpClientBuilder(OkHttpClient.Builder builder);

    /**
     * 返回baseUrl, 用于配置 "MyOkHttpUtils" 和 "Retrofit" 的 baseUrl
     * @return 示例return: "https://api.github.com";
     */
    protected abstract @NonNull String getBaseUrl();

    protected class MyHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {//3.重写未捕获异常
            if (e != null) {
                String thread = t == null ? "" : t.toString();
                StackTraceElement[] stackTrace = e.getStackTrace();
                StringBuilder sb = new StringBuilder();
                if (stackTrace != null) {
                    for (StackTraceElement element : stackTrace) {
                        sb.append(element.toString());
                        sb.append("\n");
                    }
                }
                //SPUtils 在发生异常的时候, 存储会回滚. 所以这儿用 CacheDiskUtils
                String exception = aCache.getString(EXCEPTION);
                if (exception == null) {
                    aCache.put(EXCEPTION, thread.concat("\n").concat(sb.toString()));
                } else {
                    if (exception.length() > 2 << 16) exception = "";//131 072
                    aCache.put(EXCEPTION, exception.concat("\n\n\n").concat(thread).concat("\n").concat(sb.toString()));
                }
            }
            onUncaughtException(t, e);
        }
    }

    /**
     * 当正式环境中App崩溃的时候会回调这个方法.
     * 如果是debug环境, 不会抓取bug并且不会回调这个方法
     * @param thread 线程
     * @param e 堆栈信息
     *
     * 示例处理:
     * Intent intent = new Intent(this, LoginActivity.class);
     * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);
     * //定时器
     * AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
     * mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1000:1秒钟后重启应用
     * System.exit(-1);//退出
     * //android.os.Process.killProcess(android.os.Process.myPid());//也一样是退出
     */
    protected abstract void onUncaughtException(Thread thread, Throwable e);

    /**
     * 获取崩溃信息
     */
    public String getCrashExceptionInfo() {
        String string = aCache.getString(EXCEPTION);
        boolean remove = aCache.remove(EXCEPTION);
        return string;
    }

    /**
     * 当我们没在AndroidManifest.xml中设置其 debuggable="true" 属性时:
     * <application android:debuggable="true" tools:ignore="HardcodedDebugMode"
     *
     * 运行:                                这种方式打包时其debug属性为true,
     * Build->Generate Signed APK release: 这种方式打包时其debug属性为法false.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     *
     * 如果release版本也想输出日志，那么这个时候我们到 AndroidManifest.xml 中设置debuggable="true"即可.
     * 或者重写此方法自定义返回.
     */
    protected boolean isAppDebug() {
        return AppUtils.isAppDebug();
    }
}