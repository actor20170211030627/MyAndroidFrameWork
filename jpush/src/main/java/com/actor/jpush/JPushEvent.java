package com.actor.jpush;

import androidx.annotation.NonNull;

/**
 * description: 极光推送的Event事件
 * author     : ldf
 * date       : 2020/3/24 on 19:31
 *
 * @version 1.0
 */
public class JPushEvent<T> {
    public String action;
    public boolean boo;
    public T obj;

    public JPushEvent(String action) {
        this.action = action;
    }

    public JPushEvent(String action, boolean boo) {
        this.action = action;
        this.boo = boo;
    }

    public JPushEvent(String action, T obj) {
        this.action = action;
        this.obj = obj;
    }

    public JPushEvent(String action, boolean boo, T obj) {
        this.action = action;
        this.boo = boo;
        this.obj = obj;
    }

    @NonNull
    @Override
    public String toString() {
        return "JPushEvent{" +
                "action=" + action +
                ", boo=" + boo +
                ", obj=" + obj +
                '}';
    }
}
