package com.actor.map.baidu;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

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

    //逆地理编码url, 坐标->地址
    protected static final String URL_REVERSE_GEOCODING = "http://api.map.baidu.com/reverse_geocoding/v3/";
    //地理编码url, 地址->坐标
    protected static final String URL_GEOCODING = "http://api.map.baidu.com/geocoding/v3/";
    protected static String AK                  = "u5Xz2U2d6hSgaqEcDG2Z8MlQqNhVO1VX";
    protected static String SHA1                = "F5:18:3E:C1:04:17:FC:B2:34:18:7A:11:1D:7E:C7:81:69:08:65:1B";
    protected static       String              PACKAGE_NAME = ";com.actor.sample";  //; + 包名
    protected static final Map<String, Object> params       = new LinkedHashMap<>(10);


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
        if (geoCoder == null) {
            geoCoder = GeoCoder.newInstance();
        }
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latLng)
                .newVersion(1)  // 设置是否返回新数据 默认值1返回，0不返回
                .radius(1000)   // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
        );
    }

    /**
     * 百度位置编码, 根据位置地址获取坐标
     * @param cityName 城市名称, 不能=null, 示例: 北京
     * @param address 详细地址, 不能=null, 示例: 北京上地十街10号
     * @param listener 回调监听
     */
    public static void getLngLatByGenCoder(@NonNull String cityName, @NonNull String address, @NonNull OnGetGeoCoderResultListener listener) {
        if (geoCoder == null) {
            geoCoder = GeoCoder.newInstance();
        }
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



    ///////////////////////////////////////////////////////////////////////////
    // 通过网络请求的方式转换
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 设置自己的App信息, 用于请求数据. 详情见官网
     */
    public static void setAK_sha1_packageName(@NonNull String AK, @NonNull String sha1, @NonNull String packageName) {
        BaiduGeoCoderUtils.AK = AK;
        BaiduGeoCoderUtils.SHA1 = sha1;
        BaiduGeoCoderUtils.PACKAGE_NAME = packageName;
    }

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
                    ToastUtils.showShort(info.message);
                    callback.onError(id, null, null);
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
        public LifecycleOwner tag;
        public int            id;
        public OnAddressCallback(LifecycleOwner tag) {
            this.tag = tag;
        }
        public OnAddressCallback(LifecycleOwner tag, int id) {
            this.tag = tag;
            this.id = id;
        }
        public abstract void onOk(double lng, double lat, @Nullable String address, int id);
        public void onError(int id, @Nullable Call call, @Nullable Exception e) {}
    }


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
