package com.whx.practice.content

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.whx.practice.R
import kotlinx.android.synthetic.main.activity_content_test.*

/**
 * Created by whx on 2017/12/25.
 */
class ContentTestActivity : AppCompatActivity() {

    private val uri = MyContract.CONTENT_URI!!

    private val projections = arrayOf("_id", "word")
    private val selection = "frequency = ? and locale = ?"
    private val selectionArgs = arrayOf("233", "what")
    private val order = "frequency"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_test)

        insert_btn.setOnClickListener {
            val value = ContentValues().apply {
                put("word", insert_txt.text.toString())
                put("frequency", 233)
                put("locale", "中国")
            }
            contentResolver.insert(uri, value)
        }

        query_btn.setOnClickListener {
            val cursor = contentResolver.query(uri, projections, null, null, order)
            val sb = StringBuilder()

            while (cursor.moveToNext()) {
                sb.append(cursor.getString(cursor.getColumnIndex("word")))
                sb.append("\n")
            }
            query_text.text = sb
            cursor.close()
        }
    }
}