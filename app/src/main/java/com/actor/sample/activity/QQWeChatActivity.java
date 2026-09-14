package com.actor.sample.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.AssetsUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.actor.qq_wechat.BaseUiListener;
import com.actor.qq_wechat.QQUtils;
import com.actor.qq_wechat.WXOpenCustomerServiceChatListener;
import com.actor.qq_wechat.WeChatUtils;
import com.actor.qq_wechat.WxLaunchMiniProgramListener;
import com.actor.qq_wechat.WxLoginListener;
import com.actor.qq_wechat.WxPayListener;
import com.actor.qq_wechat.WxShareListener;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityQqWeChatBinding;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PathUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Description: 主页->QQ微信登录/分享
 * Author     : ldf
 * Date       : 2020/3/13 on 12:11
 */
public class QQWeChatActivity extends BaseActivity<ActivityQqWeChatBinding> {

    private final WxShareListener shareListener = new WxShareListener() {
        @Override
        public void onShareSuccess(SendMessageToWX.Resp shareResp) {
            String json = GsonUtils.toJson(shareResp);
            LogUtils.errorFormat("分享成功! shareResp = %s", json);
            ToasterUtils.success("分享成功");
            viewBinding.tvResultWechat.setText(json);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->QQ微信登录/分享");
    }

    @Override
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_login_qq://登录
                QQUtils.login(this, "all", false, false, listener);
                break;
            case R.id.btn_login_qr://二维码登录
                QQUtils.login(this, "all", true, false, listener);
                break;
            case R.id.btn_login_account_password://账号密码登录
                QQUtils.login(this, "all", false, true, listener);
                break;
            case R.id.btn_login_server_side://Server-Side登录模式
                QQUtils.loginServerSide(this, "all", listener);
                break;
            case R.id.btn_get_user_info_qq://获取用户信息
                QQUtils.getUserInfo(new BaseUiListener() {
                    @Override
                    public void doComplete(@Nullable JSONObject response) {
                        viewBinding.tvResultQq.setText(String.valueOf(response));
                    }
                });
                break;
            case R.id.btn_share_img_text://分享图文
                QQUtils.shareToQQImgTxt(mActivity, "图文标题", null, "https://www.baidu.com",
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
                        .selectImage()
                        .setCompress(false)
                        .setSingleSelect(true)
                        .setShowCamera(true)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                LocalMedia localMedia = result.get(0);
                                PictureSelectorUtils.printLocalMedia(localMedia);
                                QQUtils.shareToQQImg(mActivity, localMedia.getAvailablePath(), "点我返回哟哟a", null, null,
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
                QQUtils.shareToQzone(mActivity, "标题呀", null, "https://www.baidu.com", null,
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
                String input = viewBinding.itilTargetQq.getText().toString();
                if (isNoEmpty(input)) {
                    int code = QQUtils.startIMAio(this, input);
                    LogUtils.error("错误码: " + code);

//                    int code = QQUtils.startIMAudio(this, getText(etTargetQq));//语音
//                    int code = QQUtils.startIMVideo(this, getText(etTargetQq));//视频
//                    int code = QQUtils.startMiniApp();//小程序
                }
                break;



            //下方是微信区
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
            case R.id.btn_login_wechat_qrcode://微信扫码登录
                WeChatUtils.loginQr("snsapi_userinfo", "test-----微信Qr登录", new Date().toString(), "signature", new OAuthListener() {
                    @Override
                    public void onAuthGotQrcode(String qrcodeImgPath, byte[] imgBuf) {
                        //auth之后返回的二维码接口
                        LogUtils.errorFormat("auth之后返回的二维码接口, qrcodeImgPath = %s, imgBuf = %s", qrcodeImgPath, imgBuf);
                        ToasterUtils.info("auth之后返回的二维码接口!");
                    }
                    @Override
                    public void onQrcodeScanned() {
                        LogUtils.error("用户扫描二维码之后");
                        ToasterUtils.info("用户扫描二维码之后!");
                    }
                    @Override
                    public void onAuthFinish(OAuthErrCode errCode, String authCode) {
                        LogUtils.errorFormat("二维码登录: 授权结果, errCode = %s, authCode = %s", errCode, authCode);
                        if (errCode == OAuthErrCode.WechatAuth_Err_OK) {
                            ToasterUtils.success("二维码登录: 授权成功!");
                        } else {
                            ToasterUtils.success("二维码登录: 授权失败!");
                        }
                    }
                });
                break;

            case R.id.btn_subscribe_message:                //订阅消息
                WeChatUtils.subscribeMessage(222, "templateID", "reserved");
                break;
            case R.id.btn_subscribe_mini_program_message:   //订阅小程序消息
                WeChatUtils.subscribeMiniProgramMsg("miniProgramAppId");
                break;

            case R.id.btn_launch_mini_program:              //拉起小程序
                WeChatUtils.launchMiniProgram("gh_d43f693ca31f", "?foo=bar", WXMiniProgramObject.MINIPROGRAM_TYPE_TEST, new WxLaunchMiniProgramListener() {
                    @Override
                    public void onLaunchMiniProgramSuccess(@NonNull WXLaunchMiniProgram.Resp launchMiniProResp) {
                        String json = GsonUtils.toJson(launchMiniProResp);
                        LogUtils.errorFormat("拉起小程序成功: launchMiniProResp = %s", json);
                        ToasterUtils.success("拉起小程序成功!");
                        viewBinding.tvResultWechat.setText(json);
                    }
                });
            case R.id.btn_open_customer_service_chat:       //拉起客服微信
                WeChatUtils.openCustomerServiceChat("gh_d43f693ca31f", "https://work.weixin.qq.com/kfid/kfcx1F35di", new WXOpenCustomerServiceChatListener() {
                    @Override
                    public void onOpenCustomerServiceChatSuccess(@NonNull WXOpenCustomerServiceChat.Resp resp) {
                        String json = GsonUtils.toJson(resp);
                        LogUtils.errorFormat("拉起客服微信成功: resp = %s", json);
                        ToasterUtils.success("拉起客服微信成功!");
                        viewBinding.tvResultWechat.setText(json);
                    }
                });

                break;
            case R.id.btn_share_text://分享文字
                WeChatUtils.sendReqText("这是分享的文字", SendMessageToWX.Req.WXSceneSession, shareListener);
                break;
            case R.id.btn_share_image://分享图片
                PictureSelectorUtils.create(this, null)
                        .selectImage()
                        .setCompress(false)
                        .setSingleSelect(true)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                LocalMedia localMedia = result.get(0);
                                PictureSelectorUtils.printLocalMedia(localMedia);
                                WeChatUtils.sendReqImage(localMedia.getAvailablePath(), null, SendMessageToWX.Req.WXSceneSession, shareListener);
                            }
                            @Override
                            public void onCancel() {
                            }
                        });
                break;
            case R.id.btn_share_video_url://分享网络视频Url
                Bitmap coverVideo = ImageUtils.getBitmap(R.drawable.logo);
                WeChatUtils.sendReqVideo("http://v.youku.com/v_show/id_XMzI0MzA3NjI1Ng==.html",
                        "标题啊", "视频的描述啊", coverVideo, SendMessageToWX.Req.WXSceneSession, shareListener
                );
                coverVideo.recycle();
                break;
            case R.id.btn_share_url://分享网页链接url
                Bitmap coverWeb = ImageUtils.getBitmap(R.drawable.logo);
                WeChatUtils.sendReqWebpage("https://www.baidu.com", "视频标题啊", "视频的描述啊",
                        coverWeb, SendMessageToWX.Req.WXSceneSession, shareListener);
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
                        SendMessageToWX.Req.WXSceneSession,
                        shareListener);
                coverMiniprogram.recycle();
                break;
            case R.id.btn_share_music://分享音乐
                Bitmap coverMusic = ImageUtils.getBitmap(R.drawable.logo);
                WeChatUtils.sendReqMusic(
                        "https://music.163.com/song/media/outer/url?id=1646740.mp3",
                        "音乐标题", "音乐描述", coverMusic, SendMessageToWX.Req.WXSceneSession, shareListener);
                coverMusic.recycle();
                break;
            case R.id.btn_share_file://分享文件到微信
                String path = PathUtils.getInternalAppNoBackupFilesPath();
                String filePath = AssetsUtils.copyFile2Dir(false, "fonts/Sofia.otf", path);
                LogUtils.errorFormat("filePath=%b", filePath);
                File file = new File(filePath);
                WeChatUtils.sendReqFile(file, file.getName(), SendMessageToWX.Req.WXSceneSession, shareListener);
                break;
            case R.id.btn_share_file_by_intent://通过Intent分享文件到微信
                String path2 = PathUtils.getInternalAppNoBackupFilesPath();
                String filePath2 = AssetsUtils.copyFile2Dir(false, "fonts/Sofia.otf", path2);
                File file2 = new File(filePath2);
                boolean jump = WeChatUtils.sendReqFileByIntent(this, file2);
                LogUtils.errorFormat("filePath2=%b, jump = %s", filePath2, jump);
                break;
            case R.id.btn_pay_wechat://微信支付
                if (WeChatUtils.isWXAppInstalled()) {
                    //使用服务器返回的以下几个参数↓
                    WeChatUtils.pay("partnerId", "prepayId", "nonceStr", "timeStamp", "sign", null, new WxPayListener() {
                        @Override
                        public void onPaySuccess(@NonNull PayResp payResp) {
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
            viewBinding.tvResultQq.setText(json);
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
