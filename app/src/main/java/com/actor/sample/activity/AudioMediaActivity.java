package com.actor.sample.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.audio.MediaPlayerUtils;
import com.actor.myandroidframework.utils.audio.MediaPlayerCallback;
import com.actor.myandroidframework.utils.audio.MediaRecorderCallback;
import com.actor.myandroidframework.utils.audio.MediaRecorderUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityAudioMediaBinding;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

/**
 * description: 音频录制, 音频播放, 视频播放
 * company    :
 * @author    : ldf
 * date       : 2024/2/20 on 11:16
 */
public class AudioMediaActivity extends BaseActivity<ActivityAudioMediaBinding> {

    //刘明湘-漂洋过海来看你(抖音版ProgHouse)（阿祥 remix）
    private final String MUSIC = "https://player.yinyueke.net/api/index.php?server=netease&type=url&id=1351664561";
    //下方播放有点问题, 无语
//    private final String MUSIC = "https://picture.halzwl.cn/picture/error/b24ecce9d181423dbc24eaf234181b61lmx-pyghlkn.mp3";
//    private final String MUSIC = "http://qxbzlgx.mtwlkj.net/lmx-pyghlkn.mp3";

    private int audioSessionIdMusic = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Audio & Media");
        MediaRecorderUtils.getInstance().setMaxRecordTimeMs(10 * 1000);
    }

    @Override
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_permission:
                boolean isGranted = XXPermissions.isGranted(this, Permission.RECORD_AUDIO);
                if (!isGranted) {
                    XXPermissions.with(this).permission(Permission.RECORD_AUDIO).request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (allGranted) ToasterUtils.success("请求权限成功!");
                        }
                    });
                }
                break;
            case R.id.btn_start_record:
                MediaRecorderUtils.getInstance().startRecordM4a(new MediaRecorderCallback() {
                    @Override
                    public void recordComplete(String audioPath, long durationMs) {
                        ToasterUtils.successFormat("录制完成, audioPath=%s, durationMs=%d", audioPath, durationMs);
                    }
                    @Override
                    public void recordCancel(String audioPath, long durationMs) {
                        ToasterUtils.warningFormat("录制取消, audioPath=%s, durationMs=%d", audioPath, durationMs);
                    }
                    @Override
                    public void recordError(@NonNull Exception e) {
                        ToasterUtils.errorFormat("录制出错, e=%s", e);
                    }
                });
                break;
            case R.id.btn_stop_record:
                MediaRecorderUtils.getInstance().stopRecord(false);
                String recordAudioPath = MediaRecorderUtils.getInstance().getRecordAudioPath();
                LogUtils.errorFormat("录制文件, recordAudioPath=%s", recordAudioPath);
                break;


            case R.id.btn_start_play_raw:
                MediaPlayerUtils.getInstance().playRaw(R.raw.one_kun, null);
                break;
            case R.id.btn_start_play:
                MediaPlayerUtils.getInstance().play(MUSIC, new MediaPlayerCallback() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        super.onPrepared(mp);
                        audioSessionIdMusic = mp.getAudioSessionId();
                    }

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        ToasterUtils.success("播放完成!");
                    }
                });
                break;
            case R.id.btn_pause_play:
                MediaPlayerUtils.getInstance().pause(audioSessionIdMusic);
                break;
            case R.id.btn_continue_play:
                MediaPlayerUtils.getInstance().start(audioSessionIdMusic);
                break;
            case R.id.btn_stop_play:
                MediaPlayerUtils.getInstance().stop(audioSessionIdMusic);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        MediaRecorderUtils.getInstance().releaseMediaRecorder();
        MediaPlayerUtils.getInstance().releaseAll();
    }
}