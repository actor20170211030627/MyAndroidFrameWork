package com.actor.myandroidframework.utils;

/**
 * Description: Eventbus事件实体类
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/1 on 11:54
 */
public class EventBusEvent<T> {

    public int code;
    public String msg;
    public T obj;

    public EventBusEvent(int code) {
        this.code = code;
    }

    public EventBusEvent(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public EventBusEvent(int code, T obj) {
        this.code = code;
        this.obj = obj;
    }

    public EventBusEvent(int code, String msg, T obj) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }
}
