package com.actor.sample.utils;

/**
 * Description: 全局变量, GlobalConstants
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/8/18 on 22:15
 */
public class Global {

    //林允儿
//    public static final String girl = "https://timgsa.baidu.com/timg?image&quality=80&" +
//            "size=b10000_10000&sec=1553570762&di=345ca57cc11ccf228e3ff8c2b33af03b&" +
//            "src=http://ww2.sinaimg.cn/large/9eb5883egw1euqvwfpmevj21kj2cok3p.jpg";
    public static final String girl = "http://ww2.sinaimg" +
            ".cn/large/9eb5883egw1euqvwfpmevj21kj2cok3p.jpg";

    public static final String BASE_URL = "https://api.github.com";

    //GET/POST, 这个也可以检测更新
//    public static final String CHECK_UPDATE = "https://github.com/actor20170211030627/" +
//            "MyAndroidFrameWork" +
//            "/raw/master/app/build/outputs/apk/debug/output.json";
    //必须GET
    public static final String CHECK_UPDATE = "https://raw.githubusercontent.com/" +
            "actor20170211030627/" +
            "MyAndroidFrameWork" +//项目名
            "/master/" +
            "app" +//模块名
            "/build/outputs/apk/debug/output.json";

    //这个也可以下载
//    public static final String DOWNLOAD_URL = "https://github.com/actor20170211030627/" +
//            "MyAndroidFrameWork" +
//            "/raw/master/app/build/outputs/apk/debug/app-debug.apk";
    public static final String DOWNLOAD_URL = "https://raw.githubusercontent.com/" +
            "actor20170211030627/" +
            "MyAndroidFrameWork" +//项目名
            "/master/" +
            "app" +//模块名
            "/build/outputs/apk/debug/app-debug.apk";


    //thanks: https://www.cr173.com/soft/2134.html
    public static final String PICPICK_DOWNLOAD_URL = "http://cr3.197946.com/picpick_portable_5.0.7.zip";


    public static final String CONTENT = "CONTENT";
    public static final String ID = "ID";
    public static final String POSITION = "POSITION";
}
