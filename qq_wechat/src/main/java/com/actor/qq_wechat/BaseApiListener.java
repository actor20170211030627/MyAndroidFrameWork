package com.actor.qq_wechat;

import com.actor.myandroidframework.utils.LogUtils;
import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

/**
 * Description: 使用requestAsync、request等通用方法调用sdk未封装的接口时，例如上传图片、查看相册等，需传入该回调的实例 <br />
 * Author     : ldf <br />
 * Date       : 2019/3/18 on 15:33
 * @deprecated 好像被废弃了
 */
@Deprecated
public class BaseApiListener implements IRequestListener {

    private static final String TAG = "BaseApiListener";

    @Override
    public void onComplete(JSONObject response) {
        LogUtils.error(TAG + ":onComplete:" +  response.toString());
    }

    @Override
    public void onIOException(IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onMalformedURLException(MalformedURLException e) {
        e.printStackTrace();
    }

    @Override
    public void onJSONException(JSONException e) {
        e.printStackTrace();
    }

    @Override
    public void onConnectTimeoutException(ConnectTimeoutException e) {
        e.printStackTrace();
    }

    @Override
    public void onSocketTimeoutException(SocketTimeoutException e) {
        e.printStackTrace();
    }

    //1.4版本中IRequestListener 新增两个异常
    @Override
    public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {
        //当前网络不可用时触发此异常
        e.printStackTrace();
    }

    @Override
    public void onHttpStatusException(HttpUtils.HttpStatusException e) {
        //http请求返回码非200时触发此异常
        e.printStackTrace();
    }

    @Override
    public void onUnknowException(Exception e) {
        //出现未知错误时会触发此异常
        e.printStackTrace();
    }
}