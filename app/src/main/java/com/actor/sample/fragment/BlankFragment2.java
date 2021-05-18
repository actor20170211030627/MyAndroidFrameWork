package com.actor.sample.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.actor.myandroidframework.widget.SwipeRefreshLayoutCompatViewPager;
import com.actor.sample.R;
import com.actor.sample.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description: 里面层Fragment
 * Author     : ldf
 * Date       : 2019-9-6 on 16:41
 */
public class BlankFragment2 extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayoutCompatViewPager swipeRefreshLayout;
    @BindView(R.id.tv_content)
    TextView tvContent;
    Unbinder unbinder;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_fragment2, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                }, 1_000);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
