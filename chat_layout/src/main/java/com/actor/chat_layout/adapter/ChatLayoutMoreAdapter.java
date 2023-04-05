package com.actor.chat_layout.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.chat_layout.R;
import com.actor.chat_layout.bean.ChatLayoutItemMore;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * description: "更多"的Adapter <br />
 * Author     : ldf <br />
 * date       : 2022/1/18 on 17
 * @version 1.0
 */
public class ChatLayoutMoreAdapter extends BaseQuickAdapter<ChatLayoutItemMore, BaseViewHolder> {

    public ChatLayoutMoreAdapter(@Nullable List<ChatLayoutItemMore> data) {
        super(R.layout.item_for_chat_layout_bottom, data);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, ChatLayoutItemMore item) {
        ImageView iv = helper.setText(R.id.tv, item.itemText).getView(R.id.iv);
        Glide.with(iv).load(item.itemIcon).into(iv);
    }
}
