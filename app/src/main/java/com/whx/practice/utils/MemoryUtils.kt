package com.whx.practice.utils

import android.app.ActivityManager
import android.content.Context
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

object MemoryUtils {

    /**
     *  获取android当前可用运行内存大小
     */
    @JvmStatic
    fun getAvailableMem(context: Context): Long {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)

        return memInfo.availMem         // 可用内存，单位B
    }

    @JvmStatic
    fun getTotalMem(): Long {
        val str = "/proc/meminfo"       // 系统内存信息文件

        try {
            val fileReader = FileReader(str)
            val bufferedReader = BufferedReader(fileReader, 8192)

            val s = bufferedReader.readLine()       // 读取meminfo 第一行，系统总内存大小
            val array = s.split("\\s+")

            val memStr = array.takeIf { it.isNotEmpty() }?.get(0)

            val mem = memStr?.filter { it.isDigit() }?.toInt() ?: 0     // 获得系统总内存，单位是KB

            return mem.toLong() * 1024              // int值乘以1024转换为long 类型，单位是B

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0
    }
}