package com.actor.sample.adapter;

import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.sample.R;
import com.actor.sample.utils.ImageConstants;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * description: 主页->元素共享跳转
 * company    :
 *
 * @author : ldf
 * date       : 2021/8/14 on 19
 * @version 1.0
 */
public class SharedElementAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SharedElementAdapter(@Nullable List<String> data) {
        super(R.layout.item_shared_element, data);
        addChildClickViewIds(R.id.iv);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        int position = helper.getBindingAdapterPosition();
        ImageView iv = helper.getView(R.id.iv);
        String url = ImageConstants.IMAGE_SOURCE[position];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setTransitionName(url);//setTransitionName
        }
        Glide.with(iv).load(url).into(iv);
    }
}
