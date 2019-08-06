package com.whx.practice.view.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.whx.practice.view.ToastCompat

@SuppressLint("LongLogTag")
class AccessibilityClickService : AccessibilityService() {

    companion object {
        const val TAG = "AccessibilityClickService----------"

        private var service: AccessibilityService? = null

        fun isRunning(): Boolean {
            if (service == null || service!!.serviceInfo == null) return false
            val accessibilityManager = service!!.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
            val infoList = accessibilityManager?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)

            var isConnect = false
            infoList?.forEach {
                if (it.id == service!!.serviceInfo.id) {
                    isConnect = true
                    return@forEach
                }
            }
            return isConnect
        }
    }

//    private val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        service = this
        Log.e(TAG, "connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkgName = event.packageName

        if (event.source != null) {
            if (pkgName == applicationContext.packageName && event.eventType != AccessibilityEvent.TYPE_VIEW_CLICKED) {
                performClick("com.whx.practice:id/clickBtn")
            }
        }

//        if (event.source.text == "Increase volume") {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                audioManager.adjustStreamVolume(AudioManager.STREAM_ACCESSIBILITY, AudioManager.ADJUST_RAISE, 0)
//            } else {
//                Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    override fun onInterrupt() {
        Log.e(TAG, "onInterrupt...")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        service = null
        return super.onUnbind(intent)
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        ToastCompat.makeTextCompat(this, event?.keyCode.toString(), Toast.LENGTH_SHORT).show()

        return super.onKeyEvent(event)
    }

    private fun performClick(id: String) {
        Log.e(TAG, "perform click...")

        val nodeInfo = this.rootInActiveWindow
        val targetNode = findNodeInfoById(nodeInfo, id)

        if (targetNode?.isClickable == true) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    private fun findNodeInfoById(nodeInfo: AccessibilityNodeInfo?, resId: String) : AccessibilityNodeInfo? {
        val list = nodeInfo?.findAccessibilityNodeInfosByViewId(resId)
        return list?.firstOrNull()
    }

    private fun findNodeInfoByText(nodeInfo: AccessibilityNodeInfo?, text: String) : AccessibilityNodeInfo? {
        val list = nodeInfo?.findAccessibilityNodeInfosByText(text)
        return list?.firstOrNull()
    }
}