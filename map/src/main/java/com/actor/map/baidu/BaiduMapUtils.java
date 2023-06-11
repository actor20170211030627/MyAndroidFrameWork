package com.actor.map.baidu;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

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
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * description: 百度地图帮助类 <br />
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
 *         2.清单文件中 ★★★不用★★★ 添加&lt;service, 只要你导入这个模块的依赖, 就已经添加好了! <br />
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
 *         7.使用前, 先初始化
 *         <pre> {@code
 *     BaiduMapUtils.setAgreePrivacy(true);//先同意隐私政策, 才能初始化
 *     BaiduMapUtils.init(this);//初始化百度地图
 * } </pre>
 *     </li>
 *     <li>
 *         8.示例使用见:
 *         <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/BaiDuMapActivity.java"
 *         target="_blank">BaiDuMapActivity.java</a>
 *     </li>
 * </ul>
 *
 * @author     : ldf
 */
public class BaiduMapUtils {

    public static final String BAIDU_PACKAGE_NAME = "com.baidu.BaiduMap";//百度地图包名

    protected BaiduMapUtils() {}

    /**
     * 移动应用调用百度地图: http://lbsyun.baidu.com/index.php?title=uri/api/android
     * 打开百度地图导航功能, 待测试
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @return 是否打开成功
     */
    public static boolean openBaiDuNavigation(Context context, double slon, double slat, String sname,
                                     double dlon, double dlat) {
        boolean appInstalled = AppUtils.isAppInstalled(BAIDU_PACKAGE_NAME);//是否安装百度地图
        if (!appInstalled) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/direction?mode=driving&");
        if (slat != 0) {
            sb.append("origin=latlng:")
                    .append(slat)
                    .append(",")
                    .append(slon)
                    .append("|name:")
                    .append(sname);
        }
        sb.append("&destination=latlng:")
                .append(dlat)
                .append(",")
                .append(dlon)
                .append("|name:");
        String string = sb.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(BAIDU_PACKAGE_NAME);
        intent.setData(Uri.parse(string));
        context.startActivity(intent);
        return true;
    }


    ///////////////////////////////////////////////////////////////////////////
    // 百度地图
    ///////////////////////////////////////////////////////////////////////////
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
    public static void init(Application application) {
        SDKInitializer.initialize(application);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    /**
     * 初始化BaiduMap, 注意调用MapView的生命周期方法:
     * @see MapView#onResume() 当activity恢复时需调用 mapView.onResume()
     * @see MapView#onPause() 当activity挂起时需调用 mapView.onPause()
     * @see MapView#onDestroy() 当activity销毁时需调用 mapView.destroy()
     * @param mapView 百度地图MapView
     * @param mapType 设置地图样式为普通,有三种, 示例: BaiduMap.MAP_TYPE_NORMAL
     * @param zoomLevel 缩放级别, 示例: 15
     * @return baiduMap
     */
    public static BaiduMap initBaiduMap(MapView mapView, int mapType, float zoomLevel) {
        BaiduMap baiduMap = mapView.getMap();
        baiduMap.setMapType(mapType);//设置地图样式为普通,有三种
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(zoomLevel));
        return baiduMap;
    }

    /**
     * 设置Map中心位置, 让地图移动到latLng这个点为中心的位置
     * @param baiduMap 百度地图
     * @param latLng 坐标
     */
    public static void setMapLocation(BaiduMap baiduMap, LatLng latLng) {
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
    }

    /**
     * 获取MarkerOptions(Marker覆盖物选项)
     * 注意:
     *   1. MarkerOptions应该复用, 只是表示一个图标而已, 可重复设置坐标再添加到地图, 写成全局变量, 例:
     *       markerOption.position(latLng);
     *       markerOption.extraInfo(Bundle bundle);
     *       baiduMap.addOverlay(markerOption);//会在地图新增一个latLng
     *   2.在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions)}//回收图片
     *
     * @param resId 图片resId
     * @param width marker宽度, 单位px, 例: 90
     * @param height marker高度, 单位px, 例: 90
     * @return 百度Marker
     */
    public static MarkerOptions getMarkerOptions(@DrawableRes int resId, int width, int height) {
        Bitmap bitmap = ImageUtils.getBitmap(resId);
        Bitmap scale = ImageUtils.scale(bitmap, width, height, true);
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromBitmap(scale);
        return new MarkerOptions().icon(bitmapDesc);
    }

    /**
     * 在百度地图上添加一个覆盖物
     * @param markerOption 见 {@link #getMarkerOptions(int, int, int)}
     * @param bundle 这个覆盖物可携带的额外数据
     * @return 返回覆盖物, 注意在onDestroy()方法中调用: {@link #recycleMarkerOption(MarkerOptions)}//回收图片
     */
    public static Overlay addOverlay(BaiduMap baiduMap, MarkerOptions markerOption, LatLng latLng, Bundle bundle) {
        if (latLng == null) return null;
        markerOption.position(latLng);
        markerOption.extraInfo(bundle);
        return baiduMap.addOverlay(markerOption);//会在地图新增一个latLng
    }
    //回收图片
    public static void recycleMarkerOption(MarkerOptions markerOption) {
        if (markerOption != null) {
            BitmapDescriptor icon = markerOption.getIcon();
            if (icon != null) icon.recycle();
        }
    }

    /**
     * 设置覆盖物显示状态
     *
     * @see #showOrRemoveOverlays(boolean, List)
     * @param visible 是否显示
     * @param overlays 覆盖物, overlay = baiduMap.addOverlay(overlayOptions);
     * @deprecated 隐藏后, 点击事件还在!!!
     */
    @Deprecated
    public static void setOverlayVisible(boolean visible, Overlay... overlays) {
        if (overlays != null && overlays.length > 0) {
            setOverlayVisible(visible, Arrays.asList(overlays));
        }
    }
    /**
     * 设置覆盖物显示状态
     *
     * @see #showOrRemoveOverlays(boolean, List)
     * @param visible 是否显示
     * @param overlays 覆盖物, overlay = baiduMap.addOverlay(overlayOptions);
     * @deprecated 隐藏后, 点击事件还在!!!
     */
    @Deprecated
    public static void setOverlayVisible(boolean visible, List<Overlay> overlays) {
        if (overlays != null && overlays.size() > 0) {
            for (Overlay overlay : overlays) {
                if (overlay != null) overlay.setVisible(visible);//隐藏后, 点击事件还在!!!
            }
        }
    }

    /**
     * 显示/移除 覆盖物
     * 移除该覆盖物, 移除后再调用overlay.setVisible(true);无效!!!
     *
     * @see #setOverlayVisible(boolean, Overlay...)
     * @see #setOverlayVisible(boolean, List)
     *
     * @param showOrRemove true显示, false移除
     * @param overlays 覆盖物, overlay = baiduMap.addOverlay(overlayOptions);
     */
    public static void showOrRemoveOverlays(boolean showOrRemove, List<Overlay> overlays) {
        if (overlays != null && overlays.size() > 0) {
            for (Overlay overlay : overlays) {
                if (overlay != null) {
                    if (showOrRemove) {
                        overlay.setVisible(true);
                    } else {
                        overlay.remove();//移除该覆盖物, 移除后再调用overlay.setVisible(true);无效!!!
                    }
                }
            }
            if (!showOrRemove) overlays.clear();//如果移除, 那就清空
        }
    }

    /**
     * 每次都new InfoWindow(), 因为如果复用infoWindow.setPosition()的话, 会造成infoWindow重叠现象
     * 注意:
     *   1.infoWindow.getView();//返回布局, 可以findViewById
     *
     * @param inflater
     * @param layoutId 布局id
     * @param latLng 坐标
     * @param yOffset y轴偏移, 比如向上偏移90, 就传: -90
     */
    public static InfoWindow getInfoWindow(LayoutInflater inflater, @LayoutRes int layoutId, LatLng latLng, int yOffset) {
        View view = inflater.inflate(layoutId, null);
        return new InfoWindow(view, latLng, yOffset);
    }

    /**
     * 显示 InfoWindow
     */
    public static void showInfoWindow(BaiduMap baiduMap, InfoWindow infoWindow/*, boolean what*/) {
        baiduMap.showInfoWindow(infoWindow/*, what*/);
    }

    /**
     * 隐藏 InfoWindow
     */
    public static void hideInfoWindow(BaiduMap baiduMap, InfoWindow infoWindow) {
        baiduMap.hideInfoWindow(infoWindow);
    }

    /**
     * 百度地图设置点击事件
     */
    public static void setOnMapClickListener(BaiduMap baiduMap, BaiduMap.OnMapClickListener listener) {
        baiduMap.setOnMapClickListener(listener);
    }

    /**
     * Marker覆盖物点击事件
     * 注意调用方法: {@link #removeMarkerClickListener(BaiduMap, BaiduMap.OnMarkerClickListener)}
     */
    public static void addOnMarkerClickListener(BaiduMap baiduMap, BaiduMap.OnMarkerClickListener listener) {
        baiduMap.setOnMarkerClickListener(listener);
    }
    public static void removeMarkerClickListener(BaiduMap baiduMap, BaiduMap.OnMarkerClickListener listener) {
        baiduMap.removeMarkerClickListener(listener);
    }

    /**
     * 清除所有覆盖物?
     */
    public static void clear(BaiduMap baiduMap) {
        baiduMap.clear();
    }
}
