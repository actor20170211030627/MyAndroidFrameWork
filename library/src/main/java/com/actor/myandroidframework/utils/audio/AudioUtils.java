package com.actor.myandroidframework.utils.audio;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.actor.myandroidframework.utils.BaseCountDownTimer;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * description: 音频录制, 播放 <br />
 * <a href="https://blog.csdn.net/weixin_44008788/article/details/122260697">Android 多媒体框架之音频录制 MediaRecorder 和 AudioRecorder_android mediarecorder-CSDN博客</a> <br />
 * <a href="https://cloud.tencent.com/document/product/269/3794">一天接入 SDK-即时通信 IM-文档中心-腾讯云</a> <br />
 * <a href="https://github.com/tencentyun/TIMSDK">TencentCloud_TIMSDK - Github</a> <br />
 * <br />
 * Android 多媒体框架针对音频录制提供了两种方法：MediaRecorder和AudioRecord。 <br />
 * AudioRecord和MediaRecorder两种都可以录制音频，MediaRecorder已实现大量的封装，操作起来更加简单，而AudioRecord使用起来更加灵活，能实现更多的功能。 <br />
 * <br />
 * MediaRecorder: 已集成了录音，编码，压缩等，支持少量的音频格式文件。 <br />
 * 优点：封装度很高，操作简单 <br />
 * 缺点：无法实现实时处理音频，输出的音频格式少。 <br />
 * MediaRecorder 录制的音频文件是经过压缩后的，需要设置编码器，并且录制的音频文件可以用系统自带的播放器播放。MediaRecorder属于系统API高度封装，所以可扩展性和可用性都比较局限，支持的格式过少并且无法实时处理音频数据，使用场景如语音消息录制等，值得一提的是MediaRecorder通常和视频录制一起使用。 <br />
 *
 * <ol>
 *     <li>
 *         如果需要录音, 需要在清单文件中添加权限: <br />
 *         <code>&lt;uses-permission android:name="android.permission.RECORD_AUDIO" /&gt; <br /></code>
 *     </li>
 *     <li>
 *         就可以使用了
 *     </li>
 * </ol>
 * 3.开始录音等 <br />
 *
 * author     : ldf
 * date       : 2019/5/30 on 17:43
 */
public class AudioUtils {

    //是否正在录制中
    protected boolean               isRecording;

    //录制的语音存储目录: /data/data/package/files/Records
    protected String                recordDir       = PathUtils.getInternalAppFilesPath();
    protected int                   maxRecordTimeMs = 2 * 60 * 1000;    //最大录音时长, 默认2分钟
    protected String                recordAudioPath;                    //录音文件具体地址
    protected MediaRecorder         mMediaRecorder;
    protected MediaRecorderCallback mRecorderCallback;
    protected BaseCountDownTimer    countDownTimer;



    //是否正在播放中
//    protected boolean             isPlaying;
    protected MediaPlayer         mMediaPlayer;
    protected MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mPlayerCallback != null) mPlayerCallback.onStartPlay();
            startPlayer(); //开始播放
        }
    };
    protected MediaPlayerCallback mPlayerCallback;

    protected static AudioUtils   instance;


    public static AudioUtils getInstance() {
        if (instance == null) instance = new AudioUtils();
        return instance;
    }



    ///////////////////////////////////////////////////////////////////////////
    // 下方是录制
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 设置录制的语音存储目录(有默认值, 可以不设置)
     */
    public void setRecordDir(@NonNull String recordDir) {
        if (!TextUtils.isEmpty(recordDir)) this.recordDir = recordDir;
    }

    /**
     * 设置最大录音时长
     * @param maxRecordTimeMs 最大录音时长, 单位毫秒(默认2分钟, 可以不设置)
     */
    public void setMaxRecordTimeMs(@IntRange(from = 1) int maxRecordTimeMs) {
        if (maxRecordTimeMs > 0) this.maxRecordTimeMs = maxRecordTimeMs;
    }

    /**
     * 获取倒计时
     */
    @NonNull
    public BaseCountDownTimer getCountDownTimer() {
        if (countDownTimer == null) {
            countDownTimer = new BaseCountDownTimer(maxRecordTimeMs, 80) {
                @Override
                protected void onTick(long millisUntilFinished) {
                }
                @Override
                protected void onFinish() {
                    stopRecord(false);  //停止录音
                }
            };
        }
        return countDownTimer;
    }

    /**
     * 录制.amr格式音频
     * @param callback 录制回调
     */
    public void startRecordAmr(@Nullable MediaRecorderCallback callback) {
        startRecord(MediaRecorder.AudioSource.MIC, MediaRecorder.OutputFormat.RAW_AMR, MediaRecorder.AudioEncoder.AMR_NB, ".amr", callback);
    }

    /**
     * 录制.3gp格式音频
     * @param callback 录制回调
     */
    public void startRecord3gp(@Nullable MediaRecorderCallback callback) {
        startRecord(MediaRecorder.AudioSource.MIC, MediaRecorder.OutputFormat.THREE_GPP, MediaRecorder.AudioEncoder.AMR_NB, ".3gp", callback);
    }

    /**
     * 录制.m4a格式音频
     * @param callback 录制回调
     */
    public void startRecordM4a(@Nullable MediaRecorderCallback callback) {
        startRecord(MediaRecorder.AudioSource.MIC, MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC, ".m4a", callback);
    }

    /**
     * 录制音频
     * @param audioSource 音频源:
     *                    <ol>
     *                        <li>{@link MediaRecorder.AudioSource#DEFAULT}: 默认音频源</li>
     *                        <li>{@link MediaRecorder.AudioSource#MIC}: 麦克风音频源</li>
     *                        <li>{@link MediaRecorder.AudioSource#VOICE_UPLINK}: 语音呼叫上行链路 （Tx） 音频源。</li>
     *                        <li>{@link MediaRecorder.AudioSource#VOICE_DOWNLINK}: 语音呼叫下行链路 （Rx） 音频源。</li>
     *                        <li>{@link MediaRecorder.AudioSource#VOICE_CALL}: 语音通话上行+下行音频源</li>
     *                        <li>{@link MediaRecorder.AudioSource#CAMCORDER}: 麦克风音频源调整为视频录制，与相机相同的方向（如果可用）。</li>
     *                        <li>{@link MediaRecorder.AudioSource#VOICE_RECOGNITION}: 麦克风音频源经过调谐，可进行语音识别。</li>
     *                        <li>{@link MediaRecorder.AudioSource#VOICE_COMMUNICATION}: 针对 VoIP 等语音通信进行调整的麦克风音频源。例如，它将利用回声消除或自动增益控制（如果可用）。</li>
     *                        <li>{@link MediaRecorder.AudioSource#REMOTE_SUBMIX}: 用于远程呈现的音频流子混合的音频源。</li>
     *                        <li>{@link MediaRecorder.AudioSource#UNPROCESSED}: 麦克风音频源调整为未处理的(原始)声音（如果可用），行为类似{@link MediaRecorder.AudioSource#DEFAULT}。</li>
     *                        <li>{@link MediaRecorder.AudioSource#VOICE_PERFORMANCE}: 用于捕获音频的源，旨在实时处理并播放以进行现场表演（例如卡拉 OK）。</li>
     *                    </ol>
     * @param outputFormat 输出格式:
     *                     <ol>
     *                         <li>{@link MediaRecorder.OutputFormat#DEFAULT}: 默认输出格式</li>
     *                         <li>{@link MediaRecorder.OutputFormat#THREE_GPP}: 3GP 文件格式(.3gp也是一种视频格式，H263视频/ARM音频编码)</li>
     *                         <li>{@link MediaRecorder.OutputFormat#MPEG_4}: MPEG-4 文件格式(3gp也是一种视频格式)</li>
     *                         <li>{@link MediaRecorder.OutputFormat#RAW_AMR}: 只支持音频且音频编码要求为AMR_NB</li>
     *                         <li>{@link MediaRecorder.OutputFormat#AMR_NB}: AMR-NB 文件格式</li>
     *                         <li>{@link MediaRecorder.OutputFormat#AMR_WB}: AMR-WB 文件格式</li>
     *                         <li>{@link MediaRecorder.OutputFormat#AAC_ADTS}: AAC ADTS 文件格式</li>
     *                         <li>{@link MediaRecorder.OutputFormat#MPEG_2_TS}: </li>
     *                         <li>{@link MediaRecorder.OutputFormat#WEBM}: WebM 文件格式</li>
     *                         <li>{@link MediaRecorder.OutputFormat#OGG}: </li>
     *                     </ol>
     * @param audioEncoder 音频编码器:
     *                     <ol>
     *                         <li>{@link MediaRecorder.AudioEncoder#DEFAULT}: 默认音频编码器(声音的（波形）的采样?)</li>
     *                         <li>{@link MediaRecorder.AudioEncoder#AMR_NB}: AMR-NB 音频编码器</li>
     *                         <li>{@link MediaRecorder.AudioEncoder#AMR_WB}: AMR-WB 音频编码器</li>
     *                         <li>{@link MediaRecorder.AudioEncoder#AAC}: AAC 音频编码器</li>
     *                         <li>{@link MediaRecorder.AudioEncoder#HE_AAC}: 高效 AAC（HE-AAC）音频编码器</li>
     *                         <li>{@link MediaRecorder.AudioEncoder#AAC_ELD}: AAC ELD 音频编码器</li>
     *                         <li>{@link MediaRecorder.AudioEncoder#VORBIS}: </li>
     *                         <li>{@link MediaRecorder.AudioEncoder#OPUS}: </li>
     *                     </ol>
     * @param suffix 音频后缀, 例: .amr, .3gp, .m4a
     * @param callback 录制回调
     */
    public void startRecord(int audioSource, int outputFormat, int audioEncoder, @NonNull String suffix, @Nullable MediaRecorderCallback callback) {
        if (isRecording) {
            LogUtils.error("正在录制中, 请先停止录制!");
            if (callback != null) callback.recordError(new IllegalStateException("正在录制中, 请先停止录制!"));
            return;
        }
        mRecorderCallback = callback;
        try {
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            } else {
//                if (isRecording) mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
            //设置音频源: setAudioSource, setVideoSource
            mMediaRecorder.setAudioSource(audioSource);
            //设置输出格式
            mMediaRecorder.setOutputFormat(outputFormat);
            //设置音频编码
            mMediaRecorder.setAudioEncoder(audioEncoder);
            //年月日时分秒毫秒
            String fileName = TimeUtils.date2String(new Date(), "yyyyMMddHHmmssSSS");
            recordAudioPath = new File(recordDir, fileName + suffix).getAbsolutePath();
            //设置输出位置
            mMediaRecorder.setOutputFile(recordAudioPath);
            //准备
            mMediaRecorder.prepare();
            //设置倒计时
            getCountDownTimer().cancel();
            getCountDownTimer().setMillisInFuture(maxRecordTimeMs);
            //开始
            mMediaRecorder.start();
            getCountDownTimer().start();
            isRecording = true;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            if (mRecorderCallback != null) mRecorderCallback.recordError(e);
        }
    }

    /**
     * 是否正在录制中
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * 结束录音
     * @param isCanceled 是否已经取消录音(比如按住时 上滑取消)
     */
    public void stopRecord(boolean isCanceled) {
        //if不是正常的倒计时完成, 就手动取消倒计时
        if (getCountDownTimer().getState() != BaseCountDownTimer.Status.FINISH) getCountDownTimer().cancel();
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
                if (mRecorderCallback != null) {
                    if (isCanceled) {
                        mRecorderCallback.recordCancel(getRecordAudioPath(), getRecordDuration());
                    } else mRecorderCallback.recordComplete(getRecordAudioPath(), getRecordDuration());
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                if (mRecorderCallback != null) mRecorderCallback.recordError(e);
            }
            //置空回调
            mRecorderCallback = null;
        }
    }

    /**
     * 释放录音资源
     */
    public void releaseMediaRecorder() {
        getCountDownTimer().cancel();
        countDownTimer = null;
        mRecorderCallback = null;
        stopRecord(true);
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
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
    public long getRecordDuration() {
        if (countDownTimer == null) return 0;
        return countDownTimer.getTimingDuration();
    }



    ///////////////////////////////////////////////////////////////////////////
    // 下方是播放
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 播放R.raw.xxx 音频
     * @param rawId 资源id
     * @param isLooping 是否循环播放
     * @param callback 播放回调
     */
    public void playRaw(Context context, @RawRes int rawId, boolean isLooping, @Nullable MediaPlayerCallback callback) {
        mPlayerCallback = callback;
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, rawId);   //nullable
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.setLooping(isLooping);
            if (callback != null) mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mPlayerCallback != null) mPlayerCallback.playComplete(rawId, null);
                }
            });
            mediaPlayer.start();
            if (callback != null) callback.onStartPlay();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
            if (callback != null) callback.playError(e);
        }
    }

    /**
     * 播放音频
     * @param audioPath 本地/网络音频
     */
    public void play(@NonNull String audioPath, boolean isLooping, @Nullable MediaPlayerCallback callback) {
        mPlayerCallback = callback;
        try {
            //如果想同时播放多个, 这儿不判空, 直接new
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
//                AudioAttributes attrs = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
//                mMediaPlayer.setAudioAttributes(attrs);
            } else mMediaPlayer.reset();
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //设置数据源, 本地or网上
            mMediaPlayer.setDataSource(audioPath);
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
//            mMediaPlayer.setOnErrorListener(mOnErrorListener);
//            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            if (callback != null) mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mPlayerCallback != null) mPlayerCallback.playComplete(null, audioPath);
                }
            });
            mMediaPlayer.prepareAsync();    //主线程中异步准备, 准备监听完成后开始播放
//            mMediaPlayer.prepare();
//            mMediaPlayer.start();
        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
            if (callback != null) callback.playError(e);
        }
    }

    /**
     * 开始播放音频
     */
    public void startPlayer() {
        try {
            if (mMediaPlayer != null) mMediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #startPlayer()}
     */
    public void pausePlayer() {
        try {
            if (mMediaPlayer != null) mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音频
     */
    public void stopPlayer() {
        try {
            //开始 or 暂停 后, 可以停止
            if (mMediaPlayer != null) mMediaPlayer.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    /**
     * 释放播放器资源
     */
    public void releaseMediaPlayer() {
        mPlayerCallback = null;
        stopPlayer();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
