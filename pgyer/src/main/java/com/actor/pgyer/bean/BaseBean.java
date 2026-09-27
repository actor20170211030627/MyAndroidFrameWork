package com.actor.pgyer.bean;

import androidx.annotation.Keep;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2026/9/27 on 00
 * @version 1.0
 */
@Keep
public class BaseBean<T> {
    /**
     * code : 0
     * message :
     * data : {"buildBuildVersion":"6","forceUpdateVersion":"","forceUpdateVersionNo":"","needForceUpdate":false,"downloadURL":"https://www.pgyer.com/app/installUpdate/4bafbd62b0c007a95017b4587084d2d9?sig=o0DFRxhTkACYBijzqQQTw6jgtbnOdxCnHL5RIkXgMpdSxUyf8rIXWk6b9UQnZssc&forceHttps=","buildHaveNewVersion":true,"buildVersionNo":"2026092501","buildVersion":"1.5","buildDescription":"中石油司机题库","buildUpdateDescription":"新增悬浮窗学习功能","buildShortcutUrl":"https://www.pgyer.com/c98VC1Cb","appURl":"https://www.pgyer.com/4bafbd62b0c007a95017b4587084d2d9","appKey":"a96adf3c304b727261325f6b60dbd865","buildKey":"4bafbd62b0c007a95017b4587084d2d9","buildName":"中石油题库","buildIcon":"https://cdn-app-icon2.pgyer.com/a/2/2/4/9/a22492919e4154b7b55f154725190e2a?x-oss-process=image/resize,m_lfit,h_120,w_120/format,jpg","buildFileKey":"7e124515555a8a2ac7cdbedda54a5527.apk","buildFileSize":"3148231"}
     */

    public int code;
    public String   message;
    public T data;

    /**
     * <a href="https://www.pgyer.com/doc/view/api#%E9%94%99%E8%AF%AF%E7%A0%81%E5%90%AB%E4%B9%89" target="_blank">错误码含义</a>
     * @return
     */
    public boolean isSuccess() {
        return code == 0;
    }
}
