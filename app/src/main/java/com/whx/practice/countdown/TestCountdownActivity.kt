package com.whx.practice.countdown

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import com.whx.practice.AbstractActivity
import com.whx.practice.R
import com.whx.practice.utils.CountdownTimer
import kotlinx.android.synthetic.main.activity_common.*
import java.lang.ref.WeakReference

class TestCountdownActivity : AbstractActivity() {

    private lateinit var handler: CHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        handler = CHandler(this)

        jump.setOnClickListener {
            val timer = object : CountdownTimer(20000, 2000) {
                override fun onTick(millisUntilFinished: Long) {
                    val time = System.currentTimeMillis().toString()
                    Log.e("start----------", time)
                    name.text = time            // millisUntilFinished.toString()
                    doSomething(this)
                }

                override fun onFinish() {
                    val time = System.currentTimeMillis().toString()
                    Log.e("end-----------", time)
                    name.text = "finish.........$time"
                }
            }

//            val t1 = Thread {
//                timer.start()
//            }
//            val t2 = Thread {
//                timer.start()
//            }
//            val t3 = Thread {
//                timer.start()
//            }
//
//            t1.start()
//            t2.start()
//            t3.start()
            timer.start()
//            testCountdown()
        }
    }

    fun doSomething(timer: CountdownTimer) {
        val t = Thread(Run(timer, this))
        t.start()
    }

    class CHandler(ref: TestCountdownActivity): Handler() {
        private val weakRef = WeakReference(ref)

        override fun handleMessage(msg: Message?) {
            if (msg?.what == 233) {
                (msg.obj as? CountdownTimer)?.cancel()
            }
        }

    }

    class Run(private val timer: CountdownTimer, ref: TestCountdownActivity) : Runnable {
        private val weakRef = WeakReference(ref)

        override fun run() {
            Thread.sleep(5000)
            weakRef.get()?.handler?.let {
                it.sendMessage(Message.obtain(it, 233, timer))
            }
        }
    }

    fun testCountdown() {
        val timer = object : CountDownTimer(20000, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("start----------", System.currentTimeMillis().toString())
                name.text = millisUntilFinished.toString()
            }

            override fun onFinish() {
                Log.e("end-----------", System.currentTimeMillis().toString())
                name.text = "finish........."
            }
        }
        timer.start()
    }
}