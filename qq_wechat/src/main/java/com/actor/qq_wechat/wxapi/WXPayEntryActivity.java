package com.actor.qq_wechat.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.qq_wechat.WeChatUtils;
import com.actor.qq_wechat.WxPayListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Description: 微信支付的返回界面 <br />
 * 1.已在框架的清单文件中添加WXPayEntryActivity, 使用者不要再在自己项目中添加本页面到清单文件中!!! <br />
 *   反正用户不需要再关心这个页面! <br />
 *
 * @Author     : ldf
 * @Date       : 2019/4/1 on 10:57
 */
public class WXPayEntryActivity extends ActorBaseActivity implements IWXAPIEventHandler {

    private final IWXAPI iwxapi = WeChatUtils.getIWXAPI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注意：
        //第三方开发者如果使用透明界面来实现WXPayEntryActivity，需要判断handleIntent的返回值，
        // 如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，
        // 避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        boolean result =  iwxapi.handleIntent(getIntent(), this);
        if(!result){
            LogUtils.error("微信登录参数不合法，未被SDK处理，退出");
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq baseReq) {
        if (AppUtils.isAppDebug()) {
            String json = baseReq == null ? "null" : GsonUtils.toJson(baseReq);
            LogUtils.errorFormat("onReq微信发送的请求: baseReq = %s", json);
        }
    }

    //发送到微信请求的响应结果将回调到onResp方法
    @Override
    public void onResp(BaseResp baseResp) {
        if (AppUtils.isAppDebug()) {
            String json = baseResp == null ? "null" : GsonUtils.toJson(baseResp);
            LogUtils.errorFormat("onResp微信响应: baseResp = %s", json);
        }
        finish();
        if (baseResp == null) return;
        //if是微信支付
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            WxPayListener wxPayListener = WeChatUtils.getWxPayListener();
            if (wxPayListener != null) {
                if (baseResp.getType() == BaseResp.ErrCode.ERR_OK) {
                    wxPayListener.onPaySuccess(baseResp);
                } else {
                    wxPayListener.onPayError(baseResp);
                }
            }
        } else {
            LogUtils.errorFormat("微信支付: baseResp.getType() = %d", baseResp.getType());
        }
    }
}
