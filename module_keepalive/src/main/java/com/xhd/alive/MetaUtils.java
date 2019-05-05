package com.xhd.alive;

import android.content.Context;
import android.content.pm.PackageManager;

import com.xhd.base.util.LogUtils;

/**
 * Created by work2 on 2019/5/5.
 */

public class MetaUtils {

    public static String getMetaStringData(Context context, String metaName) {
        try {
            String value = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString(metaName);
            LogUtils.i("meta-data", metaName + " = " + value);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

}
