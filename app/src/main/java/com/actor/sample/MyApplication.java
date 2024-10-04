package com.actor.sample;

import com.actor.chat_layout.ChatLayoutKit;
import com.actor.chat_layout.emoji.DefaultEmojiList;
import com.actor.jpush.JPushUtils;
import com.actor.map.baidu.BaiduLocationUtils;
import com.actor.map.baidu.BaiduMapUtils;
import com.actor.map.gaode.GaoDe3DMapUtils;
import com.actor.map.gaode.GaoDeLocationUtils;
import com.actor.myandroidframework.application.ActorApplication;
import com.actor.database.greendao.GreenDaoUtils;
import com.actor.myandroidframework.utils.easyhttp.EasyHttpConfigUtils;
import com.actor.myandroidframework.utils.okhttputils.OkHttpConfigUtils;
import com.actor.qq_wechat.QQUtils;
import com.actor.qq_wechat.WeChatUtils;
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

    @Override
    public void onCreate() {
        super.onCreate();

        //配置轮子哥的EasyHttp
        OkHttpClient.Builder builder = EasyHttpConfigUtils.initOkHttp(isAppDebug());
        //然后可以在 builder 中自定义设置, 添加拦截器等
        //builder.xxx
        //EasyHttp的日志打印不大科学, 使用这个拦截器打印日志
        OkHttpConfigUtils.addLogInterceptor(builder, true);
        EasyHttpConfigUtils.init(false, Global.BASE_URL_GITHUB, builder.build());


        /**
         * 数据库使用示例
         * @param isDebug 如果是debug模式, 数据库操作会打印日志
         * @param dbName 数据库名称
         * @param dbPassword 数据库密码(如果数据库没有加密, 密码传null)
         * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 示例:
         *                   ItemEntityDao.class(由'Build -> Make Project'生成), ...
         */
        GreenDaoUtils.init(this, isAppDebug(), "test_db.db3", "123456", ItemEntityDao.class/*, ...*/);


        /**
         * 聊天示例
         * 如果需要使用emoji表情, 需要在Application中初始化(如果不使用emoji, 不要初始化)
         * 也可以不使用 DefaultEmojiList.DEFAULT_EMOJI_LIST 这些emoji, 可以自定义后传入
         */
        ChatLayoutKit.init(DefaultEmojiList.DEFAULT_EMOJI_LIST, "emoji");



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
        JPushUtils.setAuth(this, true); //同意隐私政策
        JPushUtils.init(this, null);    //初始化
        JPushUtils.stopPush(this);      //停止推送, 防止未登录就接收到消息
        //JPushUtils.setAlias(this, 0, "");//瞎设置一个别名, 作用是接收不到消息(设置""好像没作用? 下次设置更复杂的字符串)



        //QQ
        QQUtils.setIsPermissionGranted(true);
        //在Application中设置appId, 一般是一串数字(我这儿设置的appid是QQ2786985624申请的)
        QQUtils.setAppId("101890804");//222222



        //在Application中设置appId
        WeChatUtils.setAppId("wx88888888");
    }

    @Override
    protected void onUncaughtException(Throwable e) {
        super.onUncaughtException(e);
    }
}
