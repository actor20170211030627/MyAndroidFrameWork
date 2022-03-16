package com.actor.myandroidframework.widget.chat.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.widget.chat.bean.Emoji;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * description: Emoji的Adapter <br />
 * Author     : ldf <br />
 * date       : 2022/1/18 on 17
 * @version 1.0
 */
public class ChatLayoutEmojiAdapter extends BaseQuickAdapter<Emoji, BaseViewHolder> {

    /**
     * @param emojiList emoji表情列表
     */
    public ChatLayoutEmojiAdapter(List<Emoji> emojiList) {
        super(R.layout.item_for_chat_layout_emoji_fragment, emojiList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Emoji item) {
        ImageView ivEmoji = helper.getView(R.id.iv_item_for_chat_layout_emoji_fragment);
        if (item.assetsPath != null) {
            //emoji from assets
            Glide.with(ivEmoji).load(Emoji.ASSETS_PREFIX + item.assetsPath).into(ivEmoji);
        } else {
            Glide.with(ivEmoji).load(item.drawable$RawId).into(ivEmoji);
        }
    }
}
