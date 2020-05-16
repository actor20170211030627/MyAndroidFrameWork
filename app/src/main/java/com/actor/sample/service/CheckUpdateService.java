package com.actor.sample.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.actor.myandroidframework.service.BaseService;
import com.actor.myandroidframework.utils.easyhttp.BaseCallBack6;
import com.actor.myandroidframework.utils.easyhttp.EasyHttpUtils;
import com.actor.myandroidframework.utils.okhttputils.GetFileCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.actor.sample.info.CheckUpdateInfo;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;

import java.io.File;
import java.util.List;

import okhttp3.Call;

/**
 * Description: 检查更新
 * 1.修改请求地址
 * 2.在清单文件中注册!!!
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/10/19 on 14:39
 *
 * @version 1.0
 */
public class CheckUpdateService extends BaseService {

    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //check update检查更新
//        MyOkHttpUtils.get(Global.CHECK_UPDATE, null, new BaseCallback<List<CheckUpdateInfo>>(this) {
//            @Override
//            public void onOk(@NonNull List<CheckUpdateInfo> info, int id) {
//                if (info.size() == 0) return;
//                CheckUpdateInfo info1 = info.get(0);
//                if (info1 == null) return;
//                CheckUpdateInfo.ApkDataBean apkData = info1.apkData;
//                if (apkData != null) {
//                    int versionCode = AppUtils.getAppVersionCode();
//                    if (versionCode < apkData.versionCode) {
//                        showDialog(apkData.versionName);
//                    }
//                }
//            }
//        });
        EasyHttpUtils.get(Global.CHECK_UPDATE, null, new BaseCallBack6<List<CheckUpdateInfo>>(this) {
            @Override
            public void onSuccess(List<CheckUpdateInfo> info) {
                if (info.size() == 0) return;
                CheckUpdateInfo info1 = info.get(0);
                if (info1 == null) return;
                CheckUpdateInfo.ApkDataBean apkData = info1.apkData;
                if (apkData != null) {
                    int versionCode = AppUtils.getAppVersionCode();
                    if (versionCode < apkData.versionCode) {
                        showDialog(apkData.versionName);
                    }
                }
            }
        });
    }

    private void showDialog(String newVersionName) {
        if (newVersionName == null) newVersionName = "";
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity == null) return;
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(topActivity)
                    .setTitle("Update: 有新版本")
                    .setMessage("有新版本: ".concat(newVersionName).concat(", 快更新吧!"))
                    .setPositiveButton("Ok", (dialog, which) -> {
                        downloadApk();
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        alertDialog.show();
    }

    private void downloadApk() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        progressDialog.show();
        MyOkHttpUtils.getFile(Global.DOWNLOAD_URL, new GetFileCallback(this, null, null) {

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                logFormat("下载文件: progress=%f, total=%d, id=%d", progress, total, id);
                progressDialog.setProgress((int) (progress * 100));
            }

            @Override
            public void onOk(@NonNull File info, int id) {
                progressDialog.dismiss();
                AppUtils.installApp(info);
                stopSelf();
            }

            @Override
            public void onError(int id, Call call, Exception e) {
                super.onError(id, call, e);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyOkHttpUtils.cancelTag(this);
    }
}
