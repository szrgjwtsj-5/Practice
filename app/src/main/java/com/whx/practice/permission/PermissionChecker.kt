package com.whx.practice.permission

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.whx.practice.permission.PermissionUtils.PERMISSION_REQUEST_CODE
import com.whx.practice.permission.PermissionUtils.checkPermissionState
import com.whx.practice.permission.PermissionUtils.getPermissionsDesc
import com.whx.practice.permission.PermissionUtils.requestPermission
import com.whx.practice.permission.PermissionUtils.shouldOpenManual
import com.whx.practice.view.ToastCompat
import java.lang.Exception

class PermissionChecker {

    private lateinit var grantCallback: () -> Unit
    private var hasPermissionRequest: Boolean = false
    private var finishIfReject = true
    private var finishIfSetting = true

    /**
     * Activity 检查权限，未授权的权限将自动请求
     */
    fun checkPermission(activity: Activity, permissions: Array<String>, grantCallback: () -> Unit, finishIfReject: Boolean, finishIfSetting: Boolean) {
        if (activity.isFinishing) return

        checkPermission(activity, permissions, grantCallback, finishIfReject, finishIfSetting) {requestPermission(activity, permissions)}
    }

    /**
     * Fragment 检查权限，未授权的权限将自动请求
     */
    fun checkPermission(fragment: Fragment, permissions: Array<String>, grantCallback:  () -> Unit, finishIfReject: Boolean, finishIfSetting: Boolean) {
        val act = fragment.activity
        if (!fragment.isAdded || act?.isFinishing == true) return

        checkPermission(act, permissions, grantCallback, finishIfReject, finishIfSetting) { requestPermission(fragment, permissions)}
    }

    private fun checkPermission(activity: Activity, permissions: Array<String>, grantCallback: () -> Unit, finishIfReject: Boolean, finishIfSetting: Boolean, requestMethod: (Array<String>) -> Unit) {
        this.grantCallback = grantCallback
        this.finishIfReject = finishIfReject
        this.finishIfSetting = finishIfSetting

        val state = checkPermissionState(activity, permissions)

        if (state.first) {
            grantCallback()
        } else {
            hasPermissionRequest = true
            requestMethod(state.second)
        }
    }

    /**
     * Activity 检查权限返回的结果处理
     */
    fun onPermissionRequestResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResult: IntArray) {
        onPermissionRequestResult(activity, requestCode, permissions, grantResult, {perms -> shouldOpenManual(activity, perms)}) { perms, callback ->
            checkPermission(activity, perms, callback, finishIfReject, finishIfSetting)
        }
    }

    /**
     * Fragment 检查权限返回的结果处理
     */
    fun onPermissionRequestResult(fragment: Fragment, requestCode: Int, permissions: Array<out String>, grantResult: IntArray) {
        val activity = fragment.activity
        if (!fragment.isAdded || activity == null) return

        onPermissionRequestResult(activity, requestCode, permissions, grantResult, {perms -> shouldOpenManual(fragment, perms)}) { perms, callback ->
            checkPermission(fragment, perms, callback, finishIfReject, finishIfSetting)
        }
    }

    private fun onPermissionRequestResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResult: IntArray, shouldShowTipMethod: (Array<String>) -> Pair<Boolean, Array<String>>, checkMethod: (Array<String>,  () -> Unit) -> Unit) {
        if (activity.isFinishing || requestCode != PERMISSION_REQUEST_CODE || !hasPermissionRequest || permissions.size != grantResult.size) return

        hasPermissionRequest = false

        val deniedPerm = permissions.filterIndexed { index, _ -> grantResult[index] != PackageManager.PERMISSION_GRANTED }.toTypedArray()  // 未被授权的权限

        if (deniedPerm.isEmpty()) {
            grantCallback()
        } else {
            val state = shouldShowTipMethod(deniedPerm)

            val builder = AlertDialog.Builder(activity)
                    .setCancelable(false)
                    .setNegativeButton("暂不") { _, _ ->
                        if (finishIfReject) {
                            activity.finish()
                        } else {
                            ToastCompat.makeTextCompat(activity, "${getPermissionsDesc(deniedPerm)}为必要权限，需要开通才能使用相关功能", Toast.LENGTH_SHORT).show()
                        }
                    }

            if (state.first) {      // 存在需要手动开启的权限
                builder.setMessage("${getPermissionsDesc(deniedPerm)}为必要权限，需要开通才能使用相关功能，请在系统设置中开启\n\n设置路径：设置->应用->Practice->权限")
                builder.setPositiveButton("去设置") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${activity.applicationContext.packageName}")
                    }
                    try {
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        //
                    }
                    if (finishIfSetting) {
                        activity.finish()
                    }
                }
            } else {
                builder.setMessage("${getPermissionsDesc(deniedPerm)}为必要权限，需要开通才能使用相关功能")
                builder.setPositiveButton("申请") { _, _ -> checkMethod(deniedPerm, grantCallback)}
            }
        }
    }

}