package com.actor.sample.activity;

import android.os.Bundle;

import com.actor.myandroidframework.utils.ThreadUtils;
import com.actor.sample.databinding.ActivityOtherBinding;

/**
 * Description: 主页->线程, 权限, SPUtils, EventBus
 * Author     : ldf
 * Date       : 2019-9-9 on 16:16
 */
public class OtherActivity extends BaseActivity<ActivityOtherBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->线程, 权限, SPUtils, EventBus");

        ///////////////////////////////////////////////////////////////////////////
        // 1.ThreadUtils线程
        ///////////////////////////////////////////////////////////////////////////
        ThreadUtils.runOnUiThread(new Runnable() {//在主线程运行
            @Override
            public void run() {
                logError(ThreadUtils.isMainThread());//是否运行在主线程
            }
        });
        ThreadUtils.runOnSubThread(new Runnable() {//在子线程运行
            @Override
            public void run() {
                logError(ThreadUtils.isMainThread());
            }
        });
        ThreadUtils.HANDLER.postDelayed(new Runnable() {//延时一秒后, 在主线程运行
            @Override
            public void run() {
                logError("ThreadUtils.handler.postDelayed");
            }
        }, 1_000L);


        ///////////////////////////////////////////////////////////////////////////
        // 2.PermissionRequestUtils权限
        ///////////////////////////////////////////////////////////////////////////
//        PermissionRequestUtils.requestPermission();
//        PermissionRequestUtils.go2Setting();
//        PermissionRequestUtils.installApk();
        //...


        ///////////////////////////////////////////////////////////////////////////
        // 3.SPUtils
        ///////////////////////////////////////////////////////////////////////////
//        SPUtils.putBoolean();
//        SPUtils.putInt();
//        SPUtils.putString();
//        SPUtils.putFloat();
//        SPUtils.putLong();
//        SPUtils.putStringSet();

//        SPUtils.getBoolean();
//        SPUtils.getInt();
//        SPUtils.getString();
//        SPUtils.getStringNoNull();//如果为null, 返回 ""
//        SPUtils.getFloat();
//        SPUtils.getLong();
//        SPUtils.getStringSet();
//        SharedPreferences sharedPreference = SPUtils.getSharedPreference();
//        Map<String, ?> all = SPUtils.getAll();

//        SPUtils.remove("key");
//        SPUtils.removeAll();
//        boolean contains = SPUtils.contails("key");
//        SPUtils.registerOnSharedPreferenceChangeListener();
//        SPUtils.unregisterOnSharedPreferenceChangeListener();


        ///////////////////////////////////////////////////////////////////////////
        // 4.EventBus
        ///////////////////////////////////////////////////////////////////////////
//        EventBus.getInstance().post(new EventBusEvent<>(1, "msg"));
//        EventBus.getInstance().post(new EventBusEvent<GithubInfo>(1, new GithubInfo()));
//        EventBus.getInstance().post(new EventBusEvent<GithubInfo>(1, "msg", new GithubInfo()));
    }
}
