package com.actor.map.gaode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.IndoorBuildingInfo;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ImageUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 高德地图工具类, 官网: <a href="https://lbs.amap.com/" target="_blank">高德地图开放平台</a> <br />
 * <ol>
 *     <li>
 *         添加应用并获取<a href="https://console.amap.com/dev" target="_blank">API Key</a>, <br />
 *         然后快速获取sha1签名, 可去
 *         <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures"
 *         target="_blank">Gitee 下载一个 "获取debug和release签名的sha1.bat" 文件</a>, 双击运行它, 然后输入发布版(release版)秘钥地址就能获取到sha1签名。
 *     </li>
 *     <li>
 *         <a href="https://lbs.amap.com/api/android-sdk/guide/create-project/android-studio-create-project#gradle_sdk"
 *         target="_blank">添加依赖</a> <br />
 *         //2.1.在Project的gradle文件中添加: <br />
 *         <code>
 *             allprojects {                                            <br />
 *             &emsp;&emsp; repositories {                              <br />
 *             &emsp;&emsp;&emsp;&emsp; jcenter() // 或者 mavenCentral() <br />
 *             &emsp;&emsp; }                                           <br />
 *             }                                                        <br />
 *         </code>
 *         //2.2.在 module 的 gradle 文件中添加:                         <br />
 *         //在 defaultConfig { 标签中添加ndk                            <br />
 *         <code>
 *             ndk {                                                    <br />
 *             &emsp;&emsp; //"armeabi", "armeabi-v7a", "x86", "arm64-v8a", "x86_64", 'mips', 'mips64' <br />
 *             &emsp;&emsp; abiFilters "armeabi-v7a", "arm64-v8a"       <br />
 *             }
 *         </code>
 *     </li>
 *     <li>
 *         //引入<a href="https://repo.maven.apache.org/maven2/com/amap/api/" target="_blank">最新版本的SDK(mavenCentral()仓库)</a>(不用全部引入, 按需引入)  <br />
 *         implementation 'com.amap.api:3dmap:latest.integration' //3D地图 <br />
 *         implementation 'com.amap.api:map2d:latest.integration' //2D地图 <br />
 *         implementation 'com.amap.api:navi-3dmap:latest.integration' //导航(5.0.0以后版本包含了3D地图SDK,就不用导入3dmap) <br />
 *         implementation 'com.amap.api:search:latest.integration' //搜索 <br />
 *         implementation 'com.amap.api:location:latest.integration' //定位 <br />
 *         <br />
 *         //或者指定版本(目前工具类使用的下方版本) <br />
 *         implementation 'com.amap.api:3dmap:9.7.0' //3D地图 <br />
 *         implementation 'com.amap.api:map2d:6.0.0' //2D地图 <br />
 *         implementation 'com.amap.api:navi-3dmap:9.8.0_3dmap9.6.2' //导航(5.0.0以后版本包含了3D地图SDK,就不用导入3dmap) <br />
 *         implementation 'com.amap.api:search:9.5.0' //搜索 <br />
 *         implementation 'com.amap.api:location:6.3.0' //定位 <br />
 *     </li>
 *     <li>
 *         在AndroidManifest.xml中添加
 *         <a href="https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention"
 *         target="_blank">&lt;meta-data</a>或调用 {@link GaoDe3DMapUtils#setApiKey(String) GaoDe3DMapUtils.setApiKey(String)}设置AK, (都可以,二选一) <br />
 *         在清单文件的&lt;application>标签里添加 &lt;meta-data 示例:  <br />
 *         <code>
 *             &lt;!--高德地图/定位设置AK，在&lt;pplication>标签中加入-->  <br />
 *             &lt;meta-data                                            <br />
 *             &emsp;&emsp; android:name="com.amap.api.v2.apikey"       <br />
 *             &emsp;&emsp; android:value="您申请的高德Api Key"/&gt;
 *         </code>
 *     </li>
 *     <li>
 *         清单文件中 ★★★不用★★★
 *         <a href="https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention#permission"
 *         target="_blank">添加权限</a>, 只要你导入这个模块的依赖, 就已经添加好了!
 *     </li>
 *     <li>
 *         <a href="https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention#obfuscated-code"
 *         target="_blank">代码混淆</a> ★★★已经在模块中完成★★★, 如果你需要混淆代码, 打开 minifyEnabled true 即可.
 *     </li>
 *     <li>
 *         使用前, 先初始化: <br />
 *         //高德地图, 先同意隐私政策 <br />
 *         {@link GaoDe3DMapUtils#updatePrivacyShow(Context, boolean, boolean) GaoDe3DMapUtils.updatePrivacyShow(Context, boolean, boolean)} <br />
 *         {@link GaoDe3DMapUtils#updatePrivacyAgree(Context, boolean) GaoDe3DMapUtils.updatePrivacyAgree(Context, boolean)} <br />
 *     </li>
 *     <li>
 *         <a href="https://lbs.amap.com/api/android-sdk/guide/create-map/show-map#map-view" target="_blank">常用地图容器</a> <br />
 *         {@link com.amap.api.maps.MapView}                                                                     <br />
 *         {@link MapView#onCreate(Bundle)} //在activity执行onCreate时执行，创建地图                                <br />
 *         {@link MapView#onResume()} //在activity执行onResume时执行，重新绘制加载地图                               <br />
 *         {@link MapView#onPause()} //在activity执行onPause时执行，暂停地图的绘制                                  <br />
 *         {@link MapView#onSaveInstanceState(Bundle)} //在activity执行onSaveInstanceState时执行，保存地图当前的状态 <br />
 *         {@link MapView#onDestroy()} //在activity执行onDestroy时执行，销毁地图                                    <br />
 *         <br />
 *
 *         {@link com.autonavi.amap.mapcore.interfaces.IAMap} <br />
 *         <br />
 *
 *         GLSurfaceView: <br />
 *         {@link com.amap.api.maps.MapFragment} extends android.app.Fragment <br />
 *         {@link com.amap.api.maps.SupportMapFragment} extends android.support.v4.app.Fragment <br />
 *         <br />
 *
 *         TextureView, 使用场景: <br />
 *         您将MapView与其他的GLSurfaceView（比如相机）叠加展示，或者是在ScrollView中加载地图时，
 *         建议使用TextureMapView及SupportTextureMapFragment来展示地图，
 *         可以有效解决 GLSurfaceView 叠加时出现的穿透、滚动黑屏等问题。 <br />
 *         {@link com.amap.api.maps.TextureMapView} <br />
 *         {@link com.amap.api.maps.TextureMapFragment} extends Fragment <br />
 *         {@link com.amap.api.maps.TextureSupportMapFragment} extends android.support.v4.app.Fragment
 *     </li>
 * </ol>
 *
 * @author : ldf
 * date       : 2020/8/5 on 16:39
 * @version 1.0
 */
public class GaoDe3DMapUtils {

    public static final String GAODE_PACKAGE_NAME = "com.autonavi.minimap";//高德地图包名

    /**
     * 更新隐私合规状态,需要在初始化地图之前完成，如果合规不通过地图将白屏
     * @param  isContains: 隐私权政策是否包含高德开平隐私权政策  true是包含
     * @param  isShow: 隐私权政策是否弹窗展示告知用户 true是展示
     * @since  8.1.0
     */
    public static void updatePrivacyShow(Context context, boolean isContains, boolean isShow) {
        MapsInitializer.updatePrivacyShow(context, isContains, isShow);
    }

    /**
     * 更新同意隐私状态,需要在初始化地图之前完成，如果合规不通过地图将白屏
     * @param context: 上下文
     * @param isAgree: 隐私权政策是否取得用户同意  true是用户同意
     * @since 8.1.0
     */
    public static void updatePrivacyAgree(Context context, boolean isAgree) {
        MapsInitializer.updatePrivacyAgree(context, isAgree);
    }

    /**
     * 设置apiKey, 如果已经在清单文件中添加了<meta-data , 就不用调用这个方法.
     * @param apiKey 在官网申请的apiKey
     */
    public static void setApiKey(String apiKey) {
        MapsInitializer.setApiKey(apiKey);
    }

    /**
     * 初始化AMap, 注意调用MapView的生命周期方法:
     * @see MapView#onCreate(Bundle)
     * @see MapView#onResume() 当activity恢复时需调用 mapView.onResume()
     * @see MapView#onPause() 当activity挂起时需调用 mapView.onPause()
     * @see MapView#onSaveInstanceState(Bundle) 保存地图当前的状态
     * @see MapView#onDestroy() 当activity销毁时需调用 mapView.destroy()
     * @param mapView 高德地图MapView
     * @param mapType 设置地图样式为普通,有三种, 示例: AMap.MAP_TYPE_NORMAL
     * @param zoomLevel 缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。, 示例: 15
     * @return aMap
     */
    public static AMap initGaoDeMap(MapView mapView, int mapType, @FloatRange(from = 3, to = 19) float zoomLevel) {
        AMap aMap = mapView.getMap();
        aMap.setMapType(mapType);//设置地图样式为普通,有三种
        //设置希望展示的地图缩放级别
        zoomTo(aMap, zoomLevel);
        return aMap;
    }

    /**
     * https://lbs.amap.com/api/android-sdk/guide/create-map/show-map
     * 获取地图控制器对象
     */
    public static AMap getAmap(MapView mapView) {
        return mapView.getMap();
    }

    public static AMap getAmap(TextureMapView mapView) {
        return mapView.getMap();
    }

    /**
     * 缩放地图到指定的缩放级别
     * @param zoomLevel 地图的缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。, 示例: 15
     */
    public static void zoomTo(AMap aMap, @FloatRange(from = 3, to = 19) float zoomLevel) {
        CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(zoomLevel);
        aMap.animateCamera(mCameraUpdate);  //带有移动过程的动画
//        aMap.moveCamera(mCameraUpdate);   //直接移动过去，不带移动过程动画
    }

    /**
     * 设置Map中心位置, 让地图移动到latLng这个点为中心的位置
     * @param aMap 高德地图
     * @param latLng 坐标
     */
    public static void setMapLocation(AMap aMap, LatLng latLng) {
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraPosition cameraPosition = new CameraPosition(latLng,
                getCurrentZoomLevel(aMap), getCurrentTile(aMap), getCurrentBearing(aMap));
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.animateCamera(mCameraUpdate);
    }

    /**
     *
     * 显示"定位蓝点"
     * @param interval 设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。2000L
     * @param locationEnable 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
     */
    public static void setMyLocationEnabled(AMap aMap, long interval, boolean locationEnable) {
        MyLocationStyle locationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        locationStyle.interval(interval);
        aMap.setMyLocationStyle(locationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(locationEnable);
    }

    /**
     * @see AMap#MAP_TYPE_NORMAL    矢量地图模式
     * @see AMap#MAP_TYPE_SATELLITE 卫星地图模式
     * @see AMap#MAP_TYPE_NIGHT     夜景地图模式
     * @see AMap#MAP_TYPE_NAVI      导航地图模式
     * @see AMap#MAP_TYPE_BUS       ?
     */
    public static void setMapType(AMap aMap, int mapType) {
        aMap.setMapType(mapType);
    }

    /**
     * 清空 element
     */
    public static void clear(AMap aMap) {
        aMap.clear();
    }

    /**
     * 获取TextOptions (文字覆盖物选项)
     * 注意:
     *   1. TextOptions应该复用, 只是表示一个文字而已, 可重复设置坐标再添加到地图, 写成全局变量, 例:
     *       textOption.position(latLng);
     *       aMap.addText(textOption);      //会在地图新增一个文字
     *   2.在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions)}//回收图片
     *
     * @param latLng 坐标
     * @param text 文字
     * @param textColor 字体颜色
     * @param backgroundColor 字体背景颜色
     * @param object 可存入一些标志性数据等
     * @return 高德Marker
     */
    public static TextOptions getTextOptions(@Nullable LatLng latLng, String text, @ColorInt int textColor,
                                             @ColorInt int backgroundColor, @Nullable Object object) {
        return new TextOptions()
                .position(latLng)
                .text(text)
                .fontColor(textColor)               //默认: Color.BLACK
                .backgroundColor(backgroundColor)   //默认: Color.WHITE
                .fontSize(20)                       //字体大小, 默认: 20
                .rotate(0.0F)                       //旋转
                .align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                .zIndex(0.0F)
                .typeface(Typeface.DEFAULT)         //Typeface.DEFAULT_BOLD
                .visible(true)                      //默认true
                .setObject(object);
    }

    /**
     * 获取MarkerOptions(Marker覆盖物选项)
     * 注意:
     *   1. MarkerOptions应该复用, 只是表示一个图标而已, 可重复设置坐标再添加到地图, 写成全局变量, 例:
     *       markerOption.position(latLng);
     *       markerOption.extraInfo(Bundle bundle);
     *       aMap.addMarker(markerOption);//会在地图新增一个latLng
     *   2.在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions)}//回收图片
     *
     * @param resId 图片resId
     * @param width marker宽度, 单位px, 例: 90
     * @param height marker高度, 单位px, 例: 90
     * @return 高德Marker
     */
    public static MarkerOptions getMarkerOptions(@DrawableRes int resId, int width, int height) {
        Bitmap bitmap = ImageUtils.getBitmap(resId);
        Bitmap scale = ImageUtils.scale(bitmap, width, height, true);
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromBitmap(scale);
        return getMarkerOptions(bitmapDesc);
    }
    /**
     * 获取MarkerOptions(Marker覆盖物选项)
     * @param descriptor 获取方式:
     *                   @see BitmapDescriptorFactory#defaultMarker()
     *                   @see BitmapDescriptorFactory#defaultMarker(float)
     *                   @see BitmapDescriptorFactory#fromAsset(String)
     *                   @see BitmapDescriptorFactory#fromBitmap(Bitmap)
     *                   @see BitmapDescriptorFactory#fromFile(String)
     *                   @see BitmapDescriptorFactory#fromPath(String)
     *                   @see BitmapDescriptorFactory#fromResource(int) //R.drawable.xxx
     *                   @see BitmapDescriptorFactory#fromView(View)
     */
    public static MarkerOptions getMarkerOptions(BitmapDescriptor descriptor) {
        return new MarkerOptions().icon(descriptor);
    }

    /**
     * 在高德地图上添加一个覆盖物 <br />
     * 高德地图的 InfoWindow 也在 Marker 里设置!!!
     * @param markerOption 见 {@link #getMarkerOptions(int, int, int)}
     * @param latLng 坐标, 可传null(默认在中心位置)
     * @param draggable 是否能拖拽
     * @param title InfoWindow 的标题      . 如果 Marker 上不要 InfoWindow, 或不显示标题, 可传null
     * @param snippet InfoWindow 的详细信息. 如果 Marker 上不要 InfoWindow, 或不显示内容, 可传null
     * @param object 可以存入标记信息(例json), 比如这个Marker是干啥的, 点击之后获取object判断这个Marker怎么操作等.
     * @return 返回覆盖物, 注意在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions)}//回收图片
     */
    public static Marker addMarker(@NonNull AMap aMap, @NonNull MarkerOptions markerOption,
                                   @Nullable LatLng latLng, boolean draggable, @Nullable String title,
                                   @Nullable String snippet, @Nullable Object object) {
        markerOption.position(latLng)
//                .icon()               //icon由上方那个方法给出
                .draggable(draggable)   //是否能拖拽
                .title(title)           //InfoWindow 显示的标题
                .snippet(snippet)       //InfoWindow 显示的内容
                .setInfoWindowOffset(0, 0)  //偏移量
                .alpha(1.0F)
                .altitude(0.0F)
                .anchor(0.5F, 1.0F)
                .autoOverturnInfoWindow(false)
                .belowMaskLayer(false)
                .displayLevel(5)
//                .icons(ArrayList<BitmapDescriptor> var1)
                .infoWindowEnable(true)
                .period(20)
                .rotateAngle(0)
//                .rotatingIcons(ArrayList<BitmapDescriptor> var1, float var2)
                .setFlat(false)
                .setGps(false)
                .visible(true)
                .zIndex(0.0F)
//                .clone()
        ;
        Marker marker = aMap.addMarker(markerOption);//会在地图新增一个latLng
        marker.setObject(object);
        return marker;
    }

    /**
     * fixme
     *
     * 每次都new InfoWindow(), 因为如果复用infoWindow.setPosition()的话, 会造成infoWindow重叠现象
     * 注意:
     *   1.infoWindow.getView();//返回布局, 可以findViewById
     *
     * @param infoWindowAdapter 自定义 InfoWindow 的样式, 重写2个方法:      <br />
     *                          &emsp; //1.可以根据marker的内容, 初始化返回一个InfoWindow的View, 可返回null  <br />
     *                          &emsp; public View getInfoWindow(Marker marker); <br />
     *                          <br />
     *                          &emsp; //2.可以根据marker的内容, 初始化返回一个InfoContent的View, 可返回null  <br />
     *                          &emsp; public View getInfoContents(Marker marker); <br />
     *                          <br />
     *                          &emsp; 3.如果这个adapter传入null: 则使用默认InfoWindow样式
     */
    public static void setInfoWindowAdapter(@NonNull AMap aMap,
                                            @Nullable AMap.InfoWindowAdapter infoWindowAdapter) {
        aMap.setInfoWindowAdapter(infoWindowAdapter);
    }

    /**
     * @param markerOption 回收 MarkerOptions
     */
    public static void recycleMarkerOption(MarkerOptions markerOption) {
        if (markerOption != null) {
            BitmapDescriptor icon = markerOption.getIcon();
            if (icon != null) icon.recycle();
        }
    }
    /**
     * Marker覆盖物点击事件
     * 注意调用方法: {@link #removeOnMarkerClickListener(AMap, AMap.OnMarkerClickListener)}
     */
    public static void addOnMarkerClickListener(AMap aMap, AMap.OnMarkerClickListener listener) {
        aMap.setOnMarkerClickListener(listener);
    }
    public static void removeOnMarkerClickListener(AMap aMap, AMap.OnMarkerClickListener listener) {
        aMap.removeOnMarkerClickListener(listener);
    }

    /**
     * Marker覆盖物 自定义InfoWindow点击事件
     */
    public static void setOnInfoWindowClickListener(AMap aMap, AMap.OnInfoWindowClickListener listener) {
        aMap.setOnInfoWindowClickListener(listener);
    }
    public static void removeInfoWindowClickListener(AMap aMap, AMap.OnInfoWindowClickListener listener) {
        aMap.removeOnInfoWindowClickListener(listener);
    }


    /**
     * 显示/移除 覆盖物
     * 移除该覆盖物, 移除后再调用marker.setVisible(true);无效!!!
     *
     * @see #setMarkerVisible(boolean, Marker...)
     * @see #setMarkerVisible(boolean, List)
     *
     * @param showOrRemove true显示, false移除
     * @param markers 覆盖物, marker = aMap.addMarker(markerOptions);
     */
    public static void showOrRemoveMarkers(boolean showOrRemove, List<Marker> markers) {
        if (markers != null && !markers.isEmpty()) {
            for (Marker marker : markers) {
                if (marker != null) {
                    if (showOrRemove) {
                        marker.setVisible(true);
                    } else {
                        marker.remove();//移除该覆盖物, 移除后再调用marker.setVisible(true);无效!!!
                    }
                }
            }
            if (!showOrRemove) markers.clear();//如果移除, 那就清空
        }
    }

    /**
     * 设置覆盖物显示状态
     *
     * @see #showOrRemoveMarkers(boolean, List)
     * @param visible 是否显示
     * @param markers 覆盖物, marker = aMap.addMarker(markerOptions);
     * @deprecated 隐藏后, 点击事件还在!!!
     */
    @Deprecated
    public static void setMarkerVisible(boolean visible, Marker... markers) {
        if (markers != null && markers.length > 0) {
            setMarkerVisible(visible, Arrays.asList(markers));
        }
    }
    /**
     * 设置覆盖物显示状态
     *
     * @see #showOrRemoveMarkers(boolean, List)
     * @param visible 是否显示
     * @param markers 覆盖物, marker = aMap.addMarker(markerOptions);
     * @deprecated 隐藏后, 点击事件还在!!!
     */
    @Deprecated
    public static void setMarkerVisible(boolean visible, List<Marker> markers) {
        if (markers != null && markers.size() > 0) {
            for (Marker marker : markers) {
                if (marker != null) marker.setVisible(visible);//隐藏后, 点击事件还在!!!
            }
        }
    }

    /**
     * 显示 InfoWindow
     */
    public static void showInfoWindow(Marker marker) {
        marker.showInfoWindow();
    }

    /**
     * 隐藏 InfoWindow
     */
    public static void hideInfoWindow(Marker marker) {
        marker.hideInfoWindow();
    }

    //个性化地图, 设置自定义样式
    public static void setCustomMapStyle(AMap aMap, byte[] bytes, boolean enable) {
        CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();
        mapStyleOptions.setStyleData(bytes);
        mapStyleOptions.setEnable(enable);
//        mapStyleOptions.setStyleId("your id");
        aMap.setCustomMapStyle(mapStyleOptions);
    }

    /**
     * 添加线
     * @param aMap
     * @param latLngs
     * @param width 线宽? 例: 5
     */
    public static Polyline addPolyline(AMap aMap, Iterable<LatLng> latLngs, float width) {
        return aMap.addPolyline((new PolylineOptions())
                .addAll(latLngs)
                .width(width));
    }

    /**
     * 移动到以某点为中心
     * @param scaleLevel 缩放级别 3 ~ 20
     *  @see AMap#getMaxZoomLevel()
     *  @see AMap#getMinZoomLevel()
     */
    public static void moveCamera(AMap aMap, LatLng latLng, @IntRange(from = 10, to = 18) int scaleLevel) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, scaleLevel));
        //aMap.animateCamera
    }

    /**
     * 获取最小缩放级别, 如果没有手动设置, 默认3
     */
    public static float getMinZoomLevel(AMap aMap) {
        return aMap.getMinZoomLevel();
    }

    /**
     * 获取最大缩放级别, 如果没有手动设置, 默认20
     */
    public static float getMaxZoomLevel(AMap aMap) {
        return aMap.getMaxZoomLevel();
    }

    /**
     * 设置当前缩放级别
     */
    public static void setCurrentZoomLevel(AMap aMap, float level) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        LatLng target = cameraPosition.target;
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, level));
//        aMap.moveCamera();
    }

    /**
     * 获取当前缩放级别
     */
    public static float getCurrentZoomLevel(AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.zoom;
    }

    /**
     * 获取当前中心蓝点坐标
     */
    public static LatLng getCurrentLatLng(AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.target;
    }

    /**
     * 获取目标可视区域的倾斜度，以角度为单位。
     * 俯仰角0°~45°（垂直与地图时为0）
     */
    public static float getCurrentTile(AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.tilt;
    }

    /**
     * 获取可视区域指向的方向，以角度为单位，从正北向逆时针方向计算，从0 度到360 度。
     */
    public static float getCurrentBearing(AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.bearing;
    }

    /**
     * "室内地图"是否显示, 默认false
     * @see GaoDeUiSettingUtils#setIndoorSwitchEnabled(AMap, boolean)
     * @param aMap
     * @param enable
     */
    public static void showIndoorMap(AMap aMap, boolean enable) {
        aMap.showIndoorMap(enable);
    }

    /**
     * 设置室内地图回调监听, 室内地图有可能有很多层的地图
     * 回调类: {@link com.amap.api.maps.model.IndoorBuildingInfo}
     *  @see com.amap.api.maps.model.IndoorBuildingInfo#poiid   楼所在id, 通过这个判断是否是同一个楼
     *  @see com.amap.api.maps.model.IndoorBuildingInfo#floor_names 所有楼层名称
     *  @see com.amap.api.maps.model.IndoorBuildingInfo#activeFloorName 当前楼层名称?
     *
     * 设置跳到某一层的地图:
     *  @see AMap#setIndoorBuildingInfo(IndoorBuildingInfo)
     */
    public static void setOnIndoorBuildingActiveListener(AMap aMap, AMap.OnIndoorBuildingActiveListener listener) {
        aMap.setOnIndoorBuildingActiveListener(listener);
    }

    //一像素代表多少米
    public static float getScalePerPixel(AMap aMap) {
        return aMap.getScalePerPixel();
    }

    //获取版本名称
    public static String getVersion() {
        return MapsInitializer.getVersion();
    }

    /**
     * 路径规划: https://lbs.amap.com/api/amap-mobile/guide/android/route
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
}
