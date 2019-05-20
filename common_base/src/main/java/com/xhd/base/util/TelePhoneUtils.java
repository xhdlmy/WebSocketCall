/*
 * © [2013-5-13] by taichenda CO.,LTD.
 * @title TelephoneStatuesCheck.java 
 * @package com.tcd.lbs.util
 * @author David [QQ:375767588] 
 * @update 修改时间 2013-5-13 上午9:26:56
 * @version V1.0 
 */
package com.xhd.base.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xhd.base.adapter.BaseRecyclerAdapter.TAG;

public class TelePhoneUtils {

    @SuppressLint("HardwareIds")
    public static String getIMSI(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm == null) return "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return tm.getSubscriberId();
    }

    @SuppressLint("HardwareIds")
    public static String getIMEI(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm == null) return "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return tm.getDeviceId();

    }

    @SuppressLint("HardwareIds")
    public static String getPhoneNum(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm == null) return "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String phone = tm.getLine1Number();
        return remove86Prefix(phone);
    }

    public static String replaceXXXX(@NonNull String remove86Phone){
        String sub1 = remove86Phone.substring(0, 3);
        String sub2 = "****";
        String sub3 = remove86Phone.substring(7, 11);
        return sub1 + sub2 + sub3;
    }

    public static String remove86Prefix(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return phoneNum;
        } else {
            phoneNum = phoneNum.replaceAll("-", "");
            phoneNum = phoneNum.replaceAll(" ", "");
            if (phoneNum.startsWith("+86")) {
                phoneNum = phoneNum.substring(3);
            }
            if (phoneNum.startsWith("86")) {
                phoneNum = phoneNum.substring(2);
            }
            return phoneNum.trim();
        }
    }

	/**
	 * 手机号码: 需不定期更新
	 * 13[0-9], 14[5,7, 9], 15[0, 1, 2, 3, 5, 6, 7, 8, 9], 17[0-9], 18[0-9]
	 * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
	 * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
	 * 电信号段: 133,149,153,170,173,177,180,181,189
	 */
	public static final String REG_MOBILE = "^1(3[0-9]|4[579]|5[0-35-9]|7[0-9]|8[0-9])\\d{8}$";
	public static final String REG_MOBILE_LOOSE  = "1[3|4|5|7|8|]\\d{9}";
	// 中国移动：China Move
	public static final String REG_CM = "^1(3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-478])\\d{8}$";
	// 中国联通：China Unicom
	public static final String REG_CU = "^1(3[0-2]|4[5]|5[56]|7[0156]|8[56])\\d{8}$";
	// 中国电信：China Telecom
	public static final String REG_CT = "^1(3[3]|4[9]|53|7[037]|8[019])\\d{8}$";

	// 判断是否为手机号码
	public static boolean isPhoneNum(@NonNull String phone) {
        if(TextUtils.isEmpty(phone)) return false;
		Pattern p = Pattern.compile(REG_MOBILE_LOOSE);
		Matcher m = p.matcher(phone);
		return m.matches();
	}

	// 检查手机SIM卡的状态
	public static boolean isCardAvailable(@NonNull Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
	}

    public enum SIMStatue {

        SIM_STATE_UNKNOWN(1),
        SIM_STATE_ABSENT(2), // 无卡
        SIM_STATE_PIN_REQUIRED(3), // 需要PIN解锁
        SIM_STATE_PUK_REQUIRED(4), // 需要PUK解锁
        SIM_STATE_NETWORK_LOCKED(5), // 需要NetworkPIN解锁
        SIM_STATE_READY(6); // 良好

        private int index;

        SIMStatue(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        };

    }

    public static SIMStatue getSIMState(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务

        switch (tm.getSimState()) { // getSimState()取得sim的状态 有下面6中状态
            case TelephonyManager.SIM_STATE_ABSENT:
                return SIMStatue.SIM_STATE_ABSENT;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                return SIMStatue.SIM_STATE_UNKNOWN;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                return SIMStatue.SIM_STATE_NETWORK_LOCKED;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                return SIMStatue.SIM_STATE_PIN_REQUIRED;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                return SIMStatue.SIM_STATE_PUK_REQUIRED;
            case TelephonyManager.SIM_STATE_READY:
                return SIMStatue.SIM_STATE_READY;
        }
        return SIMStatue.SIM_STATE_UNKNOWN;
    }

}
