package com.whx.practice.view.sbwidget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.LinearLayout
import com.whx.practice.view.leaf.UiUtils

/**
 * Created by whx on 2018/4/3.
 */
class DisplayItemContainer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) : LinearLayout(context, attrs, defArr), BasicDisplayItemNew.OnTitleChangeListener {

    private var maxTitle = 0f
    private val measurePaint by lazy {
        Paint()
    }

    init {
        orientation = VERTICAL

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        getMaxTitle()
    }

    private fun getMaxTitle() {
        val num = childCount
        for (i in 0 until num) {
            val child = getChildAt(i)

            if (child is BasicDisplayItemNew) {
                val value = child.getTitleSize()
                measurePaint.textSize = value.second
                val len = measurePaint.measureText(value.first)

                if (maxTitle <= len) {
                    maxTitle = len
                }
            }
        }
        for (i in 0 until num) {
            val child = getChildAt(i)

            if (child is BasicDisplayItemNew) {
                child.setTitleWidth(UiUtils.dp2px(context, (maxTitle + 40).toInt()).toFloat())
            }
        }
    }

    override fun onChange() {
        getMaxTitle()
    }
}