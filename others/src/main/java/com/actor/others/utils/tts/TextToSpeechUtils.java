package com.actor.others.utils.tts;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * description: <a href="https://www.jianshu.com/p/6e9cc56f080b">android 语音合成(文字转语音播放)</a> <br />
 *              <a href="https://blog.csdn.net/long375577908/article/details/78437278">TextToSpeech的使用</a> <br />
 *              <a href="https://www.jianshu.com/p/d1767a397c10">文本转语音TTS开发Android11适配方案</a> <br />
 * 需要在清单文件中的&lt;manifest>标签里面添加: <br />
 * <pre>
 *     &lt;queries>
 *         &lt;intent>
 *             &lt;action android:name="android.intent.action.TTS_SERVICE" /&gt;
 *         &lt;/intent>
 *     &lt;/queries>
 * </pre>
 *
 *  Android自带文字转语音支持:TextToSpeech, 但是在6.0之前不支持中文播放 <br />
 *  从文本合成语音以立即播放或创建声音文件。 TextToSpeech实例仅在完成初始化后才能用于合成文本。
 *  使用TextToSpeech实例完成后，请调用 {@link #shutdown()} 方法以释放TextToSpeech引擎使用的本机资源。
 * @author : ldf
 * date       : 2021/5/13 on 20
 * @version 1.0
 */
public class TextToSpeechUtils {

    protected static TextToSpeech tts;
    protected static HashMap<String, String> hashMap;

    /**
     * 调用这个方法初始化
     * @param engine 引擎, 见: {@link #getEngines()}, 可传null
     * @param listener 初始化监听, 可传null
     */
    public static void init(@NonNull Context context, @Nullable String engine, @Nullable TextToSpeech.OnInitListener listener) {
        if (tts == null) {
            if (listener == null) listener = new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
//                    boolean isMainThread = ThreadUtils.isMainThread();//主线程
                    if (status == TextToSpeech.SUCCESS) {
                        LogUtils.errorFormat("SpeechListener, 初始化成功!");
                    } else {
                        LogUtils.errorFormat("SpeechListener, 初始化失败! status=%d", status);
                    }
                }
            };
            tts = new TextToSpeech(context, listener, engine);
        }
        if (hashMap == null) hashMap = new HashMap<>();
    }

    /**
     * 设置语言
     * @param language 语言, 例: Locale.SIMPLIFIED_CHINESE
     * @return 是否设置成功
     */
    public static int setLanguage(Locale language) {
        if (language == null) {
            return TextToSpeech.ERROR;
        }
        if (tts == null) {
            LogUtils.error("语音引擎初始化失败, 请重新尝试!");
            return TextToSpeech.ERROR;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            LogUtils.error("手机安卓版本过低, 暂不支持语言设置!");
            return TextToSpeech.ERROR;
        }
        Locale locale = getLanguage();
        //如果设置的语言和当前语言不一致
        if (language != locale) {
            /**
             * @see TextToSpeech.LANG_AVAILABLE 语言可用于区域设置的语言，但不适用于国家/地区和变体
             * @see TextToSpeech.LANG_COUNTRY_AVAILABLE 语言可用于语言环境指定的语言和国家/地区，但不适用于变体
             * @see TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE 语言完全与语言环境指定的语言一致
             * @see TextToSpeech.LANG_MISSING_DATA 语言数据丢失
             * @see TextToSpeech.LANG_NOT_SUPPORTED 不支持该语言
             */
            int result = tts.setLanguage(language);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                LogUtils.error("该手机语言数据丢失");
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                LogUtils.error("该手机不支持该语言");
            }
            return result;
        }
        return TextToSpeech.ERROR;
    }



    public static void speak(@Nullable CharSequence text, @Nullable UtteranceProgressListenerImpl listener) {
        speak(text, (Locale) null, listener);
    }
    public static void speak(@Nullable CharSequence text, @Nullable Locale language,
                             @Nullable UtteranceProgressListenerImpl listener) {
        speak(text, language, (String) null, listener);
    }
    public static void speak(@Nullable CharSequence text, @Nullable Locale language,
                             @Nullable String utteranceId, @Nullable UtteranceProgressListenerImpl listener) {
        speak(text, language, TextToSpeech.QUEUE_FLUSH, (Bundle) null, utteranceId, listener);
    }
    /**
     * 播放
     * @param text 需要转化的文字
     * @param language 播放语言
     * @param queueMode 播放策略:
     *      @see TextToSpeech#QUEUE_FLUSH 会替换原有文字
     *      @see TextToSpeech#QUEUE_ADD 会将加入队列的待播报文字按顺序播放
     * @param params TTS参数，可以是null
     *      @see TextToSpeech.Engine#KEY_PARAM_STREAM 指定在说文本或播放文件时要使用的音频流类型。该值应为{@link AudioManager}中定义的STREAM_常量之一
     *      @see TextToSpeech.Engine#KEY_PARAM_UTTERANCE_ID 说出文字，播放文件或静默持续时间之后，在{@link TextToSpeech.OnUtteranceCompletedListener}中标识话语
     *      @see TextToSpeech.Engine#KEY_PARAM_VOLUME 指定相对于讲话文本时使用的当前流类型的音量的语音音量。音量被指定为从0到1的浮动范围，其中0是静默，而1是最大音量（默认行为）
     *      @see TextToSpeech.Engine#KEY_PARAM_PAN 指定在说文本时如何从左向右平移语音。平移指定为介于-1到+1之间的浮点数，其中-1映射到左硬平移，0映射到硬左平移（默认行为），而+1映射到硬右平移
     *
     * @param utteranceId 此请求的唯一标识符
     * @param listener 播放回调
     */
    public static int speak(@Nullable CharSequence text, @Nullable Locale language, int queueMode,
                             @Nullable Bundle params, @Nullable String utteranceId,
                             @Nullable UtteranceProgressListenerImpl listener) {
        if (TextUtils.isEmpty(text)) {
            if (listener != null) listener.onDone(utteranceId);
            return TextToSpeech.SUCCESS;
        }
        if (tts == null) {
            LogUtils.error("语音引擎初始化失败, 请重新尝试!");
            if (listener != null) listener.onError(utteranceId, TextToSpeech.ERROR);
            return TextToSpeech.ERROR;
        }
        setLanguage(language);

        /**
         * 设置语言播放监听, 回调参数是 utteranceId
         */
        int listenerResult = tts.setOnUtteranceProgressListener(listener);

        if (listener != null && utteranceId == null) utteranceId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * 初始化成功之后才可以播放文字, 否则会提示“speak failed: not bound to tts engine
             * 参四:
             *  此请求的唯一标识符, if(utteranceId == null) 播放的时候不会回调!!!
             */
            return tts.speak(text, queueMode, params, utteranceId);
        } else {
            hashMap.clear();
            hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            return tts.speak(String.valueOf(text), queueMode, hashMap);
        }
    }



    /**
     * 添加CharSequence(可以用TtsSpans spanned)和 声音文件 之间的映射。使用它，可以为一串文本添加自定义发音。<br />
     * 调用这个方法后, 再调用{@link TextToSpeech#speak(CharSequence, int, Bundle, String)} 将播放指定的声音资源(如果有的话)。
     * @param text The string of text. Example: "south_south_east"
     * @param file File object pointing to the sound file.
     */
    public static int addSpeech(@Nullable CharSequence text, @Nullable File file, @RawRes int resourceId) {
        if (TextUtils.isEmpty(text)) {
            return TextToSpeech.ERROR;
        }
        if (tts == null) {
            LogUtils.error("语音引擎初始化失败, 请重新尝试!");
            return TextToSpeech.ERROR;
        }
        //                                  Android 5.0, Api 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (file == null || !file.isFile()) {
                return tts.addSpeech(text, AppUtils.getAppPackageName(), resourceId);
            }
            return tts.addSpeech(text, file);
        } else {
            if (file == null || !file.isFile()) {
                return tts.addSpeech(String.valueOf(text), AppUtils.getAppPackageName(), resourceId);
            }
            return tts.addSpeech(String.valueOf(text), file.getAbsolutePath());
        }
    }

    /**
     * 检查是否用户的设置应该覆盖应用的设置。自Ice Cream Sandwich(Android 4.0)发布以来，用户设置永远不会强行覆盖应用程序的设置。
     */
    @Deprecated
    public static boolean areDefaultsEnforced() {
        if (tts != null) {
            return tts.areDefaultsEnforced();
        }
        return false;
    }

    /**
     * 获取可行的语言, 我的荣耀Honor v30有226种语言
     */
    @Nullable
    public static Set<Locale> getAvailableLanguages() {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.getAvailableLanguages();
            }
        }
        return null;
    }

    /**
     * 获取默认的引擎
     */
    @Nullable
    public static String getDefaultEngine() {
        if (tts != null) {
            return tts.getDefaultEngine();
        }
        return null;
    }

    /**
     * 获取默认使用的声音
     */
    @Nullable
    public static Voice getDefaultVoice() {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.getDefaultVoice();
            }
        }
        return null;
    }

    /**
     * 返回已安装的TTS引擎
     * 我的荣耀Honor v30只有1个引擎: label=讯飞语音引擎, name=com.iflytek.speechsuite
     */
    @Nullable
    public static List<TextToSpeech.EngineInfo> getEngines() {
        if (tts != null) {
            return tts.getEngines();
        }
        return null;
    }

    /**
     * 获取 TextToSpeech 当前的语言
     */
    @Nullable
    public static Locale getLanguage() {
        if (tts != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Voice voice = getVoice();
                if (voice != null) {
                    return voice.getLocale();//zh
                }
            } else {
                return tts.getLanguage();
            }
        }
        return null;
    }

    /**
     * 获取要转换文字的长度限制
     */
    public static int getMaxSpeechInputLength() {
        return TextToSpeech.getMaxSpeechInputLength();
    }

    /**
     * 返回一个当前正在使用的声音实例
     * Voice[Name: zh, locale: zh, quality: 300, latency: 300, requiresNetwork: false, features: []]
     */
    @Nullable
    public static Voice getVoice() {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.getVoice();
            }
        }
        return null;
    }

    /**
     * 引擎可用的声音
     */
    @Nullable
    public static Set<Voice> getVoices() {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.getVoices();
            }
        }
        return null;
    }

    /**
     * 检查指定语音是否支持
     */
    public static int isLanguageAvailable(Locale loc) {
        if (tts != null) {
            return tts.isLanguageAvailable(loc);
        }
        return TextToSpeech.ERROR;
    }

    /**
     * 是否正在播放
     */
    public static boolean isSpeaking() {
        if (tts != null) {
            return tts.isSpeaking();
        }
        return false;
    }



    ///////////////////////////////////////////////////////////////////////////
    // Earcon
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 添加文字和本地文件的映射。Adds a mapping between a string of text and a sound file. <br />
     * 使用它来添加自定耳标。Use this to add custom earcons.
     * @param earcon The name of the earcon. Example: "[tick]"
     * @param file File object pointing to the sound file. 添加文字和本地文件的映射
     * @param resourceId 添加指定包下的文字和本地文件的映射, 例: R.raw.xxx
     */
    public static int addEarcon(String earcon, @Nullable File file, @RawRes int resourceId) {
        if (TextUtils.isEmpty(earcon)) {
            return TextToSpeech.ERROR;
        }
        if (tts == null) {
            LogUtils.error("语音引擎初始化失败, 请重新尝试!");
            return TextToSpeech.ERROR;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && file != null) {
            //添加文字和本地文件的映射, api21
            return tts.addEarcon(earcon, file);
        }
        //添加指定包下的文字和本地文件的映射
        return tts.addEarcon(earcon, AppUtils.getAppPackageName(), resourceId);
    }

    /**
     * 使用指定方式和参数播放耳标
     * @param queueMode 播放策略:
     *      @see TextToSpeech#QUEUE_FLUSH 会替换原有文字
     *      @see TextToSpeech#QUEUE_ADD 会将加入队列的待播报文字按顺序播放
     * @param params
     * @param utteranceId 此请求的唯一标识符
     */
    public static int playEarcon(@Nullable String earcon, @Nullable Locale language, int queueMode,
                                 @Nullable Bundle params, @Nullable String utteranceId,
                                 @Nullable UtteranceProgressListenerImpl listener) {
        if (TextUtils.isEmpty(earcon)) {
            if (listener != null) listener.onDone(utteranceId);
            return TextToSpeech.SUCCESS;
        }
        if (tts == null) {
            LogUtils.error("语音引擎初始化失败, 请重新尝试!");
            if (listener != null) listener.onError(utteranceId, TextToSpeech.ERROR);
            return TextToSpeech.ERROR;
        }
        setLanguage(language);

        /**
         * 设置语言播放监听, 回调参数是 utteranceId
         */
        int listenerResult = tts.setOnUtteranceProgressListener(listener);
        if (listener != null && utteranceId == null) utteranceId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return tts.playEarcon(earcon, queueMode, params, utteranceId);
        } else {
            hashMap.clear();
            hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            return tts.playEarcon(earcon, queueMode, hashMap);
        }
    }



    /**
     * 指定时间使指定的事物静音
     * @param queueMode 播放策略:
     *      @see TextToSpeech#QUEUE_FLUSH 会替换原有文字
     *      @see TextToSpeech#QUEUE_ADD 会将加入队列的待播报文字按顺序播放
     * @param utteranceId 此请求的唯一标识符
     */
    public static int playSilentUtterance(long durationInMs, int queueMode, @Nullable String utteranceId,
                                          @Nullable UtteranceProgressListenerImpl listener) {
        if (tts == null) {
            LogUtils.error("语音引擎初始化失败, 请重新尝试!");
            if (listener != null) listener.onError(utteranceId, TextToSpeech.ERROR);
            return TextToSpeech.ERROR;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * 设置语言播放监听, 回调参数是 utteranceId
             */
            int listenerResult = tts.setOnUtteranceProgressListener(listener);
            if (listener != null && utteranceId == null) utteranceId = "";
            return tts.playSilentUtterance(durationInMs, queueMode, utteranceId);
        } else {
            LogUtils.error("手机版本过低, 没有 playSilentUtterance()这个方法!");
            if (listener != null) listener.onError(utteranceId, TextToSpeech.ERROR_INVALID_REQUEST);
            return TextToSpeech.ERROR_INVALID_REQUEST;
        }
    }

    /**
     * 设置播放和存文件的音频属性
     */
    public static int setAudioAttributes(AudioAttributes audioAttributes) {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.setAudioAttributes(audioAttributes);
            }
        }
        return TextToSpeech.ERROR;
    }

    /**
     * 设置音调，值越大声音越尖（女生），值越小则变成男声, 默认1.0
     */
    public static int setPitch(float pitch) {
        if (tts != null) {
            return tts.setPitch(pitch);
        }
        return TextToSpeech.ERROR;
    }

    /**
     * 设定语速 ，默认1.0正常语速, 至少>0有效
     */
    public static int setSpeechRate(float speechRate) {
        if (tts != null) {
            return tts.setSpeechRate(speechRate);
        }
        return TextToSpeech.ERROR;
    }

    /**
     * 设置文字语音转化的声音
     */
    public static int setVoice(Voice voice) {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.setVoice(voice);
            }
        }
        return TextToSpeech.ERROR;
    }

    // TODO: 2024/11/28 暂停, 也许可以参考 https://blog.csdn.net/hfut_why/article/details/95735345 ??
    protected static void pause() {
    }

    /**
     * 中断当前任务，不管是播放还是转化文件，抛弃队列内的其他任务
     */
    public static int stop() {
        if (tts != null) {
            return tts.stop();
        }
        return TextToSpeech.ERROR;
    }

    /**
     * 释放引擎使用的资源
     */
    public static void shutdown() {
        if (tts != null) {
            tts.shutdown();
            tts = null;
        }
        hashMap.clear();
        hashMap = null;
    }

    /**
     * 文字输入到文件
     * @param text 可为null ??
     */
    public static int synthesizeToFile(@Nullable CharSequence text, @Nullable Locale language,
                                       @Nullable Bundle params, @NonNull File file, @Nullable String utteranceId,
                                       @Nullable UtteranceProgressListenerImpl listener) throws FileNotFoundException {
        if (file == null) {
            LogUtils.error("file=null");
            if (listener != null) listener.onError(utteranceId, TextToSpeech.ERROR);
            return TextToSpeech.ERROR;
        }
        if (tts == null) {
            LogUtils.error("语音引擎初始化失败, 请重新尝试!");
            if (listener != null) listener.onError(utteranceId, TextToSpeech.ERROR);
            return TextToSpeech.ERROR;
        }
        setLanguage(language);

        /**
         * 设置语言播放监听, 回调参数是 utteranceId
         */
        int listenerResult = tts.setOnUtteranceProgressListener(listener);
        if (listener != null && utteranceId == null) utteranceId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android 11.0 = 30
            //FileNotFoundException
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
            return tts.synthesizeToFile(text, params, fileDescriptor, utteranceId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Android 5.0 = 21
            return tts.synthesizeToFile(text, params, file, utteranceId);
        } else {
            hashMap.clear();
            hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            //还有一些其它参数...
            return tts.synthesizeToFile(text == null ? null : text.toString(), hashMap, file.getAbsolutePath());
        }
    }
}