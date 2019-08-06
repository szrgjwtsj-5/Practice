package com.whx.practice.view.model;

/**
 * Created by whx on 2017/9/27.
 */

public class PieData {
    public String name;        //名称
    public float value;        //数值
    public float percentage;   //百分比

    private int color;          //颜色
    private float angle;        //角度

    public PieData(){}

    public PieData(String name, float value) {
        this.name = name;
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
