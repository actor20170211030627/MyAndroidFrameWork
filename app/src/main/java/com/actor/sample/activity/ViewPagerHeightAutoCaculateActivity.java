package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.actor.myandroidframework.widget.viewpager.AutoCaculateHeightViewpager;
import com.actor.sample.R;
import com.actor.sample.adapter.ViewPagerHeightAutoCaculateAdapter;
import com.actor.sample.databinding.ActivityViewPagerHeightAutoCaculateBinding;

/**
 * description: 复杂嵌套->ViewPager高度自适应
 * company    :
 * @author    : ldf
 * date       : 2022/3/5 on 19:34
 */
public class ViewPagerHeightAutoCaculateActivity extends BaseActivity<ActivityViewPagerHeightAutoCaculateBinding> {

    private SwipeRefreshLayout          srlSwiperefreshlayout;
    private AutoCaculateHeightViewpager viewPager;
    private AppCompatButton btnSwitchMode;
    private ViewPagerHeightAutoCaculateAdapter detailPagerAdapter;
    private final String[] strs = {"目前模式: 统一显示最高页面高度(矮的页面由于实际没有这么高, 底部会留空白)", "目前模式: 显示相应页面自己的实际高度"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("复杂嵌套->ViewPager高度自适应");
        setOnClickListeners(R.id.btn_switch_mode);
        srlSwiperefreshlayout = viewBinding.srlSwiperefreshlayout;
        viewPager = viewBinding.viewPager;
        btnSwitchMode = viewBinding.btnSwitchMode;
        viewPager.setAdapter(detailPagerAdapter = new ViewPagerHeightAutoCaculateAdapter(getSupportFragmentManager()));
        //刷新
        srlSwiperefreshlayout.setOnRefreshListener(() -> srlSwiperefreshlayout.postDelayed(() -> {
            if (srlSwiperefreshlayout != null) {
                srlSwiperefreshlayout.setRefreshing(false);
            }
        }, 1000L));
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_switch_mode:
                //切换高度显示模式, 默认false
                boolean shownHeightMode = viewPager.getShownHeightMode();
                viewPager.setShownHeightMode(!shownHeightMode);
                if (shownHeightMode) {
                    btnSwitchMode.setText(strs[1]);
                } else {
                    btnSwitchMode.setText(strs[0]);
                }
                break;
            default:
                break;
        }
    }
}