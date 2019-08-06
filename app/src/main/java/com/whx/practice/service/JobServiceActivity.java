package com.whx.practice.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.whx.practice.R;

/**
 * Created by whx on 2017/11/15.
 */

public class JobServiceActivity extends AppCompatActivity{

    private Button start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);

        start = findViewById(R.id.jump);
        start.setOnClickListener(view -> startSchedule());
    }

    private void startSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

            JobInfo jobInfo = new JobInfo.Builder(233, new ComponentName(getPackageName(), TestJobService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();

            jobScheduler.schedule(jobInfo);
        }
    }
}
