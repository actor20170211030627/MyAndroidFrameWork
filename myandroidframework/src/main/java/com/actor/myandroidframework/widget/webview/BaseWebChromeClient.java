package com.actor.myandroidframework.widget.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * Description: WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
 * Author     : ldf
 * Date       : 2019/3/11 on 11:33
 * @version 1.0
 */
public class BaseWebChromeClient extends WebChromeClient {

    //可重写此方法, 获取网页标题
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        LogUtils.formatError("网页标题onReceivedTitle: %s", true, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {//图标
        super.onReceivedIcon(view, icon);
        LogUtils.error("获取到网页Icon, onReceivedIcon", true);
    }

    //加载进度回调,newProgress当前的进度
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        //LogUtils.formatError("网页进度改变, onProgressChanged=%d", true, newProgress);
    }

    //打印前端的日记
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        int lineNumber = consoleMessage.lineNumber();
        String message = consoleMessage.message();
        String sourceId = consoleMessage.sourceId();
        //日志级别:ConsoleMessage.MessageLevel.TIP,LOG,WARNING,ERROR,DEBUG;
        ConsoleMessage.MessageLevel messageLevel = consoleMessage.messageLevel();
        LogUtils.formatError("前端日记: lineNumber=%d, message=%s, sourceId=%s, messageLevel=%s",
                true, lineNumber, message, sourceId, messageLevel);
        return super.onConsoleMessage(consoleMessage);
    }
    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        super.onConsoleMessage(message, lineNumber, sourceID);
    }

    //js的alert弹窗
    @Override
    public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
        LogUtils.formatError("alert弹窗: url=%s, message=%s", true, url, message);
//        new AlertDialog.Builder(webView.getContext())
//                .setTitle(url)
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                        //处理结果为确定状态同时唤醒WebCore线程
//                        //否则不能继续点击按钮
//                        result.confirm();
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        result.cancel();
//                    }
//                })
//                .setCancelable(false)
//                .create()
//                .show();
//        return true;//拦截自己处理
        return false;
    }

    //js 确认/取消对话框
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        LogUtils.formatError("网页对话框: url=%s, message=%s", true, url, message);
        return super.onJsConfirm(view, url, message, result);
    }

    /**
     * js 的提示, 可提示用户进行输入的对话框
     * @param message 有长度限制, 不同手机限制长度不一致
     * @param defaultValue ?
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        LogUtils.formatError("网页提示: url=%s, message=%s, defaultValue=%s", true, url, message, defaultValue);
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    /**
     * @return 网页在视频缓冲时显示的视图
     */
    @Override
    public View getVideoLoadingProgressView() {
        LogUtils.error("getVideoLoadingProgressView: 网页在视频缓冲时显示的视图", false);
//        FrameLayout frameLayout = new FrameLayout(activity);
//        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//        return frameLayout;
        return null;
    }

    /**
     * WebView中播放视频，一种方案是使用 onShowCustomView 回调的方式，另一种方案就是 JS 绑定回调的方式
     * @param view 在全屏模式时显示的View
     * @param callback
     */
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        LogUtils.error("onShowCustomView: 可用于播放视频", true);
//        fullScreen();
//        webView.setVisibility(View.GONE);
//        videoContainer.setVisibility(View.VISIBLE);
//        isFull=true;
//        videoContainer.addView(view);
//        mCallBack = callback;
        super.onShowCustomView(view, callback);
    }

    /**
     * 隐藏自定义View
     * 如果在全屏播放视频, 应该关闭全屏模式
     */
    @Override
    public void onHideCustomView() {
        LogUtils.error("onHideCustomView: 可用于退出全屏播放", true);
//        exitFullScreen();
//        if (mCallBack!=null){
//            mCallBack.onCustomViewHidden();
//        }
//        webView.setVisibility(View.VISIBLE);
//        videoContainer.removeAllViews();
//        videoContainer.setVisibility(View.GONE);
//        isFull=false;
        super.onHideCustomView();
    }

    /**
     * 允许获取您的地理位置信息吗？
     * @param origin
     * @param callback
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        LogUtils.error("onGeolocationPermissionsShowPrompt: 网页定位允许弹框", true);
//        new AlertDialog.Builder(activity)
//                .setTitle("位置信息")
//                .setMessage(origin + "允许获取您的地理位置信息吗？")
//                .setCancelable(true)
//                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        callback.invoke(origin, true, remember);
//                    }
//                })
//                .setNegativeButton("不允许", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                callback.invoke(origin, false, remember);
//                            }
//                        })
//                .create()
//                .show();
    }

    /**
     * 选择文件
     */
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        LogUtils.error("onShowFileChooser: 选择文件", true);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        Intent chooser = Intent.createChooser(intent, "选择图片");
//        startActivityForResult(chooser, SELECT_FILE_REQUEST_CODE);

//        fileChooserParams.getTitle();
//        Intent intent = fileChooserParams.createIntent();

        //选择图片
//        AlbumUtils.selectImage(activity, true, new Action<ArrayList<AlbumFile>>() {
//            @Override
//            public void onAction(@NonNull ArrayList<AlbumFile> result) {
//                Uri uri = Uri.fromFile(new File(result.get(0).getPath()));
//                filePathCallback.onReceiveValue(new Uri[]{uri});
//            }
//        });

//        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        return true;
    }
}
