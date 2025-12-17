package com.actor.sample.dialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.dialog.ViewBindingDialog;
import com.actor.myandroidframework.recyclerview.WheelViewLayoutManager;
import com.actor.sample.R;
import com.actor.sample.adapter.MyWheelViewTestAdapter;
import com.actor.sample.databinding.DialogWheelViewTestBinding;
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
public class WheelViewTestDialog extends ViewBindingDialog<DialogWheelViewTestBinding> {

    private boolean isSmoothScroll = false;

    public WheelViewTestDialog(@NonNull Context context) {
        super(context);
        setWidthPercent(0.8647f, SizeUtils.dp2px(308f));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WheelViewLayoutManager manager = new WheelViewLayoutManager(getContext(),
                WheelViewLayoutManager.VERTICAL, 3, 0.8f, true)
                .setLoggable(false);

        MyWheelViewTestAdapter mAdapter = new MyWheelViewTestAdapter(manager,
                R.layout.item_wheel_view_vertical,
                true,
                true);
        mAdapter.setLoggable(true);


        viewBinding.recyclerView.setAdapter(mAdapter);

        List<String> mOptionsItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mOptionsItems.add("item " + i);
        }


        mAdapter.setList(mOptionsItems);


        viewBinding.stvSure.setOnClickListener(v -> {
            isSmoothScroll = !isSmoothScroll;
            //在Dialog中, isSmoothScroll=false的时候, RecyclerView会消失, 不知道又是抽的什么风...
//            mAdapter.setCurrentPosition(0, isSmoothScroll);

            dismiss();
        });
    }
}
