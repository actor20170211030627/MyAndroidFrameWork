package com.actor.sample.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.widget.ScrollableViewPager;
import com.actor.sample.R;
import com.actor.sample.fragment.BlankFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Description: 主页->ViewPager & Fragment多层嵌套
 * Author     : 李大发
 * Date       : 2019-9-6 on 14:54
 */
public class ViewPagerAndFragmentActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ScrollableViewPager viewPager;//能设置是否能左右滑动的ViewPager

    private String[] titles = {"全部", "我的", "我的1", "我的2", "我的3", "我的4", "我的5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_and_fragment);
        ButterKnife.bind(this);

        setTitle("主页->ViewPager & Fragment多层嵌套");

        /**
         * 在Activity中传入: getSupportFragmentManager()
         * 在Fragment中传入: getChildFragmentManager()
         */
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
    }

    /**
     * 如果页面较少的情况, 应该继承 {@link com.actor.myandroidframework.adapter_viewpager.BaseFragmentPagerAdapter}
     * 如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该继承: {@link BaseFragmentStatePagerAdapter}
     */
    private class MyAdapter extends BaseFragmentStatePagerAdapter {

        //如果没有标题, 可以重写这个构造方法
//        public MyAdapter(FragmentManager fm, int size) {
//            super(fm, size);
//        }

        public MyAdapter(FragmentManager fm, @NonNull String[] titles) {
            super(fm, titles);
        }

        /**
         * 务必返回: XxxFragment.newInstance(Xxx... xxx); ,这样系统恢复时, 会重新调用Fragment的onCreate
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return BlankFragment.newInstance(position, "第1个Fragment");
//                case 1:
//                    return ...;
//                case 2:
//                    return ...;
//                case n:
//                    return ...;
                default:
                    return BlankFragment.newInstance(position, getStringFormat("第%d个Fragment", position + 1));
            }
        }
    }

    @OnCheckedChanged({R.id.toggle_button})
    public void onCheckedChanged(/*CompoundButton buttonView, */boolean isChecked){
        viewPager.setHorizontalScrollble(isChecked);
        toastFormat("外面ViewPager左右滑动 = %b", isChecked);
    }
}
