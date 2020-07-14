package com.actor.myandroidframework.utils.tencent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

/**
 * Description: QQ工具类
 * 1.需要下载jar包v3.3.7:
 *   https://wiki.connect.qq.com/sdk%E4%B8%8B%E8%BD%BD      或↓
 *   https://github.com/actor20170211030627/MyAndroidFrameWork/blob/master/myandroidframework/libs/open_sdk_r2973327_lite.jar
 *
 * 2.将jar包放在libs目录下, 并且在app的gradle中添加:
 *   implementation files('libs/open_sdk_r2973327_lite.jar')//QQ登录等v3.3.7
 *
 * 3.在腾讯开放平台注册成为开发者，然后获取APP ID
 *   注册开发者地址(一般是公司注册): https://open.qq.com/reg
 *   创建应用, 获取APP ID: https://connect.qq.com/manage.html#/
 *
 * 4.需要在清单文件中添加: https://wiki.connect.qq.com/qq%E7%99%BB%E5%BD%95
 * //只需添加以下1个activity, 另外一个activity已经添加了的.
 * <!-- 以下1个activity是QQ登录 -->
 * <activity
 *     android:name="com.tencent.tauth.AuthActivity"
 *     android:launchMode="singleTask"
 *     android:noHistory="true">
 *     <intent-filter>
 *         <action android:name="android.intent.action.VIEW" />
 *         <category android:name="android.intent.category.DEFAULT" />
 *         <category android:name="android.intent.category.BROWSABLE" />
 *         <data android:scheme="tencent222222" /> <!-- 这儿替换成: "tencent" + appid -->
 *     </intent-filter>
 * </activity>
 *
 * 5.添加apache, Android 9.0上QQ分享报错 https://developer.umeng.com/docs/66750/detail/94386
 * <!--QQUtils, 在targetSdkVersion>=28时, 在Android 9.0的手机上进行QQ登录&分享(<=v3.3.7) 会报错...-->
 * <!--写在 AndroidManifest.xml 的 <application>标签内, 和<activity 同级 -->
 * <uses-library
 *     android:name="org.apache.http.legacy"
 *     android:required="false" />
 *
 * 6.在Application中设置appid: {@link #setAppId(String)}
 *
 * 7.如果QQ登录, 需要重写方法: {@link #onActivityResult(int, int, Intent)}
 *
 * 8.错误码列表
 * https://wiki.connect.qq.com/%E5%85%AC%E5%85%B1%E8%BF%94%E5%9B%9E%E7%A0%81%E8%AF%B4%E6%98%8E
 *
 * 9.示例使用:
 * https://github.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/ThirdActivity.java
 *
 * @author     : 李大发
 * date       : 2020/3/5 on 12:28
 * @version 1.0
 */
public class QQUtils {

    private static String         tencentAppId = "222222";//1108291678
    private static Tencent        tencent;
    private static final Context CONTEXT = ConfigUtils.APPLICATION;

    /**
     * 在Application中设置appId, 一般是一串数字
     */
    public static void setAppId(String tencentAppId) {
        QQUtils.tencentAppId = tencentAppId;
    }

    public static Tencent getTencent() {
        if (tencent == null) {
            tencent = Tencent.createInstance(tencentAppId, CONTEXT);
        }
        return tencent;
    }

    /**
     * QQ登录, 需要重写方法: {@link #onActivityResult(int, int, Intent)}
     * @param scope 应用需要获得哪些接口的权限，由“，”分隔。例如：
     *              SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param qrcode 是否开启二维码登录，没有安装手Q时候使用二维码登录，一般用电视等设备。
     *               (如果true使用二维码, 就没有网页输入账号密码登录的界面了)
     * @param listener 登录回调, 可见示例: {@link #baseUiListener}
     *                 注意: 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void login(Activity activity, String scope, boolean qrcode, BaseUiListener listener) {
        //校验登录态,如果缓存的登录态有效，可以直接使用缓存而不需要再次拉起手Q
        //https://wiki.connect.qq.com/session是否有效
        boolean isValid =  getTencent().checkSessionValid(tencentAppId);
        if (isValid) {
            //https://wiki.connect.qq.com/读取session
            //从本地获取第三方应用等token、openid信息等session信息的接口。
            //注意：应该先调用Tencent.checkSessionValid() ，返回token信息有效后再调用这个接口。
            //否则如果token信息无效，调用该接口没有意义。
            JSONObject jsonObject = getTencent().loadSession(tencentAppId);
            if (jsonObject != null && jsonObject.length() > 0) {
                listener.onComplete(jsonObject);
            } else logError("QQ登录, 获取的jsonobject为空!");
        } else {
            int code = getTencent().login(activity, scope, listener, qrcode);
            logResultCode(code);
        }
    }
    //QQ登录回调
    private BaseUiListener baseUiListener = new BaseUiListener() {
        @Override
        public void doComplete(@Nullable JSONObject response) {
            QQUtils.initSessionCache(response);
//            logError(String.valueOf(response));
        }
    };

    /**
     * 强制二维码登录 or 强制输入账号密码登录
     * @param qrcode 如果true, 强制二维码登录. 如果false, 强制输入账号密码登录
     * @param listener
     */
    public static void loginQrCode$AccountPassword(Activity activity, String scope, boolean qrcode,
                                                   BaseUiListener listener) {
        //强制唤起扫码界面（无论是否安装手Q）
        activity.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
        login(activity, scope, qrcode, listener);
    }

    /**
     * Server-Side登录模式, 需要重写方法: {@link #onActivityResult(int, int, Intent)}
     * @param scope 见上一个方法
     */
    public static void loginServerSide(Activity activity, String scope, BaseUiListener listener) {
        int code = getTencent().loginServerSide(activity, scope, listener);
        logResultCode(code);
    }

    /**
     * OEM应用市场登录/校验登录态, 需要重写方法: {@link #onActivityResult(int, int, Intent)}
     * 登录/校验登录态，主要用于OEM应用市场分渠道计费参数需求。
     * @param registerChannel 注册渠道 "10000144"
     * @param installChannel 安装渠道 "10000144"
     * @param businessId 业务ID "xxxx"
     */
    public static void loginWithOEM(Activity activity, String scope, boolean qrcode,
                                String registerChannel, String installChannel, String businessId,
                                BaseUiListener listener) {
        int code = getTencent().loginWithOEM(activity, scope, listener, qrcode,
                registerChannel, installChannel, businessId);
        logResultCode(code);
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
//        logError(json);
    }

    /**
     * https://wiki.connect.qq.com/获取Token对象
     * 登录后需要调用获取用户信息、设置头像等接口时，需要登录返回的Token数据时，通过改接口获取。
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
     * 退出登录
     */
    public static void logout() {
        getTencent().logout(CONTEXT);
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
     */
    public static void shareToQQImgTxt(Activity activity, @NonNull String title, String summary,
                                       @NonNull String targetUrl, String imgUrl, String appName,
                                       Integer extInt, BaseUiListener listener) {
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
     */
    public static void shareToQQApp(Activity activity, @NonNull String title, String summary,
                                    String imgUrl, String appName, Integer extInt,
                                    BaseUiListener listener) {
        if (!Tencent.isSupportShareToQQ(CONTEXT)) {
            ToastUtils.showShort("不支持分享到QQ");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);//必传
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
        Intent sendIntent = IntentUtils.getShareImageIntent("", filePath);
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
    public static int startIMAio(Activity activity, String targetQq) {
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
    public static int startIMAudio(Activity activity, String targetQq) {
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
    public static int startIMVideo(Activity activity, String targetQq) {
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
    public static int startMiniApp(Activity activity, String miniAppId, String miniAppPath,
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
        logFormat("返回码: %d, %s", code, msg);
    }

    //打印日志
    protected static void logError(String message) {
        LogUtils.error(message, false);
    }

    protected static void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    protected static void otherMethods(Activity activity) {
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
//        logFormat("-->onActivityResult " + requestCode + " resultCode=" + resultCode);
//        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
//            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
    }
}
