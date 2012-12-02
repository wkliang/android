package com.example.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	static final String TAG = "StatusViewer";
	// SQLiteOpenHelper dbHelper;
	// SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	static final String[] FROM = {StatusData.C_CREATED_AT, StatusData.C_USER, StatusData.C_TEXT};
	static final int[] TO = {R.id.textCreatedAt, R.id.textUser, R.id.textText};
	private IntentFilter filter;
	private TimelineReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		// Find your views
		listTimeline = (ListView)findViewById(R.id.listTimeline);
		
		// Create the receiver
		filter = new IntentFilter(UpdateService.NEW_STATUS_INTENT);
		receiver = new TimelineReceiver();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// db.close();
		yamba.getStatusData().close();
	}
	
	// View binder constant to inject business logic that converts a timestamp to
	// relative time
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.textCreatedAt) {
				return false;				
			}
			// Update the created at text to relative time
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(
					view.getContext(), timestamp);
			((TextView)view).setText(relTime);
			return true;
		}
	};
	
	// Responsible for fetching data and setting up the list and adapter
	private void setupList() {
		// Get the data from the database
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
				
		// Set up the adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
		
		// cursor.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
		this.setupList();
		
		// Register the receiver
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Unregister the receiver
		unregisterReceiver(receiver);
	}
	
	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// cursor.requery();
			// adapter.notifyDataSetChanged();
			setupList();
			int count = intent.getIntExtra(UpdateService.NEW_STATUS_INTENT_COUNT, 0);
			Toast.makeText(context, count + " received", Toast.LENGTH_SHORT).show();
			Log.d("TimelineReceiver", "onReceived");
		}
	}
}
