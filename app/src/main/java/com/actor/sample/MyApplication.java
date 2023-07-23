package com.actor.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.chat_layout.ChatLayoutKit;
import com.actor.chat_layout.emoji.DefaultEmojiList;
import com.actor.map.baidu.BaiduLocationUtils;
import com.actor.map.baidu.BaiduMapUtils;
import com.actor.map.gaode.GaoDe3DMapUtils;
import com.actor.map.gaode.GaoDeLocationUtils;
import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.audio.AudioUtils;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.actor.myandroidframework.utils.jpush.JPushUtils;
import com.actor.sample.utils.Global;
import com.greendao.gen.ItemEntityDao;

import okhttp3.OkHttpClient;

/**
 * Description: 类的描述
 * Author     : ldf
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
         * 数据库使用示例
         * @param isDebug 如果是debug模式, 数据库操作会打印日志
         * @param dbName 数据库名称
         * @param dbPassword 数据库密码(如果数据库没有加密, 密码传null)
         * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 示例:
         *                   ItemEntityDao.class(由'Build -> Make Project'生成), ...
         */
        GreenDaoUtils.init(this, isAppDebug(), "test_db.db3", "123456", ItemEntityDao.class/*, ...*/);



        //百度定位, 先同意隐私政策
        BaiduLocationUtils.setAgreePrivacy(true);
        //百度地图, 先同意隐私政策
        BaiduMapUtils.setAgreePrivacy(true);
        BaiduMapUtils.init(this);



        //高德定位, 先同意隐私政策
        GaoDeLocationUtils.updatePrivacyShow(this, true, false);
        GaoDeLocationUtils.updatePrivacyAgree(this, true);
        //高德地图, 先同意隐私政策
        GaoDe3DMapUtils.updatePrivacyShow(this, true, true);
        GaoDe3DMapUtils.updatePrivacyAgree(this, true);



        //极光推送示例
        JPushUtils.setDebugMode(isAppDebug());//设置调试模式,在 init 接口之前调用
        JPushUtils.init(this);//初始化
        JPushUtils.stopPush(this);//停止推送, 防止未登录就接收到消息
        //JPushUtils.setAlias(this, 0, "");//瞎设置一个别名, 作用是接收不到消息(设置""好像没作用? 下次设置更复杂的字符串)



        /**
         * 聊天示例
         * 如果需要使用emoji表情, 需要在Application中初始化(如果不使用emoji, 不要初始化)
         * 也可以不使用 DefaultEmojiList.DEFAULT_EMOJI_LIST 这些emoji, 可以自定义后传入
         */
        ChatLayoutKit.init(DefaultEmojiList.DEFAULT_EMOJI_LIST, "emoji");
        //初始化聊天语音, 默认最大录音时长2分钟(如果不用语音, 不用初始化)
        AudioUtils.getInstance().init(null, null);
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder configOkHttpClientBuilder(@NonNull OkHttpClient.Builder builder) {
        return null;
    }
//    @Override
//    protected void configEasyHttp(EasyHttp easyHttp) {
//    }

    @NonNull
    @Override
    protected String getBaseUrl(boolean isDebugMode) {
        return Global.BASE_URL_GITHUB;
    }

    /**
     * release环境中App崩溃的时候会回调这个方法.
     * 如果是debug环境, 不会抓取bug并且不会回调这个方法
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
    protected void onUncaughtException(Throwable e) {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        //定时器
//        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1000:1秒钟后重启应用
        System.exit(-1);//退出
    }
}
