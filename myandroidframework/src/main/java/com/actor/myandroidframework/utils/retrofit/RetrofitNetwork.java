package com.actor.myandroidframework.utils.retrofit;

import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.retrofit.api.DownloadFileApi;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description: Retrofit网络请求
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/15 on 9:20
 * @version 1.0
 */
public class RetrofitNetwork {

    private static OkHttpClient        okHttpClient     = getOkHttpClient();
    private static String baseUrl;
    private static Converter.Factory   converterFactory = getConverterFactory();
    private static CallAdapter.Factory callAdapterFactory = getCallAdapterFactory();
    private static Map<String, Object> apis              = new HashMap<>();

    private static DownloadFileApi downloadFileApi;

    public RetrofitNetwork(String baseUrl) {
        RetrofitNetwork.baseUrl = baseUrl;
    }

    protected static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) okHttpClient = ActorApplication.instance.okHttpClient;
        return okHttpClient;
    }

    //返回json转换成Bean的Facory
    protected static Converter.Factory getConverterFactory() {
        //return new Retrofit2ConverterFactory();//FastJson
        return GsonConverterFactory.create();//Gson
    }

    //返回CallAdapterFactory
    protected static CallAdapter.Factory getCallAdapterFactory() {
        //return RxJava2CallAdapterFactory.create();
        return null;
    }

    // Okhttp/Retofit上传进度监听
//    public static void addOnUploadListener(String url, ProgressListener progressListener) {
//        ProgressManager.getInstance().addRequestListener(url, progressListener);
//    }

    //Okhttp/Retofit/Glide下载进度监听,此操作请在页面初始化时进行,切勿多次注册同一个(内容相同)监听器.就算多注册也不报错...
//    public static void addOnDownloadListener(String url, ProgressListener progressListener) {
//        ProgressManager.getInstance().addResponseListener(url, progressListener);
//    }

    public static <T> T getApi(Class<T> apiClass) {
        Object aClass = apis.get(apiClass.getName());
        if (aClass == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(converterFactory);
            if (callAdapterFactory != null) builder.addCallAdapterFactory(callAdapterFactory);
            Retrofit retrofit = builder.build();
            aClass = retrofit.create(apiClass);
            apis.put(apiClass.getName(), aClass);
        }
        return (T) aClass;
    }

    //下载文件示例
    public static DownloadFileApi getDownloadFileApi() {
        if (downloadFileApi == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(converterFactory);
            if (callAdapterFactory != null) builder.addCallAdapterFactory(callAdapterFactory);
            Retrofit retrofit = builder.build();
            downloadFileApi = retrofit.create(DownloadFileApi.class);
        }
        return downloadFileApi;
    }
}
