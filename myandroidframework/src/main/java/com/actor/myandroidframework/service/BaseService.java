package com.actor.myandroidframework.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.actor.myandroidframework.R;

/**
 * Description: Service 基类
 * Author     : 李大发
 * Date       : 2020/3/16 on 16:40
 *
 * @version 1.0
 */
public class BaseService extends Service {

    /**
     * 是否以 startForegroundService 的方式启动
     */
    public static final String IS_START_FOREGROUND_SERVICE = "IS_START_FOREGROUND_SERVICE";
    protected String channelId = toString();
    protected String name;
    protected int id = 1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean foreground = intent.getBooleanExtra(IS_START_FOREGROUND_SERVICE, false);
        //如果是以 startForegroundService 的方式启动
        if (foreground) {
            fitForegroundService();
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
