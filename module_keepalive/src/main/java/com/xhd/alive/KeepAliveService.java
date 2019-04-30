package com.xhd.alive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/*
Android Terminal(终端) 查看进程PID 和 优先级 oom_adj

    命令 adb shell
    ps
    ps|grep com.xhd.alive (筛选)

    信息
    USER    进程当前用户；
    PID     进程ID；
    PPID    父进程ID；
    NAME    进程名；

    查看优先级
    cat proc/{PID}/oom_adj
 */

/*
    前台进程：0
    1、拥有正在“前台”运行的 Service（服务已调用 startForeground()）    √ NotificationId 去除用户感知

    可见进程：1
    1、拥有不在前台、但仍对用户可见的 Activity（已调用 onPause()）   √ OnePixelActivity  在熄灭屏幕时，如果没有 startForeground 那么可将进程优先级从4提高到0

    服务进程:2
    1、单纯 startService()

    后台进程：3
    空闲进程：4

 */

/*
    拉活方案：
    1. START_STICKY
        局限性：Service 第一次被异常杀死后会在5秒内重启，第二次被杀死会在10秒内重启，一旦在短时间内 Service 被杀死达到5次，则系统不再拉起
    2. 接收系统广播（备用方案，暂不实现）
        局限性：只能保证发生系统广播时拉活进程，但无法保证进程挂掉后立即拉活。
 */

public class KeepAliveService extends Service {

    private static final String TAG = KeepAliveService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        KeepAliveManager.getInstance().registerScreenReceiver(this);
        KeepAliveManager.getInstance().startServiceForeground(this);
        // TODO 进程主线程中 WebSocket 的连接工作
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand"); // 可能被多次调用
        return START_STICKY;
        /*
        START_STICKY 不能完全保证拉活       Log 中出现 Scheduling restart of crashed service 可能拉活，也可能拉不活 华为测试机
        假如成功拉活后， onStartCommand 可能会调用多次
         */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeepAliveManager.getInstance().unregisterScreenReceiver(this);
        KeepAliveManager.getInstance().stopServiceForeground();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
