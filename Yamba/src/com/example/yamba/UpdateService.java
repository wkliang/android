package com.example.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	static final String TAG = UpdateService.class.getSimpleName();
	static final int DELAY = 60000;	// a minute
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	
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
		
		Log.d(TAG, this.hashCode() + ".onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		this.runFlag = true;
		this.updater.start();
		this.yamba.setServiceRunning(true);
		
		Log.d(TAG, "onStart");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.yamba.setServiceRunning(false);
		
		Log.d(TAG, "onDestroy");
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
					// Loop over the timeline and print it out
					for (Twitter.Status status : timeline) {
						Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
					}
					Log.d(TAG, "updater ran");
					Thread.sleep(DELAY);
					
				} catch(InterruptedException e) {
					updateService.runFlag = false;
				}
			}
		}
		
	}
	 
}
