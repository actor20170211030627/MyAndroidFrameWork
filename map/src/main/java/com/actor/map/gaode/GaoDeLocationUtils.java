package com.actor.map.gaode;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * description: <a href="https://lbs.amap.com/api/android-location-sdk/locationsummary/" target="_blank">高德定位</a> <br />
 * <ul>
 *     <li>1-3,5,6. 见 {@link GaoDe3DMapUtils}</li>
 *     <li>
 *         4.在AndroidManifest.xml中添加
 *         <a href="https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention"
 *         target="_blank">&lt;meta-data</a>或调用 {@link GaoDeLocationUtils#setApiKey(String) GaoDeLocationUtils.setApiKey(String)}设置AK, (都可以,二选一) <br />
 *         在清单文件的&lt;application>标签里添加 &lt;meta-data 示例:  <br />
 *         <code>
 *             &lt;!--高德地图/定位设置AK，在&lt;pplication>标签中加入-->  <br />
 *             &lt;meta-data                                            <br />
 *             &emsp;&emsp; android:name="com.amap.api.v2.apikey"       <br />
 *             &emsp;&emsp; android:value="您申请的高德Api Key"/&gt;
 *         </code>
 *     </li>
 *     <li>
 *         7.清单文件中 ★★★不用★★★ 添加&lt;service, 只要你导入这个模块的依赖, 就已经添加好了!
 *     </li>
 *     <li>
 *         8.清单文件中 ★★★不用★★★
 *         <a href="https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation"
 *         target="_blank">添加权限</a>, 只要你导入这个模块的依赖, 就已经添加好了!
 *     </li>
 *     <li>
 *         9.使用前, 先初始化: <br />
 *         {@link GaoDeLocationUtils#updatePrivacyShow(Context, boolean, boolean) GaoDeLocationUtils.updatePrivacyShow(Context, boolean, boolean)} <br />
 *         {@link GaoDeLocationUtils#updatePrivacyAgree(Context, boolean) GaoDeLocationUtils.updatePrivacyAgree(Context, boolean)}
 *     </li>
 *     <li>
 *         10.开始使用示例: <br />
 *          {@link GaoDeLocationUtils#setLocationListener(AMapLocationListener) GaoDeLocationUtils.setLocationListener(AMapLocationListener)} <br />
 *          {@link GaoDeLocationUtils#startLocation() GaoDeLocationUtils.startLocation()} <br />
 *          <br />
 *          //注销定位监听(Activity/Fragment onDestroy前一定要注销掉) <br />
 *          {@link GaoDeLocationUtils#unRegisterLocationListener(AMapLocationListener) GaoDeLocationUtils.unRegisterLocationListener(AMapLocationListener)} <br />
 *          <br />
 *          //'全局'停止定位 <br />
 *          {@link GaoDeLocationUtils#stopLocation() GaoDeLocationUtils.stopLocation()}
 *     </li>
 * </ul> <br />
 *
 * @author : ldf
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

    /**
     * 更新隐私合规状态,需要在初始化地图之前完成，如果合规不通过地图将白屏
     * @param  isContains: 隐私权政策是否包含高德开平隐私权政策  true是包含
     * @param  isShow: 隐私权政策是否弹窗展示告知用户 true是展示
     * @since  8.1.0
     */
    public static void updatePrivacyShow(Context context, boolean isContains, boolean isShow) {
        AMapLocationClient.updatePrivacyShow(context, isContains, isShow);
    }

    /**
     * 更新同意隐私状态,需要在初始化地图之前完成，如果合规不通过地图将白屏
     * @param context: 上下文
     * @param isAgree: 隐私权政策是否取得用户同意  true是用户同意
     * @since 8.1.0
     */
    public static void updatePrivacyAgree(Context context, boolean isAgree) {
        AMapLocationClient.updatePrivacyAgree(context, isAgree);
    }

    /**
     * 设置apiKey, 如果已经在清单文件中添加了<meta-data , 就不用调用这个方法.
     * @param apiKey 在官网申请的apiKey
     */
    public static void setApiKey(String apiKey) {
        AMapLocationClient.setApiKey(apiKey);
    }

    public static AMapLocationClient getLocationClient() {
        if (locationClient == null) {
            try {
                locationClient = new AMapLocationClient(ConfigUtils.APPLICATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return locationClient;
    }

    /**
     * 设置定位监听
     */
    public static void setLocationListener(@NonNull AMapLocationListener listener) {
        setLocationListener(null, listener);
    }

    /**
     * 设置定位监听
     * @param option 自定义定位选项
     * @param listener 监听, 示例处理结果:
     * if (amapLocation == null) {
     *     return;
     * }
     * switch (amapLocation.getErrorCode()) {
     *     case 0://定位成功
     *         amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
     *         amapLocation.getLatitude();//获取纬度
     *         amapLocation.getLongitude();//获取经度
     *         amapLocation.getAccuracy();//获取精度信息
     *         new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(amapLocation.getTime()));//定位时间
     *
     *         //如果你只需要定位一次, 需要在这儿注销掉监听, 否则可能会一直定时回调
     *         GaoDeLocationUtils.unRegisterLocationListener(locationListener);
     *         break;
     *     case 12://缺少定位权限,请在设备的设置中开启app的定位权限(弹出下拉通知栏,并打开定位开关)
     *         showLocationNotOpenDialog();
     *         break;
     *     default:
     *         LogUtils.errorFormat("高德定位出错: ErrCode=%d, ErrorInfo=%s", amapLocation.getErrorCode(), amapLocation.getErrorInfo());
     *         break;
     * }
     */
    public static void setLocationListener(@Nullable AMapLocationClientOption option, @NonNull AMapLocationListener listener) {
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
