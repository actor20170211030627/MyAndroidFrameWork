package com.actor.sample.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.actor.sample.R;
import com.actor.sample.bean.Item;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

/**
 * description: 主页->快速查找条
 * company    :
 *
 * @author : ldf
 * date       : 2021/8/14 on 21
 * @version 1.0
 */
public class QuickSearchBarAdapter extends BaseQuickAdapter<Item, BaseViewHolder> {

    public QuickSearchBarAdapter() {
        super(R.layout.item_quick_search_bar);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Item item) {
        int position = helper.getBindingAdapterPosition();
        String letter = item.getLetter();
        helper.setText(R.id.tv_letter, letter)
                .setText(R.id.tv_contact, item.itemName)
                .setGone(R.id.tv_letter, position > 0 &&
                        TextUtils.equals(letter, getItem(position - 1).getLetter()));
    }
}
