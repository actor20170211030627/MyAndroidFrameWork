package com.ly.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.utils.album.AlbumUtils;
import com.actor.myandroidframework.utils.tencent.BaseUiListener;
import com.actor.myandroidframework.utils.tencent.QQUtils;
import com.ly.sample.R;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->第三方登录/分享
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2020/3/13 on 12:11
 */
public class ThirdActivity extends BaseActivity {

    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.et_target_qq)
    TextView etTargetQq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        ButterKnife.bind(this);
        setTitle("主页->第三方登录/分享");

        //在Application中设置appId, 一般是一串数字
        QQUtils.setAppId("222222");
    }

    @OnClick({R.id.btn_login, R.id.btn_get_user_info, R.id.btn_share_img, R.id.btn_logout,
            R.id.btn_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login://登录
                QQUtils.login(this, "all", true, listener);
                break;
            case R.id.btn_get_user_info://获取用户信息
                QQUtils.getUserInfo(new BaseUiListener() {
                    @Override
                    public void doComplete(@Nullable JSONObject response) {
                        tvResult.setText(String.valueOf(response));
                    }
                });
                break;
            case R.id.btn_share_img://分享图片
                AlbumUtils.selectImage(this, false, new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        QQUtils.shareToQQImg(activity, result.get(0).getPath(), "安卓框架我的", null,
                                new BaseUiListener() {
                            @Override
                            public void doComplete(@Nullable JSONObject response) {
                                logError(response);
                            }
                        });
                        //还有其它分享方式
//                        QQUtils.shareToQQApp();
//                        QQUtils.shareToQQAudio();
//                        QQUtils.shareToQQImgTxt();
                    }
                });
                break;
            case R.id.btn_logout://退出
                QQUtils.logout();
                break;
            case R.id.btn_chat://聊天
                if (isNoEmpty(etTargetQq)) {
                    int code = QQUtils.startIMAio(this, getText(etTargetQq));
                    logError("错误码: " + code);

//                    int code = QQUtils.startIMAudio(this, getText(etTargetQq));//语音
//                    int code = QQUtils.startIMVideo(this, getText(etTargetQq));//视频
//                    int code = QQUtils.startMiniApp();//小程序
                }
                break;
        }
    }

    private BaseUiListener listener =new BaseUiListener() {

        @Override
        public void doComplete(@Nullable JSONObject response) {
            QQUtils.initSessionCache(response);
            tvResult.setText(String.valueOf(response));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        logError("-->onActivityResult " + requestCode + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
