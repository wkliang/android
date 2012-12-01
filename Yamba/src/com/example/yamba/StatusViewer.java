package com.example.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StatusViewer extends Activity {
	static final String TAG = "StatusViewer";
	SQLiteOpenHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	TextView textTimeline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.status_viewer);
		textTimeline = (TextView)findViewById(R.id.textTimeline);
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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		cursor = db.query("timeline", null, null, null, null, null, "created_at desc");
		startManagingCursor(cursor);
		
		String user, text, output;
		while (cursor.moveToNext()) {
			user = cursor.getString(cursor.getColumnIndex("user"));
			text = cursor.getString(cursor.getColumnIndex("txt"));
			output = String.format("%s: %s\n", user, text);
			textTimeline.append(output);
					
		}
	}
}
