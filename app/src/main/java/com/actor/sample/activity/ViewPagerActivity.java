package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.sharedelement.SharedElementAble;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.actor.sample.adapter.ShareElementViewPagerAdapter;
import com.actor.sample.databinding.ActivityViewPagerBinding;
import com.actor.sample.fragment.DetailFragment;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;

public class ViewPagerActivity extends BaseActivity<ActivityViewPagerBinding> implements SharedElementAble {

    private ViewPager viewPager;

    public static final String                       START_POSITION = "START_POSITION";
    private             int                          startPosition;
    private             ShareElementViewPagerAdapter myAdapter;

    //1.共享元素跳转回调
    private BaseSharedElementCallback sharedElementCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->元素共享跳转->ViewPager");
        viewPager = viewBinding.viewPager;

        //2.onCreate中调方法, 本Activity implements SharedElementAble
        sharedElementCallback = SharedElementUtils.getSharedElementCallback(this, this);

        startPosition = getIntent().getIntExtra(START_POSITION, 0);
        myAdapter = new ShareElementViewPagerAdapter(getSupportFragmentManager(), ImageConstants.IMAGE_SOURCE.length, startPosition);
        viewPager.setAdapter(myAdapter);
        viewPager.setCurrentItem(startPosition);
    }

    //3.重写接口 SharedElementAble 的方法
    @Override
    @NonNull
    public View sharedElementPositionChanged(int oldPosition, int currentPosition) {
//        DetailFragment fragment = myAdapter.getFragment(currentPosition);//有可能为null, 原因可能是被回收了?
        DetailFragment fragment = myAdapter.currentFragment;
        return fragment.getSharedElementView();
    }

    @Override
    public void onBackPressed() {
        //4.返回前, 将当前的position返回到上一页
        SharedElementUtils.onBackPressedSharedElement(this, sharedElementCallback,
                new Intent().putExtra(Global.CONTENT, "result ok!!"),
                startPosition, viewPager.getCurrentItem());
        super.onBackPressed();
    }
}
