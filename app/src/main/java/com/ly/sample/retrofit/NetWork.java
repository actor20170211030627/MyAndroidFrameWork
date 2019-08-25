package com.ly.sample.retrofit;

import com.actor.myandroidframework.utils.retrofit.RetrofitNetwork;
import com.ly.sample.retrofit.api.GithubApi;

/**
 * Description: 网络请求示例
 * Date       : 2019/8/18 on 20:52
 */
public class NetWork extends RetrofitNetwork {

    public static GithubApi getGithubApi() {
        return getApi(GithubApi.class);
    }
}
