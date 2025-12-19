package com.actor.chat_layout.utils;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.chat_layout.bean.Emoji;
import com.actor.myandroidframework.utils.AssetsUtils;
import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.ThreadUtils;
import com.blankj.utilcode.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: Emoji工具类 <br/>
 * author     : ldf <br/>
 * date       : 2019/6/3 on 22:16
 * @version 1.0
 */
public class EmojiUtils {

    protected static final Application CONTEXT    = ConfigUtils.APPLICATION;

    /**
     * 从 main/assets 文件夹中加载 Emojis, 加载的Emoji是无序的
     * @param emojiNameRegex 表情名称的匹配正则, 用于读取表情的输入意思, 示例: "[龇牙]@2x.png" 匹配 "\\[\\S+?]" => "[龇牙]"
     * @param assetPathName 表情在assets目录下的哪个路径, 示例: "emoji"(表情在这个文件夹内)
     * @param emojiWidth emoji的宽度
     * @param emojiHeight emoji的高度
     * @param listener 加载完成监听
     */
    public static void loadEmojisFromAssets(String emojiNameRegex, @NonNull String assetPathName,
                                            int emojiWidth, int emojiHeight, OnLoadCompleteListener listener) {
        if (emojiNameRegex == null || assetPathName == null || listener == null) return;
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                //emoji文件夹下所有表情, [龇牙]@2x.png
                String[] emojis = AssetsUtils.getFiles(assetPathName);
                if (emojis == null || emojis.length == 0) {
                    listener.onLoadComplete(null);
                } else {
                    List<Emoji> emojiList = new ArrayList<>(emojis.length);
                    boolean isAssetPathEmpty = TextUtils.isEmpty(assetPathName);
                    Pattern p = Pattern.compile(emojiNameRegex);
                    for (String emoji : emojis) {//emoji图片表情名称: "[龇牙]@2x.png"
                        Matcher m = p.matcher(emoji);
                        if (m.find()) {
                            String emojiName = m.group();//匹配到的第一组 "[龇牙]"
                            //"[龇牙]", "emoji/[龇牙]@2x.png"
                            Emoji emoji1 = new Emoji(emojiName, isAssetPathEmpty ? emoji : assetPathName + "/" + emoji, emojiWidth, emojiHeight);
                            emojiList.add(emoji1);
                        }
                    }
                    listener.onLoadComplete(emojiList);
                }
            }
        });
    }

    /**
     * 从 main/assets 文件夹中加载 Emojis, 根据 emojiFilters 排序
     * @param emojiNames emoji表情名称列表, 用于从Assets中读取Emoji后排序, 示例: "[龇牙]"
     * @param assetPathName 表情在assets目录下的哪个路径, 示例: "emoji"(表情在这个文件夹内)
     * @param emojiWidth emoji的宽度
     * @param emojiHeight emoji的高度
     * @param listener 加载完成监听
     */
    public static void loadEmojisFromAssets(List<String> emojiNames, @NonNull String assetPathName,
                                            int emojiWidth, int emojiHeight, OnLoadCompleteListener listener) {
        if (emojiNames == null || emojiNames.isEmpty() || assetPathName == null || listener == null) return;
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                //emoji文件夹下所有表情, [龇牙]@2x.png
                String[] emojis = AssetsUtils.getFiles(assetPathName);
                if (emojis == null || emojis.length == 0) {
                    listener.onLoadComplete(null);
                } else {
                    List<Emoji> emojiList = new ArrayList<>(emojis.length);
                    boolean isAssetPathEmpty = TextUtils.isEmpty(assetPathName);
                    //emojiName: "[龇牙]"
                    for (String emojiName : emojiNames) {
                        //emoji图片表情名称: "[龇牙]@2x.png"
                        for (String emoji : emojis) {
                            if (emoji.contains(emojiName)) {
                                //"[龇牙]", "emoji/[龇牙]@2x.png"
                                Emoji emoji1 = new Emoji(emojiName, isAssetPathEmpty ? emoji : assetPathName + "/" + emoji, emojiWidth, emojiHeight);
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
     * @param emojiWidth emoji的宽度
     * @param emojiHeight emoji的高度
     * @param listener 加载完成监听
     */
    public static void loadEmojiFromDrawable$Raw(@NonNull List<String> emojiNames,
                                                 @NonNull List<Integer> emojiRess,
                                                 int emojiWidth, int emojiHeight,
                                                 @NonNull OnLoadCompleteListener listener) {
        //注意: 2个List都不能为空
        int size = Math.min(emojiNames.size(), emojiRess.size());
        List<Emoji> emojiList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Emoji emoji = new Emoji(emojiNames.get(i), emojiRess.get(i), emojiWidth, emojiHeight);
            emojiList.add(emoji);
        }
        listener.onLoadComplete(emojiList);
    }

    public interface OnLoadCompleteListener {
        /**
         * 加载完成
         */
        void onLoadComplete(@Nullable List<Emoji> emojis);
    }

    /**
     * 给TextView 设置有 emoji 的文字
     * @param emojis 表情列表
     * @param emojiNameRegex 用于匹配emoji的正则, 例: "[龇牙]" 匹配 "\\[\\S+?]"
     * @param content 包含emoji的内容
     */
    public static void handlerEmojiText(TextView textView, List<Emoji> emojis,
                                        String emojiNameRegex, CharSequence content) {
        if(textView == null) return;
        if (TextUtils.isEmpty(content) || emojis == null || emojis.isEmpty()) {
            textView.setText(content);
            return;
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        Pattern p = Pattern.compile(emojiNameRegex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();//[龇牙]
            for (Emoji emoji : emojis) {
                if (tempText.equals(emoji.filter)) {
                    Bitmap bitmap = null;
                    if (emoji.assetsPath != null) {//assets
                        bitmap = AssetsUtils.toBitmap(emoji.assetsPath);
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
}
