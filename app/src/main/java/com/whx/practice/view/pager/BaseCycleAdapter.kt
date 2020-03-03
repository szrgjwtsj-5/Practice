package com.whx.practice.view.pager

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

abstract class BaseCycleAdapter : PagerAdapter() {

    override fun getCount(): Int {
        return if (getRealItemCount() > 1) getRealItemCount() + 2 else getRealItemCount()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return instantiateRealItem(container, getRealPosition(position, getRealItemCount()))
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }

    abstract fun getRealItemCount(): Int
    abstract fun instantiateRealItem(container: ViewGroup, position: Int): Any

    fun getRealPosition(position: Int, itemCount: Int): Int {
        return when (position) {
            0 -> itemCount - 1
            itemCount + 1 -> 0
            else -> position - 1
        }
    }
}