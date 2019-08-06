package com.whx.practice.view.bezier

import android.animation.TypeEvaluator
import android.graphics.PointF

/**
 * Created by whx on 2018/1/18.
 */
class BezierEvaluator(private val mControlPoints: ArrayList<PointF>) : TypeEvaluator<PointF> {

    override fun evaluate(fraction: Float, startValue: PointF, endValue: PointF) = PointF(calculateBezierX(2, 0, fraction, mControlPoints),
            calculateBezierY(2, 0, fraction, mControlPoints))
}