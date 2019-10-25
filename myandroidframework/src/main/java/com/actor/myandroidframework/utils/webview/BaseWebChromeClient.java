package com.actor.myandroidframework.utils.webview;

import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.actor.myandroidframework.utils.LogUtils;

/**
 * Description: WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/11 on 11:33
 * @version 1.0
 */
public class BaseWebChromeClient extends WebChromeClient {

    //可重写此方法, 获取标题
    @Override
    public void onReceivedTitle(WebView view, String title) {//获取网页标题
        super.onReceivedTitle(view, title);
        LogUtils.error("网页标题:" + title, false);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {//图标
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {//加载进度回调,newProgress当前的进度

    }

    //打印前端的日记
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        int lineNumber = consoleMessage.lineNumber();
        String message = consoleMessage.message();
        String sourceId = consoleMessage.sourceId();
        //日志级别:ConsoleMessage.MessageLevel.TIP,LOG,WARNING,ERROR,DEBUG;
        ConsoleMessage.MessageLevel messageLevel = consoleMessage.messageLevel();
        return super.onConsoleMessage(consoleMessage);
    }

    //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
    @Override
    public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
        localBuilder.setMessage(message).setPositiveButton("确定", null);
        localBuilder.setCancelable(false);
        localBuilder.create().show();

        //注意:
        //必须要这一句代码:result.confirm()表示:
        //处理结果为确定状态同时唤醒WebCore线程
        //否则不能继续点击按钮
        result.confirm();
        return true;
    }

    //js'confirm,确认/取消对话框
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }
    //js'prompt,可提示用户进行输入的对话框
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
}
