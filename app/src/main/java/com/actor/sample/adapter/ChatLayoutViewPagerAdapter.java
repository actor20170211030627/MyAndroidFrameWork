package com.actor.sample.adapter;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.chat_layout.ChatLayout;
import com.actor.chat_layout.bean.ChatLayoutItemMore;
import com.actor.chat_layout.bean.Emoji;
import com.actor.chat_layout.utils.EmojiUtils;
import com.actor.chat_layout.fragment.ChatLayoutEmojiFragment;
import com.actor.chat_layout.fragment.ChatLayoutMoreFragment;
import com.actor.chat_layout.emoji.TIMSDKEmoji;
import com.actor.myandroidframework.adapter_viewpager.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.MyApplication;
import com.actor.sample.R;
import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2018/8/2 on 13
 * @version 1.0
 */
public class ChatLayoutViewPagerAdapter extends BaseFragmentStatePagerAdapter {

    private final ChatLayout chatLayout;

    public ChatLayoutViewPagerAdapter(FragmentManager fm, int size, ChatLayout chatLayout) {
        super(fm, size);
        this.chatLayout = chatLayout;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //Emoji表情Fragment
                List<Emoji> emojiList = MyApplication.emojis0;
                ChatLayoutEmojiFragment emojiFragment = ChatLayoutEmojiFragment.newInstance(8, SizeUtils.dp2px(10), emojiList);
                emojiFragment.setOnEmojiClickListener(new ChatLayoutEmojiFragment.OnEmojiClickListener() {
                    @Override
                    public void onEmojiDelete() {
                        chatLayout.onKeyDownDelete();
                    }

                    @Override
                    public void onEmojiClick(Emoji emoji) {
                        chatLayout.insert(emoji.filter);
                        EditText editText = chatLayout.getEditText();
                        EmojiUtils.handlerEmojiText(editText, MyApplication.emojis0, TIMSDKEmoji.EMOJI_REGEX, editText.getText());
                    }
                });
                return emojiFragment;
            default:
                return getMoreFragment();
        }
    }

    //MoreFragment
    private Fragment getMoreFragment() {
        final ArrayList<ChatLayoutItemMore> bottomViewDatas = new ArrayList<>();
        //右下角⊕More
        for (int i = 0; i < 7; i++) {
            boolean flag = i % 2 == 0;
            int imgRes = flag ? R.drawable.camera : R.drawable.picture;
            bottomViewDatas.add(new ChatLayoutItemMore(imgRes, "Item" + i));
        }

        ChatLayoutMoreFragment moreFragment = ChatLayoutMoreFragment.newInstance(4, 50, bottomViewDatas);
        moreFragment.setOnItemClickListener(new ChatLayoutMoreFragment.OnItemClickListener() {
            //更多点击
            @Override
            public void onItemClick(int position, ChatLayoutItemMore itemMore) {
                ToasterUtils.info(itemMore.itemText);
            }
        });
        return moreFragment;
    }
}
