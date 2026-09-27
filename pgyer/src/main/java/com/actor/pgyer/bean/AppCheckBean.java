package com.actor.pgyer.bean;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestHost;

/**
 * description: 描述
 *
 * @author : ldf
 * date       : 2026/9/26 on 23
 * @version 1.0
 */
@Keep
public class AppCheckBean implements IRequestApi, IRequestHost {

    //参数名				类型	必填	说明
    //_api_key			String	是		API Key，请见 鉴权说明
    //appKey			String	是		见 appKey
    //buildVersion		String	否		使用 App 本身的 Build 版本号。Android 对应字段为 versionname，iOS 对应字段为 version
    //buildBuildVersion	Integer	否		使用蒲公英生成的自增 Build 版本号
    //channelKey		String	否		渠道 Key

    public String _api_key;
    public String appKey;
    public String buildVersion;
    public Integer buildBuildVersion;
    public String channelKey;

    public AppCheckBean(String apiKey, String appKey, String versionName) {
        this._api_key = apiKey;
        this.appKey = appKey;
        this.buildVersion = versionName;
    }

    @NonNull
    @Override
    public String getApi() {
        return "/apiv2/app/check";
    }

    @NonNull
    @Override
    public String getHost() {
        return "https://www.pgyer.com";
    }

    @Keep
    public static final class AppCheckResponse extends BaseBean<AppCheckResponse.DataBean> {
        /**
         * code : 0
         * message :
         * data : {"buildBuildVersion":"6","forceUpdateVersion":"","forceUpdateVersionNo":"","needForceUpdate":false,"downloadURL":"https://www.pgyer.com/app/installUpdate/4bafbd62b0c007a95017b4587084d2d9?sig=o0DFRxhTkACYBijzqQQTw6jgtbnOdxCnHL5RIkXgMpdSxUyf8rIXWk6b9UQnZssc&forceHttps=","buildHaveNewVersion":true,"buildVersionNo":"2026092501","buildVersion":"1.5","buildDescription":"中石油司机题库","buildUpdateDescription":"新增悬浮窗学习功能","buildShortcutUrl":"https://www.pgyer.com/c98VC1Cb","appURl":"https://www.pgyer.com/4bafbd62b0c007a95017b4587084d2d9","appKey":"a96adf3c304b727261325f6b60dbd865","buildKey":"4bafbd62b0c007a95017b4587084d2d9","buildName":"中石油题库","buildIcon":"https://cdn-app-icon2.pgyer.com/a/2/2/4/9/a22492919e4154b7b55f154725190e2a?x-oss-process=image/resize,m_lfit,h_120,w_120/format,jpg","buildFileKey":"7e124515555a8a2ac7cdbedda54a5527.apk","buildFileSize":"3148231"}
         */

//        public int code;
//        public String   message;
//        public DataBean data;
        @Keep
        public static class DataBean {
            /**
             * buildBuildVersion : 6
             * forceUpdateVersion :
             * forceUpdateVersionNo :
             * needForceUpdate : false
             * downloadURL : https://www.pgyer.com/app/installUpdate/4bafbd62b0c007a95017b4587084d2d9?sig=o0DFRxhTkACYBijzqQQTw6jgtbnOdxCnHL5RIkXgMpdSxUyf8rIXWk6b9UQnZssc&forceHttps=
             * buildHaveNewVersion : true
             * buildVersionNo : 2026092501
             * buildVersion : 1.5
             * buildDescription : 中石油司机题库
             * buildUpdateDescription : 新增悬浮窗学习功能
             * buildShortcutUrl : https://www.pgyer.com/c98VC1Cb
             * appURl : https://www.pgyer.com/4bafbd62b0c007a95017b4587084d2d9
             * appKey : a96adf3c304b727261325f6b60dbd865
             * buildKey : 4bafbd62b0c007a95017b4587084d2d9
             * buildName : 中石油题库
             * buildIcon : https://cdn-app-icon2.pgyer.com/a/2/2/4/9/a22492919e4154b7b55f154725190e2a?x-oss-process=image/resize,m_lfit,h_120,w_120/format,jpg
             * buildFileKey : 7e124515555a8a2ac7cdbedda54a5527.apk
             * buildFileSize : 3148231
             */

            public int buildBuildVersion;
            public String  forceUpdateVersion;
            public String  forceUpdateVersionNo;
            public boolean needForceUpdate;
            public String  downloadURL;
            public boolean buildHaveNewVersion;
            public int  buildVersionNo;
            public String  buildVersion;
            public String  buildDescription;
            public String  buildUpdateDescription;
            public String  buildShortcutUrl;
            public String  appURl;
            public String  appKey;
            public String  buildKey;
            public String  buildName;
            public String  buildIcon;
            public String  buildFileKey;
            public long  buildFileSize;
        }
    }
}
