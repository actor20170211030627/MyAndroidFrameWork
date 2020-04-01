package com.actor.myandroidframework.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actor.myandroidframework.R;

/**
 * description: 按住说话, 环信EaseUI里有一个 EaseVoiceRecorderView.java 可参考
 * author     : 李大发
 * date       : 2019/5/30 on 21:05
 * @version 1.0
 */
public class VoiceRecorderView extends RelativeLayout {

    protected ImageView         ivRecordingIcon;
    protected TextView          tvRecodingTips;
    protected AnimationDrawable mVolumeAnim;

    public VoiceRecorderView(Context context) {
        super(context);
        initView(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        View inflate = View.inflate(context, R.layout.view_voice_recorder, this);
        ivRecordingIcon = inflate.findViewById(R.id.iv_recording_icon);
        tvRecodingTips = inflate.findViewById(R.id.tv_recording_tips);
    }

    /**
     * 开始录音
     */
    public void startRecording() {
        ivRecordingIcon.setImageResource(R.drawable.animation_list_recording_volume);
        mVolumeAnim = (AnimationDrawable) ivRecordingIcon.getDrawable();
        setVisibility(View.VISIBLE);
        mVolumeAnim.start();
        tvRecodingTips.setTextColor(Color.WHITE);
        tvRecodingTips.setText("手指上滑，取消发送");
    }

    /**
     * 松开手指取消发送
     */
    public void release2CancelRecording() {
        ivRecordingIcon.setImageResource(R.drawable.ic_volume_cancel);
        tvRecodingTips.setTextColor(Color.RED);
        tvRecodingTips.setText("松开手指，取消发送");
    }

    /**
     * 停止录音
     */
    public void stopRecording() {
        mVolumeAnim.stop();
        setVisibility(View.GONE);
    }

    /**
     * 录音时间太短
     */
    public void tooShortRecording() {
        mVolumeAnim.stop();
        ivRecordingIcon.setImageResource(R.drawable.ic_volume_wraning);
        tvRecodingTips.setTextColor(Color.WHITE);
        tvRecodingTips.setText("录音时间太短");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        }, 1000);
    }
}
