package com.whx.practice.view.bezier

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by whx on 2018/1/17.
 */
class WaveBezier @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) : View(context, attrs, defArr), View.OnClickListener {

    private val mWaveLength = 800f
    private var mOffset = 0f
    private var mHeight = 0f
    private var mWidth = 0f
    private var mWaveCount = 0
    private var mCenterY = 0f
    private var mStart = false

    private val mPaint by lazy {
        Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }
    }
    private val mPath = Path()
    private val mAnimator by lazy {
        ValueAnimator.ofInt(0, mWaveLength.toInt()).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                mOffset = (animation.animatedValue as Int).toFloat()
                postInvalidate()
            }
        }
    }

    init {
        setOnClickListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        mWaveCount = Math.round(mWidth / mWaveLength + 1.5).toInt()
        mCenterY = mHeight / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制曲线
        mPath.reset()
        mPath.moveTo(-mWaveLength + mOffset, mCenterY)

        for (i in 0 until mWaveCount) {
            mPath.quadTo((-mWaveLength * 3 / 4) + (i * mWaveLength) + mOffset, mCenterY + 60, (-mWaveLength / 2) + (i * mWaveLength) + mOffset, mCenterY)
            mPath.quadTo((-mWaveLength / 4) + (i * mWaveLength) + mOffset, mCenterY - 60, i * mWaveLength + mOffset, mCenterY)
        }
        mPath.lineTo(mWidth, mHeight)
        mPath.lineTo(0f, mHeight)
        mPath.close()

        canvas.drawPath(mPath, mPaint)
    }

    override fun onClick(v: View) {
        mStart = if (mStart) {
            mAnimator.cancel()
            false
        } else {
            mAnimator.start()
            true
        }
    }
}