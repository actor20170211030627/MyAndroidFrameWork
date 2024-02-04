package com.actor.other_utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * description: <a href="https://www.jianshu.com/p/6e9cc56f080b">android 语音合成(文字转语音播放)</a> <br />
 *              <a href="https://blog.csdn.net/long375577908/article/details/78437278">TextToSpeech的使用</a> <br />
 *              <a href="https://www.jianshu.com/p/d1767a397c10">文本转语音TTS开发Android11适配方案</a> <br />
 *              <br />
 * Android自带文字转语音支持:TextToSpeech, 但是在6.0之前不支持中文播放 <br />
 *
 *  从文本合成语音以立即播放或创建声音文件。 TextToSpeech实例仅在完成初始化后才能用于合成文本。
 *  使用TextToSpeech实例完成后，请调用 {@link #shutdown()} 方法以释放TextToSpeech引擎使用的本机资源。
 * @author : ldf
 * date       : 2021/5/13 on 20
 * @version 1.0
 */
public class TextToSpeechUtils {

    protected static TextToSpeech tts;

    /**
     * 调用这个方法初始化
     * 初始化的参3引擎包名, 见: {@link #getEngines()}
     */
    public static void init(Context context) {
        if (tts == null) {
            tts = new TextToSpeech(ConfigUtils.APPLICATION, status -> {
                if (status != TextToSpeech.SUCCESS) {
                    LogUtils.error("SpeechListener, 初始化成功!");
                } else {
                    LogUtils.error("SpeechListener, 初始化失败!");
                }
            }, /*String engine*/ null);
        }
    }

    /**
     * 设置语言
     * @return 是否设置成功
     */
    public static boolean setLanguage(Locale language) {
        if (language == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            LogUtils.error("手机安卓版本过低, 暂不支持语言设置!");
            return false;
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
            int result = tts.setLanguage(Locale.CHINESE);
            if (result == TextToSpeech.LANG_MISSING_DATA){
                ToastUtils.showShort("该手机语言数据丢失");
                return false;
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                ToastUtils.showShort("该手机不支持该语言");
                LogUtils.error("该手机不支持该语言");
                return false;
            }
        }
        return true;
    }



    public static void speak(CharSequence text) {
        speak(text, null);
    }
    public static void speak(CharSequence text, Locale language) {
        speak(text, language, null);
    }
    public static void speak(CharSequence text, Locale language, String utteranceId) {
        speak(text, language, TextToSpeech.QUEUE_FLUSH, utteranceId);
    }
    /**
     * 播放
     * @param text 需要转化的文字
     * @param language 播放语言
     * @param queueMode 播放策略:
     *      @see TextToSpeech#QUEUE_FLUSH 会替换原有文字
     *      @see TextToSpeech#QUEUE_ADD 会将加入队列的待播报文字按顺序播放
     * @param utteranceId 此请求的唯一标识符
     */
    public static void speak(CharSequence text, Locale language, int queueMode, String utteranceId) {
        if (text == null) {
            return;
        }
        if (tts == null) {
            ToastUtils.showShort("语音引擎初始化失败, 请重新尝试!");
            return;
        }
        setLanguage(language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * 初始化成功之后才可以播放文字, 否则会提示“speak failed: not bound to tts engine
             * 参三:
             * @see TextToSpeech.Engine.KEY_PARAM_STREAM 指定在说文本或播放文件时要使用的音频流类型。该值应为{@link AudioManager}中定义的STREAM_常量之一
             * @see TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID 说出文字，播放文件或静默持续时间之后，在{@link TextToSpeech.OnUtteranceCompletedListener}中标识话语
             * @see TextToSpeech.Engine.KEY_PARAM_VOLUME 指定相对于讲话文本时使用的当前流类型的音量的语音音量。音量被指定为从0到1的浮动范围，其中0是静默，而1是最大音量（默认行为）
             * @see TextToSpeech.Engine.KEY_PARAM_PAN 指定在说文本时如何从左向右平移语音。平移指定为介于-1到+1之间的浮点数，其中-1映射到左硬平移，0映射到硬左平移（默认行为），而+1映射到硬右平移
             * 参四:
             *  此请求的唯一标识符
             */
            tts.speak(text, queueMode, null, utteranceId);
        } else {
            tts.speak(text.toString(), queueMode, null);
        }
    }

    /**
     * 添加文字和本地文件的映射
     */
//    public static boolean addEarcon(String earcon, File file) {
//        if (earcon != null && tts != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //添加文字和本地文件的映射, api21
//                return tts.addEarcon(earcon, file) == TextToSpeech.SUCCESS;
//            } else {
//                //添加指定包下的文字和本地文件的映射
//                return tts.addEarcon(earcon, AppUtils.getAppPackageName(), R.raw.picture_music) == TextToSpeech.SUCCESS;
//                //添加文字和本地文件的映射
////                return tts.addEarcon(earcon, "fileName") == TextToSpeech.SUCCESS;
//            }
//        }
//        return false;
//    }

    /**
     * 添加本地资源和文字的映射
     */
//    public static boolean addSpeech(CharSequence text, File file) {
//        if (text != null && tts != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //添加字符串和本地文件的映射, api21
//                return tts.addSpeech(text, file) == TextToSpeech.SUCCESS;
//                //添加指定包下的文字和本地文件的映射
////                return tts.addSpeech(text, AppUtils.getAppPackageName(), R.raw.picture_music) == TextToSpeech.SUCCESS;
//            } else {
//                //添加指定包下的文字和本地文件的映射
//                return tts.addSpeech(text.toString(), AppUtils.getAppPackageName(), R.raw.picture_music) == TextToSpeech.SUCCESS;
//                //添加文字和本地文件的映射
////                return tts.addSpeech(text.toString(), "fileName") == TextToSpeech.SUCCESS;
//            }
//        }
//        return false;
//    }

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

    /**
     * 使用指定方式和参数播放耳标
     */
    public static boolean playEarcon(String earcon, int queueMode, Bundle params, String utteranceId) {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.playEarcon(earcon, queueMode, params, utteranceId) == TextToSpeech.SUCCESS;
            } else {
                return tts.playEarcon(earcon, queueMode, (HashMap<String, String>) null) == TextToSpeech.SUCCESS;
            }
        }
        return false;
    }

    /**
     * 指定时间使指定的事物静音
     */
    public static boolean playSilentUtterance(long durationInMs, int queueMode, String utteranceId) {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.playSilentUtterance(durationInMs, queueMode, utteranceId) == TextToSpeech.SUCCESS;
            }
        }
        return false;
    }

    /**
     * 设置播放和存文件的音频属性
     */
    public static boolean setAudioAttributes(AudioAttributes audioAttributes) {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.setAudioAttributes(audioAttributes) == TextToSpeech.SUCCESS;
            }
        }
        return false;
    }

    /**
     * 设置语言播放监听, 回调的方法参数就是 {@link #speak(CharSequence, Locale, int, String)} 里的最后一个值
     */
    public static boolean setOnUtteranceProgressListener(UtteranceProgressListener listener) {
        if (tts != null) {
            return tts.setOnUtteranceProgressListener(listener) == TextToSpeech.SUCCESS;
        }
        return false;
    }

    /**
     * 设置音调，值越大声音越尖（女生），值越小则变成男声, 默认1.0
     */
    public static boolean setPitch(float pitch) {
        if (tts != null) {
            return tts.setPitch(pitch) == TextToSpeech.SUCCESS;
        }
        return false;
    }

    /**
     * 设定语速 ，默认1.0正常语速, 至少>0有效
     */
    public static boolean setSpeechRate(float speechRate) {
        if (tts != null) {
            return tts.setSpeechRate(speechRate) == TextToSpeech.SUCCESS;
        }
        return false;
    }

    /**
     * 设置文字语音转化的声音
     */
    public static boolean setVoice(Voice voice) {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return tts.setVoice(voice) == TextToSpeech.SUCCESS;
            }
        }
        return false;
    }

    /**
     * 释放引擎使用的资源
     */
    public static void shutdown() {
        if (tts != null) {
            tts.shutdown();
            tts = null;
        }
    }

    /**
     * 中断当前任务，不管是播放还是转化文件，抛弃队列内的其他任务
     */
    public static boolean stop() {
        if (tts != null) {
            return tts.stop() == TextToSpeech.SUCCESS;
        }
        return false;
    }

    /**
     * 使用传入的参数转化给定的文字到文件
     */
    public static boolean synthesizeToFile(CharSequence text, Bundle params, File file, String utteranceId) {
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android 11.0 = 30
                return tts.synthesizeToFile(text, params, (ParcelFileDescriptor) null, utteranceId) == TextToSpeech.SUCCESS;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //Android 5.0 = 21
                return tts.synthesizeToFile(text, params, file, utteranceId) == TextToSpeech.SUCCESS;
            } else {
                return tts.synthesizeToFile(text.toString(), (HashMap<String, String>) null, file.getAbsolutePath()) == TextToSpeech.SUCCESS;
            }
        }
        return false;
    }
}