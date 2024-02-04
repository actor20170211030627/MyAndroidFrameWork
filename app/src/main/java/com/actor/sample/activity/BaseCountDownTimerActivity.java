package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import com.actor.myandroidframework.utils.BaseCountDownTimer;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityBaseCountDownTimerBinding;

/**
 * description: BaseCountDownTimer 测试
 * company    :
 * @author    : ldf
 * date       : 2024/1/31 on 11:57
 */
public class BaseCountDownTimerActivity extends BaseActivity<ActivityBaseCountDownTimerBinding> {

    private final BaseCountDownTimer countDownTimer = new BaseCountDownTimer(5 * 1000L, 500L) {
        @Override
        public void onTick(long millisUntilFinished) {
            viewBinding.tvResult.setText(String.valueOf(millisUntilFinished / 1000));
            viewBinding.editText.setText("实际计时: " + getTimingDuration());
        }

        @Override
        public void onFinish() {
            viewBinding.tvResult.setText("倒计时完成: " + getTimingDuration());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BaseCountDownTimer测试");
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_2:
                countDownTimer.start();
                break;
            case R.id.btn_3:
                countDownTimer.setMillisInFuture(10 * 1000L).setCountdownInterval(300L).start();
                break;
            case R.id.btn_4:
                countDownTimer.cancel();
                viewBinding.editText.setText("实际计时2: " + countDownTimer.getTimingDuration());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}