package com.actor.myandroidframework.utils.audio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

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
     * 开始播放
     */
    default void onStartPlay() {
        LogUtils.error("开始播放");
    }

    /**
     * 播放完成
     * @param rawId R.raw.xxx
     * @param audioPath 音频路径
     */
    void playComplete(@Nullable @RawRes Integer rawId, @Nullable String audioPath);

    /**
     * 播放出错
     * @param e
     */
    void playError(@NonNull Exception e);
}
