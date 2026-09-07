package com.actor.others.utils.tts;

import android.media.AudioFormat;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ThreadUtils;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2024/11/29 on 10
 * @version 1.0
 */
public abstract class UtteranceProgressListenerImpl extends UtteranceProgressListener {

    Object tag;

    public UtteranceProgressListenerImpl() {
        super();
    }

    /**
     * 对本次播放进行标记, if要标记多个参数可传入数组
     * @param tag 标记, 例: position, object, { 1, true, "string", object }
     */
    public UtteranceProgressListenerImpl(Object tag) {
        super();
        this.tag = tag;
    }

    public <T extends Object> T getTTSTag() {
        return (T) tag;
    }

    /**
     * @param utteranceId 此请求的唯一标识符, 调用 speak()的时候传入的参数
     */
    @Override
    public final void onStart(String utteranceId) {
        //子线程
        LogUtils.errorFormat("onStart: utteranceId=%s", utteranceId);
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onStart2(utteranceId);
            }
        });
    }
    //主线程
    public void onStart2(String utteranceId) {
    }

    @Override
    public final void onDone(String utteranceId) {
        //子线程
        LogUtils.errorFormat("onDone: utteranceId=%s", utteranceId);
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onDone2(utteranceId);
            }
        });
    }
    //主线程
    public abstract void onDone2(String utteranceId);

    @Override
    public final void onError(String utteranceId) {
        LogUtils.errorFormat("onError: utteranceId=%s", utteranceId);
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isDealBySelf = onError2(utteranceId);
                if (!isDealBySelf) {
                    onDone2(utteranceId);
                }
            }
        });
    }

    /**
     * if 要重写 onError() 方法, 请重写这个 onError2()方法, 主线程
     * @param utteranceId
     * @return 是否自己处理错误, if false, 会回调 {@link #onDone(String)} 方法.
     */
    public boolean onError2(String utteranceId) {
        return false;
    }



    /**
     * 会回调 {@link #onError(String)} 方法
     * @param utteranceId The utterance ID of the utterance.
     * @param errorCode one of the ERROR_* codes from {@link TextToSpeech}
     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)    //Android 5.0, Api 21
    @Override
    public void onError(String utteranceId, int errorCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onError(utteranceId, errorCode);
        } else {
            onError(utteranceId);
        }
        LogUtils.errorFormat("onError: utteranceId=%s, errorCode=%d", utteranceId, errorCode);
    }

    /**
     * 当话语在进行中被停止或从合成队列中清除时调用。<br />
     * 如果客户端调用TextToSpeech.stop()或使用TextToSpeech，就会发生这种情况。<br />
     * QUEUE_FLUSH作为TextToSpeech.speak或TextToSpeech.synthesizeToFile方法的参数。
     * @param utteranceId The utterance ID of the utterance.
     * @param interrupted If true, then the utterance(语音) was interrupted while being synthesized(合成)
     *        and its output is incomplete. If false, then the utterance was flushed
     *        before the synthesis started.
     */
//    @RequiresApi(api = Build.VERSION_CODES.M)   //Android 6.0, Api 23
    @Override
    public void onStop(String utteranceId, boolean interrupted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.onStop(utteranceId, interrupted);
        }
        //子线程
        LogUtils.errorFormat("onStop: utteranceId=%s, interrupted=%b", utteranceId, interrupted);
        //手动调用stop()后, 不会自动回调 onDone().
        onDone(utteranceId);
    }

    /**
     * 开始文字转语音文件
     * @param utteranceId The utterance ID of the utterance.
     * @param sampleRateInHz Sample rate in hertz of the generated audio.
     * @param audioFormat Audio format of the generated audio. Should be one of
     *        {@link AudioFormat#ENCODING_PCM_8BIT}, {@link AudioFormat#ENCODING_PCM_16BIT} or
     *        {@link AudioFormat#ENCODING_PCM_FLOAT}.
     * @param channelCount The number of channels.
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)   //Android 7.0, Api 24
    @Override
    public void onBeginSynthesis(String utteranceId, int sampleRateInHz, int audioFormat, int channelCount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.onBeginSynthesis(utteranceId, sampleRateInHz, audioFormat, channelCount);
        }
        //子线程
        LogUtils.errorFormat("onBeginSynthesis: utteranceId=%s, sampleRateInHz=%d, audioFormat=%d, channelCount=%d",
                utteranceId, sampleRateInHz, audioFormat, channelCount);
    }

    /**
     * 当一大块音频可供使用时，就会调用这个函数。<br />
     * 音频参数是将合成到扬声器的内容的副本(当使用TextToSpeech.speak调用启动合成时)或写入文件系统的内容的副本(对于TextToSpeech.synthesizeToFile)。<br />
     * 音频字节在一个或多个块中传送；如果onDone或onError被调用，则所有块都已被接收。<br />
     * 根据缓冲区大小和合成队列中的项目数量，在一段时间内可能不会播放此处接收的音频。
     * @param utteranceId The utterance ID of the utterance.
     * @param audio A chunk of audio; the format can be known by listening to
     *        {@link #onBeginSynthesis(String, int, int, int)}.
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)  //Android 7.0, Api 24
    @Override
    public void onAudioAvailable(String utteranceId, byte[] audio) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.onAudioAvailable(utteranceId, audio);
        }
        //子线程
//        LogUtils.errorFormat("onAudioAvailable: utteranceId=%s, audio=%s", utteranceId, audio);
    }

    /**
     * 当TTS服务要用给定的utteranceId说出指定范围的话语时，就会调用这个函数。<br />
     * 当音频应该在扬声器上开始播放时，调用此方法。请注意，这与音频一生成就调用的onAudioAvailable不同。<br />
     * 例如，该信息可用于在朗读时突出显示文本的范围。<br />
     * 仅在引擎通过调用synthesis callback . rangestart(int，int，int)提供计时信息时调用。<br />
     * {@link null 注意:} <br />
     *      只有 Google TTS 引擎 和部分第三方高级引擎（如微软 Azure TTS、Amazon Polly）才完整实现了这个方法，用于支持“朗读高亮”这类高级功能。国内手机自带tts和讯飞等并没有实现这个方法, 就没有回调这个方法...
     * @param utteranceId 此请求的唯一标识符
     * @param start The start index of the range in the utterance text. 播放的字符串的起始位置
     * @param end The end index of the range (exclusive) in the utterance text. 播放的字符串的end位置
     * @param frame The position in frames in the audio of the request where this range is spoken. 此范围的请求在音频帧中的位置
     */
//    @RequiresApi(api = Build.VERSION_CODES.O)   //Android 8.0, Api 26
    @Override
    public void onRangeStart(String utteranceId, int start, int end, int frame) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onRangeStart(utteranceId, start, end, frame);
        }
        LogUtils.errorFormat("onRangeStart: utteranceId=%s, start=%d, end=%d, frame=%d", utteranceId, start, end, frame);
    }
}
