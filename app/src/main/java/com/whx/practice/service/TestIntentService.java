package com.whx.practice.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by whx on 2017/11/16.
 */

public class TestIntentService extends IntentService{

    public TestIntentService() {
        super("TestIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int res = 0;
        try {
            while (res < 5) {
                res++;

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("--------------", res+"");
    }
}
