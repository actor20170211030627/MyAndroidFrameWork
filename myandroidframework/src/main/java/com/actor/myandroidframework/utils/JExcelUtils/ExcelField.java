package com.actor.myandroidframework.utils.JExcelUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 需要写入到Excel中的字段
 * Author     : 李大发
 * Date       : 2019/10/31 on 19:46
 *
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {

    /**
     * @return 该字段写入到Excel中的第几列(从0开始), 如果不写入Excel的字段就不要写注解
     */
    int columnIndex();

    /**
     * @return 如果是Date类型的数据, 需要返回格式化类型
     */
    String dateFieldFormat() default "yyyy-MM-dd HH:mm:ss";
}
