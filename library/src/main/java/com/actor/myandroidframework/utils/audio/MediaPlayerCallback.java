package com.actor.myandroidframework.utils.audio;

import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * description: 音频播放回调
 * company    :
 *
 * @author : ldf
 * date       : 2024/2/21 on 16
 * @version 1.0
 */
public interface MediaPlayerCallback {

    /**
     * 当准备完成后
     */
    default void onPrepared(MediaPlayer mp) {
        LogUtils.error("准备完成");
    }

    /**
     * 播放完成
     */
    void onCompletion(MediaPlayer mp);

    /**
     * 从'设置数据 -> 开始播放'这个过程中(还没有开始播放), 出现的错误
     * @param e 错误信息
     * @return 如果完全自己处理错误, 则返回true. 如果未处理错误，则返回false(会调用 OnCompletionListener)。
     */
    default boolean onSetData2StartError(@NonNull Exception e) {
        LogUtils.error("从'设置数据 -> 开始播放'这个过程中(还没有开始播放), 出现错误!");
        return false;
    }

    /**
     * 播放的过程中, 出现的错误
     * @see MediaPlayer.OnErrorListener#onError(MediaPlayer, int, int)
     * @return 如果完全自己处理错误, 则返回true. 如果未处理错误，则返回false(会调用 OnCompletionListener)。
     */
    default boolean onPlayError(MediaPlayer mp, int what, int extra) {
        LogUtils.errorFormat("播放的过程中, 出现错误, what=%d, extra=%d", what, extra);
        return false;
    }
}
