package com.whx.practice.view.accessibility

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AccessibilityBtnService : AccessibilityService() {

    private var mBtnController: AccessibilityButtonController? = null
    private var mBtnCallback: AccessibilityButtonController.AccessibilityButtonCallback? = null
    private var mAccessibilityBtnAvailable = false

    @TargetApi(Build.VERSION_CODES.O)
    override fun onServiceConnected() {
        super.onServiceConnected()

        mBtnController = accessibilityButtonController
        mAccessibilityBtnAvailable = mBtnController?.isAccessibilityButtonAvailable ?: false

        if (!mAccessibilityBtnAvailable) return

        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
        }

        mBtnCallback = object : AccessibilityButtonController.AccessibilityButtonCallback() {
            override fun onClicked(controller: AccessibilityButtonController?) {
                Toast.makeText(this@AccessibilityBtnService, "accessibility button click", Toast.LENGTH_SHORT).show()
            }

            override fun onAvailabilityChanged(controller: AccessibilityButtonController?, available: Boolean) {
                if (controller == mBtnController) {
                    mAccessibilityBtnAvailable = available
                }
            }
        }
        mBtnCallback?.also {
            mBtnController?.registerAccessibilityButtonCallback(it)
        }
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }
}