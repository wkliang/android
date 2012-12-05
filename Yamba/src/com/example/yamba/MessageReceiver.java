package com.example.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class MessageReceiver extends BroadcastReceiver {
	public static final String TAG = "MessageReceiver";
	// static final String NetworkReceiver = "android.net.conn.CONNECTIVITY_CHANGE";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, intent.getAction());
		
		ConnectivityManager cm = 
			(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ani = cm.getActiveNetworkInfo();
		if (ani != null) {
			context.startService(new Intent(context, UpdateService.class));
			logMsg(context, "ActiveNetwork:" + ani.getTypeName());
		}
		else if (intent.getAction().equals(cm.CONNECTIVITY_ACTION) &&
				intent.getBooleanExtra(cm.EXTRA_NO_CONNECTIVITY, false)) {
			context.stopService(new Intent(context, UpdateService.class));
			logMsg(context, "Yamba stopService");
		} 
	}
	
	private void logMsg(Context ctx, String msg) {
		Log.d(TAG, msg);
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}
}
