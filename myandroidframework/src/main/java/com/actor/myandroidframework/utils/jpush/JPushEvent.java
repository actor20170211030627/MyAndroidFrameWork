package com.actor.myandroidframework.utils.jpush;

/**
 * description: 极光推送的Event事件
 * author     : 李大发
 * date       : 2020/3/24 on 19:31
 *
 * @version 1.0
 */
public class JPushEvent<T> {
    public int code;
    public boolean boo;
    public T obj;

    public JPushEvent(int code) {
        this.code = code;
    }

    public JPushEvent(int code, boolean boo) {
        this.code = code;
        this.boo = boo;
    }

    public JPushEvent(int code, T obj) {
        this.code = code;
        this.obj = obj;
    }

    public JPushEvent(int code, boolean boo, T obj) {
        this.code = code;
        this.boo = boo;
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "JPushEvent{" +
                "code=" + code +
                ", boo=" + boo +
                ", obj=" + obj +
                '}';
    }
}
