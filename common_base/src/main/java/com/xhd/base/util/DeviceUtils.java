package com.xhd.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

/**
 * Build 获取
 * 1 获取安卓系统版本、设备型号、设备GUID
 * 2 获取厂家生产 MAC 地址，厂家信息
 * 3 设备关机/重启
 */
public class DeviceUtils {

    /**
     * 获取设备安卓系统版本号
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }


    /**
     * 获取设备型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 获取设备 UUID 由于在算法中使用了MAC地址，所以唯一
     */
    public static String getGuid(){
        return UUID.randomUUID().toString();
    }

    /**
     * 获取设备 AndroidID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID(@NonNull Context appContext) {
        return Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备厂商
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 关机
     * <p>需要 root 权限或者系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     */
    public static void shutdown(@NonNull Context appContext) {
        ShellUtils.execCmd("reboot -p", true);
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
    }

    /**
     * 重启
     * <p>需要 root 权限或者系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     */
    public static void reboot(@NonNull Context appContext) {
        ShellUtils.execCmd("reboot", true);
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        appContext.sendBroadcast(intent);
    }

    /**
     * 重启
     * @param reason 传递给内核来请求特殊的引导模式，如"recovery"
     */
    public static void reboot(@NonNull Context appContext, String reason) {
        PowerManager mPowerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        try {
            mPowerManager.reboot(reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启到 recovery
     */
    public static void reboot2Recovery() {
        ShellUtils.execCmd("reboot recovery", true);
    }

    /**
     * 重启到 bootloader
     */
    public static void reboot2Bootloader() {
        ShellUtils.execCmd("reboot bootloader", true);
    }

    /**
     * 判断设备是否 root
     */
    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

}

final class ShellUtils {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private ShellUtils() {}

    /**
     * 返回的命令结果
     */
    public static class CommandResult {
        /**
         * 结果码
         **/
        public int    result;
        /**
         * 成功信息
         **/
        public String successMsg;
        /**
         * 错误信息
         **/
        public String errorMsg;

        public CommandResult(final int result, final String successMsg, final String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }

    public static CommandResult execCmd(final String[] commands,
                                        final boolean isRoot,
                                        final boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? "su" : "sh");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) continue;
                os.write(command.getBytes());
                os.writeBytes(LINE_SEP);
                os.flush();
            }
            os.writeBytes("exit" + LINE_SEP);
            os.flush();
            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream(),
                        "UTF-8"));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream(),
                        "UTF-8"));
                String line;
                if ((line = successResult.readLine()) != null) {
                    successMsg.append(line);
                    while ((line = successResult.readLine()) != null) {
                        successMsg.append(LINE_SEP).append(line);
                    }
                }
                if ((line = errorResult.readLine()) != null) {
                    errorMsg.append(line);
                    while ((line = errorResult.readLine()) != null) {
                        errorMsg.append(LINE_SEP).append(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                successResult.close();
                errorResult.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(
                result,
                successMsg == null ? null : successMsg.toString(),
                errorMsg == null ? null : errorMsg.toString()
        );
    }

    public static CommandResult execCmd(final String command,
                                        final boolean isRoot,
                                        final boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, isRoot, isNeedResultMsg);
    }

    public static CommandResult execCmd(final List<String> commands,
                                        final boolean isRoot,
                                        final boolean isNeedResultMsg) {
        return execCmd(commands == null ? null : commands.toArray(new String[]{}),
                isRoot,
                isNeedResultMsg);
    }

    public static CommandResult execCmd(final String[] commands, final boolean isRoot) {
        return execCmd(commands, isRoot, true);
    }

    public static CommandResult execCmd(final String command, final boolean isRoot) {
        return execCmd(new String[]{command}, isRoot, true);
    }

    public static CommandResult execCmd(final List<String> commands, final boolean isRoot) {
        return execCmd(commands == null ? null : commands.toArray(new String[]{}), isRoot, true);
    }

}
