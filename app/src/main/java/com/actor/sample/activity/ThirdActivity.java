package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.EventBusEvent;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.actor.qq_wechat.BaseUiListener;
import com.actor.qq_wechat.QQUtils;
import com.actor.qq_wechat.WeChatUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityThirdBinding;
import com.actor.sample.wxapi.WXEntryActivity;
import com.actor.sample.wxapi.WXPayEntryActivity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.tauth.Tencent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Description: 主页->第三方登录/分享
 * Author     : ldf
 * Date       : 2020/3/13 on 12:11
 */
public class ThirdActivity extends BaseActivity<ActivityThirdBinding> {

    private TextView tvResultQq;
    private EditText etTargetQq;

    private boolean isQrCode = false;
    private String accessToken, openId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->第三方登录/分享");
        tvResultQq = viewBinding.tvResultQq;
        etTargetQq = viewBinding.etTargetQq;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login_qq://登录
                QQUtils.login(this, "all", true, listener);
                break;
            case R.id.btn_login_qr_account_password://二维码登录or账号密码登录
                isQrCode = !isQrCode;
                QQUtils.loginQrCode$AccountPassword(this, "all", isQrCode, listener);
                break;
            case R.id.btn_login_server_side://Server-Side登录模式
                QQUtils.loginServerSide(this, "all", listener);
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
                PictureSelectorUtils.create(this, null)
                        .selectImage(false)
                        .setSingleSelect(true)
                        .setShowCamera(true)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                QQUtils.shareToQQImg(activity, result.get(0).getPath(), "点我返回哟哟a", null,
                                        new BaseUiListener() {
                                            @Override
                                            public void doComplete(@Nullable JSONObject response) {
                                                LogUtils.error(response);
                                            }
                                        });
                                //还有其它分享方式
//                                QQUtils.shareToQQApp();
//                                QQUtils.shareToQQAudio();
//                                QQUtils.shareToQQImgTxt();
                            }
                            @Override
                            public void onCancel() {
                            }
                        });
                break;
            case R.id.btn_logout://退出
                QQUtils.logout();
                break;
            case R.id.btn_chat://聊天
                if (isNoEmpty(etTargetQq)) {
                    int code = QQUtils.startIMAio(this, getText(etTargetQq));
                    LogUtils.error("错误码: " + code);

//                    int code = QQUtils.startIMAudio(this, getText(etTargetQq));//语音
//                    int code = QQUtils.startIMVideo(this, getText(etTargetQq));//视频
//                    int code = QQUtils.startMiniApp();//小程序
                }
                break;


            //下方是微信区
            case R.id.btn_login_wechat://微信登录
                if (WeChatUtils.isWXAppInstalled()) {
                    WeChatUtils.login("snsapi_userinfo", "test-----微信登录");
                } else showToast("您手机尚未安装微信，请安装后再登录");
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
                    showToast("请先微信登录");
                    return;
                }
                //这个也应该后台调用后一起返回
                WeChatUtils.getUserInfo(accessToken, openId, new BaseCallback<Object>(this) {
                    @Override
                    public void onOk(@NonNull Object info, int id, boolean isRefresh) {
                    }
                });
                break;
            default:
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
        LogUtils.errorFormat("requestCode=%d, resultCode=%d", requestCode, resultCode);
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Eventbus微信登录/支付回调
     @Subscribe(threadMode = ThreadMode.MAIN)
     public void onWxLoginResult(EventBusEvent<Object> eventBusEvent) {
         if (eventBusEvent == null) return;
         switch (eventBusEvent.code) {
             case WXEntryActivity.MSG_EVT_WX_LOGIN://登录
                 showToast("登录成功!");
                 LogUtils.error(eventBusEvent);
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
                 showToast("支付成功!");
                 LogUtils.error(eventBusEvent);
                 break;
             default:
                 break;
         }
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
