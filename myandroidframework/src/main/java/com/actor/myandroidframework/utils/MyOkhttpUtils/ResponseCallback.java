package com.actor.myandroidframework.utils.MyOkhttpUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 不解析,返回原生Response<T>, 示例new Response<LoginInfo(解析的类)> {}
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/17 on 15:31
 */
public abstract class ResponseCallback<T> extends BaseCallback<Response> {

    public ResponseCallback(Object tag) {
        super(tag);
    }

    @Override
    public Response parseNetworkResponse(Response response, int id) {
        return response;
    }

    @Override
    public void onResponse(Response response, int id) {
        if (response != null) {
            Type genericity = getClassGenericity(this);
            if (genericity == Response.class || genericity == Object.class) {
                onResponse(response, id, (T) response);
            } else if (genericity == String.class) {
                ResponseBody body = response.body();
                if (body != null) {
                    try {
                        onResponse(response, id, (T) body.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        onResponse(response, id, null);
                    }
                } else onResponse(response, id, null);
            } else {
                ResponseBody body = response.body();
                if (body != null) {
                    String string;
                    try {
                        string = body.string();
                        try {
//                            onResponse(response, id, JSONObject.parseObject(string, genericity));//FastJson
                            onResponse(response, id, GsonUtils.fromJson(string, genericity));//FastJson
                        } catch (Exception e) {
                            e.printStackTrace();
                            onJsonParseException(response, id, e);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        onResponse(response, id, null);
                    }
                } else onResponse(response, id, null);
            }
        } else onResponse(null, id, null);
    }

    //加了final, 子类不要重写这个类
    @Override
    protected final void onOk(@NonNull Response info, int id) {
    }

    protected abstract void onResponse(Response response, int id, @Nullable T info);
}
