package com.cl.cloud.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.cl.cloud.dao.DaoMaster;
import com.cl.cloud.dao.DaoSession;
import com.cl.cloud.service.CloudService;
import com.cl.cloud.service.NetworkStateService;
import com.cl.cloud.websocket.WebSocketManager;
import com.google.gson.Gson;
import com.xhd.alive.KeepAliveManager;
import com.xhd.base.app.BaseApplication;

import org.greenrobot.greendao.database.Database;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class App extends BaseApplication {

    public static int sVersionCode;
    public static String sVersionName;
    public static String sPackageName;

    public static final String DB_NAME = "cloud.db";
    private static final String SP_NAME = "sp";
    private static DaoSession sDaoSession;
    private static SharedPreferences sSp;

    private static Gson sGson = new Gson();
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        initAppInfo();
        initDataBase();
        sSp = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        // 进程保活 & 开启 WebSocket
        KeepAliveManager.getInstance().keepApplicaitonAlive(sContext, CloudService.class);
        // 监听网络变化
        startService(new Intent(this, NetworkStateService.class));
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

    private void initDataBase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(sContext, DB_NAME);
        Database sDb = helper.getWritableDb();
        DaoMaster sDaoMaster = new DaoMaster(sDb);
        sDaoSession = sDaoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

    public static SharedPreferences getSharedPreferences(){
        return sSp;
    }

    public static Gson getGson(){
        return sGson;
    }

    public static Handler getHandler() {
        return sHandler;
    }

    // WebSocket(缓存：一个Url对应一个Manager)
    private static LinkedHashMap<String, WebSocketManager> mWsMap = new LinkedHashMap<>();
    public static HashMap<String, WebSocketManager> getWebSocketManagerMap(){
        return mWsMap;
    }

}
