package com.ly.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.actor.myandroidframework.utils.retrofit.BaseCallback2;
import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.ly.sample.MyBottomSheetDialogFragment;
import com.ly.sample.R;
import com.ly.sample.info.GithubInfo;
import com.ly.sample.retrofit.NetWork;
import com.ly.sample.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends ActorBaseActivity {

    @BindView(R.id.iv)
    ImageView iv;

    private MyBottomSheetDialogFragment bottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Glide.with(this).load(Global.girl).into(iv);
        bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
        bottomSheetDialogFragment.setPeekHeight(ConvertUtils.dp2px(100));//首次弹出高度, 可不设置
//        bottomSheetDialogFragment.setMaxHeight(ConvertUtils.dp2px(300));//最大弹出高度, 可不设置
        bottomSheetDialogFragment.setDimAmount(0.3F);//设置背景昏暗度
    }

    @OnClick({R.id.btn_get_api, R.id.btn_bottom_sheet_dialog_fragment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_api:
                getGithubApi();
                break;
            case R.id.btn_bottom_sheet_dialog_fragment:
                bottomSheetDialogFragment.show(getSupportFragmentManager());
                break;
        }
    }

    private void getGithubApi() {
        showLoadingDialog();
        NetWork.getGithubApi().get().enqueue(new BaseCallback2<GithubInfo>() {
            @Override
            public void onOk(Call<GithubInfo> call, Response<GithubInfo> response) {
                dismissLoadingDialog();
                GithubInfo body = response.body();
                if (body != null) {
                    toast(body.hub_url);
                }
            }

            @Override
            public void onError(Call<GithubInfo> call, Throwable t) {
                super.onError(call, t);
                dismissLoadingDialog();
            }
        });
    }
}
