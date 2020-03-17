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

    protected String channelId = toString();
    protected String name;
    protected int id = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        //适配8.0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            name = getResources().getString(R.string.app_name);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(), channelId).build();
            startForeground(id, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
