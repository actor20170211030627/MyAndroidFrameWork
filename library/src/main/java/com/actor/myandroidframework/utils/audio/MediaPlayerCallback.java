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
        MediaPlayer.OnBufferingUpdateListener   //缓冲进度监听
{
    public boolean isAutoPlay = false;  //是否自动播放
    public MediaPlayer mp;              //播放器

    /**
     * 当准备完成后
     */
    @CallSuper
    public void onPrepared(MediaPlayer mp) {
        if (isAutoPlay && mp != null) MediaPlayerUtils.getInstance().start(mp.getAudioSessionId());
        LogUtils.error("准备完成");
    }

    /**
     * 从'设置数据 -> 开始播放'这个过程中(还没有开始播放), 出现的错误
     * @param e 错误信息
     * @return 如果完全自己处理错误, 则返回true. 如果未处理错误，则返回false(会调用 OnCompletionListener)。
     */
    public boolean onSetData2StartError(@NonNull Exception e) {
        e.printStackTrace();
        LogUtils.error("从'设置数据 -> 开始播放'这个过程中(还没有开始播放), 出现错误!");
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        LogUtils.errorFormat("缓冲: AudioSessionId=%d, percent=%d", mp.getAudioSessionId(), percent);
    }

    /**
     * 播放的过程中, 出现的错误
     * @return 如果完全自己处理错误, 则返回true. 如果未处理错误，则返回false(会调用 OnCompletionListener)。
     */
//    @CallSuper
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.errorFormat("播放的过程中, 出现错误, what=%d, extra=%d", what, extra);
        if (mp != null) MediaPlayerUtils.getInstance().release(mp.getAudioSessionId());
        return false;
    }

    /**
     * 播放完成
     */
    public abstract void onCompletion(@Nullable MediaPlayer mp);
}
