package com.actor.myandroidframework.widget.NineGridView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 九宫格 自定义item, 使用注意: List中的数据必须implements {@link GetIsVideoAble}, 否则报错.
 * 示例使用:
 * @BindView(R.id.nine_grid_view)
 * NineGridView<PicOrVideo> nineGridView; //class PicOrVideo implements GetIsVideoAble
 *
 * private List<PicOrVideo> items = new ArrayList<>();
 *
 * nineGridView.setData(items);
 * nineGridView.setOnItemClickListener(new NineGridView.OnItemClickListener1<PicOrVideo>() {
 *     @Override
 *     public void onItemClick(NineGridView<PicOrVideo> nineGridView, PicOrVideo item, BaseQuickAdapter<PicOrVideo, BaseViewHolder> adapter, View view, int position) {
 *         toastFormat("position=%d, isVideo=%b", position, item.isVideo());
 *     }
 * });
 *
 * Author     : ldf
 * Date       : 2019/5/16 on 14:47
 * @version 1.0
 */
// TODO: 2021/4/19 优化成一个控件
public class NineGridView<T extends GetIsVideoAble> extends ConstraintLayout {

    protected ImageView           ivPicForNineGridView;
    protected ImageView           ivPlayPauseForNineGridView;
    protected RecyclerView        recyclerViewForNineGridView;
    protected OnItemClickListener1<T> onItemClickListener;
    protected NineGridAdapter        myAdapter;
    protected List<T>                items = new ArrayList<>(1);

    public NineGridView(Context context) {
        super(context);
        initView(context, null);
    }

    public NineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public NineGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.layout_for_nine_grid_view, this);
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
     * @param datas 九宫格数据
     */
    public void setData(List<T> datas) {
        if (datas != null && !datas.isEmpty()) {
            items.clear();
            items.addAll(datas);
            setVisibility(VISIBLE);
            if (items.size() == 1) {
                GetIsVideoAble item = items.get(0);
                ivPicForNineGridView.setVisibility(VISIBLE);
                ivPlayPauseForNineGridView.setVisibility(item.isVideo() ? VISIBLE : GONE);
                recyclerViewForNineGridView.setVisibility(GONE);
                Glide.with(this).load(item.getUrl())
                        .placeholder(R.drawable.ic_holder_light_for_nine_grid_view)
                        .error(R.drawable.ic_holder_light_for_nine_grid_view)
                        .transform(new RoundedCorners(10))
                        .into(ivPicForNineGridView);
                ivPicForNineGridView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) onItemClickListener.onItemClick(NineGridView.this, items.get(0), null, ivPicForNineGridView, 0);
                    }
                });
            } else {
                ivPicForNineGridView.setVisibility(GONE);
                ivPlayPauseForNineGridView.setVisibility(GONE);
                recyclerViewForNineGridView.setVisibility(VISIBLE);
                if (myAdapter == null) {
                    myAdapter = new NineGridAdapter(R.layout.item_for_nine_grid_view, items);
                    myAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            if (onItemClickListener != null) onItemClickListener.onItemClick(NineGridView.this, myAdapter.getItem(position), adapter, view, position);
                        }
                    });
                    recyclerViewForNineGridView.setAdapter(myAdapter);
                    recyclerViewForNineGridView.addItemDecoration(new BaseItemDecoration(10, 10));
                }
                myAdapter.notifyDataSetChanged();
            }
        } else setVisibility(GONE);
    }

    /**
     * 设置Item点击事件
     */
    public void setOnItemClickListener(OnItemClickListener1<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener1<T extends GetIsVideoAble> {
        void onItemClick(NineGridView<T> nineGridView, T item, BaseQuickAdapter<T, BaseViewHolder> adapter, View view, int position);
    }

    protected class NineGridAdapter extends BaseQuickAdapter<T, BaseViewHolder> {

        public NineGridAdapter(int layoutResId, List<T> items) {
            super(layoutResId, items);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, GetIsVideoAble item) {
            if (item != null) {
                ImageView ivPic = helper.setVisible(R.id.iv_play_pause, item.isVideo())
                        .getView(R.id.iv_pic_for_nine_grid_view);
                Glide.with(ivPic).load(item.getUrl())
                        .placeholder(R.drawable.ic_holder_light_for_nine_grid_view)
                        .error(R.drawable.ic_holder_light_for_nine_grid_view)
                        .transform(new CenterCrop(), new RoundedCorners(10))
                        .into(ivPic);
            }
        }
    }
}
