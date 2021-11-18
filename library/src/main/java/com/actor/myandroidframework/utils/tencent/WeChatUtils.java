package com.actor.myandroidframework.utils.tencent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.SubscribeMiniProgramMsg;
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

import java.util.concurrent.ExecutionException;

/**
 * Description: 微信登录等
 * 集成/Demo下载: https://developers.weixin.qq.com/doc/oplatform/Downloads/Android_Resource.html
 * 接入指南: https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
 * 应用注册: https://open.weixin.qq.com/
 *
 * 1.在gradle中添加依赖
 *      //https://bintray.com/wechat-sdk-team/maven 微信登录支付,不包含统计功能
 *      implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.5.8'
 *
 * 2.添加相关Activity, 必须放在: 包名.wxapi文件夹下, 可参考:
 *   //登录等功能(如果app没有使用 登录等 这些功能, 请忽略)
 *   https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/wxapi/WXEntryActivity.java
 *   //支付功能(如果app没有使用 支付 等这些功能, 请忽略)
 *   https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/wxapi/WXPayEntryActivity.java
 *
 * 3.在 AndroidManifest.xml中添加以下2个Activity:
 * <!--微信登录等-->
 * <!-- https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html -->
 * <activity
 *     android:name=".wxapi.WXEntryActivity"
 *     android:label="@string/app_name"
 *     android:theme="@android:style/Theme.Translucent.NoTitleBar"
 *     android:exported="true"
 *     android:launchMode="singleTask"
 *     android:taskAffinity="${applicationId}" />
 * <!--微信支付-->
 * <!-- https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5 -->
 * <activity
 *     android:name=".wxapi.WXPayEntryActivity"
 *     android:exported="true"
 *     android:launchMode="singleTop" >
 * </activity>
 *
 * 4.在Application中初始化: {@link #setAppId(String)}
 *
 * 5.登录: {@link #login(String, String)}
 *
 * 6.示例使用:
 * https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/ThirdActivity.java
 *
 * @author    : ldf
 * date       : 2020/3/14 on 11:46
 * @version 1.0
 */
public class WeChatUtils {

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    protected static String appId = "wx88888888";

    /**
     * 应用密钥 AppSecret，在微信开放平台提交应用审核通过后获得(不要放在客户端!!!)
     * @deprecated Appsecret 是应用接口使用密钥，泄漏后将可能导致应用数据泄漏、
     * 应用的用户数据泄漏等高风险后果；存储在客户端，极有可能被恶意窃取（如反编译获取Appsecret）；
     */
    @Deprecated
    protected static String appSecret = "wx88888888";

    //分享图片的时候, 略缩图宽高
    protected static int thumbSize = 150;

    // IWXAPI 是第三方app和微信通信的openApi接口
    protected static IWXAPI api;

    /**
     * @param appId 设置appid
     */
    public static void setAppId(String appId) {
        WeChatUtils.appId = appId;
    }

    public static String getAppId() {
        return appId;
    }

    /**
     * @param thumbSize 分享图片的时候, 略缩图宽高
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
    }


    ///////////////////////////////////////////////////////////////////////////
    // 下面是微信分享
    ///////////////////////////////////////////////////////////////////////////
    /**
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html
     * sendReq 是第三方 app 主动发送消息给微信，发送完成之后会切回到第三方 app 界面。
     * @param text 分享文字
     * @param scene 发送场景
     *      @see  SendMessageToWX.Req#WXSceneSession 消息会发送至微信的会话内
     *      @see  SendMessageToWX.Req#WXSceneTimeline 消息会发送至朋友圈
     *      @see  SendMessageToWX.Req#WXSceneFavorite 收藏
     */
    public static void sendReqText(String text, int scene) {
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {//如果发送到朋友圈
            //微信 4.2 以上支持发到朋友圈
            if (getWXAppSupportAPI() < Build.TIMELINE_SUPPORTED_SDK_INT) return;
        }
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;//媒体对象
        mediaMessage.description = text;//长度不超过 1KB 512Bytes

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //transaction字段用与唯一标示一个请求
        //对应该请求的事务 ID，通常由 Req 发起，回复 Resp 时应填入对应事务 ID
        req.transaction = "text" + System.currentTimeMillis();
        req.message = mediaMessage;
        req.scene = scene;
        getIWXAPI().sendReq(req);//调用api接口，发送数据到微信
    }

    /**
     * 分享图片, 注意如果Bitmap不为空, 自己调用方法: {@link Bitmap#recycle()}
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html
     * @param imagePath 图片的本地路径, 对应图片内容大小不超过 10MB
     * @param scene 见↑
     */
    public static void sendReqImage(String imagePath, Bitmap bitmap, int scene) {
        if (imagePath == null && bitmap == null) return;
        WXImageObject imgObj;
        final Bitmap[] thumbBmp = {null};
        if (imagePath != null) {
            imgObj = new WXImageObject();
            //imgObj.imageData;//图片的二进制数据, 内容大小不超过 10MB
            getThumbByGlide(imagePath, false, new GetThumbByGlideListener() {
                @Override
                public void onGetThumbByGlide(Bitmap thumb) {
                    thumbBmp[0] = thumb;
                }
            });

            imgObj.imagePath = imagePath;//图片的本地路径, 图片内容大小不超过 10MB
        } else {
            imgObj = new WXImageObject(bitmap);
            thumbBmp[0] = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//            bitmap.recycle();
        }
        if (thumbBmp[0] == null) return;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        //设置缩略图
        msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp[0]);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "img" + System.currentTimeMillis();
        req.message = msg;
        req.scene = scene;
        //官网有这句, Demo没这句...
//        req.userOpenId = getOpenId();
        getIWXAPI().sendReq(req);
    }

    /**
     * 分享音乐, 注意自己调用方法: {@link Bitmap#recycle()}
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html
     * @param musicUrl 音频网页的 URL 地址, 限制长度不超过 10KB
     * @param title 音乐标题
     * @param description 音乐描述
     * @param bitmap 音乐封面?
     * @param scene 见↑
     */
    public static void sendReqMusic(String musicUrl, String title, String description,
                                    Bitmap bitmap, int scene) {
        WXMusicObject music = new WXMusicObject();
        //musicUrl 和 musicLowBandUrl 不能同时为空
        music.musicUrl = musicUrl;//音频网页的 URL 地址, 限制长度不超过 10KB
//        music.musicLowBandUrl;//供低带宽环境下使用的音频网页 URL 地址, 限制长度不超过 10KB
//        music.musicDataUrl;//音频数据的 URL 地址, 限制长度不超过 10KB
//        music.musicLowBandDataUrl;//供低带宽环境下使用的音频数据 URL 地址, 限制长度不超过 10KB

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;
        msg.description = description;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//        bitmap.recycle();
        //设置音乐缩略图
        msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "music" + System.currentTimeMillis();
        req.message = msg;
        req.scene = scene;
        //官网有这句, Demo没这句...
//        req.userOpenId = getOpenId();
        getIWXAPI().sendReq(req);
    }

    /**
     * 分享视频, 注意自己调用方法: {@link Bitmap#recycle()}
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html
     * @param videoUrl 视频链接, 限制长度不超过 10KB
     * @param title 视频标题
     * @param description 视频描述
     * @param scene 见↑
     */
    public static void sendReqVideo(String videoUrl, String title, String description,
                                    Bitmap bitmap, int scene) {
        WXVideoObject video = new WXVideoObject();
        //videoUrl 和 videoLowBandUrl 不能同时为空
        video.videoUrl = videoUrl;
//        video.videoLowBandUrl;//供低带宽的环境下使用的视频链接, 限制长度不超过 10KB
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = title;
        msg.description = description;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//        bitmap.recycle();
        msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "video" + System.currentTimeMillis();
        req.message =msg;
        req.scene = scene;
        //官网有这句, Demo没这句...
//        req.userOpenId = getOpenId();
        getIWXAPI().sendReq(req);
    }

    /**
     * 分享视频, 注意如果Bitmap不为空, 自己调用方法: {@link Bitmap#recycle()}
     * 抄自Demo
     * @param filePath 本地视频地址
     * @param title 视频标题
     * @param description 视频描述
     * @param bitmap 视频封面, 可传null
     * @param scene 见↑
     */
    public static void sendReqVideoLocal(String filePath, String title, String description,
                                         @Nullable Bitmap bitmap, int scene) {
        WXGameVideoFileObject gameVideoFileObject = new WXGameVideoFileObject();
        gameVideoFileObject.filePath = filePath;

        WXMediaMessage msg = new WXMediaMessage();
        final Bitmap[] thumbBmp = {null};
        if (bitmap != null) {
            thumbBmp[0] = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//        bitmap.recycle();
        } else {
            getThumbByGlide(filePath, true, new GetThumbByGlideListener() {
                @Override
                public void onGetThumbByGlide(Bitmap thumb) {
                    thumbBmp[0] = thumb;
                }
            });
        }
        if (thumbBmp[0] == null) return;
        msg.setThumbImage(thumbBmp[0]);
        msg.title = title;
        msg.description = description;
        msg.mediaObject = gameVideoFileObject;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "appdata" + System.currentTimeMillis();
        req.message = msg;
        req.scene = scene;
        getIWXAPI().sendReq(req);
    }

    /**
     * 分享网页, 注意自己调用方法: {@link Bitmap#recycle()}
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html
     * @param webpageUrl html 链接, 限制长度不超过 10KB
     * @param title 网页标题
     * @param description 网页描述
     * @param bitmap 分享链接右侧的图片, 注意: 需要自己回收bitmap.recycle();
     * @param scene 见↑
     */
    public static void sendReqWebpage(String webpageUrl, String title, String description,
                                      Bitmap bitmap, int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webpageUrl;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
//        bitmap.recycle();
        msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage" + System.currentTimeMillis();
        req.message =msg;
        req.scene = scene;
        //官网有这句, Demo没这句...
//        req.userOpenId = getOpenId();
        getIWXAPI().sendReq(req);
    }

    /**
     * 分享小程序, 注意自己调用方法: {@link Bitmap#recycle()}
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html
     * 注意：
     * 发起分享的 App 与小程序属于同一微信开放平台帐号。 支持分享小程序类型消息至会话，暂不支持分享至朋友圈。
     * 若客户端版本低于 6.5.6 或在 iPad 客户端接收，小程序类型分享将自动转成网页类型分享。
     * 开发者必须填写网页链接字段，确保低版本客户端能正常打开网页链接。
     *
     * @param webpageUrl 兼容低版本的网页链接, 限制长度不超过 10KB
     * @param userName 小程序原始 ID, 获取方法：登录小程序管理后台-设置-基本设置-帐号信息(gh_d43f693ca31f)
     * @param path 小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"(/pages/media)
     * @param withShareTicket 是否使用带 shareTicket 的分享
     *                        通常开发者希望分享出去的小程序被二次打开时可以获取到更多信息，例如群的标识。
     *                        可以设置 withShareTicket 为 true，当分享卡片在群聊中被其他用户打开时，
     *                        可以获取到 shareTicket，用于获取更多分享信息。详见小程序获取更多分享信息，
     *                        最低客户端版本要求：6.5.13
     * @param miniprogramType 小程序的类型，默认正式版
     *          @see WXMiniProgramObject#MINIPTOGRAM_TYPE_RELEASE 正式版:0
     *          @see WXMiniProgramObject#MINIPROGRAM_TYPE_TEST 测试版:1
     *          @see WXMiniProgramObject#MINIPROGRAM_TYPE_PREVIEW 体验版:2
     * @param title 小程序消息Title
     * @param description 小程序消息Desc
     * @param bitmap 小程序消息封面图片
     * @param scene 见↑, 但是 目前只支持会话: SendMessageToWX.Req.WXSceneSession
     */
    public static void sendReqMiniProgram(String webpageUrl, String userName, String path,
                                          boolean withShareTicket, int miniprogramType,
                                          String title, String description, Bitmap bitmap, int scene) {
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
//        bitmap.recycle();
        // 小程序消息封面图片，小于128k
        msg.thumbData = ImageUtils.bitmap2Bytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "miniProgram" + System.currentTimeMillis();
        req.message = msg;
        req.scene = scene;  //SendMessageToWX.Req.WXSceneSession 目前只支持会话
        getIWXAPI().sendReq(req);
    }

    /**
     * 分享文件到微信, 调用系统Intent分享
     * @param filePath 文件路径
     * @return 是否跳转到微信分享界面
     */
    public static boolean share2Wechat(Context context, String filePath) {
        Intent sendIntent = IntentUtils.getShareImageIntent(filePath);
        sendIntent.setType("*/*");
        sendIntent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        try {
            context.startActivity(sendIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtils.showShort("未安装微信");
            return false;
        }
    }

    /**
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
     * sendResp 是微信向第三方 app 请求数据，第三方 app 回应数据之后会切回到微信界面。
     */
    public static void sendResp(String text, Bundle bundle) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage mediaMessage = new WXMediaMessage(textObj);
        mediaMessage.description = text;

        GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
        // 将req的transaction设置到resp对象中，
        // 其中bundle为微信传递过来的Intent所带的内容，通过getExtras()方法获取
        resp.transaction = new GetMessageFromWX.Req(bundle).transaction;
        resp.message = mediaMessage;
        getIWXAPI().sendResp(resp);//调用api接口，发送数据到微信
    }


    ///////////////////////////////////////////////////////////////////////////
    //https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Pay/Vendor_Service_Center.html
    // 下面是微信支付
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 微信支付, 参考'HIHI交友' MoneyAddActivity.java
     * @param partnerId 商户号
     * @param prepayid 预支付交易会话ID
     * @param nonceStr 随机字符串
     * @param timeStamp 时间戳
     * @param packageValue 扩展字段,这里固定填写Sign=WXPay
     * @param sign 签名
     */
    public static boolean pay(String partnerId, String prepayid, String nonceStr, String timeStamp,
                       String packageValue, String sign) {
        PayReq req = new PayReq();
        req.appId = getAppId();//你的微信appid
        req.partnerId = partnerId;//商户号
        req.prepayId = prepayid;//预支付交易会话ID
        req.nonceStr = nonceStr;//随机字符串
        req.timeStamp = timeStamp;//时间戳
        req.packageValue = packageValue;//扩展字段,这里固定填写Sign=WXPay
        req.sign = sign;//签名
        //      req.extData         = "app data"; // optional
        return getIWXAPI().sendReq(req);
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
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html
     * 获取token/登录
     * 1. 第三方发起微信授权登录请求，微信用户允许授权第三方应用后，微信会拉起应用或重定向到第三方网站，并且带上授权临时票据code参数；
     * 2. 通过code参数加上AppID和AppSecret等，通过API换取access_token；
     * 3. 通过access_token进行接口调用，获取用户基本数据资源或帮助用户实现基本操作。
     * <img src="https://res.wx.qq.com/op_res/ZLIc-BdWcu_ixroOT0sBEtk0UwpTewqS6ujxbC2QOpbKIVp_DzleM_C9I-9GPDDh" />
     *
     * @param scope 应用授权作用域，如获取用户个人信息则填写snsapi_userinfo, 多个用逗号隔开
     *              "snsapi_userinfo,snsapi_friend,snsapi_message,snsapi_contact"
     * @param state 用于保持请求和回调的状态，授权请求后原样带回给第三方。
     *              该参数可用于防止 csrf 攻击（跨站请求伪造攻击），建议第三方带上该参数，
     *              可设置为简单的随机数加 session 进行校验    (可以瞎填任何值)
     */
    public static void login(String scope, @Nullable String state) {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = scope;
        req.state = state;
        getIWXAPI().sendReq(req);
    }

    /**
     * (应该服务端调这个接口)
     * 第二步：通过 {@link #login(String, String) 登录} 后返回的 code 获取 access_token
     * appid	是	应用唯一标识，在微信开放平台提交应用审核通过后获得
     * secret	是	应用密钥 AppSecret，在微信开放平台提交应用审核通过后获得
     * code	是	填写第一步获取的 code 参数
     * grant_type	是	填 authorization_code
     *
     * 返回Json:
     * {
     *   "access_token": "ACCESS_TOKEN",            //接口调用凭证, 有效期（目前为 2 个小时）
     *   "expires_in": 7200,                        //access_token 接口调用凭证超时时间，单位（秒）
     *   "refresh_token": "REFRESH_TOKEN",          //用户刷新 access_token, 较长的有效期（30 天），当 refresh_token 失效的后，需要用户重新授权
     *   "openid": "OPENID",                        //授权用户唯一标识
     *   "scope": "SCOPE",                          //用户授权的作用域，使用逗号（,）分隔
     *   "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL" //当且仅当该移动应用已获得该用户的 userinfo 授权时，才会出现该字段
     * }
     * @deprecated access_token 为用户授权第三方应用发起接口调用的凭证（相当于用户登录态），存储在客户端，
     * 可能出现恶意获取access_token 后导致的用户数据泄漏、用户微信相关接口功能被恶意发起等行为；
     */
    @Deprecated
    public static void getAccessToken(String code, BaseCallback<Object> callback) {
        String url = TextUtils2.getStringFormat("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=%s&secret=%s&code=%s&grant_type=authorization_code", getAppId(),
                appSecret, code);
        MyOkHttpUtils.get(url, null, callback);
    }

    /**
     * (应该服务端调这个接口)
     * 刷新 access_token 有效期
     * access_token 是调用授权关系接口的调用凭证，由于 access_token 有效期（目前为 2 个小时）较短，
     * 当 access_token 超时后，可以使用 refresh_token 进行刷新，access_token 刷新结果有两种：
     *   1. 若access_token已超时，那么进行refresh_token会获取一个新的access_token，新的超时时间；
     *   2. 若access_token未超时，那么进行refresh_token不会改变access_token，但超时时间会刷新，相当于续期access_token。
     * refresh_token 拥有较长的有效期（30 天），当 refresh_token 失效的后，需要用户重新授权。
     *
     * appid	是	应用唯一标识
     * grant_type	是	填 refresh_token
     * refresh_token	是	填写通过 access_token 获取到的 refresh_token 参数
     *
     * 返回Json:
     * {
     *   "access_token": "ACCESS_TOKEN",    //接口调用凭证
     *   "expires_in": 7200,                //access_token 接口调用凭证超时时间，单位（秒）
     *   "refresh_token": "REFRESH_TOKEN",  //用户刷新 access_token
     *   "openid": "OPENID",                //授权用户唯一标识
     *   "scope": "SCOPE"                   //用户授权的作用域，使用逗号（,）分隔
     * }
     * @deprecated refresh_token 为用户授权第三方应用的长效凭证，仅用于刷新access_token，但泄漏后相当于access_token 泄漏，风险同上。
     */
    @Deprecated
    public static void refreshToken(String refresh_token, BaseCallback<Object> callback) {
        String url = TextUtils2.getStringFormat("https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                "appid=%s&grant_type=refresh_token&refresh_token=%s", getAppId(), refresh_token);
        MyOkHttpUtils.get(url, null, callback);
    }

    /**
     * (应该服务端调这个接口)
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Authorized_API_call_UnionID.html
     * 检验授权凭证（access_token）是否有效
     * @param access_token	是	调用接口凭证
     * @param openid	是	普通用户标识，对该公众帐号唯一
     *
     * 正确返回Json:
     * {
     *   "errcode": 0,
     *   "errmsg": "ok"
     * }
     * @deprecated 不用调用, 就算要调用也是服务端去调
     */
    @Deprecated
    public static void authToken(String access_token, String openid, BaseCallback<Object> callback) {
        String url = TextUtils2.getStringFormat("https://api.weixin.qq.com/sns/auth?" +
                "access_token=%s&openid=%s", access_token, openid);
        MyOkHttpUtils.get(url, null, callback);
    }

    /**
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Login_via_Scan.html
     * 扫码登录
     * 扫码登录能力，指的是开发者可在移动应用内使用此能力，拉取二维码，
     * 用户使用微信客户端扫描二维码后可以登录此移动应用。
     * 此能力可被应用在多设备登录、智能硬件、电视盒子等场景。
     *
     * @param scope 应用授权作用域，拥有多个作用域用逗号（,）分隔，APP 所拥有的 scope
     * @param signature 签名
     * @deprecated 迷之api
     */
    @Deprecated
    public static void loginQr(String scope, String signature) {
        /**
         * nonceStr     是   一个随机的尽量不重复的字符串，用来使得每次的
         * timeStamp    是   时间戳
         * signature    是   签名
         * schemeData?  否   会在扫码后拼在 scheme 后
         */
        //String noncestr, String timestamp, String signature, OAuthListener listener
//        IDiffDevOAuth.auth(getAppId(), scope, UUID.randomUUID().toString(),
//                String.valueOf(System.currentTimeMillis()), signature, new OAuthListener() {
//
//                    /**
//                     * auth之后返回的二维码接口
//                     * @param qrcodeImgPath 废弃
//                     * @param imgBuf 二维码图片数据
//                     */
//                    @Override
//                    public void onAuthGotQrcode(String qrcodeImgPath, byte[] imgBuf) {
//                    }
//
//                    /**
//                     * 用户扫描二维码之后，回调改接口
//                     */
//                    @Override
//                    public void onQrcodeScanned() {
//                    }
//
//                    /**
//                     * 用户点击授权后，回调改接口
//                     */
//                    @Override
//                    public void onAuthFinish(OAuthErrCode oAuthErrCode, String s) {
//                    }
//                });
    }

    /**
     * (应该服务端调这个接口)
     * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Authorized_API_call_UnionID.html
     * 获取用户个人信息（UnionID 机制）
     * @param access_token 调用凭证
     * @param openid 普通用户的标识，对当前开发者帐号唯一
     * @deprecated 应该后端调这个接口然后返回用户信息, 因为 access_token 保存到客户端不安全
     */
    @Deprecated
    public static void getUserInfo(String access_token, String openid, BaseCallback<Object> callback) {
        String url = TextUtils2.getStringFormat("https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=%s&openid=%s", access_token, openid);
        MyOkHttpUtils.get(url, null, callback);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 下面是订阅消息
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 订阅消息, 抄自 Demo 的 SubscribeMessageActivity.java
     * @param scene ?
     * @param templateID ?
     * @param reserved ?
     * @return ?
     */
    public static boolean subscribeMessage(int scene, String templateID, String reserved) {
        //检查微信是否支持订阅消息
        if (getIWXAPI().getWXAppSupportAPI() >= Build.SUBSCRIBE_MESSAGE_SUPPORTED_SDK_INT) {
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
     * @param miniProgramAppId ?
     * @return ?
     */
    public static boolean subscribeMiniProgramMsg(String miniProgramAppId) {
        //检查微信是否支持订阅消息
        if (getIWXAPI().getWXAppSupportAPI() >= Build.SUBSCRIBE_MINI_PROGRAM_MSG_SUPPORTED_SDK_INT) {
            SubscribeMiniProgramMsg.Req req = new SubscribeMiniProgramMsg.Req();
            req.miniProgramAppId = miniProgramAppId;
            return getIWXAPI().sendReq(req);
        }
        return false;
    }


    ///////////////////////////////////////////////////////////////////////////
    // https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Smart_APIs/Image_Recognition_Interface.html
    // 微信智能接口:
    // 图像识别接口开发指南
    // 语音识别接口
    // 语音合成接口开发指南
    // 语义理解上手指南

    //https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Launching_a_Mini_Program/Launching_a_Mini_Program.html
    //App拉起小程序功能
    //移动应用拉起小程序功能
    //Android开发示例
    //iOS开发示例
    ///////////////////////////////////////////////////////////////////////////

    //通过Glide获取略缩图
    protected static void getThumbByGlide(String path, boolean isVideo, GetThumbByGlideListener listener) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
//                    if (isVideo) {
//
//                    } else {
                        Bitmap thumbBmp = Glide.with(ConfigUtils.APPLICATION)
                                .asBitmap()
                                .load(path)
                                .submit(thumbSize, thumbSize)
                                .get();//必须子线程
                        listener.onGetThumbByGlide(thumbBmp);
//                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    listener.onGetThumbByGlide(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    listener.onGetThumbByGlide(null);
                }
            }
        });
    }

    public interface GetThumbByGlideListener {
        void onGetThumbByGlide(Bitmap thumb);
    }
}
