package com.actor.myandroidframework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
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
 *         <td>同上</td>
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
 *         <td>5.如果使用 {@link MMKVUtils#getMMKV()}</td>
 *         <td>同上</td>
 *         <td>
 *             不会丢数据
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
    protected static final String BAD_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? PreferenceManager.getDefaultSharedPreferencesName(ConfigUtils.APPLICATION) : ConfigUtils.APPLICATION.getPackageName() + "_preferences";
    /**
     * 取的 {@link com.blankj.utilcode.util.SPUtils} 一样的默认名称
     */
    protected static final String DEFAULT_NAME = "spUtils";
    protected static final String DEFAULT_NAME_EXPIRE = "spUtils_expire";
    protected static SharedPreferences sharedPreferences;
    protected static SharedPreferences sharedPreferencesExpire;

    public static final long ExpireNever    = 0L;
    public static final long ExpireSecond   = 1_000L;
    public static final long ExpireInMinute = 60 * ExpireSecond;
    public static final long ExpireInHour   = 60 * ExpireInMinute;
    public static final long ExpireInDay    = 24 * ExpireInHour;
    public static final long ExpireInWeek   = 7 * ExpireInDay;
    public static final long ExpireInMonth  = 30 * ExpireInDay;
    public static final long ExpireInYear   = 365 * ExpireInDay;


    /**
     * @param name sp名称, 注意不要使用 {@link #BAD_NAME}
     * @param mode 模式
     * @see Context#MODE_PRIVATE
     *          代表私有访问模式,在Android 2.3及以前这个访问模式是可以跨进程的,
     *          之后的版本这个模式就只能访问同一进程下的数据.
     *
     * @see Context#MODE_MULTI_PROCESS
     *          在Android 2.3及以前，这个标志位都是默认开启的，允许多个进程访问同一个SharedPrecferences对象。
     *          而Android 2.3以后的版本，须将MODE_MULTI_PROCESS这个值传递给mode参数，才能开启多进程访问。
     *
     * @see Context#MODE_WORLD_READABLE 表示当前文件可以被其他应用读取
     *
     * @see Context#MODE_WORLD_WRITEABLE 表示当前文件可以被其他应用写入
     * @return
     */
    public static void setSharedPreference(@NonNull String name, int mode) {
        if (sharedPreferences == null) {
            sharedPreferences = ConfigUtils.APPLICATION.getSharedPreferences(name, mode);
        }
    }
    /**
     * 获取存取保存数据的Sp
     * @deprecated 非必要不要调用此方法
     */
    @Deprecated
    public static SharedPreferences getSharedPreference() {
        if (sharedPreferences == null) {
            //不能获取默认的 SharedPreferences, 有bug
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigUtils.APPLICATION);
            sharedPreferences = ConfigUtils.APPLICATION.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }


    /**
     * 设置过期时间Sp
     */
    public static void setSharedPreferenceExpire(@NonNull String name, int mode) {
        if (sharedPreferencesExpire == null) {
            sharedPreferencesExpire = ConfigUtils.APPLICATION.getSharedPreferences(name, mode);
        }
    }
    /**
     * 获取保存时间的Sp
     * @deprecated 非必要不要调用此方法
     */
    @Deprecated
    public static SharedPreferences getSharedPreferenceExpire() {
        if (sharedPreferencesExpire == null) {
            sharedPreferencesExpire = ConfigUtils.APPLICATION.getSharedPreferences(DEFAULT_NAME_EXPIRE, Context.MODE_PRIVATE);
        }
        return sharedPreferencesExpire;
    }



    ///////////////////////////////////////////////////////////////////////////
    /// 下方是String方法区域
    ///////////////////////////////////////////////////////////////////////////
    public static void putString(@NonNull String key,  @Nullable String value) {
        putString(key, value, ExpireNever);
    }
    /**
     * put String
     * @param expireMs 保存的时间, 单位ms
     */
    public static void putString(@NonNull String key,  @Nullable String value, @IntRange(from = ExpireNever) long expireMs) {
//        boolean commit = getSharedPreference().edit().putString(key, value).commit();//同步提交,返回boolean,不提倡.
        getSharedPreference().edit().putString(key, value).apply();//异步提交,无返回值
        putExpireTimeMs(key, expireMs);
    }
    @Nullable
    public static String getString(@NonNull String key) {
        return getString(key, null);
    }
    public static String getString(@NonNull String key, @Nullable String defValue) {
        if (isDue(key, true)) return defValue;
        return getSharedPreference().getString(key, defValue);
    }



    ///////////////////////////////////////////////////////////////////////////
    /// 下方是StringSet方法区域
    ///////////////////////////////////////////////////////////////////////////
    public static void putStringSet(@NonNull String key, @Nullable Set<String> value) {
        putStringSet(key, value, ExpireNever);
    }
    /**
     * put StringSet
     * @param expireMs 保存的时间, 单位ms
     */
    public static void putStringSet(@NonNull String key, @Nullable Set<String> value, @IntRange(from = ExpireNever) long expireMs) {
        getSharedPreference().edit().putStringSet(key, value).apply();
        putExpireTimeMs(key, expireMs);
    }
    public static Set<String> getStringSet(@NonNull String key) {
        return getStringSet(key, null);
    }
    public static Set<String> getStringSet(@NonNull String key, @Nullable Set<String> defValue) {
        if (isDue(key, true)) return defValue;
        return getSharedPreference().getStringSet(key, defValue);
    }



    ///////////////////////////////////////////////////////////////////////////
    /// 下方是int方法区域
    ///////////////////////////////////////////////////////////////////////////
    public static void putInt(@NonNull String key, int value) {
        putInt(key, value, ExpireNever);
    }
    /**
     * put int
     * @param expireMs 保存的时间, 单位ms
     */
    public static void putInt(@NonNull String key, int value, @IntRange(from = ExpireNever) long expireMs) {
        getSharedPreference().edit().putInt(key, value).apply();
        putExpireTimeMs(key, expireMs);
    }
    public static int getInt(@NonNull String key, int defValue) {
        if (isDue(key, true)) return defValue;
        return getSharedPreference().getInt(key, defValue);
    }



    ///////////////////////////////////////////////////////////////////////////
    /// 下方是long方法区域
    ///////////////////////////////////////////////////////////////////////////
    public static void putLong(@NonNull String key, long value) {
        putLong(key, value, ExpireNever);
    }
    /**
     * put long
     * @param expireMs 保存的时间, 单位ms
     */
    public static void putLong(@NonNull String key, long value, @IntRange(from = ExpireNever) long expireMs) {
        getSharedPreference().edit().putLong(key, value).apply();
        putExpireTimeMs(key, expireMs);
    }
    public static long getLong(@NonNull String key, long defValue) {
        if (isDue(key, true)) return defValue;
        return getSharedPreference().getLong(key, defValue);
    }



    ///////////////////////////////////////////////////////////////////////////
    /// 下方是float方法区域
    ///////////////////////////////////////////////////////////////////////////
    public static void putFloat(@NonNull String key, float value) {
        putFloat(key, value, ExpireNever);
    }
    /**
     * put float
     * @param expireMs 保存的时间, 单位ms
     */
    public static void putFloat(@NonNull String key, float value, @IntRange(from = ExpireNever) long expireMs) {
        getSharedPreference().edit().putFloat(key, value).apply();
        putExpireTimeMs(key, expireMs);
    }
    public static float getFloat(@NonNull String key, float defValue) {
        if (isDue(key, true)) return defValue;
        return getSharedPreference().getFloat(key, defValue);
    }



    ///////////////////////////////////////////////////////////////////////////
    /// 下方是boolean方法区域
    ///////////////////////////////////////////////////////////////////////////
    public static void putBoolean(@NonNull String key, boolean value) {
        putBoolean(key, value, ExpireNever);
    }
    /**
     * put boolean
     * @param expireMs 保存的时间, 单位ms
     */
    public static void putBoolean(@NonNull String key, boolean value, @IntRange(from = ExpireNever) long expireMs) {
        getSharedPreference().edit().putBoolean(key, value).apply();
        putExpireTimeMs(key, expireMs);
    }
    public static boolean getBoolean(@NonNull String key, boolean defValue) {
        if (isDue(key, true)) return defValue;
        return getSharedPreference().getBoolean(key, defValue);
    }



    ///////////////////////////////////////////////////////////////////////////
    /// 其他方法
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 获取所有
     */
    public static Map<String, ?> getAll() {
        Map<String, ?> all = getSharedPreference().getAll();
        if (all == null || all.isEmpty()) return all;
        Iterator<String> it = all.keySet().iterator();
        while (it.hasNext()) if (isDue(it.next(), true)) it.remove();
        return all;
    }

    /**
     * 是否包含某个key
     */
    public static boolean contains(@NonNull String key) {
        if (isDue(key, true)) return false;
        return getSharedPreference().contains(key);
    }

    /**
     * 删除
     */
    public static void remove(@NonNull String key) {
        getSharedPreference().edit().remove(key).apply();
        getSharedPreferenceExpire().edit().remove(key).apply();
    }

    /**
     * 清除所有
     */
    public static void clear() {
        getSharedPreference().edit().clear().apply();
        getSharedPreferenceExpire().edit().clear().apply();
    }

    /**
     * 注册监听某个key的值改变后回调
     */
    public static void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreference().registerOnSharedPreferenceChangeListener(listener);
        getSharedPreferenceExpire().registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 注销监听
     */
    public static void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreference().unregisterOnSharedPreferenceChangeListener(listener);
        getSharedPreferenceExpire().unregisterOnSharedPreferenceChangeListener(listener);
    }



    /**
     * 保存key对应的保存时间
     * @param expireMs 保存时间
     */
    protected static void putExpireTimeMs(@NonNull String key, @IntRange(from = ExpireNever) long expireMs) {
        if (expireMs <= ExpireNever) {
            getSharedPreferenceExpire().edit().remove(key).apply();
        } else getSharedPreferenceExpire().edit().putLong(key, System.currentTimeMillis() + expireMs).apply();
    }

    /**
     * 判断key对应的值是否过期
     * @param isDelIfDue if key对应的value过期, 是否删除value
     */
    protected static boolean isDue(@NonNull String key, boolean isDelIfDue) {
        long value = getSharedPreferenceExpire().getLong(key, 0);
        boolean isDue = value > 0 && value < System.currentTimeMillis();
        if (isDue && isDelIfDue) remove(key);
        return isDue;
    }
}
