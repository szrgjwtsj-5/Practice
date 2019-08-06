package com.whx.practice.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * 雷达图
 * Created by whx on 2018/1/16.
 */
class RadarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) : View(context, attrs, defArr) {

    private val count = 6                       // 雷达图边数
    private val angle = Math.PI * 2 / count     // 平分的角度
    private var radius = 0f                     // 中心点到顶点的距离，即外接圆半径
    private var centerX = 0f                    // 中心点坐标
    private var centerY = 0f

    private val titles = arrayOf("a", "b", "c", "d", "e", "f")
    private var data = arrayOf(100, 60, 60, 50, 10, 20)

    private var maxValue = 100f

    private val mRadarPaint by lazy {       // 背景线画笔
        Paint().apply {
            color = Color.parseColor("#aaaaaa")
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
    }
    private val mDataPaint by lazy {        // 数据区画笔
        Paint().apply {
            color = Color.CYAN
            isAntiAlias = true
        }
    }
    private val mTextPaint by lazy {        // 文字画笔
        Paint().apply {
            color = Color.parseColor("#333333")
            isAntiAlias = true
            textSize = 24f
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = Math.min(h, w) / 2 * 0.9f
        centerX = w.toFloat() / 2
        centerY = h.toFloat() / 2

        postInvalidate()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        drawBackLines(canvas)
        drawText(canvas)

        if (data.isNotEmpty()) {
            drawData(canvas)
        }
    }

    /**
     * 画背景的蛛网线
     */
    private fun drawBackLines(canvas: Canvas) {
        val path = Path()
        val r = radius / (count - 1)    // r 是背景线之间的距离

        for (i in 1 until count) {      // 中心点不用绘制
            val curR = r * i
            path.reset()

            for (j in 0 until count) {
                if (j == 0) {
                    path.moveTo(centerX + curR, centerY)
                } else {
                    // 根据半径，算出背景线上每个点的坐标
                    val x = centerX + curR * Math.cos(angle * j)
                    val y = centerY + curR * Math.sin(angle * j)
                    path.lineTo(x.toFloat(), y.toFloat())
                }
            }
            path.close()
            canvas.drawPath(path, mRadarPaint)
        }

        // 画从中心点到多边形定点的连线
        for (i in 0 until count) {
            val x = centerX + radius * Math.cos(angle * i)
            val y = centerY + radius * Math.sin(angle * i)
            canvas.drawLine(centerX, centerY, x.toFloat(), y.toFloat(), mRadarPaint)
        }
    }

    /**
     * 画文字
     */
    private fun drawText(canvas: Canvas) {
        val fontMetrics = mTextPaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent

        for (i in 0 until count) {
            val x = centerX + (radius + fontHeight/2) * Math.cos(angle * i)
            val y = centerY + (radius + fontHeight/2) * Math.sin(angle * i)

            when {
                (angle * i > Math.PI / 2 && angle * i < Math.PI * 3 / 2) -> {   // 如果文字在y 轴左侧
                    val dis = mTextPaint.measureText(titles[i])
                    canvas.drawText(titles[i], x.toFloat() - dis, y.toFloat(), mTextPaint)
                }
                else -> {       //右侧
                    canvas.drawText(titles[i], x.toFloat(), y.toFloat(), mTextPaint)
                }
            }
        }
    }

    /**
     * 画数据区
     */
    private fun drawData(canvas: Canvas) {
        val path = Path()
        mDataPaint.alpha = 255

        for (i in 0 until count) {
            val percent = data[i].toFloat()/maxValue
            val x = centerX + radius * Math.cos(angle * i) * percent
            val y = centerY + radius * Math.sin(angle * i) * percent

            if (i == 0) {
                path.moveTo(x.toFloat(), centerY)
            } else {
                path.lineTo(x.toFloat(), y.toFloat())
            }
            // 画小圆点
            canvas.drawCircle(x.toFloat(), y.toFloat(), 10f, mDataPaint)
        }
        // 画数据区域边线
        mDataPaint.style = Paint.Style.STROKE
        canvas.drawPath(path, mDataPaint)
        // 画数据区域
        mDataPaint.alpha = 127
        mDataPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(path, mDataPaint)
    }

}