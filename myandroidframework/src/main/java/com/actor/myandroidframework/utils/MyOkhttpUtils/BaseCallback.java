package com.actor.myandroidframework.utils.MyOkhttpUtils;

import android.support.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.actor.myandroidframework.utils.ToastUtils;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 基类, 主要处理错误事件 & 解析成实体类 & 或者泛型里什么都不传,会直接返回Response
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/17 on 16:03
 * @version 1.2 重写onResponse, 增加okOk
 * @version 1.3 增加onParseNetworkResponseIsNull
 * @version 1.4 把tag等修改成public, 外界可以获取
 * @version 1.4.2 修改一些小细节
 * @version 1.4.3 修改Format错误导致崩溃问题 & 修改取消请求后, onError崩溃问题(增加call.isCanceled()判断)
 * @version 1.4.4 修改错误线程问题, 添加ThreadUtils.runOnUiThread
 */
public abstract class BaseCallback<T> extends Callback<T> {

    protected boolean isStatusCodeError = false;//状态码错误
    protected boolean isParseNetworkResponseIsNull = false;//解析成的实体entity=null
    protected boolean isJsonParseException = false;//Json解析异常
    public Object tag;

    public BaseCallback(Object tag) {
        this.tag = tag;
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
    }

    @Override
    public boolean validateReponse(Response response, int id) {//sub thread
        if (super.validateReponse(response, id)) return true;
        isStatusCodeError = true;
        ThreadUtils.runOnUiThread(() -> onStatusCodeError(response.code(), response, id));
        return false;//return false:直接走onError(Call call, Exception e, int id)
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {//sub thread
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
                if (body != null) {
                    try {
                        //数据类型不对 & 非json类型数据, 都会抛异常
//                        return GsonUtils.fromJson(body.string(), genericity);//Gson
                        //解析非json类型数据会抛异常
                        return JSONObject.parseObject(body.string(), genericity);//FastJson
                    } catch (Exception e) {
                        e.printStackTrace();
                        isJsonParseException = true;
                        ThreadUtils.runOnUiThread(() -> {
                            onJsonParseException(response, id, e);
                            onError(id, null, e);//主要作用是调用子类的onError方法
                        });

                        return null;
                    }
                } else return null;
            }
        } else return null;
    }

    @Override
    public void onResponse(T response, int id) {//main thread
        if (response != null) {
            onOk(response, id);
        } else {
            isParseNetworkResponseIsNull = true;
            if (!isJsonParseException) {//如果不是Json解析错误的原因, 而是其它原因
                onParseNetworkResponseIsNull(id);
                onError(id, null, null);//主要作用是调用子类的onError方法
            }
        }
    }

    public abstract void onOk(@NonNull T info, int id);

    /**
     * 请求出错
     * 为何是final? 因为:
     * 如果是调用MyOkHttpUtils.cancelTag(tag);主动取消请求并且退出了页面的话,
     * 这个onError方法还是会调用, 如果你在ui层重写了这个onError方法并且做了ui修改, 那么很可能会造成错误!
     * 所以这个方法修饰了final(子类不能重写).
     * 如果要重写, 请重写这个方法: {@link #onError(int, Call, Exception)}
     */
    @Override
    public final void onError(Call call, Exception e, int id) {//连接错误or没网or?
        logFormat("onError: call=%s, e=%s, id=%d", call, e, id);
        if (call == null || call.isCanceled() || e == null) return;
        onError(id, call, e);
    }

    //不能重写上面那个方法, 要重写就重写这个
    public void onError(int id, Call call, Exception e) {
        if (isStatusCodeError || isJsonParseException || isParseNetworkResponseIsNull) return;
        if (e instanceof SocketTimeoutException) {
            toast("连接服务器超时,请联系管理员或稍后重试!");
        } else if (e instanceof ConnectException) {
            toast("网络连接失败,请检查网络是否打开!");
        } else {
            toast("错误信息:".concat(e.getMessage()).concat(",请联系管理员!"));
        }
    }

    /**
     * 状态码错误, 如果要自己处理错误码, 可以重写本方法
     * @param errCode 错误码
     * @param response
     * @param id
     */
    public void onStatusCodeError(int errCode, Response response, int id) {
        logFormat("状态码错误: errCode=%d, response=%s, id=%d", errCode, response, id);
        String s = String.format(Locale.getDefault(), "错误码:%d,请联系管理员!", errCode);
        toast(s);
    }

    public void onJsonParseException(Response response, int id, Exception e) {
        logFormat("数据解析错误: response=%s, id=%d, e=%s", response, id, e);
        ToastUtils.showJsonParseException(e, "数据解析错误,请联系管理员");
    }

    public void onParseNetworkResponseIsNull(int id) {
        logFormat("数据解析为空: tag=%s, id=%d", tag, id);
        toast("数据解析为空,请检查网络连接");
    }

    protected Type getClassGenericity(Object object) {
        Type type = object.getClass().getGenericSuperclass();
        return  ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    protected void logError(String msg) {
        LogUtils.Error(msg, false);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    protected void toast(String msg) {
        ToastUtils.show(msg);
    }
}
