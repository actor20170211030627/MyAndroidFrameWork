package com.ly.sample.activity;

import android.os.Bundle;

import com.ly.sample.R;
import com.yanzhenjie.album.mvp.BaseActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_result_ok)
    public void onViewClicked() {
        setResult(RESULT_OK);
        onBackPressed();
    }
}
