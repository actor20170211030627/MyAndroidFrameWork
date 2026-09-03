package com.actor.sample.adapter;

import android.media.MediaPlayer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.audio.MediaPlayerCallback;
import com.actor.myandroidframework.utils.audio.MediaPlayerUtils;
import com.actor.myandroidframework.widget.DrawableTextView;
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
        addChildClickViewIds(R.id.dtv_contact);
        setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
            case R.id.dtv_contact:
                ((DrawableTextView) view).startPlayAnim();
                MediaPlayerUtils.getInstance().playRaw(R.raw.one_kun, new MediaPlayerCallback(position) {
                    @Override
                    public void onCompletion2(@Nullable MediaPlayer mp) {
                        int tagPos = getPlayerTag();
                        View viewByPosition = getViewByPosition(tagPos, R.id.dtv_contact);
                        if (viewByPosition instanceof DrawableTextView) ((DrawableTextView) viewByPosition).stopPlayAnim();
                    }
                });
                break;
            default:
                break;
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Item item) {
        helper.setText(R.id.dtv_contact, item.itemName);
    }
}
