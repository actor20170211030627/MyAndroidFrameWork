package com.actor.sample.adapter;

import android.app.Activity;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.picture_selector.PictureSelectorUtils;
import com.actor.sample.R;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2022/3/5 on 20
 * @version 1.0
 */
public class ViewPagerHeightAutoCaculatePicsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final int dp5;

    public ViewPagerHeightAutoCaculatePicsAdapter() {
        super(R.layout.item_view_pager_height_auto_caculate_pic);
        dp5 = SizeUtils.dp2px(5);
        addChildClickViewIds(R.id.iv_pic);
        setOnItemChildClickListener((adapter, view, position) -> {
            Activity context = (Activity) getContext();
            PictureSelectorUtils.previewImageVideosS(context, position, getData());
        });

        List<String> data = getData();
        data.add(Global.girl);
        data.add(Global.girl);
        data.add(Global.girl);
        setList(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String dataBean) {
        ImageView iv = baseViewHolder.getView(R.id.iv_pic);
        Glide.with(iv).load(dataBean).transform(new RoundedCorners(dp5)).into(iv);
    }
}
