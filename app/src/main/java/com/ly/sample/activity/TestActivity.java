package com.ly.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.actor.myandroidframework.widget.BaseSpinner;
import com.ly.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 临时测试
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/8/18 on 22:22
 *
 * @version 1.0
 */
public class TestActivity extends BaseActivity {

    @BindView(R.id.sp_data)
    BaseSpinner spData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        spData.setOnItemSelectedListener(new BaseSpinner.OnItemSelectedListener2() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toast("选中了: " + position);
                logError("选中了: " + position);
            }

            @Override
            public void onItemReSelected(AdapterView<?> parent, View view, int position, long id) {
                toast("重复选中了: " + position);
                logError("重复选中了: " + position);
            }
        });
    }

    @OnClick(R.id.btn_result_ok)
    public void onViewClicked() {
        setResult(RESULT_OK);
        onBackPressed();
    }
}
