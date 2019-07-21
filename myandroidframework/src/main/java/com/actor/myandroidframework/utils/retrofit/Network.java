package com.actor.myandroidframework.utils.retrofit;

//import com.actor.myandroidframework.application.ActorApplication;
//import com.actor.myandroidframework.utils.retrofit.api.DownloadFileApi;
//import com.actor.myandroidframework.utils.retrofit.api.LoginApi;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Converter;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description: Retrofit网络请求
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/15 on 9:20
 */
public class Network {
//    public static  OkHttpClient        okHttpClient     = getOkHttpClient();
////    public static  Converter.Factory   converterFactory = new Retrofit2ConverterFactory();//FastJson
//    public static  Converter.Factory   converterFactory = GsonConverterFactory.create();
////    public static CallAdapter.Factory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
//    private static Map<String, Object> apis             = new HashMap<>();
//
//    private static DownloadFileApi downloadFileApi;
//    private static LoginApi        loginApi;
//
//    //public,其他网络可获取OkHttpClient
//    public static OkHttpClient getOkHttpClient() {
//        if (okHttpClient == null) {
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            if (ActorApplication.instance.isDebugMode) {
//                // Log信息拦截器
//                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//这里可以选择拦截级别
//
//                //设置 Debug Log 模式
//                builder.addInterceptor(loggingInterceptor);
//            }
//            // 构建 OkHttpClient 时,将 OkHttpClient.Builder() 传入 with() 方法,进行初始化配置
//            ProgressManager.getInstance().with(builder);//Glide,Download,Upload进度
//            okHttpClient = builder.build();
//        }
//        return okHttpClient;
//    }
//
//    // Okhttp/Retofit上传进度监听
//    public static void addOnUploadListener(String url, ProgressListener progressListener) {
//        ProgressManager.getInstance().addRequestListener(url, progressListener);
//    }
//
//    //Okhttp/Retofit/Glide下载进度监听,此操作请在页面初始化时进行,切勿多次注册同一个(内容相同)监听器.就算多注册也不报错...
//    public static void addOnDownloadListener(String url, ProgressListener progressListener) {
//        ProgressManager.getInstance().addResponseListener(url, progressListener);
//    }
//
//    public static <T> T getApi(Class<T> apiClass) {
//        Object aClass = apis.get(apiClass.getName());
//        if (aClass == null) {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .client(okHttpClient)
//                    .baseUrl(Global.BASE_URL)
//                    .addConverterFactory(converterFactory)
////                .addCallAdapterFactory(rxJavaCallAdapterFactory)
//                    .build();
//            aClass = retrofit.create(apiClass);
//            apis.put(apiClass.getName(), aClass);
//        }
//        return (T) aClass;
//    }
//
//    //下载文件
//    public static DownloadFileApi getDownloadFileApi() {
//        if (downloadFileApi == null) {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .client(okHttpClient)
//                    .baseUrl("http://www.kuchuanyun.com")
//                    .addConverterFactory(converterFactory)
////                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
//                    .build();
//            downloadFileApi = retrofit.create(DownloadFileApi.class);
//        }
//        return downloadFileApi;
//    }
//
//    //登录
//    public static LoginApi getLoginApi() {
//        if (loginApi == null) {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .client(okHttpClient)
//                    .baseUrl(Global.BASE_URL)
//                    .addConverterFactory(converterFactory)
////                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
//                    .build();
//            loginApi = retrofit.create(LoginApi.class);
//        }
//        return loginApi;
//    }
}
