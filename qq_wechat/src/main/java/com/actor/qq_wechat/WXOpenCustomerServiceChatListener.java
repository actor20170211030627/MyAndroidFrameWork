package com.actor.qq_wechat;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;

/**
 * description: APP拉起微信客服功能 回调
 * company    :
 *
 * @author : ldf
 * date       : 2024/1/22 on 11
 * @version 1.0
 */
public interface WXOpenCustomerServiceChatListener {
  /**
   * APP拉起微信客服功能 成功
   * @param resp 成功的内容
   */
   void onOpenCustomerServiceChatSuccess(@NonNull WXOpenCustomerServiceChat.Resp resp);

 /**
  * APP拉起微信客服功能 失败
  * @param baseResp 失败的内容
  */
   void onOpenCustomerServiceChatError(@NonNull BaseResp baseResp);
}
