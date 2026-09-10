package com.actor.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.actor.myandroidframework.adapter_viewpager.BaseFragmentPagerAdapter;
import com.actor.myandroidframework.adapter_viewpager.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.adapter_viewpager.BasePagerAdapter;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityViewPagerAndFragmentBinding;
import com.actor.sample.fragment.BlankFragment;
import com.actor.sample.fragment.ViewPagerAndInnerFragment;

/**
 * Description: 主页->ViewPager测试
 * Author     : ldf
 * Date       : 2019-9-6 on 14:54
 */
public class ViewPagerAndFragmentActivity extends BaseActivity<ActivityViewPagerAndFragmentBinding> {

    private final String[]         titles             = {"全部", "选项", "我的", "设置", "我的1", "我的2", "我的3"};
    private final String[]         titlesPagerAdapter = {"position = 0", "position = 1", "position = 2", "position = 3", "position = 4", "position = 5"};

    //1.Fragment多层嵌套
    private MyFragmentsAdapter myFragmentsAdapter;
    //2.BasePagerAdapter
    private final BasePagerAdapter pagerAdapter = new BasePagerAdapter(titlesPagerAdapter) {
        @NonNull
        @Override
        public View getItem(@NonNull ViewGroup container, int position) {
//            ImageView iv = new ImageView(mActivity);
//            container.addView(iv);
//            Glide.with(mActivity).load(Global.girl).into(iv);
//            return iv;
            AppCompatTextView textView = new AppCompatTextView(mActivity);
            textView.setText(getPageTitle(position));
            return textView;
        }
    };
    //3.BaseFragmentPagerAdapter
    private MyBaseFragmentPagerAdapter myBaseFragmentPagerAdapter;
    //4.BaseFragmentStatePagerAdapter
    private MyBaseFragmentStatePagerAdapter myBaseFragmentStatePagerAdapter;

    private class MyFragmentsAdapter extends BaseFragmentStatePagerAdapter {
        public MyFragmentsAdapter() {
            super(mActivity.getSupportFragmentManager(), titles);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ViewPagerAndInnerFragment.newInstance(position, "第1个Fragment");
                default:
                    return ViewPagerAndInnerFragment.newInstance(position, getStringFormat("第%d个Fragment", position + 1));
            }
        }
    }

    private class MyBaseFragmentPagerAdapter extends BaseFragmentPagerAdapter {
        public MyBaseFragmentPagerAdapter() {
            super(mActivity.getSupportFragmentManager(), titlesPagerAdapter);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            CharSequence pageTitle = getPageTitle(position);
            LogUtils.errorFormat("position = %d, pageTitle = %s", position, pageTitle);
            switch (position) {
                case 0:
                default:
                    return BlankFragment.newInstance(position, pageTitle);
            }
        }
    }

    private class MyBaseFragmentStatePagerAdapter extends BaseFragmentStatePagerAdapter {
        public MyBaseFragmentStatePagerAdapter() {
            super(mActivity.getSupportFragmentManager(), titlesPagerAdapter);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            CharSequence pageTitle = getPageTitle(position);
            LogUtils.errorFormat("%s: position = %d, pageTitle = %s", this, position, pageTitle);
            switch (position) {
                case 0:
                default:
                    return BlankFragment.newInstance(position, pageTitle);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->ViewPager测试");
        
        //1.ViewPager & Fragment 多层嵌套
        viewBinding.toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(buttonView, isChecked));
        viewBinding.switchLoggableFragments.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(buttonView, isChecked));
        viewBinding.switchLoggablePagerAdapter.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(buttonView, isChecked));
        viewBinding.switchLoggableFragmentPagerAdapter.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(buttonView, isChecked));
        viewBinding.switchLoggableFragmentStatePagerAdapter.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(buttonView, isChecked));

        /**
         * 在Activity中传入: getSupportFragmentManager()
         * 在Fragment中传入: getChildFragmentManager()
         */
        viewBinding.viewPager.setAdapter(myFragmentsAdapter = new MyFragmentsAdapter());



        //2.ViewPager & BasePagerAdapter
        viewBinding.viewPagerPagerAdapter.setAdapter(pagerAdapter);
        viewBinding.tabLayoutIndicator.setupWithViewPager(viewBinding.viewPagerPagerAdapter);



        //3.ViewPager & BaseFragmentPagerAdapter
        viewBinding.viewPagerFragmentPagerAdapter.setAdapter(myBaseFragmentPagerAdapter = new MyBaseFragmentPagerAdapter());
        viewBinding.tabLayoutIndicatorFragment.setupWithViewPager(viewBinding.viewPagerFragmentPagerAdapter);



        //4.ViewPager & BaseFragmentStatePagerAdapter
        viewBinding.viewPagerFragmentStatePagerAdapter.setAdapter(myBaseFragmentStatePagerAdapter = new MyBaseFragmentStatePagerAdapter());
        viewBinding.tabLayoutIndicatorFragmentState.setupWithViewPager(viewBinding.viewPagerFragmentStatePagerAdapter);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_button:
                viewBinding.viewPager.setHorizontalScrollable(isChecked);
                ToasterUtils.infoFormat("外面ViewPager左右滑动 = %b", isChecked);
            break;
            case R.id.switch_loggable_fragments:
                myFragmentsAdapter.setLoggable(isChecked);
            break;
            case R.id.switch_loggable_pager_adapter:
                pagerAdapter.setLoggable(isChecked);
            break;
            case R.id.switch_loggable_fragment_pager_adapter:
                myBaseFragmentPagerAdapter.setLoggable(isChecked);
            break;
            case R.id.switch_loggable_fragment_state_pager_adapter:
                myBaseFragmentStatePagerAdapter.setLoggable(isChecked);
            break;
        default:
            break;
        }
    }

    @Override
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
        case R.id.btn_add_page_adapter: //Add Page
            String textAdd = viewBinding.etPositionPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textAdd)) {
                ToasterUtils.warning(viewBinding.etPositionPagerAdapter.getHint());
                return;
            }
            int positionAdd = Integer.parseInt(textAdd);
            pagerAdapter.addPage(positionAdd, "addPage, position = " + positionAdd);
            break;
        case R.id.btn_remove_page_adapter:  //Remove Page
            String textRemove = viewBinding.etPositionPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textRemove)) {
                ToasterUtils.warning(viewBinding.etPositionPagerAdapter.getHint());
                return;
            }
            int positionRemove = Integer.parseInt(textRemove);
            pagerAdapter.removePage(viewBinding.viewPagerPagerAdapter, positionRemove);
            break;
        case R.id.btn_exchange_pager_adapter: //Exchange Page
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


        case R.id.btn_add_fragment_page_adapter: //Add Fragment
           String textAddF = viewBinding.etPositionFragmentPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textAddF)) {
                ToasterUtils.warning(viewBinding.etPositionFragmentPagerAdapter.getHint());
                return;
            }
            int positionAddF = Integer.parseInt(textAddF);
            myBaseFragmentPagerAdapter.addFragment(positionAddF, "addFragment, position = " + positionAddF);
            break;
        case R.id.btn_remove_fragment_page_adapter:  //Remove Fragment
            String textRemoveF = viewBinding.etPositionFragmentPagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textRemoveF)) {
                ToasterUtils.warning(viewBinding.etPositionFragmentPagerAdapter.getHint());
                return;
            }
            int positionRemoveF = Integer.parseInt(textRemoveF);
            myBaseFragmentPagerAdapter.removeFragment(viewBinding.viewPagerFragmentPagerAdapter, positionRemoveF);
            break;
        case R.id.btn_exchange_fragment_pager_adapter: //Exchange Fragment
                String fromTextF = viewBinding.etExchangeFromPositionFragmentPagerAdapter.getText().toString().trim();
                String toTextF = viewBinding.etExchangeToPositionFragmentPagerAdapter.getText().toString().trim();
                if (TextUtils.isEmpty(fromTextF)) {
                    ToasterUtils.warning(viewBinding.etExchangeFromPositionFragmentPagerAdapter.getHint());
                    return;
                }
                if (TextUtils.isEmpty(toTextF)) {
                    ToasterUtils.warning(viewBinding.etExchangeToPositionFragmentPagerAdapter.getHint());
                    return;
                }
                int fromPositionF = Integer.parseInt(fromTextF);
                int toPositionF = Integer.parseInt(toTextF);
                myBaseFragmentPagerAdapter.exchangeFragment(fromPositionF, toPositionF);
            break;


        case R.id.btn_add_fragment_state_page_adapter: //Add Fragment
           String textAddFS = viewBinding.etPositionFragmentStatePagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textAddFS)) {
                ToasterUtils.warning(viewBinding.etPositionFragmentStatePagerAdapter.getHint());
                return;
            }
            int positionAddFS = Integer.parseInt(textAddFS);
            myBaseFragmentStatePagerAdapter.addFragment(positionAddFS, "addFragment, position = " + positionAddFS);
            break;
        case R.id.btn_remove_fragment_state_page_adapter:  //Remove Fragment
            String textRemoveFS = viewBinding.etPositionFragmentStatePagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(textRemoveFS)) {
                ToasterUtils.warning(viewBinding.etPositionFragmentStatePagerAdapter.getHint());
                return;
            }
            int positionRemoveFS = Integer.parseInt(textRemoveFS);
            myBaseFragmentStatePagerAdapter.removeFragment(viewBinding.viewPagerFragmentStatePagerAdapter, positionRemoveFS);
            break;
        case R.id.btn_exchange_fragment_state_pager_adapter: //Exchange Fragment
            String fromTextFS = viewBinding.etExchangeFromPositionFragmentStatePagerAdapter.getText().toString().trim();
            String toTextFS = viewBinding.etExchangeToPositionFragmentStatePagerAdapter.getText().toString().trim();
            if (TextUtils.isEmpty(fromTextFS)) {
                ToasterUtils.warning(viewBinding.etExchangeFromPositionFragmentStatePagerAdapter.getHint());
                return;
            }
            if (TextUtils.isEmpty(toTextFS)) {
                ToasterUtils.warning(viewBinding.etExchangeToPositionFragmentStatePagerAdapter.getHint());
                return;
            }
            int fromPositionFS = Integer.parseInt(fromTextFS);
            int toPositionFS = Integer.parseInt(toTextFS);
            myBaseFragmentStatePagerAdapter.exchangeFragment(fromPositionFS, toPositionFS);
            break;
        default:
            break;
        }
    }
}
