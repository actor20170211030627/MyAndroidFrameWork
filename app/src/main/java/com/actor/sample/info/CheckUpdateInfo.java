package com.actor.sample.info;

import java.util.List;

/**
 * Description: 检查更新
 * Author     : 李大发
 * Date       : 2019/10/19 on 15:02
 *
 * @version 1.0
 */
public class CheckUpdateInfo {

    /**
     * outputType : {"type":"APK"}
     * apkData : {"type":"MAIN","splits":[],"versionCode":1,"versionName":"1.0","enabled":true,
     * "outputFile":"app-debug.apk","fullName":"debug","baseName":"debug"}
     * path : app-debug.apk
     * properties : {}
     */

    public OutputTypeBean outputType;
    public ApkDataBean    apkData;
    public String         path;
    public PropertiesBean properties;

    public static class OutputTypeBean {
        /**
         * type : APK
         */

        public String type;
    }

    public static class ApkDataBean {
        /**
         * type : MAIN
         * splits : []
         * versionCode : 1
         * versionName : 1.0
         * enabled : true
         * outputFile : app-debug.apk
         * fullName : debug
         * baseName : debug
         */

        public String type;
        public List splits;
        public int     versionCode;
        public String  versionName;
        public boolean enabled;
        public String  outputFile;
        public String  fullName;
    }

    public static class PropertiesBean {
    }
}
