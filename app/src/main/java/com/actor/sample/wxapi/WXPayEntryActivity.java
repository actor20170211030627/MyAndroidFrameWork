package com.actor.sample.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.actor.myandroidframework.utils.EventBusEvent;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.tencent.WeChatUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Description: 微信支付必须要求返回界面, 这个activity必须放在: 包名/wxapi/目录下
 * 1.在清单文件中注册
 *     <!--微信支付, 如果app没有使用 支付 功能, 请别添加-->
 *     <activity
 *         android:name=".wxapi.WXPayEntryActivity"
 *         android:exported="true"
 *         android:launchMode="singleTop" >
 * <!--        <intent-filter>-->
 * <!--            <action android:name="android.intent.action.VIEW"/>-->
 * <!--            <category android:name="android.intent.category.DEFAULT"/>-->
 * <!--            <data android:scheme="wx8aee7894414e5f5a"/>-->
 * <!--        </intent-filter>-->
 *     </activity>
 *     注意: 已在 MyAndroidFrameWork 的 AndroidManifest.xml中添加, 不要再在项目中添加到清单文件中!!!
 *
 * 2.回调
 * //Eventbus微信登录/支付回调
 * @Subscribe(threadMode = ThreadMode.MAIN)
 * public void onReceivedPayResult(EventBusEvent eventBusEvent) {
 *     if (eventBusEvent == null) return;
 *     switch (eventBusEvent.code) {
 *     case WXPayEntryActivity.MSG_EVT_WX_PAY_RESULT:
 *         toast("支付成功!");
 *         logError(eventBusEvent);
 *         break;
 * }
 *
 * Copyright  : Copyright (c) 2019
 * Author     : 李大发
 * Date       : 2019/4/1 on 10:57
 */
public class WXPayEntryActivity extends ActorBaseActivity implements IWXAPIEventHandler {

    private IWXAPI iwxapi = WeChatUtils.getIWXAPI();
    public static final int MSG_EVT_WX_PAY_RESULT = 56789079;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，
        // 如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，
        // 避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        boolean result =  iwxapi.handleIntent(getIntent(), this);
        if(!result){
            logError("微信登录参数不合法，未被SDK处理，退出");
            onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {//微信发送的请求将回调到onReq方法
        if (baseReq != null) logError(JSONObject.toJSONString(baseReq));
    }

    @Override
    public void onResp(BaseResp baseResp) {//发送到微信请求的响应结果将回调到onResp方法
        String jsonString = JSON.toJSONString(baseResp);
        //transaction 用与唯一标示一个请求null
        LogUtils.error("jsonString: " + jsonString, true);

        switch (baseResp.getType()) {
            case ConstantsAPI.COMMAND_PAY_BY_WX://微信支付
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
//                        toast("支付成功!");
                        finish();
                        EventBus.getDefault().post(new EventBusEvent<>(MSG_EVT_WX_PAY_RESULT));
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        finish();
                        toast("用户取消支付!");
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        finish();
                        toast("用户拒绝支付!");
                        break;
                    default:
                        finish();
                        toast("支付失败, 错误码: " + baseResp.errCode);
                        break;
                }
                break;
            default:
                finish();
                LogUtils.error("baseResp.getType():" + baseResp.getType(), true);
                break;
        }
    }
}
