package com.actor.myandroidframework.utils.umeng;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.umeng.commonsdk.UMConfigure;

/**
 * description:
 * 友盟分享工具类 https://developer.umeng.com/docs/128606/detail/129622
 * 如果要分享到QQ/微信, 需要自己去对应官方申请appkey等, 所以感觉友盟分享没啥用.
 *
 * 1. 使用本类需要添加依赖:
 *   //友盟分享 https://developer.umeng.com/docs/128606/detail/129620
 *   maven { url 'https://dl.bintray.com/umsdk/release' }
 *
 *   //基础组件库和分享核心库
 *   implementation 'com.umeng.umsdk:common:2.2.5'
 *   implementation 'com.umeng.umsdk:share-core:7.0.2'
 *   implementation 'com.umeng.umsdk:share-board:7.0.2'
 *
 *   //分享SDK gradle 在线依赖目前支持如下5个国内主流三方分享平台，其它平台还是需要从【友盟+】官网下载SDK进行离线集成
 *   implementation 'com.umeng.umsdk:share-qq:7.0.2'
 *   implementation 'com.umeng.umsdk:share-wx:7.0.2'
 *   implementation 'com.umeng.umsdk:share-sina:7.0.2'
 *   implementation 'com.umeng.umsdk:share-alipay:7.0.2'
 *   implementation 'com.umeng.umsdk:share-dingding:7.0.2'
 *
 * 2. 权限添加
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <uses-permission android:name="android.permission.INTERNET" />
 * <--如果需要使用QQ纯图分享或避免其它平台纯图分享的时候图片不被压缩，可以增加以下权限：-->
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 *
 * @author : ldf
 * date       : 2020/11/9 on 10
 * @version 1.0
 */
public class UMengShareUtils {

    /**
     * 初始化设置
     * https://developer.umeng.com/docs/128606/detail/129622#h2-u521Du59CBu5316u8BBEu7F6E14
     * 更多了解初始化接口可以参照统计SDK文档初始化及通用接口部分内容。
     * https://developer.umeng.com/docs/119267/detail/118588#h1-u521Du59CBu5316u53CAu901Au7528u63A5u53E31
     *
     * 如果您已经在AndroidManifest.xml中配置过appkey和channel值，可以调用此版本初始化函数
     */
    public static void init() {
        UMConfigure.init(ConfigUtils.APPLICATION, UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(ConfigUtils.IS_APP_DEBUG);
    }
    /**
     * @param appkey 如需要使用AndroidManifest.xml中配置好的appkey和channel值，
     *               UMConfigure.init调用中appkey和channel参数请置为null。
     * @param channel 渠道名称
     */
    public static void init(@Nullable String appkey, @Nullable String channel) {
        //Context context, String appkey, String channel, int deviceType, String pushSecret
        UMConfigure.init(ConfigUtils.APPLICATION, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(ConfigUtils.IS_APP_DEBUG);
    }

    //如果要分享到QQ/微信, 需要自己去对应官方申请appkey等, 所以感觉友盟分享没啥用.
}
