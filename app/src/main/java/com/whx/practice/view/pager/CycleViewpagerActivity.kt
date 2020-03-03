package com.whx.practice.view.pager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.whx.practice.R

class CycleViewpagerActivity : AppCompatActivity() {

    private var viewPager: CycleViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycleviewpager)

        val data = arrayListOf("http://ww1.sinaimg.cn/large/0065oQSqly1fsfq1ykabxj30k00pracv.jpg", "http://ww1.sinaimg.cn/large/0065oQSqly1fsfq2pwt72j30qo0yg78u.jpg",
                "http://ww1.sinaimg.cn/large/0065oQSqly1fsb0lh7vl0j30go0ligni.jpg")

        viewPager = findViewById(R.id.viewpager2)
        viewPager?.setIndicator(DotIndicator(this).apply { setDotsPadding(30f) })
        viewPager?.setAdapter(MyFragmentAdapter(supportFragmentManager, data, viewPager!!))
//        viewPager?.setAutoTurning(2000)

//        (viewPager?.getAdapter() as MyFragmentAdapter).setData(data)

    }

    class MyAdapter(private val urls: List<String>) : BaseCycleAdapter() {

        override fun getRealItemCount(): Int {
            return urls.size
        }

        override fun instantiateRealItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            Picasso.with(container.context).load(urls[position]).into(imageView)
            container.addView(imageView)
            return imageView
        }
    }

    class MyFragmentAdapter(fragmentManager: FragmentManager, private val data: List<String>, private val viewPager: CycleViewPager) : BaseCycleFragmentAdapter(fragmentManager), ImageFragment.OnfinishListener {

        override fun getRealCount(): Int {
            return data.size
        }

        override fun getRealItem(position: Int): Fragment {
            return ImageFragment.newInstance(data[position], position).apply {
                setOnfinishListener(this@MyFragmentAdapter)
            }
        }

        override fun onFinish() {
            viewPager.gotoNextItem()
        }
//
//        fun setData(data: List<String>) {
//            this.data.clear()
//            this.data.addAll(data)
//            notifyDataSetChanged()
//        }
    }
}