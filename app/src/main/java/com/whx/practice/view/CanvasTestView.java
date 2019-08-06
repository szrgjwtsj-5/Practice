package com.whx.practice.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.whx.practice.R;

/**
 * Created by whx on 2017/9/26.
 */

public class CanvasTestView extends View{

    private Paint mPaint, textPaint;
    private Picture mPicture;
    private Context mContext;

    private int mWidth, mHeight;

    private float[] points = {
            500, 500,
            500, 600,
            500, 700
    };
    private float[] lines = {
            100, 100, 200, 200,
            100, 200, 300, 300,
            500, 500, 400, 50
    };

    public CanvasTestView(Context context) {
        this(context, null);
    }
    public CanvasTestView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initPaint();
        initTextPaint();
        //recording();

//        setLayerType(LAYER_TYPE_SOFTWARE,null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
    }

    private void recording() {
        mPicture = new Picture();
        Canvas canvas = mPicture.beginRecording(500, 500);

        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL);

        canvas.translate(250, 250);
        canvas.drawCircle(0, 0, 100, paint);

        mPicture.endRecording();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
    }

    private void initTextPaint() {
        textPaint = new Paint();
        textPaint.setColor(Color.CYAN);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        testClip(canvas);
    }

    private void testClip(Canvas canvas) {


        mPaint.setStyle(Paint.Style.FILL);

        canvas.translate(10, 10);

        canvas.save();

        Path path = new Path();

        path.addRect(new RectF(0, 0, 500, 500), Path.Direction.CW);
        path.addRect(new RectF(50, 50, 250, 250), Path.Direction.CCW);
        path.setFillType(Path.FillType.WINDING);

        canvas.clipPath(path);

        mPaint.setColor(Color.parseColor("#eeeeee"));
        canvas.drawRect(new RectF(0, 0, 500, 500), mPaint);
        canvas.restore();

//        mPaint.setColor(Color.parseColor("#aaaaaa"));
//        canvas.drawCircle(300, 150, 150, mPaint);
    }

    private void testDrawText(Canvas canvas) {
//        String str = "ABCDEFGHIJKLMN";
//
//        canvas.drawText(str, 200, 300, textPaint);

        String str = "SLOOP";

        canvas.drawPosText(str.toCharArray(), 0, 3,new float[]{
                100,100,    // 第一个字符位置
                200,200,    // 第二个字符位置
                300,300,    // ...
                400,400,
                500,500
        }, textPaint);
    }
    private void testBitmap(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.raw.bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), new Paint());
    }
    private void testPicture(Canvas canvas) {
        //mPicture.draw(canvas);
        //canvas.drawPicture(mPicture, new RectF(0, 0, mPicture.getWidth(), 200));

        PictureDrawable drawable = new PictureDrawable(mPicture);
        drawable.setBounds(0, 0, 250, mPicture.getHeight());
        drawable.draw(canvas);
    }
    private void testSave(Canvas canvas) {
        canvas.save();                              //保存最开始的画布状态
        canvas.translate(mWidth/2, mHeight/2);      //变换
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, 300, 200, mPaint);

        canvas.restore();                           //恢复到上一个状态
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, 300, 200, mPaint);
    }
    private void testSkew(Canvas canvas) {
        canvas.translate(mWidth/2, mHeight/2);

        mPaint.setColor(Color.BLACK);
        canvas.drawRect(rectF, mPaint);

        canvas.skew(0, 1);
        canvas.skew(1, 0);

        mPaint.setColor(Color.CYAN);
        canvas.drawRect(rectF, mPaint);
    }
    private void testRotate(Canvas canvas) {
        canvas.translate(mWidth/2, mHeight/2);

//        mPaint.setColor(Color.BLACK);
//        canvas.drawRect(rectF, mPaint);
//
//        canvas.rotate(60, 100, 0);
//
//        mPaint.setColor(Color.CYAN);
//        canvas.drawRect(rectF, mPaint);

        canvas.drawCircle(0, 0, 300, mPaint);
        canvas.drawCircle(0, 0, 250, mPaint);

        for (int i = 0; i <= 360; i += 10) {
            canvas.drawLine(250, 0, 300, 0, mPaint);
            canvas.rotate(10);
        }
    }

    RectF rectF = new RectF(0, -200, 300, 0);
    private void testScale(Canvas canvas) {
        canvas.translate(mWidth/2, mHeight/2);  //将坐标原点移动到画布中心
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(rectF, 0, 90, true, mPaint);

        canvas.scale(0.5f, -0.5f);

        mPaint.setColor(Color.CYAN);
        canvas.drawArc(rectF, 0, 90, true, mPaint);
    }
    private void testTranslate(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(100, 100, 100, mPaint);

        mPaint.setColor(Color.CYAN);
        canvas.translate(300, 300);
        canvas.drawCircle(0, 0, 100, mPaint);

    }
    private void drawLine(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        canvas.drawPoints(points, mPaint);
        canvas.drawLine(10, 10, 10, 500, mPaint);
        canvas.drawLines(lines, mPaint);
    }

    RectF r = new RectF(100, 400, 500, 600);
    RectF rf = new RectF(100, 700, 500, 900);
    private void drawRect(Canvas canvas) {
        canvas.drawRect(100, 100, 300, 300, mPaint);
        canvas.drawRect(r, mPaint);
        canvas.drawRect(rf, mPaint);
    }
    private void drawArc(Canvas canvas) {
        canvas.drawArc(r, 0, 90, false, mPaint);
        canvas.drawArc(rf, 0, 90, true, mPaint);

        canvas.drawOval(rf, mPaint);

        canvas.drawCircle(250, 250, 100, mPaint);
    }
}
