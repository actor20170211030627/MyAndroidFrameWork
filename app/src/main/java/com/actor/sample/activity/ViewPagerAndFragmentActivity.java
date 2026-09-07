package com.actor.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.adapter_viewpager.BasePagerAdapter;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityViewPagerAndFragmentBinding;
import com.actor.sample.fragment.ViewPagerAndInnerFragment;

/**
 * Description: 主页->ViewPager测试
 * Author     : ldf
 * Date       : 2019-9-6 on 14:54
 */
public class ViewPagerAndFragmentActivity extends BaseActivity<ActivityViewPagerAndFragmentBinding> {

    private final String[]         titles             = {"全部", "我的", "我的1", "我的2", "我的3", "我的4", "我的5"};
    private final String[]         titlesPagerAdapter = {"position = 0", "position = 1", "position = 2", "position = 3", "position = 4", "position = 5"};

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
                    return ViewPagerAndInnerFragment.newInstance(position, "第1个Fragment");
//                case 1:
//                    return ...;
//                case 2:
//                    return ...;
//                case n:
//                    return ...;
                default:
                    return ViewPagerAndInnerFragment.newInstance(position, getStringFormat("第%d个Fragment", position + 1));
            }
        }
    }

    private final BasePagerAdapter pagerAdapter       = new BasePagerAdapter(titlesPagerAdapter) {
        @NonNull
        @Override
        public View instantiateItem2(@NonNull ViewGroup container, int position) {
//            ImageView iv = new ImageView(mActivity);
//            container.addView(iv);
//            Glide.with(mActivity).load(Global.girl).into(iv);
//            return iv;
            AppCompatTextView textView = new AppCompatTextView(mActivity);
            textView.setText(getPageTitle(position));
            return textView;
        }
    }.setLoggable(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->ViewPager测试");
        
        //1.ViewPager & Fragment 多层嵌套
        viewBinding.toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(/*buttonView, */isChecked));

        /**
         * 在Activity中传入: getSupportFragmentManager()
         * 在Fragment中传入: getChildFragmentManager()
         */
        viewBinding.viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));



        //2.ViewPager & BasePagerAdapter
        viewBinding.viewPagerPagerAdapter.setAdapter(pagerAdapter);
        viewBinding.tabLayoutIndicator.setupWithViewPager(viewBinding.viewPagerPagerAdapter);
    }

    public void onCheckedChanged(/*CompoundButton buttonView, */boolean isChecked){
        viewBinding.viewPager.setHorizontalScrollable(isChecked);
        ToasterUtils.infoFormat("外面ViewPager左右滑动 = %b", isChecked);
    }

    @Override
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
        case R.id.btn_add_page_adapter:
            String textAdd = viewBinding.etPositionPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textAdd)) {
                ToasterUtils.warning(viewBinding.etPositionPagerAdapter.getHint());
                return;
            }
            int positionAdd = Integer.parseInt(textAdd);
            pagerAdapter.addPage(positionAdd, "addPage, position = " + positionAdd);
            break;
        case R.id.btn_remove_page_adapter:
            String textRemove = viewBinding.etPositionPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textRemove)) {
                ToasterUtils.warning(viewBinding.etPositionPagerAdapter.getHint());
                return;
            }
            int positionRemove = Integer.parseInt(textRemove);
            pagerAdapter.removePage(viewBinding.viewPagerPagerAdapter, positionRemove);
            break;
        case R.id.btn_exchange_page:
            String fromText = viewBinding.etExchangeFromPositionPagerAdapter.getText().toString().trim();
            String toText = viewBinding.etExchangeToPositionPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(fromText)) {
                ToasterUtils.warning(viewBinding.etExchangeFromPositionPagerAdapter.getHint());
                return;
            }
            if (TextUtils.isEmpty(toText)) {
                ToasterUtils.warning(viewBinding.etExchangeToPositionPagerAdapter.getHint());
                return;
            }
            int fromPosition = Integer.parseInt(fromText);
            int toPosition = Integer.parseInt(toText);
            pagerAdapter.exchangePage(fromPosition, toPosition);
            break;
        default:
            break;
        }
    }
}
