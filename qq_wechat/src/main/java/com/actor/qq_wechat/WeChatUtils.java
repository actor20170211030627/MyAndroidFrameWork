package com.actor.qq_wechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.FileUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory;
import com.tencent.mm.opensdk.diffdev.IDiffDevOAuth;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.SubscribeMiniProgramMsg;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;
import com.tencent.mm.opensdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXGameVideoFileObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.JumpToOfflinePay;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;

/**
 * Description: 微信登录&支付&分享等 <br />
 * <a href="https://developers.weixin.qq.com/doc/oplatform/Downloads/Android_Resource.html#%E7%AD%BE%E5%90%8D%E7%94%9F%E6%88%90%E5%B7%A5%E5%85%B7">签名生成工具</a> <br />
 * <a href="https://developers.weixin.qq.com/doc/oplatform/Downloads/Android_Resource.html" target="_blank">分享, 登录Demo</a> <br />
 * {@link null 支付Demo:} 无!(无语) <br />
 * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html" target="_blank">接入指南</a> <br />
 * <a href="https://open.weixin.qq.com/" target="_blank">应用注册</a> <br />
 *
 * <ol>
 *     <li>
 *         在gradle中<a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html" target="_blank">添加依赖</a>, 在<a href="https://central.sonatype.com/artifact/com.tencent.mm.opensdk/wechat-sdk-android" target="_blank">mavenCentral()</a>中可查看最新依赖版本号 <br />
 *         //微信登录&支付 <br />
 *         implementation 'com.tencent.mm.opensdk:wechat-sdk-android:6.8.24'
 *     </li>
 *     <li>
 *         WXEntryActivity: 微信登录回调页面, 已添加到框架中, 使用者不需要再关心这个类! <br />
 *         WXPayEntryActivity: 微信支付回调页面, 已添加到框架中, 使用者不需要再关心这个类!
 *     </li>
 *     <li>
 *         已经添加<a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html" target="_blank">混淆</a>, 使用者可不再关心混淆问题.
 *     </li>
 *     <li>
 *         在Application中初始化: {@link #setAppId(String) WeChatUtils.setAppId(String)}
 *     </li>
 *     <li>
 *         使用示例 <br />
 *         登录: {@link #login(String, String, WxLoginListener) WeChatUtils.login(String, String, WxLoginListener)} <br />
 *         支付: {@link #pay(String, String, String, String, String, WxPayListener) WeChatUtils.pay(...)} <br />
 *         更多使用示例: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/ThirdActivity.java" target="_blank">ThirdActivity.java</a>
 *     </li>
 * </ol>
 * Author     : ldf <br />
 * date       : 2020/3/14 on 11:46 <br />
 */
public class WeChatUtils {

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    protected static String appId = "wx88888888";

    /**
     * 应用密钥 AppSecret，在微信开放平台提交应用审核通过后获得(不要放在客户端!!!)
     * @deprecated Appsecret 是应用接口使用密钥，泄漏后将可能导致应用数据泄漏、
     * 应用的用户数据泄漏等高风险后果；存储在客户端，极有可能被恶意窃取（如反编译获取Appsecret）；
     */
//    @Deprecated
//    protected static String appSecret = "wx88888888";

    /**
     * 分享图片的时候, 略缩图宽高
     */
    protected static int thumbSize = 150;

    // IWXAPI 是第三方app和微信通信的openApi接口
    protected static IWXAPI api;

    //支付回调
    protected static WxPayListener wxPayListener;

    //登录回调
    protected static WxLoginListener wxLoginListener;

    //拉起小程序回调
    protected static WxLaunchMiniProgramListener wxLaunchMiniProgramListener;

    //APP拉起微信客服功能
    protected static WXOpenCustomerServiceChatListener wxOpenCustomerServiceChatListener;

    /**
     * @param appId 设置appId
     */
    public static void setAppId(@NonNull String appId) {
        if (TextUtils.isEmpty(appId)) return;
        if (!TextUtils.equals(appId, getAppId()) && api != null) unregisterApp();
        WeChatUtils.appId = appId;
    }

    public static String getAppId() {
        return appId;
    }

    /**
     * @param thumbSize 分享 图片/视频/音乐/网页/小程序 的时候, 略缩图宽高
     */
    public static void setThumbSize(int thumbSize) {
        WeChatUtils.thumbSize = thumbSize;
    }

    public static IWXAPI getIWXAPI() {
        if (api == null) {
            //https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
            //注册到微信
            //要使你的程序启动后微信终端能响应你的程序，必须在代码中向微信终端注册你的 id。
            // 通过WXAPIFactory工厂，获取IWXAPI的实例
            api = WXAPIFactory.createWXAPI(ConfigUtils.APPLICATION, getAppId(), true);
            api.registerApp(getAppId());// 将应用的appId注册到微信

            //建议动态监听微信启动广播进行注册到微信
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                api.registerApp(APP_ID);// 将该app注册到微信
//            }
//        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
        }
        return api;
    }

    /**
     * @return 返回微信是否安装
     */
    public static boolean isWXAppInstalled() {
        return getIWXAPI().isWXAppInstalled();
    }

    /**
     * @return 打开微信
     */
    public static boolean openWXApp() {
        return getIWXAPI().openWXApp();
    }

    /**
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
     * @return 检查微信版本支持 API 的情况
     * @see com.tencent.mm.opensdk.constants.Build
     */
    public static int getWXAppSupportAPI() {
        return getIWXAPI().getWXAppSupportAPI();
    }

    /**
     * 从微信反注册
     */
    public static void unregisterApp() {
        getIWXAPI().unregisterApp();
        api = null;
    }


    ///////////////////////////////////////////////////////////////////////////
    // 下面是微信分享
    ///////////////////////////////////////////////////////////////////////////
    /**
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html
     * sendReq 是第三方 app 主动发送消息给微信，发送完成之后会切回到第三方 app 界面。
     * @param text 分享文字, 长度需大于 0 且不超过 10KB
     * @param scene 发送场景
     *      @see  SendMessageToWX.Req#WXSceneSession 消息会发送至微信的会话内
     *      @see  SendMessageToWX.Req#WXSceneTimeline 消息会发送至朋友圈
     *      @see  SendMessageToWX.Req#WXSceneFavorite 收藏
     */
    public static boolean sendReqText(@NonNull String text, int scene) {
        if (TextUtils.isEmpty(text)) return false;
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {//如果发送到朋友圈
            //微信 4.2 以上支持发到朋友圈
            if (getWXAppSupportAPI() < Build.TIMELINE_SUPPORTED_SDK_INT) return false;
        }
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;             //长度需大于 0 且不超过 10KB
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;//媒体对象
        if (text.length() > 1024) {
            //长度不超过 1KB (非常sb且不负责任的官方文档, 直接就使用text, 和上方的10K长度完全对不上...)
            mediaMessage.description = text.substring(0, 1024);
        } else mediaMessage.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //transaction字段用与唯一标示一个请求
        //对应该请求的事务 ID，通常由 Req 发起，回复 Resp 时应填入对应事务 ID
        req.transaction = "text: " + text;
        req.message = mediaMessage;
        req.scene = scene;
        return getIWXAPI().sendReq(req);//调用api接口，发送数据到微信
    }

    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html">分享图片</a>
     * @param imagePath 图片的本地路径, 对应图片内容大小不超过 25MB
     * @param bitmap 图片的bitmap, {@link null 注意: 如果bitmap不为空, 自己调用方法: bitmap.recycle()}
     * @param scene 见↑
     */
    public static boolean sendReqImage(String imagePath, Bitmap bitmap, int scene) {
        if (imagePath == null && bitmap == null) return false;
        Bitmap pathBitmap = null;
        if (bitmap == null) {
            File file = new File(imagePath);
            if (!file.exists()) return false;
            pathBitmap = ImageUtils.getBitmap(file);
            if (pathBitmap == null) return false;
        }
        WXImageObject imgObj;
        Bitmap thumbBitmap;     //略缩图
        if (bitmap == null) {
            imgObj = new WXImageObject();
            imgObj.imagePath = imagePath;   //图片的本地路径, 对应图片内容大小不超过 25MB
            //imgObj.imageData;             //图片的二进制数据, 内容大小不超过 10MB
            thumbBitmap = ImageUtils.scale(pathBitmap, thumbSize, thumbSize, true);
        } else {
            imgObj = new WXImageObject(bitmap);
            thumbBitmap = ImageUtils.scale(bitmap, thumbSize, thumbSize, false);
//            bitmap.recycle();             //请调用者按照自己的需求回收!
        }

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        //设置缩略图
//        msg.setThumbImage(thumbBitmap);   //质量会被压缩到85%, 会被去除透明通道(透明部分变黑!)
        msg.thumbData = ImageUtils.bitmap2Bytes(thumbBitmap);
        thumbBitmap.recycle();

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "img: " + imagePath;
        req.message = msg;
        req.scene = scene;
//        req.userOpenId = getOpenId(); //接收消息的用户的openid。仅在 scene 为 WXSceneSpecifiedContact 时生效。
        return getIWXAPI().sendReq(req);
    }

    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html">分享视频Url</a> <br />
     * {@link WXVideoObject}只能分享网络视频 <br />
     * {@link WXGameVideoFileObject}虽然能分享本地视频, 但只供祂们内部使用... 如果你想分享本地视频, 请使用安卓原生分享api
     * @param videoUrl 视频链接, 限制长度不超过 10KB
     * @param title 视频标题, 可传null
     * @param description 视频描述, 可传null
     * @param coverBitmap 视频封面, 可传null. {@link null 注意: 如果bitmap不为空, 自己调用方法: bitmap.recycle()}
     * @param scene 见↑
     */
    public static boolean sendReqVideo(@NonNull String videoUrl, @Nullable String title, @Nullable String description,
                                    @Nullable Bitmap coverBitmap, int scene) {
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = videoUrl;
//        video.videoLowBandUrl;        //供低带宽的环境下使用的视频链接, 限制长度不超过 10KB
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = title;
        msg.description = description;

        if (coverBitmap != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(coverBitmap, thumbSize, thumbSize, true);
//            coverBitmap.recycle();    //请调用者按照自己的需求回收!
            msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);
            thumbBmp.recycle();
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "video: " + videoUrl;
        req.message = msg;
        req.scene = scene;
//        req.userOpenId = getOpenId(); //接收消息的用户的openid。仅在 scene 为 WXSceneSpecifiedContact 时生效。
        return getIWXAPI().sendReq(req);
    }

    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html">分享网页</a>
     * @param webpageUrl html 链接, 限制长度不超过 10KB
     * @param title 网页标题, 可传null. {@link null 注意: 标题和描述不可同时为空, 否则会分享失败!}
     * @param description 网页描述, 可传null. {@link null 注意: 标题和描述不可同时为空, 否则会分享失败!}
     * @param bitmap 封面图片, 可传null. {@link null 注意: 如果bitmap不为空, 自己调用方法: bitmap.recycle()}
     * @param scene 见↑
     */
    public static boolean sendReqWebpage(@NonNull String webpageUrl, @Nullable String title, @Nullable String description,
                                      @Nullable Bitmap bitmap, int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webpageUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        if (bitmap != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//            bitmap.recycle();     //请调用者按照自己的需求回收!
            msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);
            thumbBmp.recycle();
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage: " + webpageUrl;
        req.message =msg;
        req.scene = scene;
//        req.userOpenId = getOpenId(); //接收消息的用户的openid。仅在 scene 为 WXSceneSpecifiedContact 时生效。
        return getIWXAPI().sendReq(req);
    }

    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html">分享小程序</a> <br />
     * 注意：
     * <ol>
     *     <li>发起分享的 App 与小程序属于同一微信开放平台账号。</li>
     *     <li>或发起分享的 App 与该小程序的代开发(获得小程序权限id为18的授权)服务商的第三方平台账号属于同一微信开放平台账号，也支持发起分享。</li>
     *     <li>支持分享小程序类型消息至会话，暂不支持分享至朋友圈。</li>
     *     <li>若客户端版本低于 6.5.6 或在 iPad 客户端接收，小程序类型分享将自动转成网页类型分享。开发者必须填写网页链接字段，确保低版本客户端能正常打开网页链接。</li>
     * </ol>
     * @param webpageUrl 兼容低版本的网页链接, 限制长度不超过 10KB, 不能传空 (if低版本微信没有小程序功能, 就会变为分享这个网页, 这个网页可以随意填官网 or 一个非空String) <br />
     * @param userName 小程序原始 ID, 获取方法：登录小程序管理后台-设置-基本设置-帐号信息(gh_d43f693ca31f) <br />
     * @param path 小程序页面路径(/pages/index)；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar" <br />
     *             可传null, 会跳转小程序首页. <br />
     * @param withShareTicket 是否使用带 shareTicket 的分享 <br />
     *                        通常开发者希望分享出去的小程序被二次打开时可以获取到更多信息，例如群的标识。
     *                        可以设置 withShareTicket 为 true，当分享卡片在群聊中被其他用户打开时，
     *                        可以获取到 shareTicket，用于获取更多分享信息。详见<a href="https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/share.html">小程序获取更多分享信息</a>，
     *                        最低客户端版本要求：6.5.13 <br />
     * @param miniprogramType 小程序的类型，默认正式版 <br />
     *        &emsp;&emsp;{@link WXMiniProgramObject#MINIPTOGRAM_TYPE_RELEASE WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE} 正式版:0 <br />
     *        &emsp;&emsp;{@link WXMiniProgramObject#MINIPROGRAM_TYPE_TEST WXMiniProgramObject.MINIPROGRAM_TYPE_TEST} 测试版:1 <br />
     *        &emsp;&emsp;{@link WXMiniProgramObject#MINIPROGRAM_TYPE_PREVIEW WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW} 体验版:2 <br />
     * @param title 小程序标题, 可传null
     * @param description 小程序描述, 可传null
     * @param bitmap 小程序封面图片，小于128k, 不能传null. {@link null 注意: 自己调用方法: bitmap.recycle()}
     * @param scene 见↑, {@link null 注意: 目前只支持会话: SendMessageToWX.Req.WXSceneSession}，暂不支持分享至朋友圈。
     */
    public static boolean sendReqMiniProgram(@NonNull String webpageUrl, @NonNull String userName,
                                             @Nullable String path, boolean withShareTicket, int miniprogramType,
                                             @Nullable String title, @Nullable String description,
                                             @NonNull Bitmap bitmap, int scene) {
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.webpageUrl = webpageUrl; // 兼容低版本的网页链接
        miniProgramObj.miniprogramType = miniprogramType;
        miniProgramObj.userName = userName;
        miniProgramObj.path = path;
        miniProgramObj.withShareTicket = withShareTicket;
        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = title;
        msg.description = description;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//        bitmap.recycle();         //请调用者按照自己的需求回收!
        // 小程序消息封面图片，小于128k
        msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);
        thumbBmp.recycle();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "miniProgram: " + path;
        req.message = msg;
        req.scene = scene;  //SendMessageToWX.Req.WXSceneSession 目前只支持会话
//        req.userOpenId = getOpenId(); //接收消息的用户的openid。仅在 scene 为 WXSceneSpecifiedContact 时生效。
        return getIWXAPI().sendReq(req);
    }

    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html">分享音乐</a>
     * @param musicUrl 音频网页的 URL 地址, 限制长度不超过 10KB
     * @param title 音乐标题, 可传null {@link null 注意: 标题和描述不可同时为空, 否则会分享失败!}
     * @param description 音乐描述, 可传null {@link null 注意: 标题和描述不可同时为空, 否则会分享失败!}
     * @param bitmap 音乐封面, 可传null {@link null 注意: 如果bitmap不为空, 自己调用方法: bitmap.recycle()}
     * @param scene 见↑
     */
    public static boolean sendReqMusic(@NonNull String musicUrl, @Nullable String title, @Nullable String description,
                                    @Nullable Bitmap bitmap, int scene) {
        WXMusicObject music = new WXMusicObject();
        //musicUrl 和 musicLowBandUrl 不能同时为空
        music.musicUrl = musicUrl;  //音频网页的 URL 地址, 限制长度不超过 10KB
//        music.musicLowBandUrl;    //供低带宽环境下使用的音频网页 URL 地址, 限制长度不超过 10KB
//        music.musicDataUrl;       //音频数据的 URL 地址, 限制长度不超过 10KB
//        music.musicLowBandDataUrl;//供低带宽环境下使用的音频数据 URL 地址, 限制长度不超过 10KB

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;
        msg.description = description;
        if (bitmap != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//            bitmap.recycle();     //请调用者按照自己的需求回收!
            //设置音乐缩略图
            msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);
            thumbBmp.recycle();
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "music: " + musicUrl;
        req.message = msg;
        req.scene = scene;
//        req.userOpenId = getOpenId(); //接收消息的用户的openid。仅在 scene 为 WXSceneSpecifiedContact 时生效。
        return getIWXAPI().sendReq(req);
    }

    /**
     * 分享文件到微信: <a href="https://developers.weixin.qq.com/community/develop/doc/0004886026c1a8402d2a040ee5b401">OpenSDK支持FileProvider方式分享文件到微信</a> <br />
     * 另外一种弹框分享: {@link FileUtils#shareFile(Context, String) FileUtils.shareFile(Context, String)} <br />
     * {@link null 注意: 现在好像分享不了了, 提示"获取资源失败", 有知道解决方法的请给俺发邮件or提issue}
     * @param filePath 文件路径
     * @return 是否跳转到微信分享界面
     */
    public static boolean shareFile2Wechat(@NonNull Context context, @NonNull String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return false;
        String mimeType = FileUtils.getMimeType(filePath);
        Intent sendIntent = IntentUtils.getShareImageIntent(file);
//        sendIntent.setType("*/*");
        if (mimeType != null) {
            LogUtils.errorFormat("mimeType = %s", mimeType);
            Uri data = sendIntent.getData();
//            sendIntent.setType(mimeType);
            sendIntent.setDataAndType(data, mimeType);
        }
        sendIntent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        try {
            context.startActivity(sendIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html">响应到微信</a> <br />
     * sendResp 是微信向第三方 app 请求数据，第三方 app 回应数据之后会切回到微信界面。
     */
    public static void sendResp(String text, Bundle bundle) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage mediaMessage = new WXMediaMessage(textObj);
        mediaMessage.description = text;

        GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
        // 将req的transaction设置到resp对象中，其中bundle为微信传递过来的Intent所带的内容，通过getExtras()方法获取
        resp.transaction = new GetMessageFromWX.Req(bundle).transaction;
        resp.message = mediaMessage;
        getIWXAPI().sendResp(resp); //调用api接口，发送数据到微信
    }



    ///////////////////////////////////////////////////////////////////////////
    // https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Pay/Vendor_Service_Center.html
    // 下面是微信支付
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_2_4.shtml">APP调起支付API</a>
     * @param partnerId 商户号
     * @param prepayId 预支付交易会话ID
     * @param nonceStr 随机字符串
     * @param timeStamp 时间戳
     * @param sign 服务器调用微信的Sdk生成的签名
     * @param payListener 支付回调
     */
    public static boolean pay(@NonNull String partnerId, @NonNull String prepayId,
                              @NonNull String nonceStr, @NonNull String timeStamp,
                              @NonNull String sign, @NonNull WxPayListener payListener) {
        wxPayListener = payListener;
        PayReq req = new PayReq();
        req.appId = getAppId();         //你的微信appId
        req.partnerId = partnerId;      //商户号
        req.prepayId = prepayId;        //预支付交易会话ID
        req.nonceStr = nonceStr;        //随机字符串
        req.timeStamp = timeStamp;      //时间戳
        req.packageValue = "Sign=WXPay";//扩展字段,这里固定填写Sign=WXPay
        req.sign = sign;                //签名
//        req.signType = ;                //签名类型, V3版本仅支持RSA
        //      req.extData         = "app data"; // optional
        return getIWXAPI().sendReq(req);
    }
    //获取支付监听
    public static WxPayListener getWxPayListener() {
        WxPayListener listener = wxPayListener;
        wxPayListener = null;
        return listener;
    }
    /**
     * 打开离线支付
     */
    public static boolean payOffline() {
        if (getWXAppSupportAPI() >= Build.OFFLINE_PAY_SDK_INT) {
            return getIWXAPI().sendReq(new JumpToOfflinePay.Req());
        }
        return false;//not supported
    }


    ///////////////////////////////////////////////////////////////////////////
    // 下面是微信登录
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html">微信登录, 获取授权临时票据code</a>, 然后把 loginListener.code 传到服务器进行以下2~4步骤 <br />
     * <ol>
     *     <li>(安卓端 调用本方法) 第三方发起微信授权登录请求，微信用户允许授权第三方应用后，微信会拉起应用或重定向到第三方网站，并且带上授权临时票据code参数；</li>
     *     <li> (服务器) 通过code参数加上AppID和AppSecret等，通过API换取access_token；(access_token 有效期（目前为 2 个小时）)</li>
     *     <li>(服务器 刷新 access_token 有效期) access_token 是调用授权关系接口的调用凭证，由于 access_token 有效期（目前为 2 个小时）较短，当 access_token 超时后，可以使用 refresh_token 进行刷新。(refresh_token 拥有较长的有效期（30 天）)</li>
     *     <li>(服务器 获取用户信息) 通过access_token进行接口调用，获取用户基本数据资源或帮助用户实现基本操作。</li>
     * </ol>
     * <img src="https://res.wx.qq.com/op_res/ZLIc-BdWcu_ixroOT0sBEtk0UwpTewqS6ujxbC2QOpbKIVp_DzleM_C9I-9GPDDh" />
     *
     * @param scope 应用授权作用域，如获取用户个人信息则填写snsapi_userinfo, 多个用逗号隔开
     *              "snsapi_userinfo,snsapi_friend,snsapi_message,snsapi_contact"
     * @param state 用于保持请求和回调的状态，授权请求后原样带回给第三方。
     *              该参数可用于防止 csrf 攻击（跨站请求伪造攻击），建议第三方带上该参数，
     *              可设置为简单的随机数加 session 进行校验    (可以瞎填任何值)
     */
    public static void login(@NonNull String scope, @Nullable String state, @NonNull WxLoginListener loginListener) {
        wxLoginListener = loginListener;
        SendAuth.Req req = new SendAuth.Req();
        req.scope = scope;
        req.state = state;
        getIWXAPI().sendReq(req);
    }
    //获取微信登录回调监听
    public static WxLoginListener getWxLoginListener() {
        WxLoginListener listener = wxLoginListener;
        wxLoginListener = null;
        return listener;
    }

    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Login_via_Scan.html">扫码登录</a> <br />
     * 扫码登录能力，指的是开发者可在移动应用内使用此能力，拉取二维码，用户使用微信客户端扫描二维码后可以登录此移动应用。
     * 此能力可被应用在多设备登录、智能硬件、电视盒子等场景。
     * @param scope 应用授权作用域，如获取用户个人信息则填写snsapi_userinfo, 多个用逗号隔开
     *              "snsapi_userinfo,snsapi_friend,snsapi_message,snsapi_contact"
     * @param nonceStr 一个随机的尽量不重复的字符串，用来使得每次的 signature 不同
     * @param timeStamp 时间戳
     * @param signature 签名
     * @param listener 授权流程的回调接口
     */
    @Deprecated
    public static boolean loginQr(@NonNull String scope, @NonNull String nonceStr,
                                  @NonNull String timeStamp, @NonNull String signature, @NonNull OAuthListener listener) {
        IDiffDevOAuth diffDevOAuth = DiffDevOAuthFactory.getDiffDevOAuth();
        return diffDevOAuth.auth(getAppId(), scope, nonceStr, timeStamp, signature, listener
//        new OAuthListener() {
//                    /**
//                     * auth之后返回的二维码接口
//                     * @param qrcodeImgPath 废弃
//                     * @param imgBuf 二维码图片数据
//                     */
//                    @Override
//                    public void onAuthGotQrcode(String qrcodeImgPath, byte[] imgBuf) {
//                    }
//                    /**
//                     * 用户扫描二维码之后，回调该接口
//                     */
//                    @Override
//                    public void onQrcodeScanned() {
//                    }
//                    /**
//                     * 用户点击授权后，回调该接口
//                     */
//                    @Override
//                    public void onAuthFinish(OAuthErrCode errCode, String authCode) {
//                    }
//                }
                );
    }



    ///////////////////////////////////////////////////////////////////////////
    // 下面是订阅消息
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/One-time_subscription_info.html">一次性订阅消息</a>, 抄自 Demo 的 SubscribeMessageActivity.java
     * @param scene 重定向后会带上 scene 参数，开发者可以填 0-10000 的整型值，用来标识订阅场值
     * @param templateID 订阅消息模板 ID，在微信开放平台提交应用审核通过后获得
     * @param reserved 用于保持请求和回调的状态，授权请后原样带回给第三方。
     *                 该参数可用于防止 csrf 攻击（跨站请求伪造攻击），建议第三方带上该参数，
     *                 可设置为简单的随机数加 session 进行校验，开发者可以填写 a-zA-Z0-9 的参数值，
     *                 最多 128 字节，要求做 urlencode
     */
    public static boolean subscribeMessage(int scene, @NonNull String templateID, @Nullable String reserved) {
        //检查微信是否支持订阅消息
        if (getWXAppSupportAPI() >= Build.SUBSCRIBE_MESSAGE_SUPPORTED_SDK_INT) {
            SubscribeMessage.Req req = new SubscribeMessage.Req();
            req.scene = scene;
            req.templateID = templateID;
            req.reserved = reserved;
            return getIWXAPI().sendReq(req);
        }
        return false;
    }

    /**
     * 订阅小程序消息, 抄自 Demo 的 SubscribeMiniProgramMsgActivity.java
     * @param miniProgramAppId 小程序appId
     */
    public static boolean subscribeMiniProgramMsg(@NonNull String miniProgramAppId) {
        //检查微信是否支持订阅消息
        if (getWXAppSupportAPI() >= Build.SUBSCRIBE_MINI_PROGRAM_MSG_SUPPORTED_SDK_INT) {
            SubscribeMiniProgramMsg.Req req = new SubscribeMiniProgramMsg.Req();
            req.miniProgramAppId = miniProgramAppId;
            return getIWXAPI().sendReq(req);
        }
        return false;
    }



    ///////////////////////////////////////////////////////////////////////////
    // https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Launching_a_Mini_Program/Android_Development_example.html
    // APP拉起小程序功能
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 可以在 APP 中跳转至微信某一小程序的指定页面，完成服务后再跳回至原 APP
     * @param userName 小程序原始 ID, 获取方法：登录小程序管理后台-设置-基本设置-帐号信息(gh_d43f693ca31f)
     * @param path 小程序页面路径(/pages/index)；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar" <br />
     *             可传null, 会跳转小程序首页. <br />
     * @param miniprogramType 小程序的类型，默认正式版 <br />
     *        &emsp;&emsp;{@link WXMiniProgramObject#MINIPTOGRAM_TYPE_RELEASE WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE} 正式版:0 <br />
     *        &emsp;&emsp;{@link WXMiniProgramObject#MINIPROGRAM_TYPE_TEST WXMiniProgramObject.MINIPROGRAM_TYPE_TEST} 测试版:1 <br />
     *        &emsp;&emsp;{@link WXMiniProgramObject#MINIPROGRAM_TYPE_PREVIEW WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW} 体验版:2 <br />
     * @param listener 跳转小程序后, 监听从小程序返回的值, 可传null <br />
     *                 小程序跳转回移动应用请参考<a href="https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/launchApp.html">《小程序开发文档》</a>
     */
    public static boolean launchMiniProgram(@NonNull String userName, @Nullable String path, int miniprogramType, @Nullable WxLaunchMiniProgramListener listener) {
        wxLaunchMiniProgramListener = listener;
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = userName;
        req.path = path;
        req.miniprogramType = miniprogramType;
        return getIWXAPI().sendReq(req);
    }
    public static WxLaunchMiniProgramListener getWxLaunchMiniProgramListener() {
        WxLaunchMiniProgramListener listener = wxLaunchMiniProgramListener;
        wxLaunchMiniProgramListener = null;
        return listener;
    }



    ///////////////////////////////////////////////////////////////////////////
    // https://developers.weixin.qq.com/doc/oplatform/Mobile_App/APP_launch_wechat_customer_service.html
    // APP拉起微信客服功能
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 从APP拉起指定的微信客服会话
     * @param corpId 企业ID
     * @param kfUrl 客服URL, 示例: https://work.weixin.qq.com/kfid/kfcxxxxx
     */
    public static boolean openCustomerServiceChat(@NonNull String corpId, @NonNull String kfUrl, @Nullable WXOpenCustomerServiceChatListener listener) {
        wxOpenCustomerServiceChatListener = listener;
        // 判断当前版本是否支持拉起客服会话
        if (getWXAppSupportAPI() >= Build.SUPPORT_OPEN_CUSTOMER_SERVICE_CHAT) {
            WXOpenCustomerServiceChat.Req req = new WXOpenCustomerServiceChat.Req();
            req.corpId = corpId;
            req.url = kfUrl;
            return getIWXAPI().sendReq(req);
        }
        return false;
    }
    public static WXOpenCustomerServiceChatListener getWxOpenCustomerServiceChatListener() {
        WXOpenCustomerServiceChatListener listener = wxOpenCustomerServiceChatListener;
        wxOpenCustomerServiceChatListener = null;
        return listener;
    }



    ///////////////////////////////////////////////////////////////////////////
    // https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Smart_APIs/Image_Recognition_Interface.html
    // 微信智能接口:
    //  图像识别接口开发指南
    //  语音识别接口
    //  语音合成接口开发指南
    //  语义理解上手指南
    ///////////////////////////////////////////////////////////////////////////
}
