package com.whx.practice.permission

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

object PermissionUtils {
    const val PERMISSION_REQUEST_CODE = 233

    private val permissionsMap: Map<String, String> = mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to "定位权限",
            Manifest.permission.ACCESS_COARSE_LOCATION to "定位权限",
            Manifest.permission.WRITE_EXTERNAL_STORAGE to "存储权限",
            Manifest.permission.READ_EXTERNAL_STORAGE to "存储权限",
            Manifest.permission.READ_PHONE_STATE to "设备信息权限",
            Manifest.permission.CAMERA to "相机权限",
            Manifest.permission.READ_CONTACTS to "联系人权限",
            Manifest.permission.WRITE_CONTACTS to "联系人权限"
    )


    fun getPermissionsDesc(permissions: Array<String>): String {
        val desc = permissions.mapNotNull { permissionsMap[it] }.distinct()
        return if (desc.isEmpty()) "" else desc.reduce { acc, s -> "$acc, $s" }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermission(fragment: Fragment, permissions: Array<String>) {
        val act = fragment.activity
        if (!fragment.isAdded || act?.isFinishing == true) return

        fragment.requestPermissions(permissions, PERMISSION_REQUEST_CODE)
    }

    fun requestPermission(activity: Activity, permissions: Array<String>) {
        if (activity.isFinishing) return
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE)
    }

    /**
     * Activity 检查是否存在需要手动开启的权限，原因是用户点了 "不再提示"
     */
    fun shouldOpenManual(activity: Activity, permissions: Array<String>): Pair<Boolean, Array<String>> {
        if (activity.isFinishing) return false to arrayOf()

        val per = permissions.filter { !ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
        return per.isNotEmpty() to per.toTypedArray()
    }

    /**
     * Fragment 检查是否存在需要手动开启的权限，原因是用户点了 "不再提示"
     */
    fun shouldOpenManual(fragment: Fragment, permissions: Array<String>): Pair<Boolean, Array<String>> {
        val activity = fragment.activity
        if (!fragment.isAdded && activity?.isFinishing == true) return false to arrayOf()

        val per = permissions.filter { !ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
        return per.isNotEmpty() to per.toTypedArray()
    }

    /**
     * 检测一个/多个权限的授予状态
     * @return Boolean true为权限全部授予 false为部分未被授予
     * @return Array<String> 未被授予的权限
     */
    fun checkPermissionState(activity: Activity, permissions: Array<String>): Pair<Boolean, Array<String>> {
        val deniedPermissions = permissions.filter { ContextCompat.checkSelfPermission(activity.applicationContext, it) == PackageManager.PERMISSION_DENIED }

        return deniedPermissions.isEmpty() to deniedPermissions.toTypedArray()
    }
}