package com.actor.sample;

import android.support.annotation.NonNull;

import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.baidu.BaiduLocationUtils;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.actor.myandroidframework.utils.jpush.JPushUtils;
import com.actor.sample.utils.Global;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.greendao.gen.ItemEntityDao;
import com.zhouyou.http.EasyHttp;
import com.zhy.http.okhttp.OkHttpUtils;

/**
 * Description: 类的描述
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
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

        /**
         * @param context application
         * @param isDebug 如果是debug模式, 数据库操作会打印日志
         * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 示例:
         *                   ItemEntityDao.class(由'Build -> Make Project'生成), ...
         */
        GreenDaoUtils.init(this, isDebugMode, ItemEntityDao.class/*, ...*/);

        //百度定位配置
        BaiduLocationUtils.setLocOption(BaiduLocationUtils.getDefaultLocationClientOption());

        //下方是百度地图, 如果用到地图需要初始化
        SDKInitializer.initialize(this);//初始化百度地图
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //Application中初始化极光推送
        JPushUtils.setDebugMode(isDebugMode);//设置调试模式,在 init 接口之前调用
        JPushUtils.init(this);//初始化
        JPushUtils.stopPush(this);//停止推送, 防止未登录就接收到消息
        //JPushUtils.setAlias(this, 0, "");//瞎设置一个别名, 作用是接收不到消息(设置""好像没作用? 下次设置更复杂的字符串)

        OkHttpUtils.initClient(EasyHttp.getOkHttpClient());//配置张鸿洋的OkHttpUtils
    }

    @Override
    protected void configEasyHttp(EasyHttp easyHttp) {
    }

    @NonNull
    @Override
    protected String getBaseUrl() {
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
