package com.xhd.base.util.manufacturer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

/**
 * support:
 * 1.Y55A androi:6.0.1/Funtouch 2.6
 * 2.Xplay5A android: 5.1.1/Funtouch 3
 *
 * manager home page, or {@link Protogenesis#settingIntent()}
 *
 * Created by joker on 2017/8/4.
 */

public class VIVO implements PermissionsPage {
    private final String MAIN_CLS = "com.iqoo.secure.MainActivity";
    private final String PKG = "com.iqoo.secure";
    private final Activity context;

    public VIVO(Activity context) {
        this.context = context;
    }

    @Override
    public Intent settingIntent() throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp = new ComponentName(PKG, MAIN_CLS);

        intent.setComponent(comp);

        return intent;
    }
}
