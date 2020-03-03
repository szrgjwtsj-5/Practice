package com.whx.practice.view.pager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

abstract class BaseCycleFragmentAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return if (getRealCount() > 1) getRealCount() + 2 else getRealCount()
    }

    override fun getItem(position: Int): Fragment {
        return getRealItem(getRealPosition(position, getRealCount()))
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(getRealPosition(position, getRealCount()))
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(getRealPosition(position, getRealCount()))
    }

    override fun getPageWidth(position: Int): Float {
        return super.getPageWidth(getRealPosition(position, getRealCount()))
    }

    abstract fun getRealCount(): Int
    abstract fun getRealItem(position: Int): Fragment

    fun getRealPosition(position: Int, itemCount: Int): Int {
        return when (position) {
            0 -> itemCount - 1
            itemCount + 1 -> 0
            else -> position - 1
        }
    }
}