package com.actor.myandroidframework.widget.chat.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description: MoreFragment 中的item
 * Author     : ldf
 * Date       : 2019/6/2 on 21:33
 */
public class ChatLayoutItemMore implements Parcelable {

    //Item 的 resId
    public int itemIcon;
    //Item图标地址
    public String itemIconUrl;
    //Item名称
    public String itemText;

    /**
     * @param itemIconUrl 图标地址
     * @param itemText 文字
     */
    public ChatLayoutItemMore(String itemIconUrl, String itemText) {
        this.itemIconUrl = itemIconUrl;
        this.itemText = itemText;
    }

    /**
     * @param itemIcon 图标
     * @param itemText 文字
     */
    public ChatLayoutItemMore(int itemIcon, String itemText) {
        this.itemIcon = itemIcon;
        this.itemText = itemText;
    }

    protected ChatLayoutItemMore(Parcel in) {
        itemIcon = in.readInt();
        itemIconUrl = in.readString();
        itemText = in.readString();
    }

    //必须要有一个非空的静态变量 CREATOR
    public static final Creator<ChatLayoutItemMore> CREATOR = new Creator<ChatLayoutItemMore>() {
        @Override
        public ChatLayoutItemMore createFromParcel(Parcel in) {
            return new ChatLayoutItemMore(in);
        }

        @Override
        public ChatLayoutItemMore[] newArray(int size) {
            return new ChatLayoutItemMore[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemIcon);
        dest.writeString(itemIconUrl);
        dest.writeString(itemText);
    }
}
