package com.actor.sample.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentPagerAdapter;
import com.actor.sample.fragment.ViewPagerDetailFragment;

/**
 * description: 共享元素, ViewPager
 * company    :
 *
 * @author : ldf
 * date       : 2022/6/15 on 12
 * @version 1.0
 */
public class ShareElementViewPagerAdapter extends BaseFragmentPagerAdapter {

    public        ViewPagerDetailFragment currentFragment;
    private final int                     startPosition;
    private final boolean        isChangeTransition;

    public ShareElementViewPagerAdapter(FragmentManager fm, int size, int startPosition, boolean isChangeTransition) {
        super(fm, size);
        this.startPosition = startPosition;
        this.isChangeTransition = isChangeTransition;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ViewPagerDetailFragment.newInstance(position, startPosition, isChangeTransition);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        currentFragment = (ViewPagerDetailFragment) object;
    }
}
