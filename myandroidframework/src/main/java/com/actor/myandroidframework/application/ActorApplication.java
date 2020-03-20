package com.actor.myandroidframework.application;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.album.GlideAlbumLoader;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.net.Proxy;
import java.util.Locale;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Description: 自定义的Application 继承本类, 然后在清单文件中注册
 *              https://github.com/actor20170211030627/MyAndroidFrameWork
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/7/21 on 16:59
 */
public abstract class ActorApplication extends Application/* implements Thread.UncaughtExceptionHandler*/ {

    public        boolean          isDebugMode = false;//用于配置"正式环境"的isDebug的值,★★★注意:上线前一定要改成false★★★
    public int mainThreadId, screenWidth, screenHeight;//屏幕宽高
    public CacheDiskUtils aCache;                      //硬盘缓存
    private static final String    EXCEPTION   = "EXCEPTION_FOR_ActorApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mainThreadId = android.os.Process.myTid();//当前线程的id, 这儿是主线程id
        if (getMode()) isDebugMode = true;//如果是"debug环境",那么值就一定是true(加判断是因为要让正式环境也可以开debug模式)
        if (!isDebugMode) {//2.如果是正式环境,在onCreate中设置默认未捕获异常线程
            Thread.setDefaultUncaughtExceptionHandler(new MyHandler());
        }
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();

        //配置信息
        ConfigUtils.baseUrl = getBaseUrl();
        ConfigUtils.isDebugMode = isDebugMode;

        //配置硬盘缓存
        aCache = CacheDiskUtils.getInstance(getFilesDir());

        //配置okhttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .connectTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
//                .readTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
//                .writeTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
//                .addInterceptor(new AddHeaderInterceptor())
//                .addInterceptor(new My401Error$RefreshTokenInterceptor(this))//401登陆过期
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(this)))
                .cache(new Cache(getFilesDir(), 1024*1024*10));//10Mb;

        OkHttpClient.Builder newBuilder = getOkHttpClientBuilder(builder);
        if (newBuilder == null) newBuilder = builder;
        ProgressManager.getInstance().with(newBuilder);//可监听Glide,Download,Upload进度
        if (isDebugMode) {
            //最后才添加日志拦截器, 否则网络请求的Header等不会打印(因为Interceptor是装在List中, 有序的)
            newBuilder.addInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY));
        } else {
            newBuilder.proxy(Proxy.NO_PROXY);
        }
        OkHttpUtils.initClient(newBuilder.build());//配置张鸿洋的OkHttpUtils

        /**
         * 配置画廊
         */
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new GlideAlbumLoader()) // 设置Album加载器。
                .setLocale(Locale.getDefault()) //Locale.CHINA 比如强制设置在任何语言下都用中文显示。
                .build());
    }

    /**
     * 配置Builder, 主要是添加'超时' & '拦截器' 等, 示例:
     * builder.connectTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
     *          .readTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
     *          .writeTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
     *          .addInterceptor(new AddHeaderInterceptor())//网络请求前添加请求头, 如果不添加可不设置
     *          .addInterceptor(new My401Error$RefreshTokenInterceptor(this));//在某个项目中,401表示token过期,需要刷新token并重新请求, 根据自己项目而定
     * return builder;
     *
     * @return 返回builder, 可以返回null
     */
    protected abstract @Nullable OkHttpClient.Builder getOkHttpClientBuilder(OkHttpClient.Builder builder);

    /**
     * 返回baseUrl, 用于配置Retrofit的baseUrl
     * @return 示例return: "https://api.github.com";
     */
    protected abstract @NonNull String getBaseUrl();

    private class MyHandler implements Thread.UncaughtExceptionHandler {
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
     * 当我们没在AndroidManifest.xml中设置其debug属性时:
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为法false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     *
     * 如果release版本也想输出日志，那么这个时候我们到 AndroidManifest.xml 中的application
     * 标签中添加属性强制设置debugable即可:
     * <application android:debuggable="true" tools:ignore="HardcodedDebugMode"
     */
    private boolean getMode(){
        return AppUtils.isAppDebug();
//        try {
//            ApplicationInfo info= getApplicationInfo();
//            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) !=0 ;
//        } catch (Exception e) {
//            return false;
//        }
    }
}