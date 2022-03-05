package com.actor.sample.fragment;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.widget.viewpager.ScrollableViewPager;
import com.actor.sample.databinding.FragmentBlankBinding;
import com.actor.sample.utils.Global;

/**
 * Description: 外层Fragment
 * Author     : ldf
 * Date       : 2019-9-6 on 16:40
 */
public class BlankFragment extends BaseFragment<FragmentBlankBinding> {

    private TextView            tvContent;
    private ScrollableViewPager viewPager;

    private int    id;
    private String content;
    private String[] titles = {"Table0", "Table1"};

    //1.
    public static BlankFragment newInstance(int id, String content) {
        BlankFragment fragment = new BlankFragment();
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

    //3.初始化数据
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvContent = viewBinding.tvContent;
        viewPager = viewBinding.viewPager;
        viewBinding.toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(/*buttonView, */isChecked));

        tvContent.setText(content);
        viewPager.setAdapter(new MyInnerAdapter(getChildFragmentManager(), titles));
        //request internet...
    }

    //4.可见变化监听, 当isVisibleToUser = true时, 可懒加载数据(请求网络)
    //也可以直接在 onViewCreated 中提前请求数据
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //request internet...
    }

    //
    private class MyInnerAdapter extends BaseFragmentStatePagerAdapter {

        public MyInnerAdapter(FragmentManager fm, @NonNull String[] titles) {
            super(fm, titles);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return BlankFragment2.newInstance(position, content);
        }
    }

//    @OnCheckedChanged({R.id.toggle_button})
    public void onCheckedChanged(/*CompoundButton buttonView, */boolean isChecked){
        viewPager.setHorizontalScrollble(isChecked);
        showToastFormat("里面ViewPager左右滑动 = %b", isChecked);
    }
}
