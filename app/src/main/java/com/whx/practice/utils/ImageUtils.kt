package com.whx.practice.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import java.io.IOException

object ImageUtils {

    @Throws
    @JvmStatic
    fun getBitmapCompressed(context: Context, uri: Uri): Bitmap? {
        var ins = context.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeStream(ins, null, options)
        ins.close()
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight
        if (originalWidth == -1 || originalHeight == -1) return null

        val hh = 800f; val ww = 480f
        var be = 1
        if (originalWidth > originalHeight && originalWidth > ww) {
            be = (originalWidth / ww).toInt()
        } else if (originalWidth < originalHeight && originalHeight > hh) {
            be = (originalHeight / hh).toInt()
        }
        if (be <= 0) {
            be = 1
        }

        val resOptions = BitmapFactory.Options().apply {
            inSampleSize = be
        }
        ins = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(ins, null, resOptions)
        ins.close()

        return bitmap
    }

    /**
     * 通过Uri获取文件路径
     */
    @JvmStatic
    fun getPathFromUri(context: Context?, uri: Uri?): String {
        if (context == null || uri == null) return ""

        val path = when (uri.scheme) {
            "file" -> uri.schemeSpecificPart
            "content" -> {
                var p = ""
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    val projection = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = context.contentResolver.query(uri, projection, null, null, null)

                    cursor?.let {
                        if (it.moveToFirst()) {
                            p = it.getString(0)
                        }
                        cursor.close()
                    }
                }
                p
            }
            else -> ""
        }
        return path
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    @JvmStatic
    fun getBitmapDegree(path: String): Float {
        var degree = 0F
        try {
            val exifInterface = ExifInterface(path)
            degree = when(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90F
                ExifInterface.ORIENTATION_ROTATE_180 -> 180F
                ExifInterface.ORIENTATION_ROTATE_270 -> 270F
                else -> 0F
            }
        } catch (e: IOException) {
            // do nothing
        }
        return degree
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    @JvmStatic
    fun rotateBitmapByDegree(bm: Bitmap, degree: Float): Bitmap {
        var res: Bitmap? = null

        val matrix = Matrix()       // 根据旋转角度，生成旋转矩阵
        matrix.postRotate(degree)

        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            res = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            // do nothing
        }

        if (res == null) {
            res = bm
        }
        if (bm != res) {
            bm.recycle()
        }

        return res
    }

    /**
     * 为bitmap 图片增加一个倒影
     */
    @JvmStatic
    fun getReverseBitmap(source: Bitmap, percent: Float): Bitmap {
        val divider = 10
        val matrix = Matrix()
        matrix.setScale(1f, -1f)

        val rvsBitmap = Bitmap.createBitmap(source, 0, (source.height * (1 - percent)).toInt(), source.width,
                (source.height * percent).toInt(), matrix, false)

        val result = Bitmap.createBitmap(source.width, source.height + rvsBitmap.height + divider, source.config)

        val canvas = Canvas(result)
        canvas.drawBitmap(source, 0f, 0f, null)
        canvas.drawBitmap(rvsBitmap, 0f, (source.height + divider).toFloat(), null)

        val paint = Paint()
        val shader = LinearGradient(0f, (source.height + divider).toFloat(), 0f, result.height.toFloat(),
                Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP)

        paint.isAntiAlias = true
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawRect(0f, (source.height + divider).toFloat(), source.width.toFloat(), result.height.toFloat(), paint)

        source.recycle()
        rvsBitmap.recycle()

        return result
    }
}