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
            if (isAutoPlay) MediaPlayerUtils.getInstance().start(audioSessionId);
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
        if (mp != null) MediaPlayerUtils.getInstance().release(mp.getAudioSessionId());
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
     * 播放的过程中, 出现的错误. if播放不存在的资源(404), 约30s才会回调...<br />
     * Called to indicate an error.
     *
     * @param mp      the MediaPlayer the error pertains to
     * @param what    the type of error that has occurred:
     * <ul>
     * <li>{@link MediaPlayer#MEDIA_ERROR_UNKNOWN}, 例: 404
     * <li>{@link MediaPlayer#MEDIA_ERROR_SERVER_DIED}
     * </ul>
     * @param extra an extra code, specific to the error. Typically
     * implementation dependent.
     * <ul>
     * <li>{@link MediaPlayer#MEDIA_ERROR_IO}
     * <li>{@link MediaPlayer#MEDIA_ERROR_MALFORMED}
     * <li>{@link MediaPlayer#MEDIA_ERROR_UNSUPPORTED}
     * <li>{@link MediaPlayer#MEDIA_ERROR_TIMED_OUT}
     * <li><code>MEDIA_ERROR_SYSTEM (-2147483648)</code> - low-level system error.
     * </ul>
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     * 如果自己处理错误, 则返回true. 否则返回false(会调用 {@link #onCompletion(MediaPlayer))。
     */
    @CallSuper
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.errorFormat("播放的过程中, 出现错误, what=%d, extra=%d", what, extra);
        /**
         * 在这儿调用{@link MediaPlayer#release()}后, 会置空所有listener, 导致{@link #onCompletion(MediaPlayer)}不会被回调
         */
//        if (mp != null) MediaPlayerUtils.getInstance().release(mp.getAudioSessionId());
        return false;
    }

    /**
     * 播放完成
     */
    @CallSuper
    public void onCompletion(@Nullable MediaPlayer mp) {
        if (isNewMediaPlayer && mp != null) MediaPlayerUtils.getInstance().release(mp.getAudioSessionId());
        onCompletion2(mp);
    }

    /**
     * 播放完成
     */
    public abstract void onCompletion2(@Nullable MediaPlayer mp);
}
