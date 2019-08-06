package com.whx.practice.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.whx.practice.R
import com.whx.practice.view.accessibility.AccessibilityClickService
import kotlinx.android.synthetic.main.activity_simulate.*
import java.lang.ref.WeakReference

class SimulateTouchActivity : AppCompatActivity() {
    private val btn by lazy { findViewById<Button>(R.id.clickBtn) }

    private lateinit var handler: MyHandler
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_simulate)

        handler = MyHandler(this)
//        handler.sendEmptyMessageDelayed(233, 3000)

        btn.setOnClickListener {
            ToastCompat.makeTextCompat(this, "hhhhhhhhh", Toast.LENGTH_SHORT).show()
            Log.e("---------------", "teeeeeeest")
        }

        startService.setOnClickListener {
            if (!AccessibilityClickService.isRunning()) {
                startAccessibilityService()
            } else {
                ToastCompat.makeTextCompat(this, "service 已启动", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simulateTouchEvent(view: View?, x: Float, y: Float) {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = downTime + 100
        val metaState = 0
        val downEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState)
        val upEvent = MotionEvent.obtain(downTime + 1000, eventTime + 1000, MotionEvent.ACTION_UP, x, y, metaState)

        view?.dispatchTouchEvent(downEvent)
        view?.dispatchTouchEvent(upEvent)
    }

    private fun performClick(view: View?) {
        view?.performClick()
    }

    private fun startAccessibilityService() {
        if (dialog == null) {
            dialog = AlertDialog.Builder(this)
                    .setTitle("开启辅助功能")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setMessage("使用模拟点击需要开启辅助功能")
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("立即开启") { _, _ ->
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        startActivity(intent)
                    }
                    .create()
        }

        dialog!!.show()
    }

    private class MyHandler(ref: SimulateTouchActivity) : Handler() {
        private val weakRef = WeakReference<SimulateTouchActivity>(ref)

        override fun handleMessage(msg: Message?) {
            if (msg?.what == 233) {
                weakRef.get()?.simulateTouchEvent(weakRef.get()?.btn, 10f, 10f)
            }
        }
    }
}