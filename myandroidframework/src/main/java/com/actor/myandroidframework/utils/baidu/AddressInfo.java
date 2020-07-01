package com.actor.myandroidframework.utils.baidu;

import java.util.List;

/**
 * Description: 解析成地址
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/23 on 09:00
 * @version 1.0
 */
public class AddressInfo {

    /**
     * status : 0
     * result : {"location":{"lng":106.59283599999999,"lat":29.543183012994206},
     * "formatted_address":"重庆市南岸区烟雨路","business":"海棠溪,南滨路,黄桷桠",
     * "addressComponent":{"country":"中国","country_code":0,"country_code_iso":"CHN",
     * "country_code_iso2":"CN","province":"重庆市","city":"重庆市","city_level":2,"district":"南岸区",
     * "town":"","adcode":"500108","street":"烟雨路","street_number":"","direction":"",
     * "distance":""},"pois":[],"roads":[],"poiRegions":[{"direction_desc":"内","name":"重庆万豪酒店",
     * "tag":"酒店;快捷酒店","uid":"6780d66548f6d482f6c59c4a","distance":"0"}],
     * "sematic_description":"重庆万豪酒店内,重庆国瑞中心附近14米","cityCode":132}
     */

    public int status;
    public ResultBean result;

    public static class ResultBean {
        /**
         * location : {"lng":106.59283599999999,"lat":29.543183012994206}
         * formatted_address : 重庆市南岸区烟雨路
         * business : 海棠溪,南滨路,黄桷桠
         * addressComponent : {"country":"中国","country_code":0,"country_code_iso":"CHN",
         * "country_code_iso2":"CN","province":"重庆市","city":"重庆市","city_level":2,
         * "district":"南岸区","town":"","adcode":"500108","street":"烟雨路","street_number":"",
         * "direction":"","distance":""}
         * pois : []
         * roads : []
         * poiRegions : [{"direction_desc":"内","name":"重庆万豪酒店","tag":"酒店;快捷酒店",
         * "uid":"6780d66548f6d482f6c59c4a","distance":"0"}]
         * sematic_description : 重庆万豪酒店内,重庆国瑞中心附近14米
         * cityCode : 132
         */

        public LocationBean location;
        public String               formatted_address;
        public String               business;
        public AddressComponentBean addressComponent;
        public String               sematic_description;
        public int                  cityCode;
        public List<?>              pois;
        public List<?>              roads;
        public List<PoiRegionsBean> poiRegions;

        public static class LocationBean {
            /**
             * lng : 106.59283599999999
             * lat : 29.543183012994206
             */

            public double lng;
            public double lat;

            @Override
            public String toString() {
                return "LocationBean{" +
                        "lng=" + lng +
                        ", lat=" + lat +
                        '}';
            }
        }

        public static class AddressComponentBean {
            /**
             * country : 中国
             * country_code : 0
             * country_code_iso : CHN
             * country_code_iso2 : CN
             * province : 重庆市
             * city : 重庆市
             * city_level : 2
             * district : 南岸区
             * town :
             * adcode : 500108
             * street : 烟雨路
             * street_number :
             * direction :
             * distance :
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
            public String adcode;
            public String street;
            public String street_number;
            public String direction;
            public String distance;

            @Override
            public String toString() {
                return "AddressComponentBean{" +
                        "country='" + country + '\'' +
                        ", country_code=" + country_code +
                        ", country_code_iso='" + country_code_iso + '\'' +
                        ", country_code_iso2='" + country_code_iso2 + '\'' +
                        ", province='" + province + '\'' +
                        ", city='" + city + '\'' +
                        ", city_level=" + city_level +
                        ", district='" + district + '\'' +
                        ", town='" + town + '\'' +
                        ", adcode='" + adcode + '\'' +
                        ", street='" + street + '\'' +
                        ", street_number='" + street_number + '\'' +
                        ", direction='" + direction + '\'' +
                        ", distance='" + distance + '\'' +
                        '}';
            }
        }

        public static class PoiRegionsBean {
            /**
             * direction_desc : 内
             * name : 重庆万豪酒店
             * tag : 酒店;快捷酒店
             * uid : 6780d66548f6d482f6c59c4a
             * distance : 0
             */

            public String direction_desc;
            public String name;
            public String tag;
            public String uid;
            public String distance;

            @Override
            public String toString() {
                return "PoiRegionsBean{" +
                        "direction_desc='" + direction_desc + '\'' +
                        ", name='" + name + '\'' +
                        ", tag='" + tag + '\'' +
                        ", uid='" + uid + '\'' +
                        ", distance='" + distance + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "location=" + location +
                    ", formatted_address='" + formatted_address + '\'' +
                    ", business='" + business + '\'' +
                    ", addressComponent=" + addressComponent +
                    ", sematic_description='" + sematic_description + '\'' +
                    ", cityCode=" + cityCode +
                    ", pois=" + pois +
                    ", roads=" + roads +
                    ", poiRegions=" + poiRegions +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AddressInfo{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }
}
