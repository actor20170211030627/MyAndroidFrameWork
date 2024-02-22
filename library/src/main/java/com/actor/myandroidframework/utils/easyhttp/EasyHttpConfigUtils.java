package com.actor.myandroidframework.utils.easyhttp;

import com.hjq.http.EasyConfig;

import okhttp3.OkHttpClient;

/**
 * description: 配置轮子哥的EasyHttp, <a href="https://github.com/getActivity/EasyHttp/blob/master/HelpDoc.md" target="_blank">EasyHttp帮助文档</a> <br />
 *
 * @author : ldf
 * date       : 2024/2/16 on 20
 * @version 1.0
 */
public class EasyHttpConfigUtils {

    /**
     * 配置轮子哥的EasyHttp, 配置后可通过{@link EasyConfig#getInstance()}重新设置
     * @param isDebugMode 是否是debug模式
     * @param baseUrl BaseUrl
     * @param okHttpClient ok3的okHttpClient
     */
    public static void init(boolean isDebugMode, String baseUrl, OkHttpClient okHttpClient) {
        EasyConfig.with(okHttpClient)
                //.addParam("token", "6666666")     // 添加全局请求参数
                //.addHeader("time", "20191030")    // 添加全局请求头
//                .removeHeader(String key)         //移除全局请求头
//                .removeParam(String key)          //移除全局请求参数

//                .setClient(OkHttpClient client)   //
                .setHandler(new RequestHandler())   // 设置请求处理策略（必须设置）
//                .setHeaders(Map<String, String> headers)    //设置请求头
//                .setInterceptor(IRequestInterceptor interceptor)  //设置拦截器 (可用于动态添加全局的参数或者请求头)
                .setLogEnabled(isDebugMode)                         // 是否打印日志
//                .setLogStrategy(IRequestLogStrategy strategy)     //设置打印日志策略
//                .setLogTag(String tag)                //设置Log的tag
//                .setParams(Map<String, Object> params)//设置全局请求参数
                .setRetryCount(1)                       // 设置请求重试次数
                .setRetryTime(0)                        //重试时间间隔
                .setServer(baseUrl)                     // 设置服务器配置（必须设置）
//                .setServer(IRequestServer server)     // 设置服务器配置
//                .setThreadSchedulers(ThreadSchedulers schedulers) //线程调度器
                .into();
    }
}
