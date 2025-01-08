package com.actor.sample.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.audio.MediaPlayerCallback;
import com.actor.myandroidframework.utils.audio.MediaPlayerUtils;
import com.actor.myandroidframework.utils.audio.MediaRecorderCallback;
import com.actor.myandroidframework.utils.audio.MediaRecorderUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.others.utils.tts.TextToSpeechUtils;
import com.actor.others.utils.tts.UtteranceProgressListenerImpl;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityAudioMediaBinding;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
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

    private String audioPath;

    //刘明湘-漂洋过海来看你(抖音版ProgHouse)（阿祥 remix）
    private final String MUSIC = "https://player.yinyueke.net/api/index.php?server=netease&type=url&id=1351664561";
    //下方播放有点问题, 无语
//    private final String MUSIC = "https://picture.halzwl.cn/picture/error/b24ecce9d181423dbc24eaf234181b61lmx-pyghlkn.mp3";
//    private final String MUSIC = "http://qxbzlgx.mtwlkj.net/lmx-pyghlkn.mp3";

    //本地声音
    private String pathWrong = PathUtils.getInternalAppFilesPath() + "/wrong.mp3";
    //网络
    private String netStudent = "https://qc.zhizunnet.cn/words/enAudio/936.mp3";

    private int audioSessionIdMusic = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Audio & Media");
        MediaRecorderUtils.getInstance().setMaxRecordTimeMs(10 * 1000);
        TextToSpeechUtils.init(this, null, null);

        //
        ResourceUtils.copyFileFromRaw(R.raw.wrong, pathWrong);
    }

    @Override
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_permission:
                boolean isGranted = XXPermissions.isGranted(this, Permission.RECORD_AUDIO);
                if (isGranted) {
                    ToasterUtils.success("请求权限成功!");
                } else {
                    XXPermissions.with(this).permission(Permission.RECORD_AUDIO).request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (allGranted) ToasterUtils.success("请求权限成功!");
                        }
                    });
                }
                break;
            case R.id.btn_start_record: //开始录音
                MediaRecorderUtils.getInstance().startRecordM4a(new MediaRecorderCallback() {
                    @Override
                    public void recordComplete(String audioPath, long durationMs) {
                        ToasterUtils.successFormat("录制完成, audioPath=%s, durationMs=%d", audioPath, durationMs);
                        AudioMediaActivity.this.audioPath = audioPath;
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
            case R.id.btn_stop_record:  //停止录音
                MediaRecorderUtils.getInstance().stopRecord(false);
                String recordAudioPath = MediaRecorderUtils.getInstance().getRecordAudioPath();
                LogUtils.errorFormat("录制文件, recordAudioPath=%s", recordAudioPath);
                break;
            case R.id.btn_play_record:  //播放录音
                if (!TextUtils.isEmpty(audioPath)) {
                    MediaPlayerUtils.getInstance().play(audioPath, true, new MediaPlayerCallback() {
                        @Override
                        public void onCompletion2(@Nullable MediaPlayer mp) {
                            LogUtils.errorFormat("播放完成: audioPath=%s", audioPath);
                        }
                    });
                }
                break;


            case R.id.btn_start_play_raw0:  //不复用MP播放
                MediaPlayerUtils.getInstance().playRaw(R.raw.one_kun, null);
                break;
            case R.id.btn_start_play_raw1:  //复用MP播放
                MediaPlayerUtils.getInstance().playRaw(R.raw.one_kun, false, null);
                break;
            case R.id.btn_start_play_raw2:  //不复用MP循环播放
                MediaPlayerUtils.getInstance().playRaw(R.raw.right, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                        view.callOnClick();
                    }
                });
                break;
            case R.id.btn_start_play_raw3:  //复用MP循环播放
                MediaPlayerUtils.getInstance().playRaw(R.raw.right, false, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                        view.callOnClick();
                    }
                });
                break;


            case R.id.btn_start_play_local0:  //不复用MP播放'本地'
                MediaPlayerUtils.getInstance().play(pathWrong, true, null);
                break;
            case R.id.btn_start_play_local1:  //复用MP播放'本地'
                MediaPlayerUtils.getInstance().play(pathWrong, null);
                break;
            case R.id.btn_start_play_local2:  //不复用MP循环播放'本地'
                MediaPlayerUtils.getInstance().play(pathWrong, true, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                        view.callOnClick();
                    }
                });
                break;
            case R.id.btn_start_play_local3:  //复用MP循环播放'本地'
                MediaPlayerUtils.getInstance().play(pathWrong, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                        view.callOnClick();
                    }
                });
                break;


            case R.id.btn_start_play_net0:  //不复用MP播放'网络'
                MediaPlayerUtils.getInstance().play(netStudent, true, null);
                break;
            case R.id.btn_start_play_net1:  //复用MP播放'网络'
                MediaPlayerUtils.getInstance().play(netStudent, null);
                break;
            case R.id.btn_start_play_net2:  //不复用MP循环播放'网络'
                MediaPlayerUtils.getInstance().play(netStudent, true, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                        view.callOnClick();
                    }
                });
                break;
            case R.id.btn_start_play_net3:  //复用MP循环播放'网络'
                MediaPlayerUtils.getInstance().play(netStudent, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                        view.callOnClick();
                    }
                });
                break;

            case R.id.btn_release_all:      //停止播放全部MP
                MediaPlayerUtils.getInstance().stopAll();
                break;


            case R.id.btn_start_play:
                MediaPlayerUtils.getInstance().play(MUSIC, new MediaPlayerCallback() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        super.onPrepared(mp);
                        audioSessionIdMusic = mp.getAudioSessionId();
                    }
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        super.onCompletion(mp);
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


            case R.id.btn_tts_play: //系统Tts播放输入的内容
                CharSequence content = viewBinding.etContent.getText();
                TextToSpeechUtils.speak(content, new UtteranceProgressListenerImpl() {
                    @Override
                    public void onDone2(String utteranceId) {
                        ToasterUtils.success("播放完成!");
                    }
                });
                break;
            case R.id.btn_tts_stop: //停止Tts
                TextToSpeechUtils.stop();
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
        TextToSpeechUtils.shutdown();
    }
}