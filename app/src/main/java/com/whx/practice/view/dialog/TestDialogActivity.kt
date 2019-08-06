package com.whx.practice.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.whx.practice.R
import kotlinx.android.synthetic.main.activity_common.*

class TestDialogActivity : AppCompatActivity() {

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        jump.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        if (dialog == null) {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null)

            dialog = Dialog(this, R.style.dialog_bottom_full)
            val window = dialog!!.window
            window.setGravity(Gravity.BOTTOM)
            window.setContentView(view)

            val lp = window.attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
        }
        if (!dialog!!.isShowing) {
            dialog!!.show()
        }
    }
}