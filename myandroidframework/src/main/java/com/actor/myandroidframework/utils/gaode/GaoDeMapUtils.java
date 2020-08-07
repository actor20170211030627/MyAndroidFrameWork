package com.actor.myandroidframework.utils.gaode;

/**
 * 高德开放平台: https://lbs.amap.com/
 * 1.添加应用并获取API Key: https://console.amap.com/dev
 *   快速获取sha1签名去下载一个 "获取debug和release签名的sha1.bat" 文件, 双击运行, 然后输入发布版秘钥地址就行:
 *   https://github.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures
 *
 * 2.添加依赖
 *   //https://lbs.amap.com/api/android-sdk/guide/create-project/android-studio-create-project#gradle_sdk
 *   2.1.在Project的gradle文件中添加:
 *       allprojects {
 *          repositories {
 *              jcenter() // 或者 mavenCentral()
 *          }
 *       }
 *
 *   2.2.在module的gradle文件中添加:
 *     //在 defaultConfig { 中添加ndk
 *       ndk {
 *           //"armeabi", "armeabi-v7a", "x86","arm64-v8a","x86_64", 'mips', 'mips64'
 *           abiFilters "armeabi"
 *       }
 *
 *     //引入最新版本的SDK
 *       implementation 'com.amap.api:3dmap:latest.integration'//3D地图
 *       implementation 'com.amap.api:map2d:latest.integration'//2D地图
 *       implementation 'com.amap.api:navi-3dmap:latest.integration'//导航
 *       implementation 'com.amap.api:search:latest.integration'//搜索
 *       implementation 'com.amap.api:location:latest.integration'//定位
 *
 *     //或者指定版本(最新版本: https://lbs.amap.com/api/android-sdk/download)
 *       implementation 'com.amap.api:3dmap:7.5.0'//3D地图
 *       implementation 'com.amap.api:map2d:6.0.0'//2D地图
 *       implementation 'com.amap.api:navi-3dmap:7.5.0'//导航(5.0.0以后版本包含了3D地图SDK,就不用导入3dmap)
 *       implementation 'com.amap.api:search:7.4.0'//搜索
 *       implementation 'com.amap.api:location:5.1.0'//定位
 *
 * 3.在AndroidManifest.xml中添加 https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention
 * <meta-data
 *     android:name="com.amap.api.v2.apikey"
 *     android:value="您的Api Key"/>
 *
 * 4.添加权限(因为添加的依赖是.jar, 不含清单文件) https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention#permission
 *   <!--高德地图包、搜索包需要的基础权限-->
 *   <!--允许程序打开网络套接字-->
 *   <uses-permission android:name="android.permission.INTERNET" />
 *   <!--允许程序设置内置sd卡的写权限-->
 *   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *   <!--允许程序获取网络状态-->
 *   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *   <!--允许程序访问WiFi网络信息-->
 *   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *   <!--允许程序读写手机状态和身份-->
 *   <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 *   <!--允许程序访问CellID或WiFi热点来获取粗略的位置-->
 *   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 *
 * 5.代码混淆 https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention#obfuscated-code
 *   已在 MyAndroidFrameWork 中添加混淆, 如果你需要混淆代码, 打开 minifyEnabled true 即可
 *
 * @author : 李大发
 * date       : 2020/8/5 on 16:39
 * @version 1.0
 */
public class GaoDeMapUtils {
}
