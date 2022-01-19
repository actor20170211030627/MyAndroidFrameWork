package com.actor.myandroidframework.widget.chat.utils;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.AssetsUtils;
import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.actor.myandroidframework.widget.chat.bean.Emoji;
import com.blankj.utilcode.util.ImageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: 加载表情等, 初始化见{@link com.actor.myandroidframework.widget.chat.ChatLayoutKit ChatLayoutKit} <br/>
 * author     : ldf <br/>
 * date       : 2019/6/3 on 22:16
 * @version 1.0
 */
public class FaceManager {

    protected static final Application CONTEXT    = ConfigUtils.APPLICATION;
    //Emoji列表
    protected static final List<Emoji> EMOJI_LIST = new ArrayList<>();

    //设置 Emoji, 用于显示在下方TabLayout内
    public static Integer  emojiResShowInTabLayout;
    public static Drawable emojiDrawableShowInTabLayout;

    /**
     * 默认表情的正则:
     * \[    转义左中括号
     * \S    匹配所有非空白字符
     * ?     标记?之前的字符为"可选". 0 或 1 次
     */
    public static final    String      DEFAULT_EMOJI_REGEX = "\\[\\S+?]";
    //你加载的Emoji的正则, 默认 = ↑
    public static          String      EMOJI_REGEX = DEFAULT_EMOJI_REGEX;

    /**
     * 从 assets 文件夹中加载 Emojis, 加载的Emoji是无序的
     * @param regex 表情的匹配正则, 用于读取表情的输入意思, 示例: "[龇牙]" 的匹配是 {@link #DEFAULT_EMOJI_REGEX}
     * @param assetPathName 表情在assets下路径, 示例: "emoji"(表情在这个文件夹内)
     * @param listener 加载完成监听
     */
    public static void loadEmojisFromAssets(String regex, String assetPathName, int width,
                                            int height, OnLoadCompleteListener listener) {
        if (listener == null) return;
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                //emoji文件夹下所有表情
                String[] emojis = AssetsUtils.getFiles(assetPathName);
                if (emojis == null || emojis.length == 0) {
                    listener.onLoadComplete(null);
                } else {
                    List<Emoji> emojiList = new ArrayList<>(emojis.length);
                    Pattern p = Pattern.compile(regex);
                    for (String emoji : emojis) {//emoji图片表情名称: "[龇牙]@2x.png"
                        Matcher m = p.matcher(emoji);
                        if (m.find()) {
                            String emojiName = m.group();//匹配到的第一组 "[龇牙]"
                            //"[龇牙]", "emoji/[龇牙]@2x.png"
                            Emoji emoji1 = new Emoji(emojiName, assetPathName + "/" + emoji, width, height);
                            emojiList.add(emoji1);
                        }
                    }
                    listener.onLoadComplete(emojiList);
                }
            }
        });
    }

    /**
     * 从 assets 文件夹中加载 Emojis, 根据 emojiFilters 排序
     * @param emojiNames emoji表情名称列表, 用于从Assets中读取Emoji后排序, 示例: "[龇牙]"
     * @param assetPathName 表情在assets下路径, 示例: "emoji"(表情在这个文件夹内)
     * @param listener 加载完成监听
     */
    public static void loadEmojisFromAssets(List<String> emojiNames, String assetPathName,
                                            int width, int height, OnLoadCompleteListener listener) {
        if (listener == null) return;
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                //emoji文件夹下所有表情
                String[] emojis = AssetsUtils.getFiles(assetPathName);
                if (emojis == null || emojis.length == 0) {
                    listener.onLoadComplete(null);
                } else {
                    List<Emoji> emojiList = new ArrayList<>(emojis.length);
                    //emojiName: "[龇牙]"
                    for (String emojiName : emojiNames) {
                        //emoji图片表情名称: "[龇牙]@2x.png"
                        for (String emoji : emojis) {
                            if (emoji.contains(emojiName)) {
                                //"[龇牙]", "emoji/[龇牙]@2x.png"
                                Emoji emoji1 = new Emoji(emojiName, assetPathName + "/" + emoji, width, height);
                                emojiList.add(emoji1);
                            }
                        }
                    }
                    listener.onLoadComplete(emojiList);
                }
            }
        });
    }

    /**
     * 从 drawable/raw 中加载Emojis, 根据 emojiFilters 排序
     * @param emojiNames emoji表情名称列表, 示例: "[龇牙]"
     * @param emojiRess Emoji 在 drawable/rwa 里的resId, 示例: R.drawable.emoji_haha
     * @param width Emoji 宽度
     * @param height Emoji 高度
     * @param listener 加载完成监听
     */
    public static void loadEmojiFromDrawable$Raw(@NonNull List<String> emojiNames,
                                                 @NonNull List<Integer> emojiRess,
                                                 int width, int height,
                                                 @NonNull OnLoadCompleteListener listener) {
        //注意: 2个List都不能为空
        int size = Math.min(emojiNames.size(), emojiRess.size());
        List<Emoji> emojiList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Emoji emoji = new Emoji(emojiNames.get(i), emojiRess.get(i), width, height);
            emojiList.add(emoji);
        }
        listener.onLoadComplete(emojiList);
    }

    public interface OnLoadCompleteListener {
        /**
         * 加载完成
         */
        void onLoadComplete(List<Emoji> emojis);
    }

    /**
     * 给TextView 设置有 emoji 的文字
     * @param regex 用于匹配emoji的正则, 例: {@link #DEFAULT_EMOJI_REGEX}
     * @param content 包含emoji的内容
     */
    public static void handlerEmojiText(TextView textView, String regex, CharSequence content) {
        if(textView == null) return;
        if (TextUtils.isEmpty(content)) {
            textView.setText(content);
            return;
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();//[龇牙]
            for (Emoji emoji : EMOJI_LIST) {
                if (tempText.equals(emoji.filter)) {
                    Bitmap bitmap = null;
                    if (emoji.assetsPath != null) {//assets
                        bitmap = assets2Bitmap(emoji.assetsPath);
                        bitmap = ImageUtils.scale(bitmap, emoji.width, emoji.height, true);
                    } else if (emoji.drawable$RawId != null) {//drawable / raw
                        bitmap = ImageUtils.getBitmap(emoji.drawable$RawId);
                        bitmap = ImageUtils.scale(bitmap, emoji.width, emoji.height, true);
                    }
                    if (bitmap != null) {
                        //转换为Span, SPAN_INCLUSIVE_EXCLUSIVE: 2个Span之间不能输入文字...
                        ssb.setSpan(new ImageSpan(CONTEXT, bitmap), m.start(), m.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                }
            }
        }
        int selection = textView.getSelectionStart();
        textView.setText(ssb);
        if (textView instanceof EditText) {
            ((EditText) textView).setSelection(selection);
        }
    }

    /** Assets转Bitmap
     * @param assetsPath 在assets中的路径, 示例: "emoji/[龇牙]@2x.png"
     */
    public static Bitmap assets2Bitmap(String assetsPath) {
        AssetManager am = CONTEXT.getAssets();
        InputStream is = null;
        try {
            is = am.open(assetsPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 如果你加载了默认表情, 返回默认加载的表情列表
     */
    public static List<Emoji> getEmojiList() {
        return EMOJI_LIST;
    }

    /**
     * 设置表情列表
     * @param list Emoji表情列表
     * @param emojiRegex 加载的这些表情的正则识别
     */
    public static void setEmojiList(List<Emoji> list, String emojiRegex) {
        if (list != null) {
            EMOJI_LIST.clear();
            EMOJI_LIST.addAll(list);
            EMOJI_REGEX = emojiRegex;
        }
    }
}
