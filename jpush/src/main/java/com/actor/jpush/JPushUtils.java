package com.actor.jpush;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;

import java.util.Set;

import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.DefaultPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.MultiActionsNotificationBuilder;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.data.JPushConfig;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.ups.JPushUPSManager;
import cn.jpush.android.ups.UPSRegisterCallBack;
import cn.jpush.android.ups.UPSTurnCallBack;
import cn.jpush.android.ups.UPSUnRegisterCallBack;

/**
 * description: <a href="https://www.jiguang.cn/" target="_blank">极光推送</a>,
 * <a href="https://docs.jiguang.cn/jpush/resources" target="_blank">资源下载(SDK&Demo)</a>
 * <ul>
 *     <li>1.<a href="https://www.jiguang.cn/accounts/register_phone" target="_blank">注册并添加App</a></li>
 *     <li>2.<a href="https://docs.jiguang.cn/jpush/client/Android/android_jghgzy" target="_blank">隐私政策与合规引导</a>(如果App要上架, 可参考这个隐私政策)</li>
 *     <li>
 *         3.<a href="https://docs.jiguang.cn/jpush/client/Android/android_jghgzy#sdk-权限控制">权限配置</a>: 集成本依赖后, 基本的权限已自动配置!
 *         <ol>
 *             <li>
 *                 如您需要接入地理围栏业务，建议集成以下权限（可选）<br />
 *                 <b>“</b>官方aar中默认已经自动集成了地理围栏业务的定位权限(ACCESS_COARSE_LOCATION,
 *                 ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION), 我已经移除了, 无语...<br />
 *                 可点击 {@link cn.jpush.client.android.R.style.JPushTheme} 下方的<b>AndroidManifest.xml</b>查看它明明已集成...<b>”</b> <br />
 *                 <b>注意:</b> 如果你需要以上3个定位权限, 需要在清单文件中配置: <br />
 *                 &lt;!--允许应用获取粗略位置--> <br />
 *                 &lt;uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /&gt; <br />
 *                 &lt;!--允许应用获取精准位置--> <br />
 *                 &lt;uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /&gt; <br />
 *                 &lt;!--Android Q适配 应用后台定位权限 --> <br />
 *                 &lt;uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" /&gt;
 *             </li>
 *             <li>
 *                 如您需要对应设备通知相关的能力，建议集成以下权限（可选）<br />
 *                 <b>“</b>官方aar中明明已经集成了设备通知相关能力的权限(CHANGE_BADGE, BADGE_ICON, VIBRATE), 官方文档也不改, 还是继续写着要手动集成...<br />
 *                 可点击 {@link cn.jpush.client.android.R.style.JPushTheme} 下方的<b>AndroidManifest.xml</b>查看它明明已集成...<b>”</b>
 *             </li>
 *             <li>
 *                 用于生成准确的推送目标 ID（极光RID)，保证消息推送的精准送达，合理分配厂商推送通道，以提升消息送达率（可选） <br />
 *                 <b>“</b>官方aar中明明已经集成了生成准确的推送目标 ID的权限(READ_PHONE_STATE,
 *                 QUERY_ALL_PACKAGES, GET_TASKS, ACCESS_WIFI_STATE), 官方文档也不改, 还是继续写着要手动集成...<br />
 *                 可点击 {@link cn.jpush.client.android.R.style.JPushTheme} 下方的<b>AndroidManifest.xml</b>查看它明明已集成...<b>”</b>
 *             </li>
 *         </ol>
 *     </li>
 *     <br />
 *     <li>4.<a href="https://docs.jiguang.cn/jpush/client/Android/protection_rights" target="_blank">关于进一步加强用户权益保护的通知</a>(稍微了解即可)</li>
 *     <li>
 *         5.项目的build.gradle中<a href="https://docs.jiguang.cn/jpush/client/Android/android_guide#配置-mavencentral-支持" target="_blank">配置 mavenCentral 支持</a>
 *         <ol>
 *             <li>
 *                 在工程build.gradle配置脚本中buildscript和allprojects段中添加 <br />
 *                 mavenCentral()
 *             </li>
 *             <li>
 *                 在模块build.gradle中配置 <br />
 *                 android { <br />
 *                     &emsp; defaultConfig { <br />
 *                     &emsp;&emsp; ndk {//选择要添加的对应 cpu 类型的 .so 库 <br />
 *                         &emsp;&emsp;&emsp; //"armeabi-v7a", "arm64-v8a", "armeabi", "x86", "x86_64", "mips", "mips64" <br />
 *                         &emsp;&emsp;&emsp; abiFilters "armeabi-v7a", "arm64-v8a" <br />
 *                     &emsp;&emsp; } <br />
 *                     &emsp;&emsp; manifestPlaceholders = [ <br />
 *                         &emsp;&emsp;&emsp; JPUSH_PKGNAME : applicationId, <br />
 *                         &emsp;&emsp;&emsp; JPUSH_APPKEY : "你的 Appkey ", //JPush 上注册的包名对应的 Appkey. <br />
 *                         &emsp;&emsp;&emsp; JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.(下载渠道) <br />
 *                     &emsp;&emsp;] <br />
 *                     &emsp; } <br />
 *                 } <br />
 *                 dependencies { <br />
 *                     &emsp; 可点击查看<a href="https://repo.maven.apache.org/maven2/cn/jiguang/sdk/jpush/">最新版本号</a> <br />
 *                     &emsp; //https://docs.jiguang.cn/jpush/client/Android/android_guide#配置-mavencentral-支持 <br />
 *                     &emsp; implementation 'cn.jiguang.sdk:jpush:5.2.2'   //必选, 注意：5.0.0 版本开始可以自动拉取 JCore 包，无需另外配置 <br />
 *                     &emsp; implementation 'cn.jiguang.sdk:joperate:2.0.2'//可选，集成极光分析SDK后，即可支持行为触发推送消息、推送转化率统计，用户行为分析和用户标签等功能 <br />
 *                     //另外还可集成Google Play版本, 具体见文档 <br />
 *                 }
 *             </li>
 *         </ol>
 *     </li>
 *     <li>
 *         7.<a href="https://docs.jiguang.cn/jpush/client/Android/android_guide#集成-jpush-android-sdk-的混淆">集成 JPush Android SDK 的混淆</a>:
 *         集成本依赖后已自动混淆, 如果你需要混淆代码, 打开 minifyEnabled true 即可. <br />
 *         <b>但是如果你要混淆资源的话, 请点击: </b><a href="https://docs.jiguang.cn/jpush/client/Android/android_guide#关于资源混淆">关于资源混淆</a>查看!
 *     </li>
 *     <li>
 *         8.示例使用 <br />
 *         <ol>
 *             <li>
 *                 Application中初始化极光推送 <br />
 *                 {@link JPushUtils#setDebugMode(boolean) JPushUtils.setDebugMode(isDebugMode())};//设置调试模式,在 init 接口之前调用 <br />
 *                 {@link JPushUtils#stopPush(Context) JPushUtils.stopPush(Context)};//停止推送, 防止未登录就接收到消息 (可选) <br />
 *             </li>
 *             <li>
 *                 如果用户还未同意隐私政策, 让用户同意隐私政策 & 初始化 <br />
 *                 {@link JPushUtils#setAuth(Context, boolean) JPushUtils.setAuth(context, true)};//用户同意隐私政策 <br />
 *                 {@link JPushUtils#init(Context, JPushConfig) JPushUtils.init(Context, JPushConfig)};//初始化 <br />
 *             </li>
 *             <li>
 *                 登录成功后 <br />
 *                 {@link JPushUtils#setAlias(Context, int, String) JPushUtils.setAlias(context, 0, username)};//设置别名, 还有其他推送方式(注意别名设置可能失败, 要多次设置) <br />
 *                 {@link JPushUtils#resumePush(Context) JPushUtils.resumePush(context)};//恢复推送服务 <br />
 *                 {@link JPushUtils#stopPush(Context) JPushUtils.stopPush(context)};//退出app时, 停止推送 <br />
 *             </li>
 *             <li>
 *                 接收消息处理结果回调, 见: {@link PushMessageService}
 *             </li>
 *         </ol>
 *     </li>
 *     <li>
 *         9.<a href="https://docs.jiguang.cn/jpush/client/Android/android_api#notificationchannel-%E9%85%8D%E7%BD%AE" target="_blank">NotificationChannel 配置</a>
 *     </li>
 *     <li>
 *         10.<a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%87%AA%E5%AE%9A%E4%B9%89%E9%93%83%E5%A3%B0%E9%85%8D%E7%BD%AEhttps://docs.jiguang.cn/jpush/client/Android/android_api#%E8%87%AA%E5%AE%9A%E4%B9%89%E9%93%83%E5%A3%B0%E9%85%8D%E7%BD%AE" target="_blank">自定义铃声配置</a>
 *     </li>
 *     <li>
 *         11.<a href="http://docs.jiguang.cn/jpush/client/Android/android_api/#_248" target="_blank">错误码</a>
 *     </li>
 * </ul>
 *
 * @author    : ldf
 * @update    : 2023/8/4 on 20:24
 */
public class JPushUtils {

    ///////////////////////////////////////////////////////////////////////////
    // 隐私确认接口与 SDK 推送业务功能启用
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_jghgzy#%E9%9A%90%E7%A7%81%E7%A1%AE%E8%AE%A4%E6%8E%A5%E5%8F%A3%E4%B8%8E-sdk-%E6%8E%A8%E9%80%81%E4%B8%9A%E5%8A%A1%E5%8A%9F%E8%83%BD%E5%90%AF%E7%94%A8" target="_blank">隐私确认接口与 SDK 推送业务功能启用</a> <br />
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#接口使用说明" target="_blank">同意隐私政策</a> <br />
     * 关于 APP 隐私政策建议和说明，具体可以参考 <a href="https://docs.jiguang.cn/compliance_guide/app_compliance_guide/app_compliance_guide1" target="_blank">如何草拟合规的隐私政策</a>。 <br />
     * @param auth 用户是否同意隐私政策            ↗
     */
    public static void setAuth(@NonNull Context context, boolean auth) {
        JCollectionAuth.setAuth(context, auth);
    }

    /**
     * 用户是否同意隐私政策 (没有官方api) <br />
     * 上方{@link #setAuth(Context, boolean)}后, 会走:
     * <ol>
     *     <li>{@link JCollectionAuth#setAuth(Context, boolean)}</li>
     *     <li>{@link cn.jiguang.api.JCoreManager#onEvent(Context, String, int, String, android.os.Bundle, Object...)}, (参3=96, 参6=auth)</li>
     *     <li>{@link cn.jiguang.api.JCoreManager#onEvent(Context, String, int, boolean, String, android.os.Bundle, Object...)}, (参3=96, 参4=false, 参7=auth)</li>
     * </ol>
     */
    protected static boolean isAuth() {
//        return cn.jiguang.cg.a.a();
//        return cn.jiguang.cn.a.a();
//        return cn.jiguang.cp.a.b();
        return false;   //↑ 经常变动, 不可靠. 如果要判断是否已经同意, 应该自己sp存储!
    }



    ///////////////////////////////////////////////////////////////////////////
    // 应用自启动开关控制接口
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_jghgzy#应用自启动开关控制接口" target="_blank">应用自启动开关控制接口</a> <br />
     * 该接口允许开发控制是否允许SDK自启动，SDK自启动主要用于优化SDK长连接，开发者可以按需配置。<br />
     * <b>在SDK初始化前，通过该接口配置自启开关。</b><br />
     * SDK自启动状态默认为开启
     * @param enable 是否开启自启动
     */
    public static void enableAutoWakeup(@NonNull Context context, boolean enable) {
        JCollectionAuth.enableAutoWakeup(context, enable);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 智能推送开关
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_jghgzy#智能推送开关" target="_blank">智能推送开关</a> <br />
     * 为了减少无用推送信息对您用户的打扰，极光向您提供智能推送功能，通过该信息推荐更符合您用户需要的推送内容，您可以通过以下接口开启或关闭这项功能。
     * @param enable 是否开启
     */
    public static void setSmartPushEnable(@NonNull Context context, boolean enable) {
        JPushInterface.setSmartPushEnable(context, enable);
    }





    ///////////////////////////////////////////////////////////////////////////
    // 统一推送服务标准接口
    // 统一推送服务（Unified Push Service，简称 UPS）技术标准，旨在为国内的消息推送服务建立统一的标准，为终端用户提供更好的手机使用体验，为应用开发者更好的解决消息推送需求。
    // 从 JPush 3.5.8 版本开始，新增 cn.jpush.android.ups.JPushUPSManager 类，该类提供符合 UPS 标准的接口。
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%B3%A8%E5%86%8C%E6%8E%A5%E5%8F%A3">注册接口</a> <br />
     * 注1：该接口与JPushInterface.init接口不建议混用，可直接使用该接口代替JPushInterface.init接口。 <br />
     * 注2：manifest中配置的appkey与该接口传入appkey建议保持一致，如不一致则以manifest中配置的接口为准。 <br />
     * 注3：如果manifest中appkey配置为空，则以该接口传入的appkey为准。
     * @param appID manifest中配置的appkey
     * @param callback 该接口的结果回调，状态码为0则说明调用成功，其它值均为失败
     */
    public static void registerToken(@NonNull Context context, String appID, @Nullable UPSRegisterCallBack callback) {
        JPushUPSManager.registerToken(context, appID, null, null, callback);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%8F%8D%E6%B3%A8%E5%86%8C%E6%8E%A5%E5%8F%A3">反注册接口</a> <br />
     * 注：调用此接口后，会停用所有Push SDK提供的功能。需通过registerToken接口或者重新安装应用才可恢复。
     * @param callback 反注册结果。状态码为0则说明调用成功，其它值均为失败
     */
    public static void unRegisterToken(@NonNull Context context, @Nullable UPSUnRegisterCallBack callback) {
        JPushUPSManager.unRegisterToken(context, callback);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%81%9C%E6%AD%A2%E6%8E%A8%E9%80%81%E6%9C%8D%E5%8A%A1">停止推送服务</a> <br />
     * 调用了本 API 后，JPush 推送服务完全被停止。具体表现为：
     * <ul>
     *     <li>收不到推送消息</li>
     *     <li>极光推送所有的其他 API 调用都无效，需要调用 {@link #turnOnPush(Context, UPSTurnCallBack)} 恢复。</li>
     * </ul>
     * @param callback 关闭服务结果。状态码为0则说明调用成功，其它值均为失败
     */
    public static void turnOffPush(@NonNull Context context, UPSTurnCallBack callback) {
        JPushUPSManager.turnOffPush(context, callback);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%81%A2%E5%A4%8D%E6%8E%A8%E9%80%81%E6%9C%8D%E5%8A%A1">恢复推送服务</a> <br />
     * 调用了此 API 后，极光推送完全恢复正常工作。
     * @param callback 恢复服务结果。状态码为0则说明调用成功，其它值均为失败
     */
    public static void turnOnPush(@NonNull Context context, UPSTurnCallBack callback) {
        JPushUPSManager.turnOnPush(context, callback);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 设置调试模式 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%AE%BE%E7%BD%AE%E8%B0%83%E8%AF%95%E6%A8%A1%E5%BC%8F-api">设置调试模式-api</a> <br />
     * 注：该接口需在 init 接口之前调用，避免出现部分日志没打印的情况。多进程情况下建议在自定义的Application 中 onCreate 中调用。
     * @param debug 为 true 则会打印 debug 级别的日志，false 则只会打印 warning 级别以上的日志
     */
    public static void setDebugMode(boolean debug) {
        JPushInterface.setDebugMode(debug);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 初始化与开启推送服务 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%88%9D%E5%A7%8B%E5%8C%96%E4%B8%8E%E5%BC%80%E5%90%AF%E6%8E%A8%E9%80%81%E6%9C%8D%E5%8A%A1-api" target="_blank">初始化推送服务-api</a> <br />
     * {@link null 注意：}必须要同意了隐私政策后，才可以初始化。 <br />
     * 该 API 支持动态设置极光 AppKey 与各厂商 AppId。 <br />
     * 注：如使用该接口配置 AppKey 进行初始化，则 build.gradle 文件中 JPUSH_APPKEY 则不需再配置，即 JPUSH_APPKEY : ""。
     * @param config 如果你在build.gradle中设置了 JPUSH_APPKEY 等, 并且没配置厂商通道的话, config可传null
     */
    public static void init(@NonNull Context context, @Nullable JPushConfig config) {
        if (config == null) {
            JPushInterface.init(context);
        } else JPushInterface.init(context, config);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 停止与恢复推送服务 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%81%9C%E6%AD%A2%E6%8E%A8%E9%80%81%E6%9C%8D%E5%8A%A1-1">停止推送服务</a> <br />
     * 停止推送, 推送服务完全被停止。具体表现为： <br />
     * 1.收不到推送消息 <br />
     * 2.极光推送所有的其他 API 调用都无效，不能通过 JPushInterface.init 恢复，需要调用 resumePush 恢复。
     */
    public static void stopPush(@NonNull Context context) {
        JPushInterface.stopPush(context);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%81%A2%E5%A4%8D%E6%8E%A8%E9%80%81%E6%9C%8D%E5%8A%A1-1">恢复推送服务</a> <br />
     * 调用了此 API 后，极光推送完全恢复正常工作。
     */
    public static void resumePush(@NonNull Context context) {
        JPushInterface.resumePush(context);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%A3%80%E6%9F%A5%E6%8E%A8%E9%80%81%E6%98%AF%E5%90%A6%E8%A2%AB%E5%81%9C%E6%AD%A2">检查推送是否被停止</a> <br />
     */
    public static boolean isPushStopped(@NonNull Context context) {
        return JPushInterface.isPushStopped(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 申请必须权限接口（ Android 6.0 及以上）
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E7%94%B3%E8%AF%B7%E5%BF%85%E9%A1%BB%E6%9D%83%E9%99%90%E6%8E%A5%E5%8F%A3%EF%BC%88-android-60-%E5%8F%8A%E4%BB%A5%E4%B8%8A%EF%BC%89">申请必须权限接口（ Android 6.0 及以上）</a> <br />
     * 在 Android 6.0 及以上的系统上，需要去请求JPush SDK 必须用到的权限。<br />
     * "android.permission.POST_NOTIFICATIONS"
     */
    public static void requestRequiredPermission(@NonNull Activity activity) {
        JPushInterface.requestRequiredPermission(activity);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E7%94%B3%E8%AF%B7%E5%8F%AF%E9%80%89%E6%9D%83%E9%99%90%E6%8E%A5%E5%8F%A3%EF%BC%88-android-60-%E5%8F%8A%E4%BB%A5%E4%B8%8A%EF%BC%89">申请可选权限接口（ Android 6.0 及以上）</a> <br />
     * 在 Android 6.0 及以上的系统上，需要去请求一些用到的权限，JPush SDK 用到的一些需要请求如下权限，因为需要这些权限使统计更加精准，功能更加丰富，建议开发者调用。<br />
     * "android.permission.READ_PHONE_STATE" <br />
     * "android.permission.WRITE_EXTERNAL_STORAGE" <br />
     * "android.permission.READ_EXTERNAL_STORAGE" <br />
     * "android.permission.ACCESS_FINE_LOCATION"    //如果你没有使用地理围栏功能, 不建议调用这个方法!!!
     */
    public static void requestPermission(@NonNull Context context) {
        JPushInterface.requestPermission(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 别名与标签 API
    // 新别名与标签接口支持增删改查的功能，从 3.0.7 版本开始支持，老版本别名与标签的接口从 3.0.7 版本开始不再维护。
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E7%AD%9B%E9%80%89%E6%9C%89%E6%95%88%E6%A0%87%E7%AD%BE">筛选有效标签</a> <br />
     * 设置 tags 时，如果其中一个 tag 无效，则整个设置过程失败。 <br />
     * 调用本方法 filterValidTags 来过滤掉无效的 tags，得到有效的 tags，再调用 JPush SDK 的 set tags / alias 方法。 <br />
     * 过滤规则见: {@link cn.jpush.android.ad.h#a(String)}
     * @param tags 标签tag, 例: game, old_page, women...
     * @return 有效的 tag 集合。
     */
    @Nullable
    public static Set<String> filterValidTags(Set<String> tags) {
        return JPushInterface.filterValidTags(tags);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%AE%BE%E7%BD%AE%E5%88%AB%E5%90%8D">设置别名</a> <br />
     * 设置一个别名, 用于别名推送 <br />
     * {@link null 注意:}
     * <ol>
     *     <li>极光于 2020/03/10 对「别名设置」的上限进行限制，最多允许绑定 10 个设备。</li>
     *     <li>需要理解的是，这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。</li>
     *     <li>init 后直接 set 操作有极大可能导致失败，可能会在回调里拿到 6022,6002 等，测试的时候可以做个 7、8 秒的延时，正式业务里一般配合用户注册使用，延时基本上够用</li>
     *     <li>当目标 alias 下已经有 10 个设备时，再去尝试绑定可能会报 6002,6022，可以尝试下后台调用我们的 服务器删除接口 清除下全部绑定关系。</li>
     * </ol>
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性，推荐每次都用不同的数字序号。<br />
     *                 设置后回调位置: {@link PushMessageService#onAliasOperatorResult(Context, JPushMessage)} =>
     *                 {@link JPushMessage#getSequence()}
     * @param alias 每次调用设置有效的别名，覆盖之前的设置。 <br />
     *              有效的别名组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。 <br />
     *              限制：alias 命名长度限制为 40 字节。（判断长度需采用 UTF-8 编码）
     */
    public static void setAlias(@NonNull Context context, int sequence, String alias) {
        if (ExampleUtil.isValidTagAndAlias(alias)) {
//            JPushInterface.setAlias(context, alias, callback);    //过时
            JPushInterface.setAlias(context, sequence, alias);
        } else {
            LogUtils.errorFormat("别名设置非法: sequence=%d, alias=%s", sequence, alias);
        }
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%88%A0%E9%99%A4%E5%88%AB%E5%90%8D">删除别名</a> <br />
     * {@link null 注意:}
     * <ul>
     *     <li>此接口只能清除当前设备与目标 alias 的绑定关系，如果你想清除目标 alias 与所有设备的绑定关系，请调 <a href="https://docs.jiguang.cn/jpush/server/push/rest_api_v3_device#%E5%88%A0%E9%99%A4%E5%88%AB%E5%90%8D" target="_blank">服务器删除接口</a>。</li>
     * </ul>
     * @param sequence 作用见上一个方法↑
     */
    public static void deleteAlias(@NonNull Context context, int sequence) {
        JPushInterface.deleteAlias(context, sequence);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%9F%A5%E8%AF%A2%E5%88%AB%E5%90%8D-1">查询别名</a> <br />
     * @param sequence 作用见上一个方法↑
     */
    public static void getAlias(@NonNull Context context, int sequence) {
        JPushInterface.getAlias(context, sequence);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%AE%BE%E7%BD%AE%E6%A0%87%E7%AD%BE">设置标签</a> <br />
     * 需要理解的是，这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。<br />
     * 为安装了应用程序的用户，打上标签。其目的主要是方便开发者根据标签，来批量下发 Push 消息。<br />
     * 可为每个用户打多个标签<br />
     * @param sequence 作用见上一个方法↑
     * @param tags 标签。举例：game, old_man, women:
     * <ol>
     *     <li>每次调用至少设置一个 tag，覆盖之前的设置，不是新增。</li>
     *     <li>有效的标签组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。</li>
     *     <li>限制：每个 tag 命名长度限制为 40 字节，最多支持设置 1000 个 tag，且单次操作总长度不得超过 5000 字节。（判断长度需采用 UTF-8 编码）</li>
     *     <li>单个设备最多支持设置 1000 个 tag。App 全局 tag 数量无限制。</li>
     * </ol>
     */
    public static void setTags(@NonNull Context context, int sequence, Set<String> tags) {
        Set<String> filter = filterValidTags(tags);
//        JPushInterface.setTags(context, filter, callback);    //过时
        JPushInterface.setTags(context, sequence, filter);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%96%B0%E5%A2%9E%E6%A0%87%E7%AD%BE">新增标签</a> <br />
     * @param sequence 作用见上一个方法↑
     * @param tags 作用见上一个方法↑
     */
    public static void addTags(@NonNull Context context, int sequence, Set<String> tags) {
        Set<String> filter = filterValidTags(tags);
        JPushInterface.addTags(context, sequence, filter);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%88%A0%E9%99%A4%E6%A0%87%E7%AD%BE">删除标签</a> <br />
     * @param sequence 作用见上一个方法↑
     * @param tags 作用见上一个方法↑
     */
    public static void deleteTags(@NonNull Context context, int sequence, Set<String> tags) {
        Set<String> filter = filterValidTags(tags);
        JPushInterface.deleteTags(context, sequence, filter);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%B8%85%E9%99%A4%E6%89%80%E6%9C%89%E6%A0%87%E7%AD%BE">清除所有标签</a> <br />
     * @param sequence 作用见上一个方法↑
     */
    public static void cleanTags(@NonNull Context context, int sequence) {
        JPushInterface.cleanTags(context, sequence);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%9F%A5%E8%AF%A2%E6%89%80%E6%9C%89%E6%A0%87%E7%AD%BE">查询所有标签</a> <br />
     * @param sequence 作用见上一个方法↑
     */
    public static void getAllTags(@NonNull Context context, int sequence) {
        JPushInterface.getAllTags(context, sequence);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%9F%A5%E8%AF%A2%E6%8C%87%E5%AE%9A%E6%A0%87%E7%AD%BE%E7%9A%84%E7%BB%91%E5%AE%9A%E7%8A%B6%E6%80%81">查询指定标签的绑定状态</a> <br />
     * @param sequence 作用见上一个方法↑
     * @param tag 被查询的 tag
     */
    public static void checkTagBindState(@NonNull Context context, int sequence, String tag) {
        JPushInterface.checkTagBindState(context, sequence, tag);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 获取 RegistrationID API
    // 开始支持的版本：Android JPush SDK v1.6.0
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_guide#获取-registration-id-交互建议">获取 Registration ID 交互建议</a> <br />
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%8E%B7%E5%8F%96-registrationid-api">获取 RegistrationID API</a> <br />
     * 功能说明 <br />
     * RegistrationID 定义 <br />
     * 集成了 JPush SDK 的应用程序在第一次成功注册到 JPush 服务器时，JPush 服务器会给客户端返回一个唯一的该设备的标识 - RegistrationID。JPush SDK 会以广播的形式发送 RegistrationID 到应用程序。<br />
     * 应用程序可以把此 RegistrationID 保存以自己的应用服务器上，然后就可以根据 RegistrationID 来向设备推送消息或者通知。<br />
     * 由于极光推送所有形式的推送最后都会转化为对 Registration ID 推送，因此排查客户问题的时候需要提供 Registration ID。为了方便线上客户准确提供信息，减少沟通成本，我们建议您完成 SDK 集成后，在 App 的【关于】、【意见反馈】、【我的】等比较不常用的 UI 中展示客户的 Registration ID 。
     * @return 只有当应用程序成功注册到 JPush 的服务器时才返回对应的值，否则返回空字符串。
     * @see PushMessageService#onRegister(Context, String)
     */
    @NonNull
    public static String getRegistrationID(@NonNull Context context) {
        return JPushInterface.getRegistrationID(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 设置手机号码 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%AE%BE%E7%BD%AE%E6%89%8B%E6%9C%BA%E5%8F%B7%E7%A0%81-api">设置手机号码 API</a> <br />
     * 调用此 API 设置手机号码，用于 <a href="https://docs.jiguang.cn/jpush/practice/push-SMS-intro">短信补充功能</a>。该接口会控制调用频率，频率为 10s 之内最多 3 次。 <br />
     * 注：短信补充仅支持国内业务，号码格式为 11 位数字，有无 +86 前缀皆可。
     * @param sequence 作用见上一个方法↑
     * @param mobileNumber 手机号码。如果传 null 或空串则为解除号码绑定操作。<br />
     *                     限制：只能以 “+” 或者 数字开头；后面的内容只能包含 “-” 和数字。
     */
    public static void setMobileNumber(@NonNull Context context, int sequence, @Nullable String mobileNumber) {
        JPushInterface.setMobileNumber(context, sequence, mobileNumber);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 配置下载渠道 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E9%85%8D%E7%BD%AE%E4%B8%8B%E8%BD%BD%E6%B8%A0%E9%81%93-api">配置下载渠道 API</a> <br />
     * 动态配置 channel（程序包下载渠道），优先级比 AndroidManifest(JPUSH_CHANNEL) 里配置的高
     * @param channel 指明应用程序包的下载渠道，为方便分渠道统计，具体值由你自行定义，如"华为应用市场"、"小米应用商店", "OPPO"等。<br />
     *                传 null 表示依然使用 AndroidManifest 里配置的 channel
     */
    public static void setChannel(@NonNull Context context, @Nullable String channel) {
        JPushInterface.setChannel(context, channel);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 应用内消息页面获取和切换
    // 应用内消息展示需要依赖页面，因此需要监听页面的生命周期
    // Activity方式: 极光SDK内部会对Activity的生命周期监听，进行动态控制处理，开发者无需特殊处理
    // Fragment方式: Fragment需要开发者主动调用接口类JPushInterface 以下两个接口进行动态控制
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E9%A1%B5%E9%9D%A2%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E7%9B%91%E5%90%AC">Fragment 页面生命周期监听</a> <br />
     * 应用内消息展示需要依赖页面，因此需要监听页面的生命周期 <br />
     * 进入Fragment页面，调用该接口, 具体调用位置, 请点击↑链接查看!
     * @param fragmentName Fragment页面完整类名（this.getClass().getCanonicalName()）
     */
    public static void onFragmentResume(@NonNull Context context, @NonNull String fragmentName) {
        JPushInterface.onFragmentResume(context, fragmentName);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#页面生命周期监听">Fragment 页面生命周期监听</a> <br />
     * 退出Fragment页面，调用该接口, 具体调用位置, 请点击↑链接查看!
     * @param fragmentName Fragment页面完整类名（this.getClass().getCanonicalName()）
     */
    public static void onFragmentPause(@NonNull Context context, @NonNull String fragmentName) {
        JPushInterface.onFragmentPause(context, fragmentName);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 统计分析 API (以下3个方法用于App统计, 非消息推送, 可不用理会)
    // 本 API 用于“用户使用时长”，“活跃用户”，“用户打开次数”的统计，并上报到服务器，在 Portal 上展示给开发者。
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#统计分析-api">统计分析 API</a> <br />
     * 本 API 用于“用户使用时长”，“活跃用户”，“用户打开次数”的统计，并上报到服务器，在 Portal 上展示给开发者。
     */
    public static void onResume(@NonNull Context context) {
        JPushInterface.onResume(context);
    }

    /**
     * 统计分析 API
     * 本 API 用于“用户使用时长”，“活跃用户”，“用户打开次数”的统计，并上报到服务器，在 Portal 上展示给开发者。
     */
    public static void onPause(@NonNull Context context) {
        JPushInterface.onPause(context);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#上报统计事件">上报统计事件</a> <br />
     * 用于上报用户的通知栏被打开，或者用于上报用户自定义消息被展示等客户端需要统计的事件。
     * @param msgId 推送每一条消息和通知对应的唯一 ID。
     *              （ msgId 来源于发送消息和通知的 Extra 字段 JPushInterface.EXTRA_MSG_ID，
     *              参考 接收推送消息 Receiver ）, 例: bundle.getString(JPushInterface.EXTRA_MSG_ID)
     */
    public static void reportNotificationOpened(@NonNull Context context, String msgId) {
        JPushInterface.reportNotificationOpened(context, msgId);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 通知开关 API (下方2个方法)
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#通知开启状态">通知开启状态</a> <br />
     * 检查当前应用的通知开关是否开启
     * @return 1表示开启，0表示关闭，-1表示检测失败
     */
    public static int isNotificationEnabled(@NonNull Context context) {
        return JPushInterface.isNotificationEnabled(context);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#跳转至应用通知设置页">跳转至应用通知设置页</a> <br />
     * 跳转手机的应用通知设置页，可由用户操作开启通知开关
     */
    public static void goToAppNotificationSettings(@NonNull Context context) {
        JPushInterface.goToAppNotificationSettings(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 设置是否开启省电模式
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#设置是否开启省电模式">设置是否开启省电模式</a> <br />
     * JPush SDK 开启和关闭省电模式，默认为关闭。
     * @param enable 是否需要开启或关闭，true 为开启，false 为关闭
     */
    public static void setPowerSaveMode(@NonNull Context context, boolean enable) {
        JPushInterface.setPowerSaveMode(context, enable);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 通知栏样式定制 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#通知栏样式定制-api">通知栏样式定制 API</a> <br />
     * 大多数情况下，开发者不需要调用这里的定制通知栏 API 来自定义通知栏样式，只需要使用 SDK 默认的即可。<br />
     * 使用本通知栏定制 API 提供的能力, 可以:
     * <ul>
     *     <li>改变 Notification 里的铃声、震动、显示与消失行为</li>
     *     <li>自定义通知栏显示样式</li>
     *     <li>不同的 Push 通知，Notification 样式不同</li>
     * </ul>
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_senior/#_8">自定义通知栏样式教程</a> <br />
     * 极光 Push SDK 提供了 3 个用于定制通知栏样式的构建类： <br />
     * {@link DefaultPushNotificationBuilder} <br />
     * ┣━ {@link BasicPushNotificationBuilder} 定制 Notification 里的 defaults/flags/ icon 等基础样式（行为）<br />
     * ┃ &emsp; ┣━ {@link CustomPushNotificationBuilder}: 继承 Basic 进一步让开发者定制 Notification Layout <br />
     * ┗━ {@link MultiActionsNotificationBuilder} : 进一步让开发者定制 Notification Layout <br />
     * 如果不调用此方法定制，则极光 Push SDK 默认的通知栏样式是：Android 标准的通知栏提示。
     */
    public static void setDefaultPushNotificationBuilder(DefaultPushNotificationBuilder builder) {
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
//        builder.statusBarDrawable = icon;
//        builder.notificationFlags = notificationFlags;
//        builder.notificationDefaults = notificationDefaults;
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#设置某编号的通知栏样式构建类">设置某编号的通知栏样式构建类</a> <br />
     * 当开发者需要为不同的通知，指定不同的通知栏样式（行为）时，则需要调用此方法设置多个通知栏构建类。<br />
     * 3.0.0 版本新增 {@link MultiActionsNotificationBuilder}，即带按钮的通知栏构建类，可通过该 api 设置。<br />
     * 设置时，开发者自己维护 notificationBuilderId 这个编号，
     * 下发通知时使用 <a href="https://docs.jiguang.cn/jpush/server/push/rest_api_v3_push#android">builder_id</a> 指定该编号，
     * 从而 Push SDK 会调用开发者应用程序里设置过的指定编号的通知栏构建类，来定制通知栏样式。<br />
     *
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_senior/#_8">自定义通知栏样式教程</a>
     */
    public static void setPushNotificationBuilder(@IntRange(from = 1) Integer notificationBuilderId, BasicPushNotificationBuilder builder) {
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
//        builder.statusBarDrawable = icon;
//        builder.notificationFlags = notificationFlags;
//        builder.notificationDefaults = notificationDefaults;

//        MultiActionsNotificationBuilder builder = new MultiActionsNotificationBuilder(context);
//        builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "first", "my_extra1");
//        builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "second", "my_extra2");
//        builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "third", "my_extra3");
        JPushInterface.setPushNotificationBuilder(notificationBuilderId, builder);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 设置保留最近通知条数 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%AE%BE%E7%BD%AE%E4%BF%9D%E7%95%99%E6%9C%80%E8%BF%91%E9%80%9A%E7%9F%A5%E6%9D%A1%E6%95%B0-api">设置保留最近通知条数 API</a> <br />
     * 通过极光推送，推送了很多通知到客户端时，如果用户不去处理，就会有很多保留在那里。 <br />
     * 从 v 1.3.0 版本开始 SDK 增加此功能，限制保留的通知条数。默认为保留最近 5 条通知。 <br />
     * @param maxNum 最多显示的条数
     */
    public static void setLatestNotificationNumber(@NonNull Context context, @IntRange(from = 1) int maxNum) {
        JPushInterface.setLatestNotificationNumber(context, maxNum);
    }



    ///////////////////////////////////////////////////////////////////////////
    // CrashLog 收集并上报  (用于崩溃日志上报, 可不用理会下方2个方法)
    // Android JPush SDK v2.1.8 及以后版本，默认为开启状态，并增加 stopCrashHandler 接口。
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#关闭-crashlog-上报">关闭 CrashLog 上报</a> <br />
     * SDK 通过 Thread.UncaughtExceptionHandler 捕获程序崩溃日志，并在程序奔溃时实时上报如果实时上报失败则会在程序下次启动时发送到服务器。如需要程序崩溃日志功能可调用此方法。
     */
    public static void stopCrashHandler(@NonNull Context context) {
        JPushInterface.stopCrashHandler(context);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#开启-crashlog-上报">开启 CrashLog 上报</a> <br />
     */
    public static void initCrashHandler(@NonNull Context context) {
        JPushInterface.initCrashHandler(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 获取推送连接状态
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#获取推送连接状态">获取推送连接状态</a> <br />
     * 开发者可以使用此功能获取当前 Push 服务的连接状态 <br />
     * 当连接状态发生变化时（连接，断开），会发出一个广播，开发者可以在自定义的 Receiver 监听
     * {@link JPushInterface#ACTION_CONNECTION_CHANGE} 获取变化的状态，也可通过 API 主动获取。
     * @see PushMessageService#onConnected(Context, boolean)
     *
     */
    public static boolean getConnectionState(@NonNull Context context) {
        return JPushInterface.getConnectionState(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 本地通知 API
    // 通过极光推送的 SDK，开发者只需要简单调用几个接口，便可以在应用中定时发送本地通知
    // 1.本地通知 API 不依赖于网络，无网条件下依旧可以触发
    // 2.本地通知与网络推送的通知是相互独立的，不受保留最近通知条数上限的限制
    // 3.本地通知的定时时间是自发送时算起的，不受中间关机等操作的影响
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#添加一个本地通知">添加一个本地通知</a> <br />
     * @param notification 是本地通知对象；建议notificationId设置为正整数，为0或者负数时会导致本地通知无法清除。<br />
     *                     可参考官方: <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#本地通知相关设置" target="_blank">本地通知相关设置</a>
     */
    public static void addLocalNotification(@NonNull Context context, JPushLocalNotification notification) {
//        JPushLocalNotification ln = new JPushLocalNotification();
//        ln.setBuilderId(0);
//        ln.setContent("hhh");
//        ln.setTitle("ln");
//        ln.setNotificationId(11111111) ;
//        ln.setBroadcastTime(System.currentTimeMillis() + 1000 * 60 * 10);
//
//        Map<String , Object> map = new HashMap<String, Object>() ;
//        map.put("name", "jpush") ;
//        map.put("test", "111") ;
//        JSONObject json = new JSONObject(map) ;
//        ln.setExtras(json.toString()) ;
        JPushInterface.addLocalNotification(context, notification);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#移除指定的本地通知">移除指定的本地通知</a> <br />
     * @param notificationId  是要移除的本地通知的 ID，注意notificationId为0或者负数的通知无法移除
     */
    public static void removeLocalNotification(@NonNull Context context, long notificationId) {
        JPushInterface.removeLocalNotification(context, notificationId);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#移除所有的本地通知">移除所有的本地通知</a> <br />
     * 注意 notificationId 为 0 或者负数时通知无法移除.
     */
    public static void clearLocalNotifications(@NonNull Context context) {
        JPushInterface.clearLocalNotifications(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 地理围栏 API (需要定位权限!)
    // JPush SDK 提供地理围栏功能，当设备进入或离开相应的地理区域才触发通知或自定义消息。开发者可以通过此功能对 SDK 提供的地理围栏功能进行设置。
    // 下方3个是"地理围栏 API", 没使用这个功能就不用理会!
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#设置地理围栏监控周期">设置地理围栏监控周期</a> <br />
     * 设置地理围栏监控周期，最小3分钟，最大1天。默认为15分钟，当距离地理围栏边界小于1000米周期自动调整为3分钟。
     * 设置成功后一直使用设置周期，不会进行调整。
     * @param interval 监控周期，单位是毫秒。
     */
    public static void setGeofenceInterval(@NonNull Context context, long interval) {
        JPushInterface.setGeofenceInterval(context, interval);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#设置允许保存的最大地理围栏个数">设置允许保存的最大地理围栏个数</a> <br />
     * 设置最多允许保存的地理围栏数量，超过最大限制后，如果继续创建先删除最早创建的地理围栏。
     * 默认数量为10个，允许设置最小1个，最大100个。
     * @param maxNumber 最多允许保存的地理围栏个数
     */
    public static void setMaxGeofenceNumber(@NonNull Context context, int maxNumber) {
        JPushInterface.setMaxGeofenceNumber(context, maxNumber);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#删除地理围栏">删除地理围栏</a> <br />
     * 删除指定id的地理围栏
     * @param geofenceid 地理围栏的id
     */
    public static void deleteGeofence(@NonNull Context context, String geofenceid) {
        JPushInterface.deleteGeofence(context, geofenceid);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 角标 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#设置角标数字">设置角标数字</a> <br />
     * 设置角标数字(目前仅支持华为手机) <br />
     * 如果需要调用这个接口，还需要在AndroidManifest.xml里配置华为指定的权限 <br />
     * <code>&lt;uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE "/&gt;</code>
     *
     * @param num 新的角标数字，传入负数将会修正为0
     */
    public static void setBadgeNumber(@NonNull Context context, int num) {
        JPushInterface.setBadgeNumber(context, num);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 清除通知 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#清除所有通知">清除所有通知</a> <br />
     * 推送通知到客户端时，由 JPush SDK 展现通知到通知栏上。 <br />
     * 此 API 提供清除通知的功能，包括：清除所有 JPush 展现的通知（不包括非 JPush SDK 展现的）；清除指定某个通知。
     */
    public static void clearAllNotifications(@NonNull Context context) {
        JPushInterface.clearAllNotifications(context);
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#清除通知">清除通知</a> <br />
     * 推送通知到客户端时，由 JPush SDK 展现通知到通知栏上。 <br />
     * 清除指定某个通知。 <br />
     * @param notificationId 通知 ID, 来源于 intent 参数 JPushInterface.EXTRA_NOTIFICATION_ID，
     *                       可参考文档：接收推送消息 Receiver
     */
    public static void clearNotificationById(@NonNull Context context, int notificationId) {
        JPushInterface.clearNotificationById(context, notificationId);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 设置允许推送时间 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#设置允许推送时间-api">设置允许推送时间 API</a> <br />
     * 如果不在该时间段内收到消息，SDK 的处理是：<b>推送到的通知会被扔掉</b>。 <br />
     *
     * 这是一个纯粹客户端的实现，所以与客户端时间是否准确、时区等这些，都没有关系。 <br />
     * 而且该接口仅对通知有效，自定义消息不受影响。 <br />
     * @param weekDays 0 表示星期天，1 表示星期一，以此类推。 （ 7 天制，Set 集合里面的 int 范围为 0 到 6 ） <br />
     *                 值为 null，则任何时间都可以收到通知，set 的 size 为 0，则表示任何时间都收不到通知。
     * @param startHour 允许推送的开始时间 （ 24 小时制：startHour 的范围为 0 到 23 ）
     * @param endHour 允许推送的结束时间 （ 24 小时制：endHour 的范围为 0 到 23 ）
     *
     * //表示周一到周五、上午 10 点到晚上 23 点，都可以推送。
     * JPushInterface.setPushTime(getApplicationContext(), days, 10, 23);
     */
    public static void setPushTime(@NonNull Context context, @Nullable Set<Integer> weekDays,
                                   @IntRange(from = 0, to = 23) int startHour,
                                   @IntRange(from = 0, to = 23) int endHour) {
        JPushInterface.setPushTime(context, weekDays, startHour, endHour);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 设置通知静默时间 API
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#设置通知静默时间-api">设置通知静默时间 API</a> <br />
     * 默认情况下用户在收到推送通知时，客户端可能会有震动，响铃等提示。但用户在睡觉、开会等时间点希望为“免打扰”模式，也是静音时段的概念。 <br />
     * 开发者可以调用此 API 来设置静音时段。如果在该时间段内收到消息，则：<b>不会有铃声和震动</b>。
     * @param startHour 静音时段的开始时间 - 小时 （ 24 小时制，范围：0~23 ）
     * @param startMinute 静音时段的开始时间 - 分钟（范围：0~59 ）
     * @param endHour 静音时段的结束时间 - 小时 （ 24 小时制，范围：0~23 ）
     * @param endMinute 静音时段的结束时间 - 分钟（范围：0~59 ）
     *
     * //晚上 10：30 点到第二天早上 8：30 点为静音时段。
     * JPushInterface.setSilenceTime(context, 22, 30, 8, 30);
     */
    public static void setSilenceTime(@NonNull Context context, @IntRange(from = 0, to = 23) int startHour,
                                      @IntRange(from = 0, to = 59) int startMinute,
                                      @IntRange(from = 0, to = 23) int endHour,
                                      @IntRange(from = 0, to = 59) int endMinute) {
        JPushInterface.setSilenceTime(context, startHour, startMinute, endHour, endMinute);
    }





    //暂未在官网找到说明
    public static String getDeviceId(Context context) {
        return JPushInterface.getUdid(context);
    }





    ///////////////////////////////////////////////////////////////////////////
    // 下方4个方法, 仅用于设置, 与本类没什么关系
    ///////////////////////////////////////////////////////////////////////////
    private static boolean      isNeedShowNotification = true;
    private static Notification notification;
    public static boolean isNeedShowNotification(Context context, NotificationMessage notificationMessage, String s) {
        return isNeedShowNotification;
    }
    public static void setIsNeedShowNotification(boolean isNeedShowNotification) {
        JPushUtils.isNeedShowNotification = isNeedShowNotification;
    }
    public static Notification getNotification(Context context, NotificationMessage notificationMessage) {
        return notification;
    }
    public static void setNotification(Notification notification) {
        JPushUtils.notification = notification;
    }
}
