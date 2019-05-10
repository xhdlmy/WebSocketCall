package com.cl.cloud.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.cl.cloud.app.App;
import com.cl.cloud.websocket.WebSocketManager;
import com.xhd.base.util.NetworkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetworkStateService extends Service {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null) return;
            String action = intent.getAction();
            if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) return;
            // 接收 ConnectivityManager.CONNECTIVITY_ACTION 网络变化的系统广播
            String networkType = NetworkUtils.getNetworkType(App.getAppContext());
            HashMap<String, WebSocketManager> wsMap = App.getWebSocketManagerMap();
            Set<Map.Entry<String, WebSocketManager>> entries = wsMap.entrySet();
            for (Map.Entry<String, WebSocketManager> entry : entries) {
                WebSocketManager wsManager = entry.getValue();
                wsManager.getNetworkListener().onNetStateChanged(networkType);
            }
        }
    };

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
