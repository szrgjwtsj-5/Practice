package com.whx.practice.reflect;

import java.lang.reflect.Field;

/**
 * Created by whx on 2018/2/8.
 */

public class ReflectEverything {

    public static void main(String[] args) {
        System.out.println(Math.sin(Math.PI/4));
    }

    private void test() {
        ParentClass goat = new ParentClass();

        Class<?> clazz = goat.getClass();

        try {
            Field field = clazz.getDeclaredField("constNum");

            field.setAccessible(true);

            int old = field.getInt(goat);

            System.out.println(old);

            field.setInt(goat, 250);

            int neww = field.getInt(goat);

            System.out.println(neww);

            ParentClass.What newWhat = goat.new What("wxh", "female");

            Field field1 = clazz.getDeclaredField("hh");
            field1.setAccessible(true);
            System.out.println(field1.get(goat));

            field1.set(goat, newWhat);

            System.out.println(field1.get(goat));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
