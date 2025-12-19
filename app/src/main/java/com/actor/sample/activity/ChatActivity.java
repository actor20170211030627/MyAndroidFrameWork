package com.actor.sample.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.chat_layout.ChatLayout;
import com.actor.chat_layout.OnListener;
import com.actor.chat_layout.VoiceRecorderView;
import com.actor.chat_layout.bean.Emoji;
import com.actor.myandroidframework.utils.AssetsUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.MyApplication;
import com.actor.sample.R;
import com.actor.sample.adapter.ChatLayoutViewPagerAdapter;
import com.actor.sample.adapter.ChatListAdapter;
import com.actor.sample.databinding.ActivityChatBinding;
import com.actor.sample.info.MessageItem;
import com.blankj.utilcode.util.ImageUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity<ActivityChatBinding> {

    private RecyclerView      recyclerView;
    private VoiceRecorderView voiceRecorder;
    private ChatLayout        chatLayout;

    private       ChatListAdapter   chatListAdapter;
    private final List<MessageItem> items           = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = viewBinding.recyclerView;
        voiceRecorder = viewBinding.voiceRecorder;
        chatLayout = viewBinding.chatLayout;

        chatListAdapter = new ChatListAdapter(items);

        //消息列表
        items.clear();
        for (int i = 0; i < 20; i++) {
            items.add(new MessageItem(i % 2 == 0, "Hello World!    " + i));
        }

        chatLayout.init(recyclerView, voiceRecorder);
        chatLayout.setViewPagerAdapter(new ChatLayoutViewPagerAdapter(getSupportFragmentManager(), 2, chatLayout));

        //设置 TabLayout 的 TabItem 的 Icon
        TabLayout.Tab tabAt = chatLayout.getTabLayout().getTabAt(0);
        List<Emoji> emojis0 = MyApplication.emojis0;
        if (tabAt != null && emojis0 != null && !emojis0.isEmpty()) {
            Emoji emoji = emojis0.get(0);
            if (emoji.assetsPath != null) {
                Bitmap bitmap = AssetsUtils.toBitmap(emoji.assetsPath);
                LogUtils.errorFormat("bitmap = %s", bitmap);
                Drawable drawable = ImageUtils.bitmap2Drawable(bitmap);
                tabAt.setIcon(drawable);
            }
        }

        TabLayout.Tab tabAt1 = chatLayout.getTabLayout().getTabAt(1);
        if (tabAt1 != null) {
            tabAt1.setIcon(R.drawable.picture);
        }

        /**
         * 设置点击事件
         */
        chatLayout.setOnListener(new OnListener() {
            @Override
            public void onBtnSendClick(EditText etMsg) {
                //点击了"发送"按钮(Send Button Click)
                String msg = getText(etMsg);
                if (!TextUtils.isEmpty(msg)) {
                    etMsg.setText("");
                    chatListAdapter.addData(new MessageItem(true, msg));
                    recyclerView.scrollToPosition(chatListAdapter.getItemCount() - 1);
                }
            }

            //点击了"表情"按钮, 你可以不重写这个方法(overrideAble)
            @Override
            public void onIvEmojiClick(ImageView ivEmoji) {
                ToasterUtils.info("Emoji Click");
            }

            //点击了"⊕"按钮, 你可以不重写这个方法(overrideAble)
            @Override
            public void onIvPlusClick(ImageView ivPlus) {
                ToasterUtils.info("Plus Click");
            }

            //没语音权限, 你可以不重写这个方法(no voice record permissions, overrideAble)
            @Override
            public void onNoPermission(String permission) {
                //可以调用默认处理方法. 你也可以不调用这个方法, 自己处理(call default request permission method, or deal by yourself)
                chatLayout.showPermissionDialog(permission);
            }

            //录音成功, 你可以不重写这个方法(voice record success, overrideAble)
            @Override
            public void onVoiceRecordSuccess(@NonNull String audioPath, long durationMs) {
                chatListAdapter.addData(new MessageItem(true, audioPath, durationMs));
                recyclerView.scrollToPosition(chatListAdapter.getItemCount() - 1);
            }

            //录音失败, 你可以不重写这个方法(voice record failure, overrideAble)
            @Override
            public void onVoiceRecordError(Exception e) {//录音失败
                e.printStackTrace();
            }

            //还可重写其它方法override other method ...
        });

        recyclerView.setAdapter(chatListAdapter);
    }

    /**
     * 如果BottomView == Gone,才finish()掉activity
     */
    @Override
    public void onBackPressed() {
        if (chatLayout.isBottomViewGone()) {
            super.onBackPressed();
        }
    }
}
