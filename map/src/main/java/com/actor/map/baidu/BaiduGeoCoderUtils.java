package com.actor.map.baidu;

import android.location.Location;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.Arrays;

/**
 * description: <a href="https://lbsyun.baidu.com/index.php?title=androidsdk/guide/search/geo" target="_blank">地理编码</a> <br />
 *              地理编码是地址信息和地理坐标之间的相互转换。
 *              可分为正地理编码（地址信息转换为地理坐标）和逆地理编码（地理坐标转换为地址信息）。 <br />
 * <ol>
 *     <li>
 *         <a href="https://repo.maven.apache.org/maven2/com/baidu/lbsyun/BaiduMapSDK_Search/"
 *         target="_blank">添加依赖</a>, 或者在<a href="https://lbsyun.baidu.com/index.php?title=androidsdk/sdkandev-download"
 *         target="_blank">下载中心</a>直接下载.aar包(需要勾选 "检索" 功能) <br />
 *         //地理编码 <br />
 *         implementation 'com.baidu.lbsyun:BaiduMapSDK_Search:7.5.7.1'
 *     </li>
 * </ol>
 *
 * @author : ldf
 */
public class BaiduGeoCoderUtils {

    ///////////////////////////////////////////////////////////////////////////
    // 百度位置编码/逆编码
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
        if (geoCoder == null) geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latLng)
                .newVersion(1)  // 设置是否返回新数据 默认值1返回，0不返回
                .radius(1000)   // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
        );
    }

    /**
     * 百度位置编码, 根据位置地址获取坐标
     * @param cityName 城市名称, 示例: 北京
     * @param address 详细地址, 示例: 北京上地十街10号
     * @param listener 回调监听
     */
    public static void getLngLatByGeoCoder(@NonNull String cityName, @NonNull String address, @NonNull OnGetGeoCoderResultListener listener) {
        if (geoCoder == null) geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.geocode(new GeoCodeOption().city(cityName).address(address));
    }

    /**
     * 在onDestroy()方法中调用, 回收GeoCoder
     */
    public static void recycleGeoCoder() {
        if (geoCoder != null) geoCoder.destroy();
    }

    //百度地图逆地址监听
    protected OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            //获取地理编码结果(根据地址获取point)
            if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
//                String address = result.getAddress();
//                LatLng location = result.getLocation();
//                int describeContents = result.describeContents();

//                ToastUtils.showShort(location.toString());
            }//else 没有检索到结果
        }
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {//主线程
            //获取反向地理编码结果(根据point获取地址)
            if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
//                String address = result.getAddress(); //详细地址
//                int cityCode = result.getCityCode();  //行政区号
//                ReverseGeoCodeResult.AddressComponent addressDetail = result.getAddressDetail();
//                String businessCircle = result.getBusinessCircle();
//                LatLng location = result.getLocation();
//                int describeContents = result.describeContents();
//                List<PoiInfo> poiList = result.getPoiList();

//                drawPosition(point, result);
            }//else 没有找到检索结果
        }
    };



    /**
     * 计算两点之间的距离,单位m
     */
    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        LogUtils.errorFormat("计算两点之间的距离: lat_a=%f, lng_a=%f, lat_b=%f, lng_b=%f", lat_a, lng_a, lat_b, lng_b);
        float[] distance = new float[3];
        Location.distanceBetween(lat_a, lng_a, lat_b, lng_b, distance);
        LogUtils.error(Arrays.toString(distance));//[404982.97, -155.15123, -156.33034]

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
        LogUtils.errorFormat("距离: %f", v);//405415.61429350835
        return v;
    }
}
