package com.actor.sample.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.actor.myandroidframework.utils.EventBusEvent;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.qq_wechat.WeChatUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Description: https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
 * 1.你的程序需要接收微信发送的请求，或者接收发送到微信请求的响应结果，需要下面 3 步操作：
 * a. 在你的包名相应目录下新建一个 wxapi 目录，并在该 wxapi 目录下新增一个 WXEntryActivity 类，该类继承自 Activity
 *    并在 manifest 文件里面加上exported、taskAffinity及launchMode属性，
 *    其中exported设置为true，taskAffinity设置为你的包名，launchMode设置为singleTask
 *     <!--微信登录, 如果app没有使用 登录等 这些功能, 请别添加-->
 *     <activity
 *         android:name=".wxapi.WXEntryActivity"
 *         android:exported="true"
 *         android:launchMode="singleTask"
 *         android:taskAffinity="${applicationId}" />
 *     注意: 已在 MyAndroidFrameWork 的 AndroidManifest.xml中添加, 不要再在项目中添加到清单文件中!!!
 *
 * b. 实现 IWXAPIEventHandler 接口，微信发送的请求将回调到 onReq 方法，
 *    发送到微信请求的响应结果将回调到 onResp 方法
 * c. 在 WXEntryActivity 中将接收到的 intent 及实现了 IWXAPIEventHandler 接口的对象传递给 IWXAPI 接口的 handleIntent 方法
 *
 * 注意: 以上3个步骤都已完成!!!
 *
 * 2.回调
 * //Eventbus微信登录/支付回调
 * @Subscribe(threadMode = ThreadMode.MAIN)
 * public void onWxLoginResult(EventBusEvent<Object> eventBusEvent) {
 *     if (eventBusEvent == null) return;
 *     switch (eventBusEvent.code) {
 *         case WXEntryActivity.MSG_EVT_WX_LOGIN:
 *             showToast("登录成功!");
 *             LogUtils.error(eventBusEvent);
 *             break;
 *     }
 * }
 *
 * 微信分享/登录 返回页面
 * @author     : ldf
 * date       : 2019/4/1 on 14:57
 */
public class WXEntryActivity extends ActorBaseActivity implements IWXAPIEventHandler {

    private IWXAPI iwxapi = WeChatUtils.getIWXAPI();
    public static final int MSG_EVT_WX_LOGIN = 7067080;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，
        // 如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，
        // 避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        boolean result =  iwxapi.handleIntent(getIntent(), this);
        if(!result){
            LogUtils.error("微信登录参数不合法，未被SDK处理，退出");
            onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq baseReq) {
        String json = baseReq == null ? "null" : GsonUtils.toJson(baseReq);
        LogUtils.errorFormat("onReq微信发送的请求: baseReq = %s", json);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        String json = baseResp == null ? "null" : GsonUtils.toJson(baseResp);
        LogUtils.errorFormat("onResp微信响应: baseResp = %s", json);
        if (baseResp == null) return;
        switch (baseResp.getType()) {
            case ConstantsAPI.COMMAND_SENDAUTH://微信登录
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        SendAuth.Resp authResp = (SendAuth.Resp) baseResp;
                        //用户换取access_token的code，仅在ErrCode为0时有效
                        //把code传到服务器, 从服务器返回 accessToken, openId, unionid 等(用于获取userinfo)
                        final String code = authResp.code;
                        /**
                         * 调用后台接口获取token
                         * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN
                         */

                        /**
                         * 第三方程序发送时用来标识其请求的唯一性的标志，由第三方程序调用sendReq时传入，
                         * 由微信终端回传，state字符串长度不能超过1K
                         */
                        String state = authResp.state;
                        String lang = authResp.lang;//微信客户端当前语言
                        String country = authResp.country;//微信用户当前国家信息

                        finish();
                        EventBus.getDefault().post(new EventBusEvent<>(MSG_EVT_WX_LOGIN, code));
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        finish();
                        showToast("用户取消登录!");
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        finish();
                        showToast("用户拒绝登录!");
                        break;
                    default:
                        finish();
                        showToast("登录失败, 错误码: " + baseResp.errCode);
                        break;
                }
                break;
            default:
                finish();
                LogUtils.error("baseResp.getType():" + baseResp.getType());
                break;
        }
    }
}
