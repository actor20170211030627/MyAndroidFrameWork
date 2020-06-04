package com.actor.myandroidframework.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * description: Gson 把 "" 转 int 报错问题
 * 怎么配置:
 * Gson gson = GsonUtils.getGson().newBuilder()
 *         .registerTypeAdapter(int.class, new IntTypeAdapter())
 *         .registerTypeAdapter(Integer.class, new IntTypeAdapter()).create();
 * GsonUtils.setGsonDelegate(gson);
 *
 * @author : 李大发
 * date       : 2020/6/4 on 18:14
 * @version 1.0
 */
class IntTypeAdapter extends TypeAdapter<Integer> {

    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        out.value(value);
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String result = in.nextString();
        if ("".equals(result)) return null;
        return Integer.parseInt(result);
    }
}
