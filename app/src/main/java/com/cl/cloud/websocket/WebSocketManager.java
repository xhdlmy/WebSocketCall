package com.cl.cloud.websocket;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cl.cloud.R;
import com.cl.cloud.app.App;
import com.cl.cloud.util.NetworkUtils;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 重连机制：重连意思是放弃（关闭）原有 RealWebSocket 连接（connection），重新发送连接请求。

 TODO 1.如何保证服务端发送的数据不丢失？

    1. 服务端发送 0x08 关闭指令，告知不会再发送数据了，数据不会丢失，客户端走 onClosing （本项目不存在）
    2. 服务端端口异常关闭，那么客户端 ping 不通，走 onFailed 回调，进行重连即可
    3. 客户端在意外情况下出现问题并没有通知到服务端是否需要停止发送指令，此时服务端继续发送会丢失一部分数据
        解决方法:
        3.1 服务端也有 ping 操作，如果 ping 不通，则停止发送消息，直到下一次客户端重新连接上服务端（如果接收到重新连接请求，那么会记录该 WebSocket 状态，废弃之前的 WebSocket 通道，继续发送消息）
        3.2 客户端接口意外断开，则进行重连操作（也可以在有网络时顺便通知服务端，以前的 Socket 要关闭了）

 TODO 2.客户端意外断开原因有？

    1. NAT超时
        所以客户端 ping 操作发送心跳包的主要作用是防止NAT超时, 其次是探测连接是否断开。
    2. 客户端网络环境较差，切换网络，4G or Wifi，或者网络断开等因素
        所以在检测到网络变化后就需要进行重连操作
    3. 客户端 WebSocket 连接所在进程挂了
        进程保活，然后第一时间重连操作

 */

public class WebSocketManager {

    private final String TAG = WebSocketManager.class.getSimpleName();

    private Context mContext;

    private OkHttpClient mOkHttpClient;
    private String mWebSocketUrl;
    private Request mRequest;
    // 代表一个 WebSocket 连接客户端，可以进行 send data, cancel, close 操作
    private WebSocket mWebSocket;

    private WebSocketListener mWebSocketListener;
    private WsStatusListener mWsStatusListener;
    private OnNetworkStateChangedListener mNetworkListener;

    // UI 线程转换
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public WebSocketManager(WebSocketManager.Builder builder) {
        // 有缓存，根据 url 获取缓存
        WebSocketManager webSocketManager = App.getWebSocketManagerMap().get(builder.mWebSocketUrl);
        if(webSocketManager != null){
            mContext = webSocketManager.mContext;
            mWebSocketUrl = webSocketManager.mWebSocketUrl;
            mOkHttpClient = webSocketManager.mOkHttpClient;
            mRequest = webSocketManager.mRequest;
            mWebSocketListener = webSocketManager.mWebSocketListener;
            mWsStatusListener = webSocketManager.mWsStatusListener;
            mNetworkListener = webSocketManager.mNetworkListener;
            return;
        }
        // 无缓存，第一次创建初始化参数
        mContext = builder.mContext;
        mWebSocketUrl = builder.mWebSocketUrl;
        mOkHttpClient = builder.mOkHttpClient;
        mWsStatusListener = builder.mWsStatusListener;
        if(mContext == null
                || TextUtils.isEmpty(mWebSocketUrl)  || !(mWebSocketUrl.startsWith("ws://") || mWebSocketUrl.startsWith("wss://"))
                || mOkHttpClient == null || mWsStatusListener == null) {
            return;
        }
        mRequest = new Request.Builder().url(mWebSocketUrl).build();
        mWebSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, final Response response) {
                Log.i(TAG, "client onOpen");
                runOnUIThread(() -> mWsStatusListener.onOpen(response));
            }

            @Override
            public void onMessage(WebSocket webSocket, final String text) {
                Log.i(TAG, "client onMessage: " + text);
                runOnUIThread(() -> mWsStatusListener.onMessage(text));
            }

            @Override
            public void onMessage(WebSocket webSocket, final ByteString bytes) {
                Log.i(TAG, "client onMessage byte[]");
                runOnUIThread(() -> mWsStatusListener.onMessage(bytes));
            }

            // 当服务端指示不再传输传入消息时调用 （0x08指令）
            @Override
            public void onClosing(WebSocket webSocket, final int code, final String reason) {
                Log.i(TAG, "client onClosing code " + code + " msg " + reason);
                runOnUIThread(() -> mWsStatusListener.onClosing(code, reason));
                mWebSocket.close(code, reason);// 客户端通知服务端可以完全关闭链接了，同时客户端也会走 onClosed
            }

            // 当两个对等方都表示不再传输消息并且连接已成功释放时调用。
            @Override
            public void onClosed(WebSocket webSocket, final int code, final String reason) {
                Log.i(TAG, "client onClosed code " + code + " msg " + reason);
                // 服务器端发送的关闭，同时客户如果非正常关闭，那么会丢失数据吧
                // code == 1000，正常关闭，但在该项目下，应该不会服务器主动关闭
                runOnUIThread(() -> mWsStatusListener.onClosed(code, reason));
                // TODO 重新连接 Reason1
                newWebSocket();
            }

            // 由于读取或写入传出和传入消息的错误而关闭Web套接字时调用，消息可能已丢失。
            @Override
            public void onFailure(WebSocket webSocket, final Throwable t, final Response response) {
                Log.i(TAG, "client onFailure throwable " + t.toString() + " response " + response);
                // 服务器端发送的错误
                runOnUIThread(() -> mWsStatusListener.onFailure(t, response));
                // TODO 重新连接 Reason1
                newWebSocket();
            }
        };

        // 网络状态监听：发生改变，需重新请求一个新的 WebSocket 通道
        mNetworkListener = type -> {
            switch (type) {
                case NetworkUtils.TYPE_MOBILE:
                case NetworkUtils.TYPE_WIFI:
                    // TODO 重新连接 Reason2
                    newWebSocket();
                    break;
                case NetworkUtils.NO_NET:
                    Toast.makeText(mContext, R.string.error_no_net, Toast.LENGTH_LONG).show();
                    break;
            }
        };

        // 最后创建完毕，将该 WebSocketManager 缓存起来
        App.getWebSocketManagerMap().put(mWebSocketUrl, this);
    }

    public void newWebSocket(){
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
    }

    /*====================================Do on MainThread===================================================*/

    private void runOnUIThread(OnUIThreadListener listener){
        if(Looper.myLooper() != Looper.getMainLooper()){
            mHandler.post(listener::onUIThread);
        }else{
            listener.onUIThread();
        }
    }

    interface OnUIThreadListener {
        void onUIThread();
    }

    /*====================================NetworkState============================================*/

    public OnNetworkStateChangedListener getNetworkListener(){
        return mNetworkListener;
    }

    public interface OnNetworkStateChangedListener {
        void onNetStateChanged(String type);
    }

    /*======================================== Builder ===============================================*/

    public static final class Builder {

        private Context mContext;
        private String mWebSocketUrl;
        private OkHttpClient mOkHttpClient;
        private WsStatusListener mWsStatusListener;

        public Builder(@NonNull Context context) {
            mContext = context;
        }

        public Builder(Context context, String webSocketUrl, OkHttpClient okHttpClient, WsStatusListener wsStatusListener) {
            mContext = context;
            mWebSocketUrl = webSocketUrl;
            mOkHttpClient = okHttpClient;
            mWsStatusListener = wsStatusListener;
        }

        public WebSocketManager.Builder setUrl(@NonNull String webSocketUrl) {
            mWebSocketUrl = webSocketUrl;
            return this;
        }

        public WebSocketManager.Builder setClient(@NonNull OkHttpClient client) {
            mOkHttpClient = client;
            return this;
        }

        public WebSocketManager.Builder setListener(@NonNull WsStatusListener listener) {
            mWsStatusListener = listener;
            return this;
        }

        public WebSocketManager build() {
            return new WebSocketManager(this);
        }

    }

}
