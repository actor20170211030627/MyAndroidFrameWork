package com.actor.map.gaode;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;

/**
 * description: 高德地图 UI 界面设置
 *
 * @author : ldf
 * date       : 2020/8/10 on 19:01
 * @version 1.0
 */
public class GaoDeUiSettingUtils {

    /**
     * https://lbs.amap.com/api/android-sdk/guide/create-map/mylocation
     * 定位按钮是否显示, 默认false
     */
    public static void setMyLocationButtonEnabled(AMap aMap, boolean enabled) {
        aMap.getUiSettings().setMyLocationButtonEnabled(enabled);
    }

    //设置罗盘, 指南针是否显示, 默认false
    public static void setCompassEnabled(AMap aMap, boolean enabled) {
        aMap.getUiSettings().setCompassEnabled(enabled);
    }

    //设置"比例尺"是否显示, 默认false
    public static void setScaleControlsEnabled(AMap aMap, boolean enabled) {
        aMap.getUiSettings().setScaleControlsEnabled(enabled);
    }

    /**
     * "室内地图控件"是否显示, 默认 {@link GaoDe3DMapUtils#showIndoorMap(AMap, boolean)} 设置的boolean值
     * 移动到有室内地图的地方,放大级别才可以看见, 示例:
     * @see GaoDe3DMapUtils#moveCamera(AMap, LatLng, int); //new LatLng(39.91095, 116.37296), 19)
     */
    public static void setIndoorSwitchEnabled(AMap aMap, boolean enable) {
        aMap.getUiSettings().setIndoorSwitchEnabled(enable);
    }

    //"缩放按钮"是否显示, 默认true
    public static void setZoomControlsEnabled(AMap aMap, boolean enable) {
        aMap.getUiSettings().setZoomControlsEnabled(enable);
    }

    /**
     * 设置缩放控件位置
     * @see com.amap.api.maps.AMapOptions#ZOOM_POSITION_RIGHT_CENTER 右中
     * @see com.amap.api.maps.AMapOptions#ZOOM_POSITION_RIGHT_BUTTOM 右下
     */
    public static void setZoomPosition(AMap aMap, int position) {
        aMap.getUiSettings().setZoomPosition(position);
    }

    /**
     * 设置Logo位置
     * @see com.amap.api.maps.AMapOptions#LOGO_POSITION_BOTTOM_LEFT   左下
     * @see com.amap.api.maps.AMapOptions#LOGO_POSITION_BOTTOM_CENTER 底部水平居中
     * @see com.amap.api.maps.AMapOptions#LOGO_POSITION_BOTTOM_RIGHT  右下
     */
    public static void setLogoPosition(AMap aMap, int position) {
        aMap.getUiSettings().setLogoPosition(position);
    }
}
