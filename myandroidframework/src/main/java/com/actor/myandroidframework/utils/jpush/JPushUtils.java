package com.actor.myandroidframework.utils.jpush;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.IntRange;

import com.actor.myandroidframework.utils.LogUtils;

import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.DefaultPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.ups.JPushUPSManager;
import cn.jpush.android.ups.UPSRegisterCallBack;
import cn.jpush.android.ups.UPSTurnCallBack;
import cn.jpush.android.ups.UPSUnRegisterCallBack;

/**
 * description: 极光推送
 * 1.注册地址 https://docs.jiguang.cn//jpush/client/Android/android_3m/
 *
 * 2.项目的.gradle中配置 jcenter 支持
 * https://docs.jiguang.cn//jpush/client/Android/android_guide/
 * repositories {
 *     jcenter()
 * }
 *
 * 3.module 的 gradle 中添加依赖
 * android {
 *     defaultConfig {
 *         ndk {//选择要添加的对应 cpu 类型的 .so 库
 *              //"armeabi", "armeabi-v7a", "x86","arm64-v8a","x86_64", 'mips', 'mips64'
 *             abiFilters 'armeabi'
 *         }
 *         manifestPlaceholders = [
 *             JPUSH_PKGNAME : applicationId,
 *             JPUSH_APPKEY : "你的 Appkey ", //JPush 上注册的包名对应的 Appkey.
 *             JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
 *         ]
 *     }
 * }
 * dependencies {
 *     //https://docs.jiguang.cn//jpush/client/Android/android_guide/ 极光推送
 *     //http://docs.jiguang.cn/jpush/updates/ 最新版本号
 *     //https://docs.jiguang.cn//jpush/client/Android/android_guide/#jcenter Gradle集成方式
 *     implementation 'cn.jiguang.sdk:jpush:3.6.6'
 *     implementation 'cn.jiguang.sdk:jcore:2.3.8'
 * }
 *
 * 4.在上方添加的'依赖'的AndroidManifest.xml中已经添加了 权限, 不需要额外再添加任何权限.
 *
 * 5.在上方添加的'依赖'的AndroidManifest.xml中已经添加了以下类,不用再添加:
 * @see cn.jpush.android.ui.PopWinActivity
 * @see cn.jpush.android.ui.PushActivity
 *
 * 6.在AndroidManifest.xml中添加以下类:
 * <!--极光推送-->
 * <!--https://docs.jiguang.cn//jpush/client/Android/android_guide/-->
 * <!-- Since JCore2.0.0 Required SDK核心功能-->
 * <!-- 可配置android:process参数将Service放在其他进程中；android:enabled属性不能是false -->
 * <!-- 这个是自定义Service，要继承极光JCommonService，可以在更多手机平台上使得推送通道保持的更稳定 -->
 * <service android:name="com.actor.myandroidframework.utils.jpush.PushService"
 *     android:enabled="true"
 *     android:exported="false"
 *     android:process=":pushcore">
 *     <intent-filter>
 *         <action android:name="cn.jiguang.user.service.action" />
 *     </intent-filter>
 * </service>
 * <!--极光推送-->
 * <!--https://docs.jiguang.cn//jpush/client/Android/android_guide/-->
 * <!-- Required since 3.0.7 -->
 * <!-- 新的 tag/alias 接口结果返回需要开发者配置一个自定的广播 -->
 * <!-- 3.3.0开始所有事件将通过该类回调 -->
 * <!-- 该广播需要继承 JPush 提供的 JPushMessageReceiver 类, 并如下新增一个 Intent-Filter -->
 * <receiver
 *     android:name="com.actor.myandroidframework.utils.jpush.MyJPushMessageReceiver"
 *     android:enabled="true"
 *     android:exported="false" >
 *     <intent-filter>
 *         <action android:name="cn.jpush.android.intent.RECEIVE_MESSAGE" />
 *         <category android:name="${applicationId}" />
 *     </intent-filter>
 * </receiver>
 * <!--自定义Receiver, 接收被拉起回调-->
 * <receiver android:name="com.actor.myandroidframework.utils.jpush.MyWakedResultReceiver" />
 *
 *
 * 7.示例使用
 *   //Application中初始化极光推送
 *   JPushUtils.setDebugMode(isDebugMode);//设置调试模式,在 init 接口之前调用
 *   JPushUtils.init(this);//初始化
 *   JPushUtils.stopPush(this);//停止推送, 防止未登录就接收到消息
 *
 *   //登录成功后
 *   JPushUtils.resumePush(activity);//恢复推送服务
 *   JPushUtils.setAlias(activity, 0, username);//设置别名, 还有其他推送方式(注意别名设置可能失败, 要多次设置)
 *
 *   //退出app
 *   JPushUtils.stopPush(activity);
 *
 * 8.接收消息处理结果回调, 见: {@link MyJPushMessageReceiver}
 *
 * 9.开始去 分组&推送&报表下载:
 * http://docs.jiguang.cn/jpush/console/Instructions/ 控制台使用指南
 * https://www.jiguang.cn/dev2/#/overview/appCardList 控制台
 *
 * 10.错误码: http://docs.jiguang.cn/jpush/client/Android/android_api/#_248
 *
 * author     : 李大发
 * date       : 2020/3/24 on 07:26
 *
 * @version 1.0
 */
public class JPushUtils {

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api
     * 设置调试模式 API
     * 注：该接口需在 init 接口之前调用，避免出现部分日志没打印的情况。多进程情况下建议在自定义的
     * Application 中 onCreate 中调用。
     * @param debug 为 true 则会打印 debug 级别的日志，false 则只会打印 warning 级别以上的日志
     */
    public static void setDebugMode(boolean debug) {
        JPushInterface.setDebugMode(debug);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_1
     * 初始化推送服务 API
     * 注： 如果暂时不希望初始化 JPush SDK ，不要调用 init， 并且在应用初始化的时候就调用 stopPush。
     */
    public static void init(Context context) {
        JPushInterface.init(context);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_2
     * 停止推送服务。
     * 是一个完全本地的状态操作。也就是说：停止推送服务的状态不会保存到服务器上。
     * 停止推送, 推送服务完全被停止。具体表现为：
     * 1.收不到推送消息
     * 2.极光推送所有的其他 API 调用都无效，不能通过 JPushInterface.init 恢复，需要调用 resumePush 恢复。
     */
    public static void stopPush(Context context) {
        JPushInterface.stopPush(context);
    }

    /**
     * 恢复推送服务。
     * 调用了此 API 后，极光推送完全恢复正常工作。
     */
    public static void resumePush(Context context) {
        JPushInterface.resumePush(context);
    }

    /**
     * 用来检查 Push Service 是否已经被停止
     */
    public static boolean isPushStopped(Context context) {
        return JPushInterface.isPushStopped(context);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#channel-api
     * 配置 Channel API
     * 动态配置 channel，优先级比 AndroidManifest 里配置的高
     * @param channel 希望配置的 channel，传 null 表示依然使用 AndroidManifest 里配置的 channel
     */
    public static void setChannel(Context context, String channel) {
        JPushInterface.setChannel(context, channel);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_3
     * 别名与标签 API
     * 过滤掉无效的别名&标记 alias/tags
     * 设置 tags 时，如果其中一个 tag 无效，则整个设置过程失败。
     * @param tags 标记, 命名规范: {@link cn.jpush.android.u.e#a(String)}, 每个tag长度不超过40
     */
    public static Set<String> filterValidTags(Set<String> tags) {
        return JPushInterface.filterValidTags(tags);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#alias-tag
     * 新别名 alias 与标签 tag 接口
     * 极光于 2020/03/10 对「别名设置」的上限进行限制，最多允许绑定 10 个设备。
     * 设置一个别名, 用于别名推送
     * 0: 成功. 遇到 6002 超时，则稍延迟重试
     *  // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     * @param alias 每次调用设置有效的别名，覆盖之前的设置。
     *              有效的别名组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。
     *              限制：alias 命名长度限制为 40 字节。（判断长度需采用 UTF-8 编码）
     */
    public static void setAlias(Context context, int sequence, String alias) {
        if (ExampleUtil.isValidTagAndAlias(alias)) {
            //http://docs.jiguang.cn/jpush/client/Android/android_api/#alias-tag_1
            //老别名 alias 与标签 tag 接口
//            JPushInterface.setAliasAndTags(context, alias, tags, callback);//过时
//            JPushInterface.setAlias(context, alias, callback);//过时
            JPushInterface.setAlias(context, sequence, alias);
        }
    }

    /**
     * 调用此 API 来删除别名。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     */
    public static void deleteAlias(Context context,int sequence) {
        JPushInterface.deleteAlias(context, sequence);
    }

    /**
     * 调用此 API 来查询别名。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     */
    public static void getAlias(Context context,int sequence) {
        JPushInterface.getAlias(context, sequence);
    }

    /**
     * 调用此 API 来设置标签。
     * 需要理解的是，这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     * @param tags 每次调用至少设置一个 tag，覆盖之前的设置，不是新增。
     *  有效的标签组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。
     *  限制：每个 tag 命名长度限制为 40 字节，最多支持设置 1000 个 tag，且单次操作总长度不得超过 5000 字节。（判断长度需采用 UTF-8 编码）
     *  单个设备最多支持设置 1000 个 tag。App 全局 tag 数量无限制。
     */
    public static void setTags(Context context, int sequence, Set<String> tags) {
        Set<String> filter = filterValidTags(tags);
//        JPushInterface.setTags(context, filter, callback);//过时
        JPushInterface.setTags(context, sequence, filter);
    }

    /**
     * 调用此 API 来新增标签。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     * @param tags 每次调用至少新增一个 tag。
     *  有效的标签组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。
     *  限制：每个 tag 命名长度限制为 40 字节，最多支持设置 1000 个 tag，且单次操作总长度不得超过 5000 字节。（判断长度需采用 UTF-8 编码）
     *  单个设备最多支持设置 1000 个 tag。App 全局 tag 数量无限制。
     */
    public static void addTags(Context context, int sequence, Set<String> tags) {
        Set<String> filter = filterValidTags(tags);
        JPushInterface.addTags(context, sequence, filter);
    }

    /**
     * 调用此 API 来删除指定标签。
     * @param sequence 用户自定义的操作序列号, 同操作结果一起返回，用来标识一次操作的唯一性。
     * @param tags 每次调用至少删除一个 tag。
     *  有效的标签组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。
     *  限制：每个 tag 命名长度限制为 40 字节，最多支持设置 1000 个 tag，且单次操作总长度不得超过 5000 字节。（判断长度需采用 UTF-8 编码）
     *  单个设备最多支持设置 1000 个 tag。App 全局 tag 数量无限制。
     */
    public static void deleteTags(Context context, int sequence, Set<String> tags) {
        Set<String> filter = filterValidTags(tags);
        JPushInterface.deleteTags(context, sequence, filter);
    }

    /**
     * 调用此 API 来清除所有标签。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     */
    public static void cleanTags(Context context, int sequence) {
        JPushInterface.cleanTags(context, sequence);
    }

    /**
     * 调用此 API 来查询所有标签。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     */
    public static void getAllTags(Context context, int sequence) {
        JPushInterface.getAllTags(context, sequence);
    }

    /**
     * 调用此 API 来查询指定 tag 与当前用户绑定的状态。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     * @param tag 被查询的 tag
     */
    public static void checkTagBindState(Context context,int sequence,String tag) {
        if (ExampleUtil.isValidTagAndAlias(tag)) {
            JPushInterface.checkTagBindState(context, sequence, tag);
        } else LogUtils.error("tag 格式不对: " + tag, false);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#_70
     * 设置手机号码接口
     * 调用此 API 设置手机号码。该接口会控制调用频率，频率为 10s 之内最多 3 次。
     * @param sequence 用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性。
     * @param mobileNumber 手机号码。如果传 null 或空串则为解除号码绑定操作。
     *                     限制：只能以 “+” 或者 数字开头；后面的内容只能包含 “-” 和数字。
     *                     @see ExampleUtil#isValidMobileNumber(String)
     */
    public static void setMobileNumber(Context context,int sequence, String mobileNumber) {
        if (ExampleUtil.isValidMobileNumber(mobileNumber)) {
            JPushInterface.setMobileNumber(context, sequence, mobileNumber);
        } else LogUtils.error("手机号 格式不对: " + mobileNumber, false);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#registrationid-api
     * 获取 RegistrationID API
     * 集成了 JPush SDK 的应用程序在第一次成功注册到 JPush 服务器时，JPush 服务器会给客户端返回一个
     * 唯一的该设备的标识 - RegistrationID。JPush SDK 会以广播的形式发送 RegistrationID 到应用程序。
     * 应用程序可以把此 RegistrationID 保存以自己的应用服务器上，然后就可以根据 RegistrationID 来向设备
     * 推送消息或者通知。
     * @return 只有当应用程序成功注册到 JPush 的服务器时才返回对应的值，否则返回空字符串。
     * @see MyJPushMessageReceiver#onRegister(Context, String)
     */
    public static String getRegistrationID(Context context) {
        return JPushInterface.getRegistrationID(context);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_4
     * 统计分析 API
     * 本 API 用于“用户使用时长”，“活跃用户”，“用户打开次数”的统计，并上报到服务器，在 Portal 上展示给开发者。
     */
    public static void onResume(Context context) {
        JPushInterface.onResume(context);
    }

    /**
     * 统计分析 API
     * 本 API 用于“用户使用时长”，“活跃用户”，“用户打开次数”的统计，并上报到服务器，在 Portal 上展示给开发者。
     */
    public static void onPause(Context context) {
        JPushInterface.onPause(context);
    }

    /**
     * 用于上报用户的通知栏被打开，或者用于上报用户自定义消息被展示等客户端需要统计的事件。
     * @param msgId 推送每一条消息和通知对应的唯一 ID。
     *              （ msgId 来源于发送消息和通知的 Extra 字段 JPushInterface.EXTRA_MSG_ID，
     *              参考 接收推送消息 Receiver ）
     */
    public static void reportNotificationOpened(Context context, String msgId/*, byte sdkType*/) {
        JPushInterface.reportNotificationOpened(context, msgId/*, sdkType*/);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_5
     * 清除通知 API
     * 推送通知到客户端时，由 JPush SDK 展现通知到通知栏上。
     * 清除所有 JPush 展现的通知（不包括非 JPush SDK 展现的）；
     */
    public static void clearAllNotifications(Context context) {
        JPushInterface.clearAllNotifications(context);
    }

    /**
     * 清除通知 API
     * 推送通知到客户端时，由 JPush SDK 展现通知到通知栏上。
     * 清除指定某个通知。
     * @param notificationId 通知 ID, 来源于 intent 参数 JPushInterface.EXTRA_NOTIFICATION_ID，
     *                       可参考文档：接收推送消息 Receiver
     */
    public static void clearNotificationById(Context context, int notificationId) {
        JPushInterface.clearNotificationById(context, notificationId);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_6
     * 设置允许推送时间 API
     * 如果不在该时间段内收到消息，SDK 的处理是：推送到的通知会被扔掉。
     *
     * 这是一个纯粹客户端的实现，所以与客户端时间是否准确、时区等这些，都没有关系。
     * 而且该接口仅对通知有效，自定义消息不受影响。
     * @param weekDays 0 表示星期天，1 表示星期一，以此类推。 （ 7 天制，Set 集合里面的 int 范围为 0 到 6 ）
     *                 值为 null，则任何时间都可以收到通知，set 的 size 为 0，则表示任何时间都收不到通知。
     * @param startHour 允许推送的开始时间 （ 24 小时制：startHour 的范围为 0 到 23 ）
     * @param endHour 允许推送的结束时间 （ 24 小时制：endHour 的范围为 0 到 23 ）
     *
     * //表示周一到周五、上午 10 点到晚上 23 点，都可以推送。
     * JPushInterface.setPushTime(getApplicationContext(), days, 10, 23);
     */
    public static void setPushTime(Context context, Set<Integer> weekDays,
                                   @IntRange(from = 0, to = 23) int startHour,
                                   @IntRange(from = 0, to = 23) int endHour) {
        JPushInterface.setPushTime(context, weekDays, startHour, endHour);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_7
     * 设置通知静默时间 API
     * 默认情况下用户在收到推送通知时，客户端可能会有震动，响铃等提示。但用户在睡觉、开会等时间点希望为“免打扰”模式，也是静音时段的概念。
     * 开发者可以调用此 API 来设置静音时段。如果在该时间段内收到消息，则：不会有铃声和震动。
     * @param startHour 静音时段的开始时间 - 小时 （ 24 小时制，范围：0~23 ）
     * @param startMinute 静音时段的开始时间 - 分钟（范围：0~59 ）
     * @param endHour 静音时段的结束时间 - 小时 （ 24 小时制，范围：0~23 ）
     * @param endMinute 静音时段的结束时间 - 分钟（范围：0~59 ）
     *
     * //晚上 10：30 点到第二天早上 8：30 点为静音时段。
     * JPushInterface.setSilenceTime(context, 22, 30, 8, 30);
     */
    public static void setSilenceTime(Context context, @IntRange(from = 0, to = 23) int startHour,
                                      @IntRange(from = 0, to = 59) int startMinute,
                                      @IntRange(from = 0, to = 23) int endHour,
                                      @IntRange(from = 0, to = 59) int endMinute) {
        JPushInterface.setSilenceTime(context, startHour, startMinute, endHour, endMinute);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#android-60
     * 申请权限接口（ Android 6.0 及以上）
     * 在 Android 6.0 及以上的系统上，需要去请求一些用到的权限，JPush SDK 用到的一些需要请求如下权限，
     * 因为需要这些权限使统计更加精准，功能更加丰富，建议开发者调用。
     * "android.permission.READ_PHONE_STATE"
     * "android.permission.WRITE_EXTERNAL_STORAGE"
     * "android.permission.READ_EXTERNAL_STORAGE"
     * "android.permission.ACCESS_FINE_LOCATION"
     */
    public static void requestPermission(Context context) {
        JPushInterface.requestPermission(context);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#_178
     * 设置是否开启省电模式
     * JPush SDK 开启和关闭省电模式，默认为关闭。
     * @param enable 是否需要开启或关闭，true 为开启，false 为关闭
     */
    public static void setPowerSaveMode(Context context, boolean enable) {
        JPushInterface.setPowerSaveMode(context, enable);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_8
     * http://docs.jiguang.cn/jpush/client/Android/android_senior/
     * API - 设置默认通知栏样式构建类
     * 当用户需要定制默认的通知栏样式时，则可调用此方法。
     * 极光 Push SDK 提供了 3 个用于定制通知栏样式的构建类：
     * DefaultPushNotificationBuilder:
     *  |_MultiActionsNotificationBuilder: 进一步让开发者定制 Notification Layout
     *  |_BasicPushNotificationBuilder: 用于定制 Android Notification 里的 defaults / flags / icon
     *  等基础样式（行为）
     *     |_CustomPushNotificationBuilder: 继承 Basic 进一步让开发者定制 Notification Layout
     *
     * 如果不调用此方法定制，则极光 Push SDK 默认的通知栏样式是：Android 标准的通知栏提示。
     *
     * 此 API 改变默认的编号为 0 的通知栏样式。
     */
    public static void setDefaultPushNotificationBuilder(DefaultPushNotificationBuilder builder) {
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
//        builder.statusBarDrawable = icon;
//        builder.notificationFlags = notificationFlags;
//        builder.notificationDefaults = notificationDefaults;
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    /**
     * 设置某编号的通知栏样式构建类
     * 当开发者需要为不同的通知，指定不同的通知栏样式（行为）时，则需要调用此方法设置多个通知栏构建类。
     *
     * 3.0.0 版本新增 MultiActionsNotificationBuilder，即带按钮的通知栏构建类，可通过该 api 设置。
     * 设置时，开发者自己维护 notificationBuilderId 这个编号，下发通知时使用 builder_id 指定该编号，从而 Push SDK
     * 会调用开发者应用程序里设置过的指定编号的通知栏构建类，来定制通知栏样式。
     *
     * http://docs.jiguang.cn/jpush/client/Android/android_senior/
     * 此 API 为开发者指定的编号，设置一个自定义的 PushNotificationBuilder（通知样式构建器）。
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

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_9
     * 设置保留最近通知条数 API
     * 通过极光推送，推送了很多通知到客户端时，如果用户不去处理，就会有很多保留在那里。
     * 从 v 1.3.0 版本开始 SDK 增加此功能，限制保留的通知条数。默认为保留最近 5 条通知。
     * 开发者可通过调用此 API 来定义为不同的数量。
     *
     * 仅对通知有效。所谓保留最近的，意思是，如果有新的通知到达，之前列表里最老的那条会被移除。
     * 例如，设置为保留最近 5 条通知。假设已经有 5 条显示在通知栏，当第 6 条到达时，第 1 条将会被移除。
     *
     * 本接口可以在 JPushInterface.init 之后任何地方调用。可以调用多次。SDK 使用最后调用的数值。
     * @param maxNum 最多显示的条数
     */
    public static void setLatestNotificationNumber(Context context, @IntRange(from = 1) int maxNum) {
        JPushInterface.setLatestNotificationNumber(context, maxNum);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#crashlog
     * CrashLog 收集并上报
     * 关闭 CrashLog 上报
     * SDK 通过 Thread.UncaughtExceptionHandler 捕获程序崩溃日志，并在程序奔溃时实时上
     * 报如果实时上报失败则会在程序下次启动时发送到服务器。如需要程序崩溃日志功能可调用此方法。
     */
    public static void stopCrashHandler(Context context) {
        JPushInterface.stopCrashHandler(context);
    }

    /**
     * 开启 CrashLog 上报
     */
    public static void initCrashHandler(Context context) {
        JPushInterface.initCrashHandler(context);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#_198
     * 获取推送连接状态
     * 开发者可以使用此功能获取当前 Push 服务的连接状态
     * 当连接状态发生变化时（连接，断开），会发出一个广播，开发者可以在自定义的 Receiver 监听
     * cn.jpush.android.intent.CONNECTION 获取变化的状态，也可通过 API 主动获取。
     * @see JPushInterface#ACTION_CONNECTION_CHANGE
     */
    public static boolean getConnectionState(Context context) {
        return JPushInterface.getConnectionState(context);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_10
     * 本地通知 API
     * 添加一个本地通知
     * 通过极光推送的 SDK，开发者只需要简单调用几个接口，便可以在应用中定时发送本地通知
     *
     * 本地通知 API 不依赖于网络，无网条件下依旧可以触发
     * 本地通知与网络推送的通知是相互独立的，不受保留最近通知条数上限的限制
     * 本地通知的定时时间是自发送时算起的，不受中间关机等操作的影响
     * @param notification 是本地通知对象；建议notificationId设置为正整数，为0或者负数时会导致本地通知无法清除。
     */
    public static void addLocalNotification(Context context, JPushLocalNotification notification) {
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
     * 移除指定的本地通知
     * 本接口可以在 JPushInterface.init 之后任何地方调用
     * @param notificationId  是要移除的本地通知的 ID，注意notificationId为0或者负数的通知无法移除
     */
    public static void removeLocalNotification(Context context, long notificationId) {
        JPushInterface.removeLocalNotification(context, notificationId);
    }

    /**
     * 移除所有的本地通知，注意notificationId为0或者负数时通知无法移除
     * 本接口可以在 JPushInterface.init 之后任何地方调用
     */
    public static void clearLocalNotifications(Context context) {
        JPushInterface.clearLocalNotifications(context);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_11
     * 地理围栏 API
     * JPush SDK 提供地理围栏功能，当设备进入或离开相应的地理区域才触发通知或自定义消息。开发者可以
     * 通过此功能对SDK提供的地理围栏功能进行设置。
     *
     * 设置地理围栏监控周期，最小3分钟，最大1天。默认为15分钟，当距离地理围栏边界小于1000米周期自动
     * 调整为3分钟。设置成功后一直使用设置周期，不会进行调整。
     * @param interval 监控周期，单位是毫秒。
     */
    public static void setGeofenceInterval(Context context, long interval) {
        JPushInterface.setGeofenceInterval(context, interval);
    }

    /**
     * 设置最多允许保存的地理围栏数量，超过最大限制后，如果继续创建先删除最早创建的地理围栏。默认数量
     * 为10个，允许设置最小1个，最大100个。
     * @param maxNumber 最多允许保存的地理围栏个数
     */
    public static void setMaxGeofenceNumber(Context context, int maxNumber) {
        JPushInterface.setMaxGeofenceNumber(context, maxNumber);
    }

    /**
     * 删除指定id的地理围栏
     * @param geofenceid 地理围栏的id
     */
    public static void deleteGeofence(Context context, String geofenceid) {
        JPushInterface.deleteGeofence(context, geofenceid);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_12
     * 角标 API
     * 设置角标数字(目前仅支持华为手机)
     * 如果需要调用这个接口，还需要在AndroidManifest.xml里配置华为指定的权限
     * <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE "/>
     * @param num 新的角标数字，传入负数将会修正为0
     */
    public static void setBadgeNumber(Context context, int num) {
        JPushInterface.setBadgeNumber(context, num);
    }

    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/#api_13
     * 通知开关 API
     * 检查当前应用的通知开关是否开启
     * @return 1表示开启，0表示关闭，-1表示检测失败
     */
    public static int isNotificationEnabled(Context context) {
        return JPushInterface.isNotificationEnabled(context);
    }

    /**
     * 跳转手机的应用通知设置页，可由用户操作开启通知开关
     * @return 1表示开启，0表示关闭，-1表示检测失败
     */
    public static void goToAppNotificationSettings(Context context) {
        JPushInterface.goToAppNotificationSettings(context);
    }


    //暂未在官网找到说明
    public static String getDeviceId(Context context) {
        return JPushInterface.getUdid(context);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 统一推送服务（Unified Push Service，简称UPS）
    ///////////////////////////////////////////////////////////////////////////
    /**
     * http://docs.jiguang.cn/jpush/client/Android/android_api/
     * 注册接口。
     * 注1：该接口与JPushInterface.init接口不建议混用，可直接使用该接口代替JPushInterface.init接口。
     * 注2：manifest中配置的appkey与该接口传入appkey建议保持一致，如不一致则以manifest中配置的接口为准。
     * 注3：如果manifest中appkey配置为空，则以该接口传入的appkey为准。
     * @param appID manifest中配置的appkey
     * @param appKey 填null即可
     * @param appSecret 填空即可
     * @param callback 该接口的结果回调，状态码为0则说明调用成功，其它值均为失败
     */
    public static void registerToken(Context context, String appID, String appKey, String appSecret,
                                     UPSRegisterCallBack callback) {
        JPushUPSManager.registerToken(context, appID, appKey, appSecret, callback);
    }

    /**
     * 反注册接口
     * 注：调用此接口后，会停用所有Push SDK提供的功能。需通过registerToken接口或者重新安装应用才可恢复。
     * @param callback 反注册结果。状态码为0则说明调用成功，其它值均为失败
     */
    public static void unRegisterToken(Context context, UPSUnRegisterCallBack callback) {
        JPushUPSManager.unRegisterToken(context, callback);
    }

    /**
     * 停止推送服务。
     * 调用了本 API 后，JPush 推送服务完全被停止。具体表现为：
     * 收不到推送消息
     * 极光推送所有的其他 API 调用都无效，需要调用 {@link #turnOnPush(Context, UPSTurnCallBack)} 恢复。
     * @param callback 关闭服务结果。状态码为0则说明调用成功，其它值均为失败
     */
    public static void turnOffPush(Context context, UPSTurnCallBack callback) {
        JPushUPSManager.turnOffPush(context, callback);
    }

    /**
     * 恢复推送服务。
     * 调用了此 API 后，极光推送完全恢复正常工作。
     * @param callback 恢复服务结果。状态码为0则说明调用成功，其它值均为失败
     */
    public static void turnOnPush(Context context, UPSTurnCallBack callback) {
        JPushUPSManager.turnOnPush(context, callback);
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
