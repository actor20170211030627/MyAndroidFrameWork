package com.actor.qq_wechat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.FileUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.SocialConstants;
import com.tencent.open.apireq.IApiCallback;
import com.tencent.open.log.Tracer;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: <a href="https://wiki.connect.qq.com/sdk%e4%b8%8b%e8%bd%bd">SDK下载 — QQ互联WIKI</a>,
 * <a href="https://wiki.connect.qq.com/android_sdk%e5%8a%9f%e8%83%bd%e5%88%97%e8%a1%a8">Android_SDK功能列表</a> <br />
 * QQ互联SDK是腾讯公司提供的一套软件开发工具包，主要用于帮助开发者在自己的应用程序或网站中集成QQ互联的功能，主要功能包括登录授权、获取用户昵称头像等信息、分享、互动等。
 * <ul>
 *     <li>
 *         1.在<a href="https://wiki.connect.qq.com/sdk%e4%b8%8b%e8%bd%bd">官网</a>下载jar包, 或在Gitee下载本工具类使用的jar包:
 *         <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/libs/open_sdk_3.5.15.6_r972e71b_lite.jar" target="_blank">open_sdk_3.5.15.6_r972e71b_lite.jar</a>
 *     </li>
 *     <li>
 *         2.将jar包放在libs目录下, 并且在app的gradle中添加: <br />
 *         //QQ登录等 <br />
 *         <code>implementation files('libs/open_sdk_3.5.15.6_r972e71b_lite.jar')</code>
 *     </li>
 *     <li>
 *         3.在腾讯开放平台<a href="https://open.qq.com/reg" target="_blank">注册成为开发者</a>，然后<a href="https://connect.qq.com/manage.html#/" target="_blank">创建应用, 获取APP ID</a>
 *     </li>
 *     <li>4.<a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">权限配置</a>: 集成本依赖后已自动配置, 使用者不用管!</li>
 *     <li>5.<a href="https://wiki.connect.qq.com/android%e5%b8%b8%e8%a7%81%e9%97%ae%e9%a2%98">混淆配置</a>: 集成本依赖后已自动混淆, 使用者不用管!</li>
 *     <li>
 *         6.需要在清单文件中<a href="https://wiki.connect.qq.com/qq%E7%99%BB%E5%BD%95" target="_blank">添加Activity标签</a>:
 *         <pre>
 *         &lt;activity
 *             &emsp; android:name="com.tencent.tauth.AuthActivity"
 *             &emsp; android:launchMode="singleTask"
 *             &emsp; android:noHistory="true">
 *             &emsp; &lt;intent-filter>
 *                 &emsp;&emsp; &lt;action android:name="android.intent.action.VIEW" /&gt;
 *                 &emsp;&emsp; &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &emsp;&emsp; &lt;category android:name="android.intent.category.BROWSABLE" /&gt;
 *                 &emsp;&emsp; &lt;data android:scheme="tencent222222" /&gt; &lt;!-- 这儿替换成: "tencent" + appid -->
 *             &lt;/intent-filter>
 *         &lt;/activity>
 *         </pre>
 *     </li>
 *     <li>
 *         7.在Application中初始化: <br />
 *         {@link #setIsPermissionGranted(boolean) QQUtils.setIsPermissionGranted(true)};   //用户已授权应用获取设备信息 <br />
 *         {@link #setAppId(String) QQUtils.setAppId(appId)};   //设置appid <br />
 *     </li>
 *     <li>
 *         8.如果分享到QQ, 分享到QQ空间, 添加QQ收藏, 发送到我的电脑, QQ登录 等, <br />
 *           请将{@link #onActivityResult(int, int, Intent)}这儿的代码复制到你的页面中
 *     </li>
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
     * 在Application中设置appId, 一般是一串数字
     */
    public static void setAppId(String tencentAppId) {
        if (!TextUtils.equals(QQUtils.tencentAppId, tencentAppId)) tencent = null;
        QQUtils.tencentAppId = tencentAppId;
    }

    /**
     * Authorities为 Manifest文件中注册FileProvider时设置的authorities属性值
     * @param authorities 可使用: <br />
     * {@link com.blankj.utilcode.util.UtilsFileProvider UtilsFileProvider} 的清单文件注册: "${applicationId}.utilcode.fileprovider"(默认) <br />
     * 或者: {@link com.luck.picture.lib.basic.PictureFileProvider PictureFileProvider} 的清单文件注册: "${applicationId}.luckProvider"
     */
    public static void setAuthorities(@NonNull String authorities) {
        if (!TextUtils.isEmpty(authorities)) {
            if (!TextUtils.equals(QQUtils.authorities, authorities)) tencent = null;
            QQUtils.authorities = authorities;
        }
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



    ///////////////////////////////////////////////////////////////////////////
    // 8.隐私权限
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">初始化SDK</a>
     * 在调用互联SDK相关功能接口之前，需要应用在确认用户已授权应用获取设备信息 <br />
     * <a href="https://wiki.connect.qq.com/%e8%ae%be%e7%bd%ae%e7%94%a8%e6%88%b7%e6%98%af%e5%90%a6%e5%b7%b2%e6%8e%88%e6%9d%83%e8%8e%b7%e5%8f%96%e8%ae%be%e5%a4%87%e4%bf%a1%e6%81%af">设置用户是否已授权获取设备信息</a> <br />
     * 在调用其他SDK接口前调用该接口通知 SDK 用户是否已授权应用获取设备信息的权限，或在应用的取消授权界面中提供用户撤销获取设备信息的权限。
     * @param isGranted 是否已经过用户授权
     */
    public static void setIsPermissionGranted(boolean isGranted) {
        //参2: 机器型号。传入后SDK内部不再自行获取
        Tencent.setIsPermissionGranted(isGranted, Build.MODEL);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%8e%b7%e5%8f%96%e7%94%a8%e6%88%b7%e6%98%af%e5%90%a6%e5%b7%b2%e6%8e%88%e6%9d%83%e8%8e%b7%e5%8f%96%e8%ae%be%e5%a4%87%e4%bf%a1%e6%81%af">获取用户是否已授权获取设备信息</a> <br />
     * 获取SDK当前用户是否已授权获取设备信息的标志位
     */
    public static boolean isPermissionGranted() {
        return !Tencent.isPermissionNotGranted();   //为何是NotGranted, 无语.
    }

    /**
     * <a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">重置内存缓存</a> <br />
     * <a href="https://wiki.connect.qq.com/%e9%87%8d%e7%bd%aeqq-tim%e5%ae%89%e8%a3%85%e4%bf%a1%e6%81%af%e5%86%85%e5%ad%98%e7%bc%93%e5%ad%98">重置QQ/Tim安装信息内存缓存</a> <br />
     * 在设备未安装QQ/Tim时启动应用，SDK内部获取到设备中未安装QQ/Tim并在内存中记录，
     * 当应用需要引导用户安装QQ/Tim时，可以在引导弹窗的点击事件等类似场景中调用接口，使SDK重现调用系统接口获取QQ/Tim的安装信息
     */
    public static void resetTargetAppInfoCache() {
        Tencent.resetTargetAppInfoCache();
        Tencent.resetQQAppInfoCache();
        Tencent.resetTimAppInfoCache();
    }

    /**
     * <a href="https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95">判断SDK内存缓存中QQ是否已经安装</a>(不包括轻聊版, 国际版)
     */
    public static boolean isQQInstalled() {
        return getTencent().isQQInstalled(CONTEXT);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 1.登陆注销
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/%E7%99%BB%E5%BD%95-%E6%A0%A1%E9%AA%8C%E7%99%BB%E5%BD%95%E6%80%81">登录/校验登录态</a> <br />
     *
     * @param scope 应用需要获得哪些接口的权限，由“,”分隔。例如：
     *              SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param qrcode 是否开启二维码登录，没有安装手Q时候使用二维码登录，一般用电视等设备。
     *               (如果true使用二维码, 就没有网页输入账号密码登录的界面了)
     * @param listener 登录回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *            <li>回调完成后保存session: {@link #initSessionCache(JSONObject)}</li>
     *        </ol>
     */
    public static int login(@NonNull Activity activity, @NonNull String scope, boolean qrcode, @NonNull BaseUiListener listener) {
        //校验登录态,如果缓存的登录态有效，可以直接使用缓存而不需要再次拉起手Q
        //https://wiki.connect.qq.com/当前会话是否有效
        boolean isValid = isSessionValid();
        if (isValid) {
            //读取session
            JSONObject jsonObject = loadSession();
            if (jsonObject != null && jsonObject.length() > 0) {
                listener.onComplete(jsonObject);
                return Constants.UI_ACTIVITY;
            } else {
                LogUtils.error("QQ登录, 获取的jsonobject为空!");
            }
        }
        int code = getTencent().login(activity, scope, listener, qrcode);
        logResultCode(code);
        return code;
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
     * 强制二维码登录 or 强制输入账号密码登录
     * @param qrcode 如果true, 强制二维码登录. 如果false, 强制输入账号密码登录
     * @param listener 登录回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *            <li>回调完成后保存session: {@link #initSessionCache(JSONObject)}</li>
     *        </ol>
     */
    public static int loginQrCode$AccountPassword(@NonNull Activity activity, @NonNull String scope,
                                                   boolean qrcode, @NonNull BaseUiListener listener) {
        //强制唤起扫码界面（无论是否安装手Q）
        activity.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
        return login(activity, scope, qrcode, listener);
    }

    /**
     * <a href="https://wiki.connect.qq.com/server-side%E7%99%BB%E5%BD%95%E6%A8%A1%E5%BC%8F">Server-Side登录模式</a> <br />
     * 1.当安装了手机QQ时，SDK会启用手机QQ的特定Activity，通过此Activity完成登录和授权功能。<br />
     * 2.当没有找到此Activity时，SDK会执行Oauth2.0的User-Agent流程，即显示一个包含WebView的对话框， 通过加载登录授权网页来完成登录和授权的交互流程。<br />
     * @param scope 应用需要获得哪些接口的权限，由“，”分隔。例如：SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param listener 登录回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *            <li>回调完成后保存session: {@link #initSessionCache(JSONObject)}</li>
     *        </ol>
     */
    public static int loginServerSide(@NonNull Activity activity, @NonNull String scope, @NonNull BaseUiListener listener) {
        //强制唤起扫码界面（无论是否安装手Q）
//        activity.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
        int code = getTencent().loginServerSide(activity, scope, listener);
        logResultCode(code);
        return code;
    }

    /**
     * <a href="https://wiki.connect.qq.com/oem%e5%ba%94%e7%94%a8%e5%b8%82%e5%9c%ba%e7%99%bb%e5%bd%95-%e6%a0%a1%e9%aa%8c%e7%99%bb%e5%bd%95%e6%80%81">OEM应用市场登录/校验登录态</a> <br />
     * 主要用于OEM应用市场分渠道计费参数需求。
     * @param scope 应用需要获得哪些接口的权限，由“，”分隔。例如：SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param qrcode 是否开启二维码登陆
     * @param registerChannel 注册渠道, 例: "10000144"
     * @param installChannel 安装渠道, 例: "10000144"
     * @param businessId 业务ID "xxxx"
     * @param listener 登录回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *            <li>回调完成后保存session: {@link #initSessionCache(JSONObject)}</li>
     *        </ol>
     */
    public static int loginWithOEM(@NonNull Activity activity, @NonNull String scope, boolean qrcode,
                                    @NonNull String registerChannel, @NonNull String installChannel,
                                    @NonNull String businessId, BaseUiListener listener) {
        //强制唤起扫码界面（无论是否安装手Q）
//        activity.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
        int code = getTencent().loginWithOEM(activity, scope, listener, qrcode, registerChannel,
                installChannel, businessId);
        logResultCode(code);
        return code;
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e6%b3%a8%e9%94%80">注销</a>
     */
    public static void logout() {
        getTencent().logout(CONTEXT);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e6%a0%a1%e9%aa%8ctoken%e5%b9%b6%e7%bb%9f%e8%ae%a1dau">校验token并统计DAU</a>
     * 登录成功以后校验token并统计DAU
     */
    public static void reportDAU() {
        getTencent().reportDAU();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e6%a0%a1%e9%aa%8ctoken">校验token</a>
     * 校验当前的token是否有效。
     */
    protected static void checkLogin(BaseUiListener listener) {
        getTencent().checkLogin(listener);

    }

     /**
     * <a href="https://wiki.connect.qq.com/%e6%94%af%e6%8c%81%e7%ac%ac%e4%b8%89%e6%96%b9%e5%ba%94%e7%94%a8%e6%a8%aa%e5%b1%8f%e4%b8%8b%e7%99%bb%e5%bd%95%e6%8e%88%e6%9d%83%e5%ae%8c%e6%88%90%e5%90%8e%e6%81%a2%e5%a4%8d%e6%a8%aa%e5%b1%8f">支持第三方应用横屏下登录授权完成后恢复横屏</a> <br />
     * @param isLandScape 是否横屏登录
     * @param scope 应用需要获得哪些接口的权限，由“,”分隔。例如：
     *              SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     * @param qrcode 是否开启二维码登录，没有安装手Q时候使用二维码登录，一般用电视等设备。
     *               (如果true使用二维码, 就没有网页输入账号密码登录的界面了)
     * @param enableShowDownloadUrl 是否显示Web页下载链接 (<a href="https://wiki.connect.qq.com/%e6%94%af%e6%8c%81%e5%b1%8f%e8%94%bd%e4%ba%8c%e7%bb%b4%e7%a0%81%e7%99%bb%e5%bd%95web%e9%a1%b5%e4%b8%8b%e8%bd%bd%e9%93%be%e6%8e%a5">支持屏蔽二维码登录Web页下载链接</a>)
     * @param listener 登录回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *            <li>回调完成后保存session: {@link #initSessionCache(JSONObject)}</li>
     *        </ol>
     */
    public static int loginLandScape(@NonNull Activity activity, boolean isLandScape,
                                     @NonNull String scope, boolean qrcode,
                                     boolean enableShowDownloadUrl, @NonNull BaseUiListener listener) {
        boolean isValid = isSessionValid();
        if (isValid) {
            JSONObject jsonObject = loadSession();
            if (jsonObject != null && jsonObject.length() > 0) {
                listener.onComplete(jsonObject);
                return Constants.UI_ACTIVITY;
            } else {
                LogUtils.error("QQ登录, 获取的jsonobject为空!");
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.KEY_RESTORE_LANDSCAPE, isLandScape);
        params.put(Constants.KEY_SCOPE, scope);
        params.put(Constants.KEY_QRCODE, qrcode);
        params.put(Constants.KEY_ENABLE_SHOW_DOWNLOAD_URL, enableShowDownloadUrl);
        int code = getTencent().login(activity, listener, params);
        logResultCode(code);
        return code;
    }



    ///////////////////////////////////////////////////////////////////////////
    // 2.访问OpenApi接口
    // https://wiki.connect.qq.com/%e8%ae%bf%e9%97%aeopenapi%e6%8e%a5%e5%8f%a3
    ///////////////////////////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////////////////////////
    // 3.分享到QQ和QQ空间
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/%e5%88%86%e4%ba%ab%e6%b6%88%e6%81%af%e5%88%b0qq%ef%bc%88%e6%97%a0%e9%9c%80qq%e7%99%bb%e5%bd%95%ef%bc%89">分享消息到QQ（无需QQ登录）</a> <br />
     * 这个方法是分享图文
     * @param title 要分享的标题, 最长30个字符
     * @param summary 要分享的消息摘要, 最长40个字
     * @param targetUrl 这条消息被好友点击后跳转的url, 示例: http://www.qq.com/news/1.html
     * @param imgUrl 分享图片的url或本地路径, 示例: http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif
     * @param appName 手Q客户端顶部, 替换"返回"按钮文字
     * @param extInt 分享额外选项, 默认"显示分享到Qzone按钮=true" & "自动打开分享到Qzone对话框=false"
     *               <ul>
     *                   <li>{@link QQShare#SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN} 自动打开分享到Qzone对话框=true</li>
     *                   <li>{@link QQShare#SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE} 显示分享到Qzone按钮=false</li>
     *               </ul>
     * @param ark 分享携带ARK（手Q轻应用）参数, 可传null
     * @param listener 分享回调 <br />
     * {@link null 注意:} 需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}
     */
    public static void shareToQQImgTxt(@NonNull Activity activity, @NonNull String title, @Nullable String summary,
                                       @NonNull String targetUrl, @Nullable String imgUrl, @Nullable String appName,
                                       @Nullable Integer extInt, @Nullable ArkJsonBean ark, BaseUiListener listener) {
        if (!isSupportShareToQQ()) {
            LogUtils.error("不支持分享到QQ!");
            return;
        }
        Bundle params = new Bundle();
        //图文分享(普通分享), 必传
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);         //必传
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);//必传
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        if (ark != null) params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, GsonUtils.toJson(ark));
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 分享纯图片
     * @param localUrl 本地图片路径, 图片不能大于5M
     * @param appName 手Q客户端顶部, 替换"返回"按钮文字
     * @param extInt 见上一个方法
     * @param ark 分享携带ARK（手Q轻应用）参数, 可传null
     * @param listener 分享回调 <br />
     * {@link null 注意:} 需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}
     */
    public static void shareToQQImg(@NonNull Activity activity, @NonNull String localUrl, @Nullable String appName,
                                    @Nullable Integer extInt, @Nullable ArkJsonBean ark, BaseUiListener listener) {
        if (!isSupportShareToQQ()) {
            LogUtils.error("不支持分享到QQ!");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);//必传
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localUrl);            //必传
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        if (ark != null) params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, GsonUtils.toJson(ark));
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 分享音乐
     * @param title 要分享的标题, 最长30个字符
     * @param summary 要分享的消息摘要, 最长40个字
     * @param targetUrl 这条消息被好友点击后跳转的url, 示例: http://www.qq.com/news/1.html
     * @param imgUrl 分享图片的url或本地路径, 示例: http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif
     * @param audioUrl 音乐链接
     * @param appName 手Q客户端顶部, 替换"返回"按钮文字
     * @param extInt 见上一个方法
     * @param ark 分享携带ARK（手Q轻应用）参数, 可传null
     * @param listener 分享回调 <br />
     * {@link null 注意:} 需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}
     */
    public static void shareToQQAudio(@NonNull Activity activity, @NonNull String title, @Nullable String summary,
                                      @NonNull String targetUrl, @Nullable String imgUrl, @NonNull String audioUrl,
                                      @Nullable String appName, @Nullable Integer extInt, @Nullable ArkJsonBean ark,
                                      BaseUiListener listener) {
        if (!isSupportShareToQQ()) {
            LogUtils.error("不支持分享到QQ!");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);//必传
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);         //必传
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);//必传
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);  //必传
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        if (ark != null) params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, GsonUtils.toJson(ark));
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 分享应用（已废弃）<br />
     * 应用分享后，发送方和接收方在聊天窗口中点击消息气泡即可进入应用的详情页。
     * @deprecated 已经没有 QQShare.SHARE_TO_QQ_TYPE_APP 这个参数了
     */
    @Deprecated
    public static void shareToQQApp(@NonNull Activity activity, @NonNull String title, @Nullable String summary,
                                    @Nullable String imgUrl, @Nullable String appName, @Nullable Integer extInt,
                                    BaseUiListener listener) {
//        if (!isSupportShareToQQ()) {
//            LogUtils.error("不支持分享到QQ!");
//            return;
//        }
//        Bundle params = new Bundle();
//        //                                          已经没有 QQShare.SHARE_TO_QQ_TYPE_APP 这个参数了
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);//必传
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//必传
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
//        if (extInt != null) params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
//        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * 分享成QQ小程序 <br />
     * 可以从外部app分享到手Q为一个QQ小程序，并可以指定预览图、文案、小程序路径
     * @param title 分享的标题, 最长30个字符。如果不填，默认使用小程序名称作为标题
     * @param summary 分享的消息摘要，最长40个字符。若不填，默认使用小程序后台注册的描述作为摘要
     * @param targetUrl 兼容低版本的网页链接
     * @param imageUrl 分享预览封面图的url，或者是本地图的路径
     * @param miniProgramAppId 分享的小程序appid，小程序与当前应用必须为同一个主体
     * @param isRelease true: 正式版, false: 体验版
     * @param miniProgramPath 分享的小程序页面路径，如不需要指定，请填主页路径 (不知是否能传null, 待验证)
     * @param listener 分享回调 <br />
     * {@link null 注意:} 需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}
     */
    public static void shareToQQMiniProgram(@NonNull Activity activity, @Nullable String title, @Nullable String summary,
                                            @NonNull String targetUrl, @Nullable String imageUrl,
                                            @NonNull String miniProgramAppId, @Nullable Boolean isRelease,
                                            String miniProgramPath, BaseUiListener listener) {
        if (!isSupportShareToQQ()) {
            LogUtils.error("不支持分享到QQ!");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_MINI_PROGRAM);//必传
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);             //必传
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);    //必传
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_APPID, miniProgramAppId); //必传
        if (isRelease != null) params.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_TYPE, isRelease ? "3" : "1"); //3表示正式版，1表示体验版
        params.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_PATH, miniProgramPath);   //必传?
        getTencent().shareToQQ(activity, params, listener);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e5%88%86%e4%ba%ab%e5%88%b0qq%e7%a9%ba%e9%97%b4%ef%bc%88%e6%97%a0%e9%9c%80qq%e7%99%bb%e5%bd%95%ef%bc%89">分享到QQ空间（无需QQ登录）</a> <br />
     * 目前支持图文分享、图片分享。
     * @param title 分享的标题，最多200个字符
     * @param summary 分享的摘要，最多600字符
     * @param targetUrl 需要跳转的链接，URL字符串
     * @param imageUrls 分享的图片，以ArrayList<String>的类型传入，以便支持多张图片 (注:图片最多支持9张图片，多余的图片会被丢弃)
     * @param listener 分享回调 <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持。</li>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *        </ol>
     */
    public static void shareToQzone(@NonNull Activity activity, @NonNull String title, @Nullable String summary,
                                    @NonNull String targetUrl, @Nullable ArrayList<String> imageUrls,
                                    BaseUiListener listener) {
        if (!isSupportPushToQZone()) {
            LogUtils.error("不支持分享到QQ!");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);//选填, 不传默认是图文
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);             //必传
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);    //必传
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        getTencent().shareToQzone(activity, params, listener);
    }

    /**
     * 发表说说、视频或上传图片 <br />
     * @param publishType 发表类型:
     *                  <ol>
     *                      <li>{@link QzonePublish#PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD} 发表说说、上传图片(默认)</li>
     *                      <li>{@link QzonePublish#PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO} 发表视频</li>
     *                  </ol>
     * @param summary 说说正文 (传图和传视频接口会过滤第三方传过来的自带描述, 目的为了鼓励用户自行输入有价值信息)
     * @param imagePaths 说说的图片，以ArrayList<String>的类型传入，以便支持多张图片(注: <=9张图片为发表说说，>9张为上传图片到相册)，只支持本地图片
     * @param videoPath 发表的视频，只支持本地地址，发表视频时必填; 上传视频的大小最好控制在100M以内 (因为QQ普通用户上传视频必须在100M以内，黄钻用户可上传1G以内视频，大于1G会直接报错。)
     * @param scene 区分分享场景，用于异化feeds点击行为和小尾巴展示
     * @param callbackInfo 游戏自定义字段，点击分享消息回到游戏时回传给游戏
     * @param listener 分享回调 <br />
     * {@link null 注意:} 需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}
     */
    public static void publishToQzone(@NonNull Activity activity, int publishType, @Nullable String summary,
                                      @Nullable ArrayList<String> imagePaths, @Nullable String videoPath,
                                      @Nullable String scene, @Nullable String callbackInfo,
                                      BaseUiListener listener) {
        if (!isSupportPushToQZone()) {
            LogUtils.error("不支持分享到QQ!");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, publishType);     //必填
        params.putString(QzonePublish.PUBLISH_TO_QZONE_SUMMARY, summary);
        params.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL, imagePaths);
        params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, videoPath);
        if (!TextUtils.isEmpty(scene) && !TextUtils.isEmpty(callbackInfo)) {
            Bundle extParams = new Bundle();
            extParams.putString(QzonePublish.HULIAN_EXTRA_SCENE, scene);        //分享场景
            extParams.putString(QzonePublish.HULIAN_CALL_BACK, callbackInfo);   //回调信息
            params.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, extParams);
        }
        getTencent().publishToQzone(activity, params, listener);
    }

    /**
     * 分享QQ小程序到QQ空间 <br />
     * 可以从外部app分享到手Q空间为一个QQ小程序，并可以指定预览图、文案、小程序路径
     * @param title 分享的标题, 最长30个字符。如果不填，默认使用小程序名称作为标题
     * @param summary 分享的消息摘要，最长40个字符。若不填，默认使用小程序后台注册的描述作为摘要
     * @param imageUrls 分享预览封面图的url，或者是本地图的路径
     * @param miniProgramAppId 分享的小程序appid，小程序与当前应用必须为同一个主体
     * @param isRelease true: 正式版, false: 体验版
     * @param miniProgramPath 分享的小程序页面路径，如不需要指定，请填主页路径
     * @param listener 分享回调 <br />
     * {@link null 注意:} 需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}
     */
    public static void shareToQzoneMiniProgram(@NonNull Activity activity, @Nullable String title,
                                               @Nullable String summary, @NonNull ArrayList<String> imageUrls,
                                               @NonNull String miniProgramAppId, @Nullable Boolean isRelease,
                                               @Nullable String miniProgramPath, BaseUiListener listener) {
        if (!isSupportPushToQZone()) {
            LogUtils.error("不支持分享到QQ!");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_MINI_PROGRAM); //必填
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        params.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_APPID, miniProgramAppId); //必填
        if (isRelease != null) params.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_TYPE, isRelease ? "3" : "1"); //3表示正式版，1表示体验版
        params.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_PATH, miniProgramPath);
        getTencent().shareToQzone(activity, params, listener);
    }

    /**
     * 解析游戏组队透传的参数
     * 小游戏邀请组队参数解析
     * https://wiki.connect.qq.com/%e8%a7%a3%e6%9e%90%e6%b8%b8%e6%88%8f%e7%bb%84%e9%98%9f%e9%80%8f%e4%bc%a0%e7%9a%84%e5%8f%82%e6%95%b0
     */

    /**
     * 分享文件到QQ, 调用系统Intent分享
     * @param file 文件
     * @return 是否跳转到QQ分享界面
     */
    public static boolean shareToQQFileByIntent(Context context, @Nullable File file) {
        if (!com.blankj.utilcode.util.FileUtils.isFile(file)) return false;
        Uri fileUri = UriUtils.file2Uri(file);
        /**
         * 对目标应用临时授权该Uri所代表的文件, 这句代码可选
         * 不写{@link com.tencent.connect.common.Constants.PACKAGE_QQ}而是写"com.tencent.mobileqq"是因为这是Intent分享, 可以不依赖QQ的sdk
         */
        context.grantUriPermission("com.tencent.mobileqq", fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String mimeType = FileUtils.getMimeType(file.getAbsolutePath());
        if (TextUtils.isEmpty(mimeType)) mimeType = "application/*";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, file.getName()); //这参数没啥用, 不会在微信那边显示.
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setType(mimeType);
        intent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            //对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            LogUtils.error("分享文件到QQ失败:", e);
            return false;
        }
    }



    ///////////////////////////////////////////////////////////////////////////
    // 4.设置QQ头像与图片收藏
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/%e8%ae%be%e7%bd%aeqq%e5%a4%b4%e5%83%8f%ef%bc%88%e5%ba%9f%e5%bc%83%ef%bc%89">设置QQ头像（废弃）</a> <br />
     * <b>特别声明</b>：出于信息安全的考虑，本接口仅对可信赖的合作应用开放。已经成功接入“QQ登录”的应用需<a href="https://wiki.open.qq.com/wiki/website/OpenAPI%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7">提交申请</a>以获取访问本接口的权限。
     * @param picturePath 将要设置为QQ头像的图片的本地路径，不支持网络图片，不支持动态图 (gif)，如果传入gif格式的图片，则只显示和使用第一顿的画面
     * @param enterAnim 进入动画id
     * @param exitAnim 退出动画id
     * @param listener 分享回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *            <li>setAvatar使用了一个Activity来让用户调整图片，因此要在项目的AndroidManifest.xml中增加一个Activity配置: <br />
     *                 <code>&lt;activity android:name="com.tencent.plus.ImageActivity" /&gt;</code>
     *            </li>
     *        </ol>
     */
    @Deprecated
    public static void setAvatar(@NonNull Activity activity, @NonNull String picturePath,
                                 @Nullable @AnimRes Integer enterAnim, @Nullable @AnimRes Integer exitAnim,
                                 BaseUiListener listener) {
        //判断用户是否存在登录态并且是否获取了openid
        if (isSessionValid() && getOpenId() != null) {
            Bundle params = new Bundle();
            params.putString(SocialConstants.PARAM_AVATAR_URI, picturePath);
            if (enterAnim != null && exitAnim != null) {
                getTencent().setAvatar(activity, params, listener, enterAnim, exitAnim);
            } else {
                if (exitAnim != null) params.putInt("exitAnim", exitAnim);
                getTencent().setAvatar(activity, params, listener);
            }
        } else {
            String errMsg = "QQ用户可能不是登录态 or 没有获取openid, 设置头像失败!";
            if (listener != null) {
                listener.onError(new UiError(100063, errMsg, errMsg));
            } else LogUtils.error(errMsg);
        }
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%ae%be%e7%bd%aeqq%e5%a4%b4%e5%83%8f">设置QQ头像</a> <br />
     * <b>特别声明</b>：出于信息安全的考虑，本接口仅对可信赖的合作应用开放。已经成功接入“QQ登录”的应用需<a href="https://wiki.open.qq.com/wiki/website/OpenAPI%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7">提交申请</a>以获取访问本接口的权限。
     * @param uri 头像资源路径URI，注意是图片资源
     * @param listener 分享回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要先登录, 才能设置头像.</li>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *        </ol>
     */
    public static void setAvatarByQQ(@NonNull Activity activity, @NonNull Uri uri, BaseUiListener listener) {
        getTencent().setAvatarByQQ(activity, uri, listener);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%ae%be%e7%bd%ae%e5%8a%a8%e6%80%81%e5%a4%b4%e5%83%8f">设置动态头像</a> <br />
     * <b>特别声明</b>：出于信息安全的考虑，本接口仅对可信赖的合作应用开放。已经成功接入“QQ登录”的应用需<a href="https://wiki.open.qq.com/wiki/website/OpenAPI%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7">提交申请</a>以获取访问本接口的权限。
     * @param uri 动态头像资源路径URI，目前只支持GIF。
     * @param listener 分享回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要先登录, 才能设置头像.</li>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *        </ol>
     */
    public static void setDynamicAvatar(@NonNull Activity activity, @NonNull Uri uri, BaseUiListener listener) {
        getTencent().setDynamicAvatar(activity, uri, listener);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%ae%be%e7%bd%aeqq%e8%a1%a8%e6%83%85">设置QQ表情</a> <br />
     * 使用Tencent类中的setEmotions接口设置收藏图片到QQ表情。<br />
     * <b>特别声明</b>：出于信息安全的考虑，本接口仅对可信赖的合作应用开放。已经成功接入“QQ登录”的应用需<a href="https://wiki.open.qq.com/wiki/website/OpenAPI%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7">提交申请</a>以获取访问本接口的权限。
     * @param list 表情的资源路径URI列表。<b>限制</b>：这里接口限制，设置图片Uri不超过9张，每张不超过1M, 一共不能超过3M。
     * @param listener 分享回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:}
     *        <ol>
     *            <li>需要先登录, 才能设置头像.</li>
     *            <li>需要重写方法可参考: {@link #onActivityResult(int, int, Intent)}</li>
     *        </ol>
     */
    public static void setEmotions(@NonNull Activity activity, @NonNull ArrayList<Uri> list, BaseUiListener listener) {
        getTencent().setEmotions(activity, list, listener);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 5.小程序/小游戏 ( QQ8.1.8)
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/%e5%94%a4%e8%b5%b7%e4%b8%bb%e4%bd%93%e5%b0%8f%e7%a8%8b%e5%ba%8f-%e5%b0%8f%e6%b8%b8%e6%88%8f">唤起主体小程序/小游戏</a> <br />
     * 直接从第三方APP主体拉起手Q内其对应的小程序/小游戏，前提是需要注册调起能力和绑定关系。
     * @param miniAppId 调用者期望调起的小程序/小游戏ID
     * @param miniAppPath 显示的小程序/小游戏的页面路径，例如: pages/tabBar/index/index
     * @param miniAppVersion 小程序/小游戏的版本: develop/trial/release
     * @return 函数执行的返回值：
     *         <ul>
     *             <li>0: MiniApp.MINIAPP_SUCCESS #执行成功</li>
     *             <li>-1: MiniApp.MINIAPP_ID_EMPTY #小程序/小游戏ID为空</li>
     *             <li>-2: MiniApp.MINIAPP_SHOULD_DOWNLOAD #需下载新版QQ</li>
     *             <li>-3: MiniApp.MINIAPP_LENGTH_SHORT #小程序/小游戏ID长度过短（保留字段）</li>
     *             <li>-4: MiniApp.MINIAPP_ID_NOT_DIGIT #小程序/小游戏ID非数字</li>
     *             <li>-5: MiniApp.MINIAPP_UNKNOWN_TYPE #错误的类型</li>
     *             <li>-6: MiniApp.MINIAPP_CONTEXT_NULL #上下文为空</li>
     *             <li>-7: MiniApp.MINIAPP_VERSION_WRONG #小程序/小游戏版本有误</li>
     *         </ul>
     */
    public static int startMiniApp(@NonNull Activity activity, @NonNull String miniAppId,
                                   @Nullable String miniAppPath, @NonNull String miniAppVersion) {
        return getTencent().startMiniApp(activity, miniAppId, miniAppPath, miniAppVersion);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 6.授权管理
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/%e8%b7%b3%e8%bd%ac%e5%88%b0%e6%89%8bq%e7%9a%84%e5%ba%94%e7%94%a8%e6%8e%88%e6%9d%83%e8%af%a6%e6%83%85%e9%a1%b5">跳转到手Q的应用授权详情页</a> <br />
     * 第三方应用登录QQ授权后，可跳转至手q的应用授权详情页，便于管理授权。
     * @param callback 回调接口, 使用见上方链接.
     */
    public static void startAuthManagePage(@NonNull Activity activity, @NonNull IApiCallback callback) {
        getTencent().startAuthManagePage(activity, callback);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 7.其他能力
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/%e5%bd%93%e5%89%8d%e4%bc%9a%e8%af%9d%e6%98%af%e5%90%a6%e6%9c%89%e6%95%88">当前会话是否有效</a> <br />
     * 判断当前会话是否是有效，无效可以调用登录接口、有效可以退出登录。
     */
    public static boolean isSessionValid() {
         return getTencent().isSessionValid();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%8e%b7%e5%8f%96appid">获取APPID</a> <br />
     * 获取当前应用的互联APPID。
     */
    public static String getAppId() {
         return getTencent().getAppId();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%8e%b7%e5%8f%96accesstoken">获取AccessToken</a> <br />
     * 登录成功以后，获取当前应用的AccessToken。
     */
    public static String getAccessToken() {
        return getTencent().getAccessToken();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%8e%b7%e5%8f%96token%e8%bf%87%e6%9c%9f%e6%97%b6%e9%97%b4">获取token过期时间</a> <br />
     * 登录成功以后，获取当前应用的Token过期时间。
     * @return 当前应用的Token过期时间（单位秒）。
     */
    public static long getExpiresIn() {
        return getTencent().getExpiresIn();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%E8%8E%B7%E5%8F%96%E7%99%BB%E5%BD%95%E7%94%A8%E6%88%B7openid">获取登录用户openid</a> <br />
     * openId为对当前应用进行授权的QQ用户的身份识别码，应用应将openId与应用中的用户账号进行关系绑定，以此来支持多账号。
     */
    public static String getOpenId() {
        return getTencent().getOpenId();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%ae%be%e7%bd%aetoken%e5%92%8c%e8%bf%87%e6%9c%9f%e6%97%b6%e9%97%b4">设置token和过期时间</a> <br />
     * 使用登陆接口登陆成功以后，在登陆的回调接口保存登陆返回的token和过期时间。
     * @param token 登录成功返回的token。
     * @param expiresIn 登录成功返回的过期时间，单位秒 <br />
     * {@link null 注意:} tencent.initSessionCache(...) 里面有setAccessToken(..)的操作
     */
    public static void setAccessToken(@NonNull String token, @NonNull String expiresIn) {
        if (token != null && expiresIn != null) getTencent().setAccessToken(token, expiresIn);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%ae%be%e7%bd%aeopenid">设置OpenId</a> <br />
     * @param openId 使用登陆接口登陆成功以后，在登陆的回调接口保存登陆返回的OpenId。<br />
     * {@link null 注意:} tencent.initSessionCache(...) 里面有setOpenId(..)的操作
     */
    public static void setOpenId(@NonNull String openId) {
        getTencent().setOpenId(openId);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e5%88%a4%e6%96%ad%e6%98%af%e5%90%a6%e7%99%bb%e5%bd%95%e4%b8%94%e8%8e%b7%e5%8f%96openid">判断是否登录且获取OpenId</a> <br />
     * 判断当前是否登录且应用已经获取OpenId。
     * <pre>
     *     if (QQUtils.isReady()) {
     *         String openId = QQUtils.getOpenId();
     *     } </pre>
     * @return true：已经登录且获取OpenId。false：没有登录获取没有获取OpenId。
     */
    public static boolean isReady() {
        return getTencent().isReady();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%8e%b7%e5%8f%96token%e5%af%b9%e8%b1%a1">获取Token对象</a> <br />
     * 登录后需要调用获取用户信息、设置头像等接口时，需要登录返回的Token数据时，通过改接口获取。
     * @param listener 登录回调, 可见示例: {@link #baseUiListener} <br />
     * {@link null 注意:} 回调完成后保存session: {@link #initSessionCache(JSONObject)}
     */
    public static void getUserInfo(@NonNull BaseUiListener listener) {
        UserInfo userInfo = new UserInfo(CONTEXT, getTencent().getQQToken());
        userInfo.getUserInfo(listener);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e6%98%af%e5%90%a6%e6%94%af%e6%8c%81sso%e7%99%bb%e5%bd%95">是否支持SSO登录</a> <br />
     * 判断当前是否支持SSO登录。
     */
    public static boolean isSupportSSOLogin(@NonNull Activity activity) {
        return getTencent().isSupportSSOLogin(activity);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e6%98%af%e5%90%a6%e6%94%af%e6%8c%81%e5%88%86%e4%ba%ab%e5%88%b0qq">是否支持分享到QQ</a> <br />
     *  判断当前是否支持第三方分享到QQ。
     */
    public static boolean isSupportShareToQQ() {
        return Tencent.isSupportShareToQQ(CONTEXT);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e6%98%af%e5%90%a6%e6%94%af%e6%8c%81%e5%88%86%e4%ba%ab%e5%88%b0qq%e7%a9%ba%e9%97%b4">是否支持分享到QQ空间</a> <br />
     * 判断当前是否支持第三方分享到QQ空间。
     */
    public static boolean isSupportPushToQZone() {
        return Tencent.isSupportPushToQZone(CONTEXT);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e4%bf%9d%e5%ad%98session%e4%bc%9a%e8%af%9d%e4%bf%a1%e6%81%af">保存Session会话信息</a> <br />
     * 登录成功以后，保存session会话信息如token, openid等信息到SharePreferecne中。
     * @param response 登录后返回的token、openid等信息json。
     */
    public static void saveSession(@Nullable JSONObject response) {
        getTencent().saveSession(response);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e8%af%bb%e5%8f%96session">读取session</a> <br />
     * 从本地获取第三方应用等token、openid信息等session信息的接口。<br />
     * {@link null 注意：}应该先调用Tencent.checkLogin()，返回token信息有效后再调用这个接口。否则如果token信息无效，调用该接口没有意义。
     * @return 登录后返回的token、openid等信息json。
     */
    public static JSONObject loadSession() {
        return getTencent().loadSession(tencentAppId);
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e5%88%9d%e5%a7%8b%e5%8c%96session">初始化session</a> <br />
     * 初始化SDK中的token信息缓存，调用完这个接口后，Tencent里才有token信息的缓存，才能调用其他的open api。<br />
     * {@link null 注意：}应该先调用Tencent.checkLogin() ，返回token信息有效后再调用这个接口。否则如果token信息无效，调用该接口没有意义。
     * @param response 登录后返回的token、openid等信息json。<br />
     *                 <br />
     * 保存token & openid, 保存后 tencent.isSessionValid() = true <br />
     * 获取到回调后, 再调用这个方法
     */
    public static void initSessionCache(@Nullable JSONObject response) {
        if (response == null) return;
        getTencent().initSessionCache(response);

        //保存Session会话信息
        saveSession(response);

        //登录成功以后校验token并统计DAU
        reportDAU();
    }

    /**
     * <a href="https://wiki.connect.qq.com/%e4%b8%9a%e5%8a%a1%e8%87%aa%e5%ae%9a%e4%b9%89%e8%be%93%e5%87%ba%e4%ba%92%e8%81%94sdk%e6%97%a5%e5%bf%97">业务自定义输出互联sdk日志</a> <br />
     * android10加强了权限限制和隐私保护，限制用户在未获取到存储权限的时候操作公共目录下的文件。提供此接口让第三方应用可以自定义互联sdk日志的输出。
     * @param tracer 用户自定义日志输出接口
     */
    public static void setCustomLogger(@Nullable Tracer tracer) {
        Tencent.setCustomLogger(tracer);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 剩余的api, 可能都调用无效了
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://wiki.connect.qq.com/%E8%81%8A%E5%A4%A9">聊天</a>
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
    public static int startIMAio(@NonNull Activity activity, @NonNull String targetQq) {
        return getTencent().startIMAio(activity, targetQq, activity.getPackageName());
    }

    /**
     * <a href="https://wiki.connect.qq.com/%E8%AF%AD%E9%9F%B3">语音</a>
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
    public static int startIMAudio(@NonNull Activity activity, @NonNull String targetQq) {
        return getTencent().startIMAudio(activity, targetQq, activity.getPackageName());
    }

    /**
     * <a href="https://wiki.connect.qq.com/%E8%A7%86%E9%A2%91">视频</a>
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
    public static int startIMVideo(@NonNull Activity activity, @NonNull String targetQq) {
        return getTencent().startIMVideo(activity, targetQq, activity.getPackageName());
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



    /**
     * 处理QQ操作返回, 将以下注释的代码复制到自己的页面中, 并将注释的内容取消注释
     * <ol>
     *     <li>不用判断requestCode == REQUEST_LOGIN || requestCode == REQUEST_APPBAR(APP内应用吧登录) 等等, 因为还有其它的操作也要回调这个方法。</li>
     *     <li>也不必担心Sdk的弱引用回调监听被系统回收, 毕竟现在手机内存都够大。</li>
     *     <li>所以{@link Tencent#onActivityResultData(int, int, Intent, IUiListener) Tencent.onActivityResultData(...)}的最后1个参数直接传{@link null null}, 原因见Sdk方法调用流程: <br />
     *         {@link Tencent#onActivityResultData(int, int, Intent, IUiListener)} <br />
     *         {@link com.tencent.open.utils.l#a(int)} <br />
     *         {@link com.tencent.connect.common.Constants}</li>
     * </ol>
     */
//    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        LogUtils.errorFormat("QQ操作返回: requestCode=%d, resultCode=%d", requestCode, resultCode);
//        Tencent.onActivityResultData(requestCode, resultCode, data, null);
//        super.onActivityResult(requestCode, resultCode, data);
    }
}
