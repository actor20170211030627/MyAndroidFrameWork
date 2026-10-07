package com.actor.myandroidframework.utils.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.content.res.ResourcesCompat;

import com.actor.myandroidframework.utils.ConfigUtils;

/**
 * description: MediaPlayer Builder
 * company    :
 *
 * @author : ldf
 * date       : 2026/10/6 on 15
 * @version 1.0
 */
public class MediaPlayerBuilder {

    @Nullable
    private Context context;

    public MediaPlayerBuilder() {
    }

    /**
     * @deprecated 非必要不要传 context
     */
    @Deprecated
    public MediaPlayerBuilder setContext(@Nullable Context context) {
        this.context = context;
        return this;
    }

    public MediaPlayerAttributeBuilder setRawId(@RawRes int rawId) {
        return new MediaPlayerAttributeBuilder(context, rawId, null, null);
    }

    public MediaPlayerAttributeBuilder setAssetPath(@NonNull String assetPath) {
        return new MediaPlayerAttributeBuilder(context, ResourcesCompat.ID_NULL, assetPath, null);
    }

    public MediaPlayerAttributeBuilder setAudioPath(@NonNull String audioPath) {
        return new MediaPlayerAttributeBuilder(context, ResourcesCompat.ID_NULL, null, audioPath);
    }



    public static class MediaPlayerAttributeBuilder {

        @Nullable
        private Context context;
        private @RawRes int rawId;
        @Nullable
        private String        assetPath;
        @Nullable
        private String        audioPath;
        @Nullable
        private MediaPlayer   mediaPlayer;
        @Nullable
        private Boolean       isNewMediaPlayer;
        private boolean       isLooping;
        private boolean       isAutoPlay = true;
        @Nullable
        private SurfaceHolder surfaceHolder;
        @Nullable
        private AudioAttributes audioAttributes;

        public MediaPlayerAttributeBuilder(@Nullable Context context, @RawRes int rawId, @Nullable String assetPath, @Nullable String audioPath) {
            this.context = context;
            this.rawId = rawId;
            this.assetPath = assetPath;
            this.audioPath = audioPath;
        }

        /**
         * 设置自己管理的 MediaPlayer
         * @deprecated 非必要不要自己管理 MediaPlayer
         */
        @Deprecated
        public MediaPlayerAttributeBuilder setMediaPlayer(@NonNull MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
            return this;
        }

        /**
         * @param isNewMediaPlayer 是否 new 1个 MediaPlayer, 而不是复用MediaPlayer. <br />
         *                         <code>if ({@link #mediaPlayer} == null)</code> 设置 <code>isNewMediaPlayer = true</code> 才有效. <br />
         *                         如果想同时播放多个音频, 传true
         *                         <ul>
         *                             <li><b>rawId & assetName</b>: 默认true</li>
         *                             <li><b>audioPath</b>: 默认false</li>
         *                         </ul>
         */
        public MediaPlayerAttributeBuilder isNewMediaPlayer(boolean isNewMediaPlayer) {
            this.isNewMediaPlayer = isNewMediaPlayer;
            return this;
        }

        /**
         * @param isLooping 是否循环播放, 默认false
         */
        public MediaPlayerAttributeBuilder isLooping(boolean isLooping) {
            this.isLooping = isLooping;
            return this;
        }

        /**
         * @param isAutoPlay 准备完成后, 是否自动播放, 默认true
         */
        public MediaPlayerAttributeBuilder isAutoPlay(boolean isAutoPlay) {
            this.isAutoPlay = isAutoPlay;
            return this;
        }

        /**
         * 看视频才传这个参数, if播放视频但不看也不用传这参数
         * @param surfaceHolder SurfaceHolder
         */
        public void setSurfaceHolder(@Nullable SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        /**
         * 普通不放不用传这参数
         * @param audioAttributes 描述音频的用途（Usage）、内容类型（Content Type）和标志（Flags）。它告诉系统这个音频是什么，以便系统做出正确的行为决策。<br />
         *                        影响音频的路由（例如，是作为媒体播放还是作为通知铃声）、音量控制策略，以及是否应该被其他应用录制等。
         */
        public void setAudioAttributes(@Nullable AudioAttributes audioAttributes) {
            this.audioAttributes = audioAttributes;
        }

        /**
         * 创建的MediaPlayer不受 {@link MediaPlayerUtils#stopAll()} & {@link MediaPlayerUtils#releaseAll()} 的影响自己管理
         * @param playerCallback 播放监听
         * @return 返回 MediaPlayer 自己管理, {@link null 注意:} <b>if (isNewMediaPlayer = false) return null;</b>
         * @deprecated 非必要不要自己管理 MediaPlayer
         */
        @Deprecated
        @Nullable
        public MediaPlayer playAndExcludeFromMap(@Nullable MediaPlayerCallback playerCallback) {
            if (playerCallback != null) playerCallback.isDealMediaPlayerBySelf = true;
            if (context == null) context = ConfigUtils.APPLICATION;
            MediaPlayer mediaPlayer2;
            if (rawId != ResourcesCompat.ID_NULL) {
                if (isNewMediaPlayer == null) isNewMediaPlayer = true;
                mediaPlayer2 = MediaPlayerUtils.getInstance().playRaw(context, mediaPlayer, rawId, isLooping, isAutoPlay, isNewMediaPlayer, true, surfaceHolder, audioAttributes, playerCallback);
            } else if (!TextUtils.isEmpty(assetPath)) {
                if (isNewMediaPlayer == null) isNewMediaPlayer = true;
                mediaPlayer2 = MediaPlayerUtils.getInstance().playAssets(context, mediaPlayer, assetPath, isLooping, isAutoPlay, isNewMediaPlayer, true, surfaceHolder, audioAttributes, playerCallback);
            } else {
                if (isNewMediaPlayer == null) isNewMediaPlayer = false;
                mediaPlayer2 = MediaPlayerUtils.getInstance().playPath(mediaPlayer, audioPath, isLooping, isAutoPlay, isNewMediaPlayer, true, surfaceHolder, audioAttributes, playerCallback);
            }
            context = null;
            rawId = ResourcesCompat.ID_NULL;
            assetPath = null;
            audioPath = null;
            mediaPlayer = null;
            if (isNewMediaPlayer) return mediaPlayer2;
            isNewMediaPlayer = null;
            return null;
        }

        /**
         * 开始
         * @param playerCallback 播放监听
         */
        public void play(@Nullable MediaPlayerCallback playerCallback) {
            if (playerCallback != null) playerCallback.isDealMediaPlayerBySelf = false;
            if (context == null) context = ConfigUtils.APPLICATION;
            if (rawId != ResourcesCompat.ID_NULL) {
                if (isNewMediaPlayer == null) isNewMediaPlayer = true;
                MediaPlayerUtils.getInstance().playRaw(context, mediaPlayer, rawId, isLooping, isAutoPlay, isNewMediaPlayer, false, surfaceHolder, audioAttributes, playerCallback);
            } else if (!TextUtils.isEmpty(assetPath)) {
                if (isNewMediaPlayer == null) isNewMediaPlayer = true;
                MediaPlayerUtils.getInstance().playAssets(context, mediaPlayer, assetPath, isLooping, isAutoPlay, isNewMediaPlayer, false, surfaceHolder, audioAttributes, playerCallback);
            } else {
                if (isNewMediaPlayer == null) isNewMediaPlayer = false;
                MediaPlayerUtils.getInstance().playPath(mediaPlayer, audioPath, isLooping, isAutoPlay, isNewMediaPlayer, false, surfaceHolder, audioAttributes, playerCallback);
            }
            context = null;
            rawId = ResourcesCompat.ID_NULL;
            assetPath = null;
            audioPath = null;
            mediaPlayer = null;
            isNewMediaPlayer = null;
        }
    }
}
