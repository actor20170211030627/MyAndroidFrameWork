package com.actor.myandroidframework.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.NotificationUtils;

/**
 * Description: Notification帮助类, 参考: <a href="https://www.cnblogs.com/whycxb/p/10077418.html" target="_blank">博客</a> <br />
 * Author     : ldf <br />
 * Date       : 2019/7/18 on 15:40
 * @version 1.0
 */
public class NotificationHelper {

    /**
     * Notifications是否可以显示出来
     */
    public static boolean areNotificationsEnabled() {
//        return NotificationManagerCompat.from(context).areNotificationsEnabled();
        return NotificationUtils.areNotificationsEnabled();
    }

    /**
     * 跳转到 APP 的"通知设置界面"
     */
    public static void gotoSet(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // android 8.0引导
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            //android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");//Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);//Settings.EXTRA_APP_UID
        } else {
            // 其他
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
