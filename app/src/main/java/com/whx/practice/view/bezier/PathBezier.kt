package com.whx.practice.view.bezier

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Created by whx on 2018/1/18.
 */
class PathBezier @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) : View(context, attrs, defArr), View.OnClickListener {

    private val mRadius = 30f

    private val mStartPoint = PointF()
    private val mEndPoint = PointF()

    private val mMovePoint = PointF()
    private val mControlPoint = PointF()

    private val mPathPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 5f
            color = Color.LTGRAY
        }
    }
    private val mCirclePaint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }

    private val mPath = Path()
    private val mAnimator by lazy {
        ValueAnimator.ofObject(BezierEvaluator(arrayListOf(mStartPoint, mControlPoint, mEndPoint)),
                mStartPoint,
                mEndPoint).apply {
            duration = 1000
            addUpdateListener { valueAnimator ->
                val point = valueAnimator.animatedValue as PointF
                mMovePoint.x = point.x
                mMovePoint.y = point.y
                invalidate()
            }
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    init {
        mStartPoint.x = 100f
        mStartPoint.y = 100f

        mEndPoint.x = 600f
        mEndPoint.y = 600f

        mMovePoint.x = mStartPoint.x
        mMovePoint.y = mStartPoint.y

        mControlPoint.x = 500f
        mControlPoint.y = 0f

        setOnClickListener(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPath.reset()

        mPath.moveTo(mStartPoint.x, mStartPoint.y)
        mPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y)

        // 绘制起点、终点、路径
        canvas.drawPath(mPath, mPathPaint)
        canvas.drawCircle(mStartPoint.x, mStartPoint.y, mRadius, mCirclePaint)
        canvas.drawCircle(mEndPoint.x, mEndPoint.y, mRadius, mCirclePaint)

        canvas.drawCircle(mMovePoint.x, mMovePoint.y, mRadius, mCirclePaint)    // 绘制圆的移动过程
    }

    override fun onClick(v: View?) {
        mAnimator.start()
    }
}