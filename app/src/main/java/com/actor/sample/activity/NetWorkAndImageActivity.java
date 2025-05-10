package com.actor.sample.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.FileUtils;
import com.actor.myandroidframework.utils.glide.DrawableRequestListener;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityNetWorkAndImageBinding;
import com.actor.sample.info.CheckUpdateInfo;
import com.actor.sample.info.EasyHttpUploadFileInfo;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.PathUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
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

    private       String                  picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->网络&图片");

        //清除目标 Activity 或 Fragment 的进入&退出过渡动画。
        if (false) {
            SharedElementUtils.cleanTransitionInDestinationActivity(getWindow());
        }

        /**
         * 推迟 Activity 或者 Fragment 的进入过渡动画。
         */
        SharedElementUtils.postponeEnterTransition(this);

        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .listener(new DrawableRequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        /**
                         * 启动之前被推迟的进入过渡动画。
                         */
                        SharedElementUtils.startPostponedEnterTransition(mActivity);
                        return super.onLoadFailed(e, model, target, isFirstResource);
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        /**
                         * 启动之前被推迟的进入过渡动画。
                         */
                        SharedElementUtils.startPostponedEnterTransition(mActivity);
                        return super.onResourceReady(resource, model, target, dataSource, isFirstResource);
                    }
                })
                .into(viewBinding.iv);
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
                                LocalMedia localMedia = result.get(0);
                                PictureSelectorUtils.printLocalMedia(localMedia);
//                                picPath = localMedia.getRealPath();   //没有sd卡读权限, 不能传这个
                                picPath = localMedia.getSandboxPath();
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
                startActivity(SocketTestActivity.class);
                break;
            default:
                break;
        }
    }

    private void getByEasyHttp() {
        EasyHttp.get(this)
//                .api(Global.CHECK_UPDATE)
                .api(CheckUpdateInfo.class)
                .request(new OnHttpListener<CheckUpdateInfo.CheckUpdateBean>() {
            @Override
            public void onHttpSuccess(@NonNull CheckUpdateInfo.CheckUpdateBean result) {
                ToasterUtils.successFormat("请求成功, variantName=%s", result.variantName);
            }
            @Override
            public void onHttpFail(@NonNull Throwable throwable) {
                ToasterUtils.error("请求失败!");
            }
        });
    }

    private void downloadFile() {
        showNetWorkLoadingDialog();
        EasyHttp.download(this)
                .url(Global.GRADLE_DOWNLOAD_URL)
                .file(new File(PathUtils.getFilesPathExternalFirst(), FileUtils.getFileNameFromUrl(Global.GRADLE_DOWNLOAD_URL)))
                .listener(new OnDownloadListener() {
                    @Override
                    public void onDownloadProgressChange(@NonNull File file, int progress) {
//                            LogUtils.error(String.valueOf(progress));
                        viewBinding.progressBar.setProgress(progress);
                    }
                    @Override
                    public void onDownloadSuccess(@NonNull File file) {
                        dismissNetWorkLoadingDialog();
                        ToasterUtils.successFormat("下载完成: %s", file.getAbsolutePath());
                    }
                    @Override
                    public void onDownloadFail(@NonNull File file, @NonNull Throwable throwable) {
                        dismissNetWorkLoadingDialog();
                        ToasterUtils.errorFormat("下载错误: %s", throwable.getMessage());
                    }
                }).start();
    }

    /**
     * 上传单个图片/文件
     */
    protected void uploadFile(@NonNull String filePath) {
        showNetWorkLoadingDialog();
        EasyHttp.post(this)
                .api(new EasyHttpUploadFileInfo(filePath))
                .request(new OnUpdateListener<String>() {
                    @Override
                    public void onUpdateProgressChange(int progress) {
                        viewBinding.progressBar.setProgress(progress);
                    }
                    @Override
                    public void onUpdateSuccess(@NonNull String result) {
                        dismissNetWorkLoadingDialog();
                        ToasterUtils.success(result);
                    }
                    @Override
                    public void onUpdateFail(@NonNull Throwable throwable) {
                        dismissNetWorkLoadingDialog();
                        ToasterUtils.error(throwable.getMessage());
                    }
                });
    }
}
