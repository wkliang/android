/**
 * 
 */
package com.example.yamba;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author wkliang
 *
 */
public class DbHelper extends SQLiteOpenHelper {
	static final String TAG = "DbHelper";
	
	static final String DB_NAME = "timelline.db";
	static final int DB_VERSION = 2;
	
	static final String TABLE = "timeline";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE = "source";
	static final String C_TEXT = "txt";
	static final String C_USER = "user";
	
	Context context;
	
	public DbHelper(Context context) {
		/**
		 * @param context
		 * @param name
		 * @param factory
		 * @param version
		 */
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, " +
				C_CREATED_AT + " int , " + C_SOURCE + " text, "+ 
				C_USER + " text, " + C_TEXT + " text)";
		db.execSQL(sql);
		Log.d(TAG, "onCreated sql: " + sql);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Typically do ALTER TABLE statements, but... wre're just in development,
		// so:
		db.execSQL("drop table if exists " + TABLE);	// drops the old database
		Log.d(TAG, "onUpdated o:" + oldVersion + ", n:" + newVersion + ".");
		onCreate(db);	// run onCreate() to get new database
	}

}
