package com.actor.sample.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentPagerAdapter;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.databinding.FragmentViewPagerAndInnerBinding;
import com.actor.sample.utils.Global;

/**
 * Description: 外层Fragment
 * Author     : ldf
 * Date       : 2019-9-6 on 16:40
 */
public class ViewPagerAndInnerFragment extends BaseFragment<FragmentViewPagerAndInnerBinding> {

    private int    id;
    private       String   content;
    private final String[] titles = {"Tab0", "Tab1"};
    private MyInnerAdapter myInnerAdapter;

    private class MyInnerAdapter extends BaseFragmentPagerAdapter {
        public MyInnerAdapter() {
            super(getChildFragmentManager(), titles);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return ViewPagerAndInnerInnerFragment.newInstance(position, content);
        }
    }

    //1.
    public static ViewPagerAndInnerFragment newInstance(int id, String content) {
        ViewPagerAndInnerFragment fragment = new ViewPagerAndInnerFragment();
        Bundle args = new Bundle();
        args.putInt(Global.ID, id);
        args.putString(Global.CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    //2.系统恢复时, 会重新调用Fragment的onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getInt(Global.ID, -1);
            content = arguments.getString(Global.CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(/*buttonView, */isChecked));

        viewBinding.tvContent.setText(content);
        viewBinding.viewPager.setAdapter(myInnerAdapter = new MyInnerAdapter());

        //TabLayout 赋值
        viewBinding.tabLayout.setTabConfigurationStrategy((tab, position) -> {
            TextView tv = (TextView) tab.getCustomView();
            if (tv != null) tv.setText("tab" + position);
        });

        //Add Fragment
        viewBinding.btnAddFragmentPageAdapter.setOnClickListener((btn) -> {
            String trim = viewBinding.etPositionFragmentPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                ToasterUtils.warning(viewBinding.etPositionFragmentPagerAdapter.getHint());
            } else {
                int positionAdd = Integer.parseInt(trim);
                myInnerAdapter.addFragment(positionAdd, "addFragment, position = " + positionAdd);
            }
        });
        //Remove Fragment
        viewBinding.btnRemoveFragmentPageAdapter.setOnClickListener((btn) -> {
            String trim = viewBinding.etPositionFragmentPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                ToasterUtils.warning(viewBinding.etPositionFragmentPagerAdapter.getHint());
            } else {
                int positionRemove = Integer.parseInt(trim);
                myInnerAdapter.removeFragment(viewBinding.viewPager, positionRemove);
            }
        });
    }

//    @OnCheckedChanged({R.id.toggle_button})
    public void onCheckedChanged(/*CompoundButton buttonView, */boolean isChecked){
        viewBinding.viewPager.setHorizontalScrollable(isChecked);
        ToasterUtils.infoFormat("里面ViewPager左右滑动 = %b", isChecked);
    }
}
