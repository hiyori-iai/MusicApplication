package com.example.musicapp;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class PrefsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "CPTR320";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey); //inflating the setting
    }
}
