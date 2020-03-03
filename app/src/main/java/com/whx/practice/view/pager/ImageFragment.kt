package com.whx.practice.view.pager

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class ImageFragment : Fragment() {

    private var imageUrl: String? = null
    private var position = -1

    private var finishedListener: OnfinishListener? = null

    companion object {
        fun newInstance(imageUrl: String?, position: Int): ImageFragment {
            return ImageFragment().apply {
                val args = Bundle()
                args.putString("imageUrl", imageUrl)
                args.putInt("position", position)
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e("----------", "fragment#$position:  onActivityCreated")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val imageView = ImageView(context)
        return imageView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("----------", "fragment#$position:  onViewCreated")
        imageUrl = arguments?.getString("imageUrl")
        position = arguments?.getInt("position") ?: -1
        imageUrl?.let {
            Log.e("----------", "fragment#$position:  loadImage")
            Glide.with(this).load(imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Log.e("----------", "fragment#$position:  load fail")
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            Log.e("----------", "fragment#$position:  load success")
                            return false
                        }
                    })
                    .into(view as ImageView)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("----------", "fragment#$position:  isVisibleToUser:  $isVisibleToUser")
        if (isVisibleToUser && isResumed) {
            onResume()
            if (position == 2) {
                view?.postDelayed({
                    finishedListener?.onFinish()
                }, 2000)
            }
        } else {
//            videoPlayerHelper?.pausePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("----------", "fragment#$position:  onresume")
        if (!userVisibleHint || !checkIsVisible(view)) {
            return
        }
        Log.e("----------", "fragment#$position:  visible resume")
//        videoPlayerHelper?.resumePlayer()
    }

    override fun onPause() {
        super.onPause()
        Log.e("----------", "fragment#$position:  onpause")
//        videoPlayerHelper?.pausePlayer()
    }

    fun checkIsVisible(view: View?): Boolean {

        val screenWidth = activity?.resources?.displayMetrics?.widthPixels ?: 0

        val screenHeight = activity?.resources?.displayMetrics?.heightPixels ?: 0

        val rect = Rect(0, 0, screenWidth, screenHeight)

        val location = IntArray(2)

        view?.getLocationInWindow(location)

        return view?.getLocalVisibleRect(rect) ?: false
    }

    fun setOnfinishListener(finishListener: OnfinishListener) {
        this.finishedListener = finishListener
    }

    interface OnfinishListener {
        fun onFinish()
    }
}