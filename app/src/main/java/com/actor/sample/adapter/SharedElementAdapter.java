package com.actor.sample.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.glide.DrawableRequestListener;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.actor.sample.R;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.Arrays;

/**
 * description: 主页->元素共享跳转
 * company    :
 *
 * @author : ldf
 * date       : 2021/8/14 on 19
 * @version 1.0
 */
public class SharedElementAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private int shareElementPos = -1;

    public SharedElementAdapter(int shareElementPos) {
        super(R.layout.item_shared_element, Arrays.asList(ImageConstants.IMAGE_SOURCE));
        this.shareElementPos = shareElementPos;
        addChildClickViewIds(R.id.iv);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        int position = helper.getBindingAdapterPosition();
        ImageView iv = helper.setText(R.id.tv, "item " + position)
                .getView(R.id.iv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setTransitionName(Global.getListTransitionName(position, true));
        }
        Glide.with(iv)
                .load(ImageConstants.IMAGE_SOURCE[position])
                .listener(new DrawableRequestListener(position) {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startAnimation(getRequestTag());
                        return super.onLoadFailed(e, model, target, isFirstResource);
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startAnimation(getRequestTag());
                        return super.onResourceReady(resource, model, target, dataSource, isFirstResource);
                    }
                }).into(iv);
    }

    private void startAnimation(int position) {
        if (position == shareElementPos) {
            RecyclerView recyclerView = getRecyclerViewOrNull();
            if (recyclerView != null) {
                recyclerView.scrollToPosition(position);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = (Activity) getContext();
                        SharedElementUtils.startPostponedEnterTransition(activity);
                    }
                });
            }
        }
    }
}
