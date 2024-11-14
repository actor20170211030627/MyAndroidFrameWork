package com.actor.myandroidframework.utils.audio;

import android.media.MediaPlayer;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * description: 音频播放回调
 *
 * @author : ldf
 * date       : 2024/2/21 on 16
 * @version 1.0
 */
public abstract class MediaPlayerCallback implements
        MediaPlayer.OnPreparedListener,         //准备完成监听
        MediaPlayer.OnErrorListener,            //错误监听
        MediaPlayer.OnCompletionListener,       //播放完成监听
        MediaPlayer.OnBufferingUpdateListener,  //缓冲进度监听
        MediaPlayer.OnSeekCompleteListener      //调用seekTo()完成后监听
{
    boolean isAutoPlay = false;         //是否自动播放
    boolean isNewMediaPlayer = false;   //是否使用新的MediaPlayer
    Object tagMPC;            //标记本次播放, 例如可以传入RecyclerView中Item的position, 播放完成拿到tag做相应操作
    MediaPlayer mp;                     //播放器

    public MediaPlayerCallback() {
    }

    public MediaPlayerCallback(Object tag) {
        this.tagMPC = tag;
    }

    @Nullable
    public <T extends Object> T getPlayerTag() {
        return (T) tagMPC;
    }

    /**
     * 当准备完成后
     */
    @CallSuper
    public void onPrepared(MediaPlayer mp) {
        if (mp != null) {
            int audioSessionId = mp.getAudioSessionId();
            LogUtils.errorFormat("onPrepared, audioSessionId=%d", audioSessionId);
            if (isAutoPlay) MediaPlayerUtils.start(audioSessionId);
        }
    }

    /**
     * 从'设置数据 -> 开始播放'这个过程中(还没有开始播放), 出现的错误
     * @param e 错误信息
     * @return 如果自己处理错误, 则返回true. 否则返回false(会调用 {@link #onCompletion(MediaPlayer)})。
     */
    @CallSuper
    public boolean onSetData2StartError(@Nullable MediaPlayer mp, @NonNull Exception e) {
        e.printStackTrace();
        LogUtils.error("从'设置数据 -> 开始播放'这个过程中(还没有开始播放), 出现错误!");
        if (mp != null) MediaPlayerUtils.release(mp.getAudioSessionId());
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        LogUtils.errorFormat("缓冲: AudioSessionId=%d, percent=%d", mp.getAudioSessionId(), percent);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (mp != null) LogUtils.errorFormat("seekTo()完成: AudioSessionId=%d", mp.getAudioSessionId());
    }

    /**
     * 播放的过程中, 出现的错误
     * @return 如果自己处理错误, 则返回true. 否则返回false(会调用 {@link #onCompletion(MediaPlayer))。
     */
    @CallSuper
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.errorFormat("播放的过程中, 出现错误, what=%d, extra=%d", what, extra);
        if (mp != null) MediaPlayerUtils.release(mp.getAudioSessionId());
        return false;
    }

    /**
     * 播放完成
     */
    @CallSuper
    public void onCompletion(@Nullable MediaPlayer mp) {
        if (isNewMediaPlayer && mp != null) MediaPlayerUtils.release(mp.getAudioSessionId());
        onCompletion2(mp);
    }

    /**
     * 播放完成
     */
    public abstract void onCompletion2(@Nullable MediaPlayer mp);
}
