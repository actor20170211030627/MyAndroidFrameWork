package com.actor.myandroidframework.utils.easyhttp;

import com.blankj.utilcode.util.FileIOUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.body.UIProgressResponseCallBack;
import com.zhouyou.http.request.BaseRequest;
import com.zhouyou.http.request.CustomRequest;
import com.zhouyou.http.request.DeleteRequest;
import com.zhouyou.http.request.DownloadRequest;
import com.zhouyou.http.request.GetRequest;
import com.zhouyou.http.request.PostRequest;
import com.zhouyou.http.request.PutRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * description: 对 EasyHttp 的简单封装: https://github.com/zhou-you/RxEasyHttp
 *
 * @author    : 李大发
 * date       : 2020/4/17 on 16:23
 *
 * @version 1.0
 */
public class EasyHttpUtils {

    protected static final Map<String, List<Disposable>> TAGS = new HashMap<>();

    public static Disposable get(String url, Map<String, String> params, BaseCallBack6 callBack6) {
        return get(url, null, params, callBack6);
    }

    /**
     * get 请求
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param callBack6 回调, 泛型类型:
     *                  支持回调的类型可以是Bean、String、CacheResult<Bean>、CacheResult<String>、List<Bean>
     */
    public static Disposable get(String url, Map<String, String> headers, Map<String, String> params, BaseCallBack6 callBack6) {
        GetRequest getRequest = EasyHttp.get(url).params(params)
//                .addCookie()
//                .addInterceptor()
//                .accessToken(false)//是否需要追加token, 默认false
//                .baseUrl()
//                .cacheKey(getClass().getSimpleName()+"test")
//                .cacheMode(CacheMode.CACHEANDREMOTE)
//                .cacheTime(5 * 60)//缓存时间300s，默认-1永久缓存  okhttp和自定义缓存都起作用
                //.okCache(new Cache());//okhttp缓存，模式为默认模式（CacheMode.DEFAULT）才生效
                //.cacheDiskConverter(new GsonDiskConverter())//默认使用的是 new SerializableDiskConverter();
//                .removeParam("appId")
//                .retryCount(5)//重试次数

//                .readTimeOut(30 * 1000)//局部定义读超时
//                .writeTimeOut(30 * 1000)
//                .connectTimeout(30 * 1000)

//                .sign(true)//是否需要签名, 默认false
//                .syncRequest(false)//设置同步请求, 默认false
//                .timeStamp(false)//是否需要追加时间戳, 默认false
                ;
        addHeaders(getRequest, headers);
        Disposable disposable = getRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    public static Disposable post(String url, Map<String, String> params, BaseCallBack6 callBack6) {
        return post(url, null, params, callBack6);
    }

    /**
     * post 请求
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param callBack6 回调
     */
    public static Disposable post(String url, Map<String, String> headers, Map<String, String> params, BaseCallBack6 callBack6) {
        PostRequest postRequest = EasyHttp.post(url).params(params);
        addHeaders(postRequest, headers);
        Disposable disposable = postRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    /**
     * 将 string 传到服务器
     * @param url url
     * @param headers 请求头 , 可传null
     * @param string 字符串
     * @param callBack6 回调
     */
    public static Disposable postString(String url, Map<String, String> headers, String string, BaseCallBack6 callBack6) {
        //默认类型：MediaType.parse("text/plain")
        PostRequest postRequest = EasyHttp.post(url).upString(string/*, String mediaType*/);
        addHeaders(postRequest, headers);
        Disposable disposable = postRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    /**
     * 将 json 传到服务器
     * @param url url
     * @param headers 请求头 , 可传null
     * @param json json
     * @param callBack6 回调
     */
    public static Disposable postJson(String url, Map<String, String> headers, String json, BaseCallBack6 callBack6) {
        PostRequest postRequest = EasyHttp.post(url).upJson(json);
        addHeaders(postRequest, headers);
        Disposable disposable = postRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    /**
     * 将 object 传到服务器
     * 必须要增加.addConverterFactory(GsonConverterFactory.create())设置,
     * 本质就是把object转成json给到服务器
     * @param url url
     * @param headers 请求头 , 可传null
     * @param obj 对象
     * @param callBack6 回调
     */
    public static Disposable postObject(String url, Map<String, String> headers, Object obj, BaseCallBack6 callBack6) {
        PostRequest postRequest = EasyHttp.post(url).upObject(obj).addConverterFactory(GsonConverterFactory.create());
        addHeaders(postRequest, headers);
        Disposable disposable = postRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    /**
     * 将文件以流的方式传到服务器
     * @param url url
     * @param headers 请求头 , 可传null
     * @param filePath 文件路径
     * @param callBack6 回调
     */
    public static Disposable postFile(String url, Map<String, String> headers, String filePath, BaseCallBack6 callBack6) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                byte[] bytes = FileIOUtils.readFile2BytesByStream(file);
                PostRequest postRequest = EasyHttp.post(url).upBytes(bytes);
                addHeaders(postRequest, headers);
                Disposable disposable = postRequest.execute(callBack6);
                if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
                return disposable;
            }
        }
        return null;
    }


    /**
     * 将文件以表单的形式传到服务器
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param filePath 文件路径
     * @param progressCallback 进度回调, 可以刷新UI
     * @param callBack6 回调
     */
    public static Disposable postFile(String url, Map<String, String> headers,
                                      Map<String, String> params, String key, String filePath,
                                      UIProgressResponseCallBack progressCallback,
                                      BaseCallBack6 callBack6) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                PostRequest postRequest = EasyHttp.post(url)
                        .params(params)
                        .params(key, file, progressCallback);
                addHeaders(postRequest, headers);
                Disposable disposable = postRequest.execute(callBack6);
                if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
                return disposable;
            }
        }
        return null;
    }


    /**
     * 将文件以表单的形式传到服务器
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param filePaths 文件路径集合
     * @param callBack6 回调
     */
    public static Disposable postFiles(String url, Map<String, String> headers,
                                      Map<String, String> params, String key, Collection<String> filePaths,
                                      UIProgressResponseCallBack progressCallback,
                                      BaseCallBack6 callBack6) {
        if (filePaths != null && !filePaths.isEmpty()) {
            List<File> files = new ArrayList<>(filePaths.size());
            for (String filePath : filePaths) {
                if (filePath != null) files.add(new File(filePath));
            }
            return postFiles(url, headers, params, key, files, progressCallback, callBack6);
        }
        return null;
    }


    /**
     * 将文件以表单的形式传到服务器
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param files 文件集合
     * @param callBack6 回调
     */
    public static Disposable postFiles(String url, Map<String, String> headers,
                                       Map<String, String> params, String key, List<File> files,
                                       UIProgressResponseCallBack progressCallback,
                                       BaseCallBack6 callBack6) {
        if (files != null && !files.isEmpty()) {
            PostRequest postRequest = EasyHttp.post(url).params(params);
            postRequest.addFileParams(key, files, progressCallback);
            addHeaders(postRequest, headers);
            Disposable disposable = postRequest.execute(callBack6);
            if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
            return disposable;
        }
        return null;
    }


    public static Disposable delete(String url, Map<String, String> params, BaseCallBack6 callBack6) {
        return delete(url, null, params, callBack6);
    }

    /**
     * delete 请求
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param callBack6 回调
     */
    public static Disposable delete(String url, Map<String, String> headers, Map<String, String> params, BaseCallBack6 callBack6) {
        DeleteRequest deleteRequest = EasyHttp.delete(url).params(params)
//                .upJson("json")//可传json
                ;
        addHeaders(deleteRequest, headers);
        Disposable disposable = deleteRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    public static Disposable put(String url, Map<String, String> params, BaseCallBack6 callBack6) {
        return put(url, null, params, callBack6);
    }

    /**
     * put 请求
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param callBack6 回调
     */
    public static Disposable put(String url, Map<String, String> headers, Map<String, String> params, BaseCallBack6 callBack6) {
        PutRequest putRequest = EasyHttp.put(url).params(params);
        addHeaders(putRequest, headers);
        Disposable disposable = putRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    /**
     * 下载文件
     * @param url url
     * @param headers 请求头 , 可传null
     * @param params 参数, 可传null
     * @param callBack6 回调
     */
    public static Disposable download(String url, Map<String, String> headers, Map<String, String> params, BaseCallBack6 callBack6) {
        DownloadRequest downloadRequest = EasyHttp.downLoad(url).params(params)
                //默认下载的目录: /storage/emulated/0/Android/data/包名/files
//                .savePath("")
                //1.首先检查用户是否传入了文件名,如果传入,将以用户传入的文件名命名
                //2.如果没有传入文件名，默认名字是时间戳生成的。
                //3.如果传入了文件名但是没有后缀，程序会自动解析类型追加后缀名
//                .saveName("")
                ;
        addHeaders(downloadRequest, headers);
        Disposable disposable = downloadRequest.execute(callBack6);
        if (callBack6 != null && callBack6.tag != null) putDisposable(callBack6.tag, disposable);
        return disposable;
    }


    /**
     * 自定义请求示例
     */
    public static CustomRequest custom(String url, Map<String, String> headers, Map<String, String> params) {
        CustomRequest customRequest = EasyHttp.custom()
                .addConverterFactory(GsonConverterFactory.create(/*new Gson()*/))
                .params(params)
                .build();
        addHeaders(customRequest, headers);
        return customRequest;
    }


    //添加请求头
    protected static void addHeaders(BaseRequest baseRequest, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                baseRequest.headers(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 移除缓存
     */
    public static void removeCache(String key) {
        if (key != null) EasyHttp.removeCache(key);
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        EasyHttp.clearCache();
    }

    /**
     * 将 Disposable 存起来, 页面销毁的时候取消网络请求
     * @param tag
     * @param disposable
     */
    public static void putDisposable(Object tag, Disposable disposable) {
        if (tag == null) return;
        List<Disposable> list = TAGS.get(tag.getClass().getName());
        if (list == null) list = new ArrayList<>();
        list.add(disposable);
    }

    /**
     * 取消订阅
     * @param tag 传this(activity or fragment or others),在onDestroy的时候:MyOkHttpUtils.cancelTag(this);
     */
    public static void cancelSubscription(Object tag) {
        if (tag != null) {
            String key = tag.getClass().getName();
            List<Disposable> list = TAGS.get(key);
            if (list != null) {
                for (Disposable disposable : list) {
                    //取消请求
                    EasyHttp.cancelSubscription(disposable);
                }
            }
            //移除 tag
            TAGS.remove(key);
        }
        Collection<List<Disposable>> values = TAGS.values();
        Iterator<List<Disposable>> iterator = values.iterator();
        while (iterator.hasNext()) {
            List<Disposable> list = iterator.next();
            Iterator<Disposable> iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                Disposable disposable = iterator2.next();
                //移除无用的 Disposable
                if (disposable == null || disposable.isDisposed()) iterator2.remove();
            }
            //移除空 List
            if (list.isEmpty()) iterator.remove();
        }
    }
}
