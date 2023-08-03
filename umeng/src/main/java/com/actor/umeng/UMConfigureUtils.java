package com.actor.umeng;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.internal.c;
import com.umeng.commonsdk.listener.OnGetOaidListener;

/**
 * description: 友盟公共配置
 * <ul>
 *     <li>1.<a href="https://developer.umeng.com/docs/119267/detail/118584">maven依赖配置</a>
 *     <ol>
 *         <li>
 *             在工程build.gradle配置脚本中buildscript和allprojects段中添加 <br />
 *             maven { url 'https://repo1.maven.org/maven2/' }
 *         </li>
 *         <li>
 *             在模块build.gradle配置脚本dependencies段中添加统计SDK库和其它库依赖 <br />
 *             //友盟基础组件库（所有友盟业务SDK都依赖基础组件库 <br />
 *             implementation  'com.umeng.umsdk:common:9.6.3'   &emsp;// (必选) <br />
 *             implementation  'com.umeng.umsdk:asms:1.8.0'     &emsp;// 必选 <br />
 *             //高级运营分析功能依赖库，请务必集成，以免影响应用安装卸载情况、及对异常设备的准确识别。common需搭配v9.6.3及以上版本，asms需搭配v1.7.0及以上版本 <br />
 *             implementation 'com.umeng.umsdk:uyumao:1.1.2' <br />
 *             implementation  'com.umeng.umsdk:abtest:1.0.1'   &emsp;//使用U-App中ABTest能力，可选
 *         </li>
 *     </ol>
 *     </li>
 *     <li>2.<a href="https://developer.umeng.com/docs/66632/detail/101814#h3-androidmanifest-appkey-channel">AndroidManifest清单配置文件方式初始化AppKey和channel</a> <br />
 *     <b>注意: 可以不在AndroidManifest.xml中配置, 而直接在代码初始化的时候设置。</b> <br />
 *     &lt;application ……&gt; <br />
 *         &emsp; …… <br />
 *         &emsp; &lt;meta-data android:name="UMENG_APPKEY" android:value="YOUR_APP_KEY"/&gt; <br />
 *         &emsp; &lt;meta-data android:name="UMENG_CHANNEL" android:value="Channel ID"/&gt; <br />
 *     &lt;/application&gt;
 *     </li>
 *     <br />
 *     <li>3.<a href="https://developer.umeng.com/docs/119267/detail/182050">请务必在《隐私政策》中向用户告知使用友盟SDK</a></li>
 *     <li>4.<a href="https://developer.umeng.com/docs/119267/detail/118584#title-tul-yb2-sqm">权限配置</a>: 集成本依赖后已自动配置!</li>
 *     <li>5.<a href="https://developer.umeng.com/docs/119267/detail/118584#title-0fo-3gc-ulu">混淆配置</a>: 集成本依赖后已自动混淆, 但要注意R文件问题: <br />
 *         SDK需要引用导入工程的资源文件，通过了反射机制得到资源引用文件R.java，但是在开发者通过proguard等混淆/优化工具处理apk时，proguard可能会将R.java删除，如果遇到这个问题，请添加如下配置：<br />
 *         -keep public class [您的应用包名].R$*{ <br />
 *             &emsp; public static final int *;        <br />
 *         }
 *     </li>
 * </ul>
 * @author : ldf
 * @update : 2023/7/29 on 18
 */
public class UMConfigureUtils {

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118588">SDK预初始化</a>
     * 为保证您的在集成【友盟+】统计SDK之后，能够满足工信部相关合规要求，您应确保在App安装后首次冷启动时按照如下方式进行初始化。 <br />
     *
     * 预初始化函数不会采集设备信息，也不会向友盟后台上报数据。
     * @param appKey 你在友盟后台申请的appKey。如需要使用AndroidManifest.xml中配置好的appkey，这儿传null
     * @param channel 渠道, 示例: huawei, oppo, vivo...。如需要使用AndroidManifest.xml中配置好的channel，这儿传null
     */
    public static void preInit(@NonNull Application app, @Nullable String appKey, @Nullable String channel) {
        //<a href="https://developer.umeng.com/docs/119267/detail/118588#title-lj7-0x1-4yb">日志显示</a>
        UMConfigure.setLogEnabled(AppUtils.isAppDebug());

        UMConfigure.preInit(app, appKey, channel);
    }

    /**
     * {@link com.umeng.commonsdk.debug.UMRTLog UMRTLog}输出功能, 默认未打开
     * @param isForceOpenUMRTLog 是否强制打开友盟内部的 UMRTLog 输出功能
     */
    public static void setForceOpenUMRTLog(boolean isForceOpenUMRTLog) {
        //反射, 设置打印内部信息
        ReflectUtils.reflect(UMConfigure.class).field("shouldOutputRT", isForceOpenUMRTLog);
    }

    /**
     * 在用户阅读您的《隐私政策》并取得用户授权之后，才调用正式初始化函数UMConfigure.init()初始化统计SDK，
     * 此时SDK才会真正采集设备信息并上报数据。<br />
     * 反之，如果用户不同意《隐私政策》授权，则不能调用UMConfigure.init()初始化函数。
     * @param isAgree 渠道
     */
    public static void submitPolicyGrantResult(@NonNull Context context, boolean isAgree) {
        UMConfigure.submitPolicyGrantResult(context, isAgree);
    }

    /**
     * 用户是否同意《隐私政策》 <br />
     * 参考{@link c#workEvent(Object, int)} 的625行
     * @return 0还未设置, 1同意, 2不同意
     */
    public static int isPolicyGrant(@NonNull Context context) {
        //c.a: um_policy_grant
        SharedPreferences var44 = context.getApplicationContext().getSharedPreferences(c.a, Context.MODE_PRIVATE);
        return var44.getInt(c.d, 0);
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118584#title-hkr-k8j-nss">获取OAID</a>
     * OAID目前为目前国内市场主流的Android Q设备标识。目前主流厂商（华为、oppo、vivo、联想、小米）均已在新版本系统中支持该标识的获取，具有权威性。
     */
    public static void getOaid(@NonNull Context context, @Nullable OnGetOaidListener listener) {
        UMConfigure.getOaid(context, listener);
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118588#title-yb4-p8i-jdc">SDK初始化</a> <br />
     * 在用户阅读您的《隐私政策》并取得用户授权之后，才调用正式初始。<br />
     * <b>注意:</b> <br />
     * &emsp; 如果你已经在AndroidManifest.xml中配置好的appkey和channel值，UMConfigure.init调用中appkey和channel参数请置为null）。
     * @param appKey 你在友盟后台申请的appKey。如需要使用AndroidManifest.xml中配置好的appkey，这儿传null
     * @param channel 渠道, 示例: huawei, oppo, vivo...。如需要使用AndroidManifest.xml中配置好的channel，这儿传null
     * @param pushSecret Push推送业务的secret，需要集成Push功能时必须传入Push的secret，否则传空。
     */
    public static void init(@NonNull Context context, @Nullable String appKey, @Nullable String channel, @Nullable String pushSecret) {
//        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, pushSecret);
        //参4: deviceType, 设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子
        UMConfigure.init(context, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, pushSecret);
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118637#title-czp-ecn-22x">子进程埋点</a>
     * 友盟+SDK仅支持子进程自定义事件埋点，页面等其他类型采集暂不支持。子进程埋点需要在SDK初始化完成后调用本函数。<br />
     * 注意：<br />
     * 如果需要在某个子进程中统计自定义事件，则需保证在此子进程中进行SDK初始化。
     * @param isSupport 支持在子进程中统计自定义事件
     */
    public static void setProcessEvent(boolean isSupport) {
        UMConfigure.setProcessEvent(isSupport);
    }

}
