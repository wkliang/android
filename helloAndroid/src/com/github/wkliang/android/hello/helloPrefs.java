package com.github.wkliang.android.hello;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class helloPrefs extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.prefs);
    }
}
