package com.xhd.base.util.manufacturer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by joker on 2017/8/4.
 */

public class PermissionsPageManager {
    
    private static PermissionsPageManager instance;

    public static PermissionsPageManager getInstances() {
        if (instance == null) {
            synchronized (PermissionsPageManager.class) {
                if(instance == null){
                    instance = new PermissionsPageManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Build.MANUFACTURER
     */
    public static final String MANUFACTURER_HUAWEI = "HUAWEI";
    public static final String MANUFACTURER_XIAOMI = "XIAOMI";
    public static final String MANUFACTURER_OPPO = "OPPO";
    public static final String MANUFACTURER_VIVO = "vivo";
    public static final String MANUFACTURER_MEIZU = "meizu";
    public static final String manufacturer = Build.MANUFACTURER;

    public Intent getIntent(Activity activity) {
        PermissionsPage permissionsPage = new Protogenesis(activity);
        try {
            if (MANUFACTURER_HUAWEI.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new HUAWEI(activity);
            } else if (MANUFACTURER_OPPO.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new OPPO(activity);
            } else if (MANUFACTURER_VIVO.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new VIVO(activity);
            } else if (MANUFACTURER_XIAOMI.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new XIAOMI(activity);
            } else if (MANUFACTURER_MEIZU.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new MEIZU(activity);
            }
            return permissionsPage.settingIntent();
        } catch (Exception e) {
            Log.e("Permissions4M", "手机品牌为：" + manufacturer + "异常抛出" + e.getMessage());
            permissionsPage = new Protogenesis(activity);
            return ((Protogenesis) permissionsPage).settingIntent();
        }
    }

}
