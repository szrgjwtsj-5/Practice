package com.whx.practice.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by whx on 2018/1/4.
 */
class PreferenceTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }
}