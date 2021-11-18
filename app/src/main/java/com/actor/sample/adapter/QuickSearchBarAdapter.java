package com.actor.sample.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.sample.R;
import com.actor.sample.bean.Item;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * description: 主页->快速查找条
 * company    :
 *
 * @author : ldf
 * date       : 2021/8/14 on 21
 * @version 1.0
 */
public class QuickSearchBarAdapter extends BaseQuickAdapter<Item, BaseViewHolder> {

    public QuickSearchBarAdapter(@Nullable List<Item> data) {
        super(R.layout.item_select_dealer, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Item item) {
        int position = helper.getBindingAdapterPosition();
        helper.setText(R.id.tv_letter, item.getLetter())
                .setText(R.id.tv_contact, item.itemName)
                .setGone(R.id.tv_letter, position > 0 &&
                        TextUtils.equals(item.getLetter(), getData().get(position - 1).getLetter()));
    }
}
