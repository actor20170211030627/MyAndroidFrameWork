package com.actor.qq_wechat;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.ToasterUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

/**
 * description: 微信登录回调
 *
 * @author : ldf
 * date       : 2024/1/9 on 11
 * @version 1.0
 */
public interface WxLoginListener {
    /**
     * 登录成功, authResp.errCode = BaseResp.ErrCode.ERR_OK <br />
     * <ol>
     *     <li>
     *         //获取的授权临时票据code, 需要把code传到服务器, 从服务器获取userinfo等... <br />
     *         String code = authResp.code; <br />
     *     </li>
     *     <li>
     *         //{@link WeChatUtils#login(String, String, WxLoginListener)} 登录的时候传入的唯一性的标志，state字符串长度不能超过1K <br />
     *         String state = authResp.state;
     *     </li>
     *     <li>String lang = authResp.lang; //微信客户端当前语言</li>
     *     <li>String country = authResp.country;   //微信用户当前国家信息</li>
     *     <li>还有其余参数...</li>
     * </ol>
     */
    void onLoginSuccess(@NonNull SendAuth.Resp authResp);

    /**
     * 登录失败, 用户可重写此方法, 也可不重写。<a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html#%E7%AC%AC%E4%B8%80%E6%AD%A5%EF%BC%9A%E8%AF%B7%E6%B1%82-CODE">错误码</a>
     * @param authResp 失败内容
     */
    default void onLoginError(@NonNull BaseResp authResp) {
        switch (authResp.errCode) {
            case BaseResp.ErrCode.ERR_USER_CANCEL:  //-2
                ToasterUtils.warning("用户取消登录!");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:  //-4
                ToasterUtils.warning("用户拒绝授权!");
                break;
            case BaseResp.ErrCode.ERR_COMM:         //-1, 登录失败! 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            case BaseResp.ErrCode.ERR_SENT_FAILED:  //-3
            case BaseResp.ErrCode.ERR_UNSUPPORT:    //-5
            case BaseResp.ErrCode.ERR_BAN:          //-6
            default:
                ToasterUtils.error("登录失败!");
                break;
        }
    }
}
