package com.actor.myandroidframework.utils.retrofit;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.retrofit.api.DownloadFileApi;
import com.blankj.utilcode.util.GsonUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description: Retrofit网络请求
 * Author     : ldf
 * Date       : 2019/3/15 on 9:20
 * @version 1.0
 * @version 1.1 修改一点点东西
 */
public class RetrofitNetwork {

    protected static OkHttpClient      okHttpClient;
    protected static final String      BASE_URL = ConfigUtils.baseUrl;
    protected static Converter.Factory converterFactory;
    protected static CallAdapter.Factory       callAdapterFactory;
    protected static final Map<String, Object> APIS    = new HashMap<>();

    private static DownloadFileApi downloadFileApi;


    protected static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        return okHttpClient;
    }

    //返回json转换成Bean的Facory
    protected static Converter.Factory getConverterFactory() {
        if (converterFactory == null) {
//            converterFactory = new Retrofit2ConverterFactory();//FastJson
            converterFactory = GsonConverterFactory.create(GsonUtils.getGson());//Gson
        }
        return converterFactory;
    }

    //返回CallAdapterFactory, 如果需要, 可重写此方法
    protected static CallAdapter.Factory getCallAdapterFactory() {
        if (callAdapterFactory == null) {
//            callAdapterFactory = RxJava2CallAdapterFactory.create();
        }
        return callAdapterFactory;
    }

    /**
     * Okhttp/Retofit 上传进度监听, 如果使用:
     * 1.需要添加依赖
     * //https://github.com/JessYanCoding/ProgressManager Okhttp/Retofit/Glide下载进度监听
     * implementation 'me.jessyan:progressmanager:1.5.0'
     * 2.Application中初始化
     * //可监听Glide,Download,Upload进度
     * ProgressManager.getInstance().with(okHttpClientBuilder);
     */
    public static void addOnUploadListener(String url, ProgressListener progressListener) {
        ProgressManager.getInstance().addRequestListener(url, progressListener);
    }

    /**
     * Okhttp/Retofit/Glide下载进度监听,此操作请在页面初始化时进行,切勿多次注册同一个(内容相同)监听器.就算多注册也不报错...
     */
    public static void addOnDownloadListener(String url, ProgressListener progressListener) {
        ProgressManager.getInstance().addResponseListener(url, progressListener);
    }

    public static <T> T getApi(Class<T> apiClass) {
        Object aClass = APIS.get(apiClass.getName());
        if (aClass == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(getConverterFactory());
            if (getCallAdapterFactory() != null) builder.addCallAdapterFactory(getCallAdapterFactory());
            Retrofit retrofit = builder.build();
            aClass = retrofit.create(apiClass);
            APIS.put(apiClass.getName(), aClass);
        }
        return (T) aClass;
    }

    //下载文件示例
    public static DownloadFileApi getDownloadFileApi() {
//        if (downloadFileApi == null) {
//            Retrofit.Builder builder = new Retrofit.Builder()
//                    .client(okHttpClient)
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(converterFactory);
//            if (callAdapterFactory != null) builder.addCallAdapterFactory(callAdapterFactory);
//            Retrofit retrofit = builder.build();
//            downloadFileApi = retrofit.create(DownloadFileApi.class);
//        }
//        return downloadFileApi;
        return getApi(DownloadFileApi.class);
    }
}
