package com.actor.myandroidframework.utils.jpush;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.actor.myandroidframework.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * http://docs.jiguang.cn/jpush/client/Android/android_api/#new-callback
 * http://docs.jiguang.cn/jpush/client/Android/android_api/#_75
 * 新的消息回调方式说明
 * 1.新的消息回调方式中相关回调类。
 * 2.新的 tag 与 alias 操作回调会在开发者定义的该类的子类中触发。
 * 3.手机号码设置的回调会在开发者定义的该类的子类中触发。
 * 4.新回调方式与旧的自定义Receiver兼容：
 *   配置了此Receiver以后，默认是也会发广播给旧Receiver的
 *   对于onMessage、onNotifyMessageArrived、onNotifyMessageOpened、onMultiActionClicked
 *   如果重写了这些方法，则需要调用super才会发给旧Receiver
 *
 *
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 * */
public class MyJPushMessageReceiver extends JPushMessageReceiver {

    public static final int TYPE_MESSAGE = 3523472;//收到自定义消息回调
    public static final int TYPE_NOTIFY_MESSAGE_ARRIVED = 3523473;//收到通知回调
    public static final int TYPE_NOTIFYMESSAGE_OPENED = 3523474;//点击通知回调
    public static final int TYPE_NOTIFYMESSAGE_DISMISS = 3523475;//清除通知回调
    public static final int TYPE_REGISTER = 3523476;//注册成功回调
    public static final int TYPE_CONNECTED = 3523477;//长连接状态回调
    public static final int TYPE_COMMAND_RESULT = 3523478;//注册失败回调
    public static final int TYPE_NOTIFYMESSAGE_UNSHOW = 3523479;//通知未展示的回调
    public static final int TYPE_MULTIACTION_CLICKED = 3523480;//通知开关的回调
    public static final int TYPE_NOTIFICATION_SETTINGS_CHECK = 3523481;//通知开关的回调
    public static final int TYPE_TAG_OPERATOR_RESULT = 3523482;//增删查改的操作会在此方法中回调结果
    public static final int TYPE_CHECK_TAG_OPERATOR_RESULT = 3523483;//查询某个 tag 与当前用户的绑定状态
    public static final int TYPE_ALIAS_OPERATOR_RESULT = 3523484;//alias 相关的操作
    public static final int TYPE_MOBILE_NUMBER_OPERATOR_RESULT = 3523485;//设置手机号码

    /**
     * 收到自定义消息回调
     * @see cn.jpush.android.api.JPushInterface#ACTION_MESSAGE_RECEIVED
     */
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);//调用super才会发给旧Receiver
        logFormat("收到自定义消息回调: context=%s, customMessage=%s", context, customMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_MESSAGE, customMessage));
    }

    /**
     * 收到通知回调
     * @see cn.jpush.android.api.JPushInterface#ACTION_NOTIFICATION_RECEIVED
     */
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);//调用super才会发给旧Receiver
        logFormat("收到通知回调: context=%s, notificationMessage=%s", context, notificationMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFY_MESSAGE_ARRIVED, notificationMessage));
    }

    /**
     * 点击通知回调
     * @see cn.jpush.android.api.JPushInterface#ACTION_NOTIFICATION_OPENED
     */
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);//调用super才会发给旧Receiver
        logFormat("点击通知回调: context=%s, notificationMessage=%s", context, notificationMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFYMESSAGE_OPENED, notificationMessage));
    }

    /**
     * 清除通知回调
     * 1.同时删除多条通知，可能不会多次触发清除通知的回调
     * 2.只有用户手动清除才有回调，调接口清除不会有回调
     * @see cn.jpush.android.api.JPushInterface#?
     */
    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageDismiss(context, notificationMessage);
        logFormat("清除通知回调: context=%s, notificationMessage=%s", context, notificationMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFYMESSAGE_DISMISS, notificationMessage));
    }

    /**
     * 注册成功回调, 可获得 RegistrationID
     * @see JPushUtils#getRegistrationID(Context)
     * @param registrationId 注册id
     */
    @Override
    public void onRegister(Context context, String registrationId) {
        super.onRegister(context, registrationId);
        logFormat("注册成功回调: context=%s, registrationId=%s", context, registrationId);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_REGISTER, registrationId));
    }

    /**
     * 长连接状态回调
     * @param isConnected 长连接状态
     */
    @Override
    public void onConnected(Context context, boolean isConnected) {
        super.onConnected(context, isConnected);
        logFormat("长连接状态回调: context=%s, isConnected=%b", context, isConnected);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_CONNECTED, isConnected));
    }

    /**
     * 注册失败回调
     * @param cmdMessage 错误信息
     */
    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        super.onCommandResult(context, cmdMessage);
        logFormat("注册失败回调: context=%s, cmdMessage=%s", context, cmdMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_COMMAND_RESULT, cmdMessage));
    }

    /**
     * 通知未展示的回调
     * 1. 3.5.8之后支持推送时指定前台不展示功能，当通知未展示时，会回调该接口
     */
    @Override
    public void onNotifyMessageUnShow(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageUnShow(context, notificationMessage);
        logFormat("通知未展示的回调: context=%s, notificationMessage=%s", context, notificationMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFYMESSAGE_UNSHOW, notificationMessage));
    }

    /**
     * 通知的MultiAction回调
     * @param intent 点击后触发的Intent
     * @see cn.jpush.android.api.JPushInterface#ACTION_NOTIFICATION_CLICK_ACTION
     */
    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        super.onMultiActionClicked(context, intent);//调用super才会发给旧Receiver
        logFormat("通知的MultiAction回调: context=%s, intent=%s", context, intent);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_MULTIACTION_CLICKED, intent));
    }

    /**
     * 通知开关的回调
     * 该方法会在以下情况触发时回调。
     *   1.sdk每次启动后都会检查通知开关状态并通过该方法回调给开发者。
     *   2.当sdk检查到通知状态变更时会通过该方法回调给开发者。
     *
     * 说明: sdk内部检测通知开关状态的方法因系统差异，在少部分机型上可能存在兼容问题(判断不准确)。
     * @param isOn 通知开关状态
     * @param source 触发场景，0为sdk启动，1为检测到通知开关状态变更
     */
    @Override
    public void onNotificationSettingsCheck(Context context,boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        logFormat("通知开关的回调: context=%s, isOn=%b, source=%d", context, isOn, source);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_NOTIFICATION_SETTINGS_CHECK, isOn, source));
    }

    @Override
    public Notification getNotification(Context context, NotificationMessage notificationMessage) {
        logFormat("getNotification: context=%s, notificationMessage=%s", context, notificationMessage);
//        return super.getNotification(context, notificationMessage);
        return JPushUtils.getNotification();
    }

    @Override
    public boolean isNeedShowNotification(Context context, NotificationMessage notificationMessage, String s) {
        logFormat("isNeedShowNotification: context=%s, notificationMessage=%s, s=%s", context, notificationMessage, s);
//        return super.isNeedShowNotification(context, notificationMessage, s);
        return JPushUtils.isNeedShowNotification();
    }

    /**
     * tag 增删查改的操作会在此方法中回调结果。
     * jPushMessage.getTags() 相关操作返回的消息结果体，具体参考 JPushMessage 类的说明。
     */
    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
//        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_TAG_OPERATOR_RESULT, jPushMessage));

        super.onTagOperatorResult(context, jPushMessage);
        logFormat("tag 增删查改: context=%s, jPushMessage=%s", context, jPushMessage);
    }

    /**
     * 查询某个 tag 与当前用户的绑定状态的操作会在此方法中回调结果。
     * jPushMessage.getCheckTag() 与当前用户绑定状态的操作返回的消息结果体，具体参考 JPushMessage 类的说明。
     */
    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage){
//        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context, jPushMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_CHECK_TAG_OPERATOR_RESULT, jPushMessage));

        super.onCheckTagOperatorResult(context, jPushMessage);
        logFormat("查询某个 tag 与当前用户的绑定状态: context=%s, jPushMessage=%s", context, jPushMessage);
    }

    /**
     * alias 相关的操作会在此方法中回调结果。
     * jPushMessage.getAlias() 相关操作返回的消息结果体，具体参考 JPushMessage 类的说明。
     */
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
//        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_ALIAS_OPERATOR_RESULT, jPushMessage));

        super.onAliasOperatorResult(context, jPushMessage);
        logFormat("alias(别名) 相关的操作: context=%s, jPushMessage=%s", context, jPushMessage);
    }

    /**
     * 设置手机号码会在此方法中回调结果。
     * @param jPushMessage 设置手机号码返回的消息结果体，具体参考 JPushMessage 类的说明。
     */
    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
//        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context, jPushMessage);
        EventBus.getDefault().post(new JPushEvent<>(TYPE_MOBILE_NUMBER_OPERATOR_RESULT, jPushMessage));

        super.onMobileNumberOperatorResult(context, jPushMessage);
        logFormat("设置手机号码: context=%s, jPushMessage=%s", context, jPushMessage);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    protected void logError(String msg) {
        LogUtils.error(msg, false);
    }
}
