package com.actor.myandroidframework.widget.chat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.fragment.ActorBaseFragment;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.actor.myandroidframework.widget.chat.adapter.ChatLayoutEmojiAdapter;
import com.actor.myandroidframework.widget.chat.bean.Emoji;
import com.actor.myandroidframework.widget.chat.utils.FaceManager;
import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;

/**
 * Description: Emoji 的 Fragment
 * Date       : 2019/6/2 on 20:08
 * @version 1.0
 */
public class ChatLayoutEmojiFragment extends ActorBaseFragment implements View.OnClickListener {

    protected RecyclerView recyclerView;

    protected int                    dp10;
    //加载的Emoji表情列表
    protected List<Emoji>            emojiList;
    protected OnEmojiClickListener   emojiClickListener;
    protected ChatLayoutEmojiAdapter myAdapter;

    /**
     * @param spanCount 表情显示成多少列
     * @param itemDecorationPx 表情之间间隔
     * @param items 表情列表
     */
    public static ChatLayoutEmojiFragment newInstance(/*int spanCount, int itemDecorationPx, ArrayList<ItemMore> items*/) {
        ChatLayoutEmojiFragment fragment = new ChatLayoutEmojiFragment();
//        Bundle args = new Bundle();
//        args.putInt(SPAN_COUNT, spanCount);
//        args.putInt(ITEM_DECORATION, itemDecorationPx);
//        args.putParcelableArrayList(ITEMS, items);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_layout_emoji, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_for_chat_layout_emoji_fragment);
        //删除
        view.findViewById(R.id.iv_delete_for_chat_layout_emoji_fragment).setOnClickListener(this);

        dp10 = ConvertUtils.dp2px(10);
        //获取加载的Emoji表情列表
        emojiList = FaceManager.getEmojiList();

        myAdapter = new ChatLayoutEmojiAdapter(emojiList);
        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (emojiClickListener != null) {
                    emojiClickListener.onEmojiClick(myAdapter.getItem(position));
                }
            }
        });
        recyclerView.addItemDecoration(new BaseItemDecoration(dp10, dp10));
        recyclerView.setAdapter(myAdapter);
    }

    //点击事件, 在 library 中不能用: switch case
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_delete_for_chat_layout_emoji_fragment) {//删除
            if (emojiClickListener != null) {
                emojiClickListener.onEmojiDelete();
            }
        }
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
        emojiList = null;
        emojiClickListener = null;
        myAdapter = null;
    }
}
