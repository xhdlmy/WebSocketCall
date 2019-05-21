package com.cl.cloud.util;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.internal.telephony.ITelephony;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.push.PushEntity;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Random;

/**
 * Created by work2 on 2019/5/20.
 */

public class TelePhonyHelper {

    public static void dealCall(PushEntity entity){
        boolean isInCall = TelePhonyHelper.phoneIsBusy(App.getAppContext());
        if(isInCall){
            // 发送通知
            try {
                NotificationHelper.buildNotifacation(App.getAppContext(), entity);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            // 自动拨号
            autoCall(App.getAppContext(), entity.detail.get(Constant.KEY_RECEIVER));
        }
    }

    public static void dealSms(PushEntity entity){
        autoSendSms(App.getAppContext(), entity.detail.get(Constant.KEY_RECEIVER), entity.detail.get(Constant.KEY_MSG_SIGN));
    }

    public static void autoSendSms(Context context, String phone, String msgContent){
        Uri smsToUri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", msgContent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void autoCall(Context context, String phone){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 判断是否在通话中
    public static boolean phoneIsBusy(Context context) {
        boolean isBusy = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                isBusy = true;
            }else{
                isBusy = tm == null || tm.isInCall();
            }
        } else {
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            Class<TelephonyManager> c = TelephonyManager.class;
            Method getITelephonyMethod;
            try {
                getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
                getITelephonyMethod.setAccessible(true);
                ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null);
                isBusy = !iTelephony.isIdle();
            } catch (Exception e) {
                isBusy = true;
            }
        }
        return isBusy;
    }

}
