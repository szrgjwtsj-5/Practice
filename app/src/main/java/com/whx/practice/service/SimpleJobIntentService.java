package com.whx.practice.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by whx on 2017/11/21.
 */

public class SimpleJobIntentService extends JobIntentService {

    static final int JOB_ID = 2333;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SimpleJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("SimpleJobIntentService", "Executing work: " + intent);

        String label = intent.getStringExtra("label");

        if (label == null) {
            label = intent.toString();
        }
        toast(label);

        for (int i = 0; i< 5; i++) {
            Log.i("SimpleJobIntentService", "Running service " + (i + 1)
                    + "/5 @ " + SystemClock.elapsedRealtime());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i("SimpleJobIntentService", "Completed service @ " + SystemClock.elapsedRealtime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toast("all work complete");
    }

    final Handler mHandler = new Handler();

    void toast(final CharSequence text) {
        mHandler.post(() -> Toast.makeText(SimpleJobIntentService.this, text, Toast.LENGTH_SHORT).show());
    }
}
