package com.whx.practice.view

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by whx on 2018/1/8.
 */
class ToastCompat(context: Context) : Toast(context) {

    companion object {
        @JvmField val TAG = "ToastCompat"

        @JvmStatic fun makeTextCompat(context: Context, text: CharSequence, duration: Int): Toast{
            val result = ToastCompat(context)

            val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflate.inflate(context.resources.getIdentifier("transient_notification", "layout", "android"), null)
            val tv = view.findViewById<TextView>(android.R.id.message)

            tv.text = text
            result.view = view
            result.duration = duration

            return result
        }

        @JvmStatic private fun setFieldValue(obj: Any, fieldName: String, newValue: Any) {
            val field = getDeclaredField(obj, fieldName)
            field?.let {
                val modifierField = Field::class.java.getDeclaredField("accessFlags")
                modifierField.isAccessible = true
                modifierField.setInt(field, field.modifiers and Modifier.FINAL.inv())

                if (!field.isAccessible) {
                    field.isAccessible = true
                }
                field.set(obj, newValue)
            }

        }

        @JvmStatic private fun getFieldValue(obj: Any, fieldName: String): Any? {
            val field = getDeclaredField(obj, fieldName)
            field?.let {
                if (!field.isAccessible) {
                    field.isAccessible = true
                }
                return field.get(obj)
            }
            return null
        }

        @JvmStatic private fun getDeclaredField(obj: Any, fieldName: String): Field? {
            var superClass = obj.javaClass
            while (superClass != Any::class.java) {
                try {
                    return superClass.getDeclaredField(fieldName)
                } catch (e: NoSuchFieldException) {
                    superClass = superClass.getSuperclass()
                    continue
                }
            }
            return null
        }
    }

    override fun show() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            fixBug()
        }
        super.show()
    }

    private fun fixBug() {
        try {
            val mTN = getFieldValue(this, "mTN")
            mTN?.let {
                val rawHandler = getFieldValue(mTN, "mHandler") as? Handler
                rawHandler?.let {
                    setFieldValue(rawHandler, "mCallback", InternalHandlerCallback(rawHandler))
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private class InternalHandlerCallback(private val mHandler: Handler) : Handler.Callback {

        override fun handleMessage(msg: Message?): Boolean {
            try {
                mHandler.handleMessage(msg)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return true
        }
    }
}