package com.whx.practice.leak;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.whx.practice.R;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;

/**
 * Created by whx on 2017/8/31.
 */

public class LeakActivity extends AppCompatActivity{

    private TextView textView;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);

        textView = (TextView) findViewById(R.id.text);
        mHandler = new MyHandler(this);

        testThread();

        try {
            byte[] buff = new byte[1024];
            FileInputStream inputStream = new FileInputStream(new File("xxx"));
            //
            StringBuilder sb = new StringBuilder();
            while (inputStream.read(buff) != 0) {
                sb.append(new String(buff));
            }
            textView.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testSingle() {
        SingleDog singleDog = SingleDog.getInstance();
        singleDog.setContext(this);
    }

    private void testThread() {
        new Thread(new MyRunnable(this)).start();
    }

    private static class MyRunnable implements Runnable {
        private WeakReference<LeakActivity> reference;

        MyRunnable(LeakActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            if (reference.get() != null) {
                reference.get().mHandler.sendEmptyMessage(233);
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (reference.get() != null) {
                reference.get().mHandler.sendEmptyMessage(666);
            }
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<LeakActivity> reference;

        MyHandler(LeakActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 233) {
                if (reference.get() != null) {
                    reference.get().textView.setText("test handler");
                }
            } else if(msg.what == 666) {
                if (reference.get() != null) {
                    reference.get().textView.setText("what the f**k");
                }
            }
            super.handleMessage(msg);
        }
    }
}
