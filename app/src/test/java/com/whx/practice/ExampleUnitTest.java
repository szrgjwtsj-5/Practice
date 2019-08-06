package com.whx.practice;

import com.whx.practice.threadpool.ModernAsyncTaskFaker;
import com.whx.practice.threadpool.MyCursorLoader;

import org.junit.Test;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() throws Exception{

//        String mobilePatten = "^1([34578])\\d{9}";
//
//        String testStr = "140123a5678";
//
//        Pattern p = Pattern.compile(mobilePatten);
//        Matcher m = p.matcher(testStr);
//
//        System.out.println(m.matches());

//        MyCursorLoader loader = new MyCursorLoader(MyApplication.getAppContext());
//        loader.startLoading();
    }

    private class SayHelloRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("fuck world");
            }
        }
    }
}