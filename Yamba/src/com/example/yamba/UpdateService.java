package com.example.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	static final String TAG = UpdateService.class.getSimpleName();
	public static final String NEW_STATUS_INTENT = "com.example.yamba.NEW_STATUS";
	public static final String NEW_STATUS_INTENT_COUNT = "com.example.yamba.NEW_STATUS_COUNT";
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
	
		// pg#127 --- What does it mean?
		// Create the instance of DbHelper and pass this as its context. This works because the
		// Android Service class is a subclass of Context. DbHelper will figure out whether the
		// database needs to be created or upgraded.

		// dbHelper = new DbHelper(this);
		
		Log.d(TAG, this.hashCode() + ".onCreate");
	}
	
	@Override
	public synchronized int onStartCommand(Intent intent, int flags, int startId) {
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
		public Updater() {
			super("UpdateService-updater");
		}
		
		@Override
		public void run() {
			UpdateService updateService = UpdateService.this;
			Log.d(TAG, "updater running");
			while (updateService.runFlag) {
				try {
					YambaApplication yamba = (YambaApplication)updateService.getApplication();
					int newUpdates = yamba.fetchStatusUpdates();
					if (newUpdates > 0) {
						Log.d(TAG, newUpdates + " new status received");
						Intent intent = new Intent(NEW_STATUS_INTENT);
						intent.putExtra(NEW_STATUS_INTENT_COUNT, newUpdates);
						updateService.sendBroadcast(intent);
					}
					Thread.sleep(DELAY);
				} catch(InterruptedException e) {
					updateService.runFlag = false;
				}
			}
		}
	} // Updater
	 
}
