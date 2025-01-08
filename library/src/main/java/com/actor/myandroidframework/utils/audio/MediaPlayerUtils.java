package com.actor.myandroidframework.utils.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.actor.myandroidframework.utils.ConfigUtils;

import java.io.IOException;
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

    protected MediaPlayer         mMediaPlayer;
    protected int                 DEFAULT_AUDIO_SESSION_ID = 0;  //默认播放id
    protected final Map<Integer, MediaPlayerCallback> playerMap = new HashMap<>(5);

    protected static MediaPlayerUtils instance;

    public static MediaPlayerUtils getInstance() {
        if (instance == null) instance = new MediaPlayerUtils();
        return instance;
    }

    /**
     * 获取所有播放器
     */
    @NonNull
    public Map<Integer, MediaPlayerCallback> getAllPlayers() {
        return playerMap;
    }

    public void playRaw(@RawRes int rawId, @Nullable MediaPlayerCallback playerCallback) {
        playRaw(rawId, true, playerCallback);
    }
    public void playRaw(@RawRes int rawId, boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
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
    public void playRaw(@RawRes int rawId, boolean isLooping, boolean isAutoPlay,
                        boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        MediaPlayer mediaPlayer = null;
        try {
            boolean isNeedPrepare = false;
            if (isNewMediaPlayer) {
                mediaPlayer = MediaPlayer.create(ConfigUtils.APPLICATION, rawId);
            } else {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(ConfigUtils.APPLICATION, rawId);
                    DEFAULT_AUDIO_SESSION_ID = mMediaPlayer.getAudioSessionId();
                } else {
                    AssetFileDescriptor afd = ConfigUtils.APPLICATION.getResources().openRawResourceFd(rawId);
                    if (afd == null) {
                        if (playerCallback != null) {
                            boolean isDealBySelf = playerCallback.onSetData2StartError(null, new IOException("Raw resource ID #0x" + Integer.toHexString(rawId)));
                            if (!isDealBySelf) playerCallback.onCompletion(null);
                        }
                        return;
                    }
                    //处理复用的mMediaPlayer正在播放的问题
                    dealRecyclerPlayerIsPlaying();
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    isNeedPrepare = true;
                    afd.close();
                }
                mediaPlayer = mMediaPlayer;
            }
            commonSetAndPlay(mediaPlayer, isNeedPrepare, isLooping, isAutoPlay, isNewMediaPlayer, playerCallback);
        } catch (IOException | Resources.NotFoundException | IllegalArgumentException | IllegalStateException e) {
            if (playerCallback != null) {
                if (mediaPlayer == mMediaPlayer) mediaPlayer = null;    //复用的播放器不要释放
                boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
            }
        }
    }

    public void play(@Nullable String audioPath, @Nullable MediaPlayerCallback playerCallback) {
        play(audioPath, false, playerCallback);
    }
    public void play(@Nullable String audioPath, boolean isNewMediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
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
    public void play(@Nullable String audioPath, boolean isLooping, boolean isAutoPlay,
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
                    //处理复用的mMediaPlayer正在播放的问题
                    dealRecyclerPlayerIsPlaying();
                    mMediaPlayer.reset();
                }
                mediaPlayer = mMediaPlayer;
            }
            //设置数据源, 本地or网上
            mediaPlayer.setDataSource(audioPath);
            commonSetAndPlay(mediaPlayer, true, isLooping, isAutoPlay, isNewMediaPlayer, playerCallback);
        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException e) {
            if (playerCallback != null) {
                if (mediaPlayer == mMediaPlayer) mediaPlayer = null;    //复用的播放器不要释放
                boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
            }
        }
    }

    /**
     * 处理复用的mMediaPlayer正在播放的问题
     */
    protected void dealRecyclerPlayerIsPlaying() {
        //复用播放器, if还在播放, 先回调播放完成.
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            stop(mMediaPlayer); //要调用停止, 否则if用户在 onCompletion()中又调用play/playRaw()方法的话, 有概率会递归栈溢出!
            int audioSessionId = mMediaPlayer.getAudioSessionId();
            MediaPlayerCallback mpc = playerMap.get(audioSessionId);
            if (mpc != null) {
                playerMap.put(audioSessionId, null);    //先手动置空回调, 防止用户在 onCompletion()中又调用playRaw()
                mpc.onCompletion(null);    //复用的播放器不要释放
            }
        }
    }

    /**
     * 公用设置&播放
     */
    protected void commonSetAndPlay(MediaPlayer mediaPlayer, boolean isNeedPrepare,
                                    boolean isLooping, boolean isAutoPlay, boolean isNewMediaPlayer,
                                    @Nullable MediaPlayerCallback playerCallback) {
//        AudioAttributes attrs = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
//        mediaPlayer.setAudioAttributes(attrs);
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
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
         * 播放本地{@link MediaPlayer#create(Context, int)}的时候, 会自动准备完毕, if调用{@link MediaPlayer#prepare()} or {@link MediaPlayer#prepareAsync()}会报错:
         * prepareAsync called in state 8, mPlayer(0x7e0c7f62c0) <br />
         * 播放{@link AssetFileDescriptor} or 路径的时候, 就需要准备了. 主线程中异步准备, 准备监听完成后开始播放
         */
        if (isNeedPrepare) mediaPlayer.prepareAsync();
//        mediaPlayer.prepare();
//        mediaPlayer.start();
    }



    /**
     * 获取'播放'的音频的当前进度 (要先设置音频)
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @return 单位ms
     */
    public int getCurrentPosition(int audioSessionId) {
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
    public int getDuration(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null || playerCallback.mp == null) return -1;
        return playerCallback.mp.getDuration();
    }



    /**
     * 开始播放音频
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void start(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) start(playerCallback.mp, playerCallback);
    }

    /**
     * 开始播放音频
     */
    public void start(@Nullable MediaPlayer mediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        try {
            if (mediaPlayer != null) mediaPlayer.start();
        } catch (IllegalStateException e) {
            if (playerCallback != null) {
                //                                                         null: 自己持有的Player, 自己去释放.
                boolean isDealBySelf = playerCallback.onSetData2StartError(null, e);
                if (!isDealBySelf) playerCallback.onCompletion(null);
            }
        }
    }

    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #start(int)}, 或者 {@link #start(MediaPlayer, MediaPlayerCallback)}
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void pause(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) pause(playerCallback.mp);
    }

    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #start(int)}, 或者 {@link #start(MediaPlayer, MediaPlayerCallback)}
     */
    public void pause(@Nullable MediaPlayer mediaPlayer) {
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
    public void stop(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback != null) stop(playerCallback.mp);
    }

    /**
     * 停止播放音频
     */
    public void stop(@Nullable MediaPlayer mediaPlayer) {
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
    public void stopAll() {
        for (Integer audioSessionId : playerMap.keySet()) {
            stop(audioSessionId);
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
    public void release(int audioSessionId) {
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
    public void release(@Nullable MediaPlayer mediaPlayer) {
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
    public void releaseAll() {
        //不使用 playerMap.keySet()来遍历, 否则调用 release() 方法的时候, 并发修改异常: java.util.ConcurrentModificationException
        List<Integer> clone = new ArrayList<>(playerMap.keySet());
        for (Integer audioSessionId : clone) {
            release(audioSessionId);
        }
    }
}
