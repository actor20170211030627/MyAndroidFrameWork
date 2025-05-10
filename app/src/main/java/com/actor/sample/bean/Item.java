package com.actor.sample.bean;

/**
 * description: Item
 * company    :
 *
 * @author : ldf
 * date       : 2021/8/14 on 21
 * @version 1.0
 */
public class Item {

    public String itemName;
    public String letter;

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
}
