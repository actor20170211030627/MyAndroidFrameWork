package com.actor.sample.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.okhttputils.GetFileCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.actor.sample.info.CheckUpdateInfo;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;

import java.io.File;
import java.util.List;

import okhttp3.Call;

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
        MyOkHttpUtils.get(Global.CHECK_UPDATE, null, new BaseCallback<CheckUpdateInfo>(tag) {
            @Override
            public void onOk(@NonNull CheckUpdateInfo info, int id, boolean isRefresh) {
                List<CheckUpdateInfo.ElementsBean> elements = info.elements;
                if (elements != null && !elements.isEmpty()) {
                    CheckUpdateInfo.ElementsBean elementsBean = elements.get(0);
                    if (elementsBean != null) {
                        int versionCode = AppUtils.getAppVersionCode();
                        if (versionCode < elementsBean.versionCode) {
                            showDialog(elementsBean.versionName);
                        }
                    }
                }
            }
        });
    }

    private void showDialog(String newVersionName) {
        if (newVersionName == null) newVersionName = "";
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity == null || topActivity.isDestroyed()) return;
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(topActivity)
                    .setTitle("Update: 有新版本")
                    .setMessage("有新版本: ".concat(newVersionName).concat(", 快更新吧!"))
                    .setPositiveButton("Ok", (dialog, which) -> downloadApk(topActivity))
                    .setNegativeButton("Cancel", null)
                    .create();
        }
        alertDialog.show();
    }

    private void downloadApk(Activity topActivity) {
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
        MyOkHttpUtils.getFile(Global.DOWNLOAD_URL, null, null, new GetFileCallback(tag, null, null) {

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                logFormat("下载文件: progress=%f, total=%d, id=%d", progress, total, id);
                progressDialog.setProgress((int) (progress * 100));
            }

            @Override
            public void onOk(@NonNull File info, int id, boolean isRefresh) {
                progressDialog.dismiss();
                AppUtils.installApp(info);
            }

            @Override
            public void onError(int id, Call call, Exception e) {
//                super.onError(id, call, e);
                progressDialog.dismiss();
                toast("下载失败, 请到Github下载.");
            }
        });
    }
}
