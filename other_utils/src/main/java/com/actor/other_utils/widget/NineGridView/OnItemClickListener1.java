package com.actor.other_utils.widget.NineGridView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

/**
 * description: NineGridView 的Item点击事件监听 <br />
 * Author     : ldf <br />
 * date       : 2021/12/21 on 20
 * @version 1.0
 */
public interface OnItemClickListener1<T extends GetIsVideoAble> {
    /**
     * item点击事件
     * @param nineGridView
     * @param item 数据
     * @param adapter
     * @param view
     * @param position 位置
     */
    void onItemClick(NineGridView<T> nineGridView, T item, @Nullable BaseQuickAdapter<T, BaseViewHolder> adapter, @NonNull View view, int position);
}
