package com.actor.myandroidframework.utils.easyhttp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.okhttputils.OkHttpConfigUtils;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyHttp;
import com.hjq.http.lifecycle.HttpLifecycleManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * description: 配置轮子哥的EasyHttp, <a href="https://github.com/getActivity/EasyHttp/blob/master/HelpDoc.md" target="_blank">EasyHttp帮助文档</a> <br />
 * 使用前先配置:
 * <ol>
 *     <li>{@link #initOkHttp(boolean)}: 先初始化 OkHttpClient.Builder</li>
 *     <li>{@link #init(boolean, String, OkHttpClient)}: 然后传入 OkHttpClient, 初始化EasyHttp</li>
 *     <li>之后可再调用: {@link EasyConfig#getInstance()} 做额外设置</li>
 * </ol>
 * @author : ldf
 * @date       : 2024/2/16 on 20
 */
public class EasyHttpConfigUtils {

    /**
     * 配置okhttp
     */
    public static OkHttpClient.Builder initOkHttp(boolean isDebugMode) {
        return OkHttpConfigUtils.initOkHttp(isDebugMode);
    }

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



    ///////////////////////////////////////////////////////////////////////////
    // 在EasyHttp基础上搞几个WebSocket方法 (轮子哥拒绝封装WebSocket)
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 获取Socket, <a href="http://www.52im.net/forum.php?mod=viewthread&tid=331&ctid=15">WebSocket详解（一）：初步认识WebSocket技术</a>
     * @param tag LifecycleOwner, {@link null 注意：}if tag != null, 当页面onDestroy的时候, 会自动断开Socket, 就不需要手动调用{@link #socketCancel(WebSocket)}方法了
     * @param wsUrl webSocket地址, 例: ws://echo.websocket.org/?encoding=text HTTP/1.1&userId=12345
     * @param listener 回调监听
     * @return WebSocket
     */
    public static WebSocket getSocket(@Nullable LifecycleOwner tag,
                                      @NonNull String wsUrl,
                                      @NonNull WebSocketListener listener) {
        Request request = new Request.Builder()
                .get()      //实际是get, 但 newWebSocket()后, 请求头 Upgrade:websocket, Connection:Upgrade
                .tag(tag)
                .url(wsUrl)
                .build();
        OkHttpClient client = EasyConfig.getInstance().getClient();
        if (tag != null) {
            /**
             * onDestroy的时候, 会自动调用{@link EasyHttp#cancelByTag(Object)}方法取消请求
             */
            HttpLifecycleManager.register(tag);
        }
        return client.newWebSocket(request, listener);
    }

    /**
     * 重连Socket
     * @param webSocket WebSocket
     * @param listener 回调监听
     * @return 返回重新连接后的WebSocket
     */
    public static WebSocket socketReConnection(@NonNull WebSocket webSocket, @NonNull WebSocketListener listener) {
        Request request = webSocket.request();
        OkHttpClient client = EasyConfig.getInstance().getClient();
        return client.newWebSocket(request, listener);
    }

    /**
     * 返回排队传送到服务器的所有消息的大小(以字节为单位)。这还不包括帧开销。
     * 如果启用了压缩，则使用未压缩的消息大小来计算此值。它也不包括由操作系统或网络中介体缓冲的任何字节。
     * 如果队列中没有等待消息，此方法返回0。
     * If webSocket被取消后返回一个非零值; 这表明未传输排队的消息。
     */
    public static long queueSize(@Nullable WebSocket webSocket) {
        if (webSocket == null) return 0;
        return webSocket.queueSize();
    }

    /**
     * 请求服务器优雅地关闭连接然后等待确认。<br />
     * 在关闭之前，所有已经在队列中的消息将被传送完毕。<br />
     * 既然涉及到交互，那么socket可能不会立即关闭。<br />
     * 如果初始化和关闭连接是和Activity的生命周期绑定的（比如onPause/onResume），有一些消息可能是在close被调用之后接收到，所以这需要小心去处理。
     * @param code <a href="https://datatracker.ietf.org/doc/html/rfc6455#section-7.4">关闭状态码</a>,
     *             只能是 1000 ~ 1003, 1007~1011 中的1个, 1000是正常关闭, 更多请参考代码
     *             {@link okhttp3.internal.ws.WebSocketProtocol#closeCodeExceptionMessage(int) WebSocketProtocol.closeCodeExceptionMessage(code)}
     *             or 阅读协议.
     * @param reason 关闭原因, 不超过123字节的UTF-8编码数据, 或传null, 例: "客户端正常关闭" or null
     * @return true: 正常关闭。
     *         false: 正常关闭已经在进行中，或者webSocket已经关闭或取消。
     */
    public static boolean socketClose(@Nullable WebSocket webSocket, int code, @Nullable String reason) {
        if (webSocket == null) return true;
        return webSocket.close(code, reason);
    }

    /**
     * 立即暴力释放此 webSocket持有的资源，丢弃任何排队的消息。
     * 如果 webSocket已经关闭或取消，这将不起作用。
     * {@link null 注意：}如果{@link #getSocket(LifecycleOwner, String, WebSocketListener)} 传入的tag != null, 会自动点开Socket, 可不用再手动调用此方法.
     */
    public static void socketCancel(@Nullable WebSocket webSocket) {
        if (webSocket != null) webSocket.cancel();
    }
}
