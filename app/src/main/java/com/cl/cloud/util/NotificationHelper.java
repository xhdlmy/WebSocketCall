package com.cl.cloud.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.cl.cloud.R;
import com.cl.cloud.app.Constant;
import com.cl.cloud.push.PushEntity;
import com.xhd.alive.KeepAliveManager;
import com.xhd.base.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by work2 on 2019/5/20.
 */

public class NotificationHelper {
    
    private static final int NOTIFICATION_ID = 1;
    private static final int NAME_MAX_LEN = 5;

    public static void buildNotifacation(Context context, PushEntity entity) throws ParseException {
        String receiverNum = entity.detail.get(Constant.KEY_RECEIVER);
        String msgSign = entity.detail.get(Constant.KEY_MSG_SIGN);
        String name = entity.detail.get(Constant.KEY_NAME);
        long date = DateUtils.parseDate(entity.respTime, DateUtils.TIMESTAMP_PATTERN).getTime();
        if (name != null && name.length() > NAME_MAX_LEN) name = name.substring(0, NAME_MAX_LEN);
        String prefix = context.getString(R.string.click_to_call);
        String subText = prefix + receiverNum;
        String ticker = prefix + name + " " + receiverNum;
        // PendingIntent 
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + receiverNum));
        PendingIntent contentIntent = PendingIntent.getActivity(context, new Random().nextInt(), intent, 0);
        // Notification
        Notification notification;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            String channelId = "1";
            NotificationChannel channel = new NotificationChannel(channelId, "autocall", NotificationManager.IMPORTANCE_NONE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
            builder.setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setWhen(date)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSubText(subText)
                    .setContentTitle(receiverNum)
                    .setContentText(name)
                    .setContentIntent(contentIntent)
                    .setContentInfo(msgSign)
                    .setTicker(ticker)
                    .setDefaults(Notification.DEFAULT_ALL);
            notification = builder.build();
        }else{
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setWhen(date)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSubText(subText)
                    .setContentTitle(receiverNum)
                    .setContentText(name)
                    .setContentIntent(contentIntent)
                    .setContentInfo(msgSign)
                    .setTicker(ticker)
                    .setDefaults(Notification.DEFAULT_ALL);
            notification = builder.build();
        }
        
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(NOTIFICATION_ID, notification);
    }
    
}
