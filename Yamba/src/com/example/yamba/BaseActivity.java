package com.example.yamba;

import winterwell.jtwitter.Twitter;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import android.content.Intent;

public class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";
	YambaApplication yamba;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		yamba = (YambaApplication)getApplication();
		
		// Check if preferences have been set
		Twitter twitter = yamba.getTwitter();
		if (twitter == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
		Log.i(TAG, "onCreated");			
	}

	// Called only once first time menu is clicked on
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	// Called every time user clicks on a menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemToggleService:
			if (yamba.isServiceRunning()) {
				stopService(new Intent(this, UpdateService.class));
			} else {
				startService(new Intent(this, UpdateService.class));
			}
			break;
		case R.id.itemPurge :
			yamba.getStatusData().deleteTimeline();
			Toast.makeText(this, R.string.msgAllDataPurged, Toast.LENGTH_LONG).show();
			break;
		case R.id.itemStatus :
			startActivity(new Intent(this, StatusActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
			break;
		case R.id.itemTimeline :
			startActivity(new Intent(this, TimelineActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
				.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;			
		}
		return true;
		
	}

	// Called every time menu is opened
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		MenuItem toggleItem = menu.findItem(R.id.itemToggleService);
		if (yamba.isServiceRunning()) {
			toggleItem.setTitle(R.string.titleServiceStop);
			toggleItem.setIcon(android.R.drawable.ic_media_pause);
		} else {
			toggleItem.setTitle(R.string.titleServiceStart);
			toggleItem.setIcon(android.R.drawable.ic_media_play);
		}
		return true;
	}

}
