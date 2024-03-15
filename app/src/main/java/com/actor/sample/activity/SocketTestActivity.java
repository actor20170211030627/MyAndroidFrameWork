package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.chat_layout.OnListener;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.easyhttp.EasyHttpConfigUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.ChatListAdapter;
import com.actor.sample.databinding.ActivitySocketTestBinding;
import com.actor.sample.info.MessageItem;
import com.blankj.utilcode.util.ThreadUtils;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class SocketTestActivity extends BaseActivity<ActivitySocketTestBinding> {

    private       ChatListAdapter   mAdapter;
    private       WebSocket         mWebSocket;
    //是否手动关闭
    private       boolean           isManuallyClosed = false;


    /**
     * http://www.websocket-test.com/, 每天最多50条消息
     */
//    public static final String WEB_SOCKET = "ws://121.40.165.18:8800";
    /**
     * http://coolaf.com/tool/chattest
     * 连接失败, t = java.net.ProtocolException: Expected HTTP 101 response but was '403 Forbidden', response = Response{protocol=http/1.1, code=403, message=Forbidden, url=http://82.157.123.54:9010/ajaxchattest}
     */
//    public static final String WEB_SOCKET = "ws://82.157.123.54:9010/ajaxchattest";

    /**
     * 官网测试 https://websocket.org/tools/websocket-echo-server/
     */
//    public static final String WEB_SOCKET = "ws://echo.websocket.org";
    public static final String WEB_SOCKET = "wss://echo.websocket.org";


    //Socket监听
    private final WebSocketListener webSocketListener = new WebSocketListener() {

        //连接成功...
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
            super.onOpen(webSocket, response);
            String s = getStringFormat("onOpen(WebSocket, Response)\nresponse = %s", response);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> {
                mAdapter.addData(new MessageItem(false, s));
                scroll2Last();
            });
        }

        //收到消息: 在收到文本（类型 0x1）消息时调用。
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            String s = getStringFormat("onMessage(WebSocket, String)\ntext = %s", text);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> {
                mAdapter.addData(new MessageItem(false, s));
                scroll2Last();
            });
        }

        //收到消息: 在收到二进制（类型 0x2）消息时调用
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            String s = getStringFormat("onMessage(WebSocket, ByteString)\nbytes = %s", bytes);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);
            runOnUiThread(() -> {
                mAdapter.addData(new MessageItem(false, s));
                scroll2Last();
            });
            scroll2Last();
        }

        //当远程对等方指示不再传输传入消息时调用。
        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosing(webSocket, code, reason);
            String s = getStringFormat("onClosing(WebSocket, int, String)\ncode = %d, reason = %s", code, reason);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> {
                mAdapter.addData(new MessageItem(false, s));
                scroll2Last();
            });
            webSocket.close(code, reason);
        }

        //连接关闭: 当两个对等点都表示不再传输消息, 并且连接已成功释放时调用
        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosed(webSocket, code, reason);
            String s = getStringFormat("onClosed(WebSocket, int, String)\ncode = %d, reason = %s", code, reason);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            runOnUiThread(() -> {
                mAdapter.addData(new MessageItem(false, s));
                scroll2Last();
            });
        }

        //连接失败
        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            String s = getStringFormat("onFailure(WebSocket, Throwable, Response)\nt = %s, response = %s", t, response);
            LogUtils.error(s);
            boolean mainThread = ThreadUtils.isMainThread();
            LogUtils.error("是否主线程: " + mainThread);//false
            //if不是手动关闭
            if (!isManuallyClosed) {
                runOnUiThread(() -> {
                    mAdapter.addData(new MessageItem(false, s));
                    scroll2Last();
                });
                //重连
                mWebSocket = EasyHttpConfigUtils.socketReConnection(webSocket, webSocketListener);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("okhttp的Socket示例");
        viewBinding.itilSocket.setText(WEB_SOCKET);
        viewBinding.rvSocketMsg.setAdapter(mAdapter = new ChatListAdapter(null));
        viewBinding.chatLayout.init(viewBinding.rvSocketMsg, null);
        viewBinding.chatLayout.setOnListener(new OnListener() {
            @Override
            public void onBtnSendClick(EditText etMsg) {
                //发Socket消息
                if (mWebSocket == null) {
                    ToasterUtils.warning("Socket还未连接");
                } else if (isNoEmpty(etMsg)) {
                    String text = getText(etMsg);
                    etMsg.setText("");
                    mAdapter.addData(new MessageItem(true, text));
                    scroll2Last();

                    boolean sendSuccess = mWebSocket.send(text);
//                    boolean sendSuccess = mWebSocket.send(ByteString.decodeHex("adef"));
                    ToasterUtils.infoFormat("发送成功? : %b", sendSuccess);
                }
            }
        });
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_socket_connet:
                //开始连Socket
                if (isNoEmpty(viewBinding.itilSocket)) {
                    viewBinding.itilSocket.setInputEnable(false);
                    String socketUrl = getText(viewBinding.itilSocket);
                    if (mWebSocket == null) {
                        mWebSocket = EasyHttpConfigUtils.getSocket(this, socketUrl, webSocketListener);
                    } else {
                        ToasterUtils.success("是已连接状态");
                    }
                    isManuallyClosed = false;
                }
                break;
            case R.id.btn_socket_close:
                //断开Socket
                if (mWebSocket == null) {
                    ToasterUtils.warning("Socket还未连接");
                } else {
                    ToasterUtils.warning("开始关闭");
                    /**
                     * 返回排队传输到服务器的所有消息的大小（以字节为单位）。
                     */
                    long queueSize = EasyHttpConfigUtils.queueSize(mWebSocket);

                    boolean close = EasyHttpConfigUtils.socketClose(mWebSocket, 1000, "客户端正常关闭!");
                    mWebSocket = null;
                    ToasterUtils.infoFormat("关闭成功? : %b", close);
                    isManuallyClosed = true;
                }
                break;
            default:
                break;
        }
    }

    private void scroll2Last() {
        viewBinding.rvSocketMsg.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onBackPressed() {
        if (viewBinding.chatLayout.isBottomViewGone()) {
            isManuallyClosed = true;
            super.onBackPressed();
        }
    }
}