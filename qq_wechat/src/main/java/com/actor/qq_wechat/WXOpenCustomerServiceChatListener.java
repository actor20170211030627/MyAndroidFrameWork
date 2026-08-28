package com.actor.qq_wechat;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;

/**
 * description: APP拉起客服微信功能 回调
 * company    :
 *
 * @author : ldf
 * date       : 2024/1/22 on 11
 * @version 1.0
 */
public interface WXOpenCustomerServiceChatListener {
  /**
   * APP拉起客服微信功能 成功
   * @param resp 成功的内容
   */
   void onOpenCustomerServiceChatSuccess(@NonNull WXOpenCustomerServiceChat.Resp resp);

 /**
  * APP拉起客服微信功能 失败, 用户可重写此方法, 也可不重写。
  * @param baseResp 失败的内容
  */
   default void onOpenCustomerServiceChatError(@NonNull BaseResp baseResp) {
       switch (baseResp.errCode) {
           case BaseResp.ErrCode.ERR_USER_CANCEL:  //-2 用户取消
               ToasterUtils.error("用户取消打开客服微信!");
               break;
           case BaseResp.ErrCode.ERR_COMM:         //-1 拉起失败! 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
           case BaseResp.ErrCode.ERR_SENT_FAILED:  //-3 发送失败
           case BaseResp.ErrCode.ERR_AUTH_DENIED:  //-4 验证失败??
           case BaseResp.ErrCode.ERR_UNSUPPORT:    //-5
           case BaseResp.ErrCode.ERR_BAN:          //-6
           default:
               ToasterUtils.error("打开客服微信失败!");
               break;
       }
   }
}
