package com.whx.practice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by whx on 2017/11/13.
 */

public class TestService extends Service{

    private final IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public TestService getService() {
            return TestService.this;
        }
    }

    public int getRandomNum() {
        return (int) (Math.random() * 20);
    }
}
