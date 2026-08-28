package com.actor.qq_wechat;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * description: 其他类型
 * company    :
 *
 * @author : ldf
 * date       : 2026/8/25 on 13
 * @version 1.0
 */
public class WxOtherTypeCallback {

    @NonNull
    public BaseResp baseResp;
    public boolean isSuccess;

    public WxOtherTypeCallback(@NonNull BaseResp baseResp, boolean isSuccess) {
        this.baseResp = baseResp;
        this.isSuccess = isSuccess;
    }
}
