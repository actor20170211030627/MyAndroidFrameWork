package com.actor.myandroidframework.utils;

import android.content.Context;
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
 * @version 1.1 fix bug
 */
public class SPUtils {

    /**
     * @see PreferenceManager#getDefaultSharedPreferences(Context) //获取默认 SharedPreferences
     * @see PreferenceManager#getDefaultSharedPreferencesName(Context) //默认'SharedPreferences'名称
     * @deprecated 由于未知原因, 如果使用默认 SharedPreferences 或 默认名称 创建的 SharedPreferences,
     * 当应用崩溃后, 存储的数据会丢失!
     */
    @Deprecated
    private static final String BAD_NAME = ConfigUtils.APPLICATION.getPackageName() + "_preferences";
    /**
     * 取的 {@link com.blankj.utilcode.util.SPUtils} 一样的默认名称
     */
    private static final String DEFAULT_NAME = "spUtils";
    private static SharedPreferences sharedPreferences;

    /**
     * @param name sp名称
     * @param mode 模式
     * @see android.content.Context#MODE_PRIVATE
     *          代表私有访问模式,在Android 2.3及以前这个访问模式是可以跨进程的,
     *          之后的版本这个模式就只能访问同一进程下的数据.
     *
     * @see android.content.Context#MODE_MULTI_PROCESS
     *          在Android 2.3及以前，这个标志位都是默认开启的，允许多个进程访问同一个SharedPrecferences对象。
     *          而Android 2.3以后的版本，须将MODE_MULTI_PROCESS这个值传递给mode参数，才能开启多进程访问。
     *
     * @see android.content.Context#MODE_WORLD_READABLE 表示当前文件可以被其他应用读取
     *
     * @see android.content.Context#MODE_WORLD_WRITEABLE 表示当前文件可以被其他应用写入
     * @return
     */
    public static void setSharedPreference(String name, int mode) {
        if (sharedPreferences == null) {
            if (BAD_NAME.equals(name)) name = DEFAULT_NAME;
            sharedPreferences = ConfigUtils.APPLICATION.getSharedPreferences(name, mode);
        }
    }

    public static SharedPreferences getSharedPreference() {
        if (sharedPreferences == null) {
            //不能获取默认的 SharedPreferences, 有bug
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigUtils.APPLICATION);
            sharedPreferences = ConfigUtils.APPLICATION.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
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
