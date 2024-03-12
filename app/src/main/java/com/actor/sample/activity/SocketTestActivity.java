package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.other_utils.widget.ItemTextInputLayout;
import com.actor.sample.R;
import com.actor.sample.adapter.SocketMsgAdapter;
import com.actor.sample.databinding.ActivitySocketTestBinding;
import com.blankj.utilcode.util.ThreadUtils;
import com.hjq.http.EasyConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class SocketTestActivity extends BaseActivity<ActivitySocketTestBinding> {

    private ItemTextInputLayout itilSocket, itilSocketMsg;
    private RecyclerView rvSocketMsg;

    private       SocketMsgAdapter  mAdapter;
    private       WebSocket         webSocket;
    private       boolean           isConnect = false;


    /**
     * http://www.websocket-test.com/
     * 每天最多50条消息
     */
    public static final String WEB_SOCKET = "ws://121.40.165.18:8800";
    /**
     * okhttp官网测试?
     * 连接失败, webSocket = okhttp3.internal.ws.RealWebSocket@14d7ee20, t = java.net.ProtocolException: Expected HTTP 101 response but was '200 OK', response = Response{protocol=http/1.1, code=200, message=OK, url=http://echo.websocket.org/}
     */
//    public static final String WEB_SOCKET = "ws://echo.websocket.org";
    /**
     * http://coolaf.com/tool/chattest
     * 连接失败, webSocket = okhttp3.internal.ws.RealWebSocket@1cad3764, t = java.net.ProtocolException: Expected HTTP 101 response but was '403 Forbidden', response = Response{protocol=http/1.1, code=403, message=Forbidden, url=http://82.157.123.54:9010/ajaxchattest}
     */
//    public static final String WEB_SOCKET = "ws://82.157.123.54:9010/ajaxchattest";


    //Socket监听
    private final WebSocketListener webSocketListener = new WebSocketListener() {

        //连接成功...
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
            super.onOpen(webSocket, response);
            isConnect = true;
            String s = getStringFormat("连接成功, webSocket = %s, response = %s", webSocket, response);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> mAdapter.addData(s));
        }

        //收到消息: 在收到文本（类型 0x1）消息时调用。
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            String s = getStringFormat("收到文本消息1, webSocket = %s, text = %s", webSocket, text);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> mAdapter.addData(s));
        }

        //收到消息: 在收到二进制（类型 0x2）消息时调用
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            String s = getStringFormat("收到二进制消息2, webSocket = %s, bytes = %s", webSocket, bytes);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);
            runOnUiThread(() -> mAdapter.addData(s));
        }

        //当远程对等方指示不再传输传入消息时调用。
        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosing(webSocket, code, reason);
            String s = getStringFormat("连接关闭2, webSocket = %s, code = %d, reason = %s", webSocket, code, reason);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> mAdapter.addData(s));
            webSocket.close(code, reason);
            isConnect = false;
        }

        //连接关闭: 当两个对等点都表示不再传输消息, 并且连接已成功释放时调用
        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosed(webSocket, code, reason);
            isConnect = false;
            String s = getStringFormat("连接关闭1, webSocket = %s, code = %d, reason = %s", webSocket, code, reason);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> mAdapter.addData(s));
        }

        //连接失败
        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            isConnect = false;
            String s = getStringFormat("连接失败, webSocket = %s, t = %s, response = %s", webSocket, t, response);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> mAdapter.addData(s));
            //重连
//            MyOkHttpUtils.socketReConnecton(webSocket, webSocketListener);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itilSocket = viewBinding.itilSocket;
        itilSocketMsg = viewBinding.itilSocketMsg;
        rvSocketMsg = viewBinding.rvSocketMsg;

        setTitle("okhttp的Socket示例");
        itilSocket.setText(WEB_SOCKET);
        rvSocketMsg.setAdapter(mAdapter = new SocketMsgAdapter());
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_socket_connet:
                //开始连Socket
                if (isNoEmpty(itilSocket)) {
                    itilSocket.setInputEnable(false);
                    String socketUrl = getText(itilSocket);
                    if (webSocket == null) {
                        // TODO: 2024/3/12 webSocket功能
                        Request request = new Request.Builder().url(socketUrl).tag(this).build();
                        OkHttpClient client = EasyConfig.getInstance().getClient();
                        webSocket = client.newWebSocket(request, webSocketListener);
                    } else {
                        if (isConnect) {
                            ToasterUtils.success("是已连接状态");
                        } else {
                            //重连
                            Request request = webSocket.request();
                            OkHttpClient client = EasyConfig.getInstance().getClient();
                            client.newWebSocket(request, webSocketListener);
                        }
                    }
                }
                break;
            case R.id.btn_socket_close:
                //断开Socket
                if (webSocket == null) {
                    ToasterUtils.warning("Socket还未连接");
                } else {
                    ToasterUtils.warning("开始关闭");
                    /**
                     * 返回排队传输到服务器的所有消息的大小（以字节为单位）。
                     */
                    long queueSize = webSocket.queueSize();

                    /**
                     * 请求服务器优雅地关闭连接然后等待确认。
                     * 在关闭之前，所有已经在队列中的消息将被传送完毕。
                     * 既然涉及到交互，那么socket可能不会立即关闭。
                     * 如果初始化和关闭连接是和Activity的生命周期绑定的（比如onPause/onResume），有一些消息可能是在close被调用之后接收到，所以这需要小心去处理。
                     * code: [1000,5000)
                     */
                    boolean close = webSocket.close(1000, "客户端正常关闭!");
                    ToasterUtils.infoFormat("关闭成功? : %b", close);
                }
                break;
            case R.id.btn_socket_send:
                //发Socket消息
                if (webSocket == null) {
                    ToasterUtils.warning("Socket还未连接");
                } else if (isNoEmpty(itilSocketMsg)) {
                    String text = getText(itilSocketMsg);
                    boolean sendSuccess = webSocket.send(text);
//                    boolean sendSuccess = webSocket.send(ByteString.decodeHex("adef"));
                    ToasterUtils.infoFormat("发送成功? : %b", sendSuccess);
                    if (!sendSuccess) {
                        //重连
//                        MyOkHttpUtils.socketReConnecton(webSocket, webSocketListener);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            /**
             * 立即暴力释放此 Web 套接字持有的资源，丢弃任何排队的消息。
             * 如果 Web 套接字已经关闭或取消，这将不起作用。
             */
            webSocket.cancel();
        }
    }
}