package com.actor.chat_layout.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.chat_layout.R;
import com.actor.chat_layout.adapter.ChatLayoutEmojiAdapter;
import com.actor.chat_layout.bean.Emoji;
import com.actor.chat_layout.utils.EmojiUtils;
import com.actor.myandroidframework.fragment.ActorBaseFragment;
import com.actor.myandroidframework.recyclerview.BaseItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;

/**
 * Description: Emoji 的 Fragment
 * Date       : 2019/6/2 on 20:08
 * @version 1.0
 */
public class ChatLayoutEmojiFragment extends ActorBaseFragment {

    public static final String                SPAN_COUNT = "SPAN_COUNT";
    public static final String                ITEM_DECORATION = "ITEM_DECORATION";
    public static final String                ITEMS = "ITEMS";

    protected RecyclerView recyclerView;

    protected int                  itemDecorationPx;
    //加载的Emoji表情列表
    protected static List<Emoji>          emojiList;
    protected OnEmojiClickListener   emojiClickListener;
    protected ChatLayoutEmojiAdapter myAdapter;

    /**
     * @param spanCount 表情显示成多少列, 例: 8
     * @param itemDecorationPx 表情之间间隔, 例: SizeUtils.dp2px(10)
     * @param emojiList 表情列表, 例: {@link EmojiUtils#loadEmojisFromAssets(List, String, int, int, EmojiUtils.OnLoadCompleteListener)} ()}
     */
    public static ChatLayoutEmojiFragment newInstance(int spanCount, @Px int itemDecorationPx, @NonNull List<Emoji> emojiList) {
        ChatLayoutEmojiFragment fragment = new ChatLayoutEmojiFragment();
        Bundle args = new Bundle();
        args.putInt(SPAN_COUNT, spanCount);
        args.putInt(ITEM_DECORATION, itemDecorationPx);
        ChatLayoutEmojiFragment.emojiList = emojiList;
//        args.putParcelableArrayList(ITEMS, items);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_layout_emoji, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_for_chat_layout_emoji_fragment);
        //删除
        view.findViewById(R.id.iv_delete_for_chat_layout_emoji_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiClickListener != null) {
                    emojiClickListener.onEmojiDelete();
                }
            }
        });

        myAdapter = new ChatLayoutEmojiAdapter(emojiList);
        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (emojiClickListener != null) {
                    emojiClickListener.onEmojiClick(myAdapter.getItem(position));
                }
            }
        });
        recyclerView.addItemDecoration(new BaseItemDecoration(itemDecorationPx, itemDecorationPx));
        recyclerView.setAdapter(myAdapter);
    }

    /**
     * 设置 Emoji 点击事件监听
     */
    public void setOnEmojiClickListener(OnEmojiClickListener onEmojiClickListener) {
        this.emojiClickListener = onEmojiClickListener;
    }

    public interface OnEmojiClickListener {
        /**
         * 当点击删除的时候
         */
        void onEmojiDelete();

        /**
         * 当点击 Emoji 的时候
         */
        void onEmojiClick(Emoji emoji);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //不能clear(), ∵是同1个引用
//        if (emojiList != null) emojiList.clear();
        emojiList = null;
        emojiClickListener = null;
        myAdapter = null;
    }
}
