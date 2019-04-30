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
 重连机制：重连意思是废弃原有WebSocket通道，创建一个新的WebSocket通道，发送连接请求，连接的过程也是通过发送HTTP请求实现的
 Reason1 触发服务端执行WebSocket的onclose方法，检测到onclose，表明连接断开，立即重新连接；
 Reason2 不触发服务端执行WebSocket的onclose方法，是客户端原因导致WebSocket断开连接：
            在有网络的情况下，可以发送心跳包消息来告诉服务器客户端的连接是否存活（存活，则服务端可以发送消息；否则服可以触发服务端关闭服务端的连接；服务端应该也有类似的监测机制，保证消息发送不丢失）；
                服务端做法：
                如果连接在客户端非正常关闭了，也不会造成服务端发送的数据的丢失。服务端发送数据之前会检查 WebSocket 连接是否存在；
                如果接收到重新连接请求，那么会记录该 WebSocket 状态，废弃之前的 WebSocket 通道，重新创建一个新的连接。
         NAT超时(所以心跳包的主要作用是防止NAT超时, 其次是探测连接是否断开。)
         如果客户端网络环境较差，切换网络，4G or Wifi，或者网络断开等因素，在检测到网络变化后进行重连；
 Reason3 客户端 WebSocket 所在进程挂了，无法发送心跳包，也无法重连，此时需要考虑的先是APP进程保活

 TODO 多线程问题？

 */

public class WebSocketManager {

    private final String TAG = WebSocketManager.class.getSimpleName();

    private Context mContext;
    private String mWebSocketUrl;
    private OkHttpClient mOkHttpClient;
    private WebSocket mWebSocket;
    private Request mRequest;
    private WebSocketListener mWebSocketListener;
    private WsStatusListener mWsStatusListener;
    private OnNetworkStateChangedListener mNetworkListener;

    // WebSocket 通道本质是远程的跨进程通信，在 Client 端连接的就是该 APP 进程
    // OkHttp 处理 WebSocket 通道传输的消息是分配了工作子线程，所以有一个 UI 线程转换问题
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public WebSocketManager(WebSocketManager.Builder builder) {
        // 对同一个 url 的 WebSocketManager 做了缓存
        WebSocketManager webSocketManager = App.getWebSocketManagerMap().get(builder.mWebSocketUrl);
        if(webSocketManager != null){
            mContext = webSocketManager.mContext;
            mWebSocketUrl = webSocketManager.mWebSocketUrl;
            mOkHttpClient = webSocketManager.mOkHttpClient;
            mWebSocket = webSocketManager.mWebSocket;
            mRequest = webSocketManager.mRequest;
            mWebSocketListener = webSocketManager.mWebSocketListener;
            mWsStatusListener = webSocketManager.mWsStatusListener;
            mNetworkListener = webSocketManager.mNetworkListener;
            return;
        }
        // 无缓存，创建过程初始化
        mContext = builder.mContext;
        mWebSocketUrl = builder.mWebSocketUrl;
        mOkHttpClient = builder.mOkHttpClient;
        mWsStatusListener = builder.mWsStatusListener;
        if(mContext == null
                || TextUtils.isEmpty(mWebSocketUrl)  || !(mWebSocketUrl.startsWith("ws://") || mWebSocketUrl.startsWith("wss://"))
                || mOkHttpClient == null
                || mWsStatusListener == null) {
            Toast.makeText(mContext, mContext.getString(R.string.error_websocket_config), Toast.LENGTH_LONG).show();
            // throw new Exception(mContext.getString(R.string.error_websocket_config));
            return;
        }

        mRequest = new Request.Builder().url(mWebSocketUrl).build();

        mWebSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, final Response response) {
                // 代表一个 WebSocket 通道，可以对该通道进行 send close cancel等操作
                mWebSocket = webSocket; //每次断线重连后都是一个新的 WebSocket 通道
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

            // 当服务端指示不再传输传入消息时调用
            @Override
            public void onClosing(WebSocket webSocket, final int code, final String reason) {
                Log.i(TAG, "client onClosing code " + code + " msg " + reason);
                runOnUIThread(() -> mWsStatusListener.onClosing(code, reason));
                // 客户端通知服务端可以完全关闭链接了
                mWebSocket.close(code, reason);
            }

            // 当两个对等方都表示不再传输消息并且 连接已成功释放 时调用。
            @Override
            public void onClosed(WebSocket webSocket, final int code, final String reason) {
                Log.i(TAG, "client onClosed code " + code + " msg " + reason);
                // 服务器端发送的关闭，如果非正常关闭，那么会丢失数据吧
                // code == 1000，正常关闭，但在该项目下，应该不会服务器主动关闭
                runOnUIThread(() -> mWsStatusListener.onClosed(code, reason));
                // TODO 重新连接 Reason1
            }

            // 由于读取或写入传出和传入消息的错误而关闭Web套接字时调用，消息可能已丢失。
            @Override
            public void onFailure(WebSocket webSocket, final Throwable t, final Response response) {
                Log.i(TAG, "client onFailure throwable " + t.toString() + " response " + response);
                // 服务器端发送的错误
                runOnUIThread(() -> mWsStatusListener.onFailure(t, response));
                // TODO 重新连接 Reason1
            }
        };

        // 网络状态监听：发生改变，需重新请求一个新的 WebSocket 通道
        mNetworkListener = type -> {
            switch (type) {
                case NetworkUtils.TYPE_MOBILE:
                case NetworkUtils.TYPE_WIFI:
                    // TODO 重新连接 Reason2
                    break;
                case NetworkUtils.NO_NET:
                    Toast.makeText(mContext, R.string.error_no_net, Toast.LENGTH_SHORT).show();
                    break;
            }
        };

        // 最后创建完毕，将该 WebSocketManager 缓存起来
        App.getWebSocketManagerMap().put(mWebSocketUrl, this);
    }

    private void newWebSocket(){
        mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
    }

    /*=======================================================================================*/

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

    public OnNetworkStateChangedListener getNetworkListener(){
        return mNetworkListener;
    }

    public interface OnNetworkStateChangedListener {
        void onNetStateChanged(String type);
    }

    /*=======================================================================================*/

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
