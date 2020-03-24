package com.actor.myandroidframework.utils;

/**
 * Description: Eventbus事件实体类
 * Author     : 李大发
 * Date       : 2019/4/1 on 11:54
 * @version 1.0
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

    @Override
    public String toString() {
        return "EventBusEvent{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", obj=" + obj +
                '}';
    }
}
