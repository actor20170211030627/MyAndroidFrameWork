package com.actor.myandroidframework.utils.baidu;

import android.support.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * description: 百度定位帮助类
 * 1.申请密钥
 * http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/key
 * 快速获取sha1签名去下载一个 "获取debug和release签名的sha1.bat" 文件, 双击运行, 然后输入发布版秘钥地址就行:
 * https://github.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures
 *
 * 2.配置环境
 *  ①.在相关下载里下载最新的库文件,SDK,示例代码,类参考:
 *    http://lbsyun.baidu.com/index.php?title=android-locsdk/geosdk-android-download
 *  ②.如果↑下载的sdk和目前这个 BaiduMapUtils 的api不适配, 请下载 BaiduMapUtils 正在使用的
 *    BaiduLBS_aar_android_Vxxx.aar:
 *     有2个.aar, 只下载一个.aar即可:
 *       比较小的一个只有 "全量定位" 功能
 *       比较大的一个有 "全量定位" & "基础地图(含室内地图)" & "检索功能" 功能
 *   https://github.com/actor20170211030627/MyAndroidFrameWork/tree/master/app/libs
 *   或 https://gitee.com/actor20170211030627/MyAndroidFrameWork/tree/master/app/libs
 *
 * 3.添加.so库文件以及适配平台
 * http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio
 * //在 defaultConfig { 中添加ndk
 * ndk {
 *     //"armeabi", "armeabi-v7a", "x86","arm64-v8a","x86_64", 'mips', 'mips64'
 *     abiFilters "armeabi"
 * }
 *
 * 4.清单文件中添加定位所需权限
 * http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio
 * <!--下方是百度定位权限-->
 * <!-- 这个权限用于进行网络定位-->
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * <!-- 这个权限用于访问GPS定位-->
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * <!-- 用于访问wifi网络信息，wifi信息会用于进行网络定位-->
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <!-- 获取运营商信息，用于支持提供运营商信息相关的接口-->
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <!-- 这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据-->
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <!-- 访问网络，网络定位需要上网-->
 * <uses-permission android:name="android.permission.INTERNET" />
 *
 * 5.清单文件中添加<meta-data
 * http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio
 *   <!--百度定位设置AK，在Application标签中加入-->
 *   <meta-data
 *       android:name="com.baidu.lbsapi.API_KEY"
 *       android:value="AK"/><!--value:开发者申请的AK-->
 *
 * 6.Application中初始化
 * //百度定位配置
 * BaiduLocationUtils.setLocOption(BaiduLocationUtils.getDefaultLocationClientOption());
 *
 * 7.开始使用示例:
 * //开始定位
 * BaiduLocationUtils.registerListener(locationListener);//listener 可直接使用or继承or参考: {@link MyLocationListener}
 * BaiduLocationUtils.start();
 *
 * //停止定位 or Activity销毁后
 * BaiduLocationUtils.unregisterListener(locationListener);
 * BaiduLocationUtils.stop();
 *
 *
 * 定位服务工具类, 抄自百度Demo LocationService.java
 *
 *
 * @author baidu
 * @version 1.0
 */
public class BaiduLocationUtils {

    protected static LocationClient       locationClient;
    //默认定位配置/自定义定位配置
    protected static LocationClientOption defaultOption, DIYoption;

    protected BaiduLocationUtils() {}

    public static LocationClient getLocationClient() {
        synchronized (BaiduLocationUtils.class) {
            if (locationClient == null) {
                locationClient = new LocationClient(ConfigUtils.APPLICATION);
            }
        }
        return locationClient;
    }

    /**
     * 设置定位配置, 可在Application中统一初始化, 也可在另外的地方设置自定义的配置
     * @param option
     */
    public static void setLocOption(LocationClientOption option) {
        if (option != null) {
            if (isStarted()) getLocationClient().stop();
            DIYoption = option;
            getLocationClient().setLocOption(option);
        }
    }

    /**
     * 获取定位配置
     */
    public static LocationClientOption getLocOption() {
        return getLocationClient().getLocOption();
    }

    /**
     * 注册定位监听
     *
     * @param listener
     * @return
     */
    public static void registerListener(BDAbstractLocationListener listener) {
        if (listener != null) {
            getLocationClient().registerLocationListener(listener);
        }
    }

    /**
     * 注销定位监听
     *
     * @param listener
     */
    public static void unregisterListener(BDAbstractLocationListener listener) {
        if (listener != null) {
            getLocationClient().unRegisterLocationListener(listener);
        }
    }

    /**
     * 开始定位
     */
    public static void start() {
        synchronized (BaiduLocationUtils.class) {
            if (!isStarted()) {
                getLocationClient().start();
            }
        }
    }

    /**
     * 结束定位
     */
    public static void stop() {
        synchronized (BaiduLocationUtils.class) {
            if (isStarted()) {
                getLocationClient().stop();
            }
        }
    }

    public static boolean isStarted() {
        return getLocationClient().isStarted();
    }

    /**
     * @return DefaultLocationClientOption  默认O设置
     */
    public static LocationClientOption getDefaultLocationClientOption() {
        if (defaultOption == null) {
            defaultOption = new LocationClientOption();
            defaultOption.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            defaultOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            defaultOption.setScanSpan(65000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            defaultOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            defaultOption.setIsNeedLocationDescribe(true);//可选,默认false.设置是否需要位置语义化结果(地址描述),
            // 可以在BDLocation.getLocationDescribe里得到,结果类似于“在北京天安门附近”
            defaultOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            defaultOption.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
            defaultOption.setIgnoreKillProcess(false);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            defaultOption.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation
            // .getPoiList里得到
            defaultOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            defaultOption.setOpenGps(true);//可选，默认false,设置是否开启Gps定位
            defaultOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

            //defaultOption.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
            defaultOption.setWifiCacheTimeOut(1 * 60 * 1000);
            //可选，SDK7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位
        }
        return defaultOption;
    }

    /**
     * @return DIYOption 自定义Option设置
     */
    public static @Nullable LocationClientOption getDIYOption() {
        return DIYoption;
    }

    public static boolean requestHotSpotState() {
        return getLocationClient().requestHotSpotState();
    }
}
