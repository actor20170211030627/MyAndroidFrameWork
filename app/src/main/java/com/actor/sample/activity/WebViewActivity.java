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

        etContent.setText("https://gitee.com/actor20170211030627/MyAndroidFrameWork");
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