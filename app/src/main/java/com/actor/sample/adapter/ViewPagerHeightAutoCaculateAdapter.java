package com.actor.sample.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentPagerAdapter;
import com.actor.sample.fragment.ViewPagerHeightAutoCaculateFragment2;
import com.actor.sample.fragment.ViewPagerHeightAutoCaculateFragment1;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2022/3/5 on 19
 * @version 1.0
 */
public class ViewPagerHeightAutoCaculateAdapter extends BaseFragmentPagerAdapter {

    public ViewPagerHeightAutoCaculateAdapter(FragmentManager fm) {
        super(fm, new String[]{"介  绍", "评  论"});
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ViewPagerHeightAutoCaculateFragment1.newInstance();
            case 1:
            default:
                return ViewPagerHeightAutoCaculateFragment2.newInstance();
        }
    }
}
