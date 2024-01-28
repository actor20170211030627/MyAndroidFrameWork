package com.actor.qq_wechat;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.AppUtils;

/**
 * description: <a href="https://wiki.connect.qq.com/%e5%88%86%e4%ba%ab%e6%b6%88%e6%81%af%e5%88%b0qq%ef%bc%88%e6%97%a0%e9%9c%80qq%e7%99%bb%e5%bd%95%ef%bc%89">（模式5）分享携带ARK JSON串</a> <br />
 * 调用分享接口分享图文消息、纯图片、音乐时可以携带额外ARK（手Q轻应用）参数JSON串，分享将被展示成ARK消息。
 * @author : ldf
 * date       : 2024/1/26 on 12
 * @version 1.0
 */
@Keep
public class ArkJsonBean {

    //ARK应用名称，com.tencent.map
    public String app = AppUtils.getAppPackageName();
    //ARK应用对应展示的视图
    public String view;
    public Object meta;

    /**
     * @param view ARK应用对应展示的视图, 例: RestaurantShare (必填)
     * @param meta ARK应用元数据，view视图对应需要的JSON字符串元数据, 例: 1个对象,最终和其它参数一起序列化成json (必填)
     */
    public ArkJsonBean(@NonNull String view, @NonNull Object meta) {
        this.view = view;
        this.meta = meta;
    }
}
