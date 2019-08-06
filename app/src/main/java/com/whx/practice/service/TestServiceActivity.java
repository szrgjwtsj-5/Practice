package com.whx.practice.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.whx.practice.R;

/**
 * Created by whx on 2017/11/15.
 */

public class TestServiceActivity extends AppCompatActivity{

    private TestService mService;
    private boolean mBound = false;

    private Button mGetNumBtn;
    private TextView mText;

    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);

        mGetNumBtn = (Button) findViewById(R.id.jump);
        mText = (TextView) findViewById(R.id.name);

        intent = new Intent(this, TestService.class);

        mGetNumBtn.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, TestIntentService.class);
            startService(intent1);
        });
    }

    private void bindService() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        } else {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TestService.MyBinder binder = (TestService.MyBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("---------", componentName.getClassName() + " disconnect");
        }
    };
}
