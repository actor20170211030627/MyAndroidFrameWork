package com.actor.umeng;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * description: <a href="https://developer.umeng.com/docs/119267/cate/119267">友盟统计</a>工具类 <br />
 * 适用于对APP的统计分析、性能监控、推送、分享、智能认证等场景
 * <ul>
 *     <li>1~5: 见: {@link UMConfigureUtils}
 *     </li>
 *     <li>6.<a href="https://developer.umeng.com/docs/119267/detail/118584#title-jkq-wtc-pxj">埋点验证配置</a>: 如果要添加埋点验证配置, 请点击链接了解!</li>
 *     <li>7.<a href="https://developer.umeng.com/docs/119267/detail/118639">集成测试</a>: 如果要添加集成测试, 请点击链接了解!</li>
 * </ul>
 *
 * @author : ldf
 * @update : 2023/7/26 on 12
 */
public class UMAnalyticsUtils {

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118637#title-9yq-hzm-972">页面采集</a>
     * 页面采集分为两种模式：自动采集（仅采集activity）和手动采集（支持activity和非activity）。页面采集模式选择需要和预初始化一起。<br />
     * @param collectionMode 采集模式: {@link MobclickAgent.PageMode#AUTO}, {@link MobclickAgent.PageMode#MANUAL}, ..
     */
    public static void setPageCollectionMode(MobclickAgent.PageMode collectionMode) {
        MobclickAgent.setPageCollectionMode(collectionMode);
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118578#title-oyr-192-0ek">Session定义</a> <br />
     * 修改session时长会影响应用使用时长，请读取以上↑链接后再合理修改！！！
     * @param interval 间隔时间. 单位为毫秒，如果想设定为40秒，interval应为 40*1000。interval最小有效值为30秒。
     */
    public static void setSessionContinueMillis(@IntRange(from = 30000L) long interval) {
        MobclickAgent.setSessionContinueMillis(interval);
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118588#title-bk0-mce-5nw">程序退出时，用于保存统计数据的API。</a>
     * 如果开发者调用kill或者exit之类的方法杀死进程，或者双击back键会杀死进程，请务必在此之前调用MobclickAgent.onKillProcess方法，用来保存统计数据。
     */
    public static void onKillProcess(@NonNull Context context) {
        MobclickAgent.onKillProcess(context);
    }



    ///////////////////////////////////////////////////////////////////////////
    // <a href="https://developer.umeng.com/docs/119267/detail/118637">高级功能</a>
    ///////////////////////////////////////////////////////////////////////////
    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118637#title-w03-xg4-sx2">集成账号统计</a>
     * 如果要使用这个功能, 需要先在友盟后台<a href="https://developer.umeng.com/docs/119267/detail/118637#title-ufv-r6r-15c">启用账号统计</a> <br />
     * 【友盟+】在统计用户时以设备为标准，如果需要统计应用自身的账号，请使用以下接口：
     * @param Provider 账号来源。如果用户通过第三方账号登陆，可以调用此接口进行统计。<br />
     *                 支持自定义，不能以下划线”_”开头，使用大写字母和数字标识，长度小于32 字节; 如果是上市公司，建议使用股票代码。<br />
     *                 <b>示例: sms, wechat, qq, phone, ...</b>
     * @param ID 用户账号ID，长度小于64字节。<b>示例: userId, phone, ...</b>
     */
    public static void onProfileSignIn(@Nullable String Provider, @Nullable String ID) {
//        MobclickAgent.onProfileSignIn(ID);
        MobclickAgent.onProfileSignIn(Provider, ID);
    }

    /**
     * 用户登出: 账号登出时需调用此接口，调用之后不再发送账号相关内容。
     */
    public static void onProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118637#title-ts5-7f3-122">手动采集-Manual</a>
     * 如果需要统计非activity页面，例如fragment、自定义View等，则需要选择手动采集。手动采集模式不会采集activity信息，仅采集开发者埋点页面的信息。<br />
     * <b>注意：</b><br />
     * <b>以下2个接口对于一个页面必须成对调用！！！ 不同页面间仅支持串行调用！！！</b><br />
     * <b>定义页面进入</b>
     */
    public static void onPageStart(String name) {
        MobclickAgent.onPageStart(name);
    }

    /**
     * <b>定义页面退出</b>
     */
    public static void onPageEnd(String name) {
        MobclickAgent.onPageEnd(name);
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/118637#title-pw2-6iv-th5">埋点接口</a>
     * @param eventID 当前统计的事件ID, 示例: "play_music"
     * @param map 对当前事件的参数描述，定义为“参数名:参数值”的HashMap“<键-值>对”
     */
    public static void onEventObject(@NonNull Context context, String eventID, @Nullable Map<String, Object> map) {
        MobclickAgent.onEventObject(context, eventID, map);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 下方5个api, 文档没介绍...
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 文档没介绍...应该和上方接口差不多
     * @param eventID 当前统计的事件ID, 示例: "play_music"
     */
    public static void onEvent(@NonNull Context context, String eventID) {
        MobclickAgent.onEvent(context, eventID);
    }
    /**
     * @param eventID 当前统计的事件ID, 示例: "play_music"
     * @param label 事件说明, 例: "点击了音乐播放按钮"
     */
    public static void onEvent(@NonNull Context context, String eventID, String label) {
        MobclickAgent.onEvent(context, eventID, label);
    }
    /**
     * @param eventID 当前统计的事件ID, 示例: "play_music"
     * @param map 对当前事件的参数描述，定义为“参数名:参数值”的HashMap“<键-值>对”
     */
    public static void onEvent(@NonNull Context context, String eventID, @Nullable Map<String, String> map) {
        MobclickAgent.onEvent(context, eventID, map);
    }
    /**
     * 文档没介绍
     */
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }
    /**
     * 文档没介绍
     */
    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }



    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/171002#h2-faq19">错误分析</a>
     * 组件化统计SDK内建JVM层错误统计。SDK通过Thread.UncaughtExceptionHandler 捕获程序崩溃日志，并在程序下次启动时发送到服务器。
     * @param isEnable false-关闭错误统计功能；true-打开错误统计功能（默认打开）
     */
    public static void setCatchUncaughtExceptions(boolean isEnable) {
        MobclickAgent.setCatchUncaughtExceptions(isEnable);
    }

    /**
     * <a href="https://developer.umeng.com/docs/119267/detail/171002#h2-faq19">异常上传</a>
     * 如果开发者自己捕获了错误，需要手动上传到【友盟+】服务器可以调用下面方法
     */
    public static void reportError(@NonNull Context context, @Nullable Throwable e, @Nullable String error) {
        if (e != null) {
            MobclickAgent.reportError(context, e);
        } else if (!TextUtils.isEmpty(error)) MobclickAgent.reportError(context, e);
    }
}
