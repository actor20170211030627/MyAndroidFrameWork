package com.actor.qq_wechat;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.toaster.ToasterUtils;
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
     * 拉起成功 <br />
     * <code>
     *     //对应小程序组件 &lt;button open-type="launchApp"> 中的 app-parameter 属性, 只有小程序主动调用 API 回传给 App 的时候才有值 <br />
     *     String extraData =launchMiniProResp.extMsg;
     * </code>
     */
    void onLaunchMiniProgramSuccess(@NonNull WXLaunchMiniProgram.Resp launchMiniProResp);

    /**
     * 拉起失败?, 用户可重写此方法, 也可不重写。
     * @param baseResp 失败内容
     */
    default void onLaunchMiniProgramError(@NonNull BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_USER_CANCEL:  //-2 用户取消
                ToasterUtils.error("用户取消打开小程序!");
                break;
            case BaseResp.ErrCode.ERR_COMM:         //-1 拉起失败! 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            case BaseResp.ErrCode.ERR_SENT_FAILED:  //-3 发送失败
            case BaseResp.ErrCode.ERR_AUTH_DENIED:  //-4 验证失败??
            case BaseResp.ErrCode.ERR_UNSUPPORT:    //-5
            case BaseResp.ErrCode.ERR_BAN:          //-6
            default:
                ToasterUtils.error("打开小程序失败!");
                break;
        }
    }
}
