package com.actor.myandroidframework.utils.MyOkhttpUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
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
 * Description: get/post方式请求数据, 上传单个/多个文件,下载文件, getBitmap
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/13 on 17:37
 * Last Update: 2019/05/27
 * @version 1.3.2 修复上传中文文件抛异常问题
 * @version 1.3.3 传参Map<String, String>修改成Map<String, Object>
 * @version 1.3.4 添加tag(),cancel功能
 * @version 1.3.5 添加一个post有请求头的方法 {@link #post(String, Map, BaseCallback)}
 * @version 1.3.6 增加同步sync方法
 * @version 1.3.7 get/post中增加传入id参数
 * @version 1.3.8 增加方法 {@link #postBody(String, Map, BaseCallback)}
 */
public class MyOkHttpUtils {

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
        OkHttpUtils.get().url(url).tag(callback == null ? null : callback.tag)
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
            execute = OkHttpUtils.get().url(url).tag(tag)
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
        OkHttpUtils.post().url(url)
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
            execute = OkHttpUtils.post().url(url)
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
        OkHttpUtils.postString().url(url).tag(callback == null ? null : callback.tag)
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
        //postString的时候,没有addParams方法,参数只能拼在url后面
        if (params != null && params.size() > 0) {
            boolean isFirstParams = true;
            if (!url.endsWith("?")) url.concat("?");
            StringBuilder urlBuilder = new StringBuilder(url);
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
                    .url(url)
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
                    .url(url)
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
        OkHttpUtils.postFile().url(url).tag(callback == null ? null : callback.tag)
                .file(file)
                //请求id, 会在回调中返回, 可用于列表请求中传入item的position, 然后在回调中根据id修改对应的item的值(如果用不着,可瞎传一个数字)
                .id(callback == null ? 0 : callback.id).build().execute(callback);
    }

    /**
     * Post表单形式上传单个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param fileName  表单上传文件的name
     * @param file      文件
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String fileName, File file, Map<String, Object> headers,
                                     Map<String, Object> params, PostFileCallback<T> callback) {
        List<File> files = new ArrayList<>();
        files.add(file);
        postFiles(url, fileName, files, headers, params, callback);
    }

    /**
     * Post表单形式上传文件,同时上传多个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param fileName  表单上传文件的name
     * @param filePaths 文件路径集合
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String fileName, Collection<String> filePaths,
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
        postFiles(url, fileName, files, headers, params, callback);
    }

    /**
     * Post表单形式上传文件,同时上传多个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param fileName  表单上传文件的name
     * @param files     文件集合
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String fileName, List<File> files,
                                     Map<String, Object> headers, Map<String, Object> params,
                                     PostFileCallback<T> callback) {
        PostFormBuilder builder = OkHttpUtils.post().url(url)
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
                         * fixed 文件名UTF-8转码,避免上传中文文件时以下方法抛异常问题,后面版本好像已经修复了这个问题
                         * @see okhttp3.Headers#checkValue(String, String)
                         */
                        builder.addFile(fileName, URLEncoder.encode(file.getName(), "UTF-8"), file);
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
                .url(url)
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
        OkHttpUtils.get().url(url).tag(callback == null ? null : callback.tag)
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
                .url(url)
                .tag(callback == null ? null : callback.tag)
                .build()
                .execute(callback);
    }

    /**
     * 同步请求示例
     * @param url
     * @param tag 传this(activity or fragment),在onDestroy的时候:MyOkHttpUtils.cancelTag(this);
     */
    public static void getSync(String url, Object tag) {
        try {
            Response response = OkHttpUtils
                    .get()
                    .url(url)
                    .tag(tag)
                    .build()
                    .execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                //do something...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void otherRequest(String url, Callback callback) {
        OkHttpUtils
                .put()//also can use delete() ,head() , patch()
                .url("http://11111.com")
                .requestBody("may be something")//String
//                .requestBody(RequestBody.create(null, "may be something"))//RequestBody
                .build()
                .execute(callback);

        try {
            OkHttpUtils
                    .head()
                    .url(url)
                    .addParams("name", "zhy")
                    .build()
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消请求:RequestCall call = OkHttpUtils.get().url(url).build();
     */
    public static void cancel(RequestCall call) {
        call.cancel();
    }

    /**
     * 取消请求
     * @param tag 传this(activity or fragment),在onDestroy的时候:MyOkHttpUtils.cancelTag(this);
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
    private static final Map<String, String> returnMap = new LinkedHashMap<>();
    private synchronized static Map<String, String> cleanNullParamMap(@Nullable Map<String, Object> map) {
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

    private static String getNoNullString(Object object) {
        return object == null ? "" : object.toString();
    }
}
