package com.actor.qq_wechat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

/**
 * Description: <a href="https://wiki.connect.qq.com/sdk%e4%b8%8b%e8%bd%bd">QQ互联SDK下载</a>,
 * <a href="https://wiki.connect.qq.com/android_sdk%e5%8a%9f%e8%83%bd%e5%88%97%e8%a1%a8">Android_SDK功能列表</a> <br />
 * QQ互联SDK是腾讯公司提供的一套软件开发工具包，主要用于帮助开发者在自己的应用程序或网站中集成QQ互联的功能，主要功能包括登录授权、获取用户昵称头像等信息、分享、互动等。
 * <ul>
 *     <li>
 *         1.需要下载jar包:
 *         //https://wiki.connect.qq.com/sdk%E4%B8%8B%E8%BD%BD QQ互联SDK
 *         <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/libs/open_sdk_3.5.14.3_rc26220c_lite.jar" target="_blank">open_sdk_3.5.14.3_rc26220c_lite.jar</a>
 *     </li>
 *     <li>
 *         2.将jar包放在libs目录下, 并且在app的gradle中添加: <br />
 *         //QQ登录等 <br />
 *         implementation files('libs/open_sdk_3.5.14.3_rc26220c_lite.jar')
 *     </li>
 *     <li>
 *         3.在腾讯开放平台注册成为开发者，然后获取APP ID <br />
 *         注册开发者地址: <a href="https://open.qq.com/reg" target="_blank">https://open.qq.com/reg</a> <br />
 *         创建应用, 获取APP ID: <a href="https://connect.qq.com/manage.html#/" target="_blank">https://connect.qq.com/manage</a>
 *     </li>
 *     <li>4.<a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">权限配置</a>: 集成本依赖后已自动配置!</li>
 *     <li>5.<a href="https://wiki.connect.qq.com/android%e5%b8%b8%e8%a7%81%e9%97%ae%e9%a2%98">混淆配置</a>: if你要混淆, 请点击链接查看...</li>
 *     <li>
 *         6.需要在清单文件中添加Activity: https://wiki.connect.qq.com/qq%E7%99%BB%E5%BD%95 <br />
 *         &lt;activity                                                                            <br />
 *             &emsp; android:name="com.tencent.tauth.AuthActivity"                                 <br />
 *             &emsp; android:launchMode="singleTask"                                               <br />
 *             &emsp; android:noHistory="true">                                                     <br />
 *             &emsp; &lt;intent-filter>                                                            <br />
 *                 &emsp;&emsp; &lt;action android:name="android.intent.action.VIEW" /&gt;          <br />
 *                 &emsp;&emsp; &lt;category android:name="android.intent.category.DEFAULT" /&gt;   <br />
 *                 &emsp;&emsp; &lt;category android:name="android.intent.category.BROWSABLE" /&gt; <br />
 *                 &emsp;&emsp; &lt;data android:scheme="tencent222222" /&gt; &lt;!-- 这儿替换成: "tencent" + appid --> <br />
 *             &lt;/intent-filter> <br />
 *         &lt;/activity>
 *     </li>
 *     <li>
 *         7.在Application中初始化: <br />
 *         {@link #setIsPermissionGranted(boolean) QQUtils.setIsPermissionGranted(true)};   //用户已授权应用获取设备信息 <br />
 *         {@link #setAppId(String) QQUtils.setAppId(appId)};   //设置appid <br />
 *     </li>
 *     <li>8.如果QQ登录, 需要重写方法参考: {@link #onActivityResult(int, int, Intent)}</li>
 *     <li>9.<a href="https://wiki.connect.qq.com/%E5%85%AC%E5%85%B1%E8%BF%94%E5%9B%9E%E7%A0%81%E8%AF%B4%E6%98%8E" target="_blank">错误码列表</a></li>
 *     <li>10.示例使用: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/ThirdActivity.java" target="_blank">ThirdActivity.java</a></li>
 * </ul>
 *
 * @author    : ldf <br />
 * @update    : 2023/7/31 <br />
 */
public class QQUtils {

    protected static final Application CONTEXT = Utils.getApp();
    protected static String            tencentAppId = "1234567890";
    protected static String            authorities = CONTEXT.getPackageName().concat(".utilcode.fileprovider");
    protected static       Tencent     tencent;

    /**
     * <a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">初始化SDK</a>
     * 在调用互联SDK相关功能接口之前，需要应用在确认用户已授权应用获取设备信息
     * @param isGranted 是否已经过用户授权
     */
    public static void setIsPermissionGranted(boolean isGranted) {
        //参2: 机器型号。传入后SDK内部不再自行获取
        Tencent.setIsPermissionGranted(isGranted, Build.MODEL);
    }

    /**
     * 在Application中设置appId, 一般是一串数字
     */
    public static void setAppId(String tencentAppId) {
        QQUtils.tencentAppId = tencentAppId;
    }

    /**
     * Authorities为 Manifest文件中注册FileProvider时设置的authorities属性值
     * @param authorities 可使用: <br />
     * {@link com.blankj.utilcode.util.UtilsFileProvider UtilsFileProvider} 的清单文件注册: "${applicationId}.utilcode.fileprovider"(默认) <br />
     * 或者: {@link com.luck.picture.lib.basic.PictureFileProvider PictureFileProvider} 的清单文件注册: "${applicationId}.luckProvider"
     */
    public static void setAuthorities(@NonNull String authorities) {
        if (!TextUtils.isEmpty(authorities)) QQUtils.authorities = authorities;
    }

    /**
     * <a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">创建实例</a>
     */
    public static Tencent getTencent() {
        if (tencent == null) {
            tencent = Tencent.createInstance(tencentAppId, CONTEXT, authorities);
        }
        return tencent;
    }

    /**
     * <a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">判断SDK内存缓存中QQ是否已经安装</a>
     */
    public static boolean isQQInstalled() {
        return getTencent().isQQInstalled(CONTEXT);
    }

    /**
     * <a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">重置内存缓存</a>
     * 3.5.9版本中SDK内部只会在首次调用SDK接口时调用系统接口获取设备中QQ/Tim的安装信息，并缓存在内存中提供后续接口使用。
     * 当用户在设备中未安装QQ/Tim时启动接入应用，SDK获取到设备中未安装QQ/Tim并缓存了查询结果，
     * 如果接入应用需要引导用户安装QQ/Tim时，需要接入应用在调用接口重置SDK缓存的安装信息, 保证SDK能够正确判断设备中是否已经安装QQ。
     * 可以调用 tencent.isQQInstalled()方法判断SDK内存缓存中QQ是否已经安装，调用Tencent.resetTargetAppInfoCache()方法重置内存缓存。
     */
    public static void resetTargetAppInfoCache() {
        Tencent.resetTargetAppInfoCache();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%E7%99%BB%E5%BD%95-%E6%A0%A1%E9%AA%8C%E7%99%BB%E5%BD%95%E6%80%81">登录/校验登录态</a>
     * 需要重写方法: {@link #onActivityResult(int, int, Intent)}
     * @param scope 应用需要获得哪些接口的权限，由“，”分隔。例如：
     *              SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param qrcode 是否开启二维码登录，没有安装手Q时候使用二维码登录，一般用电视等设备。
     *               (如果true使用二维码, 就没有网页输入账号密码登录的界面了)
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void login(@NonNull Activity activity, @NonNull String scope, boolean qrcode, BaseUiListener listener) {
        //校验登录态,如果缓存的登录态有效，可以直接使用缓存而不需要再次拉起手Q
        //https://wiki.connect.qq.com/当前会话是否有效
        boolean isValid =  getTencent().isSessionValid();
        if (isValid) {
            //https://wiki.connect.qq.com/读取session
            //从本地获取第三方应用等token、openid信息等session信息的接口。
            //注意：应该先调用Tencent.checkSessionValid() ，返回token信息有效后再调用这个接口。
            //否则如果token信息无效，调用该接口没有意义。
            JSONObject jsonObject = getTencent().loadSession(tencentAppId);
            if (jsonObject != null && jsonObject.length() > 0) {
                listener.onComplete(jsonObject);
            } else LogUtils.error("QQ登录, 获取的jsonobject为空!");
        } else {
            int code = getTencent().login(activity, scope, listener, qrcode);
            logResultCode(code);
        }
    }
    //QQ登录回调示例
    protected BaseUiListener baseUiListener = new BaseUiListener() {
        @Override
        public void doComplete(@Nullable JSONObject response) {
            QQUtils.initSessionCache(response);
//            LogUtils.error(String.valueOf(response));
        }
    };

    /**
     * <a href="https://wiki.connect.qq.com/server-side%E7%99%BB%E5%BD%95%E6%A8%A1%E5%BC%8F">Server-Side登录模式</a> <br />
     * 1.当安装了手机QQ时，SDK会启用手机QQ的特定Activity，通过此Activity完成登录和授权功能。<br />
     * 2.当没有找到此Activity时，SDK会执行Oauth2.0的User-Agent流程，即显示一个包含WebView的对话框， 通过加载登录授权网页来完成登录和授权的交互流程。<br />
     * 需要重写方法: {@link #onActivityResult(int, int, Intent)}
     * @param scope 应用需要获得哪些接口的权限，由“，”分隔。例如：SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void loginServerSide(@NonNull Activity activity, @NonNull String scope, BaseUiListener listener) {
        //强制唤起扫码界面（无论是否安装手Q）
//        activity.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
        int code = getTencent().loginServerSide(activity, scope, listener);
        logResultCode(code);
    }

    /**
     * <a href="https://wiki.connect.qq.com/oem%e5%ba%94%e7%94%a8%e5%b8%82%e5%9c%ba%e7%99%bb%e5%bd%95-%e6%a0%a1%e9%aa%8c%e7%99%bb%e5%bd%95%e6%80%81">OEM应用市场登录/校验登录态</a> <br />
     * 通过调用Tencent类的loginWithOEM函数发起登录/校验登录态，主要用于OEM应用市场分渠道计费参数需求。
     * 需要重写方法: {@link #onActivityResult(int, int, Intent)}
     * @param scope 应用需要获得哪些接口的权限，由“，”分隔。例如：SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param qrcode 是否开启二维码登陆
     * @param registerChannel 注册渠道 "10000144"
     * @param installChannel 安装渠道 "10000144"
     * @param businessId 业务ID "xxxx"
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void loginWithOEM(@NonNull Activity activity, @NonNull String scope, boolean qrcode,
                                    @NonNull String registerChannel, @NonNull String installChannel,
                                    @NonNull String businessId, BaseUiListener listener) {
        //强制唤起扫码界面（无论是否安装手Q）
//        activity.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
        int code = getTencent().loginWithOEM(activity, scope, listener, qrcode, registerChannel,
                installChannel, businessId);
        logResultCode(code);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e6%b3%a8%e9%94%80">注销</a>
     */
    public static void logout() {
        getTencent().logout(CONTEXT);
    }

    /**
     * 强制二维码登录 or 强制输入账号密码登录
     * @param qrcode 如果true, 强制二维码登录. 如果false, 强制输入账号密码登录
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void loginQrCode$AccountPassword(@NonNull Activity activity, @NonNull String scope,
                                                   boolean qrcode, BaseUiListener listener) {
        //强制唤起扫码界面（无论是否安装手Q）
        activity.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
        login(activity, scope, qrcode, listener);
    }

    /**
     * 保存token & openid, 保存后tencent.isSessionValid()=true
     * 获取到回调后, 再调用这个方法
     */
    public static void initSessionCache(JSONObject response) {
        //https://wiki.connect.qq.com/初始化session
        //初始化SDK中的token信息缓存，调用完这个接口后，Tencent里才有token信息的缓存，
        //才能调用其他的open api。
        //注意：应该先调用Tencent.checkSessionValid() ，返回token信息有效后再调用这个接口。
        //否则如果token信息无效，调用该接口没有意义。
        getTencent().initSessionCache(response);

        //https://wiki.connect.qq.com/保存Session会话信息
        //登录成功以后，保存session会话信息如token, openid等信息到SharePreferecne中。
        getTencent().saveSession(response);
//        String json = response.toString();
//        LogUtils.error(json);

        /**
         * <a href="https://wiki.connect.qq.com/%e6%a0%a1%e9%aa%8ctoken%e5%b9%b6%e7%bb%9f%e8%ae%a1dau">校验token并统计DAU</a>
         * 登录成功以后校验token并统计DAU
         */
        getTencent().reportDAU();
    }

    /**
     * https://wiki.connect.qq.com/获取Token对象
     * 登录后需要调用获取用户信息、设置头像等接口时，需要登录返回的Token数据时，通过改接口获取。
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void getUserInfo(BaseUiListener listener) {
        UserInfo userInfo = new UserInfo(CONTEXT, getTencent().getQQToken());
        userInfo.getUserInfo(listener);
    }

    /**
     * https://wiki.connect.qq.com/获取登录用户openid
     * openId为对当前应用进行授权的QQ用户的身份识别码，应用应将openId与应用中的用户帐号进行关系绑定，以此来支持多帐号。
     */
    public static String getOpenId() {
        return getTencent().getOpenId();
    }

    /**
     * https://wiki.connect.qq.com/分享消息到QQ（无需QQ登录）
     * 这个方法是分享图文
     * @param title 要分享的标题, 最长30个字符
     * @param summary 要分享的消息摘要, 最长40个字
     * @param targetUrl 这条消息被好友点击后跳转的url, 示例: http://www.qq.com/news/1.html
     * @param imgUrl 分享图片的url或本地路径, 示例: http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif
     * @param appName 手Q客户端顶部, 替换"返回"按钮文字
     * @param extInt 分享额外选项, 默认"显示分享到Qzone按钮=true" & "自动打开分享到Qzone对话框=false"
     *      @see QQShare#SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN //自动打开分享到Qzone对话框=true
     *      @see QQShare#SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE //显示分享到Qzone按钮=false
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void shareToQQImgTxt(@NonNull Activity activity, @NonNull String title, String summary,
                                       @NonNull String targetUrl, String imgUrl, String appName,
                                       @Nullable Integer extInt, BaseUiListener listener) {
        if (!Tencent.isSupportShareToQQ(CONTEXT)) {
            ToastUtils.showShort("不支持分享到QQ");
            return;
        }
        Bundle params = new Bundle();
        //图文分享(普通分享), 必传
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//必传
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);//必传
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 这个方法是分享图片
     * @param localUrl 本地图片路径, 图片不能大于5M
     * @param appName 手Q客户端顶部, 替换"返回"按钮文字
     * @param extInt 见上一个方法
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void shareToQQImg(Activity activity, @NonNull String localUrl, String appName,
                                       Integer extInt, BaseUiListener listener) {
        if (!Tencent.isSupportShareToQQ(CONTEXT)) {
            ToastUtils.showShort("不支持分享到QQ");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);//必传
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localUrl);//必传
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 这个方法是分享音乐
     * @param title 要分享的标题, 最长30个字符
     * @param summary 要分享的消息摘要, 最长40个字
     * @param targetUrl 这条消息被好友点击后跳转的url, 示例: http://www.qq.com/news/1.html
     * @param imgUrl 分享图片的url或本地路径, 示例: http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif
     * @param audioUrl 音乐链接
     * @param appName 手Q客户端顶部, 替换"返回"按钮文字
     * @param extInt 见上一个方法
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void shareToQQAudio(Activity activity, @NonNull String title, String summary,
                                      @NonNull String targetUrl, String imgUrl, @NonNull String audioUrl,
                                      String appName, Integer extInt, BaseUiListener listener) {
        if (!Tencent.isSupportShareToQQ(CONTEXT)) {
            ToastUtils.showShort("不支持分享到QQ");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);//必传
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//必传
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);//必传
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);//必传
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 这个方法是分享App, 应用分享后，发送方和接收方在聊天窗口中点击消息气泡即可进入应用的详情页。
     * @param title 要分享的标题, 最长30个字符
     * @param summary 要分享的消息摘要, 最长40个字
     * @param imgUrl 分享图片的url或本地路径, 示例: http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif
     * @param appName 手Q客户端顶部, 替换"返回"按钮文字
     * @param extInt 见上一个方法
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void shareToQQApp(@NonNull Activity activity, @NonNull String title, String summary,
                                    String imgUrl, String appName, Integer extInt,
                                    BaseUiListener listener) {
        if (!Tencent.isSupportShareToQQ(CONTEXT)) {
            LogUtils.error("不支持分享到QQ");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_MINI_PROGRAM);//必传
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//必传
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 分享文件到QQ, 调用系统Intent分享
     * @param filePath 文件路径
     * @return 是否跳转到QQ分享界面
     */
    public static boolean shareToQQFile(Context context, String filePath) {
        Intent sendIntent = IntentUtils.getShareImageIntent(filePath);
        sendIntent.setType("*/*");
        //这是正式版还是极速版?
        sendIntent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        try {
            context.startActivity(sendIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtils.showShort("未安装QQ");
            return false;
        }
    }

    public static void shareOthers() {
        //https://wiki.connect.qq.com/分享消息到QQ（无需QQ登录）
        //（模式5）分享携带ARK JSON串
        //（模式6）分享成QQ小程序
        //（模式7）分享QQ小程序到QQ空间

        //https://wiki.connect.qq.com/分享到QQ空间（无需QQ登录）
        //（模式1） 图文分享
        //（模式2） 发表说说、视频或上传图片

        //https://wiki.connect.qq.com/是否支持分享到QQ空间
        boolean supportPushToQZone = Tencent.isSupportPushToQZone(CONTEXT);
    }

    /**
     * https://wiki.connect.qq.com/聊天
     * 直接从第三方拉起手Q某个目标QQ的聊天窗口(QQ8.1.5)
     * @param targetQq 对方QQ
     * @return 函数执行的返回值：
     *      0: IM.IM_SUCCESS #执行成功
     *      -1: IM.IM_UIN_EMPTY #会话目标QQ号为空
     *      -2: IM.IM_SHOULD_DOWNLOAD #需下载新版QQ
     *      -3: IM.IM_LENGTH_SHORT #会话目标QQ号过短
     *      -4: IM.IM_UIN_NOT_DIGIT #会话目标QQ号非数字
     *      -5: IM.IM_UNKNOWN_TYPE #错误的类型
     */
    public static int startIMAio(@NonNull Activity activity, String targetQq) {
        return getTencent().startIMAio(activity, targetQq, activity.getPackageName());
    }

    /**
     * https://wiki.connect.qq.com/语音
     * 直接从第三方拉起手Q某个目标QQ的聊天窗口，并且发起语音会话(QQ8.1.5)
     * @param targetQq 对方QQ
     * @return 函数执行的返回值：
     *      0: IM.IM_SUCCESS #执行成功
     *      -1: IM.IM_UIN_EMPTY #会话目标QQ号为空
     *      -2: IM.IM_SHOULD_DOWNLOAD #需下载新版QQ
     *      -3: IM.IM_LENGTH_SHORT #会话目标QQ号过短
     *      -4: IM.IM_UIN_NOT_DIGIT #会话目标QQ号非数字
     *      -5: IM.IM_UNKNOWN_TYPE #错误的类型
     */
    public static int startIMAudio(@NonNull Activity activity, String targetQq) {
        return getTencent().startIMAudio(activity, targetQq, activity.getPackageName());
    }

    /**
     * https://wiki.connect.qq.com/视频
     * 直接从第三方拉起手Q某个目标QQ的聊天窗口，并且发起视频会话(QQ8.1.5)
     * @param targetQq 对方QQ
     * @return 函数执行的返回值：
     *      0: IM.IM_SUCCESS #执行成功
     *      -1: IM.IM_UIN_EMPTY #会话目标QQ号为空
     *      -2: IM.IM_SHOULD_DOWNLOAD #需下载新版QQ
     *      -3: IM.IM_LENGTH_SHORT #会话目标QQ号过短
     *      -4: IM.IM_UIN_NOT_DIGIT #会话目标QQ号非数字
     *      -5: IM.IM_UNKNOWN_TYPE #错误的类型
     */
    public static int startIMVideo(@NonNull Activity activity, String targetQq) {
        return getTencent().startIMVideo(activity, targetQq, activity.getPackageName());
    }

    /**
     * https://wiki.connect.qq.com/唤起主体小程序/小游戏
     * 直接从第三方APP主体拉起手Q内其对应的小程序/小游戏，前提是需要注册调起能力和绑定关系。
     * @param miniAppId 调用者期望调起的小程序/小游戏ID
     * @param miniAppPath 显示的小程序/小游戏的页面路径，例如: pages/tabBar/index/index
     * @param miniAppVersion 小程序/小游戏的版本: develop/trial/release
     * @return 函数执行的返回值：
     *      0: MiniApp.MINIAPP_SUCCESS #执行成功
     *      -1: MiniApp.MINIAPP_ID_EMPTY #小程序/小游戏ID为空
     *      -2: MiniApp.MINIAPP_SHOULD_DOWNLOAD #需下载新版QQ
     *      -3: MiniApp.MINIAPP_LENGTH_SHORT #小程序/小游戏ID长度过短（保留字段）
     *      -4: MiniApp.MINIAPP_ID_NOT_DIGIT #小程序/小游戏ID非数字
     *      -5: MiniApp.MINIAPP_UNKNOWN_TYPE #错误的类型
     *      -6: MiniApp.MINIAPP_CONTEXT_NULL #上下文为空
     *      -7: MiniApp.MINIAPP_VERSION_WRONG #小程序/小游戏版本有误
     */
    public static int startMiniApp(@NonNull Activity activity, String miniAppId, String miniAppPath,
                                   String miniAppVersion) {
        return getTencent().startMiniApp(activity, miniAppId, miniAppPath, miniAppVersion);
    }

    /**
     * @param code 返回码说明
     *             0：正常
     *            -1: 异常
     *             1 : 使用Activity登陆
     *             2 : 使用H5登陆或显示下载页面
     */
    protected static void logResultCode(int code) {
        String[] codeMsg = {"异常", "正常", "使用Activity登陆", "使用H5登陆或显示下载页面"};
        code += 1;
        String msg = null;
        if (code >= 0 && code < codeMsg.length) msg = codeMsg[code];
        LogUtils.errorFormat("返回码: %d, %s", code, msg);
    }


    protected static void otherMethods(@NonNull Activity activity) {
        //https://wiki.connect.qq.com/设置QQ头像
        //https://wiki.connect.qq.com/设置动态头像
        //https://wiki.connect.qq.com/设置QQ表情
        //出于信息安全的考虑，以上接口仅对可信赖的合作应用开放

        //https://wiki.connect.qq.com/当前会话是否有效
        boolean sessionValid = getTencent().isSessionValid();

        //https://wiki.connect.qq.com/获取APPID
        //获取当前应用的互联APPID。
        String appid = getTencent().getAppId();

        //https://wiki.connect.qq.com/获取AccessToken
        //登录成功以后，获取当前应用的AccessToken。
        String accessToken = getTencent().getAccessToken();

        //https://wiki.connect.qq.com/获取token过期时间
        //登录成功以后，获取当前应用的Token过期时间。
        long expiresIn = getTencent().getExpiresIn();//（单位秒）

        //https://wiki.connect.qq.com/设置token和过期时间
        //使用登陆接口登陆成功以后，在登陆的回调接口保存登陆返回的token和过期时间，单位秒。
        getTencent().setAccessToken("token", "expiresIn");

        //https://wiki.connect.qq.com/设置OpenId
        //使用登陆接口登陆成功以后，在登陆的回调接口保存登陆返回的OpenId。
        getTencent().setOpenId("openId");

        //https://wiki.connect.qq.com/判断是否登录且获取OpenId
        //判断当前是否登录且应用已经获取OpenId。
        //true：已经登录且获取OpenId。
        //false：没有登录获取没有获取OpenId。
        boolean ready = getTencent().isReady();

        //https://wiki.connect.qq.com/是否支持SSO登录
        //判断当前是否支持SSO登录。
        boolean supportSSOLogin = getTencent().isSupportSSOLogin(activity);

    }

//    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        LogUtils.errorFormat("QQ登录: onActivityResult: resultCode=%d, resultCode=%d", requestCode, resultCode);
//        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
//            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
    }
}
