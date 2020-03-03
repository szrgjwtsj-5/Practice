package com.whx.practice.view.pager

import android.view.View

interface Indicator {
    fun getIndicatorView(): View

    fun onChanged(itemCount: Int, currentPosition: Int)

    fun onPageSelected(position: Int)

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageScrollStateChanged(state: Int)
}