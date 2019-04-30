package com.xhd.base.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by work2 on 2019/4/30.
 */

public class BaseApplication extends Application {

    public static BaseApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Context getAppContext(){
        return sInstance;
    }
}
