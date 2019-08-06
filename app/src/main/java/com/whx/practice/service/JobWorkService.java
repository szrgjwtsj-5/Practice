package com.whx.practice.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.whx.practice.R;

/**
 * 处理job中的work示例
 * Created by whx on 2017/11/21.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobWorkService extends JobService{
    private NotificationManager mNM;
    private CommandProcessor mProcessor;

    final class CommandProcessor extends AsyncTask<Void, Void, Void> {      //注意内存泄漏
        private final JobParameters mParams;

        CommandProcessor(JobParameters parameters) {
            mParams = parameters;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean cancelled;
            JobWorkItem work;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //循环遍历job中的work队列
                while (!(cancelled = isCancelled()) && (work = mParams.dequeueWork()) != null) {
                    String txt = work.getIntent().getStringExtra("name");
                    Log.i("JobWorkService", "Processing work: " + work + ", msg: " + txt);

                    showNotification(txt);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    hideNotification();
                    Log.i("JobWorkService", "Done with: " + work);

                    mParams.completeWork(work); //告诉系统已经处理完work
                }

                if (cancelled) {
                    Log.i("JobWorkService", "CANCELLED!");
                }
            }
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Toast.makeText(this, "服务创建", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        hideNotification();
        Toast.makeText(this, "服务销毁", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mProcessor = new CommandProcessor(params);
        mProcessor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mProcessor.cancel(true);

        return true;
    }

    private void showNotification(String txt) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, JobServiceActivity.class), 0);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.leaf)
                .setTicker(txt)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("一条推送")
                .setContentText(txt)
                .setContentIntent(contentIntent);

        builder.setOngoing(true);   //常驻通知栏，除非程序来取消

        mNM.notify(2333, builder.build());
    }

    private void hideNotification() {
        mNM.cancel(2333);
    }
}
