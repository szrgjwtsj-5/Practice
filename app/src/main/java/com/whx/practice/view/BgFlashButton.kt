package com.whx.practice.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.*
import android.graphics.Typeface.BOLD
import android.util.AttributeSet
import android.view.View
import com.whx.practice.R

/**
 * 背景有流光效果的button
 */
class BgFlashButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "PurchaseButton"
    }

    private val paint = Paint()
    private val rect = RectF()
    private val path = Path()
    private val buttonColor = (0xFFFEBC15).toInt()
    private val textColor = (0xFF1C1C1C).toInt()
    private val buttonText: String
    private val slideBitmap: Bitmap
    private val animator: ValueAnimator
    private val textSize: Int
    private val bitmapSrcRect = Rect()
    private val bitmapDestRect = Rect()
    private var slidePercent: Float = 1f
//    private var font: Typeface? = null

    init {
        buttonText = "hhhhh"
        slideBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.slide_rect)
        bitmapSrcRect.set(0, 0, slideBitmap.width, slideBitmap.height)
        textSize = 38
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000L
        animator.repeatCount = INFINITE
        animator.addUpdateListener {
            slidePercent = it.animatedValue as Float
            invalidate()
        }
//        Typeface.createFromAsset(context.assets, "Roboto-Medium.ttf")
//                .also {
//                    font = Typeface.create(it, BOLD)
//                }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), height.toFloat())
        path.addRoundRect(rect, h / 2f, h / 2f, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.clipPath(path)
        super.onDraw(canvas)

        drawBg(canvas)
        drawText(canvas)
        drawSlide(canvas)
    }

    private fun drawBg(canvas: Canvas?) {
        paint.color = buttonColor
        paint.style = Paint.Style.FILL
        canvas?.drawPath(path, paint)
    }

    private fun drawText(canvas: Canvas?) {
        paint.textSize = textSize.toFloat()
        paint.color = textColor
        paint.setTextAlign(Paint.Align.CENTER)
//        paint.typeface = font
        val fontMetrics = paint.fontMetrics
        val top = fontMetrics.top //为基线到字体上边框的距离,即上图中的top
        val bottom = fontMetrics.bottom //为基线到字体下边框的距离,即上图中的bottom
        val baseLineY = height / 2f - top / 2 - bottom / 2
        canvas?.drawText(buttonText, width / 2f, baseLineY, paint)
    }

    private fun drawSlide(canvas: Canvas?) {
        canvas?.save()
        //Loger.w(TAG, "translate = ${slidePercent * width}")
        val bitmapRatio = slideBitmap.height / height.toFloat()
        val drawWidth = (slideBitmap.width / bitmapRatio).toInt()
        canvas?.translate(slidePercent * (width + drawWidth) - drawWidth, 0f)

        bitmapDestRect.set(0, 0, drawWidth, height.toFloat().toInt())
        canvas?.drawBitmap(slideBitmap, bitmapSrcRect, bitmapDestRect, paint)
        canvas?.restore()
    }

    fun onPause() {
        animator.cancel()
    }

    fun onResume() {
        animator.start()
    }
}