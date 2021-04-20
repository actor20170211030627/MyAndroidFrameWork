package com.actor.myandroidframework.utils.gaode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IntRange;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.IndoorBuildingInfo;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

/**
 * 高德地图开放平台: https://lbs.amap.com/
 * 1.添加应用并获取API Key: https://console.amap.com/dev
 *   快速获取sha1签名去下载一个 "获取debug和release签名的sha1.bat" 文件, 双击运行, 然后输入发布版秘钥地址就行:
 *   https://gitee.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures
 *
 * 2.添加依赖
 *   //https://lbs.amap.com/api/android-sdk/guide/create-project/android-studio-create-project#gradle_sdk
 *   2.1.在Project的gradle文件中添加:
 *       allprojects {
 *          repositories {
 *              jcenter() // 或者 mavenCentral()
 *          }
 *       }
 *
 *   2.2.在module的gradle文件中添加:
 *     //在 defaultConfig { 中添加ndk
 *       ndk {
 *           //"armeabi", "armeabi-v7a", "x86","arm64-v8a","x86_64", 'mips', 'mips64'
 *           abiFilters "armeabi"
 *       }
 *
 *     //引入最新版本的SDK
 *       implementation 'com.amap.api:3dmap:latest.integration'//3D地图
 *       implementation 'com.amap.api:map2d:latest.integration'//2D地图
 *       implementation 'com.amap.api:navi-3dmap:latest.integration'//导航(5.0.0以后版本包含了3D地图SDK,就不用导入3dmap)
 *       implementation 'com.amap.api:search:latest.integration'//搜索
 *       implementation 'com.amap.api:location:latest.integration'//定位
 *
 *     //或者指定版本(目前用的下方版本)(最新版本: https://bintray.com/search?query=com.amap.api [jcenter仓库])
 *       implementation 'com.amap.api:3dmap:7.5.0'//3D地图
 *       implementation 'com.amap.api:map2d:6.0.0'//2D地图
 *       implementation 'com.amap.api:navi-3dmap:7.5.0_3dmap7.5.0'//导航(5.0.0以后版本包含了3D地图SDK,就不用导入3dmap)
 *       implementation 'com.amap.api:search:7.4.0'//搜索
 *       implementation 'com.amap.api:location:5.1.0'//定位
 *
 * 3.在AndroidManifest.xml中添加 https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention
 * <meta-data
 *     android:name="com.amap.api.v2.apikey"
 *     android:value="您的Api Key"/>
 *
 * 4.添加权限(因为添加的依赖是.jar, 不含清单文件) https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention#permission
 *   <!--高德地图包、搜索包需要的基础权限-->
 *   <!--允许程序打开网络套接字-->
 *   <uses-permission android:name="android.permission.INTERNET" />
 *   <!--允许程序设置内置sd卡的写权限-->
 *   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *   <!--允许程序获取网络状态-->
 *   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *   <!--允许程序访问WiFi网络信息-->
 *   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *   <!--允许程序读写手机状态和身份-->
 *   <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 *   <!--允许程序访问CellID或WiFi热点来获取粗略的位置-->
 *   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 *
 * 5.代码混淆 https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention#obfuscated-code
 *   已在 MyAndroidFrameWork 中添加混淆, 如果你需要混淆代码, 打开 minifyEnabled true 即可
 *
 *
 * 6.常用地图容器
 * @see com.amap.api.maps.MapView
 *  @see MapView#onCreate(Bundle) //在activity执行onCreate时执行，创建地图
 *  @see MapView#onResume() //在activity执行onResume时执行，重新绘制加载地图
 *  @see MapView#onPause() //在activity执行onPause时执行，暂停地图的绘制
 *  @see MapView#onSaveInstanceState(Bundle) ////在activity执行onSaveInstanceState时执行，保存地图当前的状态
 *  @see MapView#onDestroy() //在activity执行onDestroy时执行，销毁地图
 *
 * @see com.autonavi.amap.mapcore.interfaces.IAMap
 *
 *
 * GLSurfaceView:
 *  @see com.amap.api.maps.MapFragment extends android.app.Fragment
 *  @see com.amap.api.maps.SupportMapFragment extends android.support.v4.app.Fragment
 *
 * TextureView, 使用场景:
 *  您将MapView与其他的GLSurfaceView（比如相机）叠加展示，或者是在ScrollView中加载地图时，
 *  建议使用TextureMapView及SupportTextureMapFragment来展示地图，
 *  可以有效解决 GLSurfaceView 叠加时出现的穿透、滚动黑屏等问题。
 *  @see com.amap.api.maps.TextureMapView
 *  @see com.amap.api.maps.TextureMapFragment extends Fragment
 *  @see com.amap.api.maps.TextureSupportMapFragment extends android.support.v4.app.Fragment
 *
 *
 * @author : 李大发
 * date       : 2020/8/5 on 16:39
 * @version 1.0
 */
public class GaoDeMapUtils {

    //获取版本名称
    public static String getVersion() {
        return MapsInitializer.getVersion();
    }

    /**
     * https://lbs.amap.com/api/android-sdk/guide/create-map/show-map
     * 获取地图控制器对象
     */
    public static AMap getAmap(MapView mapView) {
        return mapView.getMap();
    }

    public static AMap getAmap(TextureMapView mapView) {
        return mapView.getMap();
    }

    /**
     *
     * 显示"定位蓝点"
     * @param interval 设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。2000L
     * @param locationEnable 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
     */
    public static void setMyLocationEnabled(AMap aMap, long interval, boolean locationEnable) {
        MyLocationStyle locationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        locationStyle.interval(interval);
        aMap.setMyLocationStyle(locationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(locationEnable);
    }

    /**
     * @see AMap#MAP_TYPE_NORMAL    矢量地图模式
     * @see AMap#MAP_TYPE_SATELLITE 卫星地图模式
     * @see AMap#MAP_TYPE_NIGHT     夜景地图模式
     * @see AMap#MAP_TYPE_NAVI      导航地图模式
     * @see AMap#MAP_TYPE_BUS       ?
     */
    public static void setMapType(AMap aMap, int mapType) {
        aMap.setMapType(mapType);
    }

    //清空 element
    public static void clear(AMap aMap) {
        aMap.clear();
    }

    //个性化地图, 设置自定义样式
    public static void setCustomMapStyle(AMap aMap, byte[] bytes, boolean enable) {
        CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();
        mapStyleOptions.setStyleData(bytes);
        mapStyleOptions.setEnable(enable);
//        mapStyleOptions.setStyleId("your id");
        aMap.setCustomMapStyle(mapStyleOptions);
    }

    /**
     * 添加覆盖物
     * @param latLng 坐标
     * @param descriptor 获取方式:
     *                   @see BitmapDescriptorFactory#defaultMarker()
     *                   @see BitmapDescriptorFactory#defaultMarker(float)
     *                   @see BitmapDescriptorFactory#fromAsset(String)
     *                   @see BitmapDescriptorFactory#fromBitmap(Bitmap)
     *                   @see BitmapDescriptorFactory#fromFile(String)
     *                   @see BitmapDescriptorFactory#fromPath(String)
     *                   @see BitmapDescriptorFactory#fromResource(int) //R.drawable.xxx
     *                   @see BitmapDescriptorFactory#fromView(View)
     */
    public static Marker addMarker(AMap aMap, LatLng latLng, BitmapDescriptor descriptor) {
        return aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(descriptor));
    }

    /**
     * 添加线
     * @param aMap
     * @param latLngs
     * @param width 线宽? 例: 5
     */
    public static Polyline addPolyline(AMap aMap, Iterable<LatLng> latLngs, float width) {
        return aMap.addPolyline((new PolylineOptions())
                .addAll(latLngs)
                .width(width));
    }

    /**
     * 移动到以某点为中心
     * @param scaleLevel 缩放级别 3 ~ 20
     *  @see AMap#getMaxZoomLevel()
     *  @see AMap#getMinZoomLevel()
     */
    public static void moveCamera(AMap aMap, LatLng latLng, @IntRange(from = 10, to = 18) int scaleLevel) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, scaleLevel));
        //aMap.animateCamera
    }

    /**
     * 获取最小缩放级别, 如果没有手动设置, 默认3
     */
    public static float getMinZoomLevel(AMap aMap) {
        return aMap.getMinZoomLevel();
    }

    /**
     * 获取最大缩放级别, 如果没有手动设置, 默认20
     */
    public static float getMaxZoomLevel(AMap aMap) {
        return aMap.getMaxZoomLevel();
    }

    /**
     * 获取当前缩放级别
     */
    public static float getCurrentZoomLevel(AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
//        LatLng target = cameraPosition.target;
        return cameraPosition.zoom;
    }

    /**
     * 设置当前缩放级别
     */
    public static void setCurrentZoomLevel(AMap aMap, float level) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        LatLng target = cameraPosition.target;
//        float zoom = cameraPosition.zoom;
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, level));
//        aMap.moveCamera();
    }

    /**
     * "室内地图"是否显示, 默认false
     * @see GaoDeUiSettingUtils#setIndoorSwitchEnabled(AMap, boolean)
     * @param aMap
     * @param enable
     */
    public static void showIndoorMap(AMap aMap, boolean enable) {
        aMap.showIndoorMap(enable);
    }

    /**
     * 设置室内地图回调监听, 室内地图有可能有很多层的地图
     * 回调类: {@link com.amap.api.maps.model.IndoorBuildingInfo}
     *  @see com.amap.api.maps.model.IndoorBuildingInfo#poiid   楼所在id, 通过这个判断是否是同一个楼
     *  @see com.amap.api.maps.model.IndoorBuildingInfo#floor_names 所有楼层名称
     *  @see com.amap.api.maps.model.IndoorBuildingInfo#activeFloorName 当前楼层名称?
     *
     * 设置跳到某一层的地图:
     *  @see AMap#setIndoorBuildingInfo(IndoorBuildingInfo)
     */
    public static void setOnIndoorBuildingActiveListener(AMap aMap, AMap.OnIndoorBuildingActiveListener listener) {
        aMap.setOnIndoorBuildingActiveListener(listener);
    }

    //一像素代表多少米
    public static float getScalePerPixel(AMap aMap) {
        return aMap.getScalePerPixel();
    }
}
