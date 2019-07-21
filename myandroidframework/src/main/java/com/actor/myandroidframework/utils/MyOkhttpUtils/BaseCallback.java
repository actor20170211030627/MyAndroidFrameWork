package com.actor.myandroidframework.utils.MyOkhttpUtils;

import android.support.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ToastUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 基类, 主要处理错误事件 & 解析成实体类 & 或者泛型里什么都不传,会直接返回Response
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/17 on 16:03
 * @version 1.2 重写onResponse, 增加okOk
 * @version 1.3 增加onParseNetworkResponseIsNull
 * @version 1.4 把tag等修改成public, 外界可以获取
 * @version 1.4.1 修改一些小细节
 */
public abstract class BaseCallback<T> extends Callback<T> {

    protected boolean isStatusCodeError = false;
    public Object tag;

    public BaseCallback(Object tag) {
        this.tag = tag;
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
    }

    @Override
    public boolean validateReponse(Response response, int id) {
        if (super.validateReponse(response, id)) return true;
        onStatusCodeError(response.code(), response, id);
        return false;//return false:直接走onError()
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        if (response != null) {
            Type genericity = getClassGenericity(this);
            if (genericity == Response.class || genericity == Object.class) {
                return (T) response;
            } else if (genericity == String.class) {
                ResponseBody body = response.body();
                if (body != null) {
                    return (T) body.string();
                } else return null;
            } else {
                ResponseBody body = response.body();
                if (body != null) {//如果数据量比较大,重写本方法,自己子线程解析
                    try {//解析非json类型数据会抛异常
                        return GsonUtils.fromJson(body.string(), genericity);//Gson
//                        return JSONObject.parseObject(body.string(), genericity);//FastJson
                    } catch (Exception e) {
                        e.printStackTrace();
                        onJsonParseException(response, id, e);
                        return null;
                    }
                } else return null;
            }
        } else return null;
    }

    @Override
    public void onResponse(T response, int id) {
        if (response != null) {
            onOk(response, id);
        } else onParseNetworkResponseIsNull(id);
    }

    protected abstract void onOk(@NonNull T info, int id);

    @Override
    public void onError(Call call, Exception e, int id) {//连接错误or没网or?
        logFormat("onError: call=%s, e=%s, id=%d", call, e, id);
        if (e == null || isStatusCodeError) return;
        e.printStackTrace();
        if (e.getClass() == SocketTimeoutException.class) {
            toast("连接服务器超时,请联系管理员或稍后重试!");
        } else if (e.getClass() == ConnectException.class) {
            toast("网络连接失败,请检查网络是否打开!");
        } else if (e.getClass() == SocketException.class) {
            //java.net.SocketException: Socket closed, 调用 MyOkHttpUtils.cancelTag(this); 后会出现
        } else if (e.getClass() == IOException.class) {
            //java.io.IOException: Canceled, 调用 MyOkHttpUtils.cancelTag(this); 后会出现
        } else {
            //e.getMessage()示例:failed to connect to /111.11.111.111 (port 8080) after 10000ms
            toast("错误信息:".concat(e.getMessage()).concat(",请联系管理员!"));
        }//
    }

    /**
     * 状态码错误
     * @param errCode
     * @param response
     * @param id
     */
    protected void onStatusCodeError(int errCode, Response response, int id) {
        logFormat("状态码错误: errCode=%d, response=%s, id=%d", errCode, response, id);
        isStatusCodeError = true;
        if (errCode == 401) {//我们现在项目规定401是过期, 具体项目视情况而定
            toast("登录过期, 请重新登录!");
        } else {
            String s = String.format(Locale.getDefault(), "错误码:%d,请联系管理员!", errCode);
            toast(s);
        }
    }

    protected void onJsonParseException(Response response, int id, Exception e) {
        logFormat("数据解析错误: response=%s, id=%d, e=%s", response, id, e);
        ToastUtils.showJsonParseException(e, "数据解析错误,请联系管理员");
    }

    protected void onParseNetworkResponseIsNull(int id) {
        logFormat("数据解析为空: tag=%s, id=%d", tag, id);
        toast("数据解析为空,请检查网络连接");
    }

    protected Type getClassGenericity(Object object) {
        Type type = object.getClass().getGenericSuperclass();
        return  ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    protected static void logError(String msg) {
        LogUtils.Error(msg, false);
    }

    protected static void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    protected void toast(String msg) {
        ToastUtils.show(msg);
    }
}
