package com.actor.sample.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.actor.map.baidu.BaiduGeoCoderUtils;
import com.actor.map.baidu.LngLatInfo;
import com.actor.myandroidframework.utils.album.AlbumUtils;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.okhttputils.GetFileCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.okhttputils.PostFileCallback;
import com.actor.myandroidframework.utils.retrofit.BaseCallback2;
import com.actor.myandroidframework.utils.retrofit.RetrofitNetwork;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityNetWorkAndImageBinding;
import com.actor.sample.info.GithubInfo;
import com.actor.sample.retrofit.api.GithubApi;
import com.actor.sample.utils.Global;
import com.bumptech.glide.Glide;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;

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
            case R.id.btn_download://下载进度测试
                downloadApk();
                break;
            case R.id.btn_select_pic://上传文件(可上传中文名文件)
                AlbumUtils.selectImage(this, false, new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        picPath = result.get(0).getPath();
                    }
                });
                break;
            case R.id.btn_upload:
                if (picPath != null) {
                    uploadFile(picPath);
                } else showToast("请选择图片");
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
        BaiduGeoCoderUtils.getLngLatByNet("新疆维吾尔自治区乌鲁木齐市沙依巴克区奇台路676号", new BaseCallback<LngLatInfo>(this) {
            @Override
            public void onOk(@NonNull LngLatInfo info, int id, boolean isRefresh) {
                if (info.status == 0) {
                    LngLatInfo.ResultBean result = info.result;
                    if (result != null) {
                        LngLatInfo.ResultBean.LocationBean location = result.location;
                        if (location != null) {
                            showToastFormat("lng=%f, lat=%f", location.lng, location.lat);
                        }
                    }
                } else showToast(info.message);
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
                        showToast(info);
                    }
                });
    }

    private void getByRetrofit() {
        RetrofitNetwork.getApi(GithubApi.class).get().enqueue(new BaseCallback2<GithubInfo>(this) {
            @Override
            public void onOk(Call<GithubInfo> call, Response<GithubInfo> response, int id, boolean isRefresh) {
                GithubInfo body = response.body();
                if (body != null) showToast(body.hub_url);
            }
        });
    }

    private void downloadApk() {
        if (!alreadyDownload) {
            MyOkHttpUtils.getFile(Global.PICPICK_DOWNLOAD_URL, null, null,
                    new GetFileCallback(this, GetFileCallback.getFileNameFromUrl(Global.PICPICK_DOWNLOAD_URL)) {
                @Override
                public void onOk(@NonNull File info, int id, boolean isRefresh) {
                }

                @Override
                public void onError(int id, okhttp3.Call call, Exception e) {
                    super.onError(id, call, e);
                    showToast("下载错误: ".concat(e.getMessage()));
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
                        showToast(info);
                    }
                });
    }
}
