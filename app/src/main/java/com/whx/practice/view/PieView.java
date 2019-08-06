package com.whx.practice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.whx.practice.view.model.PieData;

import java.util.Collections;
import java.util.List;

/**
 * Created by whx on 2017/9/27.
 */

public class PieView extends View{

    //颜色表
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};

    private float mStartAngle = 0;

    private List<PieData> mData;

    private RectF mRF;

    private Paint mPaint;

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float mRadius = Math.min(w, h) / 2 * 0.8f;

        mRF = new RectF(0, 0, mRadius + mRadius, mRadius + mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mData == null)
            return;
        float currentStartAngle = mStartAngle;

        for (PieData pieData : mData) {
            mPaint.setColor(pieData.getColor());
            canvas.drawArc(mRF, currentStartAngle, pieData.getAngle(), true, mPaint);
            currentStartAngle += pieData.getAngle();
        }
    }

    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }

    public void setData(List<PieData> data) {
        mData = data;
        initData();
        invalidate();
    }

    private void initData() {
        if (mData == null || mData.size() == 0)
            return;

        Collections.sort(mData, (pie1, pie2) -> pie1.value - pie2.value > 0 ? 1 : -1); //排序

        float sumValue = 0;
        for (int i = 0; i < mData.size(); i++) {
            PieData pieData = mData.get(i);
            sumValue += pieData.value;

            int j = i % mColors.length;
            pieData.setColor(mColors[j]);   // 设置颜色
        }

        for (PieData pieData : mData) {
            float percentage = pieData.value / sumValue;
            float angle = percentage * 360;

            pieData.percentage = percentage;
            pieData.setAngle(angle);        //设置角度
        }
    }
}
