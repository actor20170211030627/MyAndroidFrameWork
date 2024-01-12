package com.actor.qq_wechat;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;

/**
 * description: 微信拉起小程序
 *
 * @author : ldf
 * date       : 2024/1/9 on 11
 * @version 1.0
 */
public interface WxLaunchMiniProgramListener {
    /**
     * 拉起成功, 一定有返回值?, authResp.errCode = BaseResp.ErrCode.ERR_OK <br />
     * String extraData =launchMiniProResp.extMsg;  //对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
     */
    void onLaunchMiniProgramSuccess(@NonNull WXLaunchMiniProgram.Resp launchMiniProResp);

    /**
     * 拉起失败?, 用户可重写此方法, 也可不重写。
     * @param authResp 失败内容
     */
    default void onLaunchMiniProgramError(@NonNull BaseResp authResp) {
        switch (authResp.errCode) {
            case BaseResp.ErrCode.ERR_USER_CANCEL:  //-2, 用户取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:  //-4, 认证被否决
                break;
            case BaseResp.ErrCode.ERR_COMM:         //-1, 拉起失败! 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            case BaseResp.ErrCode.ERR_SENT_FAILED:  //-3
            case BaseResp.ErrCode.ERR_UNSUPPORT:    //-5
            case BaseResp.ErrCode.ERR_BAN:          //-6
            default:
//                ToasterUtils.error("拉起失败!");
                break;
        }
    }
}
