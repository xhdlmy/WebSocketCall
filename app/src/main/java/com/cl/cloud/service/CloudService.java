package com.cl.cloud.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cl.cloud.activity.LoginActivity;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.protocol.ConnectMsg;
import com.cl.cloud.push.PushEntity;
import com.cl.cloud.util.LoginRequestHelper;
import com.cl.cloud.util.SpUtils;
import com.cl.cloud.websocket.OkClient;
import com.cl.cloud.websocket.WebSocketManager;
import com.cl.cloud.websocket.WsStatusListener;
import com.google.gson.JsonSyntaxException;
import com.xhd.alive.KeepAliveService;
import com.xhd.base.app.ActivityStackManager;
import com.xhd.base.util.LogUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

import static android.os.AsyncTask.execute;

/**
 * Created by work2 on 2019/5/5.
 */

public class CloudService extends KeepAliveService {

    private static final String TAG = CloudService.class.getSimpleName();

    private WsStatusListener wsStatusListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(wsStatusListener == null){
            wsStatusListener = new WsStatusListener() {

                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    LoginRequestHelper.login(CloudService.this);
                }

                @Override
                public void onMessage(String msg) {
                    try {
                        PushEntity entity = App.getGson().fromJson(msg, PushEntity.class);
                        switch (entity.getType()) {
                            case CONNECT:
                                if(entity.status == Constant.STATUS_SUCCESS){
                                    // TODO 登录成功
                                }else{
                                    LoginRequestHelper.gotoLoginActivity(CloudService.this);
                                }
                                break;
                            case AUTO_CALL_PUSH:

                                break;
                            case AUTO_SEND_PUSH:

                                break;
                            case UN_DEFINE:

                                break;
                        }
                    } catch (JsonSyntaxException exception){
                        LogUtils.i(TAG, exception.toString());
                    }

                }

                @Override
                public void onMessage(ByteString bytes) {
                    String msg = bytes.utf8();
                    onMessage(msg);
                }
            };
        }
        WebSocketManager webSocketManager = new WebSocketManager.Builder(this)
                .setUrl(Constant.WS_URL)
                .setClient(OkClient.getInstance().getOkHttpClient())
                .setListener(wsStatusListener)
                .build();
        webSocketManager.newWebSocket();
        return START_STICKY;
    }

}
