package com.actor.sample.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.actor.sample.adapter.QuickSearchBarAdapter;
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

    private final QuickSearchBarAdapter mAdapterLinearLayoutManager = new QuickSearchBarAdapter();
//    private final QuickSearchBarAdapter mAdapterGridLayoutManager = new QuickSearchBarAdapter();

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


        //初始化间隙
        int dp5 = SizeUtils.dp2px(5f);
        viewBinding.recyclerView.addItemDecoration(new BaseItemDecoration(dp5, dp5));


        //设置数据
        List<Item> items = mAdapterLinearLayoutManager.getData();
        items.clear();
        items.add(new Item("点点滴滴"));    //d
        items.add(new Item("正则正则"));    //z
        items.add(new Item("滚滚滚"));      //g
        items.add(new Item("鹅鹅鹅鹅鹅"));   //e
        items.add(new Item("-/----/---"));  //#
        items.add(new Item("啊啊啊啊啊啊啊"));//a
        items.add(new Item("摇一摇"));        //y
        items.add(new Item("重庆朝天门"));    //c
        items.add(new Item("宝宝贝贝吧"));    //b
        items.add(new Item("小星星"));        //x
        items.add(new Item("冲冲冲"));        //c
        items.add(new Item("反反复复"));      //f
        items.add(new Item("烦烦烦"));       //f
        items.add(new Item("错错错"));        //c
        items.add(new Item("我我我我"));      //w
        items.add(new Item("重庆火锅"));      //c
        items.add(new Item("v8会员"));        //v
        items.add(new Item("呵呵呵"));        //h
        items.add(new Item("通天塔"));        //t
        items.add(new Item("贾俊杰回家吃饭")); //j
        items.add(new Item("么么么么么"));     //m
        items.add(new Item("啦啦啦"));         //L
        mAdapterLinearLayoutManager.setList(items);


        setLinearLayoutManager();
    }

    private void setLinearLayoutManager() {
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, getOrientation(), false));
        viewBinding.recyclerView.setAdapter(mAdapterLinearLayoutManager);
    }

    private void setGridLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6, getOrientation(), false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position >= 1 && position <= 3) return 2;
                if (position == 5 || position == 6) return 3;
                return 6;
            }
        });
        viewBinding.recyclerView.setLayoutManager(gridLayoutManager);
//        viewBinding.recyclerView.setAdapter(mAdapterGridLayoutManager);
    }

    private void setStaggeredGridLayoutManager() {
        viewBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, getOrientation()));
//        viewBinding.recyclerView.setAdapter(mAdapterLinearLayoutManager);
    }

    private void setFlexboxLayoutManager() {
        int flexDirection = (getOrientation() == RecyclerView.HORIZONTAL) ? FlexDirection.ROW : FlexDirection.COLUMN;
        viewBinding.recyclerView.setLayoutManager(new FlexboxLayoutManager(this, flexDirection));
//        viewBinding.recyclerView.setAdapter(mAdapterLinearLayoutManager);
    }

    //方向
    private int getOrientation() {
        return viewBinding.switchOrientation.isChecked() ? RecyclerView.VERTICAL : RecyclerView.HORIZONTAL;
    }
}