package com.actor.myandroidframework.utils.video;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.hw.videoprocessor.VideoProcessor;
import com.hw.videoprocessor.util.VideoProgressListener;

import java.io.File;
import java.io.IOException;

/**
 * description: 视频压缩, 逆序, 混音. copy from: https://www.jianshu.com/p/78b7176c041e
 *  注意:
 *  使用本工具前, 需要添加依赖:
 *  1. 在项目的 build.gradle 中添加
 *      maven { url 'https://www.jitpack.io' }
 *
 *  2. 在主模块中添加
 *      //https://github.com/yellowcath/VideoProcessor 视频压缩
 *      implementation 'com.github.yellowcath:VideoProcessor:2.4.2'
 *
 * @author : 李大发
 * date       : 2021/3/30 on 00
 * @version 1.0
 */
public class VideoProcessorUtils {


    /**
     * 视频压缩
     * @param videoPath 视频路径
     * @param listener 回调监听
     */
    public static void compressVideo(Context context, String videoPath, @NonNull OnCompressListener listener) {
        if (listener == null) {
            return;
        }
        //开始压缩
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        int originWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int originHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int bitrate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
        LogUtils.formatError("originWidth=%d, originHeight=%d, bitrate(比特率)=%d", true, originWidth, originHeight, bitrate);

        /**
         * 简书: a.用VideoProcessor压缩时输出路径对应的文件夹不存在的话，不报错也没有任何反应。
         *         所以要确定videoOutCompressPath这个路径上的文件夹确实存在。
         */
        File file = getOutputVideoPath(context, videoPath);

        /**简书: c.要开启一个子线程来压缩这个视频*/
        ThreadUtils.runOnSubThread(() -> {
            try {
                VideoProcessor.processor(context)
                        .input(videoPath)//视频输入, 3种类型: MediaSource, Uri, String
                        .output(file.getPath())

                        /**以下参数全部为可选*/

                        /**简书: b.如果不配置宽高和码率（Bitrate）的话，有的小文件越压缩越大*/
                        .bitrate(bitrate / 2)//输出视频比特率

                        .outWidth(originWidth)
                        .outHeight(originHeight)
                        .dropFrames(true)          //帧率超过指定帧率时是否丢帧,默认为true
//                        .startTimeMs(startTimeMs)//用于剪辑视频
//                        .endTimeMs(endTimeMs)    //用于剪辑视频
//                        .speed(speed)            //改变视频速率，用于快慢放
//                        .changeAudioSpeed(changeAudioSpeed) //改变视频速率时，音频是否同步变化
                        .frameRate(VideoProcessor.DEFAULT_FRAME_RATE)   //帧率
                        .iFrameInterval(VideoProcessor.DEFAULT_I_FRAME_INTERVAL)//关键帧距，为0时可输出全关键帧视频（部分机器上需为-1）
                        .progressListener(new VideoProgressListener() {
                            @Override
                            public void onProgress(float progress) {//progress: 0-1
                                ThreadUtils.runOnUiThread(() -> {
                                    listener.onCompress(progress, file);
                                });
                            }
                        })
                        .process();
            } catch (Exception e) {
                ThreadUtils.runOnUiThread(() -> {
                    listener.onFailure(e);
                });
            }
        });
    }

    public interface OnCompressListener {
        /**
         * 压缩进度, 主线程回调
         * @param progress [0.0F, 1.0F]
         * @param compressedFile 压缩后的视频文件路径
         */
        void onCompress(float progress, File compressedFile);

        /**
         * 压缩失败
         */
        void onFailure(Exception e);
    }

    /**
     * 对视频先检查，如果不是全关键帧，先处理成所有帧都是关键帧，再逆序
     * @param videoPath 视频地址
     * @param reverseAudio 是否逆序音频
     */
    public static void reverseVideo(Context context, String videoPath, boolean reverseAudio) {
        File file = getOutputVideoPath(context, videoPath);
        //视频逆序
        try {
            VideoProcessor.reverseVideo(context, new VideoProcessor.MediaSource(videoPath), file.getPath(), reverseAudio, new VideoProgressListener() {
                @Override
                public void onProgress(float progress) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 混音,支持渐入渐出
     * 只支持16bit音频
     * @param videoInput 视频输入
     * @param audioInput 音频输入
     * @param startTimeMs
     * @param endTimeMs
     * @param videoVolume 视频音量, 0静音，100表示原音
     * @param aacVolume 音频音量, 0静音，100表示原音
     * @param fadeInSec
     * @param fadeOutSec
     */
    public static void mixAudioTrack(Context context, String videoInput, String audioInput,
                                     Integer startTimeMs, Integer endTimeMs, int videoVolume,
                                     int aacVolume, float fadeInSec, float fadeOutSec) {
        File file = getOutputVideoPath(context, videoInput);
        //混音,支持渐入渐出
        try {
            VideoProcessor.mixAudioTrack(context, new VideoProcessor.MediaSource(videoInput),
                    new VideoProcessor.MediaSource(audioInput), file.getPath(), startTimeMs, endTimeMs,
                    videoVolume, aacVolume,fadeInSec, fadeOutSec);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回输出视频地址
     * @param videoPath 要处理的视频地址
     */
    public static File getOutputVideoPath(Context context, String videoPath) {
        // /storage/emulated/0/Android/data/com.package.name/files/
//        File cacheDir = FileUtils.getExternalFilesDir();
        // /data/user/0/com.yys.land/cache/
        File cacheDir = context.getCacheDir();
        if (!cacheDir.exists()) {
            boolean mkdirs = cacheDir.mkdirs();
        }
        //文件名称
        String fileName = videoPath.substring(videoPath.lastIndexOf("/") + 1);
        File file = new File(cacheDir, fileName);
        if (file.exists()) {
            boolean delete = file.delete();
        }
        return file;
    }
}
