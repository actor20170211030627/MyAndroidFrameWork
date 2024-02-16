package com.actor.jpush;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actor.myandroidframework.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageService;

/**
 * description: <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#jpushmessageservice-%E5%9B%9E%E8%B0%83%E7%B1%BB%E6%9B%BF%E6%8D%A2-307-%E7%89%88%E6%9C%AC%E5%BC%80%E5%A7%8B%E7%9A%84jpushmessagereceiver" target="_blank">JPushMessageService 回调类(替换 3.0.7 版本开始的JPushMessageReceiver)</a> <br />
 * 5.2.0 版本之后新增的回调方式。<br />
 * <ol>
 *     <li>新的消息回调方式中相关回调类。</li>
 *     <li>新的 tag 与 alias 操作回调会在开发者定义的该类的子类中触发。</li>
 *     <li>手机号码设置的回调会在开发者定义的该类的子类中触发。</li>
 *     <li>新回调方式与旧的自定义 Service 兼容：<br />
 *         配置了此 Service 以后，不会再回调JPushMessageReceiver，但默认是也会发广播给旧 Receiver 的 <br />
 *         对于 onMessage、onNotifyMessageArrived、onNotifyMessageOpened、onMultiActionClicked
 *         如果重写了这些方法，则需要调用 super 才会发给旧 Receiver
 *     </li>
 * </ol>
 * {@link null 注意1：}该回调类虽然基于 Service, 但为了加快回调速度，在 sdk 内部会判断进程，当触发进程与组件配置的进程一致时，内部采用 java 对象的回调方式，所以并不会产生 android 组件的生命周期，所以不建议在该类中声明 Handler 属性 <br />
 * {@link null 注意2：}5.2.0前升级上来的客户
 * <ul>
 *     <li>1、只需要修改继承的类名（JPushMessageReceiver修改成JPushMessageService）</li>
 *     <li>2、组件 receiver 修改成 service</li>
 *     <li>3、action 从 cn.jpush.android.intent.RECEIVE_MESAGE 修改成 cn.jpush.android.intent.SERVICE_MESSAGE</li>
 *     <li>4、其他可不变</li>
 * </ul>
 * @author    : ldf
 * date       : 2024/2/14 on 17:52
 */
public class PushMessageService extends JPushMessageService {

    public static final String TYPE_MESSAGE = "TYPE_MESSAGE",           //收到自定义消息回调
            TYPE_NOTIFY_MESSAGE_ARRIVED = "TYPE_NOTIFY_MESSAGE_ARRIVED",//收到通知回调
            TYPE_NOTIFYMESSAGE_OPENED = "TYPE_NOTIFYMESSAGE_OPENED",    //点击通知回调
            TYPE_NOTIFYMESSAGE_DISMISS = "TYPE_NOTIFYMESSAGE_DISMISS",  //清除通知回调
            TYPE_REGISTER = "TYPE_REGISTER",                            //注册成功回调
            TYPE_CONNECTED = "TYPE_CONNECTED",                          //长连接状态回调
            TYPE_COMMAND_RESULT = "TYPE_COMMAND_RESULT",                //注册失败回调
            TYPE_NOTIFYMESSAGE_UNSHOW = "TYPE_NOTIFYMESSAGE_UNSHOW",    //通知未展示的回调
            TYPE_MULTIACTION_CLICKED = "TYPE_MULTIACTION_CLICKED",      //通知开关的回调
            TYPE_NOTIFICATION_SETTINGS_CHECK = "TYPE_NOTIFICATION_SETTINGS_CHECK",//通知开关的回调
            TYPE_TAG_OPERATOR_RESULT = "TYPE_TAG_OPERATOR_RESULT",      //增删查改的操作会在此方法中回调结果
            TYPE_CHECK_TAG_OPERATOR_RESULT = "TYPE_CHECK_TAG_OPERATOR_RESULT",  //查询某个 tag 与当前用户的绑定状态
            TYPE_ALIAS_OPERATOR_RESULT = "TYPE_ALIAS_OPERATOR_RESULT",          //alias 相关的操作
            TYPE_MOBILE_NUMBER_OPERATOR_RESULT = "TYPE_MOBILE_NUMBER_OPERATOR_RESULT",//设置手机号码
            TYPE_GEOFENCE_RECEIVED = "TYPE_GEOFENCE_RECEIVED",          //拉取围栏列表回调
            TYPE_GEOFENCE_REGION = "TYPE_GEOFENCE_REGION",              //触发围栏回调
            TYPE_IN_APP_MESSAGE_SHOW = "TYPE_IN_APP_MESSAGE_SHOW",      //应用内消息展示回调
            TYPE_IN_APP_MESSAGE_CLICK = "TYPE_IN_APP_MESSAGE_CLICK";    //应用内消息点击回调


    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%A0%87%E7%AD%BE%E6%93%8D%E4%BD%9C%E5%9B%9E%E8%B0%83">标签操作回调</a> <br />
     * tag 增删查改的操作会在此方法中回调结果。
     *
     * @param jPushMessage tag 相关操作返回的消息结果体，具体参考
     *                    <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#jpushmessage-%E7%BB%93%E6%9E%9C%E7%B1%BB%EF%BC%88%E6%96%B0%E5%9B%9E%E8%B0%83%EF%BC%89">JPushMessage</a> 类的说明。
     */
    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
//        super.onTagOperatorResult(context, jPushMessage);
//        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context, jPushMessage);
        LogUtils.errorFormat("jPushMessage=%s", jPushMessage);
        if (jPushMessage == null) return;
        if (jPushMessage.getErrorCode() == 0) {
            int sequence = jPushMessage.getSequence();
            Set<String> tags = jPushMessage.getTags();
            EventBus.getDefault().post(new JPushEvent<>(TYPE_TAG_OPERATOR_RESULT, jPushMessage));
        }else if (jPushMessage.getErrorCode() == 6018) {
            LogUtils.error("tags is exceed limit need to clean");
        }
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%9F%A5%E8%AF%A2%E6%A0%87%E7%AD%BE%E7%BB%91%E5%AE%9A%E7%8A%B6%E6%80%81%E5%9B%9E%E8%B0%83">查询标签绑定状态回调</a> <br />
     * 查询某个 tag 与当前用户的绑定状态的操作会在此方法中回调结果。
     * @param jPushMessage check tag 与当前用户绑定状态的操作返回的消息结果体，具体参考
     *                     <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#jpushmessage-%E7%BB%93%E6%9E%9C%E7%B1%BB%EF%BC%88%E6%96%B0%E5%9B%9E%E8%B0%83%EF%BC%89">JPushMessage</a> 类的说明。
     */
    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
//        super.onCheckTagOperatorResult(context, jPushMessage);
//        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        LogUtils.errorFormat("jPushMessage=%s", jPushMessage);
        if (jPushMessage == null) return;
        if (jPushMessage.getErrorCode() == 0) {
            int sequence = jPushMessage.getSequence();
            String checkTag = jPushMessage.getCheckTag();
            boolean tagCheckStateResult = jPushMessage.getTagCheckStateResult();
            EventBus.getDefault().post(new JPushEvent<>(TYPE_CHECK_TAG_OPERATOR_RESULT, jPushMessage));
        }
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%88%AB%E5%90%8D%E6%93%8D%E4%BD%9C%E5%9B%9E%E8%B0%83">别名操作回调</a> <br />
     * alias 相关的操作会在此方法中回调结果。
     * @param jPushMessage alias 相关操作返回的消息结果体，具体参考
     *                    <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#jpushmessage-%E7%BB%93%E6%9E%9C%E7%B1%BB%EF%BC%88%E6%96%B0%E5%9B%9E%E8%B0%83%EF%BC%89">JPushMessage</a> 类的说明。
     */
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
//        super.onAliasOperatorResult(context, jPushMessage);
//        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        LogUtils.errorFormat("jPushMessage=%s", jPushMessage);
        if (jPushMessage == null) return;
        if (jPushMessage.getErrorCode() == 0) {
            int sequence = jPushMessage.getSequence();
            String alias = jPushMessage.getAlias();
            EventBus.getDefault().post(new JPushEvent<>(TYPE_ALIAS_OPERATOR_RESULT, jPushMessage));
        }
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%AE%BE%E7%BD%AE%E6%89%8B%E6%9C%BA%E5%8F%B7%E7%A0%81%E5%9B%9E%E8%B0%83">设置手机号码回调</a> <br />
     * 设置手机号码会在此方法中回调结果。
     * 开始支持的版本：Android JPush SDK v3.1.1
     * @param jPushMessage 设置手机号码返回的消息结果体，具体参考
     *                    <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#jpushmessage-%E7%BB%93%E6%9E%9C%E7%B1%BB%EF%BC%88%E6%96%B0%E5%9B%9E%E8%B0%83%EF%BC%89">JPushMessage</a> 类的说明。
     */
    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
//        super.onMobileNumberOperatorResult(context, jPushMessage);
//        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        LogUtils.errorFormat("jPushMessage=%s", jPushMessage);
        if (jPushMessage == null) return;
        if (jPushMessage.getErrorCode() == 0) {
            String mobileNumber = jPushMessage.getMobileNumber();
            EventBus.getDefault().post(new JPushEvent<>(TYPE_MOBILE_NUMBER_OPERATOR_RESULT, jPushMessage));
        }
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%87%AA%E5%AE%9A%E4%B9%89%E6%B6%88%E6%81%AF%E5%9B%9E%E8%B0%83">自定义消息回调</a> <br />
     * 同时是应用内提醒回调。
     * @param customMessage 接收自定义消息内容
     * @see cn.jpush.android.api.JPushInterface#ACTION_MESSAGE_RECEIVED
     */
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
//        super.onMessage(context, customMessage);
        LogUtils.errorFormat("自定义消息: customMessage=%s", customMessage);
        if (customMessage == null) return;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_MESSAGE, customMessage));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%94%B6%E5%88%B0%E9%80%9A%E7%9F%A5%E5%9B%9E%E8%B0%83">收到通知回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.3.0
     * @param message 接收到的通知内容
     * @see cn.jpush.android.api.JPushInterface#ACTION_NOTIFICATION_RECEIVED
     */
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
//        super.onNotifyMessageArrived(context, message);
        LogUtils.errorFormat("收到通知回调: message=%s", message);
        if (message == null) return;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFY_MESSAGE_ARRIVED, message));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E7%82%B9%E5%87%BB%E9%80%9A%E7%9F%A5%E5%9B%9E%E8%B0%83">点击通知回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.3.0
     * @param message 点击的通知内容
     * @see cn.jpush.android.api.JPushInterface#ACTION_NOTIFICATION_OPENED
     */
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
//        super.onNotifyMessageOpened(context, message);
        LogUtils.errorFormat("点击通知回调: message=%s", message);
        if (message == null) return;
        String notificationTitle = message.notificationTitle;
        String notificationContent = message.notificationContent;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFYMESSAGE_OPENED, message));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%B8%85%E9%99%A4%E9%80%9A%E7%9F%A5%E5%9B%9E%E8%B0%83">清除通知回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.3.0 <br />
     * 说明:
     * <ol>
     *     <li>同时删除多条通知，可能不会多次触发清除通知的回调</li>
     *     <li>只有用户手动清除才有回调，调接口清除不会有回调</li>
     * </ol>
     * @param message 清除的通知内容
     * @see cn.jpush.android.api.JPushInterface#?
     */
    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
//        super.onNotifyMessageDismiss(context, message);
        LogUtils.errorFormat("清除通知回调: message=%s", message);
        if (message == null) return;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFYMESSAGE_DISMISS, message));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%B3%A8%E5%86%8C%E6%88%90%E5%8A%9F%E5%9B%9E%E8%B0%83">注册成功回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.3.0
     * @param registrationId 注册 id
     * @see JPushUtils#getRegistrationID(Context)
     */
    @Override
    public void onRegister(Context context, String registrationId) {
//        super.onRegister(context, registrationId);
        LogUtils.errorFormat("注册成功回调: registrationId=%s", registrationId);
        if (registrationId == null) return;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_REGISTER, registrationId));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E9%95%BF%E8%BF%9E%E6%8E%A5%E7%8A%B6%E6%80%81%E5%9B%9E%E8%B0%83">长连接状态回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.3.0
     * @param isConnected 长连接状态
     */
    @Override
    public void onConnected(Context context, boolean isConnected) {
//        super.onConnected(context, isConnected);
        LogUtils.errorFormat("长连接状态回调: isConnected=%b", isConnected);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_CONNECTED, isConnected));
    }

    public static final String[] PLATFORMS = {"unkown", "小米", "华为", "魅族", "OPPO", "VIVO", "ASUS", "荣耀", "FCM"};
    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E4%BA%A4%E4%BA%92%E4%BA%8B%E4%BB%B6%E5%9B%9E%E8%B0%83">交互事件回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.3.0
     * @param cmdMessage 交互事件回调信息。
     * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *     <tr>
     *         <th align="center">{@link CmdMessage#cmd}</th>
     *         <th align="center">{@link CmdMessage#errorCode}</th>
     *         <th align="center">{@link CmdMessage#msg}</th>
     *         <th align="center">DESCRIPTION</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>	失败 code</td>
     *         <td>失败信息</td>
     *         <td>注册失败</td>
     *     </tr>
     *     <tr>
     *         <td>1000</td>
     *         <td>0</td>
     *         <td>错误信息</td>
     *         <td>自定义消息展示错误</td>
     *     </tr>
     *     <tr>
     *         <td>2003</td>
     *         <td>0 / 1</td>
     *         <td>not stop / stopped</td>
     *         <td>isPushStopped 异步回调</td>
     *     </tr>
     *     <tr>
     *         <td>2004</td>
     *         <td>0 / 1</td>
     *         <td>connected / not connect</td>
     *         <td>getConnectionState 异步回调</td>
     *     </tr>
     *     <tr>
     *         <td>2005</td>
     *         <td>0</td>
     *         <td>对应 rid</td>
     *         <td>getRegistrationID 异步回调</td>
     *     </tr>
     *     <tr>
     *         <td>2006</td>
     *         <td>0</td>
     *         <td>set success</td>
     *         <td>onResume 设置回调</td>
     *     </tr>
     *     <tr>
     *         <td>2007</td>
     *         <td>0</td>
     *         <td>set success</td>
     *         <td>onStop 设置回调</td>
     *     </tr>
     *     <tr>
     *         <td>2008</td>
     *         <td>0</td>
     *         <td>success</td>
     *         <td>应用冷启动后，SDK 首次初始化成功的回调(只回调一次)</td>
     *     </tr>
     *     <tr>
     *         <td>10000</td>
     *         <td>0</td>
     *         <td>无</td>
     *         <td>厂商 token 注册回调，通过 extra 可获取对应 platform 和 token 信息</td>
     *     </tr>
     * </table>
     */
    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
//        super.onCommandResult(context, cmdMessage);
        LogUtils.errorFormat("交互事件回调: cmdMessage=%s", cmdMessage);
        if (cmdMessage == null) return;
        if (cmdMessage.cmd == 0) {
            LogUtils.errorFormat("注册失败: errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 1000) {
            LogUtils.errorFormat("自定义消息展示错误: errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 2003) {
            LogUtils.errorFormat("isPushStopped 异步回调: errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 2004) {
            LogUtils.errorFormat("getConnectionState 异步回调: errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 2005) {
            LogUtils.errorFormat("getRegistrationID 异步回调: errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 2006) {
            LogUtils.errorFormat("onResume 设置回调: errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 2007) {
            LogUtils.errorFormat("onStop 设置回调: errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 2008) {
            LogUtils.errorFormat("应用冷启动后，SDK 首次初始化成功的回调(只回调一次): errorCode=%d, msg=%s", cmdMessage.errorCode, cmdMessage.msg);
        } else if (cmdMessage.cmd == 10000) {
            /**
             * 厂商通道 Token 回调说明:
             * https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%8E%82%E5%95%86%E9%80%9A%E9%81%93-token-%E5%9B%9E%E8%B0%83%E8%AF%B4%E6%98%8E
             * 回调时机:
             *  在 sdk 绑定 token 成功时回调, 当 sdk 版本存在变更时会再次触发回调。
             *  存在回调时 token 为 null 的情况: 1. 当集成存在问题会回调 null。 2. 因为厂商服务等问题获取不到厂商 token 时。
             */
            Bundle extra = cmdMessage.extra;
            if (extra == null) return;
            String token = extra.getString("token");
            int platform = extra.getInt("platform");
            String deviceName = (platform >= 0 && platform < PLATFORMS.length) ? PLATFORMS[platform] : PLATFORMS[0];
            LogUtils.errorFormat("厂商通道 Token 回调: token=%s, platform=%d, deviceName=%s", token, platform, deviceName);
        } else {
            LogUtils.errorFormat("未知命令: cmd=%d, errorCode=%d, msg=%s", cmdMessage.cmd, cmdMessage.errorCode, cmdMessage.msg);
        }
        EventBus.getDefault().post(new JPushEvent<>(TYPE_COMMAND_RESULT, cmdMessage));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E9%80%9A%E7%9F%A5%E7%9A%84-multiaction-%E5%9B%9E%E8%B0%83">通知的 MultiAction 回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.3.2 <br />
     * {@link null 注意:} 这个方法里面禁止再调 super.onMultiActionClicked, 因为会导致逻辑混乱
     * @param intent 点击后触发的 Intent
     * @see cn.jpush.android.api.JPushInterface#ACTION_NOTIFICATION_CLICK_ACTION
     */
    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
//        super.onMultiActionClicked(context, intent);
        LogUtils.errorFormat("通知的 MultiAction 回调(用户点击了通知栏按钮): intent=%s", intent);
        if (intent == null) return;
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        String nActionExtra = extras.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);
        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            LogUtils.error("ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
        } else if (nActionExtra.equals("my_extra1")) {
            LogUtils.error("[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            LogUtils.error("[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            LogUtils.error("[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            LogUtils.error("[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
        EventBus.getDefault().post(new JPushEvent<>(TYPE_MULTIACTION_CLICKED, intent));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E9%80%9A%E7%9F%A5%E5%BC%80%E5%85%B3%E7%8A%B6%E6%80%81%E5%9B%9E%E8%B0%83">通知开关状态回调</a> <br />
     * 开始支持的版本：Android JPush SDK v3.5.0 <br />
     * 说明:
     * <ol>
     *     <li>该方法会在以下情况触发时回调。</li>
     *     <li>sdk 每次启动后都会检查通知开关状态并通过该方法回调给开发者。 2.当 sdk 检查到通知状态变更时会通过该方法回调给开发者。</li>
     *     <li>说明: sdk 内部检测通知开关状态的方法因系统差异，在少部分机型上可能存在兼容问题(判断不准确)。</li>
     * </ol>
     * @param isOn 通知开关状态
     * @param source 触发场景，0 为 sdk 启动，1 为检测到通知开关状态变更
     */
    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
//        super.onNotificationSettingsCheck(context, isOn, source);
        LogUtils.errorFormat("通知开关状态回调: isOn=%b, source=%d", isOn, source);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFICATION_SETTINGS_CHECK, isOn, source));
    }



    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%8B%89%E5%8F%96%E5%9B%B4%E6%A0%8F%E5%88%97%E8%A1%A8%E5%9B%9E%E8%B0%83">拉取围栏列表回调</a> <br />
     * 开始支持的版本：Android JPush SDK v4.0.7
     * @param geofences 地理围栏列表，返回围栏列表为空时，表示当前状态无围栏业务。json格式见上方链接
     */
    @Override
    public void onGeofenceReceived(Context context, String geofences) {
//        super.onGeofenceReceived(context, geofences);
        LogUtils.errorFormat("拉取围栏列表回调: geofences=%s", geofences);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_GEOFENCE_RECEIVED, geofences));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E8%A7%A6%E5%8F%91%E5%9B%B4%E6%A0%8F%E5%9B%9E%E8%B0%83">触发围栏回调</a> <br />
     * 开始支持的版本：Android JPush SDK v4.0.7
     * @param geofence 触发围栏详情。json格式见上方链接
     * @param longitude 触发时经度
     * @param latitude 触发时维度
     */
    @Override
    public void onGeofenceRegion(Context context, String geofence, double longitude, double latitude) {
//        super.onGeofenceRegion(context, geofence, longitude, latitude);
        LogUtils.errorFormat("触发围栏回调: geofence=%s, longitude=%f, latitude=%f", geofence, longitude, latitude);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_GEOFENCE_REGION, new GeofenceRegionInfo(geofence, longitude, latitude)));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%BA%94%E7%94%A8%E5%86%85%E6%B6%88%E6%81%AF%E5%B1%95%E7%A4%BA%E5%9B%9E%E8%B0%83">应用内消息展示回调</a> <br />
     * 应用内消息成功展示，会在此方法中回调结果。
     * 开始支持的版本：Android JPush SDK v5.0.0
     * @param message 应用内消息展示的内容
     */
    @Override
    public void onInAppMessageShow(Context context, NotificationMessage message) {
//        super.onInAppMessageShow(context, message);
        LogUtils.errorFormat("应用内消息展示回调: message=%s", message);
        if (message == null) return;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_IN_APP_MESSAGE_SHOW, message));
    }

    /**
     * <a href="https://docs.jiguang.cn/jpush/client/Android/android_api#%E5%BA%94%E7%94%A8%E5%86%85%E6%B6%88%E6%81%AF%E7%82%B9%E5%87%BB%E5%9B%9E%E8%B0%83">应用内消息点击回调</a> <br />
     * 应用内消息被用户点击，会在此方法中回调结果。
     * 开始支持的版本：Android JPush SDK v5.0.0
     * @param message 应用内消息点击的内容
     */
    @Override
    public void onInAppMessageClick(Context context, NotificationMessage message) {
//        super.onInAppMessageClick(context, message);
        LogUtils.errorFormat("应用内消息点击回调: message=%s", message);
        if (message == null) return;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_IN_APP_MESSAGE_CLICK, message));
    }





    /**
     * 通知未展示的回调
     * 1.3.5.8之后支持推送时指定前台不展示功能，当通知未展示时，会回调该接口
     */
    @Override
    public void onNotifyMessageUnShow(Context context, NotificationMessage notificationMessage) {
//        super.onNotifyMessageUnShow(context, notificationMessage);
        LogUtils.errorFormat("通知未展示的回调: context=%s, notificationMessage=%s", context, notificationMessage);
        if (notificationMessage == null) return;
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFYMESSAGE_UNSHOW, notificationMessage));
    }

    @Override
    public Notification getNotification(Context context, NotificationMessage notificationMessage) {
//        return super.getNotification(context, notificationMessage);
        LogUtils.errorFormat("getNotification: context=%s, notificationMessage=%s", context, notificationMessage);
        return JPushUtils.getNotification(context, notificationMessage);
    }

    @Override
    public boolean isNeedShowNotification(Context context, NotificationMessage notificationMessage, String s) {
        LogUtils.errorFormat("isNeedShowNotification: context=%s, notificationMessage=%s, s=%s", context, notificationMessage, s);
//        return super.isNeedShowNotification(context, notificationMessage, s);
        return JPushUtils.isNeedShowNotification(context, notificationMessage, s);
    }

}
