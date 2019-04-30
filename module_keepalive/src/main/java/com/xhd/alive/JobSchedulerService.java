package com.xhd.alive;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.xhd.base.util.LogUtils;
import com.xhd.base.util.ServiceUtils;

import java.util.concurrent.Executors;

/*
    Android5.0 以后系统对 Native 进程等加强了管理

    Android5.0 以后
    由系统统一管理和调度
    JobScheduler是framework层里用来安排各种将要执行在app自己进程里的任务的机制。
    我们需要创建各种Job的描述类JobInfo，并且通过JobScheduler传递给系统。
    当我们描述的条件或者标准满足了，系统将执行app的JobService。

    Jobscheduler 通过系统调度 JobService 运行

 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    public static final int MILLIS_30 = 30 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        startJobSheduler();
    }

    public void startJobSheduler() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            try {
                // 1. 创建 JobInfo
                int jobid = 1;
                JobInfo.Builder builder = new JobInfo.Builder(jobid, new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
                // Android 7.0 最小时间间隔为 15min （不可通过设置 setPeriodic）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setMinimumLatency(MILLIS_30); //执行的最小延迟时间
                    builder.setOverrideDeadline(MILLIS_30);  //执行的最长延时时间
                    builder.setBackoffCriteria(MILLIS_30, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案(增长策略)
                } else {
                    builder.setPeriodic(MILLIS_30);
                }
                builder.setPersisted(true);
                JobInfo jobInfo = builder.build();
                // 2. 获取 JobScheduler
                JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler == null) return;
                // 3. 将任务交由系统去调度
                int errorCode = jobScheduler.schedule(jobInfo);
                LogUtils.i(JobSchedulerService.class.getSimpleName(), "jobScheduler errorCode:" + errorCode);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static final String TAG = JobSchedulerService.class.getSimpleName();

    // 耗时任务
    private void doJob(final JobParameters params){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // 子线程执行耗时操作
                jobFinished(params, false);
            }
        });
    }

    // 瞬时任务
    private void doJob(){
        if(!ServiceUtils.isServiceRunning(this, KeepAliveService.class.getName())){
            Intent intent = new Intent(this, KeepAliveService.class);
            startService(intent);
        }

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        //代表是耗时操作（注意需要在子线程去完成，不然UI线程堵塞），然后在job任务完成后，需要手动调用jobFinished(params,false) ,参数2代表是否重复执行任务
//        doJob(params);
//        return true;
        LogUtils.i(TAG, "onStartJob");
        doJob();
        return false; // 说明任务已经执行完毕了，相当于调用了 jobFinished(params)
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        /*
        在中断任务时调用，例如用户需要服务在有充电时才运行，如果在调用JobFinished()之前（任务完成之前）充电器拔掉，
        onStopJob(...) 方法就会被调用，也就是说，一切任务就立即停止了。
        返回 true 表示：“任务应该计划在下次继续。”
        返回 false 表示：“事情就到此结束吧，不要计划下次了。”
        */
        LogUtils.i(TAG, "onStopJob");
        return false;
    }

}
