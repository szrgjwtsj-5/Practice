package com.whx.practice.service;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobServiceEngine;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by whx on 2017/11/21.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class TestJobServiceEngine extends JobServiceEngine{

    public TestJobServiceEngine(Service service) {
        super(service);
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
