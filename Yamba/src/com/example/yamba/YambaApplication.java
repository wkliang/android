package com.example.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	private SharedPreferences prefs;
	private boolean serviceRunning;
	public Twitter twitter;

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate");
	}
	
	public synchronized Twitter getTwitter() {
		if (twitter == null) {
			String username = this.prefs.getString("username","");
			String password = this.prefs.getString("password","");
			String apiRoot = this.prefs.getString("apiRoot", "http://yamba.marakana.com/api");
			if (!TextUtils.isEmpty(username) && 
					!TextUtils.isEmpty(password) &&
					!TextUtils.isEmpty(apiRoot)) {
				twitter = new Twitter(username, password);
				twitter.setAPIRootUrl(apiRoot);
			}
		}
		// TODO gotta find some way to prevent caller using null value
		if (twitter == null) {
			Log.e(TAG, "getTwitter() = null");
		}
		return twitter;
	}
	
	@Override
	public synchronized void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences,
			String key) {

		// invalidate twitter object
		twitter = null;

		Log.i(TAG, "onSharedPreferenceChanged: " + key);
	}
	
	public boolean isServiceRinning() {
		return this.serviceRunning;
	}
	
	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}

}
