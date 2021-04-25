package com.actor.myandroidframework.utils.baidu;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ImageUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * description: 百度地图帮助类
 * 1-5. 配置和 {@link BaiduLocationUtils} 百度定位Utils 一样
 *
 * 6.Application中初始化
 *   @Override
 *   public void onCreate() {
 *       super.onCreate();
 *       BaiduMapUtils.init(this);//初始化百度地图
 *   }
 *
 * 7.示例使用见:
 * https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/BaiDuMapActivity.java
 *
 * author     : 李大发
 * date       : 2019/4/22 on 21:50
 * @version 1.0
 * @version 1.1 新增从地图中编码/逆编码位置信息
 * @version 1.2 新增一些方法
 */
public class BaiduMapUtils {

    //逆地理编码url, 坐标->地址
    protected static final String URL_REVERSE_GEOCODING = "http://api.map.baidu.com/reverse_geocoding/v3/";
    //地理编码url, 地址->坐标
    protected static final String URL_GEOCODING = "http://api.map.baidu.com/geocoding/v3/";
    protected static final String AK                    = "u5Xz2U2d6hSgaqEcDG2Z8MlQqNhVO1VX";
    protected static final String SHA1 = "F5:18:3E:C1:04:17:FC:B2:34:18:7A:11:1D:7E:C7:81:69:08:65:1B";
    protected static final String PACKAGE_NAME = ";com.actor.sample";//; + 包名
    protected static final Map<String, Object> params = new LinkedHashMap<>(10);

    public static final String GAODE_PACKAGE_NAME = "com.autonavi.minimap";//高德地图包名
    public static final String BAIDU_PACKAGE_NAME = "com.baidu.BaiduMap";//百度地图包名

    protected BaiduMapUtils() {}

    /**
     * 通过网络,根据地址获取经纬度, 返回json 的 status = 0表示获取成功
     * lng 经度
     * lat 纬度
     * @param address 新疆维吾尔自治区乌鲁木齐市沙依巴克区奇台路676号
     * 地理编码:
     * http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
     */
    public static void getLngLatByNet(String address, BaseCallback<LngLatInfo> callback) {
        params.clear();
        params.put("output", "json");
        params.put("ak", AK);
        params.put("address", address);
        params.put("mcode", SHA1.concat(PACKAGE_NAME));
        MyOkHttpUtils.get(URL_GEOCODING, params, callback);
    }

    /**
     * 通过网络,根据经纬度获取地址
     * 例: double lng = info.result.location.lng;
     *     AddressInfo.ResultBean.AddressComponentBean address = info.result.addressComponent;
     *     //重庆市南岸区东水门大桥东北100米
     *     String place = address.city + address.district + address.sematic_description;
     * @param lng 经度,比如:87.593087
     * @param lat 纬度,比如:43.795592
     * 逆地理编码:
     * http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding-abroad
     */
    public static void getAddressByNet(double lng, double lat, BaseCallback<AddressInfo> callback) {
        params.clear();
        params.put("output", "json");
        params.put("ak", AK);
        //params.put("coordtype", "bd09ll");//默认
        //params.put("poi_types", "酒店|房地产");
        //params.put("extensions_poi", "1");//将上方查询点返回, 默认0不返回
        params.put("location", lat + "," + lng);
        params.put("mcode", SHA1.concat(PACKAGE_NAME));
        MyOkHttpUtils.get(URL_REVERSE_GEOCODING, params, callback);
    }

    /**
     * 获取完整路径
     * @param lng 经度,比如:87.593087
     * @param lat 纬度,比如:43.795592
     * @param callback 回调, 不能传null
     */
    public static void getAddressStringByNet(double lng, double lat, @NonNull OnAddressCallback callback) {
        getAddressByNet(lng, lat, new BaseCallback<AddressInfo>(callback.tag) {
            @Override
            public void onBefore(Request request, int id) {
                //super.onBefore(request, id);
            }
            @Override
            public void onOk(@NonNull AddressInfo info, int id, boolean isRefresh) {
                if (info.status == 0) {
                    AddressInfo.ResultBean result = info.result;
                    if (result != null) {
                        double lng = 0, lat = 0;
                        String address = result.formatted_address;
                        AddressInfo.ResultBean.LocationBean location = result.location;
                        if (location != null) {
                            lng = location.lng;
                            lat = location.lat;
                        }
                        callback.onOk(lng, lat, address, id);
                    } else {
                        callback.onOk(0, 0, null, id);
                    }
                } else {
                    toast(info.message);
                    callback.onOk(0, 0, null, id);
                }
            }
            @Override
            public void onError(int id, Call call, Exception e) {
                super.onError(id, call, e);
                callback.onError(id, call, e);
            }
        });
    }
    public static abstract class OnAddressCallback {
        public Object tag;
        public int id;
        public OnAddressCallback(Object tag) {
            this.tag = tag;
        }
        public OnAddressCallback(Object tag, int id) {
            this.tag = tag;
            this.id = id;
        }
        public abstract void onOk(double lng, double lat, @Nullable String address, int id);
        public void onError(int id, Call call, Exception e) {}
    }

    /**
     * 计算两点之间的距离,单位m
     */
    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        logFormat("计算两点之间的距离: lat_a=%f, lng_a=%f, lat_b=%f, lng_b=%f", lat_a, lng_a, lat_b, lng_b);
        float[] distance = new float[3];
        Location.distanceBetween(lat_a, lng_a, lat_b, lng_b, distance);
        logError(Arrays.toString(distance));//[404982.97, -155.15123, -156.33034]

        if (true) return distance[0];

        double pk = 180 / 3.14169;
        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        double v = 6371000 * tt;
        logFormat("距离: %f", v);//405415.61429350835
        return v;
    }

    /**
     * 打开高德地图导航功能, 待测试
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @return 是否打开成功
     */
    public static boolean openGaoDeNavigation(Context context, double slon, double slat, String sname,
                                     double dlon, double dlat) {
        boolean appInstalled = AppUtils.isAppInstalled(GAODE_PACKAGE_NAME);//是否安装高德地图
        if (!appInstalled) return false;
        StringBuilder sb = new StringBuilder("amapuri://route/plan?sourceApplication=maxuslife");
        if (slat != 0) {
            sb.append("&sname=")
                    .append(sname)
                    .append("&slat=")
                    .append(slat)
                    .append("&slon=")
                    .append(slon);
        }
        sb.append("&dlat=").append(dlat)
                .append("&dlon=")
                .append(dlon)
//                .append("&dname=")
//                .append(dname)
                .append("&dev=0")
                .append("&t=0");
        String string = sb.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(GAODE_PACKAGE_NAME);
        intent.setData(Uri.parse(string));
        context.startActivity(intent);
        return true;
    }

    /**
     * 打开百度地图导航功能, 待测试
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @return 是否打开成功
     */
    public static boolean openBaiDuNavigation(Context context, double slon, double slat, String sname,
                                     double dlon, double dlat) {
        boolean appInstalled = AppUtils.isAppInstalled(BAIDU_PACKAGE_NAME);//是否安装百度地图
        if (!appInstalled) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/direction?mode=driving&");
        if (slat != 0) {
            sb.append("origin=latlng:")
                    .append(slat)
                    .append(",")
                    .append(slon)
                    .append("|name:")
                    .append(sname);
        }
        sb.append("&destination=latlng:")
                .append(dlat)
                .append(",")
                .append(dlon)
                .append("|name:");
        String string = sb.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(BAIDU_PACKAGE_NAME);
        intent.setData(Uri.parse(string));
        context.startActivity(intent);
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 百度位置编码/逆编码(下载SDK时需要勾选"检索功能")
    ///////////////////////////////////////////////////////////////////////////
    protected static GeoCoder geoCoder;
    /**
     * 百度位置逆编码, 根据坐标获取位置地址
     * 注意:
     *   1.在onDestroy()方法中调用: {@link #recycleGeoCoder()} 方法销毁
     *   2.有可能Json会解析错误, 所以最好用getAddressStringByNet()方法
     *
     * @param latLng 坐标
     * @param listener 这个listerer不要每次都new, 只需要new一次
     */
    public static void getAddressByGenCoder(LatLng latLng, @NonNull OnGetGeoCoderResultListener listener) {
        if (geoCoder == null) {
            geoCoder = GeoCoder.newInstance();
            geoCoder.setOnGetGeoCodeResultListener(listener);
        }
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }
    //回收GeoCoder
    public static void recycleGeoCoder() {
        if (geoCoder != null) geoCoder.destroy();
    }

    //百度地图逆地址监听
    protected OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            //获取地理编码结果(根据地址获取point)
            if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
//                String address = result.getAddressByNet();
//                LatLng location = result.getLocation();
//                int i = result.describeContents();
//                result.setAddress("address");
            }//else 没有检索到结果
        }
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {//主线程
            //获取反向地理编码结果(根据point获取地址)
            if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
//                String address = result.getAddressByNet();
//                ReverseGeoCodeResult.AddressComponent addressDetail = result.getAddressDetail();
//                String businessCircle = result.getBusinessCircle();
//                LatLng location = result.getLocation();
//                int i = result.describeContents();
//                List<PoiInfo> poiList = result.getPoiList();

//                drawPosition(point, result);
//                address = result.getAddressByNet();
            }//else 没有找到检索结果
        }
    };


    ///////////////////////////////////////////////////////////////////////////
    // 百度地图
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 如果用到百度地图, 在 Application 中初始化
     */
    public static void init(Application application) {
        SDKInitializer.initialize(application);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    /**
     * 初始化BaiduMap, 注意调用MapView的生命周期方法:
     * @see MapView#onResume() 当activity恢复时需调用 mapView.onResume()
     * @see MapView#onPause() 当activity挂起时需调用 mapView.onPause()
     * @see MapView#onDestroy() 当activity销毁时需调用 mapView.destroy()
     * @param mapView 百度地图MapView
     * @param mapType 设置地图样式为普通,有三种, 示例: BaiduMap.MAP_TYPE_NORMAL
     * @param zoomLevel 缩放级别, 示例: 15
     * @return baiduMap
     */
    public static BaiduMap initBaiduMap(MapView mapView, int mapType, float zoomLevel) {
        BaiduMap baiduMap = mapView.getMap();
        baiduMap.setMapType(mapType);//设置地图样式为普通,有三种
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(zoomLevel));
        return baiduMap;
    }

    /**
     * 设置Map中心位置, 让地图移动到latLng这个点为中心的位置
     * @param baiduMap 百度地图
     * @param latLng 坐标
     */
    public static void setMapLocation(BaiduMap baiduMap, LatLng latLng) {
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
    }

    /**
     * 获取MarkerOptions(Marker覆盖物选项)
     * 注意:
     *   1. MarkerOptions应该复用, 只是表示一个图标而已, 可重复设置坐标再添加到地图, 写成全局变量, 例:
     *       markerOption.position(latLng);
     *       markerOption.extraInfo(Bundle bundle);
     *       baiduMap.addOverlay(markerOption);//会在地图新增一个latLng
     *   2.在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions)}//回收图片
     *
     * @param resId 图片resId
     * @param width marker宽度, 单位px, 例: 90
     * @param height marker高度, 单位px, 例: 90
     * @return 百度Marker
     */
    public static MarkerOptions getMarkerOptions(@DrawableRes int resId, int width, int height) {
        Bitmap bitmap = ImageUtils.getBitmap(resId);
        Bitmap scale = ImageUtils.scale(bitmap, width, height, true);
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromBitmap(scale);
        return new MarkerOptions().icon(bitmapDesc);
    }

    /**
     * 在百度地图上添加一个覆盖物
     * @param markerOption 见 {@link #getMarkerOptions(int, int, int)}
     * @param bundle 这个覆盖物可携带的额外数据
     * @return 返回覆盖物, 注意在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions)}//回收图片
     */
    public static Overlay addOverlay(BaiduMap baiduMap, MarkerOptions markerOption, LatLng latLng, Bundle bundle) {
        if (latLng == null) return null;
        markerOption.position(latLng);
        markerOption.extraInfo(bundle);
        return baiduMap.addOverlay(markerOption);//会在地图新增一个latLng
    }
    //回收图片
    public static void recycleMarkerOption(MarkerOptions markerOption) {
        if (markerOption != null) {
            BitmapDescriptor icon = markerOption.getIcon();
            if (icon != null) icon.recycle();
        }
    }

    /**
     * 设置覆盖物显示状态
     *
     * @see #showOrRemoveOverlays(boolean, List)
     * @param visible 是否显示
     * @param overlays 覆盖物, overlay = baiduMap.addOverlay(overlayOptions);
     * @deprecated 隐藏后, 点击事件还在!!!
     */
    @Deprecated
    public static void setOverlayVisible(boolean visible, Overlay... overlays) {
        if (overlays != null && overlays.length > 0) {
            setOverlayVisible(visible, Arrays.asList(overlays));
        }
    }
    /**
     * 设置覆盖物显示状态
     *
     * @see #showOrRemoveOverlays(boolean, List)
     * @param visible 是否显示
     * @param overlays 覆盖物, overlay = baiduMap.addOverlay(overlayOptions);
     * @deprecated 隐藏后, 点击事件还在!!!
     */
    @Deprecated
    public static void setOverlayVisible(boolean visible, List<Overlay> overlays) {
        if (overlays != null && overlays.size() > 0) {
            for (Overlay overlay : overlays) {
                if (overlay != null) overlay.setVisible(visible);//隐藏后, 点击事件还在!!!
            }
        }
    }

    /**
     * 显示/移除 覆盖物
     * 移除该覆盖物, 移除后再调用overlay.setVisible(true);无效!!!
     *
     * @see #setOverlayVisible(boolean, Overlay...)
     * @see #setOverlayVisible(boolean, List)
     *
     * @param showOrRemove true显示, false移除
     * @param overlays 覆盖物, overlay = baiduMap.addOverlay(overlayOptions);
     */
    public static void showOrRemoveOverlays(boolean showOrRemove, List<Overlay> overlays) {
        if (overlays != null && overlays.size() > 0) {
            for (Overlay overlay : overlays) {
                if (overlay != null) {
                    if (showOrRemove) {
                        overlay.setVisible(true);
                    } else {
                        overlay.remove();//移除该覆盖物, 移除后再调用overlay.setVisible(true);无效!!!
                    }
                }
            }
            if (!showOrRemove) overlays.clear();//如果移除, 那就清空
        }
    }

    /**
     * 每次都new InfoWindow(), 因为如果复用infoWindow.setPosition()的话, 会造成infoWindow重叠现象
     * 注意:
     *   1.infoWindow.getView();//返回布局, 可以findViewById
     *
     * @param inflater
     * @param layoutId 布局id
     * @param latLng 坐标
     * @param yOffset y轴偏移, 比如向上偏移90, 就传: -90
     */
    public static InfoWindow getInfoWindow(LayoutInflater inflater, @LayoutRes int layoutId, LatLng latLng, int yOffset) {
        View view = inflater.inflate(layoutId, null);
        return new InfoWindow(view, latLng, yOffset);
    }

    /**
     * 显示 InfoWindow
     */
    public static void showInfoWindow(BaiduMap baiduMap, InfoWindow infoWindow/*, boolean what*/) {
        baiduMap.showInfoWindow(infoWindow/*, what*/);
    }

    /**
     * 百度地图设置点击事件
     */
    public static void setOnMapClickListener(BaiduMap baiduMap, BaiduMap.OnMapClickListener listener) {
        baiduMap.setOnMapClickListener(listener);
    }

    /**
     * Marker覆盖物点击事件
     * 注意调用方法: {@link #removeMarkerClickListener(BaiduMap, BaiduMap.OnMarkerClickListener)}
     */
    public static void addOnMarkerClickListener(BaiduMap baiduMap, BaiduMap.OnMarkerClickListener listener) {
        baiduMap.setOnMarkerClickListener(listener);
    }
    public static void removeMarkerClickListener(BaiduMap baiduMap, BaiduMap.OnMarkerClickListener listener) {
        baiduMap.removeMarkerClickListener(listener);
    }

    /**
     * 清除所有覆盖物?
     */
    public static void clear(BaiduMap baiduMap) {
        baiduMap.clear();
    }

    protected static void logError(String msg) {
        LogUtils.error(msg, false);
    }

    protected static void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }
}
