package com.cl.cloud.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.cl.cloud.BuildConfig;
import com.cl.cloud.service.CloudService;
import com.cl.cloud.websocket.WebSocketManager;
import com.google.gson.Gson;
import com.xhd.alive.KeepAliveManager;
import com.xhd.base.app.BaseApplication;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class App extends BaseApplication {

    public static int sVersionCode;
    public static String sVersionName;
    public static String sPackageName;

    private static Gson sGson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        initAppInfo();
        // 进程保活 & 开启 WebSocket
        KeepAliveManager.getInstance().keepApplicaitonAlive(sContext, CloudService.class);
    }

    private void initAppInfo() {
        PackageManager packageManager = sContext.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(sContext.getPackageName(),0);
            sVersionCode = packInfo.versionCode;
            sVersionName = packInfo.versionName;
            sPackageName = packInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            sVersionCode = -1;
            sVersionName = "package not found!";
            sPackageName = sContext.getPackageName();
        }
    }

    public static Gson getGson(){
        return sGson;
    }

    // WebSocket(缓存：一个Url对应一个Manager)
    private static LinkedHashMap<String, WebSocketManager> mWsMap = new LinkedHashMap<>();
    public static HashMap<String, WebSocketManager> getWebSocketManagerMap(){
        return mWsMap;
    }

}
