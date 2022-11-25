package com.actor.sample.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentPagerAdapter;
import com.actor.sample.fragment.DetailFragment;

/**
 * description: 共享元素, ViewPager
 * company    :
 *
 * @author : ldf
 * date       : 2022/6/15 on 12
 * @version 1.0
 */
public class ShareElementViewPagerAdapter extends BaseFragmentPagerAdapter {

    public DetailFragment currentFragment;
    private int startPosition;

    public ShareElementViewPagerAdapter(FragmentManager fm, int size, int startPosition) {
        super(fm, size);
        this.startPosition = startPosition;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DetailFragment.newInstance(position, startPosition);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        currentFragment = (DetailFragment) object;
    }
}
