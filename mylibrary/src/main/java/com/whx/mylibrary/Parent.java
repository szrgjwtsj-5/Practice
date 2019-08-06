package com.whx.mylibrary;

/**
 * Created by whx on 2018/1/25.
 */

public class Parent {

    private String name;

    public static Parent sayHello(String name) {
        Parent parent = new Parent();
        parent.name = name;

        return parent;
    }

    public void show() {
        System.out.println("fuck " + name);
    }
}
