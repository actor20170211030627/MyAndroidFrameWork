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
import com.actor.sample.databinding.DialogWheelViewSingleHorizontalTestBinding;
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
public class WheelViewSingleHorizontalTestDialog extends ViewBindingDialog<DialogWheelViewSingleHorizontalTestBinding> {

    private MyWheelViewTestAdapter mAdapter0;

    public WheelViewSingleHorizontalTestDialog(@NonNull Context context) {
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
                WheelViewLayoutManager.HORIZONTAL, 3, 0.4f, true)
                .setLoggable(true);
        mAdapter0 = new MyWheelViewTestAdapter(layoutManager0, R.layout.item_wheel_view_horizontal, false, true);

        //设置是否能打印日志
        mAdapter0.setLoggable(true);

        //1.先设置Adapter
        viewBinding.recyclerView.setAdapter(mAdapter0);
        //2.然后获取到数据后, 再设置数据
        mAdapter0.setList(mOptionsItems);
        /* 1, 2顺序不要搞反了, 否则无限滚动初始化的时候, 不会滚动到最中间item的position */


        //获取选中item
        viewBinding.btnGetItem.setOnClickListener(v -> {
            String selectedItem0 = mAdapter0.getSelectedItem();
            String content = TextUtils2.getStringFormat("selectedItem0=%s", selectedItem0);
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
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ToasterUtils.warningFormat("请输入正确的position: [0 ~ %d]", mOptionsItems.size() - 1);
            }

            viewBinding.recyclerView.post(() -> {
                int width = viewBinding.recyclerView.getWidth();
                int measuredWidth = viewBinding.recyclerView.getMeasuredWidth();
                int height = viewBinding.recyclerView.getHeight();
                int measuredHeight = viewBinding.recyclerView.getMeasuredHeight();
                LogUtils.errorFormat("RecyclerView: width = %d, measuredWidth = %d \t height = %d, measuredHeight = %d",
                        width, measuredWidth, height, measuredHeight);
            });
        });

        viewBinding.stvSure.setOnClickListener(v -> {
            dismiss();
        });
    }
}
