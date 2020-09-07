package com.whx.practice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

//import com.squareup.leakcanary.LeakCanary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by whx on 2017/8/31.
 */

public class MyApplication extends Application{

    private static Application instance;
    private static int mPID = android.os.Process.myPid();

    // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
    // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
    // cmds = "logcat -s way";//打印标签过滤信息
    private static String cmd = "logcat *:e *:w | grep \"(" + mPID + ")\"";

    public static Application getInstance() {
        return instance;
    }

    private LogDumper logDumper;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
//        LeakCanary.install(this);

//        logDumper = new LogDumper();
//        logDumper.start();

        registerActivityLifecycleCallbacks(new ActivityLifeCallback());
    }

    private class ActivityLifeCallback implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.e("-----------", activity.getLocalClassName() + "  destroy");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }
    }
    public static class LogDumper extends Thread {
        private Process process;
        private FileOutputStream out;
        private boolean mRunning;
        String fileName = dic() + "/logcat.txt";
        File file = new File(fileName);

        public LogDumper() {

            File fileDic = new File(dic());
            if (!fileDic.exists()) {
                fileDic.mkdir();
            }
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
                process = Runtime.getRuntime().exec(cmd);

                out = new FileOutputStream(file);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);

                String line;

                while (mRunning && (line = bufferedReader.readLine()) != null) {
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.contains(String.valueOf(mPID))) {
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                    process = null;
                }
                if (out != null) {
                    try {
                        out.close();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }

        @Override
        public synchronized void start() {
            super.start();
            mRunning = true;
        }

        public void stopLog() {
            mRunning = false;
        }
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static String dic() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_log";
        } else {
            return instance.getFilesDir().getAbsolutePath() + File.separator + "test_log";
        }
    }

    @Override
    public void onTerminate() {
        logDumper.stopLog();
        super.onTerminate();
    }
}
