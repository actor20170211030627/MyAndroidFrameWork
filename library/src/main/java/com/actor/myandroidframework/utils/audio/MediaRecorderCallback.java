package com.actor.myandroidframework.utils.audio;

import androidx.annotation.NonNull;

/**
 * description: 录音回调
 * company    :
 *
 * @author : ldf
 * date       : 2024/2/21 on 15
 * @version 1.0
 */
public interface MediaRecorderCallback {
    /**
     * 录制完成
     * @param audioPath 语音路径
     * @param durationMs 语音时长, 单位ms
     */
    void recordComplete(String audioPath, long durationMs);

    /**
     * 录制被取消
     * @param audioPath 语音路径
     * @param durationMs 语音时长, 单位ms
     */
    void recordCancel(String audioPath, long durationMs);

    /**
     * 录音失败
     */
    default void recordError(@NonNull Exception e) {
        e.printStackTrace();
    }
}
