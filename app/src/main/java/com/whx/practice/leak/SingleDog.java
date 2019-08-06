package com.whx.practice.leak;

import android.content.Context;

/**
 * Created by whx on 2017/8/31.
 */

public class SingleDog {

    //就是要它泄漏 :-)
    private Context context;

    private static class SingleDogHandler {
        private static SingleDog instance = new SingleDog();
    }
    private SingleDog() {

    }

    public static SingleDog getInstance() {
        return SingleDogHandler.instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
