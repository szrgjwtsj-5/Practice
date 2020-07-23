package com.whx.practice

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

/**
 * 实现懒加载的Fragment 基类
 */
abstract class BaseLazyFragment : Fragment() {

    private var isLoadData = false
    private var isViewCreated = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lazyLoad()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
    }

    private fun lazyLoad() {
        if (userVisibleHint && isViewCreated && !isLoadData) {
            onLazyLoad()
            isLoadData = true
        } else {
            if (isLoadData) {
                stopLoad()
            }
        }
    }

    abstract fun onLazyLoad()

    /**
     * 停止加载
     * 当视图已经对用户不可见并且加载过数据，但是没有加载完，而只是加载loading。
     * 如果需要在切换到其他页面时停止加载数据，可以覆写此方法。
     * 存在问题，如何停止加载网络
     */
    protected fun stopLoad() {

    }
}