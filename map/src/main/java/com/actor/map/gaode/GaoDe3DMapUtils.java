package com.actor.map.gaode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
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
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.blankj.utilcode.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 高德地图工具类, 官网: <a href="https://lbs.amap.com/" target="_blank">高德地图开放平台</a>
 * <a href="https://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html" target="_blank">参考手册</a> <br />
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
 *             &emsp;&emsp; //"armeabi-v7a", "arm64-v8a", "armeabi", "x86", "x86_64", "mips", "mips64" <br />
 *             &emsp;&emsp; abiFilters "armeabi-v7a", "arm64-v8a"       <br />
 *             }
 *         </code>
 *     </li>
 *     <li>
 *         //引入<a href="https://repo.maven.apache.org/maven2/com/amap/api/" target="_blank">最新版本的SDK(mavenCentral()仓库)</a>(不用全部引入, 按需引入)  <br />
 *         implementation 'com.amap.api:3dmap:latest.integration' //3D地图(9.3.0以后版本包含了location定位SDK,就不用导入location) <br />
 *         implementation 'com.amap.api:map2d:latest.integration' //2D地图 <br />
 *         implementation 'com.amap.api:navi-3dmap:latest.integration' //导航(5.0.0以后版本包含了3D地图SDK,就不用导入3dmap) <br />
 *         implementation 'com.amap.api:search:latest.integration' //搜索 <br />
 *         implementation 'com.amap.api:location:latest.integration' //定位 <br />
 *         <br />
 *         //或者指定版本(目前工具类使用的下方版本) <br />
 *         implementation 'com.amap.api:3dmap:9.7.0' //3D地图(9.3.0以后版本包含了location定位SDK,就不用导入location) <br />
 *         implementation 'com.amap.api:map2d:6.0.0' //2D地图 <br />
 *         implementation 'com.amap.api:navi-3dmap:9.8.0_3dmap9.6.2' //导航(5.0.0以后版本包含了3D地图SDK,就不用导入3dmap) <br />
 *         implementation 'com.amap.api:search:9.7.0' //搜索 <br />
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
 *         {@link com.amap.api.maps.MapView}                                                                      <br />
 *         {@link MapView#onCreate(Bundle)} //在activity执行onCreate时执行，创建地图, if不调用会导致地图空白!!!       <br />
 *         {@link MapView#onResume()} //在activity执行onResume时执行，重新绘制加载地图                               <br />
 *         {@link MapView#onPause()} //在activity执行onPause时执行，暂停地图的绘制                                   <br />
 *         {@link MapView#onLowMemory()} //在activity执行onLowMemory时执行                                         <br />
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

    /**
     * 更新隐私合规状态,需要在初始化地图之前完成，如果合规不通过地图将白屏
     * @param  isContains: 隐私权政策是否包含高德开平隐私权政策  true是包含
     * @param  isShow: 隐私权政策是否弹窗展示告知用户 true是展示
     * @since  8.1.0
     */
    public static void updatePrivacyShow(@NonNull Context context, boolean isContains, boolean isShow) {
        MapsInitializer.updatePrivacyShow(context, isContains, isShow);
    }

    /**
     * 更新同意隐私状态,需要在初始化地图之前完成，如果合规不通过地图将白屏
     * @param context: 上下文
     * @param isAgree: 隐私权政策是否取得用户同意  true是用户同意
     * @since 8.1.0
     */
    public static void updatePrivacyAgree(@NonNull Context context, boolean isAgree) {
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
    public static AMap initGaoDeMap(@NonNull MapView mapView, int mapType, @FloatRange(from = 3, to = 19) float zoomLevel) {
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
    public static AMap getAmap(@NonNull MapView mapView) {
        return mapView.getMap();
    }

    public static AMap getAmap(@NonNull TextureMapView mapView) {
        return mapView.getMap();
    }

    /**
     * 缩放地图到指定的缩放级别
     * @param zoomLevel 地图的缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。, 示例: 15
     */
    public static void zoomTo(@NonNull AMap aMap, @FloatRange(from = 3, to = 19) float zoomLevel) {
        CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(zoomLevel);
        aMap.animateCamera(mCameraUpdate);  //带有移动过程的动画
//        aMap.moveCamera(mCameraUpdate);   //直接移动过去，不带移动过程动画
    }

    /**
     * 设置Map中心位置, 让地图移动到latLng这个点为中心的位置 https://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html
     * @param aMap 高德地图
     * @param latLng 坐标
     */
    public static void setMapLocation(@NonNull AMap aMap, @NonNull LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition(latLng,  //目标位置的屏幕中心点经纬度坐标。
                getCurrentZoomLevel(aMap),                          //目标可视区域的缩放级别。
                getCurrentTile(aMap),                               //目标可视区域的倾斜度，以角度为单位。 俯仰角0°~45°（垂直于地图时为0）
                getCurrentBearing(aMap));                           //可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度。(正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.animateCamera(mCameraUpdate);
    }

    /**
     * "定位小蓝点"（当前位置）的绘制样式类。https://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html
     * @param interval 设置发起定位请求的时间间隔，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将执行单次定位。
     * @param locationEnable 设置是否打开定位图层（myLocationOverlay）。设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
     */
    public static void setMyLocationEnabled(@NonNull AMap aMap, long interval, boolean locationEnable) {
        MyLocationStyle locationStyle = new MyLocationStyle()
                //设置我的位置展示模式:
                // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                //MyLocationStyle.LOCATION_TYPE_SHOW            //只定位。
                //MyLocationStyle.LOCATION_TYPE_LOCATE          //定位、且将视角移动到地图中心点。
                //MyLocationStyle.LOCATION_TYPE_MAP_ROTATE      //定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。
                //MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE //定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。(默认)
                .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
                .interval(interval);                //设置发起定位请求的时间间隔，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将执行单次定位。
        aMap.setMyLocationStyle(locationStyle);     //设置定位图层（myLocationOverlay）的样式。
        aMap.setMyLocationEnabled(locationEnable);  //设置是否打开定位图层（myLocationOverlay）。
    }

    /**
     * 设置地图模式。https://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html
     * @param mapType 地图模式 <br />
     * @see AMap#MAP_TYPE_NORMAL    普通矢量地图
     * @see AMap#MAP_TYPE_SATELLITE 卫星地图
     * @see AMap#MAP_TYPE_NIGHT     黑夜地图，夜间模式
     * @see AMap#MAP_TYPE_NAVI      导航模式
     * @see AMap#MAP_TYPE_BUS       公交模式
     */
    public static void setMapType(@NonNull AMap aMap, int mapType) {
        aMap.setMapType(mapType);
    }

    /**
     * 获取TextOptions (文字覆盖物选项) <br />
     * 注意:
     * <ol>
     *     <li>TextOptions应该复用, 只是表示一个文字配置选项而已, 可重复设置坐标再添加到地图. 示例: <br />
     *         textOption.position(latLng); <br />
     *         aMap.addText(textOption);      //会在地图新增一个文字 <br />
     *     </li>
     *     <li>在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions...)}//回收图片</li>
     * </ol>
     * @param text 文字
     * @param textColor 字体颜色
     * @param backgroundColor 字体背景颜色
     * @param object 可存入一些标志性数据等
     * @return 高德Marker
     */
    public static TextOptions getTextOptions(String text, @ColorInt int textColor,
                                             @ColorInt int backgroundColor, @Nullable Object object) {
        return new TextOptions()
//                .position(latLng)
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
     * <ol>
     *     <li>
     *         MarkerOptions应该复用, 只是表示一个图标配置项而已, 可重复设置坐标再添加到地图, 例: <br />
     *         markerOption.position(latLng).extraInfo(Bundle bundle); <br />
     *         aMap.addMarker(markerOption);//会在地图新增一个latLng
     *     </li>
     *     <li>在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions...)}//回收图片</li>
     * </ol>
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
     * <a href="https://a.amap.com/lbs/static/unzip/Android_Map_Doc/3D/com/amap/api/maps/model/MarkerOptions.html" target="_blank">MarkerOptions: Marker 的选项类。</a>
     * <a href="https://a.amap.com/lbs/static/unzip/Android_Map_Doc/3D/com/amap/api/maps/model/Marker.html" target="_blank">地图 Marker 覆盖物</a> <br />
     * 高德地图的 InfoWindow 在 Marker 里设置!!! (跟百度地图不一样)
     * @param markerOption 见 {@link #getMarkerOptions(int, int, int)}
     * @param latLng 坐标, 可传null(默认在中心位置)
     * @param infoWindowEnable 设置Marker覆盖物的InfoWindow是否允许显示,默认为true
     * @param title InfoWindow 的标题      . 如果 Marker 上不要 InfoWindow, 或不显示标题, 可传null
     * @param snippet InfoWindow 的片段(详细信息). 如果 Marker 上不要 InfoWindow, 或不显示内容, 可传null
     * @param object 设置Marker覆盖物的附加信息对象。可以存入标记信息(例json/Object), 比如这个Marker是干啥的, 点击之后获取object判断这个Marker怎么操作等.
     * @return 返回覆盖物, 注意在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions...)}//回收图片
     */
    public static Marker addMarker(@NonNull AMap aMap, @NonNull MarkerOptions markerOption,
                                   @Nullable LatLng latLng, boolean infoWindowEnable, @Nullable String title,
                                   @Nullable String snippet, @Nullable Object object) {
        markerOption.alpha(1.0F)            //设置Marker覆盖物的透明度
                .altitude(0.0F)             //marker的海拔
                .anchor(0.5F, 1.0F)         //设置Marker覆盖物的锚点比例。
                .draggable(false)           //设置Marker覆盖物是否可拖拽。默认false
//                .icon(BitmapDescriptor)   //设置Marker覆盖物的图标。
//                .icons(ArrayList<BitmapDescriptor> var1)  //设置Marker覆盖物的动画帧图标列表，多张图片模拟gif的效果。
                .infoWindowEnable(infoWindowEnable) //设置Marker覆盖物的InfoWindow是否允许显示,默认为true
                .period(20)                 //设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快。
                .position(latLng)           //设置Marker覆盖物的位置坐标。
                .rotateAngle(0.0F)          //设置Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
                .setFlat(false)             //设置Marker覆盖物是否平贴地图。默认false
                .setGps(false)              //设置Marker覆盖物的坐标是否是Gps，默认为false。
                .setInfoWindowOffset(0, 0)  //设置Marker覆盖物的InfoWindow相对Marker的偏移。
                .snippet(snippet)           //设置 Marker覆盖物的 文字描述 (InfoWindow 显示的内容)
                .title(title)               //设置 Marker覆盖物 的标题 (InfoWindow 显示的标题)
                .visible(true)              //设置Marker覆盖物是否可见。
                .zIndex(0.0F)               //设置Marker覆盖物 zIndex。
                //以下api, 文档没找到说明...
                .autoOverturnInfoWindow(false)
                .belowMaskLayer(false)
                .displayLevel(5)
                .rotatingIcons((ArrayList<BitmapDescriptor>) null, (float) 0)
//                .clone()
        ;
        Marker marker = aMap.addMarker(markerOption);//会在地图新增一个latLng
        marker.setObject(object);
        return marker;
    }

    /**
     * 设置marker的信息窗口定制接口。<br />
     * 当单击某个marker时，如果该{@link Marker#isInfoWindowEnable()}=true,
     * 且调用{@link Marker#showInfoWindow()}, 则不管marker的Title和Snippet是否=null，
     * 都会依次触发getInfoWindow()和getInfoContents()回调。
     * @param infoWindowAdapter 自定义 InfoWindow 的样式, 重写2个方法:      <br />
     *                          //1.可以根据marker的内容, 初始化返回一个InfoWindow的View, 可返回null  <br />
     *                          public View getInfoWindow(Marker marker); <br />
     *                          <br />
     *                          //2.可以根据marker的内容, 初始化返回一个InfoContent的View, 可返回null  <br />
     *                          public View getInfoContents(Marker marker); <br />
     *                          <br />
     *                          3.如果这个adapter传入null: 则使用默认InfoWindow样式
     */
    public static void setInfoWindowAdapter(@NonNull AMap aMap, @Nullable AMap.InfoWindowAdapter infoWindowAdapter) {
        aMap.setInfoWindowAdapter(infoWindowAdapter);
    }

    /**
     * Marker覆盖物点击事件
     * 注意调用方法: {@link #removeOnMarkerClickListener(AMap, AMap.OnMarkerClickListener)}
     */
    public static void setOnMarkerClickListener(@NonNull AMap aMap, AMap.OnMarkerClickListener listener) {
        aMap.setOnMarkerClickListener(listener);
    }

    /**
     * 设置marker的信息窗口点击事件监听接口。(这儿指的是默认的InfoWindow)
     */
    public static void setOnInfoWindowClickListener(@NonNull AMap aMap, AMap.OnInfoWindowClickListener listener) {
        aMap.setOnInfoWindowClickListener(listener);
    }
    public static void removeOnInfoWindowClickListener(@NonNull AMap aMap, AMap.OnInfoWindowClickListener listener) {
        aMap.removeOnInfoWindowClickListener(listener);
    }


    /**
     * 设置覆盖物显示状态
     * @param visible 是否显示
     * @param markers 覆盖物
     */
    public static void setMarkerVisible(boolean visible, @NonNull Marker... markers) {
        if (markers != null && markers.length > 0) {
            for (Marker marker : markers) if (marker != null) marker.setVisible(visible);
        }
    }
    /**
     * 设置覆盖物显示状态
     * @param visible 是否显示
     * @param markers 覆盖物
     */
    public static void setMarkerVisible(boolean visible, @NonNull List<Marker> markers) {
        if (markers != null && !markers.isEmpty()) {
            for (Marker marker : markers) if (marker != null) marker.setVisible(visible);
        }
    }

    /**
     * 删除当前marker。
     * @param markers 覆盖物
     */
    public static void removeMarkers(@NonNull List<Marker> markers) {
        if (markers != null && !markers.isEmpty()) {
            for (Marker marker : markers) if (marker != null) marker.remove();
            markers.clear();
        }
    }

    /**
     * 显示 InfoWindow
     */
    public static void showInfoWindow(@NonNull Marker marker) {
        marker.showInfoWindow();
    }

    /**
     * 隐藏 InfoWindow
     */
    public static void hideInfoWindow(@NonNull Marker marker) {
        marker.hideInfoWindow();
    }

    /**
     * 移动到以某点为中心
     * @param latLng 坐标
     * @param scaleLevel 缩放级别 3 ~ 20
     * @param animate 是否以动画的形式移动
     *  @see AMap#getMaxZoomLevel()
     *  @see AMap#getMinZoomLevel()
     */
    public static void moveCamera(@NonNull AMap aMap, @NonNull LatLng latLng,
                                  @FloatRange(from = 3F, to = 18F) float scaleLevel, boolean animate) {
        if (animate) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, scaleLevel));
        } else aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, scaleLevel));
    }

    /**
     * 获取最小缩放级别, 如果没有手动设置, 默认3
     */
    public static float getMinZoomLevel(@NonNull AMap aMap) {
        return aMap.getMinZoomLevel();
    }

    /**
     * 获取最大缩放级别, 如果没有手动设置, 默认20
     */
    public static float getMaxZoomLevel(@NonNull AMap aMap) {
        return aMap.getMaxZoomLevel();
    }

    /**
     * 获取当前缩放级别
     */
    public static float getCurrentZoomLevel(@NonNull AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.zoom;
    }

    /**
     * 设置当前缩放级别
     */
    public static void setCurrentZoomLevel(@NonNull AMap aMap, float level) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        LatLng target = cameraPosition.target;
        moveCamera(aMap, target, level, true);
    }

    /**
     * 获取当前中心蓝点坐标
     */
    public static LatLng getCurrentLatLng(@NonNull AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.target;
    }

    /**
     * <a href="https://a.amap.com/lbs/static/unzip/Android_Map_Doc/3D/com/amap/api/maps/model/CustomMapStyleOptions.html" target="_blank">自定义样式属性集</a>
     * (个性化地图, 设置自定义样式)
     */
    public static void setCustomMapStyle(@NonNull AMap aMap, byte[] bytes) {
        CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();
        mapStyleOptions.setEnable(true)         //设置是否开启底图自定义样式， 默认为开启
                .setStyleData(bytes)            //自定义样式二进制，使用二进制可以更快加载出自定义样式，如果设置了则不会读取.setStyleDataPath(String)
                .setStyleDataPath((String) null)//自定义样式路径
                .setStyleExtraData((byte[]) null)//样式额外的配置，比如路况，背景颜色等，使用二进制可以更快加载出自定义样式，如果设置了则不会读取.styleExtraPath
                .setStyleExtraPath((String) null)//样式额外的配置，比如路况，背景颜色等 文件路径
                .setStyleId((String) null)      //设置底图自定义样式对应的styleID，id从官网获取。
                .setStyleTextureData((byte[]) null)//自定义样式纹理二进制，使用二进制可以更快加载出自定义样式，如果设置了则不会读取.setStyleTexturePath(String)
                .setStyleTexturePath((String) null)//自定义样式纹理路径
        ;
        aMap.setCustomMapStyle(mapStyleOptions);
    }

    /**
     * 获取目标可视区域的倾斜度，以角度为单位。
     * 俯仰角0°~45°（垂直与地图时为0）
     */
    public static float getCurrentTile(@NonNull AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.tilt;
    }

    /**
     * 获取可视区域指向的方向，以角度为单位，从正北向逆时针方向计算，从0 度到360 度。
     */
    public static float getCurrentBearing(@NonNull AMap aMap) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        return cameraPosition.bearing;
    }

    /**
     * "室内地图"是否显示, 默认false
     * @see GaoDeUiSettingUtils#setIndoorSwitchEnabled(AMap, boolean)
     */
    public static void showIndoorMap(@NonNull AMap aMap, boolean enable) {
        aMap.showIndoorMap(enable);
    }

    /**
     * 设置室内地图回调监听, 室内地图有可能有很多层的地图 <br />
     * <a href="https://a.amap.com/lbs/static/unzip/Android_Map_Doc/3D/com/amap/api/maps/model/IndoorBuildingInfo.html" target="_blank">室内地图属性类，包含室内地图的POIID、楼层总数和当前显示楼层等。</a> <br />
     * 回调类: {@link com.amap.api.maps.model.IndoorBuildingInfo}
     * <ol>
     *     <li>{@link com.amap.api.maps.model.IndoorBuildingInfo#activeFloorIndex} 当显示楼层,如 1</li>
     *     <li>{@link com.amap.api.maps.model.IndoorBuildingInfo#activeFloorName} 当前显示楼层的名称，如 F1</li>
     *     <li>{@link com.amap.api.maps.model.IndoorBuildingInfo#floor_indexs} 室内地图楼层数组，如[-2,-1,1,2]</li>
     *     <li>{@link com.amap.api.maps.model.IndoorBuildingInfo#floor_names} 室内地图楼层名称数组，如['B2','B1','F1','F2']</li>
     *     <li>{@link com.amap.api.maps.model.IndoorBuildingInfo#poiid} 室内地图的poiid，是室内地图的唯一标识。(通过这个判断是否是同一个楼)</li>
     * </ol>
     *
     * 设置跳到某一层的地图:
     *  @see AMap#setIndoorBuildingInfo(IndoorBuildingInfo)
     */
    public static void setOnIndoorBuildingActiveListener(@NonNull AMap aMap, AMap.OnIndoorBuildingActiveListener listener) {
        aMap.setOnIndoorBuildingActiveListener(listener);
    }

    /**
     * 获取当前缩放级别下，地图上1像素点对应的长度，单位米。
     * @return 当前缩放级别下，地图上1像素点对应的长度，单位米。
     */
    public static float getScalePerPixel(@NonNull AMap aMap) {
        return aMap.getScalePerPixel();
    }

    //获取版本名称
    public static String getVersion() {
        return MapsInitializer.getVersion();
    }



    ///////////////////////////////////////////////////////////////////////////
    // 回收资源区
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 移除Marker覆盖物点击事件
     */
    public static void removeOnMarkerClickListener(@NonNull AMap aMap, AMap.OnMarkerClickListener listener) {
        aMap.removeOnMarkerClickListener(listener);
    }

    /**
     * 回收 MarkerOptions
     */
    public static void recycleMarkerOption(@NonNull MarkerOptions... markerOptions) {
        if (markerOptions != null && markerOptions.length > 0) {
            for (MarkerOptions markerOption : markerOptions) {
                if (markerOption != null) {
                    BitmapDescriptor icon = markerOption.getIcon();
                    if (icon != null) icon.recycle();
                }
            }
        }
    }

    /**
     * @param markers 覆盖物
     */
    public static void destroyMarkers(@NonNull Marker... markers) {
        if (markers != null && markers.length > 0) {
            for (Marker marker : markers) if (marker != null) marker.destroy();
        }
    }
    @SafeVarargs
    public static void destroyMarkers(List<Marker>... markerss) {
        if (markerss != null && markerss.length > 0) {
            for (List<Marker> markers : markerss) {
                if (markers != null && !markers.isEmpty()) {
                    for (Marker marker : markers) if (marker != null) marker.destroy();
                    markers.clear();
                }
            }
        }
    }

    /**
     * 从地图上删除所有的overlay（marker，circle，polyline 等对象）。
     */
    public static void clear(@NonNull AMap aMap) {
        aMap.clear();
    }
}
