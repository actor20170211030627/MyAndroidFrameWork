package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentPagerAdapter;
import com.actor.sample.databinding.ActivityViewPagerBinding;
import com.actor.sample.fragment.DetailFragment;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;

public class ViewPagerActivity extends BaseActivity<ActivityViewPagerBinding> {

    private ViewPager viewPager;

    public static final String START_POSITION = "START_POSITION";
    private int startPosition;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->元素共享跳转->ViewPager");
        viewPager = viewBinding.viewPager;

        startPosition = getIntent().getIntExtra(START_POSITION, 0);
        myAdapter = new MyAdapter(getSupportFragmentManager(), ImageConstants.IMAGE_SOURCE.length);
        viewPager.setAdapter(myAdapter);
        viewPager.setCurrentItem(startPosition);
    }

    @Override
    @NonNull
    public View sharedElementPositionChanged(int oldPosition, int currentPosition) {
//        DetailFragment fragment = myAdapter.getFragment(currentPosition);//有可能为null, 原因可能是被回收了?
        DetailFragment fragment = myAdapter.currentFragment;
        return fragment.getSharedElementView();
    }

    private class MyAdapter extends BaseFragmentPagerAdapter {

        private DetailFragment currentFragment;

        public MyAdapter(FragmentManager fm, int size) {
            super(fm, size);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return DetailFragment.newInstance(position, startPosition);
        }

        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            currentFragment = (DetailFragment) object;
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressedSharedElement(new Intent().putExtra(Global.CONTENT, "result ok!!"),
                startPosition, viewPager.getCurrentItem());
        super.onBackPressed();
    }
}
