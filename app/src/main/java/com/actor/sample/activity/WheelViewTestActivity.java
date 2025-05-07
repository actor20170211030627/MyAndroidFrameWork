package com.actor.sample.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import com.actor.myandroidframework.adapter_recyclerview.PickerLayoutManager2;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.MyWheelViewTestAdapter;
import com.actor.sample.databinding.ActivityWheelViewTestBinding;
import com.dingmouren.layoutmanagergroup.picker.PickerLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * description: WheelView效果测试
 * company    :
 * @author    : ldf
 * date       : 2025/1/6 on 11:15
 */
public class WheelViewTestActivity extends BaseActivity<ActivityWheelViewTestBinding> {

    private MyWheelViewTestAdapter mAdapter0;
    private MyWheelViewTestAdapter mAdapter1;
    private MyWheelViewTestAdapter mAdapter2;
    private MyWheelViewTestAdapter mAdapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WheelView测试");

        List<String> mOptionsItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mOptionsItems.add("item " + i);
        }

        PickerLayoutManager2 layoutManager0 = new PickerLayoutManager2(this,
                viewBinding.recyclerView0, PickerLayoutManager.VERTICAL, false, 3, 0.4f, true);
        PickerLayoutManager2 layoutManager1 = new PickerLayoutManager2(this,
                viewBinding.recyclerView1, PickerLayoutManager.VERTICAL, false, 3, 0.8f, true);
        PickerLayoutManager2 layoutManager2 = new PickerLayoutManager2(this,
                viewBinding.recyclerView2, PickerLayoutManager.HORIZONTAL, false, 3, 0.5f, true);
        PickerLayoutManager2 layoutManager3 = new PickerLayoutManager2(this,
                viewBinding.recyclerView3, PickerLayoutManager.HORIZONTAL, false, 3, 0.9f, true);
        //竖着滑动
        mAdapter0 = new MyWheelViewTestAdapter(layoutManager0, R.layout.item_wheel_view_vertical, false, true);
        mAdapter1 = new MyWheelViewTestAdapter(layoutManager1, R.layout.item_wheel_view_vertical, true, true);
        mAdapter2 = new MyWheelViewTestAdapter(layoutManager2, R.layout.item_wheel_view_horizontal, false, true);
        mAdapter3 = new MyWheelViewTestAdapter(layoutManager3, R.layout.item_wheel_view_horizontal, true, true);

        //设置是否能打印日志
        mAdapter0.setLoggable(true);
        mAdapter1.setLoggable(true);
        mAdapter2.setLoggable(false);
        mAdapter3.setLoggable(false);

        //1.先设置Adapter
        viewBinding.recyclerView0.setAdapter(mAdapter0);
        viewBinding.recyclerView1.setAdapter(mAdapter1);
        viewBinding.recyclerView2.setAdapter(mAdapter2);
        viewBinding.recyclerView3.setAdapter(mAdapter3);
        //2.然后获取到数据后, 再设置数据
        mAdapter0.setList(mOptionsItems);
        mAdapter1.setList(mOptionsItems);
        mAdapter2.setList(mOptionsItems);
        mAdapter3.setList(mOptionsItems);
        /* 1, 2顺序不要搞反了, 否则无限滚动初始化的时候, 不会滚动到最中间item的position */

        //设置当前滚轮position
        viewBinding.btnSetCurrentPos.setOnClickListener(v -> {
            Editable text = viewBinding.etCurrentPos.getText();
            if (TextUtils.isEmpty(text)) {
                ToasterUtils.warningFormat("请输入position: [0 ~ %d]", mOptionsItems.size() - 1);
                return;
            }
            String input = String.valueOf(text);
            try {
                int inputI = Integer.parseInt(input);
                boolean isSmoothScroll = viewBinding.scSmoothScroll.isChecked();
                mAdapter0.setCurrentPosition(inputI, isSmoothScroll);
                mAdapter1.setCurrentPosition(inputI, isSmoothScroll);
                mAdapter2.setCurrentPosition(inputI, isSmoothScroll);
                mAdapter3.setCurrentPosition(inputI, isSmoothScroll);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ToasterUtils.warningFormat("请输入正确的position: [0 ~ %d]", mOptionsItems.size() - 1);
            }
        });

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