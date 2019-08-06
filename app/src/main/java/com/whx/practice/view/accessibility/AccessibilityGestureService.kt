package com.whx.practice.view.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.FingerprintGestureController
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AccessibilityGestureService : AccessibilityService() {

    private var gestureController: FingerprintGestureController? = null
    private var gestureCallback: FingerprintGestureController.FingerprintGestureCallback? = null
    private var gestureDetectionAvailable = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        gestureController = fingerprintGestureController
        gestureDetectionAvailable = gestureController?.isGestureDetectionAvailable ?: false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected() {
        super.onServiceConnected()

        if (gestureCallback != null || !gestureDetectionAvailable) return

        gestureCallback = object : FingerprintGestureController.FingerprintGestureCallback() {
            override fun onGestureDetected(gesture: Int) {
                when(gesture) {
                    FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN -> moveCursorDown()
                    FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT -> moveCursorLeft()
                    FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT -> moveCursorRight()
                    FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP -> moveCursorUp()
                    else -> Log.e("------------", "Error: unknown gesture type detected")
                }
            }

            override fun onGestureDetectionAvailabilityChanged(available: Boolean) {
                gestureDetectionAvailable = available
            }
        }
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    private fun moveCursorDown() {
        Toast.makeText(this, "move down", Toast.LENGTH_SHORT).show()
    }

    private fun moveCursorLeft() {
        Toast.makeText(this, "move left", Toast.LENGTH_SHORT).show()
    }

    private fun moveCursorRight() {
        Toast.makeText(this, "move right", Toast.LENGTH_SHORT).show()
    }

    private fun moveCursorUp() {
        Toast.makeText(this, "move up", Toast.LENGTH_SHORT).show()
    }
}