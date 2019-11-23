package com.ly.sample.bean;

import com.actor.myandroidframework.utils.JExcelUtils.ExcelField;

import java.util.Date;

public class ExcelBean {

    @ExcelField(columnIndex = 0)
    public String  name;

    @ExcelField(columnIndex = 1)
    public int     age;

    @ExcelField(columnIndex = 2)
    public boolean boy;

    @ExcelField(columnIndex = 3/*, dateFieldFormat = "yyyy-MM-dd HH:mm:ss"*/)//这是默认格式, 可不写
    public Date    date;

    public ExcelBean(String name, int age, boolean boy, Date date) {
        this.name = name;
        this.age = age;
        this.boy = boy;
        this.date = date;
    }
}
