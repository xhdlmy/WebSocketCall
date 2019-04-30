package com.xhd.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static NetworkInfo getNetworkInfo(Context appContext){
        // LeakCanary 检测到不要用 Activity 作为 Context，否则 ConnectivityManager.mContext 会引发内存泄漏
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) return null;
        return connectivityManager.getActiveNetworkInfo();
    }

    public static boolean isNetworkConnected(Context appContext) {
        NetworkInfo networkInfo = getNetworkInfo(appContext);
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static final String TYPE_WIFI = "WIFI";
    public static final String TYPE_MOBILE = "MOBILE";
    public static final String TYPE_UNKNOW = "UNKNOW";
    public static final String NO_NET = "NO_NET";

    public static String getNetworkType(Context appContext) {
        NetworkInfo networkInfo = NetworkUtils.getNetworkInfo(appContext);
        if (networkInfo != null && networkInfo.isAvailable()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase(TYPE_WIFI)) {
                return TYPE_WIFI;
            } else if (type.equalsIgnoreCase(TYPE_MOBILE)) {
                return TYPE_MOBILE;
            } else {
                return TYPE_UNKNOW;
            }
        } else {
            return NO_NET;
        }
    }

}
