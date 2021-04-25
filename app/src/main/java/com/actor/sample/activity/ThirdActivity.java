package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.EventBusEvent;
import com.actor.myandroidframework.utils.album.AlbumUtils;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.tencent.BaseUiListener;
import com.actor.myandroidframework.utils.tencent.QQUtils;
import com.actor.myandroidframework.utils.tencent.WeChatUtils;
import com.actor.sample.R;
import com.actor.sample.wxapi.WXEntryActivity;
import com.actor.sample.wxapi.WXPayEntryActivity;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.tauth.Tencent;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.AlbumFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->第三方登录/分享
 * Author     : 李大发
 * Date       : 2020/3/13 on 12:11
 */
public class ThirdActivity extends BaseActivity {

    @BindView(R.id.tv_result_qq)
    TextView tvResultQq;
    @BindView(R.id.et_target_qq)
    TextView etTargetQq;

    private boolean isQrCode = false;
    private String accessToken, openId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setTitle("主页->第三方登录/分享");

        //在Application中设置appId, 一般是一串数字(我这儿设置的appid是QQ2786985624申请的)
        QQUtils.setAppId("101890804");//222222

        //在Application中设置appId
        WeChatUtils.setAppId("wx88888888");
    }

    @OnClick({R.id.btn_login_qq, R.id.btn_login_qr_account_password, R.id.btn_get_user_info_qq,
            R.id.btn_share_img, R.id.btn_logout, R.id.btn_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login_qq://登录
                QQUtils.login(this, "all", true, listener);
                break;
            case R.id.btn_login_qr_account_password://二维码登录or账号密码登录
                isQrCode = !isQrCode;
                QQUtils.loginQrCode$AccountPassword(this, "all", isQrCode, listener);
                break;
            case R.id.btn_get_user_info_qq://获取用户信息
                QQUtils.getUserInfo(new BaseUiListener() {
                    @Override
                    public void doComplete(@Nullable JSONObject response) {
                        tvResultQq.setText(String.valueOf(response));
                    }
                });
                break;
            case R.id.btn_share_img://分享图片
                AlbumUtils.selectImage(this, false, new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        QQUtils.shareToQQImg(activity, result.get(0).getPath(), "点我返回哟哟a", null,
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


            //下方是微信区
            case R.id.btn_login_wechat://微信登录
                if (WeChatUtils.isWXAppInstalled()) {
                    WeChatUtils.login("snsapi_userinfo", "test-----微信登录");
                } else toast("您手机尚未安装微信，请安装后再登录");
                break;
            case R.id.btn_share_text://分享文字(还可以分享其它)
                //分享返回需要自己在 WXEntryActivity 中添加逻辑
                WeChatUtils.sendReqText("这是分享的文字", SendMessageToWX.Req.WXSceneSession);
//                WeChatUtils.sendReqImage();
                //...
                break;
            case R.id.btn_get_user_info_wechat://获取用户信息
                //没试过...
                if (accessToken == null) {
                    toast("请先微信登录");
                    return;
                }
                //这个也应该后台调用后一起返回
                WeChatUtils.getUserInfo(accessToken, openId, new BaseCallback<Object>(this) {
                    @Override
                    public void onOk(@NonNull Object info, int id, boolean isRefresh) {
                    }
                });
                break;
        }
    }

    //QQ登录回调
    private BaseUiListener listener =new BaseUiListener() {

        @Override
        public void doComplete(@Nullable JSONObject response) {
            QQUtils.initSessionCache(response);
            tvResultQq.setText(String.valueOf(response));
        }
    };

    //处理QQ返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        logError("-->onActivityResult " + requestCode + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Eventbus微信登录/支付回调
     @Subscribe(threadMode = ThreadMode.MAIN)
     public void onWxLoginResult(EventBusEvent eventBusEvent) {
         if (eventBusEvent == null) return;
         switch (eventBusEvent.code) {
             case WXEntryActivity.MSG_EVT_WX_LOGIN://登录
                 toast("登录成功!");
                 logError(eventBusEvent);
                 String code = eventBusEvent.msg;
                 /**
                  * 调用后台接口获取token
                  * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN
                  */
                 //把code传到服务器, 从服务器返回 accessToken, openId, unionid 等
                 accessToken = "from service";
                 openId = "from service";
                 break;
             case WXPayEntryActivity.MSG_EVT_WX_PAY_RESULT://支付
                 toast("支付成功!");
                 logError(eventBusEvent);
                 break;
         }
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
