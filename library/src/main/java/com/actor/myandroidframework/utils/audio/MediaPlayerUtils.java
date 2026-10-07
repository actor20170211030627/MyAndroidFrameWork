package com.actor.myandroidframework.utils.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.actor.myandroidframework.utils.LogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * description: 播放音频, 复用MediaPlayer播放完后默认不重置, 新的MediaPlayer播放完后要重置! <br />
 * if创建很多个MediaPlayer, 会报错: onError: what=1, extra=-19 <br />
 * <br />
 * 更好兼容性，推荐使用 Google 的 <a href="https://github.com/google/ExoPlayer" target="_blank">ExoPlayer</a>
 * @see android.media.SoundPool
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


    public static MediaPlayerBuilder.MediaPlayerAttributeBuilder withRaw(@RawRes int rawId) {
        return withRaw(rawId, null);
    }
    /**
     * @param rawId R.raw.xxx 资源id
     * @deprecated 非必要不用传参2
     */
    @Deprecated
    public static MediaPlayerBuilder.MediaPlayerAttributeBuilder withRaw(@RawRes int rawId, @NonNull Context context) {
        return new MediaPlayerBuilder().setContext(context).setRawId(rawId);
    }

    /**
     *
     * @param assetPath 文件在assets目录下的路径, 示例: xxx.aac, audios/xxx.mp3
     */
    public static MediaPlayerBuilder.MediaPlayerAttributeBuilder withAssets(@NonNull String assetPath) {
        return withAssets(assetPath, null);
    }
    /**
     * @param assetPath 文件在assets目录下的路径, 示例: xxx.aac, audios/xxx.mp3
     * @deprecated 非必要不用传参2
     */
    @Deprecated
    public static MediaPlayerBuilder.MediaPlayerAttributeBuilder withAssets(@NonNull String assetPath, @NonNull Context context) {
        return new MediaPlayerBuilder().setContext(context).setAssetPath(assetPath);
    }

    /**
     * @param audioPath 本地/网络音频
     */
    public static MediaPlayerBuilder.MediaPlayerAttributeBuilder withAudioPath(@NonNull String audioPath) {
        return withAudioPath(audioPath, null);
    }
    /**
     * @param audioPath 本地/网络音频
     * @deprecated 非必要不用传参2
     */
    @Deprecated
    public static MediaPlayerBuilder.MediaPlayerAttributeBuilder withAudioPath(@NonNull String audioPath, @NonNull Context context) {
        return new MediaPlayerBuilder().setContext(context).setAudioPath(audioPath);
    }


    /**
     * 播放R.raw.xxx 音频
     * @param mediaPlayer 自己缓存的MediaPlayer
     * @param rawId 资源id
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param isNewMediaPlayer if mediaPlayer == null, 是否使用新的MediaPlayer, 而不是复用MediaPlayer.(如果想同时播放多个音频, 传true)
     * @param isDealMediaPlayerBySelf 创建的MediaPlayer是否不受 {@link #stopAll()} & {@link #releaseAll()} 的影响自己管理?
     * @param surfaceHolder SurfaceHolder
     * @param audioAttributes 描述音频的用途（Usage）、内容类型（Content Type）和标志（Flags）。它告诉系统这个音频是什么，以便系统做出正确的行为决策。<br />
     *                        影响音频的路由（例如，是作为媒体播放还是作为通知铃声）、音量控制策略，以及是否应该被其他应用录制等。
     * @param playerCallback 播放回调
     */
    @Nullable
    protected MediaPlayer playRaw(@NonNull Context context, @Nullable MediaPlayer mediaPlayer, @RawRes int rawId,
                                  boolean isLooping, boolean isAutoPlay, boolean isNewMediaPlayer, boolean  isDealMediaPlayerBySelf,
                                  @Nullable SurfaceHolder surfaceHolder, @Nullable AudioAttributes audioAttributes,
                                  @Nullable MediaPlayerCallback playerCallback) {
        boolean isNeedPrepare = false;
        MediaPlayer mediaPlayer2 = mediaPlayer;
        boolean ismMediaPlayerNull = mMediaPlayer == null;
        if (mediaPlayer == null) {
            if (isNewMediaPlayer) {
                mediaPlayer = MediaPlayer.create(context, rawId);
            } else {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(context, rawId);
                } else isNeedPrepare = true;
                mediaPlayer = mMediaPlayer;
            }
        } else isNeedPrepare = true;
        if (isNeedPrepare) {
            try (AssetFileDescriptor afd = context.getResources().openRawResourceFd(rawId)) {
                if (afd == null) {
                    if (playerCallback != null) {
                        boolean isDealBySelf = playerCallback.onSetData2StartError(null, new IOException("Raw resource ID #0x" + Integer.toHexString(rawId)));
                        if (!isDealBySelf) playerCallback.onCompletion(null);
                    }
                    return mediaPlayer == mMediaPlayer ? mediaPlayer2 : mediaPlayer;
                }
                if (!isNewMediaPlayer && !ismMediaPlayerNull) {
                    //处理复用的mMediaPlayer正在播放的问题
                    dealRecyclerPlayerIsPlaying();
                    mMediaPlayer.reset();
                }
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            } catch (IOException | Resources.NotFoundException | IllegalArgumentException | IllegalStateException e) {
                boolean isReUsage = mediaPlayer == mMediaPlayer;
                if (playerCallback != null) {
                    if (isReUsage) mediaPlayer = null;    //复用的播放器不要释放
                    boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                    if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
                }
                return isReUsage ? mediaPlayer2 : mediaPlayer;
            }
        }
        commonSetAndPlay(mediaPlayer, isNeedPrepare, isLooping, isAutoPlay, isNewMediaPlayer, isDealMediaPlayerBySelf, surfaceHolder, audioAttributes, playerCallback);
        return mediaPlayer == mMediaPlayer ? mediaPlayer2 : mediaPlayer;
    }


    /**
     * 播放 assets 里音频
     * @param mediaPlayer 自己缓存的MediaPlayer
     * @param assetPath 文件在assets目录下的路径, 示例: xxx.aac, audios/xxx.mp3
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param isNewMediaPlayer if mediaPlayer == null, 是否使用新的MediaPlayer, 而不是复用MediaPlayer.(如果想同时播放多个音频, 传true)
     * @param isDealMediaPlayerBySelf 创建的MediaPlayer是否不受 {@link #stopAll()} & {@link #releaseAll()} 的影响自己管理?
     * @param surfaceHolder SurfaceHolder
     * @param audioAttributes 描述音频的用途（Usage）、内容类型（Content Type）和标志（Flags）。它告诉系统这个音频是什么，以便系统做出正确的行为决策。<br />
     *                        影响音频的路由（例如，是作为媒体播放还是作为通知铃声）、音量控制策略，以及是否应该被其他应用录制等。
     * @param playerCallback 播放回调
     */
    @Nullable
    protected MediaPlayer playAssets(@NonNull Context context, @Nullable MediaPlayer mediaPlayer, @Nullable String assetPath,
                                     boolean isLooping, boolean isAutoPlay, boolean isNewMediaPlayer, boolean  isDealMediaPlayerBySelf,
                                     @Nullable SurfaceHolder surfaceHolder, @Nullable AudioAttributes audioAttributes,
                                     @Nullable MediaPlayerCallback playerCallback) {
        if (TextUtils.isEmpty(assetPath)) {
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(null, new IllegalArgumentException("fileName is Empty!"));
                if (!isDealBySelf) playerCallback.onCompletion(null);
            }
            return mediaPlayer;
        }
//        MediaPlayerUtils.getInstance().playPath("file:///android_asset/words/a.mp3", null)    //失败, MediaPlayer 不识别 android_asset 这个 scheme
        MediaPlayer mediaPlayer2 = mediaPlayer;
        try (AssetFileDescriptor afd = context.getAssets().openFd(assetPath)) {
            if (mediaPlayer == null) {
                if (isNewMediaPlayer) {
                    mediaPlayer = new MediaPlayer();
                } else {
                    if (mMediaPlayer == null) {
                        mMediaPlayer = new MediaPlayer();
                    } else {
                        //处理复用的mMediaPlayer正在播放的问题
                        dealRecyclerPlayerIsPlaying();
                        mMediaPlayer.reset();
                    }
                    mediaPlayer = mMediaPlayer;
                }
            }
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            commonSetAndPlay(mediaPlayer, true, isLooping, isAutoPlay, isNewMediaPlayer, isDealMediaPlayerBySelf, surfaceHolder, audioAttributes, playerCallback);
        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            if (playerCallback != null) {
                if (mediaPlayer == mMediaPlayer) mediaPlayer = null;    //复用的播放器不要释放
                boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
            }
        }
        return mediaPlayer == mMediaPlayer ? mediaPlayer2 : mediaPlayer;
    }


    /**
     * 播放 本地 or 网络 音频
     * @param mediaPlayer 自己缓存的MediaPlayer
     * @param audioPath 本地/网络音频
     * @param isLooping 是否循环播放
     * @param isAutoPlay 准备完成后, 是否自动播放
     * @param isNewMediaPlayer if mediaPlayer == null, 是否使用新的MediaPlayer, 而不是复用MediaPlayer.(如果想同时播放多个音频, 传true)
     * @param isDealMediaPlayerBySelf 创建的MediaPlayer是否不受 {@link #stopAll()} & {@link #releaseAll()} 的影响自己管理?
     * @param surfaceHolder SurfaceHolder
     * @param audioAttributes 描述音频的用途（Usage）、内容类型（Content Type）和标志（Flags）。它告诉系统这个音频是什么，以便系统做出正确的行为决策。<br />
     *                        影响音频的路由（例如，是作为媒体播放还是作为通知铃声）、音量控制策略，以及是否应该被其他应用录制等。
     * @param playerCallback 播放监听
     */
    @Nullable
    protected MediaPlayer playPath(@Nullable MediaPlayer mediaPlayer, @Nullable String audioPath,
                                   boolean isLooping, boolean isAutoPlay, boolean isNewMediaPlayer, boolean  isDealMediaPlayerBySelf,
                                   @Nullable SurfaceHolder surfaceHolder, @Nullable AudioAttributes audioAttributes,
                                   @Nullable MediaPlayerCallback playerCallback) {
        if (TextUtils.isEmpty(audioPath)) {
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(null, new IllegalArgumentException("audioPath is Empty!"));
                if (!isDealBySelf) playerCallback.onCompletion(null);
            }
            return mediaPlayer;
        }
        MediaPlayer mediaPlayer2 = mediaPlayer;
        if (mediaPlayer == null) {
            if (isNewMediaPlayer) {
                mediaPlayer = new MediaPlayer();
            } else {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                } else {
                    //处理复用的mMediaPlayer正在播放的问题
                    dealRecyclerPlayerIsPlaying();
                    mMediaPlayer.reset();
                }
                mediaPlayer = mMediaPlayer;
            }
        }
        try {
            //设置数据源, 本地or网上
            mediaPlayer.setAudioAttributes(audioAttributes == null ? new AudioAttributes.Builder().build() : audioAttributes);
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.setDisplay(surfaceHolder);
            commonSetAndPlay(mediaPlayer, true, isLooping, isAutoPlay, isNewMediaPlayer, isDealMediaPlayerBySelf, surfaceHolder, audioAttributes, playerCallback);
        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException e) {
            if (playerCallback != null) {
                if (mediaPlayer == mMediaPlayer) mediaPlayer = null;    //复用的播放器不要释放
                boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
            }
        }
        return mediaPlayer == mMediaPlayer ? mediaPlayer2 : mediaPlayer;
    }


    /**
     * 处理复用的mMediaPlayer正在播放的问题
     */
    protected void dealRecyclerPlayerIsPlaying() {
        //复用播放器, if还在播放, 先回调播放完成.
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int audioSessionId = mMediaPlayer.getAudioSessionId();
            //先.remove, 防止用户在 onCompletion()中又调用play()
            MediaPlayerCallback mpc = playerMap.remove(audioSessionId);
            stop(mMediaPlayer, mpc); //要调用停止, 否则if用户在 onCompletion()中又调用play()方法的话, 有概率会递归栈溢出!
            if (mpc != null) {
                mpc.onCompletion(null);    //复用的播放器要回调 & 不要释放
            }
        }
    }

    /**
     * 公用设置&播放
     */
    protected void commonSetAndPlay(@NonNull MediaPlayer mediaPlayer, boolean isNeedPrepare,
                                    boolean isLooping, boolean isAutoPlay, boolean isNewMediaPlayer,
                                    boolean  isDealMediaPlayerBySelf,
                                    @Nullable SurfaceHolder surfaceHolder, @Nullable AudioAttributes audioAttributes,
                                    @Nullable MediaPlayerCallback playerCallback) {
        if (audioAttributes == null) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder()
                    //内容类型
                    .setContentType(surfaceHolder == null ? AudioAttributes.CONTENT_TYPE_MUSIC : AudioAttributes.CONTENT_TYPE_MOVIE)
                    /**
                     * 设置音频的播放行为标志:
                     * FLAG_AUDIBILITY_ENFORCED: 请求系统确保此音频可被听到。即使设备处于静音或勿扰模式，系统也可能强制播放此音频（通常用于闹钟或紧急提示）。
                     * FLAG_HW_AV_SYNC         : 请求硬件音视频同步，用于需要精确同步的视频播放场景。
                     * FLAG_LOW_LATENCY        : 向系统请求一条低延迟的音频通路，旨在让音频从应用产生到扬声器/耳机输出的整个过程更快，减少可感知的延迟（Latency）。主要服务于对实时性要求高的场景，例如：
                     *                           音乐制作类应用：如数字音频工作站（DAW）、虚拟乐器，需要按下琴键即刻出声。
                     *                           游戏         ：需要音效与画面动作精准同步。
                     *                           交互式音频    ：需要即时反馈的音频应用。
                     *                           启用此标志并非没有代价，它有一系列严格的限制条件：
                     *                               采样率必须匹配  ：音频数据的采样率必须与设备音频输出硬件的原生采样率一致，否则低延迟路径无法生效。
                     *                               音频效果不可用  ：为了降低延迟，系统会绕过音频效果处理链，这意味着你无法同时使用均衡器、混响等 AudioEffect。
                     *                               缓冲区管理要求高：需要开发者手动、精细地管理音频缓冲区大小，否则容易出现音频卡顿（Glitch）。通常需要先用一个较大缓冲区，再通过 AudioTrack#setBufferSizeInFrames 逐步调小，直到找到稳定与低延迟的平衡点。
                     */
//                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED | AudioAttributes.FLAG_HW_AV_SYNC | AudioAttributes.FLAG_LOW_LATENCY)
                    /**
                     * 根据旧的音频流类型（如 STREAM_MUSIC、STREAM_ALARM）来推断并设置 AudioAttributes 的各项属性。
                     * 会覆盖 setContentType, setFlags, setUsage, setAllowedCapturePolicy, setHapticChannelsMuted 等方法设置的所有信息
                     */
//                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    //用途
                    .setUsage(AudioAttributes.USAGE_MEDIA);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //指定音频是否可以被其他app或系统捕获。
                builder.setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_ALL);
                //是否静音触觉通道: 控制播放“音频-触觉耦合”数据时，是否静音触觉反馈（振动）通道 (音频文件中包可能含触觉数据, 如某些 OGG 文件)
                builder.setHapticChannelsMuted(false);
            }
            audioAttributes = builder.build();
        }
        mediaPlayer.setAudioAttributes(audioAttributes);
        mediaPlayer.setDisplay(surfaceHolder);
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  //AudioManager.STREAM_VOICE_CALL;
        mediaPlayer.setVolume(1f, 1f);
        mediaPlayer.setLooping(isLooping);
        if (playerCallback == null) playerCallback = new MediaPlayerCallback() {
            @Override
            public void onCompletion2(@Nullable MediaPlayer mp) {
            }
        };
        playerCallback.isDealMediaPlayerBySelf = isDealMediaPlayerBySelf;
        playerCallback.isAutoPlay = isAutoPlay;
        playerCallback.isNewMediaPlayer = isNewMediaPlayer;
        playerCallback.isStopped = false;
        playerCallback.mp = mediaPlayer;

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
         *
         * prepare()和prepareAsync()允许的状态: Initialized, Stopped
         * prepare()和prepareAsync()禁止的状态: Idle, Error
         */
        if (isNeedPrepare) mediaPlayer.prepareAsync();
//        mediaPlayer.prepare();
//        mediaPlayer.start();
    }



    /**
     * 获取'播放'的音频的当前进度 (要先设置音频)
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @return 单位ms, if没找到 MediaPlayer 会返回-1
     */
    public int getCurrentPosition(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        return playerCallback == null ? -1 : getCurrentPosition(playerCallback.mp);
    }
    /**
     * 获取'播放'的音频的当前进度 (要先设置音频)
     * @deprecated 建议使用 {@link #getCurrentPosition(int)}
     */
    @Deprecated
    public int getCurrentPosition(@Nullable MediaPlayer mediaPlayer) {
        return mediaPlayer == null ? -1 : mediaPlayer.getCurrentPosition();
    }

    /**
     * 设置'播放'进度, 设置后播放状态不变
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @param msec 从开始搜寻到的位移，以毫秒为单位。if(msec)<0，将使用时间位置零。if(msec) > 持续时间，将使用持续时间。
     */
    public void seekTo(int audioSessionId, int msec) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null) return;
        seekTo(playerCallback.mp, playerCallback, msec);
    }
    /**
     * 设置'播放'进度, 设置后播放状态不变
     * @param msec 从开始搜寻到的位移，以毫秒为单位。if(msec)<0，将使用时间位置零。if(msec) > 持续时间，将使用持续时间。
     * @deprecated 建议使用 {@link #seekTo(int, int)}
     */
    @Deprecated
    public void seekTo(@Nullable MediaPlayer mediaPlayer, @Nullable MediaPlayerCallback playerCallback, int msec) {
        if (mediaPlayer == null) return;
        if (playerCallback == null) playerCallback = playerMap.get(mediaPlayer.getAudioSessionId());
        //seekTo()禁止的状态: Stopped, Idle, Initialized, Error 否则报错: what=-38, extra=0
        //并且.stop()后, currentPos 已经 = 0
        if (playerCallback != null && (!playerCallback.isPrepared || playerCallback.isStopped)) return;
        try {
            //允许的状态: Prepared, Started, Paused, PlaybackCompleted
            mediaPlayer.seekTo(msec);
        } catch (IllegalStateException e) {
            LogUtils.error("seekTo(int, int) 设置'播放'进度报错:", e);
        }
    }

    /**
     * 获取'播放'的音频的总时长 (要先设置音频)
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @return 单位ms, if没找到 MediaPlayer 会返回-1
     */
    public int getDuration(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        return playerCallback == null ? -1 : getDuration(playerCallback.mp);
    }
    /**
     * 获取'播放'的音频的总时长 (要先设置音频)
     * @deprecated 建议使用 {@link #getDuration(int)}
     */
    @Deprecated
    public int getDuration(@Nullable MediaPlayer mediaPlayer) {
        return mediaPlayer == null ? -1 : mediaPlayer.getDuration();
    }



    /**
     * 开始播放 / 继续播放音频
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void start(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null) {
            LogUtils.errorFormat("audioSessionId = %d 的MediaPlayer不存在or已移除!", audioSessionId);
        } else start(playerCallback.mp, playerCallback);
    }
    /**
     * 开始播放 / 继续播放音频
     * @param playerCallback 播放回调
     * @deprecated 建议使用 {@link #start(int)}
     */
    @Deprecated
    public void start(@Nullable MediaPlayer mediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        if (mediaPlayer == null) return;
        if (playerCallback == null) playerCallback = playerMap.get(mediaPlayer.getAudioSessionId());
        try {
            if (playerCallback != null && playerCallback.isStopped) {
                playerCallback.isStopped = false;
                //禁止的状态: Stopped, Idle, Initialized, Error. if isStopped, 需要重新准备, 否则会报错: what=-38, extra=0
                mediaPlayer.prepareAsync();
            } else {
                //允许的状态: Prepared, Paused, PlaybackCompleted
                mediaPlayer.start();
            }
        } catch (IllegalStateException e) {
            if (playerCallback != null) {
                boolean isDealBySelf = playerCallback.onSetData2StartError(mediaPlayer, e);
                if (!isDealBySelf) playerCallback.onCompletion(mediaPlayer);
            }
        }
    }

    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #start(int)}, 或者 {@link #start(MediaPlayer, MediaPlayerCallback)}
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public boolean pause(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null) {
            LogUtils.errorFormat("audioSessionId = %d 的MediaPlayer不存在or已移除!", audioSessionId);
            return false;
        } else return pause(playerCallback.mp);
    }
    /**
     * 暂停播放音频 <br />
     * 继续播放的话调用 {@link #start(int)}, 或者 {@link #start(MediaPlayer, MediaPlayerCallback)}
     * @deprecated 建议使用 {@link #pause(int)}
     */
    @Deprecated
    public boolean pause(@Nullable MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) return false;
        try {
            //禁止的状态: 其他所有状态. 一定要判断.isPlaying(), 否则如果.stop()了的话, 再调用.pause()会报错: what=-38, extra=0
            if (mediaPlayer.isPlaying()) {
                //允许的状态: Started, Paused
                mediaPlayer.pause();
                return true;
            }
        } catch (IllegalStateException e) {
            LogUtils.error("pause(MediaPlayer) 暂停播放音频报错:", e);
        }
        return false;
    }

    /**
     * 停止播放音频
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public boolean stop(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null) {
            LogUtils.errorFormat("audioSessionId = %d 的MediaPlayer不存在or已移除!", audioSessionId);
            return false;
        }
        return stop(playerCallback.mp, playerCallback);
    }
    /**
     * 停止播放音频
     * @deprecated 建议使用 {@link #stop(int)}
     */
    @Deprecated
    public boolean stop(@Nullable MediaPlayer mediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        if (mediaPlayer == null) return false;
        if (playerCallback == null) playerCallback = playerMap.get(mediaPlayer.getAudioSessionId());
        try {
            //开始 or 暂停 后, 可以停止
            //允许的状态: Prepared, Started, Stopped, Paused, PlaybackCompleted
            //禁止的状态: Idle, Initialized, Error
            if (playerCallback != null) playerCallback.isStopped = true;
            mediaPlayer.stop();
            return true;
        } catch (IllegalStateException e) {
            LogUtils.error("stop(MediaPlayer) 停止播放音频报错:", e);
        }
        return false;
    }

    /**
     * 停止播放所有音频
     */
    public void stopAll() {
        for (Map.Entry<Integer, MediaPlayerCallback> entry : playerMap.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isDealMediaPlayerBySelf) stop(entry.getKey());
        }
    }

    /**
     * 是否正在播放
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public boolean isPlaying(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null) {
            LogUtils.errorFormat("audioSessionId = %d 的MediaPlayer不存在or已移除!", audioSessionId);
            return false;
        }
        return isPlaying(playerCallback.mp);
    }
    /**
     * 是否正在播放
     * @deprecated 建议使用 {@link #isPlaying(int)}
     */
    @Deprecated
    public boolean isPlaying(@Nullable MediaPlayer mediaPlayer) {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /**
     * 设置速度系数, 若需更大范围&更好兼容性，推荐使用 Google 的 <a href="https://github.com/google/ExoPlayer" target="_blank">ExoPlayer</a>
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     * @param playSpeed 速度系数, 一般[0.5f~2.0f], 经Vivo X27 测试的允许范围[0.01~6.491], 超过就会设置失败.
     */
    public boolean setPlaySpeed(int audioSessionId, @FloatRange(from = 0.f) float playSpeed) {
        MediaPlayerCallback playerCallback = playerMap.get(audioSessionId);
        if (playerCallback == null) {
            LogUtils.errorFormat("audioSessionId = %d 的MediaPlayer不存在or已移除!", audioSessionId);
            return false;
        }
        return setPlaySpeed(playerCallback.mp, playSpeed);
    }
    /**
     * 设置速度系数, 设置完成后会自动播放...
     * @param playSpeed 速度系数, 一般[0.5f~2.0f], 经Vivo X27 测试的允许范围[0.01~6.491], 超过就会设置失败.
     * @deprecated 建议使用 {@link #setPlaySpeed(int, float)}
     */
    @Deprecated
    public boolean setPlaySpeed(@Nullable MediaPlayer mp, @FloatRange(from = 0.f) float playSpeed) {
        if (mp == null) return false;
        //Android 6.0(API 23)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                PlaybackParams playbackParams = mp.getPlaybackParams();
                playbackParams.setSpeed(playSpeed);
                mp.setPlaybackParams(playbackParams);
                return true;
            } catch (IllegalStateException | IllegalArgumentException e) {
                //IllegalStateException:    if the internal player engine has not been initialized or has been released.
                //IllegalArgumentException: if params is not supported. (速度系数不在设置范围)
                LogUtils.error("设置速度系数出错:", e);
            }
        }
        return false;
    }

    /**
     * 释放播放器资源
     * @param audioSessionId {@link MediaPlayer#getAudioSessionId()}, 用于确定哪一个播放器
     */
    public void release(int audioSessionId) {
        MediaPlayerCallback playerCallback = playerMap.remove(audioSessionId);
        if (playerCallback != null) {
            release(playerCallback.mp, playerCallback);
            playerCallback.mp = null;
        }
    }
    /**
     * 释放播放器资源
     * @deprecated 建议使用 {@link #release(int)}
     */
    @Deprecated
    public void release(@Nullable MediaPlayer mediaPlayer, @Nullable MediaPlayerCallback playerCallback) {
        stop(mediaPlayer, playerCallback);
        if (mediaPlayer != null) {
            mediaPlayer.reset();
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
        Iterator<Map.Entry<Integer, MediaPlayerCallback>> iterator = playerMap.entrySet().iterator();
        //并发修改, 不要用for
        while (iterator.hasNext()) {
            Map.Entry<Integer, MediaPlayerCallback> entity = iterator.next();
            MediaPlayerCallback playerCallback = entity.getValue();
            if (playerCallback != null && !playerCallback.isDealMediaPlayerBySelf) release(entity.getKey());
        }
    }
}
