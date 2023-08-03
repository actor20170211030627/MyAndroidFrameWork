package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.map.gaode.GaoDe3DMapUtils;
import com.actor.map.gaode.GaoDeGeoCoderUtils;
import com.actor.map.gaode.GaoDeLocationUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityGaoDeMapBinding;
import com.actor.sample.info.Object4GaodeMapInfoWindow;
import com.actor.sample.utils.Global;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 高德地图
 * author     : ldf
 * date       : 2020/3/21 on 17:12
 *
 * @version 1.0
 */
public class GaoDeMapActivity extends BaseActivity<ActivityGaoDeMapBinding> {

    private TextView tvResult;
    private MapView  mapView;

    private       AMap   aMap;
    private final LatLng centerLatLng = new LatLng(30.624659D, 104.10621D);   //中心红点

    private MarkerOptions markerOptionsLocation;   //显示Marker(红点)
    private MarkerOptions markerOptionsPerson;     //人
    private MarkerOptions markerOptionsRepository; //仓库
    private MarkerOptions markerOptionsCar;        //车辆
    private MarkerOptions markerOptionsCamera;     //摄像头
    private MarkerOptions markerOptionsBridge;     //桥

    private       Marker       markerRedPoint;   //红点
    private final List<Marker> markerPersons     = new ArrayList<>();//人员覆盖物列表
    private final List<Marker> markerRepositorys = new ArrayList<>();//仓库覆盖物列表
    private final List<Marker> markerCars        = new ArrayList<>();//车辆覆盖物列表
    private final List<Marker> markerCameras     = new ArrayList<>();//摄像头覆盖物列表
    private final List<Marker> markerBridges     = new ArrayList<>();//桥覆盖物列表

    private static final String IS_SHOW_MULTYINFO_WINDOW = "IS_SHOW_MULTYINFO_WINDOW";  //是否显示多个InfoWindow

    private static final String WINDOW_TYPE_PERSON     = "WINDOW_TYPE_PERSON";//InfoWindow类型, 人
    private static final String WINDOW_TYPE_REPOSITORY = "WINDOW_TYPE_REPOSITORY";//InfoWindow类型, 仓库
    private static final String WINDOW_TYPE_CAR        = "WINDOW_TYPE_CAR";//InfoWindow类型, 车辆
    private static final String WINDOW_TYPE_CAMERA     = "WINDOW_TYPE_CAMERA";//InfoWindow类型, 摄像头
    private static final String WINDOW_TYPE_BRIDGE     = "WINDOW_TYPE_BRIDGE";//InfoWindow类型, 桥
//    private static final String arg0 = "arg0";
//    private static final String arg1 = "arg1";
//    private static final String arg2 = "arg2";
//    private static final String arg3 = "arg3";
//    private static final String arg4 = "arg4";
//    private static final String arg5 = "arg5";

    private Marker         infoWindowMarker;//提示窗
    private final String[] permissions = {Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("高德地图");
        tvResult = viewBinding.tvResult;
        mapView = viewBinding.mapView;
        //if不调用会导致地图空白
        mapView.onCreate(savedInstanceState);
        aMap = GaoDe3DMapUtils.initGaoDeMap(mapView, AMap.MAP_TYPE_NORMAL, 15);

        //地图定位移动到这个位置
        GaoDe3DMapUtils.setMapLocation(aMap, centerLatLng);
        markerOptionsLocation = GaoDe3DMapUtils.getMarkerOptions(R.drawable.map_location_icon, 90, 90);
        //在地图上添加Marker,并显示(红点)
        markerRedPoint = GaoDe3DMapUtils.addMarker(aMap, markerOptionsLocation, centerLatLng, false, null, null, null);

        //Marker覆盖物点击事件
        GaoDe3DMapUtils.setOnMarkerClickListener(aMap, markerClickListener);
        //设置InfoWindow的Adapter
        GaoDe3DMapUtils.setInfoWindowAdapter(aMap, infoWindowAdapter);
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_address_by_latlng_geo://坐标→'SDK'→地址
                GaoDeGeoCoderUtils.getAddressByGeocodeSearch(new LatLonPoint(43.795592, 87.593087),
                        null, null, new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                        //获取反向地理编码结果(根据point获取地址)
                        if (result != null && rCode == 1000) {
                            RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
                            RegeocodeQuery regeocodeQuery = result.getRegeocodeQuery();
                            if (regeocodeAddress != null) {
                                String formatAddress = regeocodeAddress.getFormatAddress();
//                                String cityCode = regeocodeAddress.getCityCode();
//                                regeocodeAddress.get...
                                showToast(formatAddress);
                            }
//                            if (regeocodeQuery != null) {
//                                regeocodeQuery.get...
//                            }
                        } else {
                            showToast("没有找到检索结果");
                        }
                    }
                    @Override
                    public void onGeocodeSearched(GeocodeResult result, int rCode) {
                    }
                });
                break;
            case R.id.btn_get_latlng_by_address_geo://地址→'SDK'→坐标
                GaoDeGeoCoderUtils.getLngLatByGeocodeSearch(Global.ADDRESS_XIN_JIANG, "新疆", new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                    }
                    @Override
                    public void onGeocodeSearched(GeocodeResult result, int rCode) {
                        if (result != null && rCode == 1000) {
                            GeocodeQuery geocodeQuery = result.getGeocodeQuery();
                            List<GeocodeAddress> geocodeAddresses = result.getGeocodeAddressList();
//                            if (geocodeQuery != null) {
//                                String country = geocodeQuery.getCountry();
//                                String city = geocodeQuery.getCity();
//                                String locationName = geocodeQuery.getLocationName();
//                            }
                            if (geocodeAddresses != null && !geocodeAddresses.isEmpty()) {
                                for (GeocodeAddress geocodeAddress : geocodeAddresses) {
                                    LatLonPoint latLonPoint = geocodeAddress.getLatLonPoint();
//                                    geocodeAddress.get...
                                    showToastFormat("lat=%f, lng=%f", latLonPoint.getLatitude(), latLonPoint.getLongitude());
                                }
                            }
                        } else {
                            showToast("没有找到检索结果");
                        }
                    }
                });
                break;
            case R.id.btn_start://开始定位
                boolean granted = XXPermissions.isGranted(this, permissions);
                if (granted) {
                    GaoDeLocationUtils.setLocationListener(locationListener);
                    GaoDeLocationUtils.startLocation();
                } else {
                    XXPermissions.with(this).permission(permissions).request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> list, boolean b) {
                            if (b) {
                                GaoDeLocationUtils.setLocationListener(locationListener);
                                GaoDeLocationUtils.startLocation();
                            } else {
                                ToastUtils.showShort("您有权限未同意!");
                                XXPermissions.startPermissionActivity(activity);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_stop://结束定位
                stopLocation();
                break;
            case R.id.iv_person://人
                showMarkerPersonList(view);
                break;
            case R.id.iv_repository://仓库
                showMarkerRepositoryList(view);
                break;
            case R.id.iv_car://车辆
                showMarkerCarList(view);
                break;
            case R.id.iv_camera://摄像头
                showMarkerCameraList(view);
                break;
            case R.id.iv_bridge://桥
                showMarkerBridgeList(view);
                break;
            default:
                break;
        }
        view.setSelected(!view.isSelected());//更新选中状态
    }

    //显示所有人员覆盖物列表
    private void showMarkerPersonList(View view) {
        if (markerPersons.isEmpty()) {
            markerOptionsPerson = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_person, 90, 90);

            //添加到高德地图中                       Object4GaodeMapInfoWindow是自己写的自定义对象
            Object4GaodeMapInfoWindow person1 = new Object4GaodeMapInfoWindow("person1", 23, "123");
            Marker marker = GaoDe3DMapUtils.addMarker(aMap, markerOptionsPerson, new LatLng(30.678095, 104.1065), true, WINDOW_TYPE_PERSON, null, person1);
            markerPersons.add(marker);

            Object4GaodeMapInfoWindow person2 = new Object4GaodeMapInfoWindow("person2", 2, "12");
            marker = GaoDe3DMapUtils.addMarker(aMap, markerOptionsPerson, new LatLng(29.538895, 106.47601), true, WINDOW_TYPE_PERSON, null, person2);
            markerPersons.add(marker);

            Object4GaodeMapInfoWindow person3 = new Object4GaodeMapInfoWindow("person3", 3, "32");
            marker = GaoDe3DMapUtils.addMarker(aMap, markerOptionsPerson, new LatLng(30.67064, 104.04872), true, WINDOW_TYPE_PERSON, null, person3);
            markerPersons.add(marker);
        } else {
            GaoDe3DMapUtils.setMarkerVisible(!view.isSelected(), markerPersons);
        }
    }
    //仓库
    private void showMarkerRepositoryList(View view) {
        if (markerRepositorys.isEmpty()) {
            markerOptionsRepository = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_repository, 90, 90);

            //添加到高德地图中
            Object4GaodeMapInfoWindow repository1 = new Object4GaodeMapInfoWindow("Repository1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsRepository, new LatLng(30.692505, 103.860146), true, WINDOW_TYPE_REPOSITORY, null, repository1);
            markerRepositorys.add(overlay);

            Object4GaodeMapInfoWindow repository2 = new Object4GaodeMapInfoWindow("Repository2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsRepository, new LatLng(30.681076, 104.06568), true, WINDOW_TYPE_REPOSITORY, null, repository2);
            markerRepositorys.add(overlay);

            Object4GaodeMapInfoWindow repository3 = new Object4GaodeMapInfoWindow("Repository3", 3, "32");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsRepository, new LatLng(30.648273, 104.07574), true, WINDOW_TYPE_REPOSITORY, null, repository3);
            markerRepositorys.add(overlay);
        } else {
            GaoDe3DMapUtils.setMarkerVisible(!view.isSelected(), markerRepositorys);
        }
    }
    //车辆
    private void showMarkerCarList(View view) {
        if (markerCars.isEmpty()) {
            markerOptionsCar = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_car, 90, 90);

            //添加到高德地图中
            Object4GaodeMapInfoWindow car1 = new Object4GaodeMapInfoWindow("Car1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsCar, new LatLng(30.64877, 104.07603), true, WINDOW_TYPE_CAR, null, car1);
            markerCars.add(overlay);

            Object4GaodeMapInfoWindow car2 = new Object4GaodeMapInfoWindow("Car2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsCar, new LatLng(30.651007, 104.0766), true, WINDOW_TYPE_CAR, null, car2);
            markerCars.add(overlay);
        } else {
            GaoDe3DMapUtils.setMarkerVisible(!view.isSelected(), markerCars);
        }
    }
    //摄像头
    private void showMarkerCameraList(View view) {
        if (markerCameras.isEmpty()) {
            markerOptionsCamera = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_camera, 90, 90);

            //添加到高德地图中
            Object4GaodeMapInfoWindow camera1 = new Object4GaodeMapInfoWindow("Camera1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsCamera, new LatLng(26.0, 108.0), true, WINDOW_TYPE_CAMERA, null, camera1);
            markerCameras.add(overlay);

            Object4GaodeMapInfoWindow camera2 = new Object4GaodeMapInfoWindow("Camera2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsCamera, new LatLng(30.70269, 103.99612), true, WINDOW_TYPE_CAMERA, null, camera2);
            markerCameras.add(overlay);

            Object4GaodeMapInfoWindow camera3 = new Object4GaodeMapInfoWindow("Camera3", 33, "128");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsCamera, new LatLng(30.6607, 104.06108), true, WINDOW_TYPE_CAMERA, null, camera3);
            markerCameras.add(overlay);

            Object4GaodeMapInfoWindow camera4 = new Object4GaodeMapInfoWindow("Camera4", 55, "163");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsCamera, new LatLng(30.711384, 103.98145), true, WINDOW_TYPE_CAMERA, null, camera4);
            markerCameras.add(overlay);

            Object4GaodeMapInfoWindow camera5 = new Object4GaodeMapInfoWindow("Camera5", 66, "173");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsCamera, new LatLng(30.602964, 103.945305), true, WINDOW_TYPE_CAMERA, null, camera5);
            markerCameras.add(overlay);
        } else {
            GaoDe3DMapUtils.setMarkerVisible(!view.isSelected(), markerCameras);
        }
    }
    //显示所有桥
    private void showMarkerBridgeList(View view) {
        if (markerBridges.isEmpty()) {
            markerOptionsBridge = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_bridge, 90, 90);

            //添加到高德地图中
            Object4GaodeMapInfoWindow bridge1 = new Object4GaodeMapInfoWindow("Bridge1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsBridge, new LatLng(30.624659, 104.10621), true, WINDOW_TYPE_BRIDGE, null, bridge1);
            markerBridges.add(overlay);

            Object4GaodeMapInfoWindow bridge2 = new Object4GaodeMapInfoWindow("Bridge2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsBridge, new LatLng(30.738207, 104.11397), true, WINDOW_TYPE_BRIDGE, null, bridge2);
            markerBridges.add(overlay);

            Object4GaodeMapInfoWindow bridge3 = new Object4GaodeMapInfoWindow("Bridge3", 33, "128");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerOptionsBridge, new LatLng(30.629381, 104.205956), true, WINDOW_TYPE_BRIDGE, null, bridge3);
            markerBridges.add(overlay);
        } else {
            GaoDe3DMapUtils.setMarkerVisible(!view.isSelected(), markerBridges);
        }
    }

    /**
     * 定位回调监听
     */
    private final AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            String result = aMapLocation.toString();
            tvResult.setText(result);
            //定位成功后, 调用stop()方法不会不会及时的停下来, 所以这儿延时5秒后再调停止
//            tvResult.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (tvResult != null) stopLocation();
//                }
//            }, 5000);
        }
    };

    //Marker覆盖物点击事件
    private final AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            LogUtils.error("AMap.OnMarkerClickListener.onMarkerClick");
            infoWindowMarker = marker;
            String title = marker.getTitle();
            if (title == null) return false;
            switch (title) {
                case WINDOW_TYPE_PERSON:    //显示人员信息(显示多个InfoWindow模式)
//                    showPersonInfoWindow(marker, object);
                    GaoDe3DMapUtils.showInfoWindow(marker);//显示信息窗
                    break;
                case WINDOW_TYPE_REPOSITORY://显示仓库信息窗
                case WINDOW_TYPE_CAR:       //显示车辆信息窗
                case WINDOW_TYPE_CAMERA:    //显示摄像头信息窗
                case WINDOW_TYPE_BRIDGE:    //显示桥信息窗
                    GaoDe3DMapUtils.showInfoWindow(marker);
                    break;
                default:
                    break;
            }
            return true;//若响应点击事件，返回true，否则返回false
        }
    };

    private final AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            LogUtils.error("AMap.InfoWindowAdapter.getInfoWindow");
            //自定义布局
            View view = getLayoutInflater().inflate(R.layout.map_info_window_event, null);
            TextView tvType = view.findViewById(R.id.tv_type);//类型
            view.findViewById(R.id.iv_close).setOnClickListener(infoWindowClickListener);//关闭
            view.findViewById(R.id.btn).setOnClickListener(infoWindowClickListener);//按钮
            TextView tvAddress = view.findViewById(R.id.tv_address);//地点
            TextView tvName = view.findViewById(R.id.tv_person);//name
            TextView tvPhone = view.findViewById(R.id.tv_phone);//电话

            tvType.setText("type: " + marker.getTitle());
            Object4GaodeMapInfoWindow object = (Object4GaodeMapInfoWindow) marker.getObject();
            if (object != null) {
                tvAddress.setText(String.valueOf(object.age));
                tvName.setText(object.name);
                tvPhone.setText(object.tel);
            }
            return view;
        }
        @Override
        public View getInfoContents(Marker marker) {
            LogUtils.error("AMap.InfoWindowAdapter.getInfoContents");
            return null;
        }
    };

    //infoWindow(相当于Dialog)里的按钮被点击之后
    private final View.OnClickListener infoWindowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close://关闭 InfoWindow
                    GaoDe3DMapUtils.hideInfoWindow(infoWindowMarker);//隐藏信息窗
                    break;
                case R.id.btn:
                    showToast("clicked btn");
                    break;
                default:
                    break;
            }
        }
    };

    //停止定位
    private void stopLocation() {
        GaoDeLocationUtils.unRegisterLocationListener(locationListener);
        GaoDeLocationUtils.stopLocation();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();//MapView的生命周期与Activity同步
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();//MapView的生命周期与Activity同步
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();     //停止定位
        mapView.onDestroy();//MapView的生命周期与Activity同步

        //移除Marker点击监听
        GaoDe3DMapUtils.removeOnMarkerClickListener(aMap, markerClickListener);
        //回收MarkerOptions里的图片
        GaoDe3DMapUtils.recycleMarkerOption(markerOptionsLocation, markerOptionsPerson,
                markerOptionsRepository, markerOptionsCar, markerOptionsCamera, markerOptionsBridge);

        //销毁Marker
        GaoDe3DMapUtils.destroyMarkers(markerRedPoint, infoWindowMarker);
        GaoDe3DMapUtils.destroyMarkers(markerPersons, markerRepositorys, markerCars, markerCameras, markerBridges);

        infoWindowMarker = null;

        GaoDe3DMapUtils.clear(aMap);
    }
}
