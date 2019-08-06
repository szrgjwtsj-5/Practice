package com.whx.practice.test;

/**
 * Created by whx on 2017/8/31.
 */

public class People {

    private String name;
    private String gender;
    private int age;

    public People() {

    }
    private People(Builder builder) {
        this.name = builder.name;
        this.gender = builder.gender;
        this.age = builder.age;
    }

    public static class Builder {
        private String name;
        private String gender;
        private int age;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public People build() {
            return new People(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "name: " + name + " gender: " + gender + " age: " + age;
    }
}
