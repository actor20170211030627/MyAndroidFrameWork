package com.actor.myandroidframework.utils.retrofit;

import java.lang.reflect.Field;

import okhttp3.HttpUrl;

/**
 * Description: 动态修改BaseUrl, 示例使用:
 * BaseUrlHelper baseUrlHelper = new BaseUrlHelper(HttpUrl.get("http://www.baseurl.com"));
 * new Retrofit.Builder().baseUrl(baseUrlHelper.getHttpUrl());
 *
 * 修改host:
 * baseUrlHelper.setHost("www.baidu.com");//不要写前缀http(s)://协议
 *
 * Author     : 李大发
 * Date       : 2019/6/7 on 00:29
 */
public class BaseUrlHelper {

    private static final Field hostField;
    private final HttpUrl httpUrl;

    static {
        Field field = null;
        try {
            field = HttpUrl.class.getDeclaredField("host");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        hostField = field;
    }

    /**
     * 初始化
     * @param httpUrl HttpUrl.get("http://www.baseurl.com")
     */
    public BaseUrlHelper(HttpUrl httpUrl) {
        this.httpUrl = httpUrl;
    }

    /**
     * 动态设置host
     * @param host baseUrlHelper.setHost("www.baidu.com");//不要写前缀http(s)://协议
     */
    public void setHost(String host) {
        try {
            hostField.set(httpUrl, host);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("动态设置host,非法存取异常", e);
        }
    }

    /**
     * 获取HttpUrl
     * @return new Retrofit.Builder().baseUrl(baseUrlHelper.getHttpUrl())
     */
    public HttpUrl getHttpUrl() {
        return httpUrl;
    }
}
