package com.xhd.alive.module;

import com.xhd.alive.KeepAliveManager;
import com.xhd.base.app.BaseApplication;

public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // 进程保活
        KeepAliveManager.getInstance().startKeepAliveService(this);
        KeepAliveManager.getInstance().startJobScheduler(this);
        KeepAliveManager.getInstance().syncAccount(this);
    }

}
