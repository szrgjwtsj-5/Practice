package com.whx.practice.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/29.
 */

public class CheckView extends View{

    private Context mContext;
    private enum Anim {
        NULL,
        CHECK,
        UN_CHECK
    }

    private int mWidth, mHeight;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Handler handler;

    private int animCurrentPage = -1;
    private int animMaxPage = 13;
    private int duration = 500;
    private Anim state = Anim.NULL;

    private boolean isCheck = false;

    private Rect src, dst;

    public CheckView(Context context) {
        this(context, null);
    }
    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mPaint = new Paint();
        mPaint.setColor(0xFFFF5317);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.checkmark);

        src = new Rect();
        dst = new Rect(-200, -200, 200, 200);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (animCurrentPage < animMaxPage && animCurrentPage >= 0) {
                    invalidate();
                    switch (state) {
                        case NULL:
                            return;
                        case CHECK:
                            animCurrentPage ++;
                            break;
                        case UN_CHECK:
                            animCurrentPage --;
                            break;
                    }
                    sendEmptyMessageDelayed(0, duration / animMaxPage);
                    Log.e("CheckView------", "count = " + animCurrentPage);
                } else {
                    if (isCheck) {
                        animCurrentPage = animMaxPage - 1;
                    } else {
                        animCurrentPage = -1;
                    }
                    invalidate();
                    state = Anim.NULL;
                }
            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth/2, mHeight/2);

        canvas.drawCircle(0, 0, 250, mPaint);       //绘制背景

        int sideLength = mBitmap.getHeight();
        src.set(sideLength * animCurrentPage, 0, sideLength * (animCurrentPage + 1), sideLength);

        canvas.drawBitmap(mBitmap, src, dst, null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isCheck)
                unCheck();
            else
                check();
        }
        return true;
    }

    public void check() {
        if (state != Anim.NULL || isCheck)
            return;
        state = Anim.CHECK;
        isCheck = true;

        animCurrentPage = 0;
        handler.sendEmptyMessageDelayed(0, duration/animMaxPage);
    }

    public void unCheck() {
        if (state != Anim.NULL || !isCheck)
            return;
        state = Anim.UN_CHECK;
        isCheck = false;

        animCurrentPage = animMaxPage - 1;
        handler.sendEmptyMessageDelayed(0, duration/animMaxPage);
    }

    public void setDuration(int duration) {
        if (duration <= 0)
            return;
        this.duration = duration;
    }

    public void setBgColor(int color) {
        mPaint.setColor(color);
    }
}
