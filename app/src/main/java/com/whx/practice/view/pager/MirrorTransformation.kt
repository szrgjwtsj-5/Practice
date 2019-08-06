package com.whx.practice.view.pager

import android.graphics.Bitmap
import com.squareup.picasso.Transformation
import com.whx.practice.utils.ImageUtils
import java.lang.IllegalArgumentException

class MirrorTransformation : Transformation {

    override fun transform(source: Bitmap?): Bitmap {
        if (source == null) throw IllegalArgumentException("源图片不能为空")

        return ImageUtils.getReverseBitmap(source, 0.5f)
    }

    override fun key(): String {
        return "mirror"
    }
}