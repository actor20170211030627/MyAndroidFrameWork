package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.map.gaode.GaoDe3DMapUtils;
import com.actor.map.gaode.GaoDeGeoCoderUtils;
import com.actor.map.gaode.GaoDeLocationUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityGaoDeMapBinding;
import com.actor.sample.info.Object4GaodeMapInfoWindow;
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
import com.blankj.utilcode.util.GsonUtils;
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

    private AMap          aMap;
    private MarkerOptions markerLocation;//显示Marker(红点)
    private MarkerOptions markerPerson;//人
    private MarkerOptions markerRepository;//仓库
    private MarkerOptions markerCar;//车辆
    private MarkerOptions markerCamera;//摄像头
    private MarkerOptions markerBridge;//桥

    private final double        lat = 30.624659D;
    private final double        lng = 104.10621D;//中心红点
    private       List<Marker> overlayPersons;//人员覆盖物列表
    private List<Marker> overlayRepositorys;//仓库覆盖物列表
    private List<Marker> overlayCars;//车辆覆盖物列表
    private List<Marker> overlayCameras;//摄像头覆盖物列表
    private List<Marker> overlayBridges;//桥覆盖物列表

//    private static final String OVERLAY_TYPE = "OVERLAY_TYPE";//覆盖物类型
    private static final String WINDOW_TYPE_EVENT = "WINDOW_TYPE_EVENT";//InfoWindow类型, 中心红点
    private static final String WINDOW_TYPE_PERSON = "WINDOW_TYPE_PERSON";//InfoWindow类型, 人
    private static final String WINDOW_TYPE_REPOSITORY = "WINDOW_TYPE_REPOSITORY";//InfoWindow类型, 仓库
    private static final String WINDOW_TYPE_CAR = "WINDOW_TYPE_CAR";//InfoWindow类型, 车辆
    private static final String WINDOW_TYPE_CAMERA = "WINDOW_TYPE_CAMERA";//InfoWindow类型, 摄像头
    private static final String WINDOW_TYPE_BRIDGE = "WINDOW_TYPE_BRIDGE";//InfoWindow类型, 桥
//    private static final String arg0 = "arg0";
//    private static final String arg1 = "arg1";
//    private static final String arg2 = "arg2";
//    private static final String arg3 = "arg3";
//    private static final String arg4 = "arg4";
//    private static final String arg5 = "arg5";

    private Marker         infoWindowMarker;//提示窗(像Dialog)
    private final String[] permissions = {Permission.ACCESS_COARSE_LOCATION,
            Permission.ACCESS_FINE_LOCATION, Permission.WRITE_EXTERNAL_STORAGE};

    private final AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            //自定义布局
            View view = getLayoutInflater().inflate(R.layout.map_info_window_event, null);
//            infoWindowMarker = GaoDe3DMapUtils.getInfoWindow(, , latLng, -90);
//            View view = infoWindowMarker.getView();
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
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("高德地图");
        tvResult = viewBinding.tvResult;
        mapView = viewBinding.mapView;

        aMap = GaoDe3DMapUtils.initGaoDeMap(mapView, AMap.MAP_TYPE_NORMAL, 15);

        LatLng latLng = new LatLng(lat, lng);
        //地图定位移动到这个位置
        GaoDe3DMapUtils.setMapLocation(aMap, latLng);
//        GaoDe3DMapUtils.clear(aMap);
        markerLocation = GaoDe3DMapUtils.getMarkerOptions(R.drawable.map_location_icon, 90, 90);
        //在地图上添加Marker,并显示(红点)
        GaoDe3DMapUtils.addMarker(aMap, markerLocation, latLng, false, WINDOW_TYPE_EVENT, null, null);

        //Marker覆盖物点击事件
        GaoDe3DMapUtils.addOnMarkerClickListener(aMap, markerClickListener);
    }

//    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_get_address_by_latlng,
//            R.id.btn_get_latlng_by_address, R.id.iv_person, R.id.iv_repository, R.id.iv_car,
//            R.id.iv_camera, R.id.iv_bridge})
    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                        }//else 没有找到检索结果
                    }
                    @Override
                    public void onGeocodeSearched(GeocodeResult result, int rCode) {
                    }
                });
                break;
            case R.id.btn_get_latlng_by_address_geo://地址→'SDK'→坐标
                GaoDeGeoCoderUtils.getLngLatByGeocodeSearch("新疆维吾尔自治区乌鲁木齐市沙依巴克区奇台路676号",
                        "新疆", new GeocodeSearch.OnGeocodeSearchListener() {
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
                        }//else 没有检索到结果
                    }
                });
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
        if (overlayPersons == null) {
            overlayPersons = new ArrayList<>();
            if (markerPerson == null) {
                markerPerson = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_person, 90, 90);
            }
            //添加到高德地图中
            Object4GaodeMapInfoWindow person1 = new Object4GaodeMapInfoWindow("person1", 23, "123");
            Marker marker = GaoDe3DMapUtils.addMarker(aMap, markerPerson,
                    new LatLng(30.678095, 104.1065), false, WINDOW_TYPE_PERSON, null, person1);
            overlayPersons.add(marker);//添加到集合中
            Object4GaodeMapInfoWindow person2 = new Object4GaodeMapInfoWindow("person2", 2, "12");
            marker = GaoDe3DMapUtils.addMarker(aMap, markerPerson,
                    new LatLng(29.538895, 106.47601), false, WINDOW_TYPE_PERSON, null, person2);
            overlayPersons.add(marker);
            Object4GaodeMapInfoWindow person3 = new Object4GaodeMapInfoWindow("person3", 3, "32");
            marker = GaoDe3DMapUtils.addMarker(aMap, markerPerson,
                    new LatLng(30.67064, 104.04872), false, WINDOW_TYPE_PERSON, null, person3);
            overlayPersons.add(marker);
        } else {
            GaoDe3DMapUtils.showOrRemoveMarkers(false, overlayPersons);
            overlayPersons = null;
//            boolean selected = view.isSelected();
//            if (selected) {//需要移除
//            }
        }
    }
    //仓库
    private void showMarkerRepositoryList(View view) {
        if (overlayRepositorys == null) {
            overlayRepositorys = new ArrayList<>();
            if (markerRepository == null) {
                markerRepository = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_repository, 90, 90);
            }
            //添加到高德地图中
            Object4GaodeMapInfoWindow repository1 = new Object4GaodeMapInfoWindow("Repository1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerRepository,
                    new LatLng(30.692505, 103.860146), false, WINDOW_TYPE_REPOSITORY, null, repository1);
            overlayRepositorys.add(overlay);//添加到集合中
            Object4GaodeMapInfoWindow repository2 = new Object4GaodeMapInfoWindow("Repository2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerRepository,
                    new LatLng(30.681076, 104.06568), false, WINDOW_TYPE_REPOSITORY, null, repository2);
            overlayRepositorys.add(overlay);
            Object4GaodeMapInfoWindow repository3 = new Object4GaodeMapInfoWindow("Repository3", 3, "32");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerRepository,
                    new LatLng(30.648273, 104.07574), false, WINDOW_TYPE_REPOSITORY, null, repository3);
            overlayRepositorys.add(overlay);
        } else {
            GaoDe3DMapUtils.showOrRemoveMarkers(false, overlayRepositorys);
            overlayRepositorys = null;
        }
    }
    //车辆
    private void showMarkerCarList(View view) {
        if (overlayCars == null) {
            overlayCars = new ArrayList<>();
            if (markerCar == null) {
                markerCar = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_car, 90, 90);
            }
            //添加到高德地图中
            Object4GaodeMapInfoWindow car1 = new Object4GaodeMapInfoWindow("Car1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerCar,
                    new LatLng(30.64877, 104.07603), false, WINDOW_TYPE_CAR, null, car1);
            overlayCars.add(overlay);//添加到集合中
            Object4GaodeMapInfoWindow car2 = new Object4GaodeMapInfoWindow("Car2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerCar,
                    new LatLng(30.651007, 104.0766), false, WINDOW_TYPE_CAR, null, car2);
            overlayCars.add(overlay);
        } else {
            GaoDe3DMapUtils.showOrRemoveMarkers(false, overlayCars);
            overlayCars = null;
        }
    }
    //摄像头
    private void showMarkerCameraList(View view) {
        if (overlayCameras == null) {
            overlayCameras = new ArrayList<>();
            if (markerCamera == null) {
                markerCamera = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_camera, 90, 90);
            }
            //添加到高德地图中
            Object4GaodeMapInfoWindow camera1 = new Object4GaodeMapInfoWindow("Camera1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerCamera,
                    new LatLng(26.0, 108.0), false, WINDOW_TYPE_CAMERA, null, camera1);
            overlayCameras.add(overlay);//添加到集合中
            Object4GaodeMapInfoWindow camera2 = new Object4GaodeMapInfoWindow("Camera2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerCamera,
                    new LatLng(30.70269, 103.99612), false, WINDOW_TYPE_CAMERA, null, camera2);
            overlayCameras.add(overlay);
            Object4GaodeMapInfoWindow camera3 = new Object4GaodeMapInfoWindow("Camera3", 33, "128");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerCamera,
                    new LatLng(30.6607, 104.06108), false, WINDOW_TYPE_CAMERA, null, camera3);
            overlayCameras.add(overlay);
            Object4GaodeMapInfoWindow camera4 = new Object4GaodeMapInfoWindow("Camera4", 55, "163");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerCamera,
                    new LatLng(30.711384, 103.98145), false, WINDOW_TYPE_CAMERA, null, camera4);
            overlayCameras.add(overlay);
            Object4GaodeMapInfoWindow camera5 = new Object4GaodeMapInfoWindow("Camera5", 66, "173");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerCamera,
                    new LatLng(30.602964, 103.945305), false, WINDOW_TYPE_CAMERA, null, camera5);
            overlayCameras.add(overlay);
        } else {
            GaoDe3DMapUtils.showOrRemoveMarkers(false, overlayCameras);
            overlayCameras = null;
        }
    }
    //显示所有桥
    private void showMarkerBridgeList(View view) {
        if (overlayBridges == null) {
            overlayBridges = new ArrayList<>();
            if (markerBridge == null) {
                markerBridge = GaoDe3DMapUtils.getMarkerOptions(R.drawable.location_bridge, 90, 90);
            }
            //添加到高德地图中
            Object4GaodeMapInfoWindow bridge1 = new Object4GaodeMapInfoWindow("Bridge1", 23, "123");
            Marker overlay = GaoDe3DMapUtils.addMarker(aMap, markerBridge,
                    new LatLng(30.624659, 104.10621), false, WINDOW_TYPE_BRIDGE, null, bridge1);
            overlayBridges.add(overlay);//添加到集合中
            Object4GaodeMapInfoWindow bridge2 = new Object4GaodeMapInfoWindow("Bridge2", 2, "12");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerBridge,
                    new LatLng(30.738207, 104.11397), false, WINDOW_TYPE_BRIDGE, null, bridge2);
            overlayBridges.add(overlay);
            Object4GaodeMapInfoWindow bridge3 = new Object4GaodeMapInfoWindow("Bridge3", 33, "128");
            overlay = GaoDe3DMapUtils.addMarker(aMap, markerBridge,
                    new LatLng(30.629381, 104.205956), false, WINDOW_TYPE_BRIDGE, null, bridge3);
            overlayBridges.add(overlay);
        } else {
            GaoDe3DMapUtils.showOrRemoveMarkers(false, overlayBridges);
            overlayBridges = null;
        }
    }

    /**
     * 定位回调监听
     */
    private final AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            String result = GsonUtils.toJson(aMapLocation);
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
    private final AMap.OnMarkerClickListener markerClickListener = marker -> {
        infoWindowMarker = marker;
        String title = marker.getTitle();
        Object4GaodeMapInfoWindow object = (Object4GaodeMapInfoWindow) marker.getObject();
        if (title == null) {
            showToast("未获取到数据");
            return false;
        }
        switch (title) {
            case WINDOW_TYPE_EVENT:
                showEventInfoWindow(marker, object);
                break;
            case WINDOW_TYPE_PERSON:
                showPersonInfoWindow(marker, object);
                break;
            case WINDOW_TYPE_REPOSITORY:
                showRepositoryInfoWindow(marker, object);
                break;
            case WINDOW_TYPE_CAR:
                showCarInfoWindow(marker, object);
                break;
            case WINDOW_TYPE_CAMERA:
                showCameraInfoWindow(marker, object);
                break;
            case WINDOW_TYPE_BRIDGE:
                showBridgeInfoWindow(marker, object);
                break;
        }
        return true;//若响应点击事件，返回true，否则返回false
    };

    //中心红点信息窗
    private void showEventInfoWindow(Marker marker, @Nullable Object4GaodeMapInfoWindow extraInfo) {
        //自定义布局
//        infoWindowMarker = GaoDe3DMapUtils.getInfoWindow(getLayoutInflater(), R.layout.map_info_window_event, latLng, -90);
//        View view = infoWindowMarker.getView();
//        TextView tvType = view.findViewById(R.id.tv_type);//类型
//        view.findViewById(R.id.iv_close).setOnClickListener(infoWindowClickListener);//关闭
//        view.findViewById(R.id.btn).setOnClickListener(infoWindowClickListener);//按钮
//        TextView tvAddress = view.findViewById(R.id.tv_address);//地点
//        TextView tvName = view.findViewById(R.id.tv_person);//name
//        TextView tvPhone = view.findViewById(R.id.tv_phone);//电话
//
//        tvType.setText("type: " + extraInfo.getString(OVERLAY_TYPE));
//        tvAddress.setText(extraInfo.getString(arg0));
//        tvName.setText(extraInfo.getString(arg1));
//        tvPhone.setText(extraInfo.getString(arg2));
        GaoDe3DMapUtils.setInfoWindowAdapter(aMap, infoWindowAdapter);
        GaoDe3DMapUtils.showInfoWindow(marker);//显示信息窗
    }
    //显示人员信息窗(可自定义, 这儿为了简便, 和上方一致)
    private void showPersonInfoWindow(Marker marker, @NonNull Object4GaodeMapInfoWindow extraInfo) {
        showEventInfoWindow(marker, extraInfo);
    }
    //显示仓库信息窗
    private void showRepositoryInfoWindow(Marker marker, @NonNull Object4GaodeMapInfoWindow extraInfo) {
        showEventInfoWindow(marker, extraInfo);
    }
    //显示车辆信息窗
    private void showCarInfoWindow(Marker marker, @NonNull Object4GaodeMapInfoWindow extraInfo) {
        showEventInfoWindow(marker, extraInfo);
    }
    //显示摄像头信息窗
    private void showCameraInfoWindow(Marker marker, @NonNull Object4GaodeMapInfoWindow extraInfo) {
        showEventInfoWindow(marker, extraInfo);
    }
    //显示桥信息窗
    private void showBridgeInfoWindow(Marker marker, @NonNull Object4GaodeMapInfoWindow extraInfo) {
        showEventInfoWindow(marker, extraInfo);
    }

    //infoWindow(相当于Dialog)里的按钮被点击之后
    private View.OnClickListener infoWindowClickListener = new View.OnClickListener() {
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
        stopLocation();//停止定位
        infoWindowClickListener = null;
        infoWindowMarker = null;
        mapView.onDestroy();//MapView的生命周期与Activity同步
        GaoDe3DMapUtils.removeOnMarkerClickListener(aMap, markerClickListener);
        GaoDe3DMapUtils.recycleMarkerOption(markerLocation);
        GaoDe3DMapUtils.recycleMarkerOption(markerPerson);
        GaoDe3DMapUtils.recycleMarkerOption(markerRepository);
        GaoDe3DMapUtils.recycleMarkerOption(markerCar);
        GaoDe3DMapUtils.recycleMarkerOption(markerCamera);
        GaoDe3DMapUtils.recycleMarkerOption(markerBridge);

        if (isNoEmpty(overlayPersons)) overlayPersons.clear();//人员覆盖物列表
        if (isNoEmpty(overlayRepositorys)) overlayRepositorys.clear();//仓库覆盖物列表
        if (isNoEmpty(overlayCars)) overlayCars.clear();//车辆覆盖物列表
        if (isNoEmpty(overlayCameras)) overlayCameras.clear();//摄像头覆盖物列表
        if (isNoEmpty(overlayBridges)) overlayBridges.clear();//摄像头覆盖物列表
    }
}
