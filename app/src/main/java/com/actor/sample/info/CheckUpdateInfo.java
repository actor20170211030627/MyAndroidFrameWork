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
     * version : 1
     * artifactType : {"type":"APK","kind":"Directory"}
     * applicationId : com.actor.sample
     * variantName : debug
     * elements : [{"type":"SINGLE","filters":[],"properties":[],"versionCode":2021021101,"versionName":"2.0.1","enabled":true,"outputFile":"app-debug.apk"}]
     */

    public int version;
    public ArtifactTypeBean   artifactType;
    public String             applicationId;
    public String             variantName;
    public List<ElementsBean> elements;

    public static class ArtifactTypeBean {
        /**
         * type : APK
         * kind : Directory
         */

        public String type;
        public String kind;
    }

    public static class ElementsBean {
        /**
         * type : SINGLE
         * filters : []
         * properties : []
         * versionCode : 2021021101
         * versionName : 2.0.1
         * enabled : true
         * outputFile : app-debug.apk
         */

        public String type;
        public List<?> filters;
        public List<?> properties;
        public int     versionCode;
        public String  versionName;
        public boolean enabled;
        public String  outputFile;
    }
}
