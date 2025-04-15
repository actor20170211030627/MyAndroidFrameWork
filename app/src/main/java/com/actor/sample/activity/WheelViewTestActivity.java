package com.actor.sample.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
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

    private MyWheelViewTestAdapter mAdapter0;
    private MyWheelViewTestAdapter mAdapter1;
    private MyWheelViewTestAdapter mAdapter2;
    private MyWheelViewTestAdapter mAdapter3;
    private MyWheelViewTestAdapter mAdapter4;
    private MyWheelViewTestAdapter mAdapter5;
    private MyWheelViewTestAdapter mAdapter6;
    private MyWheelViewTestAdapter mAdapter7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WheelView测试");

        List<String> mOptionsItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mOptionsItems.add("item " + i);
        }

        //竖着滑动
        mAdapter0 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_vertical, 3, false, true);
        mAdapter1 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_vertical, 3, true, true);
        mAdapter2 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_vertical, 3, false, true);
        mAdapter3 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_vertical, 3, true, true);

        //横着滑动
        mAdapter4 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_horizontal, 3, false, true);
        mAdapter5 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_horizontal, 3, true, true);
        mAdapter6 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_horizontal, 3, false, true);
        mAdapter7 = new MyWheelViewTestAdapter(R.layout.item_wheel_view_horizontal, 3, true, true);

        //设置是否能打印日志
        mAdapter0.setLoggable(true);
        mAdapter1.setLoggable(true);
        mAdapter2.setLoggable(true);
        mAdapter3.setLoggable(false);
        mAdapter4.setLoggable(false);
        mAdapter5.setLoggable(false);
        mAdapter6.setLoggable(false);
        mAdapter7.setLoggable(false);

        //1.先设置Adapter
        viewBinding.recyclerView0.setAdapter(mAdapter0);
        viewBinding.recyclerView1.setAdapter(mAdapter1);
        viewBinding.recyclerView2.setAdapter(mAdapter2);
        viewBinding.recyclerView3.setAdapter(mAdapter3);
        viewBinding.recyclerView4.setAdapter(mAdapter4);
        viewBinding.recyclerView5.setAdapter(mAdapter5);
        viewBinding.recyclerView6.setAdapter(mAdapter6);
        viewBinding.recyclerView7.setAdapter(mAdapter7);
        //2.然后获取到数据后, 再设置数据
        mAdapter0.setList(mOptionsItems);
        mAdapter1.setList(mOptionsItems);
        mAdapter2.setList(mOptionsItems);
        mAdapter3.setList(mOptionsItems);
        mAdapter4.setList(mOptionsItems);
        mAdapter5.setList(mOptionsItems);
        mAdapter6.setList(mOptionsItems);
        mAdapter7.setList(mOptionsItems);

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
                mAdapter4.setCurrentPosition(inputI, isSmoothScroll);
                mAdapter5.setCurrentPosition(inputI, isSmoothScroll);
                mAdapter6.setCurrentPosition(inputI, isSmoothScroll);
                mAdapter7.setCurrentPosition(inputI, isSmoothScroll);
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
            String selectedItem4 = mAdapter4.getSelectedItem();
            String selectedItem5 = mAdapter5.getSelectedItem();
            String selectedItem6 = mAdapter6.getSelectedItem();
            String selectedItem7 = mAdapter7.getSelectedItem();
            LogUtils.errorFormat("selectedItem0=%s, selectedItem1=%s, selectedItem2=%s, selectedItem3=%s", selectedItem0, selectedItem1, selectedItem2, selectedItem3);
            LogUtils.errorFormat("selectedItem4=%s, selectedItem5=%s, selectedItem6=%s, selectedItem7=%s", selectedItem4, selectedItem5, selectedItem6, selectedItem7);
        });
    }
}