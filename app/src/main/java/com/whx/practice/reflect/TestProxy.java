package com.whx.practice.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by whx on 2017/11/27.
 */
interface Animal {
    void say(String name, String word);
}

class Cat implements Animal {
    @Override
    public void say(String name, String word) {
        System.out.println("I'm a " + name + " I say " + word);
    }
}

class MyInvocationHandler implements InvocationHandler {
    private Object obj = null;

    public Object bind(Object obj) {
        this.obj = obj;

        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.obj, args);
    }
}

public class TestProxy {

    public static void main(String[] args) {
        MyInvocationHandler demo = new MyInvocationHandler();

        Animal animal = (Animal) demo.bind(new Cat());
        animal.say("cat", "meow");
    }
}
