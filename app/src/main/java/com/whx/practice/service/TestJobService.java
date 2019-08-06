package com.whx.practice.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 *
 * Created by whx on 2017/11/15.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TestJobService extends JobService{

    private Handler mHandler = new MyHandler(this);

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Message message = Message.obtain();
        message.obj = jobParameters;
        mHandler.sendMessage(message);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mHandler.removeCallbacksAndMessages(null);
        return false;
    }

    static class MyHandler extends Handler {

        private WeakReference<TestJobService> reference;

        MyHandler(TestJobService service) {
            reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            TestJobService service = reference.get();
            if (service != null) {
                Toast.makeText(service, "hhhhhhhhhhhh", Toast.LENGTH_SHORT).show();
                JobParameters parameters = (JobParameters) msg.obj;
                service.jobFinished(parameters, true);

                Intent intent = new Intent(service, JobServiceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                service.startActivity(intent);
            }
        }
    }
}
