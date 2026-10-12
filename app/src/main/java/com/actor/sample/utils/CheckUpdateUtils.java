package com.actor.sample.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.SPUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.info.CheckUpdateInfo;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnHttpListener;

import java.io.File;
import java.util.List;

/**
 * Description: 检查更新
 * 1.修改请求地址
 * 2.使用: new CheckUpdateUtils().check(this);
 *
 * Author     : ldf
 * Date       : 2019/10/19 on 14:39
 *
 * @version 1.0
 */
public class CheckUpdateUtils {

    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    //check update检查更新
    @RequiresPermission(value = Manifest.permission.REQUEST_INSTALL_PACKAGES)
    public void check(LifecycleOwner tag) {
        EasyHttp.get(tag)
                .api(CheckUpdateInfo.class)
                .request(new OnHttpListener<CheckUpdateInfo.CheckUpdateBean>() {
                    @Override
                    public void onHttpSuccess(@NonNull CheckUpdateInfo.CheckUpdateBean result) {
                        SPUtils.putBoolean(Global.IS_CHECK_UPDATE, true, SPUtils.ExpireInDay);
                        List<CheckUpdateInfo.CheckUpdateBean.ElementsBean> elements = result.elements;
                        if (elements == null || elements.isEmpty()) return;
                        CheckUpdateInfo.CheckUpdateBean.ElementsBean elementsBean = elements.get(0);
                        if (elementsBean == null) return;
                        int versionCode = AppUtils.getAppVersionCode();
                        if (versionCode < elementsBean.versionCode) {
                            showDialog(elementsBean);
                        }
                    }
                    @Override
                    public void onHttpFail(@NonNull Throwable throwable) {
                    }
                });
    }

    private void showDialog(@NonNull CheckUpdateInfo.CheckUpdateBean.ElementsBean elementsBean) {
        if (elementsBean.versionName == null) elementsBean.versionName = "";
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity == null || topActivity.isDestroyed()) return;
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(topActivity)
                    .setTitle("Update: 有新版本")
                    .setMessage("有新版本: ".concat(elementsBean.versionName).concat(", 快更新吧!"))
                    .setPositiveButton("Ok", (dialog, which) -> {
                        downloadApk(topActivity, elementsBean);
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
        }
        alertDialog.show();
    }

    private void downloadApk(@NonNull Activity topActivity, @NonNull CheckUpdateInfo.CheckUpdateBean.ElementsBean elementsBean) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(topActivity);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        progressDialog.show();
        LifecycleOwner tag = null;
        if (topActivity instanceof LifecycleOwner) {
            tag = (LifecycleOwner) topActivity;
        }
        EasyHttp.download(tag)
                .url(TextUtils2.getStringFormat(Global.DOWNLOAD_URL, elementsBean.versionName, elementsBean.outputFile))
                .listener(new OnDownloadListener() {
                    @Override
                    public void onDownloadProgressChange(@NonNull File file, int progress) {
                        LogUtils.errorFormat("下载文件: progress=%d", progress);
                        progressDialog.setProgress(progress);
                    }
                    @Override
                    public void onDownloadSuccess(@NonNull File file) {
                        progressDialog.dismiss();
                        AppUtils.installApp(file);
                    }
                    @Override
                    public void onDownloadFail(@NonNull File file, @NonNull Throwable throwable) {
                        progressDialog.dismiss();
                        ToasterUtils.error("下载失败, 请到Github下载.");
                    }
                }).start();
    }
}
