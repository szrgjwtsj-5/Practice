package com.whx.practice.view.sbwidget

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.whx.practice.R

/**
 * Created by whx on 2018/4/2.
 */
class BasicDisplayItemNew @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) : ConstraintLayout(context, attrs, defArr) {

    private val READ_ONLY = 1
    private val EDITABLE = 2
    private val CLICKABLE = 3

    private var titleColor = Color.parseColor("#333333")
    private var readOnlyColor = Color.parseColor("#999999")
    private var contentColor = Color.parseColor("#333333")
    private var arrowShow = false

    private var titleText: String? = null
    private var contentText: String? = null
    private var contentHint: String? = null
    private var titleSize = 16f
    private var contentSize = 16f

    private var mTitle: TextView? = null
    private var mContent: EditText? = null
    private var mArrow: TextView? = null
    private var mContentTextView: TextView? = null

//    private var mTitleWidth = 0

    private var mState = READ_ONLY

    interface OnTitleChangeListener {
        fun onChange()
    }

    init {
        initAttrs(attrs)
        initView()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val typeArr = context.obtainStyledAttributes(attrs, R.styleable.BasicDisplayItemNew)
            titleColor = typeArr.getColor(R.styleable.BasicDisplayItemNew_titleColor, Color.parseColor("#333333"))
            contentColor = typeArr.getColor(R.styleable.BasicDisplayItemNew_contentColor, Color.parseColor("#333333"))
            arrowShow = typeArr.getBoolean(R.styleable.BasicDisplayItemNew_iconShow, false)
            titleText = typeArr.getString(R.styleable.BasicDisplayItemNew_titleText)
            contentText = typeArr.getString(R.styleable.BasicDisplayItemNew_contentText)
            mState = typeArr.getInt(R.styleable.BasicDisplayItemNew_contentState, READ_ONLY)
            contentHint = typeArr.getString(R.styleable.BasicDisplayItemNew_contentHint)
            titleSize = typeArr.getDimension(R.styleable.BasicDisplayItemNew_titleSize, 16f)
            contentSize = typeArr.getDimension(R.styleable.BasicDisplayItemNew_contentSize, 16f)

            typeArr.recycle()
        }
    }

    private fun initView() {
        setPadding(0, resources.getDimension(R.dimen.dp_16).toInt(), 0, 0)
        LayoutInflater.from(context).inflate(R.layout.basic_item, this, true)
        mTitle = findViewById(R.id.title)
        mContent = findViewById(R.id.content)
        mArrow = findViewById(R.id.arrow)
//        mContentTextView = findViewById(R.id.content_t)

        mTitle?.let {
            it.setTextColor(titleColor)
            it.text = titleText
            it.textSize = titleSize
        }

        if (mState == CLICKABLE) {
            mContentTextView?.let {
                it.visibility = View.VISIBLE
                mContent?.visibility = View.GONE
                it.text = contentText
                it.textSize = contentSize
                it.setTextColor(contentColor)
            }
        } else {
            mContentTextView?.visibility = View.GONE
            mContent?.let {
                when (mState) {
                    READ_ONLY -> {
                        it.setTextColor(readOnlyColor)
                        it.isEnabled = false
                    }
                    EDITABLE -> {
                        it.setTextColor(contentColor)
//                        it.isClickable = true
//                        it.isFocusable = true
//                        it.isFocusableInTouchMode = true
//                        it.requestFocus()
//                        it.isEnabled = true
                        it.hint = contentHint
                    }
                }
                it.setText(contentText)
                it.textSize = titleSize
            }
        }

        mArrow?.visibility = if(arrowShow) View.VISIBLE else View.GONE
    }

    fun setTitleText(text: String) {
        mTitle?.text = text

        if (parent is OnTitleChangeListener) {
            (parent as OnTitleChangeListener).onChange()
        }
    }

    fun setContentHint(text: String) {
        mContent?.hint = text
    }

    fun setContentText(text: String) {
        mContent?.setText(text)
    }

    fun setIsArrowShow(isShow: Boolean) {
        mArrow?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun setTitleWidth(width: Float) {
        val tmp = (if (mState == CLICKABLE) mContentTextView else mContent)?.layoutParams as ConstraintLayout.LayoutParams

        val params = ConstraintLayout.LayoutParams(tmp)
        params.marginStart = width.toInt()
        (if (mState == CLICKABLE) mContentTextView else mContent)?.layoutParams = params
    }

    fun getTitleSize(): Pair<String, Float> = Pair(mTitle?.text.toString(), titleSize)

    fun setContentClickListener(onClickListener: OnClickListener) {
        if (mState == CLICKABLE) {
            mArrow?.setOnClickListener(onClickListener)
            mContent?.setOnClickListener(onClickListener)
        }
    }
}