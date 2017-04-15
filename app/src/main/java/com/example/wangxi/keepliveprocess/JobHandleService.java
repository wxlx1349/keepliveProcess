package com.example.wangxi.keepliveprocess;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by wangxi on 2017/4/15.
 */

@SuppressLint("NewApi")
public class JobHandleService extends JobService {

    private int kJobId=0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("INFO", "jobService create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        JobInfo info=getJobInfo();
        Log.e("INFO", "jobService start id="+info.getId());
        scheduleJob(info);
        return START_NOT_STICKY;
    }

    /** Send job to the JobScheduler. */
    public void scheduleJob(JobInfo t) {
        Log.i("INFO", "Scheduling job");
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo(){
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, JobHandleService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);  //设备重启之后你的任务是否还要继续执行
//        builder.setMinimumLatency(3000);// 设置任务运行最少延迟时间
        builder.setRequiresCharging(false); // 设置是否充电的条件,默认false
        builder.setRequiresDeviceIdle(false);  // 设置手机是否空闲的条件,默认false
        builder.setPeriodic(10);//间隔时间--周期
        return builder.build();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("INFO", "job start");
//		scheduleJob(getJobInfo());
        boolean isLocalServiceWork = isServiceWork(this, "com.dn.keepliveprocess.LocalService");
        boolean isRemoteServiceWork = isServiceWork(this, "com.dn.keepliveprocess.RemoteService");
//		Log.i("INFO", "localSericeWork:"+isLocalServiceWork);
//		Log.i("INFO", "remoteSericeWork:"+isRemoteServiceWork);
        if(!isLocalServiceWork||
                !isRemoteServiceWork){
            this.startService(new Intent(this,LocalService.class));
            this.startService(new Intent(this,RemoteService.class));
            Toast.makeText(this, "jobservice process start", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "process stop", Toast.LENGTH_SHORT).show();
        scheduleJob(getJobInfo());
        return true;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
