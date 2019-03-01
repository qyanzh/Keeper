package com.example.keeper.fragments;

import android.os.Bundle;

import android.preference.PreferenceFragment;

import com.example.keeper.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}