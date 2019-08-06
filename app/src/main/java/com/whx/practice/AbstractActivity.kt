package com.whx.practice

import android.support.v7.app.AppCompatActivity
import com.whx.practice.permission.ICheckPermission
import com.whx.practice.permission.PermissionChecker

abstract class AbstractActivity : AppCompatActivity(), ICheckPermission {
    private val permissionChecker = PermissionChecker()

    override fun checkPermissions(permissions: Array<String>, finishIfReject: Boolean, finishIfSetting: Boolean, grantCallback: () -> Unit) {
        permissionChecker.checkPermission(this, permissions, grantCallback, finishIfReject, finishIfSetting)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionChecker.onPermissionRequestResult(this, requestCode, permissions, grantResults)
    }

}