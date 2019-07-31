package com.actor.myandroidframework.utils.retrofit.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Description: 下载文件
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/15 on 10:10
 */
public interface DownloadFileApi {

    /**
     * rxjava返回io.reactivex.Observable, retrofit返回retrofit2.Call
     */
    @GET
    @Streaming
    Call<ResponseBody> startDownload(@Url String url);
}
