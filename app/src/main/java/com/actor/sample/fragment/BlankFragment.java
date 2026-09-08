package com.actor.sample.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.sample.databinding.FragmentBlankBinding;
import com.actor.sample.utils.Global;

/**
 * description: 测试Fragment
 * company    :
 * @author    : ldf
 * date       : 2026/9/8 on 16:15
 */
public class BlankFragment extends BaseFragment<FragmentBlankBinding> {

    private int position;
    private CharSequence content;

    public static BlankFragment newInstance(int position, CharSequence content) {
        LogUtils.errorFormat("position = %d, content = %s", position, content);
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(Global.POSITION, position);
        args.putCharSequence(Global.CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            position = arguments.getInt(Global.POSITION, -1);
            content = arguments.getCharSequence(Global.CONTENT);
        }
        LogUtils.errorFormat("position = %d, content = %s", position, content);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.tvContent.setText(getStringFormat("content=%s", content));
    }
}