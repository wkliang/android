package com.example.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	static final String TAG = UpdateService.class.getSimpleName();
	static final int DELAY = 60000;	// a minute
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	
	// work with DBHelper;
	DbHelper dbHelper;
	SQLiteDatabase db;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public void onCreate() {
		super.onCreate();
		
		this.yamba = (YambaApplication)getApplication();
		this.updater = new Updater();
	
		// pg#127 --- What does it mean?
		// Create the instance of DbHelper and pass this as its context. This works because the
		// Android Service class is a subclass of Context. DbHelper will figure out whether the
		// database needs to be created or upgraded.

		dbHelper = new DbHelper(this);
		
		Log.d(TAG, this.hashCode() + ".onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		if (runFlag) {
			Log.d(TAG, "already Started");
		} else {
			this.runFlag = true;
			this.updater.start();
			this.yamba.setServiceRunning(true);
		
			Log.d(TAG, "onStarted");
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.yamba.setServiceRunning(false);
		
		Log.d(TAG, "onDestroyed");
	}
	
	/**
	 * Thread that performs the actual update from the online service
	 */
	private class Updater extends Thread {
		List<Twitter.Status> timeline;
		
		public Updater() {
			super("UpdateService-updater");
		}
		
		@Override
		public void run() {
			UpdateService updateService = UpdateService.this;
			while (updateService.runFlag) {
				Log.d(TAG, "updater running");
				try {
					// Get the timeline from the cloud
					try {
						timeline = yamba.getTwitter().getFriendsTimeline();
					} catch(TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}
					
					// Open the database for writing
					db = dbHelper.getWritableDatabase();
					
					// Loop over the timeline and print it out				
					ContentValues values = new ContentValues();
					for (Twitter.Status status : timeline) {
						// Insert into database
						values.clear();
						values.put(DbHelper.C_ID, status.id);
						values.put(DbHelper.C_CREATED_AT, status.createdAt.getTime());
						// SOURCE is unknown
						values.put(DbHelper.C_SOURCE, status.source);
						values.put(DbHelper.C_TEXT, status.text);
						values.put(DbHelper.C_USER, status.user.name);
						
						try {
							db.insertOrThrow(DbHelper.TABLE, null, values);
							Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					// Close the database
					db.close();
					
					Log.d(TAG, "updater ran");
					Thread.sleep(DELAY);
					
				} catch(InterruptedException e) {
					updateService.runFlag = false;
				}
			}
		}
	} // Updater
	 
}
