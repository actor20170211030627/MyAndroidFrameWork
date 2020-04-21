package com.actor.sample.info;

import java.util.List;

/**
 * description: 返回列表基类
 *
 * @author : 李大发
 * date       : 2020/4/20 on 18:35
 * @version 1.0
 */
public class BaseListInfo<T> {

    public int code;
    public String msg;
    public DataBean<T> data;

    public static class DataBean<T> {
        /**
         * total : 0
         * rows : [{"id":0,"landId":0,"panoramagramId":0,"landPositionOneId":0,"landPositionTwoId":0,"landPositionThreeId":0,"landPositionFourId":0,"time":"","trackRecord":"","place":"","coorDonate":"","villageName":"寡妇村","landName":"片块2-1"}]
         */

        public int     total;
        public List<T> rows;
    }
}
