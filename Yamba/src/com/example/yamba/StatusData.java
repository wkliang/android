package com.example.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatusData {
	private static final String TAG = StatusData.class.getSimpleName();
	
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "timeline";
	
	public static final String C_ID = "_id"; // BaseColumns._ID;
	public static final String C_CREATED_AT = "created_at";
	public static final String C_SOURCE = "source";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	private static final String[] MAX_CREATED_AT_COLUMNS = 
		{ "max(" + StatusData.C_CREATED_AT + ")"};
	private static final String[] DB_TEXT_COLUMNS = { C_TEXT };
	
	class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			/**
			 * @param context
			 * @param name
			 * @param factory
			 * @param version
			 */
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "create table " + TABLE + " (" + C_ID + " int primary key, " +
					C_CREATED_AT + " int , " + C_SOURCE + " text, "+ 
					C_USER + " text, " + C_TEXT + " text)";
			db.execSQL(sql);
			Log.d(TAG, "onCreated sql: " + sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Typically do ALTER TABLE statements, but... wre're just in development,
			// so:
			db.execSQL("drop table if exists " + TABLE);	// drops the old database
			Log.d(TAG, "onUpdated o:" + oldVersion + ", n:" + newVersion + ".");
			this.onCreate(db);	// run onCreate() to get new database
		}
	} // DbHelper
	
	private final DbHelper dbHelper;

	public SQLiteOpenHelper getDbHelper() {
		return dbHelper;
	}
	
	public StatusData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Initialized data");
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public void insertOrIgnore(ContentValues values) {
		if (this == null)
			Log.e(TAG, "null");
		Log.d(TAG, "insertOrIgnore on " + values);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			// db.insertOrThrow(TABLE, null, values);
			// Yamba service seems bound _id to MAX:1024
			// change CONFLICT_IGNORE with CONFLICT_REPLACE to work around
			db.insertWithOnConflict(TABLE, null, values,
					SQLiteDatabase.CONFLICT_REPLACE); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.close();
		}
	}
	
	/*
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */
	public Cursor getStatusUpdates() {
		if (this == null)
			Log.e(TAG, "null");
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	/*
	 * @return Timestamp of latest status we have it in the database
	 */
	public long getLatestStatusCreatedAtTime() {
		if (this == null)
			Log.e(TAG, "null");
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS, null, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
	
	/*
	 * @param id of the status we are looking for
	 * @return Text of the status
	 */
	public String getStatusTextById(long id) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
	
	public void deleteTimeline() {
		Log.i(TAG, "msgAllDataPurged");
		if (false) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			db.delete(TABLE, null, null);
			db.close();	
		}
	}
}
