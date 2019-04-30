package com.xhd.base.util;

import android.support.annotation.NonNull;
import android.util.Log;

public class LogUtils {
    // 日志输出固定 TAG
    @NonNull
    private static String mTag = "App";
    // 日志输出级别 NONE ~ ALL
    public static final int LEVEL_OFF = 0;
    public static final int LEVEL_VERBOSE = 1;
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_WARN = 4;
    public static final int LEVEL_ERROR = 5;
    public static final int LEVEL_ALL = 6;
    // 是否允许输出 LOG (根据读取 build.gradle 是发布版本 or 开发测试版本 判断)
//    private static int mDebuggable = BuildConfig.LOG_DEBUG ? LEVEL_ALL : LEVEL_OFF;
    private static int mDebuggable = LEVEL_ALL;

    /**---------------日志输出,已固定TAG  begin---------------**/
    public static void v(String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, msg);
        }
    }
    public static void d(String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, msg);
        }
    }
    public static void i(String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, msg);
        }
    }
    public static void w(String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, msg);
        }
    }
    public static void e(String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, msg);
        }
    }
    /**---------------日志输出,已固定TAG  end---------------**/

    /**---------------日志输出,未固定TAG  begin---------------**/
    public static void v(String tag, String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }
    public static void d(String tag, String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }
    public static void w(String tag, String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, msg);
        }
    }
    public static void e(String tag, String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }
    /**---------------日志输出,未固定TAG  end---------------**/
}