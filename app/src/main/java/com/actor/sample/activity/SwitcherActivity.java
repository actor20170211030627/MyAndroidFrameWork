package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.widget.BaseTextSwitcher;
import com.actor.myandroidframework.widget.BaseViewSwitcher;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivitySwitcherBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 主页->切换
 * Author     : ldf
 * Date       : 2019-9-6 on 14:23
 */
public class SwitcherActivity extends BaseActivity<ActivitySwitcherBinding> {

    private BaseTextSwitcher<CharSequence> bts;
    private BaseViewSwitcher<CharSequence> bvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_switcher);

        setTitle("主页->切换");
        bts = viewBinding.bts;
        bvs = viewBinding.bvs;

        List<CharSequence> datas = new ArrayList<>();
        datas.add("11111111fsdlkjdfioiorioiodvmiowfmvoifi");
        datas.add("222dfsdiook,poopeg,po,;d,pv,o,vopv,ov,");
        datas.add("333333333ok,poopegdw撒旦法师打发斯蒂芬的");
        bts.setDataSource(datas);
        bts.setOnItemClickListenerForTextSwitcher(new BaseTextSwitcher.OnItemClickListener<CharSequence>() {
            @Override
            public void onItemClick(TextView textView, int position, CharSequence item) {
                logFormat("pos=%d, str=%s", position, item);
            }
        });

        bvs.init(R.layout.item_base_view_switcher, new BaseViewSwitcher.OnSwitcherListener<CharSequence>() {
            @Override
            public void onSwitch(View view, int position, CharSequence item) {
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(item);
                logFormat("onSwitch: view=%s, pos=%d, item=%s", view, position, item);
            }
        });
        bvs.setOnItemClickListenerForViewSwitcher(new BaseViewSwitcher.OnItemClickListener<CharSequence>() {
            @Override
            public void onItemClick(View view, int position, CharSequence item) {
                logFormat("pos=%d, str=%s", position, item);
            }
        });
        bvs.setDataSource(datas);
    }

//    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                bts.startSwitch();
                bvs.startSwitch();
                break;
            case R.id.btn_stop:
                bts.stopSwitcher();
                bvs.stopSwitch();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bts.stopSwitcher();
        bvs.stopSwitch();
    }
}
