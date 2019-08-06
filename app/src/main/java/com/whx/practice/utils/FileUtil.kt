package com.whx.practice.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.whx.practice.threadpool.act.CollectionUtils
import java.io.*
import java.util.*

object FileUtil {

    /**
     * 获取文件大小，支持2 G 以上文件
     */
    @JvmStatic
    fun getFileSize(context: Context, uri: Uri): Long {
        val file = context.contentResolver.openInputStream(uri)
        val fc = (file as? FileInputStream)?.channel

        return fc?.size() ?: 0
    }

    /**
     * 关闭流 或 资源
     */
    @JvmStatic
    fun close(source: Closeable?) {
        try {
            source?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 初始化一个图片文件路径
     *
     * @return
     */
    @JvmStatic
    fun initRandomImageFileOnSdcard(context: Context?): File? {
        if (context == null) {
            return null
        }

        val fileName = UUID.randomUUID().toString() + ".jpg"

        //检查sdcard是否存在
        if (!isSdcardValid()) {
            return null
        }

        val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (fileDir != null) {
            if (!fileDir.exists()) {
                if (fileDir.mkdirs()) {
                    //创建图片文件
                    return File(fileDir.absolutePath + File.separator + fileName)
                }
            } else {
                return File(fileDir.absolutePath + File.separator + fileName)
            }
        }

        return null
    }


    /**
     * 判断SD卡是否可用
     */
    @JvmStatic
    fun isSdcardValid(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 安全删除文件
     *
     * @param file
     */
    @JvmStatic
    fun deleteFile(file: File?): Boolean {
        return if (file != null && file.exists()) {
            file.delete()
        } else false
    }

    /**
     * 安全删除文件
     *
     * @param fileList
     */
    @JvmStatic
    fun deleteFiles(fileList: List<File?>) {
        if (CollectionUtils.isEmpty(fileList)) {
            return
        }
        for (file in fileList) {
            if (file != null && file.exists()) {
                file.delete()
            }
        }

    }

    /**
     * 获取文件的二进制数组
     */
    @JvmStatic
    fun getBytesWithFile(file: File): ByteArray? {
        var result: ByteArray?
        var fis: FileInputStream? = null
        var bos: ByteArrayOutputStream? = null
        try {
            fis = FileInputStream(file)
            bos = ByteArrayOutputStream()
            val bs = ByteArray(4096)
            var len = fis.read(bs, 0, bs.size)


            while (len != -1) {
                bos.write(bs, 0, len)
                len = fis.read(bs, 0, bs.size)
            }
            result = bos.toByteArray()
        } catch (e: Exception) {
            result = null
        } finally {
            try {
                fis?.close()
                bos?.close()
            } catch (e: IOException) {
            }

        }
        return result
    }

    /**
     * 判断文件是否存在
     */
    @JvmStatic
    fun isFileExist(filePath: String): Boolean {
        return try {
            File(filePath).exists()
        } catch (e: Exception) {
            false
        }

    }
}