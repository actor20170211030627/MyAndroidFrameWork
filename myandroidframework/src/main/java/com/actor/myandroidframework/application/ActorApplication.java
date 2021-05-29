package com.actor.myandroidframework.application;import android.app.Application;import android.content.Context;import androidx.annotation.NonNull;import androidx.annotation.Nullable;import com.actor.myandroidframework.utils.ConfigUtils;import com.actor.myandroidframework.utils.okhttputils.log.RequestInterceptor;import com.blankj.utilcode.util.CacheDiskUtils;import com.blankj.utilcode.util.CrashUtils;import com.blankj.utilcode.util.LogUtils;import com.blankj.utilcode.util.Utils;import com.zhy.http.okhttp.OkHttpUtils;import com.zhy.http.okhttp.cookie.CookieJarImpl;import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;import java.net.Proxy;import okhttp3.Cache;import okhttp3.OkHttpClient;/** * Description: 自定义的Application 继承本类, 然后在清单文件中注册 *              https://gitee.com/actor20170211030627/MyAndroidFrameWork * Author     : ldf * Date       : 2019/7/21 on 16:59 */public abstract class ActorApplication extends Application/*MultiDexApplication implements Thread.UncaughtExceptionHandler*/ {    protected static final String EXCEPTION_FOR_ActorApplication = "EXCEPTION_FOR_ActorApplication";    public CacheDiskUtils         aCache;                      //硬盘缓存    /**     * 在 onCreate 之前调用, 可以做一些较早的初始化     * 常用于 MultiDex 以及插件化框架的初始化     */    @Override    protected void attachBaseContext(Context base) {        super.attachBaseContext(base);//        MultiDex.install(this);    }    /**     * 在模拟环境中程序终止时会被调用     */    @Override    public void onTerminate() {        super.onTerminate();    }    @Override    public void onCreate() {        super.onCreate();        //配置工具类        Utils.init(this);        //配置硬盘缓存        initDiskCache();        boolean isDebugMode = isAppDebug();        initDefaultUncaughtExceptionHandler(isDebugMode);        //配置BaseUrl        String baseUrl = getBaseUrl(isDebugMode);        if (baseUrl != null) ConfigUtils.baseUrl = baseUrl;        //配置OkHttp        initOkHttp(isDebugMode);        //配置Log日志        initLog(isDebugMode);    }    /**     * 配置硬盘缓存     */    protected void initDiskCache() {        aCache = CacheDiskUtils.getInstance(getFilesDir());    }    /**     * 2.如果是debug环境, 默认未捕获异常, 直接打印在控制台     * @see #getCrashExceptionInfoAndClear() 获取崩溃信息, 并清空     */    protected void initDefaultUncaughtExceptionHandler(boolean isDebugMode) {        if (!isDebugMode) {            CrashUtils.init(new CrashUtils.OnCrashListener() {                @Override                public void onCrash(String crashInfo, Throwable e) {                    //SPUtils 在发生异常的时候, 存储会回滚. 所以这儿用 CacheDiskUtils                    String exception = aCache.getString(EXCEPTION_FOR_ActorApplication);                    if (exception == null) {                        aCache.put(EXCEPTION_FOR_ActorApplication, crashInfo);                    } else {                        if (exception.length() > 2 << 16) exception = "";//131 072                        aCache.put(EXCEPTION_FOR_ActorApplication, exception.concat("\n\n\n").concat(crashInfo));                    }                    onUncaughtException(e);                }            });        }    }    /**     * 配置okhttp     */    protected void initOkHttp(boolean isDebugMode) {        OkHttpClient.Builder builder = new OkHttpClient.Builder()//                .connectTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置//                .readTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置//                .writeTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置//                .addInterceptor(new AddHeaderInterceptor())//                .addInterceptor(new My401Error$RefreshTokenInterceptor(this))//401登陆过期重新获取token等...                .retryOnConnectionFailure(false)//连接失败重试                .cookieJar(new CookieJarImpl(new PersistentCookieStore(this)))                .cache(new Cache(getFilesDir(), 1024*1024*10));//10Mb;        OkHttpClient.Builder newBuilder = configOkHttpClientBuilder(builder);        if (newBuilder == null) newBuilder = builder;        if (isDebugMode) {            //最后才添加官方日志拦截器, 否则网络请求的Header等不会打印(因为Interceptor是装在List中, 有序的)//            newBuilder.addInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY));            //改成这个日志拦截器, 打印更全面            newBuilder.addInterceptor(new RequestInterceptor());        } else {            newBuilder.proxy(Proxy.NO_PROXY);        }        OkHttpUtils.initClient(newBuilder.build());//配置张鸿洋的OkHttpUtils    }    /**     * 配置Builder, 主要是配置'超时' & '重试' & '拦截器' 等, 示例:     * builder.connectTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置     *          .readTimeout(30_000L, TimeUnit.MILLISECONDS) //默认10s, 可不设置     *          .writeTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置     *          .retryOnConnectionFailure(false)             //连接失败重试, 默认false     *          .addInterceptor(new AddHeaderInterceptor())  //网络请求前添加请求头, 如果不添加可不设置     *          .addInterceptor(new My401Error$RefreshTokenInterceptor(this));//在某个项目中,401表示token过期,需要刷新token并重新请求, 根据自己项目而定     * @return 返回builder, 可以返回null     */    @Nullable    protected abstract OkHttpClient.Builder configOkHttpClientBuilder(OkHttpClient.Builder builder);    //配置Log日志    protected void initLog(boolean isDebugMode) {        LogUtils.getConfig()                .setLogSwitch(isDebugMode)//是否能输出日志到 控制台/文件                .setBorderSwitch(true)//是否打印边框                .setConsoleSwitch(true)//是否能输出到 控制台                .setLogHeadSwitch(true)//是否打印头(哪个文件哪一行, 点击能跳转相应文件)                .setSingleTagSwitch(false)//日志是否在控制台开始位置输出, 默认true                .setLog2FileSwitch(false);//是否能输出到 文件, 默认false    }    /**     * 返回baseUrl, 用于配置 "MyOkHttpUtils" 和 "Retrofit" 的 baseUrl     * @return 示例return: "https://api.github.com";     */    @NonNull    protected abstract String getBaseUrl(boolean isDebugMode);    /**     * release环境中App崩溃的时候会回调这个方法.     * 如果是debug环境, 不会抓取bug并且不会回调这个方法     * @param e 堆栈信息     *     * 示例处理:     * Intent intent = new Intent(this, LoginActivity.class);     * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     * PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);     * //定时器     * AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);     * mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1000:1秒钟后重启应用     * System.exit(-1);//退出     * //android.os.Process.killProcess(android.os.Process.myPid());//也一样是退出     */    protected abstract void onUncaughtException(Throwable e);    /**     * 获取崩溃信息, 并清空信息     */    public String getCrashExceptionInfoAndClear() {        String string = aCache.getString(EXCEPTION_FOR_ActorApplication);        boolean remove = aCache.remove(EXCEPTION_FOR_ActorApplication);        return string;    }    /**     * 如果release版本也想输出日志，那么这个时候我们到 AndroidManifest.xml 中设置debuggable="true"即可.     * 或者重写此方法自定义返回.     */    protected boolean isAppDebug() {        return ConfigUtils.IS_APP_DEBUG;    }}