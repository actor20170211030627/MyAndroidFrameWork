package com.actor.umeng;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushAliasCallback;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.api.UPushSettingCallback;
import com.umeng.message.api.UPushTagCallback;
import com.umeng.message.api.UPushThirdTokenCallback;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.inapp.IUmengInAppMsgCloseCallback;
import com.umeng.message.inapp.InAppMessageManager;
import com.uyumao.sdk.UYMManager;

import java.util.List;

/**
 * description: <a href="https://developer.umeng.com/docs/67966/detail/206987">友盟消息推送</a>工具类 <br />
 * <ul>
 *     <li>1~5: 见 {@link UMConfigureUtils}</li>
 *     <li>6.消息推送另外还要加1个<a href="https://developer.umeng.com/docs/67966/detail/206987#p-h6g-v65-yzt">maven依赖配置</a>
 *     <ol>
 *         <li>
 *             在工程build.gradle配置脚本中buildscript和allprojects段中添加 <br />
 *             mavenCentral()
 *         </li>
 *         <li>
 *             在模块build.gradle配置脚本dependencies段中添加统计SDK库和其它库依赖 <br />
 *             implementation  'com.umeng.umsdk:push:6.6.1'     &emsp;//消息推送 <br />
 *         </li>
 *     </ol>
 *     </li>
 * </ul>
 *
 * @author    : ldf
 * @update    : 2023/7/26 on 11:10
 */
public class UMPushUtils {

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98585#p-3aw-0lt-wzi">注册推送</a>
     * 注册成功会返回 deviceToken, 是推送平台生成的用于标识设备的id，长度为44位
     * @param callback 注册回调: <br />
     * {@link UPushRegisterCallback#onSuccess(String) public void onSuccess(String deviceToken)} //注册成功 <br />
     * {@link UPushRegisterCallback#onFailure(String, String) public void onFailure(String errCode, String errDesc)} //注册失败 <br />
     */
    public static void register(@NonNull Context context, UPushRegisterCallback callback) {
        PushAgent.getInstance(context).register(callback);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/206987#p-e3l-vt6-dle">注册推送</a>
     * 注册成功后，可通过SDK接口获取 deviceToken
     */
    public static String getRegistrationId(@NonNull Context context) {
        return PushAgent.getInstance(context).getRegistrationId();
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98585#p-3aw-0lt-wzi">应用活跃统计</a>
     * 在App的SplashActivity或MainActivity中onCreate()方法添加 <br />
     * <b>警告</b> <br />
     * 该方法是推送平台多维度推送决策必调用的方法，请务必调用
     * 需在用户同意隐私政策协议之后调用，否则会出现合规问题
     */
    public static void onAppStart(@NonNull Context context) {
        PushAgent.getInstance(context).onAppStart();
    }



    ///////////////////////////////////////////////////////////////////////////
    // <a href="https://developer.umeng.com/docs/67966/detail/98583">高级功能集成文档</a>
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h1-u7075u6D3Bu63A7u5236u901Au77E5u680F2">自定义通知</a>
     */

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u81EAu5B9Au4E49u901Au77E5u680Fu662Fu5426u663Eu793A3">设置App处于前台时是否显示通知</a>
     * 如果您的应用在前台，您可以设置不显示通知消息。默认情况下，应用在前台是显示通知的。
     */
    public static void setNotificationOnForeground(@NonNull Context context, boolean isNotification) {
        PushAgent.getInstance(context).setNotificationOnForeground(isNotification);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u81EAu5B9Au4E49u901Au77E5u680Fu6837u5F0F4">自定义通知样式</a>
     * UmengMessageHandler类负责处理消息，包括通知和自定义消息。其中，getNotification方法返回通知样式。若默认展示样式不符合开发者的需求，可通过重写该方法自定义展示样式。
     */
    public static void setMessageHandler(@NonNull Context context, UmengMessageHandler messageHandler) {
        PushAgent.getInstance(context).setMessageHandler(messageHandler);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u81EAu5B9Au4E49u901Au77E5u680Fu6253u5F00u52A8u4F5C5">自定义点击通知时的打开动作</a>
     * 开发者可自定义点击通知的后续动作，自定义行为在UMessage.custom字段。
     * 在推送通知消息时，在“后续动作”中的“自定义行为”中输入相应的值或代码即可实现。
     * 若开发者需要处理自定义行为，则可以重写方法dealWithCustomAction()
     */
    public static void setNotificationClickHandler(@NonNull Context context, UmengNotificationClickHandler notificationClickHandler) {
        PushAgent.getInstance(context).setNotificationClickHandler(notificationClickHandler);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u8BBEu7F6Eu901Au77E5u680Fu663Eu793Au6570u91CF6">设置显示通知的数量</a>
     * 可以设置最多显示通知的条数，当显示数目大于设置值时，若再有新通知到达，会移除一条最早的通知。<br />
     * 参数number可以设置为0~10，当参数为0时，表示不限制显示个数
     */
    public static void setDisplayNotificationNumber(@NonNull Context context, @IntRange(from = 0, to = 10) int displayNotificationNumber) {
        PushAgent.getInstance(context).setDisplayNotificationNumber(displayNotificationNumber);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2--7">设置通知响铃</a>
     * 响铃、震动及呼吸灯可以分别通过以下三个接口，可单独设置控制方式：<br />
     * 1、服务端控制：通过服务端推送状态来设置通知到达后响铃、震动、呼吸灯的状态；<br />
     * 2、客户端控制：关闭服务端推送控制能力，由客户端控制通知到达后是否响铃、震动以及呼吸灯是否点亮
     * @param playSound {@link MsgConstant#NOTIFICATION_PLAY_SERVER} <br />
     *                  {@link MsgConstant#NOTIFICATION_PLAY_SDK_ENABLE} <br />
     *                  {@link MsgConstant#NOTIFICATION_PLAY_SDK_DISABLE}
     */
    public static void setNotificationPlaySound(@NonNull Context context, int playSound) {
        PushAgent.getInstance(context).setNotificationPlaySound(playSound);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2--7">设置通知震动</a>
     * @param playVibrate 同上
     */
    public static void setNotificationPlayVibrate(@NonNull Context context, int playVibrate) {
        PushAgent.getInstance(context).setNotificationPlayVibrate(playVibrate);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2--7">设置通知呼吸灯</a>
     * @param playLights 同上
     */
    public static void setNotificationPlayLights(@NonNull Context context, int playLights) {
        PushAgent.getInstance(context).setNotificationPlayLights(playLights);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u901Au77E5u514Du6253u6270u6A21u5F0F8">设置通知免打扰时段</a>
     * 为避免打扰用户，默认在“23:00”到“7:00”之间收到通知消息时不响铃，不振动，不闪灯。<br />
     * 如果需要改变默认的静音时间，可以使用以下接口。 <br />
     * 可以通过这个设置，来关闭免打扰模式：setNoDisturbMode(0, 0, 0, 0);
     * @param startHour 开始时
     * @param startMinute 开始分
     * @param endHour 结束时
     * @param endMinute 结束分
     */
    public static void setNoDisturbMode(@NonNull Context context, int startHour, int startMinute, int endHour, int endMinute) {
        PushAgent.getInstance(context).setNoDisturbMode(startHour, startMinute, endHour, endMinute);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u901Au77E5u514Du6253u6270u6A21u5F0F8">消息提醒冷却时间</a>
     * 默认情况下，同一台设备在1分钟内收到同一个应用的多条通知时，不会重复提醒，可以通过如下方法来设置冷却时间：
     */
    public static void setMuteDurationSeconds(@NonNull Context context, int seconds) {
        PushAgent.getInstance(context).setMuteDurationSeconds(seconds);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u81EAu5B9Au4E49u8D44u6E90u5305u540D11">设置资源包名</a>
     * Android Studio开发工具是基于gradle的配置方式，资源文件的包和应用程序的包是可以分开的，为了正确的找到资源包名，需开发者调用设置资源包的接口。
     * 当资源包名(app/src/main/AndroidManifest.xml文件中package字段值)和应用程序包名(app/build.gradle文件中appId)不一致时，需调用设置资源包名的接口
     */
    public static void setResourcePackageName(@NonNull Context context, String resPackageName) {
        PushAgent.getInstance(context).setResourcePackageName(resPackageName);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h1--tag-alias-4">标签(Tag)与别名(Alias)</a>
     * 添加标签(Tag) <br />
     * 标签可以给某一类人群推送消息，别名可以给指定用户推送消息。最佳实践： <br />
     * 客户端开发者在应用内调用 addTags 或者 addAlias来设置对应关系； <br />
     * 【友盟+】消息后台存储相应的关系设置； <br />
     * 在服务器端推送消息时，指定向之前设置过的别名或者标签推送。
     */
    public static void addTags(@NonNull Context context, UPushTagCallback<ITagManager.Result> tagCallback, String... tags) {
        PushAgent.getInstance(context).getTagManager().addTags(tagCallback, tags);
    }

    /**
     * 删除标签(Tag)
     */
    public static void deleteTags(@NonNull Context context, UPushTagCallback<ITagManager.Result> tagCallback, String... tags) {
        PushAgent.getInstance(context).getTagManager().deleteTags(tagCallback, tags);
    }

    /**
     * 获取服务器端的所有标签(Tag)
     */
    public static void getTags(@NonNull Context context, UPushTagCallback<List<String>> tagCallback) {
        PushAgent.getInstance(context).getTagManager().getTags(tagCallback);
    }

    /**
     * 增加别名：(Alias)，将某一类型的别名ID绑定至某设备，老的绑定设备信息还在，别名ID和device_token是一对多的映射关系
     */
    public static void getTags(@NonNull Context context, String aliasId, String aliasType, UPushAliasCallback aliasCallback) {
        PushAgent.getInstance(context).addAlias(aliasId, aliasType, aliasCallback);
    }

    /**
     * 绑定别名：(Alias)，将某一类型的别名ID绑定至某设备，老的绑定设备信息被覆盖，别名ID和deviceToken是一对一的映射关系
     */
    public static void setAlias(@NonNull Context context, String aliaId, String aliasType, UPushAliasCallback aliasCallback) {
        PushAgent.getInstance(context).setAlias(aliaId, aliasType, aliasCallback);
    }

    /**
     * 移除别名：(Alias)
     */
    public static void deleteAlias(@NonNull Context context, String aliaId, String aliasType, UPushAliasCallback aliasCallback) {
        PushAgent.getInstance(context).deleteAlias(aliaId, aliasType, aliasCallback);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#58c5dfb070ely">设置角标</a>
     * 设置角标数字（支持华为、vivo、荣耀、OPPO(需申请)）
     */
    public static void setBadgeNum(@NonNull Context context, int number) {
        PushAgent.getInstance(context).setBadgeNum(number);
    }

    /**
     * 角标数字（支持华为、荣耀）递增减 <br />
     * 华为、荣耀设备的通知消息：用户点击后，角标数字会自动减1
     * @param number 递增减数值
     */
    public static void changeBadgeNum(@NonNull Context context, int number) {
        PushAgent.getInstance(context).changeBadgeNum(number);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#0bd923a0a8vu5">获取弹出通知权限状态</a>
     * @return true:开启；false:关闭
     */
    public static boolean isNotificationEnabled(@NonNull Context context) {
        return PushAgent.getInstance(context).isNotificationEnabled();
    }

    /**
     * 打开通知权限设置界面
     * @return true:成功；false:失败
     */
    public static boolean openNotificationSettings(@NonNull Context context) {
        return PushAgent.getInstance(context).openNotificationSettings();
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#2438ee70f3cxt">获取厂商Token</a>
     * 可通过调用API接口设置厂商Token的回调，通过实现回调接口获取厂商的Token
     */
    public static void setThirdTokenCallback(@NonNull Context context, UPushThirdTokenCallback callback) {
        PushAgent.getInstance(context).setThirdTokenCallback(callback);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 以下4个api是'应用内消息', 仅用于测试时使用, 详情请阅读官网:
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h1-u5E94u7528u5185u6D88u606Fu63A7u52365">应用内消息</a>
     * 应用内消息可以在【友盟+】Push消息后台的【推送】-【创建任务】中选择【应用内消息】： <br />
     * 说明 应用内消息默认为线上模式，如需使用测试模式，请调用如下代码：  <br />
     * InAppMessageManager.getInstance(context).setInAppMsgDebugMode(true);
     */
    public static void setInAppMsgDebugMode(@NonNull Context context, boolean inAppMsgDebugMode) {
        InAppMessageManager.getInstance(context).setInAppMsgDebugMode(inAppMsgDebugMode);
    }
    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u5168u5C4Fu6D88u606F12">全屏消息</a>
     * 全屏消息是App首次启动打开进入的页面，以全屏图片的形式展示。
     * @param mainActivityPath 设置全屏消息默认跳转Activity的路径
     */
    public static void setMainActivityPath(@NonNull Context context, @NonNull Class<? extends Activity> mainActivityPath) {
        InAppMessageManager.getInstance(context).setMainActivityPath(mainActivityPath.getName());
    }
    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u63D2u5C4Fu6D88u606F13">插屏消息</a>
     * 插屏消息是在App页面之上弹出的图片或文本消息。插屏消息分为三种类型：插屏、自定义插屏和纯文本。
     * @param activity 在要展示的页面中调用如下方法
     * @param label 是插屏消息的标签，用来标识该消息。客户端需先调用showCardMessage，把label发送到服务器，之后U-Push后台【展示位置】才会出现可选label。
     */
    public static void showCardMessage(@NonNull Activity activity, String label, @NonNull IUmengInAppMsgCloseCallback callback) {
        InAppMessageManager.getInstance(activity).showCardMessage(activity, label, callback);
    }
    /**
     * <a href="">纯文本</a>
     * 纯文本插屏字体大小可以由开发者控制，单位为sp，默认为18、16、16，可以使用以下方法设置（在showCardMessage之前调用）
     */
    public static void setPlainTextSize(@NonNull Context context, int titleTextSize, int contentTextSize, int buttonTextSize) {
        InAppMessageManager.getInstance(context).setPlainTextSize(titleTextSize, contentTextSize, buttonTextSize);
    }



    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u68C0u67E5u96C6u6210u914Du7F6Eu6587u4EF614">集成自检设置</a>
     * @param isPushCheck 是否开启自检
     */
    public static void setPushCheck(@NonNull Context context, boolean isPushCheck) {
        PushAgent.getInstance(context).setPushCheck(isPushCheck);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h2-u5173u95EDu63A8u900115">推送开关设置</a>
     * @param isOpen 开启/关闭
     */
    public static void enable(@NonNull Context context, boolean isOpen, UPushSettingCallback settingCallback) {
        if (isOpen) {
            PushAgent.getInstance(context).enable(settingCallback);
        } else {
            PushAgent.getInstance(context).disable(settingCallback);
        }
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#h3-lgu-sci-tdi">推送降耗设置</a>
     * @param isSmartEnable 开启/关闭
     */
    public static void setSmartEnable(@NonNull Context context, boolean isSmartEnable) {
        PushAgent.getInstance(context).setSmartEnable(isSmartEnable);
    }

    /**
     * <a href="https://developer.umeng.com/docs/67966/detail/98583#ba7f004014yge">推送地理围栏设置</a>
     * 如需使用地理围栏功能，需要集成对应SDK（uyumao），并声明以下权限（可选）： <br />
     * &lt;!-- 允许应用获取粗略位置 --> <br />
     * &lt;uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /&gt; <br />
     * &lt;!-- 允许应用获取精准位置 --> <br />
     * &lt;uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /&gt; <br />
     * &lt;!-- 应用后台定位权限 -->     <br />
     * &lt;uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" /&gt; <br />
     * <br />
     * 如不需使用地理围栏功能，可调用以下关闭接口：<br />
     * <b>重要</b> <br />
     *  关闭接口需要在UMConfigure.init(...)正式初始化函数调用之前调用。
     * @param enable 开启/关闭
     */
    public static void enableYm(@NonNull Context context, boolean enable) {
        UYMManager.enableYm1(context, enable); // 禁止获取位置信息(发起定位请求)
        UYMManager.enableYm2(context, enable); // 禁止获取位置信息(读取定位缓存)
        UYMManager.enableYm3(context, enable); // 禁止获取已连接WiFi路由器信息
        UYMManager.enableYm4(context, enable); // 禁止获取周边WiFi路由列表信息
        UYMManager.enableYm5(context, enable); // 禁止获取基站信息
//        UYMManager.enableYm6(context, enable); // 友盟官网上没有, 文档也没有...??
    }
}
