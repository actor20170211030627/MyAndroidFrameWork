package com.actor.chat_layout.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * description: Emoji表情实体类 <br />
 * author     : ldf <br />
 * date       : 2019/6/3 on 14:03
 */
public class Emoji {

    //如果Emoji来自assets, Glide加载时, 需要在前面加上这个前缀
    public static final String ASSETS_PREFIX = "file:///android_asset/";

    public int    groupId;//表情所在组id

    public String filter;//示例: "[龇牙]"

    //Emoji是否来自assets
//    public boolean isEmojiFromAssets;

    //在assets中的路径, 示例: "emoji/[龇牙]@2x.png", Glide加载时: Emoji.ASSETS_PREFIX + assetsPath
    @Nullable
    public String assetsPath;

    //drawable or raw 资源id
    @Nullable
    public Integer drawable$RawId;

    public int    width;
    public int    height;

    public Emoji(String filter, @NonNull String assetsPath, int width, int height) {
        this.filter = filter;
        this.assetsPath = assetsPath;
        this.width = width;
        this.height = height;
    }

    public Emoji(String filter, @NonNull Integer drawable$RawId, int width, int height) {
        this.filter = filter;
        this.drawable$RawId = drawable$RawId;
        this.width = width;
        this.height = height;
    }
}
