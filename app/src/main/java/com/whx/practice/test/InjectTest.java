package com.whx.practice.test;

/**
 * Created by whx on 2018/2/2.
 */

public class InjectTest {

    public InjectTest() {
        System.out.println("hhhhh");
    }

    public static void stMethod() {

    }

    public void noparamMethod() {

    }

    public void paramMethod(String str, int num) {

    }

    public String func() {
        return "hello";
    }

    public String paramFunc(String str, int num) {
        return str + num;
    }

    private int addFunc(int a, int b) {
        return a + b;
    }

    public class NonStNested {

    }

    public class StNested {

    }
}
