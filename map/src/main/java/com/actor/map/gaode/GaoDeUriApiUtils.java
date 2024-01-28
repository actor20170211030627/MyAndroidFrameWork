package com.actor.map.gaode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.AppUtils;

/**
 * description: <a href="https://lbs.amap.com/api/amap-mobile/summary" target="_blank">高德地图手机版</a> <br />
 *              高德地图手机版第三方调用URI API是为开发者提供的一种在自己应用中调用高德地图app的方法.
 *              开发者只需根据提供的URI API构造一条标准的URI, 将其放在自己的应用程序中,
 *              便可调用高德地图APP来进行POI 标点、公交、驾车查询等功能。
 *              开发者可以根据业务的需求, 直接调用高德地图APP的各种功能来丰富完善自己的应用，
 *              是一种简单易用的LBS开发工具。
 * @author : ldf
 * @version 1.0
 */
public class GaoDeUriApiUtils {

    // TODO: 待完善

    public static final String GAODE_PACKAGE_NAME = "com.autonavi.minimap";//高德地图包名

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
    public static boolean openGaoDeNavigation(@NonNull Context context, double slon, double slat, String sname,
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
        startActivity(context, sb);
        return true;
    }

    //跳转高德地图
    protected static void startActivity(@NonNull Context context, @NonNull StringBuilder sb) {
        String str = sb.toString();
        LogUtils.errorFormat(str);
        context.startActivity(new Intent(/*Intent.ACTION_VIEW*/)
//                .setPackage(GAODE_PACKAGE_NAME)
                .addFlags((context instanceof Activity) ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(Uri.parse(str))
        );
    }
}
