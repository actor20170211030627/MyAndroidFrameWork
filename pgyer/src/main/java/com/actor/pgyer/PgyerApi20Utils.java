package com.actor.pgyer;

import androidx.lifecycle.LifecycleOwner;

import com.actor.pgyer.bean.AppCheckBean;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;

/**
 * description: <a href="https://www.pgyer.com/doc/view/api", target="_blank">蒲公英Api 2.0</a>
 *
 * @author : ldf
 * date       : 2026/9/26 on 23
 * @version 1.0
 */
public class PgyerApi20Utils {

    //API Key，用来识别 API 调用者的身份。如无特别说明，每个接口中都需要包含此参数。对同一个蒲公英注册用户来说，这个值是固定的。
    protected static String apiKey;
    protected static String appKey;

    /**
     * 初始化
     * @param apiKey 蒲公英头像 -> API信息: <a href="https://www.pgyer.com/account/api" target="_blank">API 信息页面</a>
     * @param appKey 蒲公英App详情页, 包名后面
     */
    public static void init(String apiKey, String appKey) {
        PgyerApi20Utils.apiKey = apiKey;
        PgyerApi20Utils.appKey = appKey;
    }

    /**
     * 检测 App 是否有更新
     * @param lifecycleOwner 请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                       如需传入其他对象请参考以下两个类
     *                       {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                       {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     * @param versionName    当前版本名称, 例: 1.0
     * @param tag            设置请求的标记（可用于 {@link EasyHttp#cancelByTag(String)}）
     */
    public static void appCheck(LifecycleOwner lifecycleOwner, String versionName, Object tag, OnHttpListener<AppCheckBean.AppCheckResponse> listener) {
        EasyHttp.post(lifecycleOwner).tag(tag).api(new AppCheckBean(apiKey, appKey, versionName)).request(listener);
    }
}
