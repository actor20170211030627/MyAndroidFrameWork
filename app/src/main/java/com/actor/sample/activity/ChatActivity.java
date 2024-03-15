package com.actor.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.chat_layout.ChatLayout;
import com.actor.chat_layout.OnListener;
import com.actor.chat_layout.VoiceRecorderView;
import com.actor.chat_layout.bean.ChatLayoutItemMore;
import com.actor.chat_layout.fragment.ChatLayoutMoreFragment;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.ChatListAdapter;
import com.actor.sample.databinding.ActivityChatBinding;
import com.actor.sample.info.MessageItem;
import com.actor.sample.utils.CheckUpdateUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity<ActivityChatBinding> {

    private RecyclerView      recyclerView;
    private VoiceRecorderView voiceRecorder;
    private ChatLayout        chatLayout;

    private       ChatListAdapter               chatListAdapter;
    private final List<MessageItem>             items           = new ArrayList<>();
    private final ArrayList<ChatLayoutItemMore> bottomViewDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = viewBinding.recyclerView;
        voiceRecorder = viewBinding.voiceRecorder;
        chatLayout = viewBinding.chatLayout;

        //消息列表
        items.clear();
        chatListAdapter = new ChatListAdapter(items);
        for (int i = 0; i < 20; i++) {
            items.add(new MessageItem(i % 2 == 0, "Hello World!    " + i));
        }

        //右下角⊕More
        for (int i = 0; i < 8; i++) {
            boolean flag = i % 2 == 0;
            int imgRes = flag ? R.drawable.camera : R.drawable.picture;
            bottomViewDatas.add(new ChatLayoutItemMore(imgRes, "Item" + i));
        }

        chatLayout.init(recyclerView, voiceRecorder);

        //MoreFragment
        ChatLayoutMoreFragment moreFragment = ChatLayoutMoreFragment.newInstance(4, 50, bottomViewDatas);
        moreFragment.setOnItemClickListener(new ChatLayoutMoreFragment.OnItemClickListener() {
            //更多点击
            @Override
            public void onItemClick(int position, ChatLayoutItemMore itemMore) {
                ToasterUtils.info(itemMore.itemText);
            }
        });
        chatLayout.setBottomFragment(getSupportFragmentManager(), moreFragment);
        //set Tab Icon
//        chatLayout.getTabLayout().getTabAt(0).setIcon(R.drawable.emoji_small);
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
                chatLayout.showPermissionDialog();
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

        recyclerView.setAdapter(chatListAdapter = new ChatListAdapter(items));

        //检查更新
        new CheckUpdateUtils().check(this);
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
