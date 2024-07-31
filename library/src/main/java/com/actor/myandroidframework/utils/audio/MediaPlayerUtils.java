package com.actor.myandroidframework.utils.audio;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 播放音频
 *
 * @author     : ldf
 * @date       : 2019/5/30 on 17:43
 */
public class MediaPlayerUtils {

    protected MediaPlayer         mMediaPlayer;
    protected int                 DEFAULT_AUDIO_SESSION_ID = 0;  //默认播放id
    //默认回调
    protected MediaPlayerCallback DEFAULT_MEDIA_PLAYER_CALLBACK = new MediaPlayerCallback() {
        @Override
        public void onCompletion(@Nullable MediaPlayer mp) {
            if (mp != null) releaseMediaPlayer(mp.getAudioSessionId());
        }
    };
    protected Map<Integer, MediaPlayerCallback> playerMap = new HashMap<>(5);

    protected static MediaPlayerUtils instance;


    public static MediaPlayerUtils getInstance() {
        if (instance == null) instance = new MediaPlayerUtils();
        return instance;
    }

    /**
     * 播放R.raw.xxx 音频
     * @param rawId 资源id
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param playerCallback 播放回调
     */
    public void playRaw(@RawRes int rawId, boolean isLooping, boolean isAutoPlay,
                        @Nullable MediaPlayerCallback playerCallback) {
        playRaw(rawId, isLooping, isAutoPlay, true, playerCallback);
    }
    /**
     * 播放R.raw.xxx 音频
     * @param rawId 资源id
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param playerCallback 播放回调
     * @param isNewMediaPlayer 是否使用新的MediaPlayer, 而不是已存在的那个.(如果想同时播放多个, 传true)
     */
    public void playRaw(@RawRes int rawId, boolean isLooping, boolean isAutoPlay,
                        boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        if (isNewMediaPlayer) {
            MediaPlayer mMediaPlayer = null;
            try {
                mMediaPlayer = MediaPlayer.create(ConfigUtils.APPLICATION, rawId);   //nullable
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setVolume(1, 1);
                mMediaPlayer.setLooping(isLooping);

                if (playerCallback == null) playerCallback = DEFAULT_MEDIA_PLAYER_CALLBACK;
                playerCallback.isAutoPlay = isAutoPlay;
                playerCallback.mp = mMediaPlayer;

                int audioSessionId = mMediaPlayer.getAudioSessionId();
                playerMap.put(audioSessionId, playerCallback);

                //准备完成监听
                mMediaPlayer.setOnPreparedListener(playerCallback);
                //播放出错监听
                mMediaPlayer.setOnErrorListener(playerCallback);
                mMediaPlayer.setOnCompletionListener(playerCallback);

                /**
                 * 播放本地的时候, .prepare() 会报错: prepareAsync called in state 8, mPlayer(0x7e0c7f62c0)
                 * 会自动准备完毕
                 */
//                mMediaPlayer.prepareAsync();
//                mMediaPlayer.prepare();
            } catch (NullPointerException | IllegalStateException e) {
                e.printStackTrace();
                if (playerCallback != null) {
                    boolean isDealBySelf = playerCallback.onSetData2StartError(e);
                    if (!isDealBySelf) playerCallback.onCompletion(mMediaPlayer);
                }
            }
        } else {
            /**
             * ∵调用不到系统隐藏的api: int s = AudioSystem.newAudioSessionId(); 所以只能转存到本地再播放...
             */
            InputStream inputStream = ConfigUtils.APPLICATION.getResources().openRawResource(rawId);
            String path = PathUtils.getInternalAppFilesPath() + File.separator + rawId;
            boolean success = FileIOUtils.writeFileFromIS(path, inputStream);
            if (!success) LogUtils.errorFormat("raw(id=%d)写入内部文件失败: %s", rawId, path);
            play(path, isLooping, isAutoPlay, isNewMediaPlayer, playerCallback);
        }
    }

    /**
     * 播放音频
     * @param audioPath 本地/网络音频
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param playerCallback 播放监听
     */
    public void play(@NonNull String audioPath, boolean isLooping, boolean isAutoPlay,
                     @Nullable MediaPlayerCallback playerCallback) {
        play(audioPath, isLooping, isAutoPlay, false, playerCallback);
    }
    /**
     * 播放音频
     * @param audioPath 本地/网络音频
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param playerCallback 播放监听
     * @param isNewMediaPlayer 是否使用新的MediaPlayer, 而不是已存在的那个.(如果想同时播放多个, 传true)
     */
    public void play(@NonNull String audioPath, boolean isLooping, boolean isAutoPlay,
                     boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        if (TextUtils.isEmpty(audioPath)) {
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(new NullPointerException("audioPath is Empty!"));
                if (!isDealBySelf) {
                    playerCallback.onCompletion(null);
                }
            }
            return;
        }
        MediaPlayer mMediaPlayer = null;
        try {
            if (isNewMediaPlayer) {
                mMediaPlayer = new MediaPlayer();
            } else {
                if (this.mMediaPlayer == null) {
                    this.mMediaPlayer = new MediaPlayer();
                    DEFAULT_AUDIO_SESSION_ID = this.mMediaPlayer.getAudioSessionId();
                } else {
                    this.mMediaPlayer.reset();
                }
                mMediaPlayer = this.mMediaPlayer;
            }
//            AudioAttributes attrs = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
//            mMediaPlayer.setAudioAttributes(attrs);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //设置数据源, 本地or网上
            mMediaPlayer.setDataSource(audioPath);
            mMediaPlayer.setLooping(isLooping);

            if (playerCallback == null) playerCallback = DEFAULT_MEDIA_PLAYER_CALLBACK;
            playerCallback.isAutoPlay = isAutoPlay;
            playerCallback.mp = mMediaPlayer;

            int audioSessionId = mMediaPlayer.getAudioSessionId();
            playerMap.put(audioSessionId, playerCallback);

            //准备完成监听
            mMediaPlayer.setOnPreparedListener(playerCallback);
            //播放出错监听
            mMediaPlayer.setOnErrorListener(playerCallback);
//            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            mMediaPlayer.setOnCompletionListener(playerCallback);
            mMediaPlayer.prepareAsync();    //主线程中异步准备, 准备监听完成后开始播放
//            mMediaPlayer.prepare();
//            mMediaPlayer.start();
        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(e);
                if (!isDealBySelf) playerCallback.onCompletion(mMediaPlayer);
            }
        }
    }

    /**
     * 获取'播放'的音频的时长 (要先设置音频)
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @return 单位ms
     */
    public int getDuration(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null || playerCallback.mp == null) return -1;
        return playerCallback.mp.getDuration();
    }

    /**
     * 开始播放音频
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void startPlayer(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        try {
            if (playerCallback != null && playerCallback.mp != null) playerCallback.mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            boolean isDealBySelf = playerCallback.onSetData2StartError(e);
            if (!isDealBySelf) playerCallback.onCompletion(playerCallback.mp);
        }
    }

    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #startPlayer(int)}
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void pausePlayer(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        try {
            if (playerCallback != null && playerCallback.mp != null) playerCallback.mp.pause();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音频
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void stopPlayer(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) {
            MediaPlayer mp = playerCallback.mp;
            try {
                //开始 or 暂停 后, 可以停止
                if (mp != null) mp.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止播放所有音频
     */
    public void stopAllPlayer() {
        for (Integer audioSessionId : playerMap.keySet()) {
            stopPlayer(audioSessionId);
        }
    }

    /**
     * @return 是否正在播放
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public boolean isPlaying(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        return playerCallback != null && playerCallback.mp != null && playerCallback.mp.isPlaying();
    }

    /**
     * 释放播放器资源
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void releaseMediaPlayer(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) {
            stopPlayer(audioSessionId);
            MediaPlayer mp = playerCallback.mp;
            if (mp != null) {
                mp.release();
                playerCallback.mp = null;
                //默认播放器释放后, 要置空, 否则.reset()会报错
                if (audioSessionId == DEFAULT_AUDIO_SESSION_ID) {
                    DEFAULT_AUDIO_SESSION_ID = 0;
                    mMediaPlayer = null;
                }
            }
            playerMap.remove(audioSessionId);
        }
    }

    /**
     * 释放所有播放器资源
     */
    public void releaseAllMediaPlayer() {
        for (Integer audioSessionId : playerMap.keySet()) {
            releaseMediaPlayer(audioSessionId);
        }
    }
}
