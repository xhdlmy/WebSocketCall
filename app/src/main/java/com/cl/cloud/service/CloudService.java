package com.cl.cloud.service;

import android.content.Intent;

import com.cl.cloud.app.Constant;
import com.cl.cloud.websocket.OkClient;
import com.cl.cloud.websocket.WebSocketManager;
import com.cl.cloud.websocket.WsStatusListener;
import com.xhd.alive.KeepAliveService;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.ByteString;

/**
 * Created by work2 on 2019/5/5.
 */

public class CloudService extends KeepAliveService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO 进程主线程中 WebSocket 的连接工作
        WebSocketManager.Builder builder = new WebSocketManager.Builder(this, Constant.WS_URL, OkClient.getInstance().getOkHttpClient(), new WsStatusListener() {
            @Override
            public void onOpen(Response response) {
                super.onOpen(response);
                //
            }

            @Override
            public void onMessage(ByteString bytes) {
                super.onMessage(bytes);
            }
        });
        WebSocketManager webSocketManager = new WebSocketManager(builder);
        webSocketManager.newWebSocket();

        return START_STICKY;
    }
}
