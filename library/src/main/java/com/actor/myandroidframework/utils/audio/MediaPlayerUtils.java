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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 播放音频, 复用MediaPlayer播放完后默认不重置, 新的MediaPlayer播放完后要重置! <br />
 * if创建很多个MediaPlayer, 会报错: onError: what=1, extra=-19
 *
 * @author     : ldf
 * @date       : 2019/5/30 on 17:43
 */
public class MediaPlayerUtils {

    protected static MediaPlayer         mMediaPlayer;
    protected static int                 DEFAULT_AUDIO_SESSION_ID = 0;  //默认播放id
    protected static final Map<Integer, MediaPlayerCallback> playerMap = new HashMap<>(5);

//    protected static MediaPlayerUtils instance;
//
//
//    public static MediaPlayerUtils getInstance() {
//        if (instance == null) instance = new MediaPlayerUtils();
//        return instance;
//    }

    /**
     * 获取所有播放器
     */
    @NonNull
    public Map<Integer, MediaPlayerCallback> getAllPlayers() {
        return playerMap;
    }

    public static void playRaw(@RawRes int rawId, @Nullable MediaPlayerCallback playerCallback) {
        playRaw(rawId, true, playerCallback);
    }
    public static void playRaw(@RawRes int rawId, boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        playRaw(rawId, false, true, isNewMediaPlayer, playerCallback);
    }
    /**
     * 播放R.raw.xxx 音频
     * @param rawId 资源id
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param playerCallback 播放回调
     * @param isNewMediaPlayer 是否使用新的MediaPlayer, 而不是已存在的那个.(如果想同时播放多个, 传true)
     */
    public static void playRaw(@RawRes int rawId, boolean isLooping, boolean isAutoPlay,
                        boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        if (isNewMediaPlayer) {
            MediaPlayer mediaPlayer = null;
            try {
                mediaPlayer = MediaPlayer.create(ConfigUtils.APPLICATION, rawId);   //nullable
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setVolume(1, 1);
                mediaPlayer.setLooping(isLooping);

                if (playerCallback == null) playerCallback = new MediaPlayerCallback() {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                    }
                };
                playerCallback.isAutoPlay = isAutoPlay;
                playerCallback.isNewMediaPlayer = isNewMediaPlayer;
                playerCallback.mp = mediaPlayer;

                int audioSessionId = mediaPlayer.getAudioSessionId();
                playerMap.put(audioSessionId, playerCallback);

                //准备完成监听
                mediaPlayer.setOnPreparedListener(playerCallback);
                //播放出错监听
                mediaPlayer.setOnErrorListener(playerCallback);
                mediaPlayer.setOnBufferingUpdateListener(playerCallback);
                //调用seekTo()完成后监听
                mediaPlayer.setOnSeekCompleteListener(playerCallback);
                mediaPlayer.setOnCompletionListener(playerCallback);

                /**
                 * 播放本地的时候, .prepare() 会报错: prepareAsync called in state 8, mPlayer(0x7e0c7f62c0)
                 * 会自动准备完毕
                 */
//                mediaPlayer.prepareAsync();
//                mediaPlayer.prepare();
            } catch (NullPointerException | IllegalStateException e) {
                if (playerCallback != null) {
                    boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                    if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
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

    public static void play(@Nullable String audioPath, @Nullable MediaPlayerCallback playerCallback) {
        play(audioPath, false, playerCallback);
    }
    public static void play(@Nullable String audioPath, boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        play(audioPath, false, true, isNewMediaPlayer, playerCallback);
    }
    /**
     * 播放音频
     * @param audioPath 本地/网络音频
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param playerCallback 播放监听
     * @param isNewMediaPlayer 是否使用新的MediaPlayer, 而不是已存在的那个.(如果想同时播放多个, 传true)
     */
    public static void play(@Nullable String audioPath, boolean isLooping, boolean isAutoPlay,
                     boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        if (TextUtils.isEmpty(audioPath)) {
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(null, new IllegalStateException("audioPath is Empty!"));
                if (!isDealBySelf) {
                    playerCallback.onCompletion(null);
                }
            }
            return;
        }
        MediaPlayer mediaPlayer = null;
        try {
            if (isNewMediaPlayer) {
                mediaPlayer = new MediaPlayer();
            } else {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                    DEFAULT_AUDIO_SESSION_ID = mMediaPlayer.getAudioSessionId();
                } else {
                    //复用播放器, if还在播放, 先回调播放完成.
                    if (mMediaPlayer.isPlaying()) {
                        MediaPlayerCallback mpc = playerMap.get(mMediaPlayer.getAudioSessionId());
                        if (mpc != null) mpc.onCompletion(mMediaPlayer);
                    }
                    mMediaPlayer.reset();
                }
                mediaPlayer = mMediaPlayer;
            }
//            AudioAttributes attrs = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
//            mediaPlayer.setAudioAttributes(attrs);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //设置数据源, 本地or网上
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.setLooping(isLooping);

            if (playerCallback == null) playerCallback = new MediaPlayerCallback() {
                @Override
                public void onCompletion2(@Nullable MediaPlayer mp) {
                }
            };
            playerCallback.isAutoPlay = isAutoPlay;
            playerCallback.isNewMediaPlayer = isNewMediaPlayer;
            playerCallback.mp = mediaPlayer;

            int audioSessionId = mediaPlayer.getAudioSessionId();
            playerMap.put(audioSessionId, playerCallback);

            //准备完成监听
            mediaPlayer.setOnPreparedListener(playerCallback);
            //播放出错监听
            mediaPlayer.setOnErrorListener(playerCallback);
            mediaPlayer.setOnBufferingUpdateListener(playerCallback);
            //调用seekTo()完成后监听
            mediaPlayer.setOnSeekCompleteListener(playerCallback);
            mediaPlayer.setOnCompletionListener(playerCallback);
            mediaPlayer.prepareAsync();    //主线程中异步准备, 准备监听完成后开始播放
//            mediaPlayer.prepare();
//            mediaPlayer.start();
        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException e) {
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
            }
        }
    }



    /**
     * 获取'播放'的音频的当前进度 (要先设置音频)
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @return 单位ms
     */
    public static int getCurrentPosition(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null || playerCallback.mp == null) return -1;
        return playerCallback.mp.getCurrentPosition();
    }

    /**
     * 设置'播放'进度
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @param msec 从开始搜寻到的位移，以毫秒为单位。if(msec)<0，将使用时间位置零。if(msec) > 持续时间，将使用持续时间。
     */
    public void seekTo(int audioSessionId, int msec) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null || playerCallback.mp == null) return;
        playerCallback.mp.seekTo(msec);
    }

    /**
     * 获取'播放'的音频的总时长 (要先设置音频)
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @return 单位ms
     */
    public static int getDuration(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null || playerCallback.mp == null) return -1;
        return playerCallback.mp.getDuration();
    }



    /**
     * 开始播放音频
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public static void start(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) start(playerCallback.mp, playerCallback);
    }

    /**
     * 开始播放音频
     */
    public static void start(@Nullable MediaPlayer mediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        try {
            if (mediaPlayer != null) mediaPlayer.start();
        } catch (IllegalStateException e) {
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                if (!isDealBySelf) playerCallback.onCompletion(playerCallback.mp);
            }
        }
    }

    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #start(int)}, 或者 {@link #start(MediaPlayer, MediaPlayerCallback)}
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public static void pause(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) pause(playerCallback.mp);
    }

    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #start(int)}, 或者 {@link #start(MediaPlayer, MediaPlayerCallback)}
     */
    public static void pause(@Nullable MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null) mediaPlayer.pause();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音频
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public static void stop(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) stop(playerCallback.mp);
    }

    /**
     * 停止播放音频
     */
    public static void stop(@Nullable MediaPlayer mediaPlayer) {
        try {
            //开始 or 暂停 后, 可以停止
            if (mediaPlayer != null) mediaPlayer.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放所有音频
     */
    public static void stopAll() {
        for (Integer audioSessionId : playerMap.keySet()) {
            stop(audioSessionId);
        }
    }

    /**
     * @return 是否正在播放
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public static boolean isPlaying(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        return playerCallback != null && playerCallback.mp != null && playerCallback.mp.isPlaying();
    }

    /**
     * 释放播放器资源
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public static void release(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.remove(audioSessionId);
        if (playerCallback != null) {
            MediaPlayer mp = playerCallback.mp;
            release(mp);
            playerCallback.mp = null;
        }
    }

    /**
     * 释放播放器资源
     * @deprecated 建议使用 {@link #release(int)}
     */
    @Deprecated
    public static void release(@Nullable MediaPlayer mediaPlayer) {
        stop(mediaPlayer);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            //默认播放器释放后, 要置空, 否则.reset()会报错
            if (mediaPlayer == mMediaPlayer) {
                DEFAULT_AUDIO_SESSION_ID = 0;
                mMediaPlayer = null;
            }
        }
    }

    /**
     * 释放所有播放器资源
     */
    public static void releaseAll() {
        //不使用 playerMap.keySet()来遍历, 否则调用 release() 方法的时候, 并发修改异常: java.util.ConcurrentModificationException
        List<Integer> clone = new ArrayList<>(playerMap.keySet());
        for (Integer audioSessionId : clone) {
            release(audioSessionId);
        }
    }
}
