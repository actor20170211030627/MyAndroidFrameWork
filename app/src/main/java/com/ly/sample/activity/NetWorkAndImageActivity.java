package com.ly.sample.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.actor.myandroidframework.utils.MyOkhttpUtils.BaseCallback;
import com.actor.myandroidframework.utils.MyOkhttpUtils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.retrofit.BaseCallback2;
import com.actor.myandroidframework.utils.retrofit.RetrofitNetwork;
import com.bumptech.glide.Glide;
import com.ly.sample.R;
import com.ly.sample.info.GithubInfo;
import com.ly.sample.retrofit.NetWork;
import com.ly.sample.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.body.ProgressInfo;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description: 主页->网络&图片
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-9-6 on 14:23
 */
public class NetWorkAndImageActivity extends BaseActivity {

    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private boolean alreadyDownload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_and_image);
        ButterKnife.bind(this);

        setTitle("主页->网络&图片");
        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .into(iv);

        RetrofitNetwork.addOnDownloadListener(Global.PICPICK_DOWNLOAD_URL, new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                logError(progressInfo.getPercent());
                progressBar.setProgress(progressInfo.getPercent());
            }

            @Override
            public void onError(long id, Exception e) {
                toast("下载错误: ".concat(e.getMessage()));
            }
        });
    }

    @OnClick({R.id.btn_get_okhttp, R.id.btn_get_retrofit, R.id.btn_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_okhttp:
                getByOkHttpUtils();
                break;
            case R.id.btn_get_retrofit:
                getByRetrofit();
                break;
            case R.id.btn_download:
                downloadApk();
                break;
        }
    }

    private void getByOkHttpUtils() {
        showLoadingDialog();
        MyOkHttpUtils.get(Global.BASE_URL, null, new BaseCallback<GithubInfo>(this) {
            @Override
            public void onOk(@NonNull GithubInfo info, int id) {//info != null(不会为空, 已做判断)
                dismissLoadingDialog();
                toast(info.hub_url);
            }

            //可以不用重写onError方法
            @Override
            public void onError(int id, okhttp3.Call call, Exception e) {
                super.onError(id, call, e);
                dismissLoadingDialog();
            }
        });
    }

    private void getByRetrofit() {
        showLoadingDialog();
        putCall(NetWork.getGithubApi().get()).enqueue(new BaseCallback2<GithubInfo>() {
            @Override
            public void onOk(Call<GithubInfo> call, Response<GithubInfo> response) {
                dismissLoadingDialog();
                GithubInfo body = response.body();
                if (body != null) toast(body.hub_url);
            }

            //可以不用重写onError方法
            @Override
            public void onError(Call<GithubInfo> call, Throwable t) {
                super.onError(call, t);
                dismissLoadingDialog();
            }
        });
    }

    private void downloadApk() {
        if (!alreadyDownload) {
            MyOkHttpUtils.getFile(Global.PICPICK_DOWNLOAD_URL, null);
            alreadyDownload = true;
        }
    }
}
