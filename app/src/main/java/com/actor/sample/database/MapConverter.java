package com.actor.sample.database;

import com.blankj.utilcode.util.GsonUtils;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Map;

/**
 * description: json和map参数转换
 *              1. 必须是 public
 *              2.如果写成内部类, 还需要加上 static
 *
 * @author    : 李大发
 * date       : 2020/4/21 on 14:32
 * @version 1.0
 */
public class MapConverter implements PropertyConverter<Map<String, Object>, String> {
    @Override
    public Map<String, Object> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) return null;
        return GsonUtils.fromJson(databaseValue, GsonUtils.getMapType(String.class, Object.class));
    }
    @Override
    public String convertToDatabaseValue(Map<String, Object> entityProperty) {
        if (entityProperty == null) return null;
        return GsonUtils.toJson(entityProperty);
    }
}
