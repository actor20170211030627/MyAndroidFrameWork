package com.actor.sample.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.actor.sample.adapter.RecyclerViewTestAdapter;
import com.actor.sample.bean.Item;
import com.actor.sample.databinding.ActivityRecyclerViewTestBinding;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

/**
 * description: RecyclerVie 测试
 * company    :
 * @author    : ldf
 * date       : 2025/3/25 on 11:44
 */
public class RecyclerViewTestActivity extends BaseActivity<ActivityRecyclerViewTestBinding> {

    private final RecyclerViewTestAdapter mAdapterVertical = new RecyclerViewTestAdapter(false, true);
    private final RecyclerViewTestAdapter mAdapterHorizontal = new RecyclerViewTestAdapter(false, false);
    private final RecyclerViewTestAdapter mAdapterFlexbox = new RecyclerViewTestAdapter(true, true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("RecyclerVie 测试");

        viewBinding.btnLinearLayoutManager.setOnClickListener(v -> {
            setLinearLayoutManager();
        });
        viewBinding.btnGridLayoutManager.setOnClickListener(v -> {
            setGridLayoutManager();
        });
        viewBinding.btnStaggeredGridLayoutManager.setOnClickListener(v -> {
            setStaggeredGridLayoutManager();
        });
        viewBinding.btnFlexboxLayoutManager.setOnClickListener(v -> {
            setFlexboxLayoutManager();
        });


        //设置数据
        List<Item> items = mAdapterVertical.getData();
        items.clear();
        for (int i = 0; i < 72; i++) {
            items.add(new Item("item " + i));
        }
        mAdapterVertical.setList(items);
        mAdapterHorizontal.setList(items);
        mAdapterFlexbox.setList(items);


        //初始化间隙
        int dp5 = SizeUtils.dp2px(5f);
        BaseItemDecoration baseItemDecoration = new BaseItemDecoration(dp5, dp5);
//        baseItemDecoration.setLoggable(true);   //打印日志
        viewBinding.recyclerView.addItemDecoration(baseItemDecoration);
        setAdapter();
    }

    private void setLinearLayoutManager() {
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, getOrientation(), false));
        setAdapter();
    }

    private void setGridLayoutManager() {
        int selectedItemPosition = viewBinding.bsSpanCount.getSelectedItemPosition();
        //                                                                  count = 7
        int spanCount = selectedItemPosition == 0 ? viewBinding.bsSpanCount.getCount() : selectedItemPosition;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount, getOrientation(), false);
        if (selectedItemPosition == 0) {
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //用第1个预览效果
                    if (true) {
                        return Math.max(1, (position + 1) % gridLayoutManager.getSpanCount());
                    } else {
                        if (position >= 6 && position <= 8) return 2;
                        if (position == 15 || position == 16) return 3;
                        return 1;
                    }
                }
            });
        }
        this.viewBinding.recyclerView.setLayoutManager(gridLayoutManager);
        setAdapter();
    }

    private void setStaggeredGridLayoutManager() {
        int selectedItemPosition = viewBinding.bsSpanCount2.getSelectedItemPosition();
        //                                                                  count = 7
        int spanCount = selectedItemPosition == 0 ? viewBinding.bsSpanCount2.getCount() : selectedItemPosition;
        viewBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, getOrientation()));
        setAdapter();
    }

    private void setFlexboxLayoutManager() {
        int flexDirection = (getOrientation() == RecyclerView.VERTICAL) ? FlexDirection.ROW : FlexDirection.COLUMN;
        viewBinding.recyclerView.setLayoutManager(new FlexboxLayoutManager(this, flexDirection));
        viewBinding.recyclerView.setAdapter(mAdapterFlexbox);
//        setAdapter();
    }

    //方向
    private int getOrientation() {
        return viewBinding.switchOrientation.isChecked() ? RecyclerView.VERTICAL : RecyclerView.HORIZONTAL;
    }

    private void setAdapter() {
        if (viewBinding.switchOrientation.isChecked()) {
            viewBinding.recyclerView.setAdapter(mAdapterVertical);
        } else {
            viewBinding.recyclerView.setAdapter(mAdapterHorizontal);
        }
    }
}