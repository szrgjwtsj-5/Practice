package com.whx.practice.reflect;

/**
 * Created by whx on 2017/11/27.
 */

public class ParentClass {

    private final What hh = new What("whx", "male");

    private static String TAG = "hhhhhh";

    public static final String LIU = "666666";

    private int num;

    public float rate = 0.5f;

    private final int constNum = 233;

    public String getName() {
        return hh.getName();
    }

    class What {
        private String name;
        private String sex;

        What(String name, String sex) {
            this.name = name;
            this.sex = sex;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        @Override
        public String toString() {
            return "name is " + name + ", sex is " + sex;
        }
    }
}
