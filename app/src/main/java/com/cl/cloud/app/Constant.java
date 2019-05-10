package com.cl.cloud.app;

import android.Manifest;

public class Constant {

    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS;

    public static final String WS_URL = "ws://192.168.1.108:18888";
    public static final long PING_INTERVAL_MILLIS = 30 * 1000; // ping 操作的时间间隔
    public static final long HINT_NO_NET_MILLIS = 10 * 1000; // 提示无网络时间间隔

    public static final String UTF_8 = "UTF-8";

    public static final int PROTOCOL_VERSION = 1;
    public static final int CONNECT_LEN = 156;
    public static final byte OS_WEB_JS = 0;
    public static final byte OS_APP = 1;
    public static final String CHANNEL = "jh001";
    public static final String PROTOCOL_KEY = "CL2015091401";
    public static final String BASE_ACTION = "com.cl.action.base_";

    public static final String EXTRA_DETAIL = "detail";

    public static final int STATUS_SUCCESS = 1;
}
