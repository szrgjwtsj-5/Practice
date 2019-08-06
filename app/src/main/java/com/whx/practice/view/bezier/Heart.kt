package com.whx.practice.view.bezier

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * Created by whx on 2018/1/17.
 */
class Heart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) : View(context, attrs, defArr) {

    companion object {
        const val C = 0.551915024494f     // 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置
    }

    private var mCenterX = 0f
    private var mCenterY = 0f

    private val mCircleRadius = 200f                  // 圆的半径
    private val mDifference = mCircleRadius * C        // 圆形的控制点与数据点的差值

    private val mData = Array(4, {PointF()})               // 顺时针记录绘制圆形的四个数据点
    private val mCtrl = Array(8, {PointF()})            // 顺时针记录绘制圆形的八个控制点

    private val mDuration = 2000f                     // 变化总时长
    private var mCurrent = 0f                         // 当前已进行时长
    private val mCount = 100f                         // 将时长总共划分多少份
    private val mPiece = mDuration / mCount            // 每一份的时长

    private val mPaint by lazy {
        Paint().apply {
            strokeWidth = 8f
            style = Paint.Style.STROKE
            textSize = 60f
            isAntiAlias = true
        }
    }
    private val mPath = Path()

    init {
        // 初始化数据点
        mData[0].x = 0f
        mData[0].y = mCircleRadius

        mData[1].x = mCircleRadius
        mData[1].y = 0f

        mData[2].x = 0f
        mData[2].y = -mCircleRadius

        mData[3].x = -mCircleRadius
        mData[3].y = 0f

        // 初始化控制点
        mCtrl[0].x = mData[0].x + mDifference
        mCtrl[0].y = mData[0].y

        mCtrl[1].x = mData[1].x
        mCtrl[1].y = mData[1].y + mDifference

        mCtrl[2].x = mData[1].x
        mCtrl[2].y = mData[1].y - mDifference

        mCtrl[3].x = mData[2].x + mDifference
        mCtrl[3].y = mData[2].y

        mCtrl[4].x = mData[2].x - mDifference
        mCtrl[4].y = mData[2].y

        mCtrl[5].x = mData[3].x
        mCtrl[5].y = mData[3].y - mDifference

        mCtrl[6].x = mData[3].x
        mCtrl[6].y = mData[3].y + mDifference

        mCtrl[7].x = mData[0].x - mDifference
        mCtrl[7].y = mData[0].y
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCenterX = w.toFloat() / 2
        mCenterY = h.toFloat() / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawCoordinateSystem(canvas)
        drawAuxiliaryLine(canvas)

        // 绘制贝赛尔曲线
        mPaint.color = Color.RED
        mPaint.strokeWidth = 8f

        mPath.reset()
        mPath.moveTo(mData[0].x, mData[0].y)

        mPath.cubicTo(mCtrl[0].x, mCtrl[0].y, mCtrl[1].x, mCtrl[1].y, mData[1].x, mData[1].y)
        mPath.cubicTo(mCtrl[2].x, mCtrl[2].y, mCtrl[3].x, mCtrl[3].y, mData[2].x, mData[2].y)
        mPath.cubicTo(mCtrl[4].x, mCtrl[4].y, mCtrl[5].x, mCtrl[5].y, mData[3].x, mData[3].y)
        mPath.cubicTo(mCtrl[6].x, mCtrl[6].y, mCtrl[7].x, mCtrl[7].y, mData[0].x, mData[0].y)

        canvas.drawPath(mPath, mPaint)

        mCurrent += mPiece
        if (mCurrent < mDuration) {
            mData[0].y -= 120/mCount
            mCtrl[3].y += 80/mCount
            mCtrl[4].y += 80/mCount

            mCtrl[2].x -= 20/mCount
            mCtrl[5].x += 20/mCount

            postInvalidateDelayed(mPiece.toLong())
        }
    }

    // 画辅助线
    private fun drawAuxiliaryLine(canvas: Canvas) {
        mPaint.color = Color.GRAY
        mPaint.strokeWidth = 20f

        // 画点
        for (point in mData) {
            canvas.drawPoint(point.x, point.y, mPaint)
        }

        for (point in mCtrl) {
            canvas.drawPoint(point.x, point.y, mPaint)
        }

        // 画辅助线
        mPaint.strokeWidth = 4f
        var i = 1; var j = 1
        while (i < 4) {
            canvas.drawLine(mData[i].x, mData[i].y, mCtrl[j].x, mCtrl[j].y, mPaint)
            canvas.drawLine(mData[i].x, mData[i].y, mCtrl[j+1].x, mCtrl[j+1].y, mPaint)
            i++
            j += 2
        }
        canvas.drawLine(mData[0].x, mData[0].y, mCtrl[0].x, mCtrl[0].y, mPaint)
        canvas.drawLine(mData[0].x, mData[0].y, mCtrl[7].x, mCtrl[7].y, mPaint)
    }

    // 画坐标系
    private fun drawCoordinateSystem(canvas: Canvas) {
        canvas.translate(mCenterX, mCenterY)    // 将坐标系移到画布中央
        canvas.scale(1f, -1f)            // 翻转 y 轴

        mPaint.color = Color.CYAN
        mPaint.strokeWidth = 5f

        canvas.drawLine(0f, -2000f, 0f, 2000f, mPaint)
        canvas.drawLine(-2000f, 0f, 2000f, 0f, mPaint)
    }
}