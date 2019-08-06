package com.whx.practice.threadpool;

import android.content.Context;
import android.net.Uri;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by whx on 2017/11/24.
 */

public class MyCursorLoader extends AsyncTaskLoaderFaker<String>{

    @Override
    public String loadInBackground() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    public MyCursorLoader(Context context) {
        super(context);

        try {
            Field myExecutor = this.getClass().getSuperclass().getDeclaredField("mExecutor");

            myExecutor.setAccessible(true);

            ThreadPoolExecutor newExecutor = (ThreadPoolExecutor)myExecutor.get(this);
            newExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

                    //new ThreadPoolExecutor(5, 128, 1, TimeUnit.SECONDS, )
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
