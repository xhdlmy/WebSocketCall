package com.cl.cloud.service;

import android.content.Intent;

import com.cl.cloud.activity.LoginActivity;
import com.cl.cloud.activity.MainActivity;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.push.PushEntity;
import com.cl.cloud.util.ActivityHelper;
import com.cl.cloud.util.LoginRequestHelper;
import com.cl.cloud.util.SpUtils;
import com.cl.cloud.websocket.OkClient;
import com.cl.cloud.websocket.WebSocketManager;
import com.cl.cloud.websocket.WsStatusListener;
import com.google.gson.JsonSyntaxException;
import com.xhd.alive.KeepAliveService;
import com.xhd.base.util.LogUtils;
import com.xhd.base.util.ToastUtils;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class CloudService extends KeepAliveService {

    private static final String TAG = CloudService.class.getSimpleName();

    private WsStatusListener wsStatusListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(wsStatusListener == null){
            wsStatusListener = new WsStatusListener() {

                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    // TODO 是否需要判断进程是否是被杀后拉起？
                    // websocket login
                    LoginRequestHelper.login(CloudService.this);
                }

                @Override
                public void onMessage(String msg) {
                    try {
                        PushEntity entity = App.getGson().fromJson(msg, PushEntity.class);
                        switch (entity.getType()) {
                            case CONNECT:
                                if(entity.status == Constant.STATUS_SUCCESS){
                                    SpUtils.getInstances().putIsLogin(true).commit();
                                    ActivityHelper.gotoActivity(CloudService.this, MainActivity.class);
                                }else{
                                    SpUtils.getInstances().putIsLogin(false).commit();
                                    ActivityHelper.gotoActivity(CloudService.this, LoginActivity.class);
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

                @Override
                public void onClosed(int code, String reason) {
                    SpUtils.getInstances().putIsLogin(false).commit();
                }

                @Override
                public void onFailure(Throwable t, Response response) {
                    SpUtils.getInstances().putIsLogin(false).commit();
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
