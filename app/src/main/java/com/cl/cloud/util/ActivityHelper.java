package com.cl.cloud.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cl.cloud.app.Constant;
import com.xhd.base.activity.BaseActivity;
import com.xhd.base.app.ActivityStackManager;

/**
 * Created by work2 on 2019/5/14.
 */

public class ActivityHelper {

    public static void gotoActivity(Context context, Class<? extends BaseActivity> clazz) {
        Activity activity = ActivityStackManager.getInstance().getTopActivity();
        Intent intent = new Intent(context, clazz);
        if(activity == null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            activity.startActivity(intent);
            activity.finish();
        }
    }

}
