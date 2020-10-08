package com.actor.myandroidframework.utils.baidu;

/**
 * Description: 坐标
 * Author     : 李大发
 * Date       : 2019/4/23 on 09:22
 * @version 1.0
 */
public class LngLatInfo {

    /**
     * status : 0
     * result : {"location":{"lng":116.3084202915042,"lat":40.05703033345938},"precise":1,"confidence":80,"comprehension":100,"level":"门址"}
     */

    public int status;
    public String message;
    public ResultBean result;

    public static class ResultBean {
        /**
         * location : {"lng":116.3084202915042,"lat":40.05703033345938}
         * precise : 1
         * confidence : 80
         * comprehension : 100
         * level : 门址
         */

        public LocationBean location;
        public int    precise;
        public int    confidence;
        public int    comprehension;
        public String level;

        public static class LocationBean {
            /**
             * lng : 116.3084202915042
             * lat : 40.05703033345938
             */

            public double lng;
            public double lat;
        }
    }
}
