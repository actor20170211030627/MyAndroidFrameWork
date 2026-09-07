package com.actor.sample.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

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
import java.util.Set;

/**
 * description: 音频录制, 音频播放, 视频播放
 * company    :
 * @author    : ldf
 * date       : 2024/2/20 on 11:16
 */
public class AudioMediaActivity extends BaseActivity<ActivityAudioMediaBinding> {

    private String audioPath;

    //https://gitee.com/UYADDAYU/majsoul_custom_charactor
//    private final String MUSIC = "https://gitee.com/UYADDAYU/majsoul_custom_charactor/raw/master/bgm/e1.mp3";
    //下方播放有点问题, 无语
//    private final String MUSIC = "https://picture.halzwl.cn/picture/error/b24ecce9d181423dbc24eaf234181b61lmx-pyghlkn.mp3";
    //刘明湘-漂洋过海来看你(抖音版ProgHouse)（阿祥 remix）
    private final String MUSIC = "http://qxbzlgx.mtwlkj.net/lmx-pyghlkn.mp3";
    private final String MUSIC2 = "https://music.163.com/song/media/outer/url?id=1646740.mp3";

    //本地声音
    private String pathWrong = PathUtils.getInternalAppFilesPath() + "/wrong.mp3";
    //student 网络
    private String netStudent = "https://qc.zhizunnet.cn/words/enAudio/936.mp3";

    //404 Not Found
//    private String netPhoneticNotFound = "http://bdcyuyin.mtwlkj.net:8115/skill-yuyin/yinbiaofayin/us/a%CA%8A.mp3";
    private String netPhoneticNotFound = "http://bdcyuyin.mtwlkj.net:8115/skill-yuyin/yinbiaofayin/us/aʊ.mp3";

    private MediaPlayer mpRaw, mpHttp;
    private MediaPlayerCallback mpRawCallback, mpHttpCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Audio & Media");
        MediaRecorderUtils.getInstance().setMaxRecordTimeMs(10 * 1000);
        TextToSpeechUtils.init(this, null, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    LogUtils.errorFormat("SpeechListener, 初始化成功!");
                } else {
                    LogUtils.errorFormat("SpeechListener, 初始化失败! status=%d", status);
                }
                Voice voice = TextToSpeechUtils.getVoice();
                Voice defaultVoice = TextToSpeechUtils.getDefaultVoice();
                Set<Voice> voices = TextToSpeechUtils.getVoices();
                LogUtils.errorFormat("voice = %s", voice);
                LogUtils.errorFormat("defaultVoice = %s", defaultVoice);
                if (voices != null) {
                    for (Voice voice1 : voices) {
                        LogUtils.errorFormat("voice1 = %s", voice1);
                    }
                } else LogUtils.error("voices = null");
            }
        });

        //
        ResourceUtils.copyFileFromRaw(R.raw.wrong, pathWrong);

        //播放速度系数
        viewBinding.seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float speed = progress / 1000f;
                    boolean isSuccess = MediaPlayerUtils.getInstance().setPlaySpeed(mpHttp, speed);
                    LogUtils.errorFormat("speed = %f, isSuccess = %b", speed, isSuccess);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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
                MediaPlayerUtils.getInstance().playRaw(R.raw.right, true, true, true, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                    }
                });
                break;
            case R.id.btn_start_play_raw3:  //复用MP循环播放
                MediaPlayerUtils.getInstance().playRaw(R.raw.right, true, true, false, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
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
                MediaPlayerUtils.getInstance().play(pathWrong, true, true, true, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                    }
                });
                break;
            case R.id.btn_start_play_local3:  //复用MP循环播放'本地'
                MediaPlayerUtils.getInstance().play(pathWrong, true, true, false, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
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
                MediaPlayerUtils.getInstance().play(netStudent, true, true, true, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                    }
                });
                break;
            case R.id.btn_start_play_net3:  //复用MP循环播放'网络'
                MediaPlayerUtils.getInstance().play(netStudent, true, true, false, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                    }
                });
                break;
            case R.id.btn_start_play_net_404_0:  //不复用MP播放'网络404'
                MediaPlayerUtils.getInstance().play(netPhoneticNotFound, false, true, true, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                    }
                });
                break;
            case R.id.btn_start_play_net_404_1:  //复用MP播放'网络404'
                MediaPlayerUtils.getInstance().play(netPhoneticNotFound, false, true, false, new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        LogUtils.error("onCompletion");
                    }
                });
                break;

            case R.id.btn_release_all:      //stopAll()停止播放全部MP
                MediaPlayerUtils.getInstance().stopAll();
                break;


            case R.id.btn_start_play_raw:
                initMpRawAndPlay();
                break;
            case R.id.btn_pause_play_raw:
                MediaPlayerUtils.getInstance().pause(mpRaw);
                break;
            case R.id.btn_continue_play_raw:
                MediaPlayerUtils.getInstance().start(mpRaw, mpRawCallback);
                break;
            case R.id.btn_stop_play_raw:
                MediaPlayerUtils.getInstance().stop(mpRaw, mpRawCallback);
                break;


            case R.id.btn_start_play_http:
                initMpRawAndHttp();
                break;
            case R.id.btn_pause_play_http:
                MediaPlayerUtils.getInstance().pause(mpHttp);
                break;
            case R.id.btn_continue_play_http:
                MediaPlayerUtils.getInstance().start(mpHttp, mpHttpCallback);
                break;
            case R.id.btn_stop_play_http:
                MediaPlayerUtils.getInstance().stop(mpHttp, mpHttpCallback);
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

    private void initMpRawAndPlay() {
        if (mpRaw == null) {
            mpRawCallback = new MediaPlayerCallback() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    super.onPrepared(mp);
                    mpRaw = mp;
                    //从MediaPlayerUtils中移除, 否则播放完成后会自动release()
                    MediaPlayerUtils.getInstance().getAllPlayers().remove(mp.getAudioSessionId());
                }
                @Override
                public void onCompletion2(@Nullable MediaPlayer mp) {
                    ToasterUtils.success("播放完成!");
                }
            };
            //为了不和下面那个 http 的长歌播放器复用到, 所以new 1个 MediaPlayer
            MediaPlayerUtils.getInstance().playRaw(R.raw.am_487_833_s161603081658, true, mpRawCallback);
        } else {
            MediaPlayerUtils.getInstance().start(mpRaw, mpRawCallback);
            //if .stop() 后, 调用了↑ .start() -> prepare(), 还在prepare中就调用seekTo()会报错...
//            MediaPlayerUtils.getInstance().seekTo(mpRaw, mpRawCallback, 0);
        }
    }

    private void initMpRawAndHttp() {
        if (mpHttp == null) {
            mpHttpCallback = new MediaPlayerCallback() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    super.onPrepared(mp);
                    mpHttp = mp;
                    //从MediaPlayerUtils中移除, 否则播放完成后会自动release()
                    MediaPlayerUtils.getInstance().getAllPlayers().remove(mp.getAudioSessionId());
                }
                @Override
                public void onCompletion2(@Nullable MediaPlayer mp) {
                    ToasterUtils.success("播放完成!");
                }
            };
            //为了不和上面那个 raw 的长歌播放器复用到, 所以new 1个 MediaPlayer
            MediaPlayerUtils.getInstance().play(MUSIC, true, mpHttpCallback);
        } else {
            MediaPlayerUtils.getInstance().start(mpHttp, mpHttpCallback);
            //if .stop() 后, 调用了↑ .start() -> prepare(), 还在prepare中就调用seekTo()会报错...
//            MediaPlayerUtils.getInstance().seekTo(mpHttp, mpHttpCallback, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        MediaRecorderUtils.getInstance().releaseMediaRecorder();
        MediaPlayerUtils.getInstance().releaseAll();
        MediaPlayerUtils.getInstance().release(mpRaw, mpRawCallback);
        MediaPlayerUtils.getInstance().release(mpHttp, mpHttpCallback);
        TextToSpeechUtils.shutdown();
    }
}