package com.whx.practice.view.leaf;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.whx.practice.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by whx on 2017/9/30.
 */

public class LeafView extends View{

    private static final int LEAF_FLOAT_TIME = 3000;    //叶子飘动一个周期所花时间
    private static final int LEAF_ROTATE_TIME = 2000;   //叶子旋转一周所花时间
    private static final int LEFT_MARGIN = 9;
    private static final int RIGHT_MARGIN = 25;

    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;

    private int mProgressWidth, mProgressHeight;
    private int mTotalWidth, mTotalHeight;
    private int mCurrentPosition;
    private int mLeafFloatTime;     //叶子开始旋转时间
    private int mAddTime;           //用于控制随机增加的时间不抱团
    private int mArcRadius;         //弧形的半径
    private int mLeftMargin;        //
    private int mRightMargin;       //
    private int mLeafWidth, mLeafHeight;

    private Paint mBitmapPaint, mWhitePaint, mOrangePaint, mOuterPaint;
    private Resources mResource;
    private List<Leaf> mLeafs;
    private Bitmap mLeafBitmap;

    public LeafView(Context context) {
        this(context, null);
    }
    public LeafView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLeftMargin = UiUtils.dp2px(context, LEFT_MARGIN);
        mRightMargin = UiUtils.dp2px(context, RIGHT_MARGIN);

        initPaint();
        initBitmap();
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);

        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setColor(ORANGE_COLOR);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(6);
    }
    private void initBitmap() {
        mLeafBitmap = BitmapFactory.decodeResource(mResource, R.drawable.leaf);

        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;

        mProgressWidth = mTotalWidth - mLeftMargin - mRightMargin;

        mArcRadius = (mProgressHeight - 2 * mLeftMargin) / 2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void drawOuter(Canvas canvas) {
        canvas.save();
//        canvas.translate(m);
    }

    private enum StartType {
        LITTLE, MIDDLE, BIG
    }

    private class Leaf {
        //绘制部分的位置
        float x, y;
        //飘动的幅度
        StartType type;
        //旋转的角度
        int rotateAngle;
        //旋转方向, 0顺时针，1逆时针
        int rotateDirection;
        //起始时间
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 10;

        Random random = new Random();

        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(3);
            StartType type = StartType.MIDDLE;

            switch (randomType) {
                case 0:
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
                default:
                    break;
            }
            leaf.type = type;
            //随机起始旋转角度
            leaf.rotateAngle = random.nextInt(360);
            //随机旋转方向
            leaf.rotateDirection = random.nextInt(2);

            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int)(mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;

            return leaf;
        }

        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        public List<Leaf> generateLeafs(int size) {
            List<Leaf> list = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                list.add(generateLeaf());
            }

            return list;
        }
    }
}
