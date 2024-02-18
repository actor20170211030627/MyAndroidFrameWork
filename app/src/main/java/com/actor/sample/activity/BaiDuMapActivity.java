package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.map.baidu.BDLocationListener;
import com.actor.map.baidu.BaiduGeoCoderUtils;
import com.actor.map.baidu.BaiduLocationUtils;
import com.actor.map.baidu.BaiduMapUtils;
import com.actor.map.baidu.BaiduUriApiUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityBaiDuMapBinding;
import com.actor.sample.utils.Global;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 百度地图
 * author     : ldf
 * date       : 2020/3/21 on 17:12
 *
 * @version 1.0
 */
public class BaiDuMapActivity extends BaseActivity<ActivityBaiDuMapBinding> {

    private TextView tvResult;
    private MapView  mapView;

    private       BaiduMap baiduMap;
    private final LatLng   centerLatLng = new LatLng(30.624659D, 104.10621D);   //中心红点

    private MarkerOptions markerLocation;   //显示Marker(红点)
    private MarkerOptions markerPerson;     //人
    private MarkerOptions markerRepository; //仓库
    private MarkerOptions markerCar;        //车辆
    private MarkerOptions markerCamera;     //摄像头
    private MarkerOptions markerBridge;     //桥

    private       Overlay       overlayRedPoint; //红点
    private final List<Overlay> overlayPersons     = new ArrayList<>(); //人员覆盖物列表
    private final List<Overlay> overlayRepositorys = new ArrayList<>(); //仓库覆盖物列表
    private final List<Overlay> overlayCars        = new ArrayList<>(); //车辆覆盖物列表
    private final List<Overlay> overlayCameras     = new ArrayList<>(); //摄像头覆盖物列表
    private final List<Overlay> overlayBridges     = new ArrayList<>(); //桥覆盖物列表

    private static final String IS_SHOW_MULTYINFO_WINDOW = "IS_SHOW_MULTYINFO_WINDOW";  //是否显示多个InfoWindow
    private static final String OVERLAY_TYPE             = "OVERLAY_TYPE";              //覆盖物类型

    private static final String WINDOW_TYPE_PERSON = "WINDOW_TYPE_PERSON";      //InfoWindow类型, 人
    private static final String WINDOW_TYPE_REPOSITORY = "WINDOW_TYPE_REPOSITORY";//InfoWindow类型, 仓库
    private static final String WINDOW_TYPE_CAR = "WINDOW_TYPE_CAR";            //InfoWindow类型, 车辆
    private static final String WINDOW_TYPE_CAMERA = "WINDOW_TYPE_CAMERA";      //InfoWindow类型, 摄像头
    private static final String WINDOW_TYPE_BRIDGE = "WINDOW_TYPE_BRIDGE";      //InfoWindow类型, 桥
    private static final String arg0 = "arg0";
    private static final String arg1 = "arg1";
    private static final String arg2 = "arg2";
    private static final String arg3 = "arg3";
    private static final String arg4 = "arg4";
    private static final String arg5 = "arg5";

    //同时显示多个InfoWindow
    private final Map<String, InfoWindow> infoWindowMaps = new HashMap<>();
    //其余类型, 同时仅允许显示1个InfoWindow
    private InfoWindow              infoWindow;
    private final String[]   permissions = {Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("百度地图");
        tvResult = viewBinding.tvResult;
        mapView = viewBinding.mapView;

        mapView.onCreate(this, savedInstanceState);
        baiduMap = BaiduMapUtils.initBaiduMap(mapView, BaiduMap.MAP_TYPE_NORMAL, 15);

        //设置地图加载完成回调
        BaiduMapUtils.setOnMapLoadedCallback(baiduMap, new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                markerLocation = BaiduMapUtils.getMarkerOptions(R.drawable.map_location_icon, 90, 90);
                //在地图上添加Marker,并显示(红点)
                overlayRedPoint = BaiduMapUtils.addOverlay(baiduMap, markerLocation, centerLatLng, null);
                //移除监听
                BaiduMapUtils.setOnMapLoadedCallback(baiduMap, null);
            }
        });

        //地图定位移动到这个位置
        BaiduMapUtils.setMapLocation(baiduMap, centerLatLng);

        //Marker覆盖物点击事件
        BaiduMapUtils.addOnMarkerClickListener(baiduMap, markerClickListener);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_address_by_latlng_geo://坐标→'SDK'→地址
                BaiduGeoCoderUtils.getAddressByGenCoder(new LatLng(43.795592, 87.593087), new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult result) {
                    }
                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                        if (result == null) {
                            ToasterUtils.error("result = null");
                            return;
                        }
                        //获取反向地理编码结果(根据point获取地址)
                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                            String address = result.getAddress();
                            ReverseGeoCodeResult.AddressComponent addressDetail = result.getAddressDetail();
                            String businessCircle = result.getBusinessCircle();
                            LatLng location = result.getLocation();
                            int describeContents = result.describeContents();
                            List<PoiInfo> poiList = result.getPoiList();

                            ToasterUtils.success(address);
                        } else {    //没有找到检索结果
                            ToasterUtils.errorFormat("result.error = %s", result.error);
                        }
                    }
                });
                break;
            case R.id.btn_get_latlng_by_address_geo://地址→'SDK'→坐标
                BaiduGeoCoderUtils.getLngLatByGeoCoder("新疆", Global.ADDRESS_XIN_JIANG, new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult result) {
                        if (result == null) {
                            ToasterUtils.error("result = null");
                            return;
                        }
                        //获取地理编码结果(根据地址获取point)
                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                            String address = result.getAddress();
                            LatLng location = result.getLocation();
                            int describeContents = result.describeContents();
                            ToasterUtils.success(location.toString());
                        } else {    //没有找到检索结果
                            ToasterUtils.errorFormat("result.error = %s", result.error);
                        }
                    }
                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    }
                });
                break;
            case R.id.btn_start://开始定位
                boolean granted = XXPermissions.isGranted(this, permissions);
                if (granted) {
                    BaiduLocationUtils.registerLocationListener(locationListener);
                    BaiduLocationUtils.start();
                } else {
                    XXPermissions.with(this).permission(permissions).request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> list, boolean b) {
                            if (b) {
                                BaiduLocationUtils.registerLocationListener(locationListener);
                                BaiduLocationUtils.start();
                            } else {
                                ToasterUtils.warning("您有权限未同意!");
                                XXPermissions.startPermissionActivity(mActivity);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_stop://结束定位
                stopLocation();
                break;
            case R.id.btn_navigation://调用百度导航
                if (BaiduUriApiUtils.isBaiduMapInstalled()) {
                    boolean success = BaiduUriApiUtils.pathPlanning(this,
                            centerLatLng.longitude, centerLatLng.latitude, Global.ADDRESS_RED_POINT, //起点
                            43.795592, 87.593087, Global.ADDRESS_XIN_JIANG, //终点
                            null, null, null, null);
                } else {
                    ToasterUtils.error("您还未安装百度地图!");
                }
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
        ToasterUtils.info("同时显示多个InfoWindow");
        if (overlayPersons.isEmpty()) {
            markerPerson = BaiduMapUtils.getMarkerOptions(R.drawable.location_person, 90, 90);

            //InfoWindow
            InfoWindow infoWindow0 = BaiduMapUtils.getInfoWindow(getLayoutInflater(), R.layout.map_info_window_event, new LatLng(30.678095, 104.1065), -90);
            InfoWindow infoWindow1 = BaiduMapUtils.getInfoWindow(getLayoutInflater(), R.layout.map_info_window_event, new LatLng(29.538895, 106.47601), -90);
            InfoWindow infoWindow2 = BaiduMapUtils.getInfoWindow(getLayoutInflater(), R.layout.map_info_window_event, new LatLng(30.67064, 104.04872), -90);

            infoWindowMaps.put("person1", infoWindow0);
            infoWindowMaps.put("person2", infoWindow1);
            infoWindowMaps.put("person3", infoWindow2);

            Overlay overlay0 = BaiduMapUtils.addOverlay(baiduMap, markerPerson, new LatLng(30.678095, 104.1065), getBundle(WINDOW_TYPE_PERSON, true, "person1", "age: 23", "tel: 123"));
            //填充InfoWindow & 设置点击事件
            inflateInfoWindow(infoWindow0, overlay0.getExtraInfo());
            overlayPersons.add(overlay0);

            Overlay overlay1 = BaiduMapUtils.addOverlay(baiduMap, markerPerson, new LatLng(29.538895, 106.47601), getBundle(WINDOW_TYPE_PERSON, true, "person2", "age: 2", "tel: 12"));
            inflateInfoWindow(infoWindow1, overlay1.getExtraInfo());
            overlayPersons.add(overlay1);

            Overlay overlay2 = BaiduMapUtils.addOverlay(baiduMap, markerPerson, new LatLng(30.67064, 104.04872), getBundle(WINDOW_TYPE_PERSON, true, "person3", "age: 3", "tel: 32"));
            inflateInfoWindow(infoWindow2, overlay2.getExtraInfo());
            overlayPersons.add(overlay2);
        } else {
            BaiduMapUtils.setOverlayVisible(!view.isSelected(), overlayPersons);
        }
    }
    //仓库
    private void showMarkerRepositoryList(View view) {
        if (overlayRepositorys.isEmpty()) {
            markerRepository = BaiduMapUtils.getMarkerOptions(R.drawable.location_repository, 90, 90);

            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerRepository, new LatLng(30.692505, 103.860146), getBundle(WINDOW_TYPE_REPOSITORY, false, "Repository1", "age: 23", "tel: 123"));
            overlayRepositorys.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerRepository, new LatLng(30.681076, 104.06568), getBundle(WINDOW_TYPE_REPOSITORY, false, "Repository2", "age: 2", "tel: 12"));
            overlayRepositorys.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerRepository, new LatLng(30.648273, 104.07574), getBundle(WINDOW_TYPE_REPOSITORY, false, "Repository3", "age: 3", "tel: 32"));
            overlayRepositorys.add(overlay);
        } else {
            BaiduMapUtils.setOverlayVisible(!view.isSelected(), overlayRepositorys);
        }
    }
    //车辆
    private void showMarkerCarList(View view) {
        if (overlayCars.isEmpty()) {
            markerCar = BaiduMapUtils.getMarkerOptions(R.drawable.location_car, 90, 90);

            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerCar, new LatLng(30.64877, 104.07603), getBundle(WINDOW_TYPE_CAR, false, "Car1", "age: 23", "tel: 123"));
            overlayCars.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCar, new LatLng(30.651007, 104.0766), getBundle(WINDOW_TYPE_CAR, false, "Car2", "age: 2", "tel: 12"));
            overlayCars.add(overlay);
        } else {
            BaiduMapUtils.setOverlayVisible(!view.isSelected(), overlayCars);
        }
    }
    //摄像头
    private void showMarkerCameraList(View view) {
        if (overlayCameras.isEmpty()) {
            markerCamera = BaiduMapUtils.getMarkerOptions(R.drawable.location_camera, 90, 90);

            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera, new LatLng(26.0, 108.0), getBundle(WINDOW_TYPE_CAMERA, false, "Camera1", "age: 23", "tel: 123"));
            overlayCameras.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera, new LatLng(30.70269, 103.99612), getBundle(WINDOW_TYPE_CAMERA, false, "Camera2", "age: 2", "tel: 12"));
            overlayCameras.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera, new LatLng(30.6607, 104.06108), getBundle(WINDOW_TYPE_CAMERA, false, "Camera3", "age: 33", "tel: 128"));
            overlayCameras.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera, new LatLng(30.711384, 103.98145), getBundle(WINDOW_TYPE_CAMERA, false, "Camera4", "age: 55", "tel: 162"));
            overlayCameras.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera, new LatLng(30.602964, 103.945305), getBundle(WINDOW_TYPE_CAMERA, false, "Camera5", "age: 66", "tel: 172"));
            overlayCameras.add(overlay);
        } else {
            BaiduMapUtils.setOverlayVisible(!view.isSelected(), overlayCameras);
        }
    }
    //显示所有桥
    private void showMarkerBridgeList(View view) {
        if (overlayBridges.isEmpty()) {
            markerBridge = BaiduMapUtils.getMarkerOptions(R.drawable.location_bridge, 90, 90);

            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerBridge, new LatLng(30.624659, 104.10621), getBundle(WINDOW_TYPE_BRIDGE, false, "Bridge1", "age: 23", "tel: 123"));
            overlayBridges.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerBridge, new LatLng(30.738207, 104.11397), getBundle(WINDOW_TYPE_BRIDGE, false, "Bridge2", "age: 2", "tel: 12"));
            overlayBridges.add(overlay);

            overlay = BaiduMapUtils.addOverlay(baiduMap, markerBridge, new LatLng(30.629381, 104.205956), getBundle(WINDOW_TYPE_BRIDGE, false, "Bridge3", "age: 33", "tel: 128"));
            overlayBridges.add(overlay);
        } else {
            BaiduMapUtils.setOverlayVisible(!view.isSelected(), overlayBridges);
        }
    }

    /**
     * 定位回调监听
     * @see BDLocationListener extends BDAbstractLocationListener
     */
    private final BDAbstractLocationListener locationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String result = bdLocation.toString();
            tvResult.setText(result);
            //定位成功后, 调用stop()方法不会及时的停下来, 所以这儿延时5秒后再调停止
//            tvResult.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (tvResult != null) stopLocation();
//                }
//            }, 5000);
        }
    };

    //Marker覆盖物点击事件
    private final BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle extraInfo = marker.getExtraInfo();
            if (extraInfo == null) return false;
            String infoWindowType = extraInfo.getString(OVERLAY_TYPE);
            if (infoWindowType != null) {
                switch (infoWindowType) {
                    case WINDOW_TYPE_PERSON:    //显示人员信息(显示多个InfoWindow模式)
                        InfoWindow infoWindow = infoWindowMaps.get(extraInfo.getString(arg0));
                        if (infoWindow != null) BaiduMapUtils.showInfoWindow(baiduMap, infoWindow, false);
                        break;
                    case WINDOW_TYPE_REPOSITORY://显示仓库信息窗
                    case WINDOW_TYPE_CAR:       //显示车辆信息窗
                    case WINDOW_TYPE_CAMERA:    //显示摄像头信息窗
                    case WINDOW_TYPE_BRIDGE:    //显示桥信息窗
                        showInfoWindow(marker.getPosition(), extraInfo);
                        break;
                }
            }
            return true;//若响应点击事件，返回true，否则返回false
        }
    };

    //显示信息窗
    private void showInfoWindow(@NonNull LatLng latLng, @NonNull Bundle extraInfo) {
        //其余类型, 全部复用1个InfoWindow
        if (infoWindow == null) {
            infoWindow = BaiduMapUtils.getInfoWindow(getLayoutInflater(), R.layout.map_info_window_event, latLng, -90);
        } else {
            infoWindow.setPosition(latLng);
        }
        inflateInfoWindow(infoWindow, extraInfo);
        BaiduMapUtils.showInfoWindow(baiduMap, infoWindow, true);
    }
    //填充InfoWindow & 设置点击事件
    private void inflateInfoWindow(@NonNull InfoWindow infoWindow, @NonNull Bundle extraInfo) {
        View view = infoWindow.getView();
        TextView tvType = view.findViewById(R.id.tv_type);//类型
        View ivClose = view.findViewById(R.id.iv_close);
        //如果是人物, 显示多个InfoWindow模式
        if (extraInfo.getBoolean(IS_SHOW_MULTYINFO_WINDOW)) ivClose.setTag(infoWindow);
        ivClose.setOnClickListener(infoWindowClickListener);//关闭
        view.findViewById(R.id.btn).setOnClickListener(infoWindowClickListener);//按钮
        TextView tvAddress = view.findViewById(R.id.tv_address);//地点
        TextView tvName = view.findViewById(R.id.tv_person);//name
        TextView tvPhone = view.findViewById(R.id.tv_phone);//电话

        tvType.setText("type: " + extraInfo.getString(OVERLAY_TYPE));
        tvAddress.setText(extraInfo.getString(arg0));
        tvName.setText(extraInfo.getString(arg1));
        tvPhone.setText(extraInfo.getString(arg2));
    }

    //infoWindow里的按钮被点击之后
    private final View.OnClickListener infoWindowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close://关闭 InfoWindow
                    Object tag = v.getTag();
                    if (tag instanceof InfoWindow) {
                        //是否显示多个InfoWindow模式, 以setTag的方式用于关闭InfoWindow
                        BaiduMapUtils.hideInfoWindow(baiduMap, (InfoWindow) tag);
                    } else {
                        BaiduMapUtils.hideInfoWindow(baiduMap, infoWindow);//隐藏信息窗
                    }
                    break;
                case R.id.btn:
                    ToasterUtils.info("clicked btn");
                    break;
            }
        }
    };

    //停止定位
    private void stopLocation() {
        BaiduLocationUtils.unRegisterLocationListener(locationListener);
        BaiduLocationUtils.stop();
    }

    /**
     * @param type Marker类型
     * @param isShowMultyInfoWindow 是否显示多个InfoWindow
     * @param extra 其余额外信息
     */
    private Bundle getBundle(String type, boolean isShowMultyInfoWindow, String... extra) {
        Bundle bundle = new Bundle();
        bundle.putString(OVERLAY_TYPE, type);
        bundle.putBoolean(IS_SHOW_MULTYINFO_WINDOW, isShowMultyInfoWindow);
        for (int i = 0; i < extra.length; i++) {
            bundle.putString("arg" + i, extra[i]);
        }
        return bundle;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); //MapView的生命周期与Activity同步
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();  //MapView的生命周期与Activity同步
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();         //停止定位
        mapView.onDestroy();    //MapView的生命周期与Activity同步

        //移除Marker点击监听
        BaiduMapUtils.removeMarkerClickListener(baiduMap, markerClickListener);

        //回收MarkerOptions里的图片
        BaiduMapUtils.recycleMarkerOptions(markerLocation, markerPerson, markerRepository,
                markerCar, markerCamera, markerBridge);

        infoWindowMaps.clear();
        infoWindow = null;

        //清空覆盖物资源
        BaiduMapUtils.removeOverlays(overlayRedPoint);
        BaiduMapUtils.removeOverlays(overlayPersons, overlayRepositorys, overlayCars,
                overlayCameras, overlayBridges);

        BaiduMapUtils.clear(baiduMap);

        //回收 地理编码
        BaiduGeoCoderUtils.recycleGeoCoder();
    }
}
