package com.whx.practice.activity

import android.os.Bundle
import android.preference.PreferenceFragment
import com.whx.practice.R

/**
 * Created by whx on 2018/1/4.
 */
class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.settings)
    }
}