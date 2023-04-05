package com.actor.chat_layout.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.chat_layout.R;
import com.actor.chat_layout.adapter.ChatLayoutMoreAdapter;
import com.actor.chat_layout.bean.ChatLayoutItemMore;
import com.actor.myandroidframework.fragment.ActorBaseFragment;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

/**
 * Description: 更多
 * Author     : ldf
 * Date       : 2019/6/2 on 20:07
 */
public class ChatLayoutMoreFragment extends ActorBaseFragment {

    public static final String                SPAN_COUNT = "SPAN_COUNT";
    public static final String                ITEM_DECORATION = "ITEM_DECORATION";
    public static final String                        ITEMS = "ITEMS";
    private             int                 spanCount;
    private             int                 itemDecorationPx;
    private             RecyclerView                  recyclerView;
    private             ArrayList<ChatLayoutItemMore> items;
    private             OnItemClickListener   mListener;
    private             ChatLayoutMoreAdapter moreAdapter;

    /**
     * 获取实例
     * @param spanCount recyclerview 列数, 一般4列
     * @param itemDecorationPx item间距, 单位px, 比如: 50
     * @param items 填充到 recyclerview 中的数据
     */
    public static ChatLayoutMoreFragment newInstance(int spanCount, int itemDecorationPx, ArrayList<ChatLayoutItemMore> items) {
        ChatLayoutMoreFragment fragment = new ChatLayoutMoreFragment();
        Bundle args = new Bundle();
        args.putInt(SPAN_COUNT, spanCount);
        args.putInt(ITEM_DECORATION, itemDecorationPx);
        args.putParcelableArrayList(ITEMS, items);
        fragment.setArguments(args);
        return fragment;
    }

    //获取参数
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            spanCount = arguments.getInt(SPAN_COUNT);
            itemDecorationPx = arguments.getInt(ITEM_DECORATION);
            items = arguments.getParcelableArrayList(ITEMS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_chat_layout_more, container, false);
        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
        moreAdapter = new ChatLayoutMoreAdapter(items);
        moreAdapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (mListener != null) mListener.onItemClick(position, items.get(position));
            }
        });
        recyclerView.addItemDecoration(new BaseItemDecoration(itemDecorationPx, itemDecorationPx));
        recyclerView.setAdapter(moreAdapter);
    }

    /**
     * 设置 Item 点击监听
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ChatLayoutItemMore itemMore);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
        moreAdapter = null;
    }
}
