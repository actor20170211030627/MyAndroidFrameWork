package com.actor.sample.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.okhttputils.GetFileCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.okhttputils.PostFileCallback;
import com.actor.myandroidframework.utils.retrofit.BaseCallback2;
import com.actor.myandroidframework.utils.retrofit.RetrofitNetwork;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityNetWorkAndImageBinding;
import com.actor.sample.info.CheckUpdateInfo;
import com.actor.sample.info.GithubInfo;
import com.actor.sample.retrofit.api.GithubApi;
import com.actor.sample.utils.Global;
import com.bumptech.glide.Glide;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description: 主页->网络&图片
 * Author     : ldf
 * Date       : 2019-9-6 on 14:23
 */
public class NetWorkAndImageActivity extends BaseActivity<ActivityNetWorkAndImageBinding> {

    private ImageView iv;
    private ProgressBar progressBar;

    private boolean alreadyDownload = false;
    private String picPath;

    private Map<String, Object> params = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv = viewBinding.iv;
        progressBar = viewBinding.progressBar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        setTitle("主页->网络&图片");
        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .into(iv);
        iv.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition();
                }
            }
        });
    }

//    @OnClick({R.id.btn_get_okhttp, R.id.btn_post_body_okhttp, R.id.btn_get_retrofit,
//            R.id.btn_download, R.id.btn_select_pic, R.id.btn_upload})
    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_okhttp://MyOkHttpUtils方式获取数据
                getByOkHttpUtils();
                break;
            case R.id.btn_post_body_okhttp://MyOkHttpUtils方式通过body传递数据
                postBodyByOkHttpUtils();
                break;
            case R.id.btn_get_retrofit://Retrofit方式获取数据
                getByRetrofit();
                break;
            case R.id.btn_get_easy_http://EasyHttp方式获取数据
                getByEasyHttp();
                break;
            case R.id.btn_download://下载进度测试
                downloadApk();
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

    private void getByOkHttpUtils() {
        MyOkHttpUtils.get(Global.DOU_BAN_BOOK, null, new BaseCallback<String>(this) {
            @Override
            public void onOk(@NonNull String info, int requestId, boolean isRefresh) {
                ToasterUtils.success(info);
            }
        });
    }

    private void postBodyByOkHttpUtils() {
        params.clear();
        params.put("sign", 123);
        params.put("userId", "abc");
        params.put("token", 123);
        params.put("key", 0);
        MyOkHttpUtils.postFormBody("http://111.22.133.44:9001/abc/defg/v1/xxxxxx", null, params,
                new BaseCallback<String>(this) {
                    @Override
                    public void onOk(@NonNull String info, int id, boolean isRefresh) {
                        ToasterUtils.success(info);
                    }
                });
    }

    private void getByRetrofit() {
        RetrofitNetwork.getApi(GithubApi.class).get().enqueue(new BaseCallback2<GithubInfo>(this) {
            @Override
            public void onOk(Call<GithubInfo> call, Response<GithubInfo> response, int id, boolean isRefresh) {
                GithubInfo body = response.body();
                if (body != null) ToasterUtils.success(body.hub_url);
            }
        });
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

    private void downloadApk() {
        if (!alreadyDownload) {
            MyOkHttpUtils.getFile(Global.GRADLE_DOWNLOAD_URL, null, null,
                    new GetFileCallback(this, GetFileCallback.getFileNameFromUrl(Global.GRADLE_DOWNLOAD_URL)) {
                @Override
                public void onOk(@NonNull File info, int id, boolean isRefresh) {
                }

                @Override
                public void onError(int id, okhttp3.Call call, Exception e) {
                    super.onError(id, call, e);
                    ToasterUtils.error("下载错误: ".concat(e.getMessage()));
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    int parcent = (int) (progress/total * 100);
//                    LogUtils.error(String.valueOf(parcent));
                    progressBar.setProgress(parcent);
                }
            });
            alreadyDownload = true;
        }
    }

    /**
     * 上传单个图片/文件
     */
    protected void uploadFile(@NonNull String filePath) {
        MyOkHttpUtils.postFiles("http://111.22.133.44:9001/fileManage/upload", "filed", new File(filePath),
                null, null, new PostFileCallback<String>(this) {
                    @Override
                    public void onOk(@NonNull String info, int id, boolean isRefresh) {
                        ToasterUtils.success(info);
                    }
                });
    }
}
