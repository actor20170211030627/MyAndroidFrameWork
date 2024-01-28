package com.actor.qq_wechat;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.ToasterUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * description: 微信支付回调
 *
 * @author : ldf
 * date       : 2024/1/9 on 11
 * @version 1.0
 */
public interface WxPayListener {
    /**
     * 支付成功, baseResp.errCode = BaseResp.ErrCode.ERR_OK
     */
    void onPaySuccess(@NonNull BaseResp baseResp);

    /**
     * 支付失败, 用户可重写此方法, 也可不重写。<a href="https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_5_2.shtml#part-6">错误码</a>
     * @param baseResp 失败内容
     */
    default void onPayError(@NonNull BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_USER_CANCEL:  //-2
                ToasterUtils.warning("用户取消支付!");
                break;
            case BaseResp.ErrCode.ERR_COMM:         //-1, 支付失败! 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            case BaseResp.ErrCode.ERR_SENT_FAILED:  //-3
            case BaseResp.ErrCode.ERR_AUTH_DENIED:  //-4, 验证失败??
            case BaseResp.ErrCode.ERR_UNSUPPORT:    //-5
            case BaseResp.ErrCode.ERR_BAN:          //-6
            default:
                ToasterUtils.error("支付失败!");
                break;
        }
    }
}
