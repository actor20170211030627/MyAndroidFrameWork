package com.actor.qq_wechat;

import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

/**
 * description: 微信分享回调
 * company    :
 *
 * @author : ldf
 * date       : 2026/8/25 on 11
 * @version 1.0
 */
public interface WxShareListener {

    /**
     * 分享文字、图片、视频Url、链接Url、小程序、音乐、文件 到微信可能成功。(∵用户取消分享也会回调成功)
     * @param shareResp
     */
    void onShareSuccess(SendMessageToWX.Resp shareResp);

    /**
     * 分享到微信失败, 用户可重写此方法, 也可不重写。
     * @param baseResp
     */
    default void onShareError(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_USER_CANCEL:  //-2
                ToasterUtils.warning("用户取消分享!");
                break;
            case BaseResp.ErrCode.ERR_COMM:         //-1 分享失败! 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            case BaseResp.ErrCode.ERR_SENT_FAILED:  //-3 发送失败
            case BaseResp.ErrCode.ERR_AUTH_DENIED:  //-4 验证失败??
            case BaseResp.ErrCode.ERR_UNSUPPORT:    //-5 sdk版本不适配?
            case BaseResp.ErrCode.ERR_BAN:          //-6 app被禁止分享?
            default:
                ToasterUtils.error("分享失败!");
                break;
        }
    }
}
