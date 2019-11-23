package com.actor.myandroidframework.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin.
 * Editor by actor.
 * SharedPreferences 共享参数的工具类
 * @version 1.0
 */
public class SPUtils {

//    private static final String PREF_NAME = "config_sputils";
    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreference() {
        if (sharedPreferences == null) {
            //ActorApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigUtils.APPLICATION);//获取默认
        }
        return sharedPreferences;
    }

    /**
     * 下方是String方法区域
     */
    public static void putString(String key,  @Nullable String value) {
//        getSharedPreference().edit().putString(key, value).commit();//同步提交,返回boolean,不提倡.
        getSharedPreference().edit().putString(key, value).apply();//异步提交,无返回值
    }

    public static String getString(String key) {
        return getSharedPreference().getString(key, null);
    }

    public static String getStringNoNull(String key) {
        String value = getString(key);
        if (value == null) return "";
        return value;
    }

    /**
     * 下方是StringSet方法区域
     */
    public static void putStringSet(String key, @Nullable Set<String> value) {
        getSharedPreference().edit().putStringSet(key, value).apply();
    }

    public static Set<String> getStringSet(String key) {
        return getSharedPreference().getStringSet(key, null);
    }

    /**
     * 下方是int方法区域
     */
    public static void putInt(String key, int value) {
        getSharedPreference().edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        return getSharedPreference().getInt(key, defValue);
    }

    /**
     * 下方是long方法区域
     */
    public static void putLong(String key, long value) {
        getSharedPreference().edit().putLong(key, value).apply();
    }

    public static long getLong(String key, long defValue) {
        return getSharedPreference().getLong(key, defValue);
    }

    /**
     * 下方是float方法区域
     */
    public static void putFloat(String key, float value) {
        getSharedPreference().edit().putFloat(key, value).apply();
    }

    public static float getFloat(String key, float defValue) {
        return getSharedPreference().getFloat(key, defValue);
    }

    /**
     * 下方是boolean方法区域
     */
    public static void putBoolean(String key, boolean value) {
        getSharedPreference().edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreference().getBoolean(key, defValue);
    }

    /**
     * 获取所有
     */
    public static Map<String, ?> getAll() {
        return getSharedPreference().getAll();
    }

    /**
     * 是否包含某个key
     */
    public static boolean contails(String key) {
        return getSharedPreference().contains(key);
    }

    /**
     * 删除
     */
    public static void remove(String key) {
        getSharedPreference().edit().remove(key).apply();
    }

    /**
     * 删除所有
     */
    public static void removeAll() {
        getSharedPreference().edit().clear().apply();
    }

    /**
     * 注册监听某个key的值改变后回调
     */
    public static void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreference().registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 注销监听
     */
    public static void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreference().unregisterOnSharedPreferenceChangeListener(listener);
    }
}
