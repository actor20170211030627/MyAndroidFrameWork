package com.actor.map.baidu;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.Utils;

import java.util.List;

/**
 * description: 百度地图工具类 <br />
 * 缺点: 原生api无法同时显示多个InfoWindow, 具体要自己实现, 请百度...
 * <ul>
 *     <li>1,3步骤. 配置和 {@link BaiduLocationUtils} 百度定位Utils 一样</li>
 *     <li>
 *         2.<a href="https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-project/androidstudio"
 *  *         target="_blank">添加依赖</a>, <a href="https://lbsyun.baidu.com/index.php?title=androidsdk/sdkandev-download"
 *  *         target="_blank">SDK, 示例代码, 类参文档</a> <br />
 *         <code>
 *             //项目gradle中添加仓库  <br />
 *             mavenCentral()         <br />
 *             <br />
 *             //这些组件中都包含了BaiduMapSDK_Map组件，根据需求选其一,
 *             <a href="https://repo.maven.apache.org/maven2/com/baidu/lbsyun/" target="_blank">mavenCentral()最新版本</a> <br />
 *             //地图组件 <br />
 *             implementation 'com.baidu.lbsyun:BaiduMapSDK_Map:7.5.7.1' <br />
 *             //步骑行组件 <br />
 *             implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-BWNavi:7.5.7.1' <br />
 *             //驾车导航组件 <br />
 *             implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-Navi:7.5.7.1' <br />
 *             //驾车导航+步骑行导航 <br />
 *             implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-AllNavi:7.5.7.1' <br />
 *         </code> <br />
 *     </li>
 *     <li>
 *         4.1.添加<a href="https://mapopen-pub-androidsdk.cdn.bcebos.com/map/7_5_8/doc/index.html"
 *         target="_blank">&lt;meta-data</a>或调用 {@link BaiduMapUtils#setApiKey(String) BaiduMapUtils.setApiKey(String)}设置AK, (都可以,二选一) <br />
 *         在清单文件的&lt;application>标签里添加 &lt;meta-data 示例: <br />
 *         <code>
 *             &lt;!--百度定位设置AK，在Application标签中加入-->      <br />
 *             &lt;meta-data                                        <br />
 *             &emsp;&emsp; android:name="com.baidu.lbsapi.API_KEY" <br />
 *             &emsp;&emsp; android:value="您申请的百度地图AK"/&gt;   <br />
 *         </code>                                                   <br />
 *         &ensp; 2.清单文件中 ★★★不用★★★ 添加&lt;service, 只要你导入这个模块的依赖, 就已经添加好了! <br />
 *     </li>
 *     <li>
 *         5.资源说明 <br />
 *         V5.1.0版本起，为了优化SDK的jar包体积，
 *         将一些Demo中用到的图片资源文件从SDK的jar包中移到了Demo的资源文件路径下，若有依赖，
 *         请在Demo中的资源路径获取，
 *         <a href="http://lbsyun.baidu.com/index.php?title=androidsdk/sdkandev-download"
 *         target="_blank">源码Demo下载</a>。路径如下：<br />
 *         BaiduMapsApiASDemo/app/src/main/assets/ <br />
 *         移除的图片资源包括：(
 *         <a href="https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-project/androidstudio"
 *         target="_blank">官网查看</a>) <br />
 *         注意：若您下载的开发包是步骑行导航的，在解压后的开发包中会包含一个assets目录，您需要将该目录下的png文件拷贝至您的项目的assets目录下。
 *     </li>
 *     <li>
 *         6.<a href="https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-project/androidstudio"
 *         target="_blank">代码混淆</a> ★★★已经在模块中完成★★★, 如果你需要混淆代码, 打开 minifyEnabled true 即可.
 *     </li>
 *     <li>
 *         7.使用前, 先初始化 <br />
 *         {@link BaiduMapUtils#setAgreePrivacy(boolean) BaiduMapUtils.setAgreePrivacy(true)};//先同意隐私政策, 才能初始化 <br />
 *         {@link BaiduMapUtils#init(Application) BaiduMapUtils.init(application)};           //初始化百度地图
 *     </li>
 *     <li>
 *         8.Activity/Fragment中 <br />
 *         {@link MapView#onCreate(Context, Bundle) mapView.onCreate(activity, savedInstanceState)} <br />
 *         {@link MapView#onSaveInstanceState(Bundle) mapView.onSaveInstanceState(outState)} <br />
 *         {@link MapView#onResume() mapView.onResume()} <br />
 *         {@link MapView#onPause() mapView.onPause()} <br />
 *         {@link MapView#onDestroy() mapView.onDestroy()} <br />
 *     </li>
 *     <li>
 *         9.示例使用见:
 *         <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/BaiDuMapActivity.java"
 *         target="_blank">BaiDuMapActivity.java</a>
 *     </li>
 * </ul>
 *
 * @author     : ldf
 */
public class BaiduMapUtils {

    protected BaiduMapUtils() {}

    /**
     * https://mapopen-pub-androidsdk.cdn.bcebos.com/map/7_5_8/doc/index.html
     * 请务必确保用户同意隐私政策后调用setAgreePrivacy接口以进行SDK初始化之前的准备工作。
     *
     * 设置隐私模式，一定要保证在调用 {@link BaiduMapUtils#init(Application)} 之前设置;
     *
     * @param agree true，表示用户同意隐私合规政策
     *              false，表示用户不同意隐私合规政策
     */
    public static void setAgreePrivacy(boolean agree) {
        SDKInitializer.setAgreePrivacy(Utils.getApp(), agree);
    }

    /**
     * 设置AK, 如果已经在清单文件中添加了<meta-data , 就不用调用这个方法.
     * @param AK 在官网申请的AK
     */
    public static void setApiKey(String AK) {
        SDKInitializer.setApiKey(AK);
    }

    /**
     * @return 是否已经同意隐私政策
     */
    public static boolean getAgreePrivacy() {
        return SDKInitializer.getAgreePrivacy();
    }


    /**
     * 如果用到百度地图, 在 Application 中初始化
     */
    public static void init(@NonNull Application application) {
        SDKInitializer.initialize(application);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    /**
     * 初始化BaiduMap
     * @param mapView 百度地图MapView
     * @param mapType 设置地图样式, 有三种, 示例: BaiduMap.MAP_TYPE_NORMAL
     * @param zoomLevel 缩放级别, 示例: 15
     * @return baiduMap
     */
    public static BaiduMap initBaiduMap(@NonNull MapView mapView, int mapType, float zoomLevel) {
        BaiduMap baiduMap = mapView.getMap();
        baiduMap.setMapType(mapType);//设置地图样式为普通,有三种
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(zoomLevel));
        return baiduMap;
    }

    /**
     * https://mapopen-pub-androidsdk.cdn.bcebos.com/map/7_5_8/doc/index.html <br />
     * 设置地图加载完成回调，该接口需要在地图加载到页面之前调用，否则不会触发回调。<br />
     * if刚开始加载时, 中心点的"红点"加载不出来, 可设置这个监听回调后再加载红点!!
     * @param callback 回调, 可传null(相当于取消监听)
     */
    public static void setOnMapLoadedCallback(@NonNull BaiduMap baiduMap, @Nullable BaiduMap.OnMapLoadedCallback callback) {
        baiduMap.setOnMapLoadedCallback(callback);
    }

    /**
     * 设置Map中心位置, 让地图移动到latLng这个点为中心的位置
     * @param baiduMap 百度地图
     * @param latLng 坐标
     */
    public static void setMapLocation(@NonNull BaiduMap baiduMap, LatLng latLng) {
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
    }

    /**
     * 获取MarkerOptions(Marker覆盖物选项) https://mapopen-pub-androidsdk.cdn.bcebos.com/map/7_5_8/doc/index.html <br />
     * 注意: <br />
     *      &emsp; 1.同1个尺寸相同图片的MarkerOptions, 应该复用. <br />
     *      &emsp; 2.在onDestroy()方法中调用: {@link #recycleMarkerOptions(MarkerOptions...)}//回收图片 <br />
     * @param resId 图片resId
     * @param width marker宽度, 单位px, 例: 90
     * @param height marker高度, 单位px, 例: 90
     * @param infoWindow 设置 Marker 绑定的InfoWindow. 绑定后, 可以和Marker同时显示(多个InfoWindow显示效果). <br />
     *                   &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp; if你不需要同时显示多个InfoWindow(仅显示1个), 就不要绑定, 传null <br />
     *                   &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp; 吐槽: Marker隐藏后, 绑定的InfoWindow不会跟着隐藏. <br />
     *                   &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp; 建议: 不要设置InfoWindow, {@link #showInfoWindow(BaiduMap, InfoWindow, boolean)}的参3可以设置其它InfoWindow是否隐藏. <br />
     */
    public static MarkerOptions getMarkerOptions(@DrawableRes int resId, int width, int height
                                                 //, @Nullable InfoWindow infoWindow
    ) {
        Bitmap bitmap = ImageUtils.getBitmap(resId);
        Bitmap scale = ImageUtils.scale(bitmap, width, height, true);
        //还可以加载其它类型, 例: fromView, fromResource, ...
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromBitmap(scale);
        return new MarkerOptions()
//                .alpha(1.0F)                                        //覆盖物图标的透明度[0,1], 默认1.0
//                .anchor(0.5f, 1.0f)                                 //设置 marker 覆盖物的锚点比例，默认（0.5f, 1.0f）水平居中，垂直下对齐
//                .animateType((MarkerOptions.MarkerAnimateType) null)//设置marker动画类型，见 MarkerAnimateType，默认无动画
//                .clickable(true)                                    //设置Marker是否可点击, 默认true
//                .draggable(false)                                   //设置 marker 是否允许拖拽, 默认false
//                .endLevel(22)                                       //设置marker显示层级 EndLevel：最大显示层级
                .extraInfo((Bundle) null)                           //设置 marker 覆盖物的额外信息
//                .fixedScreenPosition((Point) null)                  //修复屏幕位置??
//                .flat(false)                                        //设置 marker设置 是否平贴地图, 默认false
//                .height(0)                                          //设置3D marker的高度值
                .icon(bitmapDesc)                                   //设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
//                .icons((ArrayList<BitmapDescriptor>) null)          //设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
//                .infoWindow(infoWindow)                             //设置 Marker 绑定的InfoWindow
//                .isForceDisPlay(false)                              //设置marker碰撞时是否强制显示, 默认false
//                .isJoinCollision(false)                             //设置marker是否参与碰撞检测
//                .period(20)                                         //设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快
//                .perspective(true)                                  //设置是否开启 marker 覆盖物近大远小效果，默认开启
//                .poiCollided(false)                                 //设置是否碰撞底图POI
//                  .position(latLng)                                 //设置 marker 覆盖物的位置坐标
//                .priority(Integer.MAX_VALUE)                        //设置marker碰撞时的显示优先级
//                .rotate(0F)                                         //设置 marker 覆盖物旋转角度，逆时针
//                .scaleX(1.0F)                                       //设置 Marker 覆盖物X方向缩放
//                .scaleY(1.0F)                                       //设置 Marker 覆盖物Y方向缩放
//                .startLevel(4)                                      //设置marker显示层级 StartLevel：最小显示层级
                .visible(true);                                     //设置 marker 覆盖物的可见性, 默认true
    }

    /**
     * 在百度地图上添加一个覆盖物
     * @param latLng 坐标
     * @param bundle 这个覆盖物可携带的额外数据
     * @return 返回覆盖物, 注意在onDestroy()方法中调用: {@link #removeOverlays(List[])}//回收覆盖物
     */
    public static Overlay addOverlay(@NonNull BaiduMap baiduMap, @NonNull MarkerOptions markerOption,
                                     @NonNull LatLng latLng, @Nullable Bundle bundle) {
        markerOption.position(latLng).extraInfo(bundle);
        return baiduMap.addOverlay(markerOption);
    }

    /**
     * 设置覆盖物显示状态
     * @param visible 是否显示
     * @param overlays 覆盖物
     */
    public static void setOverlayVisible(boolean visible, Overlay... overlays) {
        if (overlays != null && overlays.length > 0) {
            for (Overlay overlay : overlays) if (overlay != null) overlay.setVisible(visible);
        }
    }
    /**
     * 设置覆盖物显示状态
     * @param visible 是否显示
     * @param overlays 覆盖物
     */
    public static void setOverlayVisible(boolean visible, List<Overlay> overlays) {
        if (overlays != null && !overlays.isEmpty()) {
            for (Overlay overlay : overlays) if (overlay != null) overlay.setVisible(visible);
        }
    }

    /**
     * 信息窗口 https://mapopen-pub-androidsdk.cdn.bcebos.com/map/7_5_8/doc/index.html <br />
     * <ol>
     *     <li>if地图上仅显示1个InfoWindow, 应该复用(infoWindow.setPosition()).</li>
     *     <li>if地图上需要同时显示多个InfoWindow, 应该new多个InfoWindow(), 并存储在Map中, 显示的时候从Map获取.</li>
     *     <li>infoWindow.getView(); //返回布局, 可用于findViewById</li>
     * </ol>
     * @param inflater Activity中: getLayoutInflater()
     * @param layoutId 布局id
     * @param latLng 坐标
     * @param yOffset y轴偏移, 比如向上偏移90, 就传: -90
     */
    public static InfoWindow getInfoWindow(@NonNull LayoutInflater inflater, @LayoutRes int layoutId,
                                           @NonNull LatLng latLng, int yOffset) {
        InfoWindow infoWindow = new InfoWindow(inflater.inflate(layoutId, null), latLng, yOffset);
        infoWindow.setBitmapDescriptor((BitmapDescriptor) null);//更新InfoWindow的BitmapDescriptor属性。
//        infoWindow.setPosition(latLng);                         //设置位置数据
        infoWindow.setTag("");                                  //设置InfoWindow的Tag
        /**
         * 更新InfoWindow的View属性
         * 注: 仅支持通过InfoWindow(View, LatLng, int, boolean, int) or InfoWindow(View, LatLng, int)两种方式创建 InfoWindow的更新;
         *     如果是使用了InfoWindow(BitmapDescriptor, LatLng, int, OnInfoWindowClickListener)方式创建的 InfoWindow，
         *     则不要使用该接口更新View属性，否则可能出现View与BitmapDescriptor层叠的现象。
         */
//        infoWindow.setView((View) null);
//        infoWindow.setYOffset(yOffset);                         //设置InfoWindow的YOffset偏移
        return infoWindow;
    }

    /**
     * 显示 InfoWindow
     * @param isHideOthers 设置是否在添加InfoWindow之前，先隐藏其他已经添加的InfoWindow. (默认true)
     */
    public static void showInfoWindow(@NonNull BaiduMap baiduMap, @Nullable InfoWindow infoWindow, boolean isHideOthers) {
        baiduMap.showInfoWindow(infoWindow, isHideOthers);
    }

    /**
     * 隐藏 InfoWindow
     */
    public static void hideInfoWindow(@NonNull BaiduMap baiduMap, @Nullable InfoWindow infoWindow) {
        baiduMap.hideInfoWindow(infoWindow);
    }

    /**
     * 百度地图设置点击事件
     */
    public static void setOnMapClickListener(@NonNull BaiduMap baiduMap, @Nullable BaiduMap.OnMapClickListener listener) {
        baiduMap.setOnMapClickListener(listener);
    }

    /**
     * Marker覆盖物点击事件 <br />
     * 注意调用方法: {@link #removeMarkerClickListener(BaiduMap, BaiduMap.OnMarkerClickListener)}移除监听
     */
    public static void addOnMarkerClickListener(@NonNull BaiduMap baiduMap, BaiduMap.OnMarkerClickListener listener) {
        baiduMap.setOnMarkerClickListener(listener);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 回收资源区
    ///////////////////////////////////////////////////////////////////////////
    public static void removeMarkerClickListener(@NonNull BaiduMap baiduMap, BaiduMap.OnMarkerClickListener listener) {
        baiduMap.removeMarkerClickListener(listener);
    }

    /**
     * 回收MarkerOptions里的图片
     */
    public static void recycleMarkerOptions(MarkerOptions... markerOptions) {
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
     * 移除该覆盖物
     */
    public static void removeOverlays(Overlay... overlays) {
        if (overlays != null && overlays.length > 0) {
            for (Overlay overlay : overlays) if (overlay != null) overlay.remove();
        }
    }
    @SafeVarargs
    public static void removeOverlays(List<Overlay>... overlayss) {
        if (overlayss != null && overlayss.length > 0) {
            for (List<Overlay> overlays : overlayss) {
                if (overlays != null && !overlays.isEmpty()) {
                    for (Overlay overlay : overlays) if (overlay != null) overlay.remove();
                    overlays.clear();
                }
            }
        }
    }

    /**
     * 清空地图所有的 Overlay 覆盖物以及 InfoWindow
     */
    public static void clear(@NonNull BaiduMap baiduMap) {
        baiduMap.clear();
    }
}
