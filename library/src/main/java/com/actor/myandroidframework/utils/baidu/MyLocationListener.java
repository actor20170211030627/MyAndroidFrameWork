package com.actor.myandroidframework.utils.baidu;

import com.actor.myandroidframework.utils.LogUtils;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.blankj.utilcode.util.GsonUtils;

/**
 * Description: 位置监听
 * Author     : ldf
 * Date       : 2017/9/18 on 16:20.
 * @version 1.0
 *
 * 第三步，实现BDAbstractLocationListener接口
 * Android定位SDK自v7.2版本起，对外提供了Abstract类型的监听接口BDAbstractLocationListener
 * ，用于实现定位监听。原有BDLocationListener暂时保留，推荐开发者升级到Abstract类型的新监听接口使用，
 * 该接口会异步获取定位结果，实现方式如下：
 */

public class MyLocationListener extends BDAbstractLocationListener {

    protected OnLocationResult onLocationResult;

    public static double lngDefault, latDefault;//默认经纬度, 如果定位失败返回默认, 可在Application中设置
    public static String addressDefault;//默认地址, 如果定位失败返回默认, 可在Application中设置

    public MyLocationListener(OnLocationResult onLocationResult) {
        this.onLocationResult = onLocationResult;
    }

    private String  msg;
    private boolean locationSuccess = false;

    @Override
    public void onReceiveLocation(BDLocation location) {
        LogUtils.error(false, GsonUtils.toJson(location));//打印成json,有很多很多信息
        //获取定位结果
//        location.getTime();             //获取定位时间
//        location.getLocType();          //获取定位类型
//        location.getLocTypeDescription();//对应的定位类型说明
//        location.getLatitude();         //获取纬度信息
//        location.getLongitude();        //获取经度信息
//        location.getRadius();           //获取定位精准度
//        location.getCountry();          //获取国家名称
//        location.getCountryCode();      //获取国家码
//        location.getCity();             //获取城市信息
//        location.getCityCode();         //获取城市编码
//        location.getDistrict();         //获取区县信息
//        location.getStreet();           //获取街道信息
//        location.getStreetNumber();     //获取街道码
//        location.getAddrStr();          //获取地址信息
//        location.getUserIndoorState();  //返回用户室内外判断结果
//        location.getDirection();        //方向
//        location.getLocationDescribe(); //获取当前位置描述信息,位置语义化信息,类似于“在北京天安门附近”
//        location.getPoiList();          //获取当前位置周边POI信息
//        if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//            for (int i = 0; i < location.getPoiList().size(); i++) {
//                Poi poi = (Poi) location.getPoiList().get(i);
//                sb.append(poi.getName() + ";");
//            }
//        }
//        location.getFloor();            //室内精准定位下，获取当前位置所处的楼层信息
//        location.getBuildingName();     //室内精准定位下，获取楼宇名称
//        location.getBuildingID();       //室内精准定位下，获取楼宇ID

        if (location.getLocType() == BDLocation.TypeGpsLocation){//当前为GPS定位结果,可获取以下信息
            location.getSpeed();            //获取当前速度，单位：km/h
            location.getSatelliteNumber();  //获取当前卫星数
            location.getAltitude();         //获取海拔高度信息，单位米
            location.getDirection();        //获取方向信息，单位度
            location.getGpsAccuracyStatus();//gps质量判断
            locationSuccess = true;
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){//网络定位结果,可获取以下信息
            if (location.hasAltitude()) {   //如果有海拔高度
                location.getAltitude();     // 单位：米
            }
            location.getOperators();        //获取运营商信息
            locationSuccess = true;
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {//离线定位结果

            locationSuccess = true;

        } else if (location.getLocType() == BDLocation.TypeServerError) {

            //当前网络定位失败
            //可将定位唯一ID、IMEI、定位失败时间反馈至loc-bugs@baidu.com
            location.getLocationID();       //获取定位唯一ID，v7.2版本新增，用于排查定位问题

            locationSuccess = false;
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {//当前网络不通

            locationSuccess = false;

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            //无法获取有效定位依据导致定位失败:
            //1.可能是用户没有授权,建议弹出提示框让用户开启权限
            //2.可能是由于手机的原因,处于飞行模式下一般会造成这种结果,可以试着重启手机
            //可进一步参考onLocDiagnosticMessage中的错误返回码
            locationSuccess = false;
        }
        if (onLocationResult != null) {
            if (locationSuccess) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String addrStr = location.getAddrStr();
                onLocationResult.onOk(longitude, latitude, addrStr);
            } else onLocationResult.onOk(lngDefault, latDefault, addressDefault);
        }
    }

    /**
     * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
     * 自动回调，相同的diagnosticType只会回调一次
     *
     * @param locType           当前定位类型
     * @param diagnosticType    诊断类型（1~9）
     * @param diagnosticMessage 具体的诊断信息释义
     */
    @Override
    public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {

        if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {

            //建议打开GPS

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {

            //建议打开wifi，不必连接，这样有助于提高网络定位精度！

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_LOC_PERMISSION) {

            //定位权限受限，建议提示用户授予APP定位权限！

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_NET) {

            //网络异常造成定位失败，建议用户确认网络状态是否异常！

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CLOSE_FLYMODE) {

            //手机飞行模式造成定位失败，建议用户关闭飞行模式后再重试定位！

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_INSERT_SIMCARD_OR_OPEN_WIFI) {

            //无法获取任何定位依据，建议用户打开wifi或者插入sim卡重试！

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_OPEN_PHONE_LOC_SWITCH) {

            //无法获取有效定位依据，建议用户打开手机设置里的定位开关后重试！

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_SERVER_FAIL) {

            //百度定位服务端定位失败
            //建议反馈location.getLocationID()和大体定位时间到loc-bugs@baidu.com

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_FAIL_UNKNOWN) {

            //无法获取有效定位依据，但无法确定具体原因
            //建议检查是否有安全软件屏蔽相关定位权限
            //或调用LocationClient.restart()重新启动后重试！

        }
    }

    public interface OnLocationResult {
        void onOk(double lng, double lat, String address);
    }
}
