package com.actor.sample.bean;

import com.actor.myandroidframework.widget.QuickSearchBar;

/**
 * description: 主页->快速查找条->Item
 * company    :
 *
 * @author : ldf
 * date       : 2021/8/14 on 21
 * @version 1.0
 */
public class Item implements QuickSearchBar.PinYinSortAble {

    public String itemName;
    private String letter;

    public Item(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public String getLetter() {
        return letter;
    }

    @Override
    public String getSortString() {
        return itemName;
    }
}
