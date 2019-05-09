package com.xhd.base.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by work2 on 2019/4/30.
 */

public class BaseApplication extends Application {

    protected static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
        sContext = this;
    }

    public static Context getAppContext(){
        return sContext;
    }
}
