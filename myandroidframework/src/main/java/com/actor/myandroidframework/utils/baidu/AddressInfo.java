package com.actor.myandroidframework.utils.baidu;

import java.util.List;

/**
 * Description: 坐标解析成地址
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/23 on 09:00
 * @version 1.0
 */
public class AddressInfo {

    /**
     * status : 0
     * result : {"location":{"lng":121.49884033193993,"lat":31.225696429417987},"formatted_address":"上海市黄浦区净土街31弄-4号","business":"老西门,城隍庙,西藏南路","addressComponent":{"country":"中国","country_code":0,"country_code_iso":"CHN","country_code_iso2":"CN","province":"上海市","city":"上海市","city_level":2,"district":"黄浦区","town":"","town_code":"","adcode":"310101","street":"净土街","street_number":"31弄-4号","direction":"北","distance":"58"},"pois":[],"roads":[],"poiRegions":[],"sematic_description":"","cityCode":289}
     */

    public int status;
    public String message;
    public ResultBean result;

    public static class ResultBean {
        /**
         * location : {"lng":121.49884033193993,"lat":31.225696429417987}
         * formatted_address : 上海市黄浦区净土街31弄-4号
         * business : 老西门,城隍庙,西藏南路
         * addressComponent : {"country":"中国","country_code":0,"country_code_iso":"CHN","country_code_iso2":"CN","province":"上海市","city":"上海市","city_level":2,"district":"黄浦区","town":"","town_code":"","adcode":"310101","street":"净土街","street_number":"31弄-4号","direction":"北","distance":"58"}
         * pois : []
         * roads : []
         * poiRegions : []
         * sematic_description :
         * cityCode : 289
         */

        public LocationBean location;
        public String               formatted_address;
        public String               business;
        public AddressComponentBean addressComponent;
        public String               sematic_description;
        public int                  cityCode;
        public List<?>              pois;
        public List<?>              roads;
        public List<?>              poiRegions;

        public static class LocationBean {
            /**
             * lng : 121.49884033193993
             * lat : 31.225696429417987
             */

            public double lng;
            public double lat;
        }

        public static class AddressComponentBean {
            /**
             * country : 中国
             * country_code : 0
             * country_code_iso : CHN
             * country_code_iso2 : CN
             * province : 上海市
             * city : 上海市
             * city_level : 2
             * district : 黄浦区
             * town :
             * town_code :
             * adcode : 310101
             * street : 净土街
             * street_number : 31弄-4号
             * direction : 北
             * distance : 58
             */

            public String country;
            public int    country_code;
            public String country_code_iso;
            public String country_code_iso2;
            public String province;
            public String city;
            public int    city_level;
            public String district;
            public String town;
            public String town_code;
            public String adcode;
            public String street;
            public String street_number;
            public String direction;
            public String distance;
        }
    }
}
