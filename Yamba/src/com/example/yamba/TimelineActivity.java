package com.example.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineActivity extends Activity {
	static final String TAG = "StatusViewer";
	SQLiteOpenHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	static final String[] FROM = {StatusData.C_CREATED_AT, StatusData.C_USER, StatusData.C_TEXT};
	static final int[] TO = {R.id.textCreatedAt, R.id.textUser, R.id.textText};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		// Find your views
		listTimeline = (ListView)findViewById(R.id.listTimeline);
		
		// Connect to database
		dbHelper = new SQLiteOpenHelper(this, "timeline.db", null, 1) {
			@Override
			public void onCreate(SQLiteDatabase db) {
				Log.i(TAG, "dbHelper.onCreate");
			}
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				Log.i(TAG, "dbHelper.onUpgrade");
			}
		};
		db = dbHelper.getReadableDatabase();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
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
	
	@Override
	protected void onResume() {
		super.onResume();
	
		// Get the data from the database
		cursor = db.query("timeline", null, null, null, null, null, "created_at desc");
		startManagingCursor(cursor);
		
		// Set up the adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}
}
