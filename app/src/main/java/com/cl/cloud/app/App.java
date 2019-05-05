package com.cl.cloud.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.cl.cloud.service.CloudService;
import com.cl.cloud.service.NetworkStateService;
import com.cl.cloud.websocket.WebSocketManager;
import com.xhd.alive.KeepAliveManager;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        // 进程保活 & 开启 WebSocket
        KeepAliveManager.getInstance().keepApplicaitonAlive(sContext, CloudService.class);

    }

    public static Context getContext(){
        return sContext;
    }

    // WebSocket(缓存：一个Url对应一个Manager)
    private static LinkedHashMap<String, WebSocketManager> mWsMap = new LinkedHashMap<>();
    public static HashMap<String, WebSocketManager> getWebSocketManagerMap(){
        return mWsMap;
    }

}
