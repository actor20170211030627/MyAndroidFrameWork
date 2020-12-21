package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actor.myandroidframework.widget.webview.BaseWebChromeClient;
import com.actor.myandroidframework.widget.webview.BaseWebView;
import com.actor.myandroidframework.widget.webview.BaseWebViewClient;
import com.actor.sample.R;
import com.blankj.utilcode.util.KeyboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * description: WebView
 *
 * @author : 李大发
 * date       : 2020/12/21 on 13:33
 */
public class WebViewActivity extends BaseActivity {

    @BindView(R.id.et_content)
    EditText    etContent;
    @BindView(R.id.btn_go)
    Button      btnGo;
    @BindView(R.id.web_view)
    BaseWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

//        etContent.setText("file:///android_asset/html_call_java.html");//java&H5 互调

//        etContent.setText("http://10.189.13.197:1250/#/activity11");//梁荣双11, 试版
//        etContent.setText("http://h5.12316cq.com/yf/#/activity11");//双11活动yf正式链接
//        etContent.setText("http://h5.12316cq.com/pages/yngg/#/activity11");//双11活动正式链接

//        etContent.setText("http://183.230.101.142:6620/pages/yngg/#/dsfw/foodHall");//美食馆, 测试
//        etContent.setText("http://h5.12316cq.com/pages/yngg/#/dsfw/foodHall");//美食馆

//        etContent.setText("http://192.168.1.102:5502/pages/index/index.html?assocaiationID=8822");

//        etContent.setText("http://183.230.101.142:6620/pages/yngg/#/fiveblessings");//新春活动, 测试
//        etContent.setText("http://h5.12316cq.com/pages/yngg/#/fiveblessings");//新春活动

//        etContent.setText("https://www.baidu.com");//百度

        etContent.setText("https://github.com/actor20170211030627/MyAndroidFrameWork1");//404

        webView.init(new BaseWebViewClient(), new BaseWebChromeClient());
        btnGo.performClick();
    }

    @OnClick({R.id.btn_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_go:
                webView.loadUrl(etContent.getText().toString());
                KeyboardUtils.hideSoftInput(view);
                break;
            default:
                break;
        }
    }
}