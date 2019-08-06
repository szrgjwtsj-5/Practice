package com.whx.practice.view.bezier

import android.graphics.PointF
import android.R.attr.y
import android.R.attr.x



/**
 * 计算贝赛尔曲线上点的坐标
 * Created by whx on 2018/1/18.
 */

/**
 * B(t) = (1 - t)^2 * P0 + 2t * (1 - t) * P1 + t^2 * P2, t ∈ [0,1]
 *
 * @param t  曲线长度比例
 * @param p0 起始点
 * @param p1 控制点
 * @param p2 终止点
 * @return t对应的点
 */
fun calculateBezierPointForQuadratic(t: Float, p0: PointF, p1: PointF, p2: PointF): PointF{
    val point = PointF()
    val temp = 1 - t
    point.x = temp * temp * p0.x + 2 * t * temp * p1.x + t * t * p2.x
    point.y = temp * temp * p0.y + 2 * t * temp * p1.y + t * t * p2.y
    return point
}

/**
 * B(t) = P0 * (1-t)^3 + 3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3, t ∈ [0,1]
 *
 * @param t  曲线长度比例
 * @param p0 起始点
 * @param p1 控制点1
 * @param p2 控制点2
 * @param p3 终止点
 * @return t对应的点
 */
fun calculateBezierPointForCubic(t: Float, p0: PointF, p1: PointF, p2: PointF, p3: PointF): PointF{
    val point = PointF()
    val temp = 1 - t
    point.x = temp * temp * temp * p0.x + 3 * p1.x * t * temp * temp + 3 * t * t * p2.x * temp + p3.x * t * t * t
    point.y = temp * temp * temp * p0.y + 3 * p1.y * t * temp * temp + 3 * t * t * p2.y * temp + p3.y * t * t * t
    return point
}

/**
 * deCasteljau算法, 计算点的x 坐标
 *
 * @param i 阶数
 * @param j 控制点的索引
 * @param t 点在线段的比例
 * @param mControlPoints 所有控制点
 */
fun calculateBezierX(i: Int, j: Int, t: Float, mControlPoints: ArrayList<PointF>): Float {
    return if (i == 1) {
        (1 - t) * mControlPoints[j].x + t * mControlPoints[j + 1].x
    } else (1 - t) * calculateBezierX(i - 1, j, t, mControlPoints) + t * calculateBezierX(i - 1, j + 1, t, mControlPoints)
}

/**
 * 计算点的y 坐标
 */
fun calculateBezierY(i: Int, j: Int, t: Float, mControlPoints: ArrayList<PointF>): Float {
    return if (i == 1) {
        (1 - t) * mControlPoints[j].y + t * mControlPoints[j + 1].y
    } else (1 - t) * calculateBezierY(i - 1, j, t, mControlPoints) + t * calculateBezierY(i - 1, j + 1, t, mControlPoints)
}
