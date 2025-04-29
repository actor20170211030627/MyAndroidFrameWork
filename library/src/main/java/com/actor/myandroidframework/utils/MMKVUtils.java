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

    /**
     * 初始化 (使用者可不用调用)
     * @return 存储目录: String rootDir = getFilesDir().getAbsolutePath() + "/mmkv";
     */
    public static String initialize() {
        return MMKV.initialize(ConfigUtils.APPLICATION);
    }

    public static MMKV getMMKV() {
        if (mmkv == null) {
            if (MMKV.getRootDir() == null) initialize();
            mmkv = MMKV.defaultMMKV();
        }
        return mmkv;
    }

    /**
     * 自定义 MMKV (要先{@link #initialize()} 初始化)
     * @param mmkv 例:
     * <ol>
     *     <li>{@link MMKV#mmkvWithID(String) MMKV.mmkvWithID(String mmapID)}</li>
     *     <li>{@link MMKV#mmkvWithID(String, int) MMKV.mmkvWithID(String mmapID, int mode)}</li>
     *     <li>{@link MMKV#mmkvWithID(String, int, String) MMKV.mmkvWithID(String mmapID, int mode, @Nullable String cryptKey)}</li>
     *     <li>{@link MMKV#mmkvWithID(String, int, long) MMKV.mmkvWithID(String mmapID, int mode, long expectedCapacity)}</li>
     *     <li>{@link MMKV#mmkvWithID(String, int, String, String) MMKV.mmkvWithID(String mmapID, int mode, @Nullable String cryptKey, String rootPath)}</li>
     *     <li>{@link MMKV#mmkvWithID(String, int, String, String, long) MMKV.mmkvWithID(String mmapID, int mode, @Nullable String cryptKey, String rootPath, long expectedCapacity)}</li>
     *     <li>{@link MMKV#mmkvWithID(String, String) MMKV.mmkvWithID(String mmapID, String rootPath)}</li>
     *     <li>{@link MMKV#mmkvWithID(String, String, long) MMKV.mmkvWithID(String mmapID, String rootPath, long expectedCapacity)}</li>
     *     <li>
     *         参数说明:
     *         <ul>
     *             <li>mmapID: 根据给定的ID获取MMKV实例。可以使用不同的ID创建多个MMKV实例，每个实例都有独立的数据存储。</li>
     *             <li>mode: {@link MMKV#SINGLE_PROCESS_MODE}: 单进程模式. {@link MMKV#MULTI_PROCESS_MODE}: 多进程模式.</li>
     *             <li>cryptKey: if你要加密存储, 就设置加密的key</li>
     *             <li>expectedCapacity: 预期容量</li>
     *             <li>rootPath: </li>
     *         </ul>
     *     </li>
     * </ol>
     */
    public static void setMMKV(@Nullable MMKV mmkv) {
        MMKVUtils.mmkv = mmkv;
    }


    /**
     * 重设秘钥
     * @param cryptKey 新的秘钥, if cryptKey = null, 相当于改成明文存储
     */
    public static boolean reKey(@NonNull MMKV mmkv, @Nullable String cryptKey) {
        return mmkv.reKey(cryptKey);
    }

    /**
     * 返回加密的秘钥
     */
    @Nullable
    public static String cryptKey(@NonNull MMKV mmkv) {
        return mmkv.cryptKey();
    }



    ///////////////////////////////////////////////////////////////////////////
    /// put & get
    ///////////////////////////////////////////////////////////////////////////
    public static boolean putBoolean(String key, boolean value) {
        return putBoolean(key, value, MMKV.ExpireNever);
    }
    /**
     * put boolean
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putBoolean(String key, boolean value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
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



    public static boolean putInt(String key, int value) {
        return putInt(key, value, MMKV.ExpireNever);
    }
    /**
     * put int
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putInt(String key, int value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
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



    public static boolean putLong(String key, long value) {
        return putLong(key, value, MMKV.ExpireNever);
    }
    /**
     * put long
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putLong(String key, long value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
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



    public static boolean putFloat(String key, float value) {
        return putFloat(key, value, MMKV.ExpireNever);
    }
    /**
     * put float
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putFloat(String key, float value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
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



    public static boolean putDouble(String key, double value) {
        return putDouble(key, value, MMKV.ExpireNever);
    }
    /**
     * put double
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putDouble(String key, double value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
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



    public static boolean putString(String key, @Nullable String value) {
        return putString(key, value, MMKV.ExpireNever);
    }
    /**
     * put String
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putString(String key, @Nullable String value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
    }

    @Nullable
    public static String getString(String key) {
        return getMMKV().decodeString(key);
    }

    public static String getString(String key, String defaultValue) {
        return getMMKV().getString(key, defaultValue);
    }



    public static boolean putStringSet(String key, @Nullable Set<String> value) {
        return putStringSet(key, value, MMKV.ExpireNever);
    }
    /**
     * put Set<String>
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putStringSet(String key, @Nullable Set<String> value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
    }
    /**
     * 获取Set<String>, 默认返回HashSet类型
     */
    @Nullable
    public static Set<String> getStringSet(String key) {
        return getMMKV().decodeStringSet(key);
    }

    public static boolean putBytes(String key,  @Nullable byte[] value) {
        return putBytes(key, value, MMKV.ExpireNever);
    }
    /**
     * put byte[]
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putBytes(String key,  @Nullable byte[] value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
    }

    @Nullable
    public static byte[] getBytes(String key) {
        return getMMKV().decodeBytes(key);
    }

    public static boolean putParcelable(String key, @Nullable Parcelable value) {
        return putParcelable(key, value, MMKV.ExpireNever);
    }
    /**
     * put Parcelable
     * @param expireDurationInSecond 过期时间, 单位秒
     */
    public static boolean putParcelable(String key, @Nullable Parcelable value, int expireDurationInSecond) {
        return getMMKV().encode(key, value, expireDurationInSecond);
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(String key, Class<T> tClass) {
        return getMMKV().decodeParcelable(key, tClass);
    }



    /**
     * 移除某个键所对应的值
     */
    public static void remove(@NonNull String key) {
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
     * 清除所有
     */
    public static void clearAll() {
        getMMKV().clearAll();
    }

    /**
     * 从 SharedPreferences 导入
     */
    public static int importFromSharedPreferences(@NonNull SharedPreferences preferences) {
        return getMMKV().importFromSharedPreferences(preferences);
    }

    /**
     * 备份数据到某个目录
     * @param mmapID 根据给定的ID获取MMKV实例。可以使用不同的ID创建多个MMKV实例，每个实例都有独立的数据存储。
     * @param dstDir 备份到哪个目录, 例: getFilesDir().getAbsolutePath() + "/mmkv_backup";
     */
    public static boolean backup(@NonNull String mmapID, @NonNull String dstDir) {
        return MMKV.backupOneToDirectory(mmapID, dstDir, null);
    }

    /**
     * 恢复某个目录的数据到指定目录
     * @param mmapID 根据给定的ID获取MMKV实例。可以使用不同的ID创建多个MMKV实例，每个实例都有独立的数据存储。
     * @param srcDir 数据所在目录, 例: getFilesDir().getAbsolutePath() + "/mmkv_backup";
     * @param restorePath 需要恢复到哪个目录, 例: getFilesDir().getAbsolutePath() + "/mmkv";
     */
    public static boolean restore(@NonNull String mmapID, @NonNull String srcDir, @Nullable String restorePath) {
        return MMKV.restoreOneMMKVFromDirectory(mmapID, srcDir, restorePath);
    }

    /**
     * 备份所有数据到某个目录
     * @param dstDir 备份到哪个目录, 例: getFilesDir().getAbsolutePath() + "/mmkv_backup";
     * @return ??
     */
    public static long backupAll(@NonNull String dstDir) {
        return MMKV.backupAllToDirectory(dstDir);
    }

    /**
     * 恢复某个目录的所有数据
     * @param srcDir 数据所在目录, 例: getFilesDir().getAbsolutePath() + "/mmkv_backup";
     * @return ??
     */
    public static long restoreAll(@NonNull String srcDir) {
        return MMKV.restoreAllFromDirectory(srcDir);
    }

    /**
     * 全局过期. 给整个文件设定统一的过期间隔。可自定义
     * @param expireDurationInSecond 过期时间, 单位秒
     * @see MMKV#ExpireNever 0                          永不过期
     * @see MMKV#ExpireInMinute 60                      1分钟
     * @see MMKV#ExpireInHour 60 * 60                   1分钟
     * @see MMKV#ExpireInDay 24 * 60 * 60               1天
     * @see MMKV#ExpireInMonth 30 * 24 * 60 * 60        1个月
     * @see MMKV#ExpireInYear 24 * 30 * 24 * 60 * 60    1年
     */
    public static boolean enableAutoKeyExpire(int expireDurationInSecond) {
        return getMMKV().enableAutoKeyExpire(expireDurationInSecond);
    }

    /**
     * ?
     */
    public static long count() {
        return getMMKV().count();
    }
}
