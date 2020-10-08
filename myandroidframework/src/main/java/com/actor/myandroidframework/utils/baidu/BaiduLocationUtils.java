package com.actor.myandroidframework.utils.baidu;

import android.annotation.SuppressLint;
import android.os.Message;

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
 * 5.清单文件中添加 <meta-data 和 Service
 * http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio
 *  <!--百度定位服务-->
 *  <service
 *      android:name="com.baidu.location.f"
 *      android:enabled="true"
 *      tools:node="strict"
 *      android:process=":remote">
 *  </service>
 *
 *   <!--百度定位设置AK，在Application标签中加入-->
 *   <meta-data
 *       android:name="com.baidu.lbsapi.API_KEY"
 *       android:value="AK"/><!--value:开发者申请的AK-->
 *
 * 6.开始使用示例:
 * //开始定位
 * BaiduLocationUtils.registerListener(locationListener);//listener 可直接使用or继承or参考: {@link MyLocationListener}
 * BaiduLocationUtils.start();
 *
 * //停止定位 or Activity销毁后
 * BaiduLocationUtils.unregisterListener(locationListener);
 * BaiduLocationUtils.stop();
 *
 *
 * 定位服务工具类, 借鉴自百度Demo LocationService.java
 *
 *
 * @author baidu
 * @version 1.0
 */
public class BaiduLocationUtils {

    @SuppressLint("StaticFieldLeak")
    protected static LocationClient       locationClient;
    //定位配置选项
    protected static LocationClientOption clientOption;

    protected BaiduLocationUtils() {}

    public static LocationClient getLocationClient() {
        if (locationClient == null) locationClient = new LocationClient(ConfigUtils.APPLICATION);
        return locationClient;
    }

    /**
     * 注册定位监听
     */
    public static void registerListener(BDAbstractLocationListener listener) {
        registerListener(null, listener);
    }

    /**
     * 注册定位监听
     * @param option 定位配置选项
     */
    public static void registerListener(LocationClientOption option, BDAbstractLocationListener listener) {
        LocationClient client = getLocationClient();
        client.setLocOption(option == null ? getDefaultLocationClientOption() : option);
        /**
         * listener会添加进List: {@link LocationClient#f(Message)}
         */
        client.registerLocationListener(listener);
    }

    /**
     * 注销定位监听
     */
    public static void unregisterListener(BDAbstractLocationListener listener) {
        getLocationClient().unRegisterLocationListener(listener);
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

    /**
     * @return 是否已经开始定位
     */
    public static boolean isStarted() {
        return getLocationClient().isStarted();
    }

    /**
     * 获取定位配置
     */
    public static LocationClientOption getLocOption() {
        return getLocationClient().getLocOption();
    }

    /**
     * @return 默认定位配置
     */
    public static LocationClientOption getDefaultLocationClientOption() {
        if (clientOption == null) {
            clientOption = new LocationClientOption();
            clientOption.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式，高精度，低功耗，仅设备，默认高精度
            clientOption.setCoorType("bd09ll");//设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll，默认gcj02
            clientOption.setScanSpan(2000);//定位间隔, 默认0，即仅定位一次，设置需要大于等于1000ms才有效
            clientOption.setIsNeedAddress(true);//是否需要地址信息，默认false
            clientOption.setIsNeedLocationDescribe(true);//可选,默认false.设置是否需要位置语义化结果(地址描述),
            // 可以在BDLocation.getLocationDescribe里得到,结果类似于“在北京天安门附近”
            clientOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            clientOption.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
            clientOption.setIgnoreKillProcess(false);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            clientOption.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation
            // .getPoiList里得到
            clientOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            clientOption.setOpenGps(true);//可选，默认false,设置是否开启Gps定位
            clientOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

            //defaultOption.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
            clientOption.setWifiCacheTimeOut(1 * 60 * 1000);
            //可选，SDK7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位
        }
        return clientOption;
    }

    /**
     * 触发请求当前连接wifi是否是移动热点的状态
     * @return
     */
    public static boolean requestHotSpotState() {
        return getLocationClient().requestHotSpotState();
    }
}
