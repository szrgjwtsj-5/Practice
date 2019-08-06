package com.whx.practice.reflect;

/**
 * Created by whx on 2017/11/28.
 */
interface Fruit {
    void eat();
}
class Apple implements Fruit {
    @Override
    public void eat() {
        System.out.println("eat apple");
    }
}

class Orange implements Fruit {
    @Override
    public void eat() {
        System.out.println("eat orange");
    }
}

class Factory {
    public static Fruit getInstance(String className) {
        Fruit fruit = null;

        try {
            fruit = (Fruit) Class.forName(className).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fruit;
    }
}

public class TestFactory {
    public static void main(String[] args) {
        Fruit f = Factory.getInstance("com.whx.practice.reflect.Apple");

        if (f != null) {
            f.eat();
        }
    }
}
