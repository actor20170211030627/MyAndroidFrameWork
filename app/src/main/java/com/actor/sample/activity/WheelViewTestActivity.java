package com.actor.sample.activity;

import android.os.Bundle;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.MyWheelViewTestAdapter;
import com.actor.sample.databinding.ActivityWheelViewTestBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * description: WheelView效果测试
 * company    :
 * @author    : ldf
 * date       : 2025/1/6 on 11:15
 */
public class WheelViewTestActivity extends BaseActivity<ActivityWheelViewTestBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WheelView测试");

        List<String> mOptionsItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mOptionsItems.add("item " + i);
        }

        MyWheelViewTestAdapter mAdapter0 = new MyWheelViewTestAdapter(R.layout.item_wheel_view, 3, false, true);
        MyWheelViewTestAdapter mAdapter1 = new MyWheelViewTestAdapter(R.layout.item_wheel_view, 3, true, true);
        MyWheelViewTestAdapter mAdapter2 = new MyWheelViewTestAdapter(R.layout.item_wheel_view, 3, false, true);
        MyWheelViewTestAdapter mAdapter3 = new MyWheelViewTestAdapter(R.layout.item_wheel_view, 3, true, true);
        //设置是否能打印日志
        mAdapter0.setLoggable(true);
        mAdapter1.setLoggable(false);
        mAdapter2.setLoggable(true);
        mAdapter3.setLoggable(true);

        viewBinding.recyclerView0.setAdapter(mAdapter0);
        viewBinding.recyclerView1.setAdapter(mAdapter1);
        viewBinding.recyclerView2.setAdapter(mAdapter2);
        viewBinding.recyclerView3.setAdapter(mAdapter3);
        //设置数据
        mAdapter0.setList(mOptionsItems);
        mAdapter1.setList(mOptionsItems);
        mAdapter2.setList(mOptionsItems);
        mAdapter3.setList(mOptionsItems);

        //获取选中item
        viewBinding.btnGetItem.setOnClickListener(v -> {
            String selectedItem0 = mAdapter0.getSelectedItem();
            String selectedItem1 = mAdapter1.getSelectedItem();
            String selectedItem2 = mAdapter2.getSelectedItem();
            String selectedItem3 = mAdapter3.getSelectedItem();
            LogUtils.errorFormat("selectedItem0=%s, selectedItem1=%s, selectedItem2=%s, selectedItem3=%s", selectedItem0, selectedItem1, selectedItem2, selectedItem3);
        });
    }
}