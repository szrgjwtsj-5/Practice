package com.whx.practice.reflect;

import java.lang.reflect.Field;

/**
 * Created by whx on 2017/11/27.
 */

public class ChildClass extends ParentClass{

    public void change() {

//            Field nameField = ParentClass.class.getDeclaredField("hh");
//
//            nameField.setAccessible(true);
//
//            What hh = (What) nameField.get(this);
//            hh.setName("wxh");
        Class<?> clazz = ParentClass.class;

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                System.out.println(field.getInt(this));
            } catch (Exception e) {
                System.out.println("error");
            }
        }

    }

    @Override
    public String getName() {
        return super.getName();
    }

    public static void main(String[] args) {
        ChildClass cc = new ChildClass();
        cc.change();

        //System.out.println(cc.getName());
    }
}
