package com.actor.sample.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.actor.qq_wechat.BaseUiListener;
import com.actor.qq_wechat.QQUtils;
import com.actor.qq_wechat.WeChatUtils;
import com.actor.qq_wechat.WxLoginListener;
import com.actor.qq_wechat.WxPayListener;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityThirdBinding;
import com.blankj.utilcode.util.ImageUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.tauth.Tencent;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->第三方登录/分享");
        tvResultQq = viewBinding.tvResultQq;
        etTargetQq = viewBinding.etTargetQq;
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
            case R.id.btn_share_img_text://分享图文
                QQUtils.shareToQQImgTxt(activity, "图文标题", null, "https://www.baidu.com",
                        null, "返回1", null, null, new BaseUiListener() {
                    @Override
                    public void doComplete(@Nullable JSONObject response) {
                        LogUtils.error(response);
                        ToasterUtils.success("分享图文成功!");
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
                                LocalMedia localMedia = result.get(0);
                                PictureSelectorUtils.printLocalMedia(localMedia);
                                QQUtils.shareToQQImg(activity, localMedia.getRealPath(), "点我返回哟哟a", null, null,
                                        new BaseUiListener() {
                                            @Override
                                            public void doComplete(@Nullable JSONObject response) {
                                                LogUtils.error(response);
                                                ToasterUtils.success("分享图片成功!");
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
            case R.id.btn_share_img2_qzone://分享图文到QQ空间
                QQUtils.shareToQzone(activity, "标题呀", null, "https://www.baidu.com", null,
                        new BaseUiListener() {
                            @Override
                            public void doComplete(@Nullable JSONObject response) {
                                LogUtils.error(response);
                                ToasterUtils.success("分享图文到QQ空间成功!");
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
            case R.id.btn_pay_wechat://微信支付
                if (WeChatUtils.isWXAppInstalled()) {
                    //使用服务器返回的以下几个参数↓
                    WeChatUtils.pay("partnerId", "prepayId", "nonceStr", "timeStamp", "sign", new WxPayListener() {
                        @Override
                        public void onPaySuccess(@NonNull BaseResp baseResp) {
                            ToasterUtils.success("支付成功");
                        }
                        @Override
                        public void onPayError(@NonNull BaseResp baseResp) {
                            //支付失败, 使用默认提示. (也可注释下面这句, 使用自定义提示)
                            WxPayListener.super.onPayError(baseResp);
                        }
                    });
                } else {
                    ToasterUtils.warning("您手机尚未安装微信");
                }
                break;
            case R.id.btn_login_wechat://微信登录
                if (WeChatUtils.isWXAppInstalled()) {
                    WeChatUtils.login("snsapi_userinfo", "test-----微信登录", new WxLoginListener() {
                        @Override
                        public void onLoginSuccess(@NonNull SendAuth.Resp authResp) {
                            ToasterUtils.success("登录成功!");
                            String code = authResp.code;

                            //然后把code传给后台服务器, 从服务器返回 accessToken, openId, unionid 等
                            String accessToken = "from service";
                            String openId = "from service";
                        }
                        @Override
                        public void onLoginError(@NonNull BaseResp authResp) {
                            //登录失败, 使用默认提示. (也可注释下面这句, 使用自定义提示)
                            WxLoginListener.super.onLoginError(authResp);
                        }
                    });
                } else {
                    ToasterUtils.warning("您手机尚未安装微信，请安装后再登录");
                }
                break;
            case R.id.btn_share_text://分享文字
                WeChatUtils.sendReqText("这是分享的文字", SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.btn_share_image://分享图片
                PictureSelectorUtils.create(this, null).selectImage(false)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                LocalMedia localMedia = result.get(0);
                                PictureSelectorUtils.printLocalMedia(localMedia);
                                WeChatUtils.sendReqImage(result.get(0).getRealPath(), null, SendMessageToWX.Req.WXSceneSession);
                            }
                            @Override
                            public void onCancel() {
                            }
                        });
                break;
            case R.id.btn_share_video_url://分享网络视频Url
                Bitmap coverVideo = ImageUtils.getBitmap(R.drawable.logo);
                WeChatUtils.sendReqVideo("http://v.youku.com/v_show/id_XMzI0MzA3NjI1Ng==.html",
                        "标题啊", "视频的描述啊", coverVideo, SendMessageToWX.Req.WXSceneSession
                );
                coverVideo.recycle();
                break;
            case R.id.btn_share_url://分享网页链接url
                Bitmap coverWeb = ImageUtils.getBitmap(R.drawable.logo);
                WeChatUtils.sendReqWebpage("https://www.baidu.com", "视频标题啊", "视频的描述啊",
                        coverWeb, SendMessageToWX.Req.WXSceneSession);
                coverWeb.recycle();
                break;
            case R.id.btn_share_miniprogram://分享小程序
                Bitmap coverMiniprogram = ImageUtils.getBitmap(R.drawable.logo);
                WeChatUtils.sendReqMiniProgram(
                        "https://www.baidu.com",    //可随意填1个非空字符串都行
                       "gh_07933c23d664",
                        null,                       // "/pages/down/index?user_id=123",
                        false,
                        WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE,
                        "小程序标题啊",
                        "小程序描述啊",
                        coverMiniprogram,
                        SendMessageToWX.Req.WXSceneSession);
                coverMiniprogram.recycle();
                break;
            case R.id.btn_share_music://分享音乐
                Bitmap coverMusic = ImageUtils.getBitmap(R.drawable.logo);
                WeChatUtils.sendReqMusic(
                        "https://music.163.com/song/media/outer/url?id=1646740.mp3",
                        "音乐标题", "音乐描述", coverMusic, SendMessageToWX.Req.WXSceneSession);
                coverMusic.recycle();
                break;
            case R.id.btn_pay_offline://打开离线支付
                WeChatUtils.payOffline();
                break;
            default:
                break;
        }
    }

    //QQ登录回调
    private final BaseUiListener listener = new BaseUiListener() {

        @Override
        public void doComplete(@Nullable JSONObject response) {
            QQUtils.initSessionCache(response);
            String json = String.valueOf(response);
            LogUtils.error(json);
            tvResultQq.setText(json);
        }
    };

    //处理QQ返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.errorFormat("requestCode=%d, resultCode=%d", requestCode, resultCode);
        Tencent.onActivityResultData(requestCode, resultCode, data, null);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
