package com.actor.myandroidframework.utils.okhttputils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * description: 这是对鸿洋大神okhttputils的简单封装, get/post方式请求数据, 上传单个/多个文件, 下载文件, getBitmap
 *              由于大神已经停止更新框架, 所以正在考虑换一个网络请求框架:
 *              @see com.actor.myandroidframework.utils.easyhttp.EasyHttpUtils
 *              如果你想继续使用 MyOkHttpUtils, 需要进行额外配置:
 *
 * 1.添加依赖:
 *   //https://github.com/hongyangAndroid/okhttputils
 *   compileOnly ('com.zhy:okhttputils:2.6.2') {
 *       exclude group: 'com.squareup.okhttp3', module: 'okhttp'//3.3.1
 *   }
 *
 * 2.在Application中初始化, 示例:
 *   //配置Okhttp
 *   OkHttpClient.Builder builder = new OkHttpClient.Builder()
 *   //        .connectTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
 *   //        .readTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
 *   //        .writeTimeout(30_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
 *   //        .addInterceptor(new AddHeaderInterceptor())//可添加请求头拦截器
 *   //        .addInterceptor(new My401Error$RefreshTokenInterceptor(this))//401登陆过期拦截器
 *             .cookieJar(new CookieJarImpl(new PersistentCookieStore(this)))
 *             .cache(new Cache(getFilesDir(), 1024*1024*10));//10Mb;
 *   if (isDebugMode) {
 *       //最后才添加日志拦截器, 否则网络请求的Header等不会打印(因为Interceptor是装在List中, 有序的)
 *       builder.addInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY));
 *   } else {
 *       builder.proxy(Proxy.NO_PROXY);
 *   }
 *   OkHttpUtils.initClient(builder.build());//配置张鸿洋的OkHttpUtils
 *
 * @author    : 李大发
 * date       : 2019/3/13 on 17:37
 *
 * @version 1.3.2 修复上传中文文件抛异常问题
 * @version 1.3.3 传参Map<String, String>修改成Map<String, Object>
 * @version 1.3.4 添加tag(),cancel功能
 * @version 1.3.5 增加同步sync方法
 * @version 1.3.6 增加方法 {@link #postBody(String, Map, BaseCallback)}
 */
public class MyOkHttpUtils {

    //Base Url
    protected static final String BASE_URL = ConfigUtils.baseUrl;

    /**
     * 获取 BASE_URL
     * @param url 如果是"http"开头, 直接返回url.
     *           如果不是"http"开头, 会在前面加上 BASE_URL
     * @return
     */
    protected static @NonNull String getUrl(String url) {
        if (url == null) return BASE_URL;
        if (url.startsWith("http://") || url.startsWith("https://")) return url;
        return BASE_URL + url;
    }

    public static <T> void get(String url, Map<String, Object> params, BaseCallback<T> callback) {
        get(url, null, params, callback);
    }

    /**
     * get方式请求数据
     * @param url       地址
     * @param headers   请求头
     * @param params    参数,一般用LinkedHashMap<String, Object>
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象,示例:JSONObject, String, BaseInfo...
     */
    public static <T> void get(String url, Map<String, Object> headers, Map<String, Object> params, BaseCallback<T> callback) {
        OkHttpUtils.get().url(getUrl(url)).tag(callback == null ? null : callback.tag)
                .headers(cleanNullParamMap(headers))
                .params(cleanNullParamMap(params))
                //请求id, 会在回调中返回, 可用于列表请求中传入item的position, 然后在回调中根据id修改对应的item的值
                .id(callback == null ? 0 : callback.id)
                .build().execute(callback);
    }

    /**
     * get同步请求
     */
    public static Response getSync(String url, Map<String, Object> headers, Map<String, Object> params, Object tag) {
        Response execute = null;
        try {
            execute = OkHttpUtils.get().url(getUrl(url)).tag(tag)
                    .headers(cleanNullParamMap(headers))
                    .params(cleanNullParamMap(params))
                    .build().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return execute;
    }

    public static <T> void post(String url, Map<String, Object> params, BaseCallback<T> callback) {
        post(url, null, params, callback);
    }

    /**
     * post方式请求网络
     * @param url       地址
     * @param params    请求参数, 放在请求体中
     * @param callback  监听
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void post(String url, Map<String, Object> headers, Map<String, Object> params, BaseCallback<T> callback) {
        OkHttpUtils.post().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .headers(cleanNullParamMap(headers))
                .params(cleanNullParamMap(params))
                //请求id, 会在回调中返回, 可用于列表请求中传入item的position, 然后在回调中根据id修改对应的item的值
                .id(callback == null ? 0 : callback.id)
                .build().execute(callback);
    }

    /**
     * post同步请求, 比如token过期后重新获取token, 在同一线程内刷新token
     */
    public static Response postSync(String url, Map<String, Object> headers, Map<String, Object> params, Object tag) {
        Response execute = null;
        try {
            execute = OkHttpUtils.post().url(getUrl(url))
                    .tag(tag)
                    .headers(cleanNullParamMap(headers))
                    .params(cleanNullParamMap(params))
                    .build().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return execute;
    }

    /**
     * 通过Post方法向服务器传递String,待试...
     * @param url       地址
     * @param string    传递的String放入请求体(body)
     * @param callback  回调
     */
    public static <T> void postString(String url, String string, BaseCallback<T> callback) {
        OkHttpUtils.postString().url(getUrl(url)).tag(callback == null ? null : callback.tag)
                .content(string).build().execute(callback);
    }

    public static <T> void postJson(String url, String json, BaseCallback<T> callback) {
        postJson(url, null, json, callback);
    }

    public static <T> void postJson(String url, Map<String, Object> jsonMap, BaseCallback<T> callback) {
        postJson(url, null, JSONObject.toJSONString(jsonMap), callback);//FastJson
//        postJson(url, null, GsonUtils.toJson(jsonMap), callback);//Gson
    }

    /**
     * 通过Post方法向服务器传递Json
     * 1.设置Headers里参数:Content-Type:application/json
     * 服务端接收的时候,要加上@RequestBody注解,才能解析成实体
     * @param url       地址
     * @param params    参数
     * @param json      传递的Json放入请求体(body)
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postJson(String url, Map<String, Object> params, String json, BaseCallback<T> callback) {
        //postString的时候, 参数拼在url后面
        if (params != null && !params.isEmpty()) {
            boolean isFirstParams = true;
            StringBuilder urlBuilder = new StringBuilder(url);
            if (!url.endsWith("?")) urlBuilder.append("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                if (!TextUtils.isEmpty(key)) {
                    String value = getNoNullString(entry.getValue());
                    if (isFirstParams) {
                        urlBuilder.append(key).append("=").append(value);
                        isFirstParams = false;
                    } else urlBuilder.append("&").append(key).append("=").append(value);
                }
            }
            url = urlBuilder.toString();
        }
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        if (true) {
            OkHttpUtils.postString()
                    .url(getUrl(url))
                    .tag(callback == null ? null : callback.tag)
                    .content(json == null ? "" : json)//判空否则会报错
                    //.addHeader()//不要通过addHeader去设置contentType
                    //一定要设置MediaType:设置Content-Type 标头中包含的媒体类型值
                    .mediaType(mediaType)
                    .id(callback == null ? 0 : callback.id)
                    .build()
//                    .connTimeOut(15000).readTimeOut(15000).writeTimeOut(15000)
                    .execute(callback);
        } else {
            //也可以这样直接使用Okhttp3直接请求
            RequestBody requestBody = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(getUrl(url))
                    .post(requestBody)
                    .build();
            OkHttpClient client = new OkHttpClient.Builder().build();
            client.newCall(request).enqueue(null);
        }
    }

    /**
     * 将文件作为请求体，发送到服务器
     * @param url
     * @param file
     * @param callback 回调
     * @param <T> 要解析成什么类型的对象
     */
    public static <T> void postFile(String url, File file, BaseCallback<T> callback) {
        OkHttpUtils.postFile().url(getUrl(url)).tag(callback == null ? null : callback.tag)
                .file(file)
                //请求id, 会在回调中返回, 可用于列表请求中传入item的position, 然后在回调中根据id修改对应的item的值(如果用不着,可瞎传一个数字)
                .id(callback == null ? 0 : callback.id).build().execute(callback);
    }

    /**
     * Post表单形式上传单个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param key       表单上传文件的key
     * @param file      文件
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String key, File file, Map<String, Object> headers,
                                     Map<String, Object> params, PostFileCallback<T> callback) {
        List<File> files = new ArrayList<>();
        files.add(file);
        postFiles(url, key, files, headers, params, callback);
    }

    /**
     * Post表单形式上传文件,同时上传多个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param key       表单上传文件的key
     * @param filePaths 文件路径集合
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String key, Collection<String> filePaths,
                                     Map<String, Object> headers, Map<String, Object> params,
                                     PostFileCallback<T> callback) {
        List<File> files = new ArrayList<>();
        if (filePaths != null && filePaths.size() > 0) {
            for (String filePath : filePaths) {
                if (!TextUtils.isEmpty(filePath)) {
                    files.add(new File(filePath));
                }
            }
        }
        postFiles(url, key, files, headers, params, callback);
    }

    /**
     * Post表单形式上传文件,同时上传多个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param key       表单上传文件的key
     * @param files     文件集合
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String key, List<File> files,
                                     Map<String, Object> headers, Map<String, Object> params,
                                     PostFileCallback<T> callback) {
        PostFormBuilder builder = OkHttpUtils.post().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .headers(cleanNullParamMap(headers))
                .params(cleanNullParamMap(params));
//                  .files("file", files);
        if (files != null && files.size() > 0) {
            for (File file : files) {
                //<input type="file" name="mFile"/>注意这个mFile要和后端协商好,否则接收不到
                if (file != null && file.isFile()) {
                    try {
                        /**
                         * https://github.com/square/okhttp/issues/4564
                         * fixed 文件名UTF-8转码,避免上传中文文件时以下方法抛异常问题
                         * @see okhttp3.Headers#checkValue(String, String)
                         */
                        builder.addFile(key, URLEncoder.encode(file.getName(), "UTF-8"), file);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        builder.id(callback == null ? 0 : callback.id).build().execute(callback);
    }

    /**
     * 把参数通过body传到服务器
     * @param url       地址
     * @param params    参数
     * @param callback  回调, 如果要使用id, 在这个回调的构造方法中传入!!
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postBody(@NonNull String url, Map<String, Object> params, BaseCallback<T> callback) {
        FormBody.Builder builder = new FormBody.Builder();
        Map<String, String> cleanNullParamMap = cleanNullParamMap(params);
        if (cleanNullParamMap != null) {
            for (Map.Entry<String, String> entity : cleanNullParamMap.entrySet()) {
                builder.add(entity.getKey(), entity.getValue());
            }
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(getUrl(url))
                .post(formBody)
                .build();
        OkHttpUtils.getInstance().getOkHttpClient()
                .newCall(request)
                .enqueue(callback);
    }

    /**
     * 请求图片,返回Bitmap
     * @param url 网址
     * @param callback 回调
     */
    public static void getBitmap(@NonNull String url, GetBitmapCallback callback) {
        OkHttpUtils.get().url(getUrl(url)).tag(callback == null ? null : callback.tag)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(callback);
    }

    /**
     * 下载文件回调
     *
     * @param url
     * @param callback     回调
     */
    public static void getFile(@NonNull String url, GetFileCallback callback) {
        OkHttpUtils
                .get()
                .url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .build()
                .execute(callback);
    }

    public static <T> void delete(@NonNull String url, Map<String, Object> params, BaseCallback<T> callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, JSONObject.toJSONString(params));
        OkHttpUtils.delete().url(getUrl(url)).tag(callback == null ? null : callback.tag)
                .requestBody(requestBody)
                .id(callback == null ? 0 : callback.id)
                .build().execute(callback);
    }

    public static <T> void put(@NonNull String url, Map<String, Object> params, BaseCallback<T> callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, JSONObject.toJSONString(params));
        OkHttpUtils.put().url(getUrl(url)).tag(callback == null ? null : callback.tag)
                .requestBody(requestBody)
                .id(callback == null ? 0 : callback.id)
                .build().execute(callback);
    }

    public static <T> void customRequest(String url, Map<String, Object> params, BaseCallback<T> callback) {
        //自定义put
        RequestBody requestBody = RequestBody.create((MediaType) null, "json or something");
        OkHttpUtils.put()//also can use delete() ,head() , patch()
                .url(getUrl(url))
                .requestBody("String")
                .requestBody(requestBody)// String/RequestBody
                .build()
                .execute(callback);

        OkHttpUtils.head()
                .url(getUrl(url))
                .addParams("name", "zhy")
                .build()
                .execute(callback);

        //自定义POST
        boolean isFirstParams = true;
        StringBuilder urlBuilder = new StringBuilder(url);
        if (!url.contains("?")) urlBuilder.append("?");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key)) {
                String value = getNoNullString(entry.getValue());
                if (isFirstParams) {
                    urlBuilder.append(key).append("=").append(value);
                    isFirstParams = false;
                } else urlBuilder.append("&").append(key).append("=").append(value);
            }
        }
        url = urlBuilder.toString();
        Request request = new Request.Builder()
                .method("POST",requestBody)
                .url(getUrl(url))
                .build();
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 取消请求:RequestCall call = OkHttpUtils.get().url(url).build();
     */
    public static void cancel(RequestCall call) {
        call.cancel();
    }

    /**
     * 取消请求
     * @param tag 传this(activity or fragment or others),在onDestroy的时候:MyOkHttpUtils.cancelTag(this);
     */
    public static void cancelTag(Object tag) {
        OkHttpUtils.getInstance().cancelTag(tag);
    }

    /**
     * 清空Cookie & Session
     */
    public static void clearCookie$Session() {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl) {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }

    /**
     * 清除key值为null的参数 & 保证value != null
     * 并且转换为Map<String, String>
     * @param map
     * @return Nullable
     */
    protected static final Map<String, String> returnMap = new LinkedHashMap<>();
    protected synchronized static Map<String, String> cleanNullParamMap(@Nullable Map<String, Object> map) {
        if (map == null || map.isEmpty()) return null;
        returnMap.clear();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key)) {
                returnMap.put(key, getNoNullString(entry.getValue()));
            }
        }
        return returnMap;
    }

    protected static String getNoNullString(Object object) {
        return object == null ? "" : object.toString();
    }
}
