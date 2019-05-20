package com.cl.cloud.service;

import android.content.Intent;
import android.os.Handler;

import com.cl.cloud.activity.LoginActivity;
import com.cl.cloud.activity.MainActivity;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.dao.ReceiveBean;
import com.cl.cloud.dao.ReceiveBeanDaoAgent;
import com.cl.cloud.push.PushEntity;
import com.cl.cloud.util.ActivityHelper;
import com.cl.cloud.util.LoginRequestHelper;
import com.cl.cloud.util.NotificationHelper;
import com.cl.cloud.util.SpUtils;
import com.cl.cloud.util.TelePhonyHelper;
import com.cl.cloud.websocket.OkClient;
import com.cl.cloud.websocket.WebSocketManager;
import com.cl.cloud.websocket.WsStatusListener;
import com.google.gson.JsonSyntaxException;
import com.xhd.alive.KeepAliveService;
import com.xhd.base.util.LogUtils;

import java.text.ParseException;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class CloudService extends KeepAliveService {

    private static final String TAG = CloudService.class.getSimpleName();

    private WsStatusListener wsStatusListener;

    private Handler mHandler = App.getHandler();

    private ReceiveBeanDaoAgent mDaoAgent = ReceiveBeanDaoAgent.getInstance();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(wsStatusListener == null){
            wsStatusListener = new WsStatusListener() {

                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    // 不需要判断进程是否是被杀后拉起，登录成功 or 失败都去对应的 Activity
                    // websocket login
                    String userName = SpUtils.getInstances().getUserName();
                    String userPwd = SpUtils.getInstances().getUserPwd();
                    LoginRequestHelper.login(CloudService.this, userName, userPwd);
                }

                @Override
                public void onMessage(String msg) {
                    try {
                        PushEntity entity = App.getGson().fromJson(msg, PushEntity.class);
                        switch (entity.getType()) {
                            case CONNECT:
                                if(entity.status == Constant.STATUS_SUCCESS){
                                    ActivityHelper.gotoActivity(CloudService.this, MainActivity.class);
                                }else{
                                    ActivityHelper.gotoActivity(CloudService.this, LoginActivity.class);
                                }
                                break;
                            case AUTO_CALL_PUSH:
                            case AUTO_SEND_PUSH:
                                dealPush(entity);
                                // 存入本地
                                String userName = SpUtils.getInstances().getUserName();
                                ReceiveBean bean = new ReceiveBean(userName, entity);
                                mDaoAgent.insert(bean);
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

    private void dealPush(PushEntity entity) {
        PushEntity.MsgType type = entity.getType();
        switch (type) {
            case AUTO_CALL_PUSH:
                // 自动拨号
                TelePhonyHelper.dealCall(entity);
                break;
            case AUTO_SEND_PUSH:
                // 发送短信
                TelePhonyHelper.dealSms(entity);
                break;
        }

    }

}
