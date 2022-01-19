package com.actor.myandroidframework.widget.chat;

import android.graphics.Bitmap;

import com.actor.myandroidframework.widget.chat.bean.Emoji;
import com.actor.myandroidframework.widget.chat.utils.FaceManager;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ImageUtils;

import java.util.Arrays;
import java.util.List;

/**
 * description: 用作初始化emoji, Application中初始化
 * author     : ldf
 * date       : 2019/6/3 on 20:45
 * @version 1.0
 */
public class ChatLayoutKit {

    //emoji默认尺寸
    public static final int EMOJI_DEAULT_SIZE = ConvertUtils.dp2px(25);
    //设置emoji尺寸
    public static int EMOJI_SIZE = EMOJI_DEAULT_SIZE;

    public static void init(String[] emojiNames, String assetPathName) {
        init(Arrays.asList(emojiNames), assetPathName);
    }

    public static void init(List<String> emojiNames, String assetPathName) {
        init(emojiNames, assetPathName, EMOJI_DEAULT_SIZE);
    }

    public static void init(List<String> emojiNames, String assetPathName, int emojiSize) {
        init(emojiNames, assetPathName, emojiSize, FaceManager.DEFAULT_EMOJI_REGEX);
    }

    /**
     * Application中初始化, 如果不使用emoji, 就不用初始化
     * @param emojiNames emoji名称列表, 示例:"[龇牙]", "[调皮]" ...
     * @param assetPathName 表情在assets下路径, 示例: "emoji"(表情在这个文件夹内)
     * @param emojiSize 表情宽高
     * @param emojiRegex 加载的这些表情的正则识别
     */
    public static void init(List<String> emojiNames, String assetPathName, int emojiSize, String emojiRegex) {
        ChatLayoutKit.EMOJI_SIZE = emojiSize;
        FaceManager.loadEmojisFromAssets(emojiNames, assetPathName, EMOJI_SIZE, EMOJI_SIZE,  new FaceManager.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(List<Emoji> emojis) {
                FaceManager.setEmojiList(emojis, emojiRegex);

                //设置默认Drawable, 用于显示在TabLayout下方
                String assetsPath = emojis.get(0).assetsPath;
                Bitmap bitmap = FaceManager.assets2Bitmap(assetsPath);
                FaceManager.emojiDrawableShowInTabLayout = ImageUtils.bitmap2Drawable(bitmap);
            }
        });
    }
}
