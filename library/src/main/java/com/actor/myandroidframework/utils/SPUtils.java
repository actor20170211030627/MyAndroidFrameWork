package com.actor.myandroidframework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin. <br />
 * Editor by actor. <br />
 * SharedPreferences 共享参数的工具类, 实现类:{@link android.app.SharedPreferencesImpl}, 参考<a href="https://www.jianshu.com/p/2a4b411383d4" target="_blank">简书</a> <br />
 *
 * <br />
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th>创建问题前提</th>
 *     </tr>
 *     <tr>
 *         <td>①.后台杀死</td>
 *         <td>..........</td>
 *         <td>..........①</td>
 *     </tr>
 *     <tr>
 *         <td>②.在MainActivity创建一个崩溃代码: int x = 1 / 0;</td>
 *         <td>..........</td>
 *         <td>..........②</td>
 *     </tr>
 *
 *     <tr />
 *     <tr>
 *         <th>{@link SharedPreferences}创建方式</th>
 *         <th nowrap="nowrap">当遇到问题①</th>
 *         <th>当遇到问题②</th>
 *     </tr>
 *     <tr>
 *         <td>1.{@link PreferenceManager#getDefaultSharedPreferences(Context)}</td>
 *         <td>会丢数据</td>
 *         <td>'LoginActivity'保存账号后进入'MainActivity', 遇到②, 存储的数据会丢失!</td>
 *     </tr>
 *     <tr>
 *         <td>2.{@link PreferenceManager#getDefaultSharedPreferencesName(Context)}</td>
 *         <td>会丢数据</td>
 *         <td>同上</td>
 *     </tr>
 *     <tr>
 *         <td>3.使用自定义名称创建</td>
 *         <td>不会丢数据</td>
 *         <td>同上</td>
 *     </tr>
 *     <tr>
 *         <td>4.如果使用 {@link com.blankj.utilcode.util.SPStaticUtils SPStaticUtils}</td>
 *         <td>同上</td>
 *         <td>同上</td>
 *     </tr>
 *     <tr>
 *         <td>5.如果使用 {@link com.blankj.utilcode.util.CacheDiskUtils#getInstance(File) CacheDiskUtils.getInstance(File)}</td>
 *         <td>同上</td>
 *         <td>
 *             不会丢数据 <br />
 *             (如果想长期存储, file 传 'getFilesDir()'. 已经在 {@link com.actor.myandroidframework.application.ActorApplication ActorApplication}) 中配置, 直接用: {@link com.actor.myandroidframework.application.ActorApplication#aCache ActorApplication.aCache})就行!
 *         </td>
 *     </tr>
 * </table>
 *
 * <br />
 * 上方数据丢失应该是数据回滚(.bak)造成的结果
 * @version 1.0 <br />
 *          1.1 fix bug
 */
public class SPUtils {

    //这个名字有问题, 见上方第 2 条
    @Deprecated
    protected static final String BAD_NAME = ConfigUtils.APPLICATION.getPackageName() + "_preferences";
    /**
     * 取的 {@link com.blankj.utilcode.util.SPUtils} 一样的默认名称
     */
    protected static final String DEFAULT_NAME = "spUtils";
    protected static SharedPreferences sharedPreferences;

    /**
     * @param name sp名称, 注意不要使用 {@link #BAD_NAME}
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

    @Nullable
    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defValue) {
        return getSharedPreference().getString(key, defValue);
    }

    /**
     * 如果值=null, 返回""
     */
    @NonNull
    public static String getStringNoNull(String key) {
        return getString(key, "");
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
