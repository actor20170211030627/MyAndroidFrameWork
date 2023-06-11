package com.actor.map.gaode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.blankj.utilcode.util.Utils;

/**
 * description: <a href="https://lbs.amap.com/api/android-sdk/guide/map-data/geo" target="_blank">获取地址描述数据</a>
 *
 * @author : ldf
 */
public class GaoDeGeoCoderUtils {

    ///////////////////////////////////////////////////////////////////////////
    // 高德位置编码/逆编码
    ///////////////////////////////////////////////////////////////////////////
    protected static GeocodeSearch geocoderSearch;

    public static GeocodeSearch getGeocodeSearch() {
        if (geocoderSearch == null) {
            try {
                geocoderSearch = new GeocodeSearch(Utils.getApp());
            } catch (AMapException e) {
                e.printStackTrace();
            }
        }
        return geocoderSearch;
    }

    /**
     * 高德位置逆编码, 根据坐标获取位置地址
     * @param latLng 坐标
     * @param radius 范围多少米, 默认1000米, {@link RegeocodeQuery#getRadius()}
     * @param latLonType 火系坐标系还是GPS原生坐标系, 默认{@link GeocodeSearch#AMAP}, 可传null 或 {@link GeocodeSearch#GPS}
     * @param listener 这个listerer不要每次都new, 只需要new一次
     */
    public static void getAddressByGeocodeSearch(@NonNull LatLonPoint latLng, @Nullable Float radius,
                                            @Nullable String latLonType,
                                            @NonNull GeocodeSearch.OnGeocodeSearchListener listener) {
        GeocodeSearch geocodeSearch = getGeocodeSearch();
        geocodeSearch.setOnGeocodeSearchListener(listener);
        if (radius == null) radius = 1000.0F;
        geocodeSearch.getFromLocationAsyn(new RegeocodeQuery(latLng, radius, latLonType));
    }

    /**
     * 高德位置编码, 根据位置地址获取坐标 <br />
     * 结构化地址的定义： <br />
     * 首先，地址肯定是一串字符，内含国家、省份、城市、城镇、乡村、街道、门牌号码、屋邨、大厦等建筑物名称。
     * 按照由大区域名称到小区域名称组合在一起的字符。一个有效的地址应该是独一无二的。 <br />
     * 注意：针对大陆、港、澳地区的地理编码转换时可以将国家信息选择性的忽略，但省、市、城镇等级别的地址构成是不能忽略的。
     * @param locationName 地址, 不能=null, 示例: 新疆维吾尔自治区乌鲁木齐市沙依巴克区奇台路676号
     * @param city 查询的城市，可传入: cityname(中文或中文全拼)，citycode(城市编码)、adcode(行政区划代码), <br />
     *             &emsp;&emsp; 传入null或空字符串则为“全国”,
     *             示例: "北京", "beijing", "010"
     * @param listener 回调监听
     */
    public static void getLngLatByGeocodeSearch(@NonNull String locationName, @Nullable String city,
                                                @NonNull GeocodeSearch.OnGeocodeSearchListener listener) {
        GeocodeSearch geocodeSearch = getGeocodeSearch();
        geocodeSearch.setOnGeocodeSearchListener(listener);
        //GeocodeQuery: https://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html
        geocodeSearch.getFromLocationNameAsyn(new GeocodeQuery(locationName, city));
    }

    //高德地图逆地址监听
    protected GeocodeSearch.OnGeocodeSearchListener listener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            //获取反向地理编码结果(根据point获取地址), rCode错误码: https://lbs.amap.com/api/android-sdk/guide/map-tools/error-code
            if (result != null && rCode == 1000) {
//                RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
//                RegeocodeQuery regeocodeQuery = result.getRegeocodeQuery();
//                if (regeocodeAddress != null) {
//                    String formatAddress = regeocodeAddress.getFormatAddress();
//                    String cityCode = regeocodeAddress.getCityCode();
//                    regeocodeAddress.get...
//                }
//                if (regeocodeQuery != null) {
//                    regeocodeQuery.get...
//                }
//                drawPosition(point, result);
            }//else 没有找到检索结果
        }

        @Override
        public void onGeocodeSearched(GeocodeResult result, int rCode) {
            //获取地理编码结果(根据地址获取point)
            if (result != null && rCode == 1000) {
//                GeocodeQuery geocodeQuery = result.getGeocodeQuery();
//                List<GeocodeAddress> geocodeAddresses = result.getGeocodeAddressList();
//                if (geocodeQuery != null) {
//                    String country = geocodeQuery.getCountry();
//                    String city = geocodeQuery.getCity();
//                    String locationName = geocodeQuery.getLocationName();
//                }
//                if (geocodeAddresses != null && !geocodeAddresses.isEmpty()) {
//                    for (GeocodeAddress geocodeAddress : geocodeAddresses) {
//                        LatLonPoint latLonPoint = geocodeAddress.getLatLonPoint();
//                        geocodeAddress.get...
//                    }
//                }
            }//else 没有检索到结果
        }
    };
}
