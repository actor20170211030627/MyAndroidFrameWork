package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.myandroidframework.widget.webview.BaseWebChromeClient;
import com.actor.myandroidframework.widget.webview.BaseWebView;
import com.actor.myandroidframework.widget.webview.BaseWebViewClient;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityWebViewBinding;

/**
 * description: WebView
 *
 * @author : ldf
 * date       : 2020/12/21 on 13:33
 */
public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

    private final ValueCallback<String> valueCallback = new ValueCallback() {
        @Override
        public void onReceiveValue(Object value) {
            ToasterUtils.success(value);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.webView.init(new BaseWebViewClient(), new BaseWebChromeClient());
        viewBinding.webView.loadUrl("file:///android_asset/html_call_java_call_html.html");
//        viewBinding.btnGo.callOnClick();
//        viewBinding.btnGo.performClick();
    }

    private Object[] params = {"张三", 23, true, 'a', 35L, null, valueCallback};

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_alert:
                viewBinding.webView.evaluateJavascript("javascript:alert('你好Alert!')", valueCallback);
                break;
            case R.id.btn_confirm:
                viewBinding.webView.evaluateJavascript("javascript:confirm('你好Confirm!')", valueCallback);
                break;
            case R.id.btn_prompt:
                viewBinding.webView.evaluateJavascript("javascript:prompt('请输入口令:')", valueCallback);
                break;
            case R.id.btn_console_log:
                viewBinding.webView.loadUrl("javascript:console.log('H5打印日志!')");
                break;
            case R.id.btn_set_userinfo:
//                viewBinding.webView.evaluateJavascript("javascript:setUserInfo('张三', 23)", valueCallback);

                //张三,23,true,a,35,null,com.actor.sample.activity.WebViewActivity$1@d06e5c0
                String s = BaseWebView.params2String(params);
                LogUtils.errorFormat("s=%s, s1=s", s);
                break;
            default:
                break;
        }
    }
}