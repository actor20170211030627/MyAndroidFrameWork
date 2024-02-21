package com.actor.myandroidframework.utils.easyhttp;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.request.HttpRequest;
import com.tencent.mmkv.MMKV;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2022/03/22
 *    desc   : Http 缓存管理器
 *    <br />
 *    本类地址: <a href="https://github.com/getActivity/EasyHttp/blob/master/app/src/main/java/com/hjq/easy/demo/http/model/HttpCacheManager.java" target="_blank">HttpCacheManager.java</a>
 */
public class HttpCacheManager {

    private static final MMKV HTTP_CACHE_CONTENT = MMKV.mmkvWithID("http_cache_content");;

    private static final MMKV HTTP_CACHE_TIME = MMKV.mmkvWithID("http_cache_time");

    /**
     * 生成缓存的 key
     */
    public static String generateCacheKey(@NonNull HttpRequest<?> httpRequest) {
        IRequestApi requestApi = httpRequest.getRequestApi();
//        return "请替换成当前的用户 id" + "\n" + requestApi.getApi() + "\n" + GsonUtils.toJson(requestApi);
        return requestApi.getApi() + "\n" + GsonUtils.toJson(requestApi);
    }

    /**
     * 读取缓存
     */
    public static String readHttpCache(String cacheKey) {
        String cacheValue = HTTP_CACHE_CONTENT.getString(cacheKey, null);
        if ("".equals(cacheValue) || "{}".equals(cacheValue)) {
            return null;
        }
        return cacheValue;
    }

    /**
     * 写入缓存
     */
    public static boolean writeHttpCache(String cacheKey, String cacheValue) {
        return HTTP_CACHE_CONTENT.putString(cacheKey, cacheValue).commit();
    }

    /**
     * 清理缓存
     */
    public static void clearCache() {
        HTTP_CACHE_CONTENT.clearMemoryCache();
        HTTP_CACHE_CONTENT.clearAll();

        HTTP_CACHE_TIME.clearMemoryCache();
        HTTP_CACHE_TIME.clearAll();
    }

    /**
     * 获取 Http 写入缓存的时间
     */
    public static long getHttpCacheTime(String cacheKey) {
        return HTTP_CACHE_TIME.getLong(cacheKey, 0);
    }

    /**
     * 设置 Http 写入缓存的时间
     */
    public static boolean setHttpCacheTime(String cacheKey, long cacheTime) {
        return HTTP_CACHE_TIME.putLong(cacheKey, cacheTime).commit();
    }

    /**
     * 判断缓存是否过期
     */
    public static boolean isCacheInvalidate(String cacheKey, long maxCacheTime) {
        if (maxCacheTime == Long.MAX_VALUE) {
            // 表示缓存长期有效，永远不会过期
            return false;
        }
        long httpCacheTime = getHttpCacheTime(cacheKey);
        if (httpCacheTime == 0) {
            // 表示不知道缓存的时间，这里默认当做已经过期了
            return true;
        }
        return httpCacheTime + maxCacheTime < System.currentTimeMillis();
    }
}
