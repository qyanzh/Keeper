package com.example.keeper.fragments

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.example.keeper.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}