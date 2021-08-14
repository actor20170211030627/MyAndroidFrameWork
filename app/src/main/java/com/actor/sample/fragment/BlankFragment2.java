package com.actor.sample.fragment;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.actor.myandroidframework.widget.SwipeRefreshLayoutCompatViewPager;
import com.actor.sample.databinding.FragmentBlank2Binding;
import com.actor.sample.utils.Global;

/**
 * Description: 里面层Fragment
 * Author     : ldf
 * Date       : 2019-9-6 on 16:41
 */
public class BlankFragment2 extends BaseFragment<FragmentBlank2Binding> {

    private SwipeRefreshLayoutCompatViewPager swipeRefreshLayout;
    private TextView tvContent;

    private int position;
    private String content;

    public static BlankFragment2 newInstance(int position, String content) {
        BlankFragment2 fragment = new BlankFragment2();
        Bundle args = new Bundle();
        args.putInt(Global.POSITION, position);
        args.putString(Global.CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            position = arguments.getInt(Global.POSITION, -1);
            content = arguments.getString(Global.CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = viewBinding.swipeRefreshLayout;
        tvContent = viewBinding.tvContent;

        tvContent.setText(getStringFormat("第%d个Table, content=%s,\n可以下拉刷新哦 ↓", position, content));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {//1秒后消失
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null) {//onDestroyView后, 所有view = null
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1_000L);
            }
        });
    }
}
