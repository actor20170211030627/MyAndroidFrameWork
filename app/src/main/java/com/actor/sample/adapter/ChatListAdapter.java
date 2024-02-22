package com.actor.sample.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.chat_layout.emoji.FaceManager;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.audio.AudioUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.info.MessageItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * description: 聊天列表的Adapter
 *
 * @author    : ldf
 * date       : 2021/5/14 on 18
 * @version 1.0
 */
public class ChatListAdapter extends BaseQuickAdapter<MessageItem, BaseViewHolder> {

    public ChatListAdapter(@Nullable List<MessageItem> data) {
        super(R.layout.item_chat_contact, data);
        //item点击
        addChildClickViewIds(R.id.tv);
        setOnItemClickListener((adapter, view, position) -> {
            MessageItem item = getItem(position);
            if (item != null) {
                String message = item.message;
                if (message != null) {
                    ToasterUtils.info(message);
                } else {
                    AudioUtils.getInstance().play(item.audioPath, false, true, null);
                }
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MessageItem item) {
        TextView tv = helper.getView(R.id.tv);
        String message = item.message;
        if (message != null) {
            FaceManager.handlerEmojiText(tv, FaceManager.EMOJI_REGEX, message);
        } else {
            tv.setText(TextUtils2.getStringFormat("audioPath = %s\ndurationMs = %dms", item.audioPath, item.durationMs));
        }
    }
}
