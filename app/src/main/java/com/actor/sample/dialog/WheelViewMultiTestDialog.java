package com.actor.sample.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.dialog.ViewBindingDialog;
import com.actor.myandroidframework.recyclerview.WheelViewLayoutManager;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.MyWheelViewTestAdapter;
import com.actor.sample.databinding.DialogWheelViewMultiTestBinding;
import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 在Dialog中测试WheelViewLayoutManager
 * company    :
 *
 * @author : ldf
 * date       : 2025/8/2 on 19
 * @version 1.0
 */
public class WheelViewMultiTestDialog extends ViewBindingDialog<DialogWheelViewMultiTestBinding> {

    private MyWheelViewTestAdapter mAdapter0;
    private MyWheelViewTestAdapter mAdapter1;
    private MyWheelViewTestAdapter mAdapter2;
    private MyWheelViewTestAdapter mAdapter3;

    public WheelViewMultiTestDialog(@NonNull Context context) {
        super(context);
        setWidthPercent(0.8647f, SizeUtils.dp2px(308f));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> mOptionsItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mOptionsItems.add("item " + i);
        }

        //竖着滑动
        WheelViewLayoutManager layoutManager0 = new WheelViewLayoutManager(getContext(),
                WheelViewLayoutManager.VERTICAL, 3, 0.4f, true)
                .setLoggable(true);
        WheelViewLayoutManager layoutManager1 = new WheelViewLayoutManager(getContext(),
                WheelViewLayoutManager.VERTICAL, 3, 0.8f, false)
                .setLoggable(true);
        //水平滑动
        WheelViewLayoutManager layoutManager2 = new WheelViewLayoutManager(getContext(),
                WheelViewLayoutManager.HORIZONTAL, 3, 0.5f, true)
                .setLoggable(false);
        WheelViewLayoutManager layoutManager3 = new WheelViewLayoutManager(getContext(),
                WheelViewLayoutManager.HORIZONTAL, 3, 0.9f, true)
                .setLoggable(false);

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


        //获取选中item
        viewBinding.btnGetItem.setOnClickListener(v -> {
            String selectedItem0 = mAdapter0.getSelectedItem();
            String selectedItem1 = mAdapter1.getSelectedItem();
            String selectedItem2 = mAdapter2.getSelectedItem();
            String selectedItem3 = mAdapter3.getSelectedItem();
            String content = TextUtils2.getStringFormat("selectedItem0=%s, selectedItem1=%s, selectedItem2=%s, selectedItem3=%s",
                    selectedItem0, selectedItem1, selectedItem2, selectedItem3);
            viewBinding.tvItemsContent.setText(content);
        });

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

            viewBinding.recyclerView1.post(() -> {
                int width = viewBinding.recyclerView1.getWidth();
                int measuredWidth = viewBinding.recyclerView1.getMeasuredWidth();
                int height = viewBinding.recyclerView1.getHeight();
                int measuredHeight = viewBinding.recyclerView1.getMeasuredHeight();
                LogUtils.errorFormat("RecyclerView1: width = %d, measuredWidth = %d \t height = %d, measuredHeight = %d",
                        width, measuredWidth, height, measuredHeight);
            });
        });

        viewBinding.stvSure.setOnClickListener(v -> {
            dismiss();
        });
    }
}
