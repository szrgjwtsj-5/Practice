package com.whx.practice.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.whx.practice.R;

/**
 * Created by whx on 2017/11/6.
 */

public class PathTestView extends View{

    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private Matrix matrix;

    private float[] pos;
    private float[] tan;
    private float currentValue = 0;
    private int mHeight, mWidth;
    private boolean started = false;

    ValueAnimator animator;

    public PathTestView(Context context) {
        this(context, null);
    }
    public PathTestView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.CYAN);

        mPath = new Path();

        pos = new float[2];
        tan = new float[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow, options);
        matrix = new Matrix();

        path.addCircle(0,0, 200, Path.Direction.CW);

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            currentValue = (Float) valueAnimator.getAnimatedValue();    // 计算当前的位置在总长度上的比例[0,1]

            if(currentValue >=1) {
                currentValue = 0;
            }

            invalidate();
        });

        setOnClickListener(v -> {
            if (started) {
                animator.cancel();
                started = false;
            } else {
                animator.start();
                started = true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHeight = h;
        mWidth = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth/2, mHeight/2);
        testFish(canvas);

//        searchAnim(canvas);
    }

    private void testMoveTo(Canvas canvas) {

        mPath.reset();
        mPath.lineTo(200, 200);
        mPath.moveTo(200, 100);
        mPath.lineTo(200, 0);

        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    private void testSetLastPoint(Canvas canvas) {

        mPath.lineTo(200, 200);
        mPath.setLastPoint(200, 100);
        mPath.lineTo(200, 0);

        canvas.drawPath(mPath, mPaint);
    }

    private void testDirection(Canvas canvas) {
//        mPath.reset();

        mPath.addRect(-200, 200, 200, -200, Path.Direction.CCW);
        //mPath.setLastPoint(-300, 300);

        canvas.drawPath(mPath, mPaint);
    }

    private void testAddPath(Canvas canvas) {
        canvas.scale(1, -1);        //y 轴翻转

        mPath.reset();
        Path src = new Path();

        mPath.addRect(-200, -200, 200, 200, Path.Direction.CW);
        src.addCircle(0, 0, 100, Path.Direction.CW);

        mPath.addPath(src, 0, 200);     //将src 平移之后添加
        canvas.drawPath(mPath, mPaint);
    }

    private void testAddArc(Canvas canvas) {
        canvas.scale(1, -1);        //y 轴翻转

        mPath.reset();
        mPath.lineTo(100, 100);

        RectF rect = new RectF(0, 0, 300, 300);
//        mPath.addArc(rect, 0, 270);
//        mPath.arcTo(rect, 0, 270);
        mPath.arcTo(rect, 0, 270, false);

        canvas.drawPath(mPath, mPaint);
    }

    // Path 的集合操作
    private void testFish(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);

        Path path1 = new Path();
        Path path2 = new Path();
        Path path3 = new Path();
        Path path4 = new Path();

        path1.addCircle(0, 0, 200, Path.Direction.CW);
        path2.addRect(0, -200, 200, 200, Path.Direction.CW);
        path3.addCircle(0, -100, 100, Path.Direction.CW);
        path4.addCircle(0, 100, 100, Path.Direction.CCW);


        path1.op(path2, Path.Op.DIFFERENCE);
        path1.op(path3, Path.Op.UNION);
        path1.op(path4, Path.Op.DIFFERENCE);

        path1.computeBounds(new RectF(), true);
        canvas.drawPath(path1, mPaint);
    }

    private void testPathMeasure(Canvas canvas) {

        // 这段是 getLength
        /*
        Path path = new Path();
        path.lineTo(0, 200);
        path.lineTo(200, 200);
        path.lineTo(200, 0);

        PathMeasure measure = new PathMeasure(path, false);
        Log.e("path length = ", "" + measure.getLength());

        PathMeasure measure1 = new PathMeasure(path, true);
        Log.e("path length = ", "" + measure1.getLength());

        canvas.drawPath(path, mPaint);
        */

        // 这段是 getSegment
        /*
        canvas.translate(mWidth/2, mHeight/2);

        Path path = new Path();
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);

//        canvas.drawPath(path, mPaint);

        Path dst = new Path();
        dst.lineTo(-300, -300);

        PathMeasure measure = new PathMeasure(path, false);
        measure.getSegment(200, 600, dst, false);

        mPaint.setColor(Color.RED);
        canvas.drawPath(dst, mPaint);
        */

        Path path = new Path();

//        path.addCircle(0, 0, 200, Path.Direction.CW);
        path.addRect(-100, -100, 100, 100, Path.Direction.CW);
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);

        canvas.drawPath(path, mPaint);

        PathMeasure measure = new PathMeasure(path, false);

        Log.e("length1 = ", "" + measure.getLength());

        measure.nextContour();

        Log.e("length2 = ", "" + measure.getLength());
    }

    Path path = new Path();
    PathMeasure measure = new PathMeasure();

    private void arrowRing(Canvas canvas) {
        measure.setPath(path, false);

//        measure.getPosTan(measure.getLength() * currentValue, pos, tan);    // 获取当前位置的坐标以及趋势
//
//        matrix.reset();
//        float degree = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);      // 计算图片旋转角度
//
//        matrix.postRotate(degree, mBitmap.getWidth()/2, mBitmap.getHeight()/2);     // 旋转图片
//        matrix.postTranslate(pos[0] - mBitmap.getWidth()/2, pos[1] - mBitmap.getHeight()/2);    // 将图片绘制中心调整到与当前点重合

        measure.getMatrix(measure.getLength() * currentValue, matrix, PathMeasure.POSITION_MATRIX_FLAG | PathMeasure.TANGENT_MATRIX_FLAG);
        matrix.preTranslate(-mBitmap.getWidth()/2, -mBitmap.getHeight()/2);

        canvas.drawPath(path, mPaint);
        canvas.drawBitmap(mBitmap, matrix, mPaint);
    }

    private void searchAnim(Canvas canvas) {
        float innerRadius = 50;
        float outerRadius = 100;

        float rate = (float) Math.sin(Math.PI / 4);

        Path innerPath = new Path();

//        innerPath.moveTo(innerRadius * rate, innerRadius * rate);
//        innerPath.addCircle(0, 0, innerRadius, Path.Direction.CW);
//        innerPath.lineTo(outerRadius * rate, outerRadius * rate);

        innerPath.addArc(-50, 50, 50, 50, 45, 359.9f);

        canvas.drawPath(innerPath, mPaint);

        Path outerPath = new Path();
        outerPath.addCircle(0, 0, outerRadius, Path.Direction.CCW);

        canvas.drawPath(outerPath, mPaint);
    }
}
