package com.xhd.base.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * 1.管理 Activity 栈（后期有什么需求，都可以在这里增加）
 */
public class ActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    private static ActivityStackManager sStackManager = ActivityStackManager.getInstance();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        /*
        再次跳转 Activity
        如果 activity 为 SingleTop 模式，并且位于栈顶，不会调用该方法
        如果 activity 为 SingleTask 模式，走 onNewInstance 方法，不会调用该方法；
         */
        sStackManager.addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        sStackManager.removeActivity(activity);
    }
    
}
