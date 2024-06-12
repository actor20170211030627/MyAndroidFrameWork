package com.actor.sample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.action.AnimAction;
import com.actor.myandroidframework.fragment.BaseDialogFragment;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.activity.ChatActivity;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2024/6/12 on 12
 * @version 1.0
 */
public class MyDialogFragment extends BaseDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWidth(ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(40f));
        //底部弹出测试
        setGravityAndAnimation(Gravity.CENTER, AnimAction.ANIM_BOTTOM_SLIDE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_bottom_sheet_dialog, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_dismiss).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            ToasterUtils.success("yes~");
            startActivityForResult(new Intent(mActivity, ChatActivity.class), (resultCode, data) -> {
                ToasterUtils.successFormat("resultCode=%d, data=%s", resultCode, data);
            });
        });
    }
}
