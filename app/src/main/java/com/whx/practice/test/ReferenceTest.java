package com.whx.practice.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by whx on 2017/8/31.
 */

public class ReferenceTest {

    public static void main(String[] args) {

//        File file = new File("/Users/whx/Desktop/test.txt");
//
//        StringBuilder sb = new StringBuilder();
//
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//
//            String res = sb.toString();
//
//            sb = new StringBuilder(res.replaceAll("\n", ""));
//
//            System.out.println(sb.toString());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static void testSoft(People p) {
        SoftReference<People> softReference = new SoftReference<>(p);

        System.out.println("soft --" + softReference.get());

        p = null;

        System.out.println("soft " + softReference.get());
        System.gc();

        try {
            while (softReference.get() != null) {
                System.out.println("soft " + softReference.get());
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void testWeak() {
        People.Builder builder = new People.Builder();
        People p = builder.setName("wang")
                .setGender("man")
                .build();

        WeakReference<People> weakReference = new WeakReference<>(p);

        System.out.println("weak --" + weakReference.get());

        p = null;

        System.out.println("weak --" + weakReference.get());
        System.gc();

        try {
            while (weakReference.get() != null) {
                System.out.println("weak" + weakReference.get());
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testPhantom(People p) {

        ReferenceQueue<People> referenceQueue = new ReferenceQueue<>();

        PhantomReference<People> phantomReference = new PhantomReference<>(p, referenceQueue);
        System.out.println("phantom --" + phantomReference.get());
        System.out.println(referenceQueue.poll());

        p = null;
        System.out.println("phantom --" + phantomReference.get());
        System.gc();

        try {
            while (phantomReference.get() != null) {
                System.out.println("phantom " + phantomReference.get());
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
