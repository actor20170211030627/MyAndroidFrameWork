package com.actor.myandroidframework.widget.NineGridView;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 九宫格 自定义item, 使用注意: List中的数据必须implements {@link GetIsVideoAble}, 否则报错.
 * Author     : 李大发
 * Date       : 2019/5/16 on 14:47
 * @version 1.0
 */
public class NineGridView extends ConstraintLayout {

    protected ImageView                    ivPicForNineGridView;
    protected ImageView                    ivPlayPauseForNineGridView;
    protected RecyclerView                 recyclerViewForNineGridView;
    private List<? extends GetIsVideoAble> items = new ArrayList<>();
    private OnItemClickListener            onItemClickListener;
    private MyAdapter                      myAdapter;

    public NineGridView(Context context) {
        super(context);
        initView();
    }

    public NineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NineGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_for_nine_grid_view, this);
        ivPicForNineGridView = view.findViewById(R.id.iv_pic_for_nine_grid_view);
        ivPlayPauseForNineGridView = view.findViewById(R.id.iv_play_pause_for_nine_grid_view);
        recyclerViewForNineGridView = view.findViewById(R.id.recycler_view_for_nine_grid_view);

//        int screenWidth = ScreenUtils.getScreenWidth();
//        LayoutParams layoutParams = (LayoutParams) ivPic.getLayoutParams();
//        layoutParams.matchConstraintPercentWidth = 0.3f;
//        ivPic.setMaxWidth(getScreenWidth() / 3);
//        ivPic.setMaxHeight(getScreenWidth() / 3);


    }

    /**
     * 注意, List里的数据必须 implements GetIsVideoAble, 否则报错. {@link GetIsVideoAble}
     * @param datas
     */
    public void setData(List datas) {
        items.clear();
        if (datas != null) items.addAll(datas);
        if (items.size() > 0) {
            setVisibility(VISIBLE);
            if (items.size() == 1) {
                GetIsVideoAble getIsVideoAble = items.get(0);
                ivPicForNineGridView.setVisibility(VISIBLE);
                ivPlayPauseForNineGridView.setVisibility(getIsVideoAble.isVideo() ? VISIBLE : GONE);
                recyclerViewForNineGridView.setVisibility(GONE);
                Glide.with(this).load(getIsVideoAble.getUrl())
                        .placeholder(R.drawable.ic_holder_light_for_nine_grid_view)
                        .error(R.drawable.ic_holder_light_for_nine_grid_view)
                        .transform(new RoundedCorners(10))
                        .into(ivPicForNineGridView);
                ivPicForNineGridView.setOnClickListener(v -> {
                    if (onItemClickListener != null) onItemClickListener.onItemClick(this, items, null, ivPicForNineGridView, 0);
                });
            } else {
                ivPicForNineGridView.setVisibility(GONE);
                ivPlayPauseForNineGridView.setVisibility(GONE);
                recyclerViewForNineGridView.setVisibility(VISIBLE);
                myAdapter = new MyAdapter(R.layout.item_for_nine_grid_view, items);
                myAdapter.setOnItemClickListener((adapter, view1, position) -> {
                    if (onItemClickListener != null) onItemClickListener.onItemClick(this, items, adapter, view1, position);
                });
                recyclerViewForNineGridView.setAdapter(myAdapter);
                recyclerViewForNineGridView.addItemDecoration(new BaseItemDecoration(10, 10));
                myAdapter.notifyDataSetChanged();
            }
        } else setVisibility(GONE);
    }

    /**
     * 设置Item点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(NineGridView nineGridView, List<T> datas, BaseQuickAdapter adapter, View view, int position);
    }

    private class MyAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

        public MyAdapter(int layoutResId, List items) {
            super(layoutResId, items);
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            if (item != null) {
                if (item instanceof GetIsVideoAble) {
                    GetIsVideoAble getIsVideoAble = (GetIsVideoAble) item;
                    ImageView ivPic = helper.setVisible(R.id.iv_play_pause, getIsVideoAble.isVideo())
                            .getView(R.id.iv_pic_for_nine_grid_view);
                    Glide.with(getContext()).load(getIsVideoAble.getUrl())
                            .placeholder(R.drawable.ic_holder_light_for_nine_grid_view)
                            .error(R.drawable.ic_holder_light_for_nine_grid_view)
                            .transform(new CenterCrop(), new RoundedCorners(10))
                            .into(ivPic);
                }
            }
        }
    }
}
