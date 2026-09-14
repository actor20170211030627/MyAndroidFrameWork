package cn.jpush.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//不能写个 jpush.java 类, 因为 jpush sdk 有个 cn.jpush.android.service 这个包名, if 写成.java 会找到sdk 那个包那儿去, 导致找不到 JCommonService 就报错
public class JCommonService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
