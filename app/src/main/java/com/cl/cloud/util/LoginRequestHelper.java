package com.cl.cloud.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cl.cloud.activity.LoginActivity;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.protocol.ConnectMsg;
import com.cl.cloud.websocket.WebSocketManager;
import com.xhd.base.app.ActivityStackManager;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.ByteString;

public class LoginRequestHelper {

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void login(Context context, String userName, String userPwd){
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)){
            ActivityHelper.gotoActivity(context, LoginActivity.class);
        }
        executor.execute(() -> {
            ConnectMsg cm = new ConnectMsg(userName, userPwd);
            byte[] data = cm.wrap().array();
            ByteString byteString = ByteString.of(data);
            HashMap<String, WebSocketManager> wsMap = App.getWebSocketManagerMap();
            WebSocketManager webSocketManager = wsMap.get(Constant.WS_URL);
            webSocketManager.getWebSocket().send(byteString);
        });
    }



}
