package com.actor.sample.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.utils.baidu.BaiduLocationUtils;
import com.actor.myandroidframework.utils.baidu.BaiduMapUtils;
import com.actor.myandroidframework.utils.baidu.LngLatInfo;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.sample.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * description: 百度地图
 * author     : 李大发
 * date       : 2020/3/21 on 17:12
 *
 * @version 1.0
 */
public class BaiDuMapActivity extends BaseActivity {

    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.map_view)
    MapView  mapView;

    private BaiduMap      baiduMap;
    private MarkerOptions markerLocation;//显示Marker(红点)
    private MarkerOptions markerPerson;//人
    private MarkerOptions markerRepository;//仓库
    private MarkerOptions markerCar;//车辆
    private MarkerOptions markerCamera;//摄像头
    private MarkerOptions markerBridge;//桥

    private double lat = 30.624659, lng = 104.10621;//中心红点
    private List<Overlay> overlayPersons;//人员覆盖物列表
    private List<Overlay> overlayRepositorys;//仓库覆盖物列表
    private List<Overlay> overlayCars;//车辆覆盖物列表
    private List<Overlay> overlayCameras;//摄像头覆盖物列表
    private List<Overlay> overlayBridges;//桥覆盖物列表

    private static final String OVERLAY_TYPE = "OVERLAY_TYPE";//覆盖物类型
    private static final String WINDOW_TYPE_EVENT = "WINDOW_TYPE_EVENT";//InfoWindow类型, 中心红点
    private static final String WINDOW_TYPE_PERSON = "WINDOW_TYPE_PERSON";//InfoWindow类型, 人
    private static final String WINDOW_TYPE_REPOSITORY = "WINDOW_TYPE_REPOSITORY";//InfoWindow类型, 仓库
    private static final String WINDOW_TYPE_CAR = "WINDOW_TYPE_CAR";//InfoWindow类型, 车辆
    private static final String WINDOW_TYPE_CAMERA = "WINDOW_TYPE_CAMERA";//InfoWindow类型, 摄像头
    private static final String WINDOW_TYPE_BRIDGE = "WINDOW_TYPE_BRIDGE";//InfoWindow类型, 桥
    private static final String arg0 = "arg0";
    private static final String arg1 = "arg1";
    private static final String arg2 = "arg2";
    private static final String arg3 = "arg3";
    private static final String arg4 = "arg4";
    private static final String arg5 = "arg5";

    private InfoWindow                             infoWindow;//提示窗(像Dialog)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bai_du_map);
        ButterKnife.bind(this);
        setTitle("百度地图");
        baiduMap = BaiduMapUtils.initBaiduMap(mapView, BaiduMap.MAP_TYPE_NORMAL, 15);

        LatLng latLng = new LatLng(lat, lng);
        //地图定位移动到这个位置
        BaiduMapUtils.setMapLocation(baiduMap, latLng);
//        BaiduMapUtils.clear(baiduMap);
        markerLocation = BaiduMapUtils.getMarkerOptions(R.drawable.baidumap_location_icon, 90, 90);
        //在地图上添加Marker,并显示(红点)
        BaiduMapUtils.addOverlay(baiduMap, markerLocation, latLng, getBundle(WINDOW_TYPE_EVENT));

        //Marker覆盖物点击事件
        BaiduMapUtils.addOnMarkerClickListener(baiduMap, markerClickListener);
    }

    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_get_address_by_latlng,
            R.id.btn_get_latlng_by_address, R.id.iv_person, R.id.iv_repository, R.id.iv_car,
            R.id.iv_camera, R.id.iv_bridge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start://开始定位
                BaiduLocationUtils.registerListener(locationListener);
                BaiduLocationUtils.start();
                break;
            case R.id.btn_stop://结束定位
                stopLocation();
                break;
            case R.id.btn_get_address_by_latlng://坐标→'网络'→地址
                BaiduMapUtils.getAddressStringByNet(87.593087, 43.795592, new BaiduMapUtils.OnAddressCallback(this) {
                    @Override
                    public void onOk(double lng, double lat, @Nullable String address, int id) {
                        dismissLoadingDialog();
                        toast(address);
                    }
                });
                break;
            case R.id.btn_get_latlng_by_address://地址→'网络'→坐标
                BaiduMapUtils.getLngLatByNet("新疆维吾尔自治区乌鲁木齐市沙依巴克区奇台路676号", new BaseCallback<LngLatInfo>(this) {
                    @Override
                    public void onOk(@NonNull LngLatInfo info, int id) {
                        dismissLoadingDialog();
                        if (info.status == 0) {
                            LngLatInfo.ResultBean result = info.result;
                            if (result != null) {
                                LngLatInfo.ResultBean.LocationBean location = result.location;
                                if (location != null) {
                                    toast(getStringFormat("lng=%f, lat=%f", location.lng, location.lat));
                                }
                            }
                        } else toast(info.message);
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
        }
        view.setSelected(!view.isSelected());//更新选中状态
    }

    //显示所有人员覆盖物列表
    private void showMarkerPersonList(View view) {
        if (overlayPersons == null) {
            overlayPersons = new ArrayList<>();
            if (markerPerson == null) {
                markerPerson = BaiduMapUtils.getMarkerOptions(R.drawable.location_person, 90, 90);
            }
            //添加到百度地图中
            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerPerson,
                    new LatLng(30.678095, 104.1065),
                    getBundle(WINDOW_TYPE_PERSON, "person1", "age: 23", "tel: 123"));
            overlayPersons.add(overlay);//添加到集合中
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerPerson,
                    new LatLng(29.538895, 106.47601),
                    getBundle(WINDOW_TYPE_PERSON, "person2", "age: 2", "tel: 12"));
            overlayPersons.add(overlay);
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerPerson,
                    new LatLng(30.67064, 104.04872),
                    getBundle(WINDOW_TYPE_PERSON, "person3", "age: 3", "tel: 32"));
            overlayPersons.add(overlay);
        } else {
            BaiduMapUtils.showOrRemoveOverlays(false, overlayPersons);
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
                markerRepository = BaiduMapUtils.getMarkerOptions(R.drawable.location_repository, 90, 90);
            }
            //添加到百度地图中
            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerRepository,
                    new LatLng(30.692505, 103.860146),
                    getBundle(WINDOW_TYPE_REPOSITORY, "Repository1", "age: 23", "tel: 123"));
            overlayRepositorys.add(overlay);//添加到集合中
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerRepository,
                    new LatLng(30.681076, 104.06568),
                    getBundle(WINDOW_TYPE_REPOSITORY, "Repository2", "age: 2", "tel: 12"));
            overlayRepositorys.add(overlay);
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerRepository,
                    new LatLng(30.648273, 104.07574),
                    getBundle(WINDOW_TYPE_REPOSITORY, "Repository3", "age: 3", "tel: 32"));
            overlayRepositorys.add(overlay);
        } else {
            BaiduMapUtils.showOrRemoveOverlays(false, overlayRepositorys);
            overlayRepositorys = null;
        }
    }
    //车辆
    private void showMarkerCarList(View view) {
        if (overlayCars == null) {
            overlayCars = new ArrayList<>();
            if (markerCar == null) {
                markerCar = BaiduMapUtils.getMarkerOptions(R.drawable.location_car, 90, 90);
            }
            //添加到百度地图中
            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerCar,
                    new LatLng(30.64877, 104.07603),
                    getBundle(WINDOW_TYPE_CAR, "Car1", "age: 23", "tel: 123"));
            overlayCars.add(overlay);//添加到集合中
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCar,
                    new LatLng(30.651007, 104.0766),
                    getBundle(WINDOW_TYPE_CAR, "Car2", "age: 2", "tel: 12"));
            overlayCars.add(overlay);
        } else {
            BaiduMapUtils.showOrRemoveOverlays(false, overlayCars);
            overlayCars = null;
        }
    }
    //摄像头
    private void showMarkerCameraList(View view) {
        if (overlayCameras == null) {
            overlayCameras = new ArrayList<>();
            if (markerCamera == null) {
                markerCamera = BaiduMapUtils.getMarkerOptions(R.drawable.location_camera, 90, 90);
            }
            //添加到百度地图中
            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera,
                    new LatLng(26.0, 108.0),
                    getBundle(WINDOW_TYPE_CAMERA, "Camera1", "age: 23", "tel: 123"));
            overlayCameras.add(overlay);//添加到集合中
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera,
                    new LatLng(30.70269, 103.99612),
                    getBundle(WINDOW_TYPE_CAMERA, "Camera2", "age: 2", "tel: 12"));
            overlayCameras.add(overlay);
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera,
                    new LatLng(30.6607, 104.06108),
                    getBundle(WINDOW_TYPE_CAMERA, "Camera3", "age: 33", "tel: 128"));
            overlayCameras.add(overlay);
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera,
                    new LatLng(30.711384, 103.98145),
                    getBundle(WINDOW_TYPE_CAMERA, "Camera4", "age: 55", "tel: 162"));
            overlayCameras.add(overlay);
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerCamera,
                    new LatLng(30.602964, 103.945305),
                    getBundle(WINDOW_TYPE_CAMERA, "Camera5", "age: 66", "tel: 172"));
            overlayCameras.add(overlay);
        } else {
            BaiduMapUtils.showOrRemoveOverlays(false, overlayCameras);
            overlayCameras = null;
        }
    }
    //显示所有桥
    private void showMarkerBridgeList(View view) {
        if (overlayBridges == null) {
            overlayBridges = new ArrayList<>();
            if (markerBridge == null) {
                markerBridge = BaiduMapUtils.getMarkerOptions(R.drawable.location_bridge, 90, 90);
            }
            //添加到百度地图中
            Overlay overlay = BaiduMapUtils.addOverlay(baiduMap, markerBridge,
                    new LatLng(30.624659, 104.10621),
                    getBundle(WINDOW_TYPE_BRIDGE, "Bridge1", "age: 23", "tel: 123"));
            overlayBridges.add(overlay);//添加到集合中
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerBridge,
                    new LatLng(30.738207, 104.11397),
                    getBundle(WINDOW_TYPE_BRIDGE, "Bridge2", "age: 2", "tel: 12"));
            overlayBridges.add(overlay);
            overlay = BaiduMapUtils.addOverlay(baiduMap, markerBridge,
                    new LatLng(30.629381, 104.205956),
                    getBundle(WINDOW_TYPE_BRIDGE, "Bridge3", "age: 33", "tel: 128"));
            overlayBridges.add(overlay);
        } else {
            BaiduMapUtils.showOrRemoveOverlays(false, overlayBridges);
            overlayBridges = null;
        }
    }

    /**
     * 定位回调监听
     * @see com.actor.myandroidframework.utils.baidu.MyLocationListener extends BDAbstractLocationListener
     */
    private BDAbstractLocationListener locationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String result = GsonUtils.toJson(bdLocation);
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
    private BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle extraInfo = marker.getExtraInfo();
            if (extraInfo == null) {
                toast("未获取到数据");
            } else {
                String infoWindowType = extraInfo.getString(OVERLAY_TYPE);
                if (infoWindowType != null) {
                    switch (infoWindowType) {
                        case WINDOW_TYPE_EVENT:
                            showEventInfoWindow(marker.getPosition(), extraInfo);
                            break;
                        case WINDOW_TYPE_PERSON:
                            showPersonInfoWindow(marker.getPosition(), extraInfo);
                            break;
                        case WINDOW_TYPE_REPOSITORY:
                            showRepositoryInfoWindow(marker.getPosition(), extraInfo);
                            break;
                        case WINDOW_TYPE_CAR:
                            showCarInfoWindow(marker.getPosition(), extraInfo);
                            break;
                        case WINDOW_TYPE_CAMERA:
                            showCameraInfoWindow(marker.getPosition(), extraInfo);
                            break;
                        case WINDOW_TYPE_BRIDGE:
                            showBridgeInfoWindow(marker.getPosition(), extraInfo);
                            break;
                    }
                }
            }
            return true;//若响应点击事件，返回true，否则返回false
        }
    };

    //中心红点信息窗
    private void showEventInfoWindow(LatLng latLng, @NonNull Bundle extraInfo) {
        //自定义布局
        infoWindow = BaiduMapUtils.getInfoWindow(getLayoutInflater(), R.layout.baidu_map_info_window_event, latLng, -90);
        View view = infoWindow.getView();
        TextView tvType = view.findViewById(R.id.tv_type);//类型
        view.findViewById(R.id.iv_close).setOnClickListener(infoWindowClickListener);//关闭
        view.findViewById(R.id.btn).setOnClickListener(infoWindowClickListener);//按钮
        TextView tvAddress = view.findViewById(R.id.tv_address);//地点
        TextView tvName = view.findViewById(R.id.tv_person);//name
        TextView tvPhone = view.findViewById(R.id.tv_phone);//电话

        tvType.setText("type: " + extraInfo.getString(OVERLAY_TYPE));
        tvAddress.setText(extraInfo.getString(arg0));
        tvName.setText(extraInfo.getString(arg1));
        tvPhone.setText(extraInfo.getString(arg2));
        BaiduMapUtils.showInfoWindow(baiduMap, infoWindow);//显示信息窗
    }
    //显示人员信息窗(可自定义, 这儿为了简便, 和上方一致)
    private void showPersonInfoWindow(LatLng latLng, @NonNull Bundle extraInfo) {
        showEventInfoWindow(latLng, extraInfo);
    }
    //显示仓库信息窗
    private void showRepositoryInfoWindow(LatLng latLng, @NonNull Bundle extraInfo) {
        showEventInfoWindow(latLng, extraInfo);
    }
    //显示车辆信息窗
    private void showCarInfoWindow(LatLng latLng, @NonNull Bundle extraInfo) {
        showEventInfoWindow(latLng, extraInfo);
    }
    //显示摄像头信息窗
    private void showCameraInfoWindow(LatLng latLng, @NonNull Bundle extraInfo) {
        showEventInfoWindow(latLng, extraInfo);
    }
    //显示桥信息窗
    private void showBridgeInfoWindow(LatLng latLng, @NonNull Bundle extraInfo) {
        showEventInfoWindow(latLng, extraInfo);
    }

    //infoWindow(相当于Dialog)里的按钮被点击之后
    private View.OnClickListener infoWindowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close://关闭 InfoWindow
                    baiduMap.hideInfoWindow(infoWindow);//隐藏信息窗
                    break;
                case R.id.btn:
                    toast("clicked btn");
                    break;
            }
        }
    };

    //停止定位
    private void stopLocation() {
        BaiduLocationUtils.unregisterListener(locationListener);
        BaiduLocationUtils.stop();
    }

    private Bundle getBundle(String type, String... extra) {
        Bundle bundle = new Bundle();
        bundle.putString(OVERLAY_TYPE, type);
        for (int i = 0; i < extra.length; i++) {
            bundle.putString("arg" + i, extra[i]);
        }
        return bundle;
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
        infoWindow = null;
        mapView.onDestroy();//MapView的生命周期与Activity同步
        BaiduMapUtils.removeMarkerClickListener(baiduMap, markerClickListener);
        BaiduMapUtils.recycleMarkerOption(markerLocation);
        BaiduMapUtils.recycleMarkerOption(markerPerson);
        BaiduMapUtils.recycleMarkerOption(markerRepository);
        BaiduMapUtils.recycleMarkerOption(markerCar);
        BaiduMapUtils.recycleMarkerOption(markerCamera);
        BaiduMapUtils.recycleMarkerOption(markerBridge);

        if (isNoEmpty(overlayPersons)) overlayPersons.clear();//人员覆盖物列表
        if (isNoEmpty(overlayRepositorys)) overlayRepositorys.clear();//仓库覆盖物列表
        if (isNoEmpty(overlayCars)) overlayCars.clear();//车辆覆盖物列表
        if (isNoEmpty(overlayCameras)) overlayCameras.clear();//摄像头覆盖物列表
        if (isNoEmpty(overlayBridges)) overlayBridges.clear();//摄像头覆盖物列表
    }
}
