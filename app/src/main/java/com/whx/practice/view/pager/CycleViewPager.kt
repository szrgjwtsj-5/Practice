package com.whx.practice.view.pager

import android.content.Context
import android.database.DataSetObserver
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.whx.practice.R
import java.lang.ref.WeakReference
import java.util.Objects

class CycleViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defArr: Int = 0) :
        FrameLayout(context, attrs, defArr) {
    private val mViewPager: ViewPager

    private var canAutoTurning = false
    private var autoTurningTime: Long = 0
    private var isTurning = false
    private var mAutoTurningRunnable: AutoTurningRunnable? = null

    private var mPendingCurrentItem = NO_POSITION
    private var mIndicator: Indicator? = null

    private val mAdapterDataObserver = object : DataSetObserver() {
        override fun onChanged() {
            val itemCount = Objects.requireNonNull<PagerAdapter>(getAdapter()).count
            if (itemCount <= 1) {
                if (isTurning) {
                    stopAutoTurning()
                }
            } else {
                if (!isTurning) {
                    startAutoTurning()
                }
            }
            if (mIndicator != null) {
                mIndicator!!.onChanged(getPagerRealCount(), getRealCurrentItem())
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ip_layout_cycleviewpager, this, true)
        mViewPager = findViewById(R.id.cycle_viewpager)

        initWithAttrs(attrs)

        mViewPager.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mViewPager.offscreenPageLimit = 1

        val mCycleOnPageChangeCallback = CycleOnPageChangeCallback(this)
        mViewPager.addOnPageChangeListener(mCycleOnPageChangeCallback)

        mAutoTurningRunnable = AutoTurningRunnable(this)
    }

    private fun initWithAttrs(attrs: AttributeSet?) {
        val tpa = context.obtainStyledAttributes(attrs, R.styleable.CycleViewPager)
        canAutoTurning = tpa.getBoolean(R.styleable.CycleViewPager_canAutoTurning, false)
        autoTurningTime = tpa.getInt(R.styleable.CycleViewPager_turningTime, 0).toLong()
        tpa.recycle()
    }

    fun setAutoTurning(autoTurningTime: Long) {
        setAutoTurning(canAutoTurning, autoTurningTime)
    }

    fun setAutoTurning(canAutoTurning: Boolean, autoTurningTime: Long) {
        this.canAutoTurning = canAutoTurning
        this.autoTurningTime = autoTurningTime
        stopAutoTurning()
        startAutoTurning()
    }

    fun startAutoTurning() {
        if (!canAutoTurning || autoTurningTime <= 0 || isTurning) return
        isTurning = true
        postDelayed(mAutoTurningRunnable, autoTurningTime)
    }

    fun stopAutoTurning() {
        isTurning = false
        removeCallbacks(mAutoTurningRunnable)
    }

    fun setAdapter(adapter: PagerAdapter) {
        if (adapter is BaseCycleAdapter || adapter is BaseCycleFragmentAdapter) {
            if (getAdapter() == adapter) return
            adapter.registerDataSetObserver(mAdapterDataObserver)
            mViewPager.adapter = adapter
            setCurrentItem(1, false)
            initIndicator()
            return
        }
        throw IllegalArgumentException("adapter must be an instance of CyclePagerAdapter " +
                "or CyclePagerFragmentAdapter")
    }

    fun getAdapter(): PagerAdapter? {
        return mViewPager.adapter
    }

    private fun getPagerRealCount(): Int {
        val adapter = getAdapter()
        if (adapter is BaseCycleAdapter) {
            return adapter.getRealItemCount()
        }
        return if (adapter is BaseCycleFragmentAdapter) {
            adapter.getRealCount()
        } else 0
    }

    fun setPageTransformer(transformer: ViewPager.PageTransformer?) {
        mViewPager.setPageTransformer(false, transformer)
    }

    fun setCurrentItem(item: Int) {
        setCurrentItem(item, true)
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
//        Logger.d("setCurrentItem $item")
        mViewPager.setCurrentItem(item, smoothScroll)
        if (!smoothScroll && mIndicator != null) {
            mIndicator!!.onPageSelected(getRealCurrentItem())
        }
    }

    private fun getCurrentItem(): Int {
        return mViewPager.currentItem
    }

    fun getRealCurrentItem(): Int {
        return if (getCurrentItem() >= 1) getCurrentItem() - 1 else getCurrentItem()
//        if (getPagerRealCount() == 1) {
//            return getCurrentItem()
//        }
//        if (getCurrentItem() <= getPagerRealCount() && getCurrentItem() >= 1) {
//            return getCurrentItem() - 1
//        }
//        if (getCurrentItem() < 1) {
//            return getPagerRealCount() - 1
//        } else {
//            return getCurrentItem() - getPagerRealCount()
//        }
    }

    fun setOffscreenPageLimit(limit: Int) {
        mViewPager.offscreenPageLimit = limit
    }

    fun getOffscreenPageLimit(): Int {
        return mViewPager.offscreenPageLimit
    }

    fun registerOnPageChangeCallback(callback: ViewPager.OnPageChangeListener) {
        mViewPager.addOnPageChangeListener(callback)
    }

    fun unregisterOnPageChangeCallback(callback: ViewPager.OnPageChangeListener) {
        mViewPager.removeOnPageChangeListener(callback)
    }

    fun getViewPager(): ViewPager {
        return mViewPager
    }

    fun gotoNextItem() {
        val itemCount = getAdapter()?.count ?: 0
        if (itemCount == 0) return
        val nextItem = (getCurrentItem() + 1) % itemCount
        if (nextItem == itemCount - 1) {
            setCurrentItem(1, false)
            return
        }
        setCurrentItem(nextItem, true)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        if (action == MotionEvent.ACTION_DOWN) {
            if (canAutoTurning && isTurning) {
                stopAutoTurning()
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL ||
                action == MotionEvent.ACTION_OUTSIDE) {
            if (canAutoTurning) startAutoTurning()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAutoTurning()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoTurning()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState ?: return null)
        ss.mCurrentItem = getCurrentItem()
//        Logger.d("onSaveInstanceState: " + ss.mCurrentItem)
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        mPendingCurrentItem = state.mCurrentItem
//        Logger.d("onRestoreInstanceState: $mPendingCurrentItem")
        restorePendingState()
    }

    private fun restorePendingState() {
        if (mPendingCurrentItem == NO_POSITION) {
            // No state to restore, or state is already restored
            return
        }
        val currentItem = 0.coerceAtLeast(mPendingCurrentItem.coerceAtMost(
                ((getAdapter())?.count ?: 0) - 1))
//        Logger.d("restorePendingState: $currentItem")
        mPendingCurrentItem = NO_POSITION
        setCurrentItem(currentItem, false)
    }

    fun setIndicator(indicator: Indicator?) {
        if (mIndicator === indicator) return
        removeIndicatorView()
        mIndicator = indicator
        initIndicator()
    }

    private fun initIndicator() {
        if (mIndicator == null || getAdapter() == null) return
        addView(mIndicator!!.getIndicatorView())
        mIndicator!!.onChanged(getPagerRealCount(), getRealCurrentItem())
    }

    private fun removeIndicatorView() {
        removeView(mIndicator?.getIndicatorView() ?: return)
    }

    //1.normal:
    //onPageScrollStateChanged(state=1) -> onPageScrolled... -> onPageScrollStateChanged(state=2)
    // -> onPageSelected -> onPageScrolled... -> onPageScrollStateChanged(state=0)
    //2.setCurrentItem(,true):
    //onPageScrollStateChanged(state=2) -> onPageSelected -> onPageScrolled... -> onPageScrollStateChanged(state=0)
    //3.other: no call onPageSelected()
    //onPageScrollStateChanged(state=1) -> onPageScrolled... -> onPageScrollStateChanged(state=2)
    // -> onPageScrolled... -> onPageScrollStateChanged(state=0)
    private class CycleOnPageChangeCallback(cycleViewPager2: CycleViewPager) : ViewPager.OnPageChangeListener {
        private var isBeginPagerChange: Boolean = false
        private var mTempPosition = INVALID_ITEM_POSITION
        private val weakReference = WeakReference<CycleViewPager>(cycleViewPager2)

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//            Logger.d("onPageScrolled: " + position + " positionOffset: " + positionOffset
//                    + " positionOffsetPixels: " + positionOffsetPixels)

            weakReference.get()?.mIndicator?.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            Log.d("-------","onPageSelected: $position")
            if (isBeginPagerChange) {
                mTempPosition = position
            }

            weakReference.get()?.mIndicator?.onPageSelected(weakReference.get()?.getRealCurrentItem()
                    ?: return)
        }

        override fun onPageScrollStateChanged(state: Int) {
//            Logger.d("onPageScrollStateChanged: state $state")
            val cp = weakReference.get() ?: return
            if (state == ViewPager.SCROLL_STATE_DRAGGING || cp.isTurning && state ==
                    ViewPager.SCROLL_STATE_SETTLING) {
                isBeginPagerChange = true
            } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                isBeginPagerChange = false
                val fixCurrentItem = getFixCurrentItem(mTempPosition)
                if (fixCurrentItem != INVALID_ITEM_POSITION && fixCurrentItem != mTempPosition) {
                    mTempPosition = INVALID_ITEM_POSITION
//                    Logger.d("onPageScrollStateChanged: fixCurrentItem $fixCurrentItem")
                    cp.setCurrentItem(fixCurrentItem, false)
                }
            }

            cp.mIndicator?.onPageScrollStateChanged(state)
        }

        private fun getFixCurrentItem(position: Int): Int {
            if (position == INVALID_ITEM_POSITION) return INVALID_ITEM_POSITION
            val lastPosition = Objects.requireNonNull<PagerAdapter>(weakReference
                    .get()?.getAdapter()).count - 1
            var fixPosition = INVALID_ITEM_POSITION
            if (position == 0) {
                fixPosition = if (lastPosition == 0) 0 else lastPosition - 1
            } else if (position == lastPosition) {
                fixPosition = 1
            }
            return fixPosition
        }

        companion object {
            private const val INVALID_ITEM_POSITION = -1
        }
    }

    private class AutoTurningRunnable(cycleViewPager: CycleViewPager) : Runnable {
        private val reference: WeakReference<CycleViewPager> = WeakReference(cycleViewPager)

        override fun run() {
            val cycleViewPager = reference.get()
            if (cycleViewPager != null && cycleViewPager.canAutoTurning && cycleViewPager.isTurning) {
                cycleViewPager.gotoNextItem()
                cycleViewPager.postDelayed(cycleViewPager.mAutoTurningRunnable, cycleViewPager.autoTurningTime)
            }
        }
    }

    private class SavedState : BaseSavedState {
        var mCurrentItem: Int = 0

        constructor(source: Parcel?) : super(source) {
            readValues(source, null)
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        constructor(source: Parcel?, loader: ClassLoader?) : super(source, loader) {
            readValues(source, loader)
        }

        constructor(superState: Parcelable) : super(superState)

        private fun readValues(source: Parcel?, loader: ClassLoader?) {
            mCurrentItem = source?.readInt() ?: 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mCurrentItem)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.ClassLoaderCreator<SavedState> {
            override fun createFromParcel(source: Parcel?, loader: ClassLoader?): SavedState {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    SavedState(source, loader)
                else
                    SavedState(source)
            }

            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}