package com.actor.map.gaode;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;

/**
 * description: 画形状小工具
 * @author : ldf
 * @version 1.0
 */
public class GaoDeShapeUtils {

    // TODO: 很多形状未完善, 没空

    /**
     * <a href="https://a.amap.com/lbs/static/unzip/Android_Map_Doc/3D/com/amap/api/maps/model/PolylineOptions.html" target="_blank">线段的选项类</a>
     * @param latLngs 传入List/Set等
     * @param width 设置线段的宽度，默认为10。
     */
    public static PolylineOptions getPolylineOptions(@NonNull Iterable<LatLng> latLngs,
                                                     @ColorInt int color, float width) {
        return new PolylineOptions()
//                .add(LatLng... points)//追加一批顶点到线段的坐标集合。
//                .add(LatLng point)    //追加一个顶点到线段的坐标集合。
                .addAll(latLngs)        //追加一批顶点到线段的坐标集合。
                .color(color)           //设置线段的颜色，需要传入32位的ARGB格式。
//                .colorValues((List<java.lang.Integer>) colors)    //设置线段的颜色
                .geodesic(false)        //设置线段是否为大地曲线，默认false，不画大地曲线。
                .lineCapType(PolylineOptions.LineCapType.LineCapRound)      //设置Polyline尾部形状
                .lineJoinType(PolylineOptions.LineJoinType.LineJoinBevel)   //设置Polyline连接处形状
//                .setCustomTexture(BitmapDescriptor customTexture)         //设置线段的纹理图，图片为2的n次方。
//                .setCustomTextureIndex(List<java.lang.Integer> custemTextureIndexs)   //设置线段纹理index数组
//                .setCustomTextureList(List<BitmapDescriptor> customTextureList)       //设置线段纹理list
                .setDottedLine(false)                                       //设置是否画虚线，默认为false，画实线。
                .setDottedLineType(PolylineOptions.DOTTEDLINE_TYPE_SQUARE)  //设置虚线形状。
//                .setEraseColor(boolean eraseVisible, int eraseColor)  //设置线段擦除（显示范围外）颜色，需要传入32位的ARGB格式，针对颜色线段生效。
//                .setPoints(List<LatLng> points)                       //设置线段的点坐标集合,如果以前已经存在点,则会清空以前的点。
                .setUseTexture(true)                                    //设置是否使用纹理贴图画线。
                .transparency(1.0F)                                     //设置线段的透明度0~1，默认是1,1表示不透明
                .useGradient(false)                                     //设置线段是否使用渐变色
                .visible(true)                                          //设置线段的可见性。
                .width(width)                                           //设置线段的宽度，默认为10。
                .zIndex(0.0F)                                           //设置线段Z轴的值。
                ;
    }
}
