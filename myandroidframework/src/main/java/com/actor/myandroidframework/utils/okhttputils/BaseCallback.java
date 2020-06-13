package com.actor.myandroidframework.utils.okhttputils;

import android.support.annotation.NonNull;

import com.actor.myandroidframework.dialog.ShowLoadingDialogAble;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 基类, 主要处理错误事件 & 解析成实体类 & 或者泛型里什么都不传,会直接返回Response, 可参考:
 * https://github.com/hongyangAndroid/okhttputils/commit/1d717d1116cc5c81d05e58343e99229d4ccc9f08
 *
 * 在{@link #onBefore(Request, int)} 的时候, 默认会显示LoadingDialog, 可重写此方法.
 * 在{@link #onError(int, Call, Exception)} 的时候, 默认会隐藏LoadingDialog, 可重写此方法.
 * 在{@link #onOk(Object, int)} 方法里, 需要自己调用 "dismissLoadingDialog();" 的方法..
 *
 * Author     : 李大发
 * Date       : 2019/4/17 on 16:03
 * @version 1.2 重写onResponse, 增加okOk
 * @version 1.3 增加onParseNetworkResponseIsNull
 * @version 1.4 把tag等修改成public, 外界可以获取
 * @version 1.4.2 修改一些小细节
 * @version 1.4.3 修改Format错误导致崩溃问题 & 修改取消请求后, onError崩溃问题(增加call.isCanceled()判断)
 * @version 1.4.4 修改错误线程问题, 添加ThreadUtils.runOnUiThread
 * @version 1.4.5
 *              1.实现接口: {@link okhttp3.Callback}
 *              2.重写方法: {@link #onFailure(Call, IOException)}
 *              3.重写方法: {@link #onResponse(Call, Response)}
 *              4.增加构造方法: {@link #BaseCallback(Object, int)}
 */
public abstract class BaseCallback<T> extends Callback<T> implements okhttp3.Callback {

    protected boolean isStatusCodeError = false;//状态码错误
    protected boolean isParseNetworkResponseIsNull = false;//解析成的实体entity=null
    protected boolean isJsonParseException = false;//Json解析异常
    public    Object  tag;
    public    int     id;

    public BaseCallback(Object tag) {
        this.tag = tag;
    }

    public BaseCallback(Object tag, int id) {
        this.tag = tag;
        this.id = id;
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        //开始请求, 默认显示LoadingDialog. 如果不想显示或自定义, 请重写此方法
        if (tag instanceof ShowLoadingDialogAble) {
            ((ShowLoadingDialogAble) tag).showLoadingDialog();
        }
    }

    @Override
    public boolean validateReponse(Response response, int id) {//sub thread
        if (super.validateReponse(response, id)) return true;
        isStatusCodeError = true;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onStatusCodeError(response.code(), response, id);
            }
        });
        return false;//return false:直接走onError(Call call, Exception e, int id)
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {//sub thread
        if (response == null) return null;
        Type genericity = getGenericityType(this);
        if (genericity == Response.class || genericity == Object.class) {
            return (T) response;
        }
        ResponseBody body = response.body();
        if (body == null) return null;
        String json = body.string();
        if (genericity == String.class) {
            return (T) json;
        } else {//解析成: JSONObject & JSONArray & T
            try {
                /**
                 * Gson: 数据类型不对(""解析成int) & 非json类型数据, 都会抛异常
                 * @see com.actor.myandroidframework.utils.IntTypeAdapter
                 */
                return GsonUtils.fromJson(json, genericity);
                //FastJson: 解析非json类型数据会抛异常
//                return JSONObject.parseObject(json, genericity);
            } catch (Exception e) {
                e.printStackTrace();
                isJsonParseException = true;
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onJsonParseException(response, id, e);
                        onError(id, null, e);//主要作用是调用子类的onError方法
                    }
                });
                return null;
            }
        }
    }

    @Override
    public void onResponse(T response, int id) {//main thread
        if (response != null) {
            onOk(response, id);
        } else {
            isParseNetworkResponseIsNull = true;
            if (!isJsonParseException) {//如果不是Json解析错误的原因, 而是其它原因
                onParseNetworkResponseIsNull(response, id);
                onError(id, null, null);//主要作用是调用子类的onError方法
            }
        }
    }

    public abstract void onOk(@NonNull T info, int id);

    //okhttp3.Callback的方法
    @Override
    public final void onFailure(Call call, IOException e) {//sub thread
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onError(call, e, id);
            }
        });
    }
    //okhttp3.Callback的方法
    @Override
    public final void onResponse(Call call, Response response) throws IOException {//sub thread
        if (validateReponse(response, id)) {
            T t = parseNetworkResponse(response, id);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onResponse(t, id);
                }
            });
        } else {
            isStatusCodeError = true;
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onStatusCodeError(response.code(), response, id);
                }
            });
            onFailure(call, new IOException("状态码错误: " + response.code()));
        }
    }
    /**
     * 请求出错
     * 为何是final? 因为:
     * 如果是调用MyOkHttpUtils.cancelTag(tag);主动取消请求并且退出了页面的话,
     * 这个onError方法还是会调用, 如果你在ui层重写了这个onError方法并且做了ui修改, 那么很可能会造成错误!
     * 所以这个方法修饰了final(子类不能重写).
     * 如果要重写, 请重写这个方法: {@link #onError(int, Call, Exception)}
     */
    @Override
    public final void onError(Call call, Exception e, int id) {//连接超时 or 连接失败 or 其它错误
        logFormat("onError: call=%s, e=%s, id=%d", call, e, id);
        if (call == null || call.isCanceled() || e == null) return;
        onError(id, call, e);
    }

    //不能重写上面那个方法, 要重写就重写这个
    public void onError(int id, Call call, Exception e) {
        //请求出错, 默认隐藏LoadingDialog. 如果不想隐藏或自定义, 请重写此方法
        if (tag instanceof ShowLoadingDialogAble) {
            ((ShowLoadingDialogAble) tag).dismissLoadingDialog();
        }
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
     * 状态码错误, 默认会toast, 可以重写本方法
     * @param errCode 错误码
     */
    public void onStatusCodeError(int errCode, Response response, int id) {
        String s = getStringFormat("状态码错误: errCode=%d, response=%s, id=%d", errCode, response, id);
        logError(s);
        toast(s);
    }

    /**
     * 数据解析错误, 默认会toast, 可重写此方法
     */
    public void onJsonParseException(Response response, int id, Exception e) {
        String s = getStringFormat("数据解析错误: response=%s, id=%d, e=%s", response, id, e);
        logError(s);
        toast(s);
    }

    /**
     * 数据解析为空, 默认会toast, 可重写此方法
     */
    public void onParseNetworkResponseIsNull(T response, int id) {
        logFormat("数据解析为空: tag=%s, response=%s, id=%d", tag, response, id);
        toast("数据解析为空,请检查网络连接");
    }

    protected Type getGenericityType(Object object) {
        Type type = object.getClass().getGenericSuperclass();
        return  ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    protected void logError(String msg) {
        LogUtils.error(msg, false);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    protected String getStringFormat(String format, Object... args) {
        return TextUtil.getStringFormat(format, args);
    }

    protected void toast(String msg) {
        ToastUtils.showShort(msg);
    }
}
