package com.actor.map.baidu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.AppUtils;

/**
 * description: <a href="https://lbsyun.baidu.com/index.php?title=uri" target="_blank">地图调起API</a>, URI API v2.0  <br />
 *              百度地图URI API是为开发者提供直接调起百度地图产品（百度地图手机客户端）以满足特定业务场景下应用需求的程序接口，
 *              开发者只需按照接口规范构造一条标准的URI，便可在PC和移动端浏览器或移动开发应用中调起百度地图产品，
 *              进行地图展示和检索、线路查询、导航等功能，无需进行复杂的地图功能开发。 该套API免费对外开放，无需申请ak。<br />
 *              <br />
 *              百度地图开放平台首页 -> '功能与服务'='地图' -> "JS API" -> 左侧"开发指南" -> '调起百度地图'<br />
 *              <br />
 *              注意: 本工具不需要添加百度地图的sdk
 * @author : ldf
 * @version 1.0
 */
public class BaiduUriApiUtils {

    // TODO: 一些注释的接口还未实现, 没空

    public static final String BAIDU_PACKAGE_NAME = "com.baidu.BaiduMap";//百度地图包名
    protected static String companyName, appName;

    /**
     * 是否安装百度地图
     */
    public static boolean isBaiduMapInstalled() {
        return AppUtils.isAppInstalled(BAIDU_PACKAGE_NAME);
    }

    /**
     * 2.2 图区功能
     * 2.2.1 展示地图 <br />
     * 展示地图，可通过zoom、center以及bounds来指定地图的视野范围。
     * URL接口: baidumap://map/show
     */

    /**
     * 2.2.2 自定义打点 <br />
     * 调用该接口可调起Android百度地图，且在指定坐标点上显示点的名称和内容信息。
     * URL接口：baidumap://map/marker
     */

    /**
     * 2.2.3 展示地图图区 <br />
     * 调用该接口可调起Android百度地图，且在指定坐标点上显示点的名称和内容信息。
     */
    public static boolean openBaiduMap(@NonNull Context context) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map?");
        appendCompanyName$AppName(context, sb, false);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.2.4 地址解析
     * 调用该接口可以在调起百度地图时，在图区显示地址对应的坐标点。
     * @param address 地址, 示例: 北京市海淀区上地信息路9号奎科科技大厦
     */
    public static boolean showPointByGenCoder(@NonNull Context context, @NonNull String address) {
        if (address == null || address.isEmpty()) return false;
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/geocoder?address=").append(address);
        appendCompanyName$AppName(context, sb, true);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.2.5 反向地址解析
     * 调用该接口可调起百度地图，经过逆地理编码后，以标注形式显示位置和地址信息。
     * URL接口: baidumap://map/geocoder
     */

    /**
     * 2.3 搜索功能
     * 2.3.1 POI搜索
     * 根据给定的关键字、检索条件进行检索。
     * URL接口：baidumap://map/place/search
     */

    /**
     * 2.3.2 路线规划 (可用作导航前的路线规划) <br />
     * 公交、驾车、步行、骑行路线规划 <br />
     * @param slon    起点经度. 可只传起点经纬度, 不传起点名称.
     * @param slat    起点纬度
     * @param sname   起点名称. 可只传起点名称, 不传起点经纬度.
     * @param dlon    终点经度. 可只传终点经纬度, 不传终点名称.
     * @param dlat    终点纬度
     * @param dname   终点名称. 可只传终点名称, 不传终点经纬度.
     * @param coord_type 坐标类型: bd09ll（百度经纬度坐标）(默认), bd09mc（百度墨卡托坐标）, gcj02（经国测局加密的坐标）, wgs84（gps获取的原始坐标）<br />
     *                  如开发者不传递正确的坐标类型参数，会导致地点坐标位置偏移。
     * @param mode 导航模式(可选)，transit（公交）、driving（驾车）(默认)、walking（步行）和riding（骑行）
     * @param sy 公交检索策略(可选)，只针对mode字段填写transit情况下有效，值为数字。
     * 0：推荐路线, 2：少换乘, 3：少步行, 4：不坐地铁, 5：时间短, 6：地铁优先
     * @param car_type 驾车路线规划类型(可选)，BLK:躲避拥堵(自驾); TIME:最短时间(自驾); DIS:最短路程(自驾);
     * FEE:少走高速(自驾); HIGHWAY:高速优先; DEFAULT:推荐（自驾，地图app不选择偏好）;
     * @return 是否打开成功
     */
    public static boolean pathPlanning(@NonNull Context context,
                                       @Nullable Double slon, @Nullable Double slat, @Nullable String sname,
                                       @Nullable Double dlon, @Nullable Double dlat, @Nullable String dname,
                                       @Nullable String coord_type, @Nullable String mode,
                                       @Nullable Integer sy, @Nullable Integer car_type) {
        //起点经纬度 & 起点名称 不能同时为null
        if ((slon == null || slat == null) && (sname == null || sname.isEmpty())) return false;
        //终点经纬度 & 终点名称 不能同时为null
        if ((dlon == null || dlat == null) && (dname == null || dname.isEmpty())) return false;
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/direction?origin=");
        //1.起点名称和经纬度, 必选
        if (sname != null && !sname.isEmpty()) {
            if (slon == null || slat == null) {
                sb.append(sname);    //注意：仅有名称的情况下，请不要带“name:”，只需要origin=“起点名称”
            } else sb.append("name:").append(sname).append("|latlng:");
        }
        if (slon != null && slat != null) sb.append(slat).append(",").append(slon);
        //2.终点名称和经纬度, 必选
        sb.append("&destination=");
        if (dname != null && !dname.isEmpty()) {
            if (dlon == null || dlat == null) {
                sb.append(dname);    //注意：仅有名称的情况下，请不要带“name:”，只需要destination=“终点名称”
            } else sb.append("name:").append(dname).append("|latlng:");
        }
        if (dlon != null && dlat != null) sb.append(dlat).append(",").append(dlon);
        //3.坐标类型，必选参数
        sb.append("&coord_type=").append((coord_type == null || coord_type.isEmpty()) ? "bd09ll" : coord_type);
        //4.导航模式，(可选)
        if (mode != null && !mode.isEmpty()) sb.append("&mode=").append(mode);
        //5.公交检索策略(可选)
        if (sy != null && "transit".equals(mode)) sb.append("&sy=").append(sy);
        //6.驾车路线规划类型(可选)
        if (car_type != null && (mode == null || mode.isEmpty() || "driving".equals(mode))) sb.append("&car_type=").append(car_type);
        //7.统计来源，必选. 参数格式为：andr.companyName.appName, 不传此参数，不保证服务
        appendCompanyName$AppName(context, sb, true);
        //8.跳转
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.3.3 公交、地铁线路查询
     * 公交、地铁线路查询。
     * URL接口：baidumap://map/line
     */

    /**
     * 2.3.4 附近搜索
     * 进入附近页，搜周边页，或者直接发起周边检索。
     * URL接口1：baidumap://map/place/nearby
     * URL接口2：baidumap://map/nearbysearch <br/>
     * 两个协议头的功能和参数完全一样，因为历史遗留，两个都保留
     */

    /**
     * 2.4 导航
     * 2.4.1 驾车导航
     * URL接口：baidumap://map/navi
     */

    /**
     * 2.4.2 骑行导航
     * URL接口：baidumap://map/bikenavi
     */

    /**
     * 2.4.3 步行导航
     * URL接口：baidumap://map/walknavi
     */

    /**
     * 2.4.4 导航到家（公司）（map9.5.5以上版本支持）
     * URL接口：baidumap://map/navi/common
     */

    /**
     * 2.5 信息显示
     * 2.5.1 POI详情显示页
     * 根据POI的uid展示详情页。
     * URL接口：baidumap://map/place/detail
     */

    /**
     * 2.5.2 离线导航包
     * 调起离线导航包下载页面，无参数。
     */
    public static boolean downloadOfflineNavmap(@NonNull Context context) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/navi/offlinemap?");
        appendCompanyName$AppName(context, sb, false);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.5.3 路线(出行)页面调起协议
     * URL接口：baidumap://map/routepage
     */

    /**
     * 2.5.4 出行早晚报
     * 跳转出行早晚报页面
     * URL接口：baidumap://map/newsassistant
     */

    /**
     * 2.5.5 行程助手
     * 跳转行程助手主页或者编辑页
     * URL接口：baidumap://map/trip
     */

    /**
     * 2.5.6 实时公交页面（map9.5.5以上版本支持）
     * URL接口：baidumap://map/page/realtimebus
     */

    /**
     * 2.5.7 离线地图下载页面（map9.5.5以上版本支持）(map9.5.5更新时间：2016年12月21日)
     * @param mode 启动模式：<br />
     *             CLEAN_MODE 启动后清除页面栈，back后退出程序；<br />
     *             MAP_MODE 启动后清除页面栈，back回退后到住地图；<br />
     *             NORMAL_MODE 启动后保留原有页面栈；<br />
     *             NORMAL_MAP_MODE 如果有界面，保持栈不变，如果没有插入地图。
     */
    public static boolean downloadOfflinemap(@NonNull Context context, @Nullable String mode) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/page/offlinemap?");
        appendCompanyName$AppName(context, sb, false);
        if (mode != null && !mode.isEmpty()) sb.append("&mode=").append(mode);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.5.8 实时汇率页面
     * @param mode 启动模式：<br />
     *             CLEAN_MODE 启动后清除页面栈，back后退出程序；<br />
     *             MAP_MODE 启动后清除页面栈，back回退后到住地图；<br />
     *             NORMAL_MODE 启动后保留原有页面栈；<br />
     *             NORMAL_MAP_MODE 如果有界面，保持栈不变，如果没有插入地图。
     */
    public static boolean showExchangerate(@NonNull Context context, @Nullable String mode) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/component?comName=international&target=international_exchangerate_page");
        appendCompanyName$AppName(context, sb, true);
        if (mode != null && !mode.isEmpty()) sb.append("&mode=").append(mode);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.5.9 实时翻译页面
     * @param mode 启动模式：<br />
     *             CLEAN_MODE 启动后清除页面栈，back后退出程序；<br />
     *             MAP_MODE 启动后清除页面栈，back回退后到住地图；<br />
     *             NORMAL_MODE 启动后保留原有页面栈；<br />
     *             NORMAL_MAP_MODE 如果有界面，保持栈不变，如果没有插入地图。
     */
    public static boolean showTranslation(@NonNull Context context, @Nullable String mode) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/component?comName=international&target=international_translation_page");
        appendCompanyName$AppName(context, sb, true);
        if (mode != null && !mode.isEmpty()) sb.append("&mode=").append(mode);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.5.10 电子狗
     * 调用该接口，可以调起百度地图的电子狗功能，无参数。
     */
    public static boolean showCruiser(@NonNull Context context) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/navi/cruiser?");
        appendCompanyName$AppName(context, sb, false);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.5.11 路况页面调起协议
     * 调用该接口，可以查看到达终点的路况情况。
     * URL接口：baidumap://map/traffic?keyword=xxx&playMode=0&resetVoiceMode=0
     */

    /**
     * 2.6 标准组件
     * 2.6.1 AR识楼
     */
    public static boolean showArExplore(@NonNull Context context) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/component?comName=mapbasear&target=show_arexplore_page");
        appendCompanyName$AppName(context, sb, true);
        startActivity(context, sb);
        return true;
    }

    /**
     * 2.6.2 地铁图
     * 调用该接口，可以调起进入地铁路首页
     */
    public static boolean showSubway(@NonNull Context context) {
        if (!isBaiduMapInstalled()) return false;
        StringBuilder sb = new StringBuilder("baidumap://map/component?target=subway_page_openapi&comName=subway");
        appendCompanyName$AppName(context, sb, true);
        startActivity(context, sb);
        return true;
    }

    //统计来源，必选. 参数格式为：andr.companyName.appName, 不传此参数，不保证服务
    protected static void appendCompanyName$AppName(@NonNull Context context, StringBuilder sb, boolean appendAnd) {
        if (companyName == null || appName == null) {
            companyName = appName = context.getPackageName();
            if (appName.contains(".")) {
                String[] strs = appName.split("\\.");
                appName = strs[strs.length - 1];
                companyName = strs[1];
            }
        }
        if (appendAnd) sb.append("&");
        sb.append("src=andr.").append(companyName).append(".").append(appName);
    }

    //跳转百度地图
    protected static void startActivity(@NonNull Context context, @NonNull StringBuilder sb) {
        String str = sb.toString();
        LogUtils.errorFormat(str);
        context.startActivity(new Intent(/*Intent.ACTION_VIEW*/)
//                .setPackage(BAIDU_PACKAGE_NAME)
                .setFlags((context instanceof Activity) ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(Uri.parse(str))
        );
    }
}
