package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.widget.BaseTextSwitcher;
import com.actor.myandroidframework.widget.BaseViewSwitcher;
import com.actor.sample.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->切换
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-9-6 on 14:23
 */
public class SwitcherActivity extends BaseActivity {

    @BindView(R.id.bts)
    BaseTextSwitcher bts;
    @BindView(R.id.bvs)
    BaseViewSwitcher bvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switcher);
        ButterKnife.bind(this);

        setTitle("主页->切换");
        List<CharSequence> datas = new ArrayList<>();
        datas.add("11111111fsdlkjdfioiorioiodvmiowfmvoifi");
        datas.add("222dfsdiook,poopeg,po,;d,pv,o,vopv,ov,");
        datas.add("333333333ok,poopegdw撒旦法师打发斯蒂芬的");
        bts.setDataSource(datas);
        bts.setOnItemClickListener(new BaseTextSwitcher.OnItemClickListener() {
            @Override
            public void onItemClick(TextView textView, int position, CharSequence charSequence) {
                logFormat("pos=%d, str=%s", position, charSequence);
            }
        });

        bvs.init(R.layout.item_base_view_switcher, new BaseViewSwitcher.OnSwitcherListener<CharSequence>() {
            @Override
            public void onSwitch(View view, int position, CharSequence data) {
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(data);
                logFormat("onSwitch: view=%s, pos=%d, item=%s", view, position, data);
            }
        });
        bvs.setOnItemClickListener(new BaseViewSwitcher.OnItemClickListener<CharSequence>() {
            @Override
            public void onItemClick(View view, int position, CharSequence data) {
                logFormat("pos=%d, str=%s", position, data);
            }
        });
        bvs.setDataSource(datas);
    }

    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                bts.startSwitch();
                bvs.startSwitch();
                break;
            case R.id.btn_stop:
                bts.stopSwitcher();
                bvs.stopSwitcher();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bts.stopSwitcher();
        bvs.stopSwitcher();
    }
}
