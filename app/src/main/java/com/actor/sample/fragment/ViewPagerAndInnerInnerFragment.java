package com.actor.sample.fragment;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.actor.sample.databinding.FragmentViewPagerAndInnerInnerBinding;
import com.actor.sample.utils.Global;

/**
 * Description: 里面层Fragment
 * Author     : ldf
 * Date       : 2019-9-6 on 16:41
 */
public class ViewPagerAndInnerInnerFragment extends BaseFragment<FragmentViewPagerAndInnerInnerBinding> {

    private int position;
    private CharSequence content;

    public static ViewPagerAndInnerInnerFragment newInstance(int position, CharSequence content) {
        ViewPagerAndInnerInnerFragment fragment = new ViewPagerAndInnerInnerFragment();
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.tvContent.setText(getStringFormat("第%d个Tab, content=%s", position, content));
        viewBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewBinding.swipeRefreshLayout.postDelayed(new Runnable() {//1秒后消失
                    @Override
                    public void run() {
                        if (viewBinding.swipeRefreshLayout != null) {//onDestroyView后, 所有view = null
                            viewBinding.swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1_000L);
            }
        });
    }
}
