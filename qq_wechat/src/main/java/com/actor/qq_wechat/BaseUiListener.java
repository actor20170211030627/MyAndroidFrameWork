package com.actor.qq_wechat;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Description: 调用SDK已经封装好的接口时，例如：登录、快速支付登录、应用分享、应用邀请等接口，需传入该回调的实例 <br />
 * Author     : ldf <br />
 * Date       : 2019/3/18 on 15:26
 */
public abstract class BaseUiListener implements IUiListener {

    private static final String TAG = "BaseUiListener";

    /**
     * @param response 是一个 JSONObject
     */
    @Override
    public void onComplete(Object response) {
        JSONObject jsonResponse = (JSONObject) response;
            doComplete(jsonResponse);
    }

    public abstract void doComplete(@Nullable JSONObject response);

    @Override
    public void onError(UiError e) {
        LogUtils.errorFormat("code=%d, msg=%s, detail=%s", e.errorCode, e.errorMessage, e.errorDetail);
    }
    @Override
    public void onCancel() {
        LogUtils.error(TAG + ":onCancel");
    }
}
