package com.whx.practice.permission

interface ICheckPermission {
    fun checkPermissions(permissions: Array<String>, finishIfReject: Boolean, finishIfSetting: Boolean, grantCallback: () -> Unit)
}