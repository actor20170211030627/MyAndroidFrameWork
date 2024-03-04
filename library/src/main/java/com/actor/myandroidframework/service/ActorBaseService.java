package com.actor.myandroidframework.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.actor.myandroidframework.R;

/**
 * Description: Service 基类 <br/>
 * Android 8.0 系统不允许后台应用创建后台服务。
 * 因此，Android 8.0 引入了一种全新的方法，即 context.startForegroundService()，以在前台启动新服务。
 * 在系统创建服务后，应用有五秒的时间来调用该服务的 startForeground() 方法以显示新服务的用户可见通知。
 * 如果应用在此时间限制内未调用 startForeground()，则系统将停止服务并声明此应用为 ANR
 * <p>
 * Author     : ldf
 * Date       : 2020/3/16 on 16:40
 *
 * @version 1.0
 *
 * @see com.blankj.utilcode.util.ServiceUtils
 */
public abstract class ActorBaseService extends Service {

    /**
     * 是否以 startForegroundService 的方式启动
     */
    public static final String IS_START_FOREGROUND_SERVICE = "IS_START_FOREGROUND_SERVICE";
    protected           String channelId                   = toString();
    protected           String name;
    //用于notification notify 使用, 不能为0
    protected           int    id                          = 1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean foreground = intent.getBooleanExtra(IS_START_FOREGROUND_SERVICE, false);
            //如果是以 startForegroundService 的方式启动
            if (foreground) fitForegroundService();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 适配后台Service, 示例写法, 可重写此方法
     */
    protected void fitForegroundService() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            name = getResources().getString(R.string.app_name);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(), channelId).build();
            startForeground(id, notification);
        } else {
            // TODO: 2020/5/19 适配低版本
        }
    }
}
