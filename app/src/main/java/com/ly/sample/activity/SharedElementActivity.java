package com.ly.sample.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ly.sample.R;
import com.ly.sample.utils.Global;
import com.ly.sample.utils.ImageConstants;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description: 主页->元素共享跳转
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2020/2/6 on 18:41
 */
public class SharedElementActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_element);
        ButterKnife.bind(this);
        setTitle("主页->元素共享跳转");

        myAdapter = new MyAdapter(R.layout.item_shared_element, Arrays.asList(ImageConstants.IMAGE_SOURCE));
        myAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            //startActivity & startActivityForResult
            startActivityForResult(new Intent(this, ViewPagerActivity.class)
                    .putExtra(ViewPagerActivity.START_POSITION, position),
                    0, true, view);
        });
        recyclerView.setAdapter(myAdapter);
    }

    @NonNull
    @Override
    protected View sharedElementPositionChanged(int oldPosition, int currentPosition) {
        return recyclerView.findViewHolderForAdapterPosition(currentPosition).itemView.findViewById(R.id.iv);
    }

    @Override
    protected void onSharedElementBacked(int oldPosition, int currentPosition) {
        super.onSharedElementBacked(oldPosition, currentPosition);
        recyclerView.scrollToPosition(currentPosition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();//延时动画
            recyclerView.post(this::startPostponedEnterTransition);//开始延时共享动画
        }
    }

    private class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            int position = helper.getAdapterPosition();
            ImageView iv = helper.addOnClickListener(R.id.iv).getView(R.id.iv);
            String url = ImageConstants.IMAGE_SOURCE[position];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv.setTransitionName(url);//setTransitionName
            }
            Glide.with(activity).load(url).into(iv);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            toast(data.getStringExtra(Global.CONTENT));
        }
    }
}
