package com.example.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.SQLException;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	private SharedPreferences prefs;
	private boolean serviceRunning;
	private StatusData statusData;
	public Twitter twitter;

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		statusData = new StatusData(this);
		Log.i(TAG, "onCreated");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		statusData.close();
		Log.i(TAG, "onTerminated");
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
	
	public StatusData getStatusData() {
		if (statusData == null) {
			Log.e(TAG, "statusData is null");
		}
		return statusData;
	}

	// Connects to the online service and puts the latest statuses into DB.
	// Returns the count of new statuses
	public synchronized int fetchStatusUpdates() {
		Log.d(TAG, "Fetching status updates");
		Twitter twitter = this.getTwitter();
		if (twitter == null) {
			Log.d(TAG, "Twitter connection info not initialized");
			return 0;
		}
		try {
			List<Twitter.Status> statusUpdates = twitter.getFriendsTimeline();
			long latestStatusCreatedAtTime = 
					this.getStatusData().getLatestStatusCreatedAtTime();
			int count = 0;
			// Loop over the timeline and print it out				
			ContentValues values = new ContentValues();
			for (Twitter.Status status : statusUpdates) {
				// Insert into database
				values.clear();
				values.put(StatusData.C_ID, status.getId());
				long createAt = status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT, createAt);
				values.put(StatusData.C_SOURCE, status.source);
				values.put(StatusData.C_TEXT, status.getText());
				values.put(StatusData.C_USER, status.getUser().getName());
				
				Log.d(TAG, "Got update with id " + status.getId() + ". Saving");
				this.getStatusData().insertOrIgnore(values);
				if (latestStatusCreatedAtTime < createAt) {
					count++;
				}
			}
			Log.d(TAG, count > 0 ? 
				"Got " + count + " status updates" : "No new status updates");
			return count;
		} catch (RuntimeException e) {
			return 0;
		}
	}
}
