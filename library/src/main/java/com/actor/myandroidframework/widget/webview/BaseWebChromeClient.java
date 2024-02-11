package com.actor.myandroidframework.widget.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.GsonUtils;

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
        LogUtils.errorFormat("网页标题onReceivedTitle: %s", title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {//图标
        super.onReceivedIcon(view, icon);
        LogUtils.error("获取到网页Icon, onReceivedIcon");
    }

    //加载进度回调,newProgress当前的进度
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        //LogUtils.errorFormat("网页进度改变, onProgressChanged=%d", newProgress);
    }

    //打印前端的日志
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        int lineNumber = consoleMessage.lineNumber();
        String message = consoleMessage.message();
        String sourceId = consoleMessage.sourceId();
        //日志级别:ConsoleMessage.MessageLevel.TIP,LOG,WARNING,ERROR,DEBUG;
        ConsoleMessage.MessageLevel messageLevel = consoleMessage.messageLevel();
        LogUtils.errorFormat("前端日志: lineNumber=%d, message=%s, sourceId=%s, messageLevel=%s",
                lineNumber, message, sourceId, messageLevel);
        return super.onConsoleMessage(consoleMessage);
    }
    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        super.onConsoleMessage(message, lineNumber, sourceID);
    }

    //js的alert弹窗
    @Override
    public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
        LogUtils.errorFormat("alert弹窗: url=%s, message=%s", url, message);
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
        LogUtils.errorFormat("confirm对话框: url=%s, message=%s", url, message);
        return super.onJsConfirm(view, url, message, result);
    }

    /**
     * js 的提示, 可提示用户进行输入的对话框
     * @param message 有长度限制, 不同手机限制长度不一致
     * @param defaultValue ?
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        LogUtils.errorFormat("prompt输入提示框: url=%s, message=%s, defaultValue=%s", url, message, defaultValue);
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    /**
     * @return 网页在视频缓冲时显示的视图
     */
    @Override
    public View getVideoLoadingProgressView() {
        LogUtils.error("getVideoLoadingProgressView: 网页在视频缓冲时显示的视图");
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
        LogUtils.error("onShowCustomView: 可用于播放视频");
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
        LogUtils.error("onHideCustomView: 可用于退出全屏播放");
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
        LogUtils.error("onGeolocationPermissionsShowPrompt: 网页定位允许弹框");
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
     * 选择文件: <input type="file" name = "选择文件" />
     */
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (webView == null || filePathCallback == null) return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        LogUtils.error("onShowFileChooser(选择文件), 请你自己重写此方法, 并在重写的方法中书写选择文件的逻辑(可参考下方注释的部分代码!)");
        if (fileChooserParams == null) {
            LogUtils.error("fileChooserParams=null");
        } else {
            LogUtils.errorFormat("fileChooserParams={'AcceptTypes':%s, 'FilenameHint':%s, 'Title':%s, 'Mode':%d, 'isCaptureEnabled':%b}",
                    GsonUtils.toJson(fileChooserParams.getAcceptTypes()),
                    fileChooserParams.getFilenameHint(),
                    fileChooserParams.getTitle(),
                    fileChooserParams.getMode(),
                    fileChooserParams.isCaptureEnabled()
            );
        }

        /**
         * 1.请在你的Activity/Fragment中, 重写此方法, 并在重写的方法中书写选择文件的逻辑(可参考下方注释的部分代码!)
         * ValueCallback<Uri[]> filePathCallback2; //是一个全局变量, 用于选择文件后回调给h5
         */
//        filePathCallback2 = filePathCallback;
//        Intent intent = fileChooserParams.createIntent();
//        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);//参2: 请求码是你自己定义的一个int常量

        /**
         * 2.然后在你的Activity/Fragment中, 重写以下类似方法将结果回调给h5
         */
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            if (requestCode == REQUEST_CODE_FILE_CHOOSER) {
//                Uri[] fileUris = null;
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    //将选择的文件赋值给返回值
//                    fileUris = ...;
//                }
//                //将选择的文件回传给h5
//                filePathCallback2.onReceiveValue(fileUris);
//            }
//        }

        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    /**
     * 如果设置{@link WebSettings#supportMultipleWindows()} = true, 支持打开新窗口.
     * 则应该重写这个方法
     */
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }
}
