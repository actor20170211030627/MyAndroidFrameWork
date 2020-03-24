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

/**
 * description: 极光推送
 * company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * author     : 李大发
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
        if (event != null && event.code == MyJPushMessageReceiver.TYPE_MESSAGE) {
            CustomMessage message = event.obj;
            if (message != null) {
                tvResult.setText(message.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        JPushUtils.stopPush(activity);//停止接收
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
