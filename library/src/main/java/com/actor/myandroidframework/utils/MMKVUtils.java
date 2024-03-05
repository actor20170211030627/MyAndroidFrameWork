package com.actor.myandroidframework.utils;

import android.content.SharedPreferences;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.mmkv.MMKV;

import java.util.Set;

/**
 * description: <a href="https://github.com/Tencent/MMKV" target="_blank">MMKV - Github</a>
 * 是腾讯基于 mmap 内存映射的 key-value 组件, 性能&可靠性远远高于SharedPreferences <br />
 * @author : ldf
 * date       : 2020/5/22 on 16:36
 * @version 1.0
 */
public class MMKVUtils {

    //腾讯键值缓存
    protected static MMKV mmkv;

    public static MMKV getMMKV() {
        if (mmkv == null) mmkv = MMKV.defaultMMKV();
        return mmkv;
    }

    /**
     * 自定义 MMKV
     */
    public static void setMMKV(MMKV mmkv) {
        if (mmkv != null) MMKVUtils.mmkv = mmkv;
    }



    /**
     * 下方是boolean方法区域
     */
    public static boolean putBoolean(String key, boolean value) {
//        SharedPreferences.Editor editor = getMMKV().putBoolean(key, value);//一样的
//        return editor.commit();
        return getMMKV().encode(key, value);
    }

    /**
     * @return 获取boolean, 默认返回false
     */
    public static boolean getBoolean(String key) {
        return getMMKV().decodeBool(key);
    }

    /**
     * @param defaultValue 默认值
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
//        return getMMKV().getBoolean(key, defaultValue);//一样的
        return getMMKV().decodeBool(key, defaultValue);
    }



    /**
     * 下方是int方法区域
     */
    public static boolean putInt(String key, int value) {
        return getMMKV().encode(key, value);
    }
    /**
     * 获取int, 默认返回0
     */
    public static int getInt(String key) {
        return getMMKV().decodeInt(key);
    }
    public static int getInt(String key, int defaultValue) {
        return getMMKV().decodeInt(key, defaultValue);
    }



    /**
     * 下方是long方法区域
     */
    public static boolean putLong(String key, long value) {
        return getMMKV().encode(key, value);
    }
    /**
     * 获取long, 默认返回0
     */
    public static long getLong(String key) {
        return getMMKV().decodeLong(key);
    }
    public static long getLong(String key, long defaultValue) {
        return getMMKV().decodeLong(key, defaultValue);
    }



    /**
     * 下方是float方法区域
     */
    public static boolean putFloat(String key, float value) {
        return getMMKV().encode(key, value);
    }
    /**
     * 获取float, 默认返回0
     */
    public static float getFloat(String key) {
        return getMMKV().decodeFloat(key);
    }
    public static float getFloat(String key, float defaultValue) {
        return getMMKV().decodeFloat(key, defaultValue);
    }



    /**
     * 下方是double方法区域
     */
    public static boolean putDouble(String key, double value) {
        return getMMKV().encode(key, value);
    }
    /**
     * 获取double, 默认返回0
     */
    public static double getDouble(String key) {
        return getMMKV().decodeDouble(key);
    }
    public static double getDouble(String key, double defaultValue) {
        return getMMKV().decodeDouble(key, defaultValue);
    }



    /**
     * 下方是String方法区域
     */
    public static boolean putString(String key, @Nullable String value) {
        return getMMKV().encode(key, value);
    }

    @Nullable
    public static String getString(String key) {
        return getMMKV().decodeString(key);
    }

    public static String getString(String key, String defaultValue) {
        return getMMKV().getString(key, defaultValue);
    }



    /**
     * 下方是Set<String>方法区域
     */
    public static boolean putStringSet(String key, @Nullable Set<String> value) {
        if (value == null) return false;
        return getMMKV().encode(key, value);
    }
    /**
     * 获取Set<String>, 默认返回HashSet类型
     */
    @Nullable
    public static Set<String> getStringSet(String key) {
        return getMMKV().decodeStringSet(key);
    }

    /**
     * 下方是byte[]方法区域
     */
    public static boolean putBytes(String key,  @Nullable byte[] value) {
        return getMMKV().encode(key, value);
    }

    @Nullable
    public static byte[] getBytes(String key) {
        return getMMKV().decodeBytes(key);
    }

    /**
     * 下方是Parcelable方法区域
     */
    public static boolean putParcelable(String key, @Nullable Parcelable value) {
        if (value == null) return false;
        return getMMKV().encode(key, value);
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(String key, Class<T> tClass) {
        return getMMKV().decodeParcelable(key, tClass);
    }

    /**
     * 移除某个键所对应的值
     */
    public static void remove(String key) {
        getMMKV().removeValueForKey(key);
    }

    /**
     * 所有key
     */
    @Nullable
    public static String[] allKeys() {
        return getMMKV().allKeys();
    }

    /**
     * 移除所有
     */
    public static void removeAll() {
        getMMKV().clearAll();
    }

    /**
     * 从 SharedPreferences 导入
     */
    public static int importFromSharedPreferences(@NonNull SharedPreferences preferences) {
        return getMMKV().importFromSharedPreferences(preferences);
    }

    /**
     * ?
     */
    public static long count() {
        return getMMKV().count();
    }

    /**
     * ?
     */
    @Nullable
    public static String cryptKey() {
        return getMMKV().cryptKey();
    }
}
