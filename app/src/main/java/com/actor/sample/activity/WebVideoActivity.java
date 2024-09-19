package com.actor.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;

import com.actor.myandroidframework.widget.webview.BaseWebChromeClient;
import com.actor.myandroidframework.widget.webview.BaseWebViewClient;
import com.actor.sample.databinding.ActivityWebVideoBinding;

/**
 * description: 播放网页视频
 *      在清单文件的<activity 中添加: android:configChanges="orientation|screenSize|keyboardHidden" (不添加也可以)
 * @author    : ldf
 * date       : 2024/9/19 on 14:32
 */
public class WebVideoActivity extends BaseActivity<ActivityWebVideoBinding> {

    private final String videoUrl = "https://player.bilibili.com/player.html?isOutside=true&aid=1756207625&bvid=BV164421U781&cid=1613225408&p=1";

    public static void start(Context context) {
        context.startActivity(new Intent(context, WebVideoActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("播放网页视频");

        showNetWorkLoadingDialog();
        viewBinding.webView.init(new BaseWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showNetWorkLoadingDialog(); //会进入2次
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissNetWorkLoadingDialog();
            }
        }, (BaseWebChromeClient) null   //第2参数传null, 原因↙

            /**
             * if第2参数不传null的话, 就要自己处理onShowCustomView()方法, 否则b站视频就不播放了并有报错信息
             */
//           new BaseWebChromeClient() {
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                viewBinding.tvTitle.setText(title);
//            }
//
//            @Override
//            public void onReceivedIcon(WebView view, Bitmap icon) {
//                super.onReceivedIcon(view, icon);
//                viewBinding.ivIcon.setImageBitmap(icon);
//            }
//
//           @Override
//           public void onShowCustomView(View view, CustomViewCallback callback) {
//               super.onShowCustomView(view, callback);
//               //全屏播放视频(请实现本方法, 否则视频可能会停止播放!!!)
//           }
//        }
        );
        viewBinding.webView.loadUrl(videoUrl);


    }

    @Override
    protected void onResume() {
        super.onResume();
        viewBinding.webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewBinding.webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.webView.stopLoading();
        viewBinding.webView.removeAllViews();
        viewBinding.webView.destroy();
    }
}