package com.actor.sample.info;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * description: 聊天消息
 *
 * @author : ldf
 * date       : 2021/5/26 on 16
 * @version 1.0
 */
public class MessageItem implements MultiItemEntity {

    public boolean isSelfMsg;

    public String message;

    public String audioPath;
    public long durationMs;

    public MessageItem(boolean isSelfMsg, String message) {
        this.isSelfMsg = isSelfMsg;
        this.message = message;
    }

    public MessageItem(boolean isSelfMsg, String audioPath, long durationMs) {
        this.isSelfMsg = isSelfMsg;
        this.audioPath = audioPath;
        this.durationMs = durationMs;
    }

    @Override
    public int getItemType() {
        return isSelfMsg ? 0 : 1;
    }
}
