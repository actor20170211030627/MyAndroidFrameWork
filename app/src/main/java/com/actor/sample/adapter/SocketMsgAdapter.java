package com.actor.sample.adapter;

import androidx.annotation.NonNull;

import com.actor.sample.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

/**
 * description: Socket接收消息的Adapter
 * company    :
 *
 * @author : ldf
 * date       : 2022/2/26 on 14
 * @version 1.0
 */
public class SocketMsgAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SocketMsgAdapter() {
        super(R.layout.item_textview_textcolor_red);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv, s);
    }
}
