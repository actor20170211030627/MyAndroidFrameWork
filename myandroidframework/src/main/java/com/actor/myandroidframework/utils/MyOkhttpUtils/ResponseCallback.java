package com.actor.myandroidframework.utils.MyOkhttpUtils;

import android.support.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 不解析,返回原生Response<T>, 示例new ResponseCallback<LoginInfo> {}
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/17 on 15:31
 * @version 1.1
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
                        String string = body.string();
                        if (string != null) {
                            onResponse(response, id, (T) string);
                        } else {
                            isParseNetworkResponseIsNull = true;
                            onParseNetworkResponseIsNull(id);
                            onError(id, null, null);//主要作用是调用子类的onError方法
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        isParseNetworkResponseIsNull = true;
                        onParseNetworkResponseIsNull(id);
                        onError(id, null, null);//主要作用是调用子类的onError方法
                    }
                } else {
                    isParseNetworkResponseIsNull = true;
                    onParseNetworkResponseIsNull(id);
                    onError(id, null, null);//主要作用是调用子类的onError方法
                }
            } else {
                ResponseBody body = response.body();
                if (body != null) {
                    try {
                        String string = body.string();
//                        T t = JSONObject.parseObject(string, genericity);//FastJson
                        T t = GsonUtils.fromJson(string, genericity);//FastJson
                        if (t != null) {
                            onResponse(response, id, t);
                        } else {
                            isParseNetworkResponseIsNull = true;
                            onParseNetworkResponseIsNull(id);
                            onError(id, null, null);//主要作用是调用子类的onError方法
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isJsonParseException = true;
                        onJsonParseException(response, id, e);
                        onError(id, null, null);//主要作用是调用子类的onError方法
                    }
                } else {
                    isParseNetworkResponseIsNull = true;
                    onParseNetworkResponseIsNull(id);
                    onError(id, null, null);//主要作用是调用子类的onError方法
                }
            }
        } else {
            isParseNetworkResponseIsNull = true;
            onParseNetworkResponseIsNull(id);
            onError(id, null, null);//主要作用是调用子类的onError方法
        }
    }

    //加了final, 子类不要重写这个类
    @Override
    public final void onOk(@NonNull Response info, int id) {
    }

    protected abstract void onResponse(Response response, int id, @NonNull T info);
}
