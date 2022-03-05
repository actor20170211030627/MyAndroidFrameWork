package com.actor.sample.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.sample.adapter.ViewPagerHeightAutoCaculatePicsAdapter;
import com.actor.sample.databinding.Fragment1ViewPagerHeightAutoCaculateBinding;

/**
 * description: ViewPagerHeightAutoCaculate Fragment1
 * company    :
 * @author    : ldf
 * date       : 2022/3/5 on 13:51
 */
public class ViewPagerHeightAutoCaculateFragment1 extends BaseFragment<Fragment1ViewPagerHeightAutoCaculateBinding> {

    private RecyclerView rvRvpics;


    private ViewPagerHeightAutoCaculatePicsAdapter mDetailPicsAdapter;

    public ViewPagerHeightAutoCaculateFragment1() {
        // Required empty public constructor
    }

    public static ViewPagerHeightAutoCaculateFragment1 newInstance() {
        return new ViewPagerHeightAutoCaculateFragment1();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRvpics = viewBinding.rvRvpics;

        rvRvpics.setAdapter(mDetailPicsAdapter = new ViewPagerHeightAutoCaculatePicsAdapter());
    }
}