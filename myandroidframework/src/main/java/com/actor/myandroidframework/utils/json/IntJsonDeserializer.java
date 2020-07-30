package com.actor.myandroidframework.utils.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * description: 解决Gson 把 "" 转 int 报错问题(反序列化阶段)
 * 在Application中配置:
 *
 * IntJsonDeserializer intJsonDeserializer = new IntJsonDeserializer();
 * Gson gson = GsonUtils.getGson().newBuilder()
 *         .registerTypeAdapter(int.class, intJsonDeserializer)
 *         .registerTypeAdapter(Integer.class, intJsonDeserializer).create();
 * GsonUtils.setGsonDelegate(gson);
 *
 * @author : 李大发
 * date       : 2020/7/30 on 18:51
 * @version 1.0
 */
public class IntJsonDeserializer implements JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        boolean isJsonNull = json.isJsonNull();
        if (isJsonNull) return null;

        String string = json.getAsString();
        if ("".equals(string)) return null;

        return json.getAsInt();
    }
}
