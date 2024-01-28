package com.actor.qq_wechat.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.qq_wechat.WXOpenCustomerServiceChatListener;
import com.actor.qq_wechat.WeChatUtils;
import com.actor.qq_wechat.WxLaunchMiniProgramListener;
import com.actor.qq_wechat.WxLoginListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Description: 微信登录/分享的返回界面 <br />
 * 1.已在框架的清单文件中添加WXEntryActivity, 使用者不要再在自己项目中添加本页面到清单文件中!!! <br />
 *   反正用户不需要再关心这个页面! <br />
 *
 * @author     : ldf
 * @date       : 2019/4/1 on 14:57
 */
public class WXEntryActivity extends ActorBaseActivity implements IWXAPIEventHandler {

    private final IWXAPI iwxapi = WeChatUtils.getIWXAPI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，
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

    @Override
    public void onResp(BaseResp baseResp) {
        if (AppUtils.isAppDebug()) {
            String json = baseResp == null ? "null" : GsonUtils.toJson(baseResp);
            String respClassName = baseResp == null ? "null" : baseResp.getClass().getName();
            LogUtils.errorFormat("onResp微信响应, baseResp实际类型 = %s", respClassName);
            LogUtils.errorFormat("onResp微信响应: baseResp = %s", json);
        }
        finish();
        if (baseResp == null) return;
        switch (baseResp.getType()) {
            case ConstantsAPI.COMMAND_SENDAUTH:             //1, 微信登录
                WxLoginListener wxLoginListener = WeChatUtils.getWxLoginListener();
                if (wxLoginListener == null) return;
                if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
                    if (baseResp instanceof SendAuth.Resp) {
                        SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                        wxLoginListener.onLoginSuccess(resp);
                    } else {
                        wxLoginListener.onLoginError(baseResp);
                    }
                } else {
                    wxLoginListener.onLoginError(baseResp);
                }
                break;
            case ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM://19, 拉起小程序
                WxLaunchMiniProgramListener wxLaunchMiniProgramListener = WeChatUtils.getWxLaunchMiniProgramListener();
                if (wxLaunchMiniProgramListener == null) return;
                if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
                    if (baseResp instanceof WXLaunchMiniProgram.Resp) {
                        WXLaunchMiniProgram.Resp resp = (WXLaunchMiniProgram.Resp) baseResp;
                        wxLaunchMiniProgramListener.onLaunchMiniProgramSuccess(resp);
                    } else {
                        wxLaunchMiniProgramListener.onLaunchMiniProgramError(baseResp);
                    }
                } else {
                    wxLaunchMiniProgramListener.onLaunchMiniProgramError(baseResp);
                }
                break;
            case ConstantsAPI.COMMAND_OPEN_CUSTOMER_SERVICE_CHAT://37, APP拉起微信客服功能
                WXOpenCustomerServiceChatListener wxOpenCustomerServiceChatListener = WeChatUtils.getWxOpenCustomerServiceChatListener();
                if (wxOpenCustomerServiceChatListener == null) return;
                if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
                    if (baseResp instanceof WXOpenCustomerServiceChat.Resp) {
                        WXOpenCustomerServiceChat.Resp resp = (WXOpenCustomerServiceChat.Resp) baseResp;
                        wxOpenCustomerServiceChatListener.onOpenCustomerServiceChatSuccess(resp);
                    } else {
                        wxOpenCustomerServiceChatListener.onOpenCustomerServiceChatError(baseResp);
                    }
                } else {
                    wxOpenCustomerServiceChatListener.onOpenCustomerServiceChatError(baseResp);
                }
                break;
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:    //2, 分享消息/图片/视频(...)到微信
                if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
                    LogUtils.error("分享到微信可能成功。(∵用户取消分享也会回调成功)");
                } else {
                    LogUtils.error("分享到微信失败!");
                }
                break;
            case ConstantsAPI.COMMAND_UNKNOWN:    //0, 未知
            default:
                LogUtils.errorFormat("其余类型, baseResp.getType() = %d", baseResp.getType());
                break;
        }
    }
}
