package com.actor.myandroidframework.utils.baidu;

/**
 * Description: 坐标
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/23 on 09:22
 * @version 1.0
 */
public class LngLatInfo {

    /**
     * status : 0
     * result : {"location":{"lng":106.55843415537664,"lat":29.568996245338923},"precise":0,
     * "confidence":0,"comprehension":100,"level":"城市"}
     */

    public int status;
    public ResultBean result;

    public static class ResultBean {
        /**
         * location : {"lng":106.55843415537664,"lat":29.568996245338923}
         * precise : 0
         * confidence : 0
         * comprehension : 100
         * level : 城市
         */

        public LocationBean location;
        public int    precise;
        public int    confidence;
        public int    comprehension;
        public String level;

        public static class LocationBean {
            /**
             * lng : 106.55843415537664
             * lat : 29.568996245338923
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

        @Override
        public String toString() {
            return "ResultBean{" +
                    "location=" + location +
                    ", precise=" + precise +
                    ", confidence=" + confidence +
                    ", comprehension=" + comprehension +
                    ", level='" + level + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LngLatInfo{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }
}
