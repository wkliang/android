package com.example.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class YambaWidget extends AppWidgetProvider {
	private static final String TAG = YambaWidget.class.getSimpleName();
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Cursor c = context.getContentResolver().query(StatusProvider.CONTENT_URI, null, null, null, null);
		try {
			Log.d(TAG, "return:" + c.getCount());
			if (!c.moveToFirst()) {
				Log.d(TAG, "No data to update");
				for (int appWidgetId: appWidgetIds) {
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yamba_widget);
				 	views.setTextViewText(R.id.textUser, "user");
				 	views.setTextViewText(R.id.textCreatedAt, "createdAt");
				 	views.setTextViewText(R.id.textText, "message");
				 	views.setOnClickPendingIntent(R.id.yamba_icon, 
						PendingIntent.getActivity(context, 0, 
								new Intent(context, TimelineActivity.class), 0));
				 	appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			} else {
				CharSequence user = c.getString(c.getColumnIndex(StatusData.C_USER));
				CharSequence createdAt = DateUtils.getRelativeTimeSpanString(context, 
						c.getLong(c.getColumnIndex(StatusData.C_CREATED_AT)));
				CharSequence message = c.getString(c.getColumnIndex(StatusData.C_TEXT));
				// Loop through all instance of this widget
				for (int appWidgetId: appWidgetIds) {
					Log.d(TAG, "Updating widget" + appWidgetId);
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yamba_widget);
					views.setTextViewText(R.id.textUser, user);
					views.setTextViewText(R.id.textCreatedAt, createdAt);
					views.setTextViewText(R.id.textText, message);
					views.setOnClickPendingIntent(R.id.yamba_icon, 
							PendingIntent.getActivity(context, 0, 
									new Intent(context, TimelineActivity.class), 0));
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
				
				
			}
    	} catch (SQLiteException ex) {
    		Log.d(TAG, ex.getMessage());
		} finally {
			c.close();
		}
		Log.d(TAG, "onUpdated");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals(UpdateService.NEW_STATUS_INTENT)) {
			Log.d(TAG, "onReceived detected new status update");
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			this.onUpdate(context, awm, awm.getAppWidgetIds(new ComponentName(context, YambaWidget.class)));
		}
			
	}
}
