package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.audio.MediaPlayerCallback;
import com.actor.myandroidframework.utils.audio.MediaRecorderCallback;
import com.actor.myandroidframework.utils.audio.AudioUtils;
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
//    private final String MUSIC = "https://player.yinyueke.net/api/index.php?server=netease&type=url&id=1351664561";
    private final String MUSIC = "https://picture.halzwl.cn/picture/error/b24ecce9d181423dbc24eaf234181b61lmx-pyghlkn.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Audio & Media");
        AudioUtils.getInstance().setMaxRecordTimeMs(10 * 1000);
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
                AudioUtils.getInstance().startRecordM4a(new MediaRecorderCallback() {
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
                AudioUtils.getInstance().stopRecord(false);
                String recordAudioPath = AudioUtils.getInstance().getRecordAudioPath();
                LogUtils.errorFormat("录制文件, recordAudioPath=%s", recordAudioPath);
                break;


            case R.id.btn_start_play_raw:
                AudioUtils.getInstance().playRaw(this, R.raw.one_kun, false, null);
                break;
            case R.id.btn_start_play:
                AudioUtils.getInstance().play(MUSIC, false, new MediaPlayerCallback() {
                    @Override
                    public void onStartPlay() {
                        ToasterUtils.warning("开始播放~");
                    }
                    @Override
                    public void playComplete(@Nullable Integer rawId, @Nullable String audioPath) {
                        ToasterUtils.success("播放完成!");
                    }
                    @Override
                    public void playError(@NonNull Exception e) {
                        ToasterUtils.error("播放错误!");
                    }
                });
                break;
            case R.id.btn_pause_play:
                AudioUtils.getInstance().pausePlayer();
                break;
            case R.id.btn_continue_play:
                AudioUtils.getInstance().startPlayer();
                break;
            case R.id.btn_stop_play:
                AudioUtils.getInstance().stopPlayer();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        AudioUtils.getInstance().releaseMediaRecorder();
        AudioUtils.getInstance().releaseMediaPlayer();
    }
}