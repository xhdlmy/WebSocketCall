package com.xhd.base.util.manufacturer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * support:
 * 1.mate7 android:6.0/emui 4.0.1
 * 2.畅享7 android:7.0/emui 5.1
 */

public class HUAWEI implements PermissionsPage {
    private final Activity context;
    private final String PKG = "com.huawei.systemmanager";
    private final String MANAGER_OUT_CLS = "com.huawei.permissionmanager.ui.MainActivity";

    public HUAWEI(Activity context) {
        this.context = context;
    }

    @Override
    public Intent settingIntent() throws ActivityNotFoundException {
        Intent intent = new Protogenesis(context).settingIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp = null;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(PKG,
                    PackageManager.GET_ACTIVITIES);
            for (ActivityInfo activityInfo : pi.activities) {
                if (activityInfo.name.equals(MANAGER_OUT_CLS)) {
                    comp = new ComponentName(PKG, MANAGER_OUT_CLS);
                }
            }
            if (comp != null) {
                intent.setComponent(comp);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return intent;
        }

        return intent;
    }
}
