package com.actor.sample.adapter;

import androidx.annotation.NonNull;

import com.actor.sample.R;
import com.actor.sample.bean.Item;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

/**
 * description: 主页->RecyclerView测试
 * company    :
 *
 * @author : ldf
 * date       : 2021/8/14 on 21
 * @version 1.0
 */
public class RecyclerViewTestAdapter extends BaseQuickAdapter<Item, BaseViewHolder> {

    public RecyclerViewTestAdapter(boolean isFlexbox, boolean isVertical) {
        super(isFlexbox ? R.layout.item_recycler_view_test_flexbox : isVertical ? R.layout.item_recycler_view_test_vertical : R.layout.item_recycler_view_test_horizontal);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Item item) {
        helper.setText(R.id.tv_contact, item.itemName);
    }
}
