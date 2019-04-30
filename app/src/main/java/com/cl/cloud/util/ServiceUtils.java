package com.cl.cloud.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 1 获取正在运行的服务
 * 2 启动/关闭 服务
 * 3 绑定/解绑 服务
 */
public class ServiceUtils {

    private static List<RunningServiceInfo> getRunningServices(@NonNull Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> info = activityManager.getRunningServices(0x7FFFFFFF);
        return info;
    }
    
    public static List<String> getRunningServiceNames(@NonNull Context context) {
        List<RunningServiceInfo> info = getRunningServices(context);
        List<String> names = new ArrayList<>();
        if (info == null || info.size() == 0) return null;
        for (RunningServiceInfo aInfo : info) {
            names.add(aInfo.service.getClassName());
        }
        return names;
    }
    
    public static boolean isServiceRunning(@NonNull Context context, @NonNull String className) {
        List<RunningServiceInfo> info = getRunningServices(context);
        if (info == null || info.size() == 0) return false;
        for (RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }

    public static void startService(@NonNull Context context, String className) throws ClassNotFoundException {
        startService(context, Class.forName(className));
    }

    public static void startService(@NonNull Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startService(intent);
    }

    public static boolean stopService(@NonNull Context context, String className) throws ClassNotFoundException {
        return stopService(context, Class.forName(className));
    }

    public static boolean stopService(@NonNull Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        return context.stopService(intent);
    }

    public static void bindService(@NonNull Context context, String className, @NonNull ServiceConnection conn, int flags) throws ClassNotFoundException {
        bindService(context, Class.forName(className), conn, flags);
    }

    public static void bindService(@NonNull Context context, String className, @NonNull ServiceConnection conn) throws ClassNotFoundException {
        bindService(context, className, conn, Context.BIND_AUTO_CREATE);
    }

    public static void bindService(@NonNull Context context, Class<?> cls, @NonNull ServiceConnection conn, int flags) {
        Intent intent = new Intent(context, cls);
        context.bindService(intent, conn, flags);
    }

    public static void unbindService(@NonNull Context context, @NonNull ServiceConnection conn) {
        context.unbindService(conn);
    }

}
