package com.actor.sample.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.actor.myandroidframework.recyclerview.WheelViewAdapter;
import com.actor.myandroidframework.recyclerview.WheelViewLayoutManager;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.sample.R;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * description: WheelView效果测试
 * company    :
 *
 * @author : ldf
 * date       : 2025/2/11 on 15
 * @version 1.0
 */
public class MyWheelViewTestAdapter extends WheelViewAdapter<String> {
    /**
     * @param layoutResId            item布局
     * @param isInfinityLoop         是否无限循环
     * @param itemClickScroll2Center item点击是否自动滚动到中间
     */
    public MyWheelViewTestAdapter(@NonNull WheelViewLayoutManager wheelViewLayoutManager,
                                  @LayoutRes int layoutResId, boolean isInfinityLoop,
                                  boolean itemClickScroll2Center) {
        super(wheelViewLayoutManager, layoutResId, isInfinityLoop, itemClickScroll2Center);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String item) {
        LogUtils.errorFormat("holder = %s, item = %s", holder, item);
        holder.setVisible(R.id.iv, true).setVisible(R.id.tv, true)
                .setImageResource(R.id.iv, R.drawable.logo)
                .setText(R.id.tv, item);
        if (holder.getAbsoluteAdapterPosition() == wheelViewSelectedPos) {
            holder.setTextColorRes(R.id.tv, R.color.red);
        } else {
            holder.setTextColorRes(R.id.tv, R.color.black);
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String item, @NonNull List<?> payloads) {
        super.convert(holder, item, payloads);
        Object o = payloads.get(0);
        if (o instanceof Boolean) {
            boolean isSelected = (boolean) o;
            holder.setTextColorRes(R.id.tv, isSelected ? R.color.red : R.color.black);
        }
    }
}
