package com.whx.practice.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Created by whx on 2018/2/28.
 */
class SearchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) : View(context, attrs, defArr) {

    enum class State {
        NONE, STARTING, SEARCHING, ENDING
    }

    private val currentState = State.NONE

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.CYAN
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
    }

    private val innerPath = Path()
    private val outerPath = Path()

    private var mWidth = 0f
    private var mHeight = 0f

    private fun initPath() {

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(mWidth/2, mHeight/2)

    }

}