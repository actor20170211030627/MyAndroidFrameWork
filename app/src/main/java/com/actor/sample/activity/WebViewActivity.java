package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.actor.myandroidframework.widget.webview.BaseWebChromeClient;
import com.actor.myandroidframework.widget.webview.BaseWebView;
import com.actor.myandroidframework.widget.webview.BaseWebViewClient;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityWebViewBinding;
import com.blankj.utilcode.util.KeyboardUtils;

/**
 * description: WebView
 *
 * @author : ldf
 * date       : 2020/12/21 on 13:33
 */
public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

    private EditText    etContent;
    private Button      btnGo;
    private BaseWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_web_view);
        etContent = viewBinding.etContent;
        btnGo = viewBinding.btnGo;
        webView = viewBinding.webView;

        etContent.setText("https://gitee.com/actor20170211030627/MyAndroidFrameWork");
        /**
         * 雷电模拟器5.1.1会进入 {@link BaseWebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)} 这个方法
         * 会报错, 原因未知, 莫名其妙!
         */
        webView.init(new BaseWebViewClient(), new BaseWebChromeClient());
        btnGo.performClick();
    }

//    @OnClick({R.id.btn_go})
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