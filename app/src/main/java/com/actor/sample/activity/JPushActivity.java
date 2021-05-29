package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.utils.jpush.JPushEvent;
import com.actor.myandroidframework.utils.jpush.JPushUtils;
import com.actor.myandroidframework.utils.jpush.MyJPushMessageReceiver;
import com.actor.sample.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;

/**
 * description: 极光推送
 * author     : ldf
 * date       : 2020/3/24 on 20:13
 *
 * @version 1.0
 */
public class JPushActivity extends BaseActivity {

    @BindView(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpush);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setTitle("主页->极光推送");
    }

    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start://开始接收
                JPushUtils.resumePush(activity);//恢复推送服务
//                JPushUtils.setAlias(activity, 0, "actor");//设置别名, 还有其他推送方式
                break;
            case R.id.btn_stop://停止接收
                JPushUtils.stopPush(activity);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(JPushEvent<CustomMessage> event) {
        tvResult.setText("");//clear
        //这儿接收自定义消息, 还有其它的消息类型
        if (event != null && event.code == MyJPushMessageReceiver.TYPE_MESSAGE) {
            CustomMessage message = event.obj;
            if (message != null) {
                tvResult.setText(message.toString());
            }
        }
    }

    //收到别名操作相关(如果你设置了别名的话, 用下方方法接收别名设置结果)
    //别名设置可能多次失败, 所以判断如果设置失败的话, 需要再次设置!
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedAlias(JPushEvent<JPushMessage> event) {
        if (event == null) return;
        JPushMessage message = event.obj;
        if (message == null) return;
        switch (event.code) {
            case MyJPushMessageReceiver.TYPE_ALIAS_OPERATOR_RESULT://alias(别名) 相关的操作
                int errorCode = message.getErrorCode();
                //别名设置错误, 常见错误6002: 设置超时  建议重试，一般出现在网络不佳、初始化尚未完成时。
                if (errorCode != 0) {
                    logError("别名设置失败!");
                    JPushUtils.setAlias(activity, 0, "这儿填写需再次设置的别名");//设置别名, 还有其他推送方式
                } else {
                    logError("别名设置成功!");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        JPushUtils.stopPush(activity);//停止接收
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
