package com.ly.sample;

import android.support.annotation.NonNull;

import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.greendao.gen.ItemEntityDao;
import com.ly.sample.utils.Global;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Description: 类的描述
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/7/27 on 02:35
 * @version 1.1
 */
public class MyApplication extends ActorApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * @param context application
         * @param isDebug 如果是debug模式, 数据库操作会打印日志
         * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 示例:
         *                   ItemEntityDao.class(由'Build -> Make Project'生成), ...
         */
        GreenDaoUtils.init(this, isDebugMode, ItemEntityDao.class/*, ...*/);
    }

    //配置Builder
    @Override
    protected OkHttpClient.Builder getOkHttpClientBuilder(OkHttpClient.Builder builder) {
        builder.connectTimeout(60_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
                .readTimeout(60_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
                .writeTimeout(60_000L, TimeUnit.MILLISECONDS);//默认10s, 可不设置
//                .addInterceptor(new AddHeaderInterceptor())//网络请求前添加请求头, 如果不添加可不设置
//                .addInterceptor(new My401Error$RefreshTokenInterceptor(this));//在某个项目中,401表示token过期,需要刷新token并重新请求, 根据自己项目而定
        return builder;
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
