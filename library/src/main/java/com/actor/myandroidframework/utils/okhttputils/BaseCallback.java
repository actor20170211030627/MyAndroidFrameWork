package com.actor.myandroidframework.utils.okhttputils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.dialog.ShowNetWorkLoadingDialogable;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.actor.myandroidframework.utils.okhttputils.lifecycle.MyOkHttpLifecycleUtils;
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
 * Description: 返回基类
 */
public abstract class BaseCallback<T> extends Callback<T> implements okhttp3.Callback {

    //本次请求LoadingDialog是否show, 默认true
    protected boolean           isShowedLoadingDialog        = true;
    //这次请求是否是(下拉)刷新
    protected boolean           thisRequestIsRefresh         = false;
    //状态码错误
    protected boolean           isStatusCodeError            = false;
    //解析成的实体entity=null
    protected boolean           isParseNetworkResponseIsNull = false;
    //Json解析异常
    protected boolean           isJsonParseException         = false;
    private   int               requestId;
    public    LifecycleOwner    tag;

    public BaseCallback(@Nullable LifecycleOwner tag) {
        this(tag, true);
    }

    public BaseCallback(@Nullable LifecycleOwner tag, boolean isShowLoadingDialog) {
        this(tag, isShowLoadingDialog, false);
    }

    public BaseCallback(@Nullable LifecycleOwner tag, boolean isShowLoadingDialog, boolean isRefresh) {
        this(tag, isShowLoadingDialog, isRefresh, 0);
    }

    /**
     * @param tag 2个作用: <br />
     *            1.1.传入LifecycleOwner, 用于onDestroy的时候的时候取消请求. <br />
     *            1.2.如果是在Dialog/Others..., onDestroy的时候, 需要你自己调用: {@link MyOkHttpUtils#cancelTag(Object)} <br />
     *            2.如果 tag instanceof ShowNetWorkLoadingDialogAble, 可以show/dismiss LoadingDialog. <br /> <br />
     *
     * @param isShowLoadingDialog 是否显示LoadingDialog, 默认true <br /> <br />
     *
     * @param isRefresh 这次请求是否是"下拉刷新 or 上拉加载", 可用于请求列表数据时, 标记这次请求 <br /> <br />
     * @param requestId
     *            1.可传入"List/RecyclerView"的position或item对应的id,
     *              当你在List/RecyclerView中多个item"同时请求"时, 这个requestId可用于区别你这次请求是哪一个item发起的. <br />
     *            2.也可用于需要"同时上传"多个文件, 但每次只能上传一个文件的情况. 传入文件对应的position,
     *              当上传成功后, 就可根据这个requestId判断是上传哪一个文件. <br />
     */
    public BaseCallback(@Nullable LifecycleOwner tag, boolean isShowLoadingDialog, boolean isRefresh, int requestId) {
        this.tag = tag;
        this.isShowedLoadingDialog = isShowLoadingDialog;
        this.thisRequestIsRefresh = isRefresh;
        this.requestId = requestId;
        MyOkHttpLifecycleUtils.addObserver(tag);
    }

    /**
     * 开始请求, 默认显示LoadingDialog. 如果不想显示或自定义, 请重写此方法
     */
    @Override
    public void onBefore(@Nullable Request request, int requestId) {
        super.onBefore(request, requestId);
        if (tag instanceof ShowNetWorkLoadingDialogable) {
            ((ShowNetWorkLoadingDialogable) tag).showNetWorkLoadingDialog();
            isShowedLoadingDialog = true;
        } else {
            isShowedLoadingDialog = false;
        }
    }

    //sub thread
    @Override
    public boolean validateReponse(Response response, int id) {
        if (super.validateReponse(response, id)) return true;
        isStatusCodeError = true;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onStatusCodeError(response.code(), response, id);
            }
        });
        /**
         * return false:直接走: {@link #onError(Call call, Exception e, int id)}
         */
        return false;
    }

    //sub thread
    @Nullable
    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        if (response == null) return null;
        Type genericity = getGenericityType(this);
        if (genericity == Response.class || genericity == Object.class) {
            return (T) response;
        }
        ResponseBody body = response.body();
        if (body == null) return null;
        String json = body.string();
        try {
            return json2Entity(json, genericity);
        } catch (Exception e) {
            e.printStackTrace();
            isJsonParseException = true;
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onJsonParseException(response, id, e);
                    //主要作用是调用子类的onError方法
                    onError(id, null, e);
                }
            });
            return null;
        } finally {
            body.close();
        }
    }

    /**
     * json 解析成对象
     * @param json json
     * @param entityType 对象类型, 例: LoginBean.class
     * @return
     */
    @Nullable
    protected T json2Entity(@Nullable String json, Type entityType) throws Exception {
        if (json == null || entityType == String.class) {
            return (T) json;
        }
        //解析成: JSONObject & JSONArray & T
        /**
         * Gson: 数据类型不对(""解析成int) & 非json类型数据, 默认都会抛异常, 解决方法↓
         * @see com.actor.myandroidframework.utils.gson.IntJsonDeserializer
         */
        return GsonUtils.fromJson(json, entityType);
        //FastJson: bug太多也不修复一下, 删掉...
//          return JSONObject.parseObject(json, genericity);
    }

    //main thread
    @Override
    public void onResponse(@Nullable T response, int id) {
        if (response != null) {
            if (isShowedLoadingDialog && tag instanceof ShowNetWorkLoadingDialogable) {
                ((ShowNetWorkLoadingDialogable) tag).dismissNetWorkLoadingDialog();
            }
            onOk(response, id, thisRequestIsRefresh);
        } else {
            isParseNetworkResponseIsNull = true;
            //如果不是Json解析错误的原因, 而是其它原因
            if (!isJsonParseException) {
                onParseNetworkResponseIsNull(id);
                //主要作用是调用子类的onError方法
                onError(id, null, null);
            }
        }
    }

    /**
     * 请求成功回调
     * @param info json解析成的对象
     * @param requestId 本次回调标记, 见构造方法: {@link BaseCallback(Object, int)}
     * @param isRefresh 这次请求是否是(下拉)刷新, 需要在构造方法中传入: {@link BaseCallback(Object, boolean)}
     */
    public abstract void onOk(@NonNull T info, int requestId, boolean isRefresh);

    /**
     * 上传/下载进度
     * @param progress 进度[0, 1]
     * @param total 总大小
     * @param id 请求时传入的id, 默认0
     */
    @Override
    public void inProgress(float progress, long total, int id) {
//        LogUtils.errorFormat("上传/下载进度: progress=%f, total=%d, id=%d", progress, total, id);
    }

    //okhttp3.Callback的方法, sub thread
    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onError(call, e, requestId);
            }
        });
    }

    //okhttp3.Callback的方法, sub thread
    @Override
    public final void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (validateReponse(response, requestId)) {
            T t = parseNetworkResponse(response, requestId);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onResponse(t, requestId);
                }
            });
        } else {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onStatusCodeError(response.code(), response, requestId);
                }
            });
            onFailure(call, new IOException("状态码错误: " + response.code()));
        }
    }

    /**
     * 连接超时 or 连接失败 or 其它错误
     * 为何是final? 因为:
     * 如果是调用MyOkHttpUtils.cancelTag(tag);主动取消请求并且退出了页面的话,
     * 这个onError方法还是会调用, 如果你在ui层重写了这个onError方法并且做了ui修改, 那么很可能会造成错误!
     * 所以这个方法修饰了final(子类不能重写).
     * 如果要重写, 请重写这个方法: {@link #onError(int, Call, Exception)}
     */
    @Override
    public final void onError(Call call, Exception e, int id) {
        LogUtils.errorFormat("onError: call=%s, e=%s, id=%d", call, e, id);
        if (call == null || call.isCanceled() || e == null) return;
        onError(id, call, e);
    }

    //不能重写上面那个方法, 要重写就重写这个
    public void onError(int id, Call call, Exception e) {
        //请求出错, 默认隐藏LoadingDialog. 如果不想隐藏或自定义, 请重写此方法
        if (isShowedLoadingDialog && tag instanceof ShowNetWorkLoadingDialogable) {
            ((ShowNetWorkLoadingDialogable) tag).dismissNetWorkLoadingDialog();
        }
        if (isStatusCodeError || isJsonParseException || isParseNetworkResponseIsNull) return;
        if (e instanceof SocketTimeoutException) {
            ToastUtils.showShort("连接服务器超时,请联系管理员或稍后再试!");
        } else if (e instanceof ConnectException) {
            ToastUtils.showShort("网络连接失败,请检查网络是否打开!");
        } else if (e != null) {
            String message = e.getMessage();
            if (message == null) message = "";
            ToastUtils.showShort("错误信息:".concat(message).concat(",请联系管理员!"));
        }
    }

    /**
     * 状态码错误, 默认会toast, 可以重写本方法
     *
     * @param errCode 错误码
     */
    public void onStatusCodeError(int errCode, Response response, int requestId) {
        String s = TextUtils2.getStringFormat("状态码错误: errCode=%d, response=%s, requestId=%d", errCode, response, requestId);
        LogUtils.error(s);
        ToastUtils.showShort(TextUtils2.getStringFormat("状态码错误: %d", errCode));
    }

    /**
     * 数据解析错误, 默认会toast, 可重写此方法
     */
    public void onJsonParseException(Response response, int requestId, Exception e) {
        String s = TextUtils2.getStringFormat("数据解析错误: response=%s, requestId=%d, e=%s", response, requestId, e);
        LogUtils.error(s);
        ToastUtils.showShort("数据解析错误");
    }

    /**
     * 数据解析为空, 默认会toast, 可重写此方法
     */
    public void onParseNetworkResponseIsNull(int requestId) {
        LogUtils.errorFormat("数据解析为空: tag=%s, requestId=%d", tag, requestId);
        ToastUtils.showShort("数据解析为空");
    }

    protected Type getGenericityType(Object object) {
        Type type = object.getClass().getGenericSuperclass();
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    public int getRequestId() {
        return requestId;
    }
}
