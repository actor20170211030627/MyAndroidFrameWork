package com.actor.myandroidframework.utils.gaode;

import android.annotation.SuppressLint;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * description: 高德定位
 *
 * 1-5.见{@link GaoDeMapUtils}
 *
 * 6.定位服务(注意: 已经在 MyAndroidFrameWork 中添加这个服务, 不需要再添加!) https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation#androidmanifest
 *   <!-- 高德定位需要的服务 使用2.0的定位需要加上这个 -->
 *   <service android:name="com.amap.api.location.APSService" />
 *
 * 7.添加权限
 *  <!--高德定位-->
 *  <!--用于进行网络定位-->
 *  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 *  <!--用于访问GPS定位-->
 *  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *  <!--获取运营商信息，用于支持提供运营商信息相关的接口-->
 *  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *  <!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
 *  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *  <!--这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
 *  <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 *  <!--用于访问网络，网络定位需要上网-->
 *  <uses-permission android:name="android.permission.INTERNET" />
 *  <!--用于读取手机当前的状态-->
 *  <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 *  <!--写入扩展存储，向扩展卡写入数据，用于写入缓存定位数据-->
 *  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *  <!--用于申请调用A-GPS模块-->
 *  <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
 *
 *  ★★★下方权限如果用不到, 就不用申请★★★
 *  <!--用于申请获取蓝牙信息进行室内定位-->
 *  <uses-permission android:name="android.permission.BLUETOOTH" />
 *  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
 *
 *  8.添加服务
 *  <!-- 高德定位需要的服务 使用2.0+的定位需要加上这个 -->
 *  <service android:name="com.amap.api.location.APSService" />
 *
 * 9.开始使用示例:
 *  GaoDeLocationUtils.setLocationListener(locationListener);
 *  GaoDeLocationUtils.startLocation();
 *
 *  //注销定位监听(Activity/Fragment onDestroy前一定要注销掉)
 *  GaoDeLocationUtils.unRegisterLocationListener(locationListener);
 *
 *  //'全局'停止定位
 *  GaoDeLocationUtils.stopLocation();
 *
 * @author : 李大发
 * date       : 2020/8/10 on 10:09
 * @version 1.0
 */
public class GaoDeLocationUtils {

    @SuppressLint("StaticFieldLeak")
    protected static AMapLocationClient       locationClient;
    //定位参数
    protected static AMapLocationClientOption clientOption;

    protected GaoDeLocationUtils() {
    }

    public static AMapLocationClient getLocationClient() {
        if (locationClient == null) {
            locationClient = new AMapLocationClient(ConfigUtils.APPLICATION);
        }
        return locationClient;
    }

    /**
     * 设置定位监听
     */
    public static void setLocationListener(AMapLocationListener listener) {
        setLocationListener(null, listener);
    }

    /**
     * 设置定位监听
     * @param option 自定义定位选项
     * @param listener 监听, 示例处理结果:
     * if (amapLocation != null) {
     *     if (amapLocation.getErrorCode() == 0) {//定位成功
     *         amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
     *         amapLocation.getLatitude();//获取纬度
     *         amapLocation.getLongitude();//获取经度
     *         amapLocation.getAccuracy();//获取精度信息
     *         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     *         df.format(new Date(amapLocation.getTime()));//定位时间
     *
     *         //如果你只需要定位一次, 需要在这儿注销掉监听, 否则可能会一直定时回调
     *         GaoDeLocationUtils.unRegisterLocationListener(locationListener);
     *     } else {
     *         logFormat("高德定位出错: ErrCode=%d, ErrorInfo=%s", amapLocation.getErrorCode(), amapLocation.getErrorInfo());
     *     }
     * }
     */
    public static void setLocationListener(AMapLocationClientOption option, AMapLocationListener listener) {
        AMapLocationClient client = getLocationClient();
        //设置定位参数
        client.setLocationOption(option == null ? getDefaultLocationClientOption() : option);
        client.setLocationListener(listener);
    }

    /**
     * 注销定位监听
     */
    public static void unRegisterLocationListener(AMapLocationListener listener) {
        getLocationClient().unRegisterLocationListener(listener);
    }

    /**
     * 开始定位
     * 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
     */
    public static void startLocation() {
        getLocationClient().startLocation();
    }

    /**
     * 停止定位
     */
    public static void stopLocation() {
        getLocationClient().stopLocation();
    }

    /**
     * 如果 AMapLocationClient 是Activity初始化的, 那么需要调用 onDestroy 方法
     * @deprecated 本工具类是用的 Application 初始化, 所以不用调这个方法
     */
    @Deprecated
    public static void onDestroy() {
        if (locationClient != null) locationClient.onDestroy();
    }

    /**
     * 获取默认定位参数
     */
    public static AMapLocationClientOption getDefaultLocationClientOption() {
        if (clientOption == null) {
            clientOption = new AMapLocationClientOption();
            //定位模式: Hight_Accuracy,高精度模式. Battery_Saving,低功耗模式. Device_Sensors,仅设备模式
            clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            clientOption.setGpsFirst(false);//是否gps优先，只在高精度模式下有效。默认关闭
            clientOption.setHttpTimeOut(30_000L);//网络请求超时时间。默认为30秒。在仅设备模式下无效
            clientOption.setInterval(2000);//定位间隔, 单位毫秒, 最小800ms, 默认2000ms
            clientOption.setNeedAddress(true);//是否返回逆地理地址信息。默认是true
            clientOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
            clientOption.setOnceLocationLatest(false);//是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
            AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
            clientOption.setSensorEnable(false);//是否使用传感器。默认是false
            clientOption.setWifiScan(true); //是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
            clientOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        }
        return clientOption;
    }
}
