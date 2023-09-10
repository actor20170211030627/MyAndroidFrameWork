package com.actor.map.baidu;

import android.annotation.SuppressLint;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.blankj.utilcode.util.Utils;

/**
 * description: 百度定位帮助类 <br />
 * <ol>
 *     <li>
 *         先去<a href="http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/key" target="_blank">申请AK密钥</a>,
 *         然后快速获取sha1签名, 可去<a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures"
 *         target="_blank">Gitee 下载一个 "获取debug和release签名的sha1.bat" 文件</a>, 双击运行它, 然后输入发布版(release版)秘钥地址就能获取到sha1签名。
 *     </li>
 *     <li>
 *         <a href="https://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio"
 *         target="_blank">添加依赖</a>, <a href="http://lbsyun.baidu.com/index.php?title=android-locsdk/geosdk-android-download"
 *         target="_blank">SDK, 示例代码, 类参文档</a> <br />
 *         <code>
 *             //项目gradle中添加仓库  <br />
 *             mavenCentral()         <br />
 *             <br />
 *             //以下依赖2选1, <a href="https://repo.maven.apache.org/maven2/com/baidu/lbsyun/" target="_blank">mavenCentral()最新版本</a> <br />
 *             //基础定位组件: 开发包体积最小，但只包含基础定位能力(卫星定位/WiFi/基站)、基础位置描述能力 <br />
 *             implementation 'com.baidu.lbsyun:BaiduMapSDK_Location:9.4.0' <br />
 *             //全量定位: 包含基础定位、离线定位、室内高精度定位能力，以及更多辅助功能(如地理围栏等) <br />
 *             implementation 'com.baidu.lbsyun:BaiduMapSDK_Location_All:9.4.0' <br />
 *         </code> <br />
 *     </li>
 *     <li>
 *         添加.so库文件以及适配平台,
 *         参考<a href="http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio"
 *         target="_blank">官网</a> <br />
 *         //在 defaultConfig { 节点中添加ndk <br />
 *         ndk { <br />
 *         &emsp;&emsp; //"armeabi-v7a", "arm64-v8a", "armeabi", "x86", "x86_64", "mips", "mips64" <br />
 *         &emsp;&emsp; abiFilters "armeabi-v7a", "arm64-v8a" <br />
 *         }
 *     </li>
 *     <li>
 *         1.添加<a href="http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio"
 *         target="_blank">&lt;meta-data</a>或调用 {@link BaiduLocationUtils#setKey(String) BaiduLocationUtils.setKey(String AK)}设置AK, (都可以,二选一) <br />
 *         在清单文件的&lt;application>标签里添加 &lt;meta-data 示例: <br />
 *         <code>
 *             &lt;!--百度定位设置AK，在Application标签中加入-->      <br />
 *             &lt;meta-data                                        <br />
 *             &emsp;&emsp; android:name="com.baidu.lbsapi.API_KEY" <br />
 *             &emsp;&emsp; android:value="您申请的百度地图AK"/&gt;   <br />
 *         </code>                                                   <br />
 *         2.清单文件中 ★★★不用★★★ 添加&lt;service, 只要你导入这个模块的依赖, 就已经添加好了! <br />
 *     </li>
 *     <br />
 *     <li>
 *         清单文件中 ★★★不用★★★ 添加
 *         <a href="http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio"
 *         target="_blank">定位所需权限</a>, 只要你导入这个模块的依赖, 就已经添加好了!
 *     </li>
 *     <li>
 *         <a href="https://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/obfuscation"
 *         target="_blank">代码混淆</a> ★★★已经在模块中完成★★★, 如果你需要混淆代码, 打开 minifyEnabled true 即可.
 *     </li>
 *     <li>
 *         使用示例: <br />
 *         {@link BaiduLocationUtils#setAgreePrivacy(boolean) BaiduLocationUtils.setAgreePrivacy(true);} //先同意隐私政策 <br />
 *         <br />
 *         //1.开始定位, listener 可直接使用or继承or参考: {@link BDLocationListener} <br />
 *         <code>
 *             {@link BaiduLocationUtils#registerLocationListener(BDAbstractLocationListener) BaiduLocationUtils.registerLocationListener(locationListener);} <br />
 *             {@link BaiduLocationUtils#start() BaiduLocationUtils.start();}   <br />
 *         </code>                                                              <br />
 *         //2.停止定位 or Activity/Fragment销毁后 <br />
 *         <code>
 *             {@link BaiduLocationUtils#unRegisterLocationListener(BDAbstractLocationListener) BaiduLocationUtils.unRegisterLocationListener(locationListener);} <br />
 *             {@link BaiduLocationUtils#stop() BaiduLocationUtils.stop();}
 *         </code>
 *     </li>
 * </ol> <br />
 *
 * 定位服务工具类, 借鉴自百度Demo LocationService.java
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

    /**
     * https://lbsyun.baidu.com/index.php?title=android-privacy
     * 百度Android定位SDK自v9.2.9版本起增加了隐私合规接口，使用方式发生了改变，与旧版本不兼容，
     * 请务必确保用户同意隐私政策后调用setAgreePrivacy接口以进行SDK初始化之前的准备工作。
     *
     * setAgreePrivacy接口需要在LocationClient实例化之前调用
     * 如果setAgreePrivacy接口参数设置为了false，则定位功能不会实现
     * @param agree true，表示用户同意隐私合规政策
     *              false，表示用户不同意隐私合规政策
     */
    public static void setAgreePrivacy(boolean agree) {
        LocationClient.setAgreePrivacy(agree);
    }

    /**
     * 设置AK, 如果已经在清单文件中添加了<meta-data , 就不用调用这个方法.
     * @param AK 在官网申请的AK
     */
    public static void setKey(String AK) {
        LocationClient.setKey(AK);
    }

    public static LocationClient getLocationClient() {
        if (locationClient == null) {
            try {
                //隐私政策, 如果用户没同意, 就会抛出异常
                locationClient = new LocationClient(Utils.getApp()/*, locationClientOption*/);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return locationClient;
    }

    /**
     * 注册定位监听
     */
    public static void registerLocationListener(@NonNull BDAbstractLocationListener listener) {
        registerLocationListener(null, listener);
    }

    /**
     * 注册定位监听
     * @param option 定位配置选项
     */
    public static void registerLocationListener(@Nullable LocationClientOption option, @NonNull BDAbstractLocationListener listener) {
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
    public static void unRegisterLocationListener(@NonNull BDAbstractLocationListener listener) {
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

    /**
     * @return 获取SDK版本
     */
    public static String getVersion() {
        return getLocationClient().getVersion();
    }
}
