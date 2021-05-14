package com.actor.myandroidframework.utils.audio;


import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;

/**
 * description: 抄自腾讯TUIKit Demo中的录音和播放: UIKitAudioArmMachine.java
 * https://cloud.tencent.com/document/product/269/3794
 * https://github.com/tencentyun/TIMSDK
 * https://github.com/tencentyun/TIMSDK/tree/master/Android
 *
 * 1.在清单文件中添加权限:
 *   <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 * 2.在Application中初始化:
 *   AudioUtils.getInstance().init(60, null);
 *
 * 3.开始录音
 *
 * author     : 李大发
 * date       : 2019/5/30 on 17:43
 * TODO: 2020/4/1 逻辑理顺一下...&hihi是录制的什么格式音频?
 */
public class AudioUtils {

    protected boolean playing, innerRecording;
    protected volatile Boolean recording           = false;
    public    String           recordDir;//语音存储目录

    protected String recordAudioPath;//录音文件地址
    protected long   startTime, endTime;
    protected        MediaPlayer   mPlayer;
    protected        MediaRecorder mRecorder;
    protected          AudioRecordCallback mRecordCallback;
    protected          AudioPlayCallback   mPlayCallback;
    protected static AudioUtils    instance;
    protected int           maxRecordTime = 2 * 60 * 1000;//最大录音时长, 默认2分钟

    /**
     * 初始化
     * @param maxRecordTimeSecond 最大录音时长, 单位秒, 默认2分钟, 可以传null
     * @param recordDir 设置录音文件存放路径, 默认: getFilesDir()
     */
    public void init(@Nullable Integer maxRecordTimeSecond, @Nullable String recordDir) {
        if (TextUtils.isEmpty(recordDir)) {
            this.recordDir = FileUtils.getFilesDir().getAbsolutePath();
        } else this.recordDir = recordDir;
        if (maxRecordTimeSecond != null) maxRecordTime = maxRecordTimeSecond * 1000;
    }

    public static AudioUtils getInstance() {
        if (instance == null) instance = new AudioUtils();
        return instance;
    }

    /**
     * 开始录音
     */
    public void startRecord(AudioRecordCallback callback) {
        synchronized (recording) {
            mRecordCallback = callback;
            recording = true;
            new RecordThread().start();
        }
    }

    /**
     * 结束录音
     * @param isCanceled 是否已经取消录音(比如按住时 上滑取消)
     */
    public void stopRecord(boolean isCanceled) {
        synchronized (recording) {
            if (recording) {
                recording = false;
                endTime = System.currentTimeMillis();
                if (mRecorder != null && innerRecording) {
                    try {
                        innerRecording = false;
                        mRecorder.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mRecordCallback != null) {
                    if (isCanceled) {
                        mRecordCallback.recordCancel(getRecordAudioPath(), endTime - startTime);//子线程
                    } else mRecordCallback.recordComplete(getRecordAudioPath(), endTime - startTime);//子线程
                }
            }
        }
    }

    /**
     * 播放录音
     */
    public void playRecord(String filePath, AudioPlayCallback callback) {
        this.mPlayCallback = callback;
        new PlayThread(filePath).start();
    }

    /**
     * 停止播放录音
     */
    public void stopPlayRecord() {
        if (mPlayer != null) {
            mPlayer.stop();
            playing = false;
            if (mPlayCallback != null) mPlayCallback.playComplete(null);
        }
    }

    /**
     * @return 是否正在播放
     */
    public boolean isPlayingRecord() {
        return playing;
    }


    /**
     * @return 获取录音地址
     */
    public String getRecordAudioPath() {
        return recordAudioPath;
    }

    /**
     * @return 获取录音时长, 单位ms
     */
    public long getDuration() {
        return endTime - startTime;
    }

    public interface AudioRecordCallback {
        /**
         * 录制完成
         * @param audioPath 语音路径
         * @param durationMs 语音时长, 单位ms
         */
        void recordComplete(String audioPath, long durationMs);
        void recordCancel(String audioPath, long durationMs);//取消录音
        void recordError(Exception e);//录音失败
    }

    public interface AudioPlayCallback {
        void playComplete(@Nullable String audioPath);
        void playError(String audioPath, String errorReason);
    }


    protected class RecordThread extends Thread {
        @Override
        public void run() {
            //根据采样参数获取每一次音频采样大小
            try {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //RAW_AMR虽然被高版本废弃，但它兼容低版本还是可以用的
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                recordAudioPath = new File(recordDir, System.currentTimeMillis() + ".amr")
                        .getAbsolutePath();
                mRecorder.setOutputFile(recordAudioPath);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                startTime = System.currentTimeMillis();
                synchronized (recording) {
                    if (!recording) return;
                    mRecorder.prepare();
                    mRecorder.start();
                }
                innerRecording = true;
                new Thread() {
                    @Override
                    public void run() {
                        while (recording && innerRecording) {
                            try {
                                RecordThread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //TencentImUtils.getBaseConfigs().getAudioRecordMaxTime() * 1000
                            if (System.currentTimeMillis() - startTime >= maxRecordTime) {
                                stopRecord(false);
                                return;
                            }
                        }
                    }
                }.start();

            } catch (Exception e) {
                e.printStackTrace();
                if (mRecordCallback != null) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecordCallback.recordError(e);
                        }
                    });
                }
            }
        }
    }


    protected class PlayThread extends Thread {
        String audioPath;

        PlayThread(String filePath) {
            audioPath = filePath;
        }

        public void run() {
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(audioPath);
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mPlayCallback != null) mPlayCallback.playComplete(audioPath);
                        playing = false;
                    }
                });
                mPlayer.prepare();
                mPlayer.start();
                playing = true;
            } catch (Exception e) {
                e.printStackTrace();
                if (mPlayCallback != null) mPlayCallback.playError(audioPath, "语音文件已损坏或不存在");
                playing = false;
            }
        }
    }
}
