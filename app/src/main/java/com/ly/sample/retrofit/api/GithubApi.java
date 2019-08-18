package com.ly.sample.retrofit.api;

import com.ly.sample.info.GithubInfo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Description: 类的描述
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/8/18 on 20:53
 */
public interface GithubApi {

    @GET("/")
    Call<GithubInfo> get();
}
