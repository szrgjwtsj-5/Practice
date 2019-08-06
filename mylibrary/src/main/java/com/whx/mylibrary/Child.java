package com.whx.mylibrary;

/**
 * Created by whx on 2018/1/25.
 */

public class Child extends Parent{
    private String name;

    public static Parent sayHello(String name) {

        Child child = new Child();
        child.name = name;
        return child;
    }

    @Override
    public void show() {
        System.out.println("hello " + name);
    }
}
