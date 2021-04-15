package com.actor.sample.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.widget.ScrollableViewPager;
import com.actor.sample.R;
import com.actor.sample.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;

/**
 * Description: 外层Fragment
 * Author     : 李大发
 * Date       : 2019-9-6 on 16:40
 */
public class BlankFragment extends BaseFragment {

    @BindView(R.id.tv_content)
    TextView            tvContent;
    @BindView(R.id.view_pager)
    ScrollableViewPager viewPager;

    Unbinder unbinder;
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

    //3.创建Fragment的View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    //4.初始化数据
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvContent.setText(content);
        viewPager.setAdapter(new MyInnerAdapter(getChildFragmentManager(), titles));
        //request internet...
    }

    //5.可见变化监听, 当isVisibleToUser = true时, 可懒加载数据(请求网络)
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

        @Override
        public Fragment getItem(int position) {
            return BlankFragment2.newInstance(position, content);
        }
    }

    @OnCheckedChanged({R.id.toggle_button})
    public void onCheckedChanged(/*CompoundButton buttonView, */boolean isChecked){
        viewPager.setHorizontalScrollble(isChecked);
        toastFormat("里面ViewPager左右滑动 = %b", isChecked);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
