package com.actor.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.album.AlbumUtils;
import com.actor.myandroidframework.utils.baidu.BaiduMapUtils;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.actor.myandroidframework.utils.jpush.JPushUtils;
import com.actor.sample.utils.Global;
import com.greendao.gen.ItemEntityDao;

import okhttp3.OkHttpClient;

/**
 * Description: 类的描述
 * Author     : 李大发
 * Date       : 2019/7/27 on 02:35
 * @version 1.1
 */
public class MyApplication extends ActorApplication {

    public static MyApplication   instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //配置画廊, 图片/视频选择
        AlbumUtils.init(this);

        /**
         * @param context application
         * @param isDebug 如果是debug模式, 数据库操作会打印日志
         * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 示例:
         *                   ItemEntityDao.class(由'Build -> Make Project'生成), ...
         */
        GreenDaoUtils.init(this, isAppDebug(), ItemEntityDao.class/*, ...*/);

        BaiduMapUtils.init(this);//初始化百度地图

        //Application中初始化极光推送
        JPushUtils.setDebugMode(isAppDebug());//设置调试模式,在 init 接口之前调用
        JPushUtils.init(this);//初始化
        JPushUtils.stopPush(this);//停止推送, 防止未登录就接收到消息
        //JPushUtils.setAlias(this, 0, "");//瞎设置一个别名, 作用是接收不到消息(设置""好像没作用? 下次设置更复杂的字符串)
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder configOkHttpClientBuilder(OkHttpClient.Builder builder) {
        return null;
    }
//    @Override
//    protected void configEasyHttp(EasyHttp easyHttp) {
//    }

    @NonNull
    @Override
    protected String getBaseUrl(boolean isDebugMode) {
        return Global.BASE_URL;
    }

    /**
     *
     * @param thread 线程
     * @param e 堆栈信息
     *
     * 示例处理:
     * Intent intent = new Intent(this, LoginActivity.class);
     * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);
     * //定时器
     * AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
     * mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1000:1秒钟后重启应用
     * System.exit(-1);//退出
     */
    @Override
    protected void onUncaughtException(Thread thread, Throwable e) {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        //定时器
//        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1000:1秒钟后重启应用
        System.exit(-1);//退出
    }
}
