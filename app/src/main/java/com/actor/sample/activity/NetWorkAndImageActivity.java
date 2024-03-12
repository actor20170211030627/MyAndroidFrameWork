package com.actor.sample.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.FileUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityNetWorkAndImageBinding;
import com.actor.sample.info.CheckUpdateInfo;
import com.actor.sample.info.EasyHttpUploadFileInfo;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.PathUtils;
import com.bumptech.glide.Glide;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.listener.OnUpdateListener;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Description: 主页->网络&图片
 * Author     : ldf
 * Date       : 2019-9-6 on 14:23
 */
public class NetWorkAndImageActivity extends BaseActivity<ActivityNetWorkAndImageBinding> {

    private ProgressBar progressBar;

    private boolean alreadyDownload = false;
    private String picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = viewBinding.progressBar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        setTitle("主页->网络&图片");
        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .into(viewBinding.iv);
        viewBinding.iv.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition();
                }
            }
        });
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_easy_http://EasyHttp方式获取数据
                getByEasyHttp();
                break;
            case R.id.btn_get_retrofit://Retrofit方式获取数据
                ToasterUtils.warning("Retrofit已删除, 感觉不好用.");
                break;
            case R.id.btn_download://下载进度测试
                downloadFile();
                break;
            case R.id.btn_select_pic://选择图片
                PictureSelectorUtils.create(this, null)
                        .selectImage(false)
                        .setSingleSelect(true)
                        .setShowCamera(true)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                picPath = result.get(0).getRealPath();
                            }
                            @Override
                            public void onCancel() {
                            }
                        });
                break;
            case R.id.btn_upload://上传文件(可上传中文名文件)
                if (picPath != null) {
                    uploadFile(picPath);
                } else ToasterUtils.warning("请选择图片");
                break;
            case R.id.btn_socket_example:
                //okhttp的Socket示例
                startActivity(new Intent(this, SocketTestActivity.class));
                break;
            default:
                break;
        }
    }

    private void getByEasyHttp() {
        EasyHttp.get(this)
//                .api(Global.CHECK_UPDATE)
                .api(CheckUpdateInfo.class)
                .request(new OnHttpListener<CheckUpdateInfo>() {
            @Override
            public void onHttpSuccess(CheckUpdateInfo result) {
                ToasterUtils.successFormat("请求成功, variantName=%s", result.variantName);
            }
            @Override
            public void onHttpFail(Throwable throwable) {
                ToasterUtils.error("请求失败!");
            }
        });
    }

    private void downloadFile() {
        if (!alreadyDownload) {
            EasyHttp.download(this)
                    .url(Global.GRADLE_DOWNLOAD_URL)
                    .file(new File(PathUtils.getFilesPathExternalFirst(), FileUtils.getFileNameFromUrl(Global.GRADLE_DOWNLOAD_URL)))
                    .listener(new OnDownloadListener() {
                        @Override
                        public void onDownloadProgressChange(File file, int progress) {
//                            LogUtils.error(String.valueOf(progress));
                            progressBar.setProgress(progress);
                        }
                        @Override
                        public void onDownloadSuccess(File file) {
                            ToasterUtils.errorFormat("下载完成: %s", file.getAbsolutePath());
                        }
                        @Override
                        public void onDownloadFail(File file, Throwable throwable) {
                            ToasterUtils.errorFormat("下载错误: %s", throwable.getMessage());
                        }
                    }).start();
            alreadyDownload = true;
        }
    }

    /**
     * 上传单个图片/文件
     */
    protected void uploadFile(@NonNull String filePath) {
        EasyHttp.post(this)
                .api(new EasyHttpUploadFileInfo(filePath))
                .request(new OnUpdateListener<String>() {
                    @Override
                    public void onUpdateProgressChange(int progress) {
                        progressBar.setProgress(progress);
                    }
                    @Override
                    public void onUpdateSuccess(String result) {
                        ToasterUtils.success(result);
                    }
                    @Override
                    public void onUpdateFail(Throwable throwable) {
                        ToasterUtils.success(throwable.getMessage());
                    }
                });
    }
}
