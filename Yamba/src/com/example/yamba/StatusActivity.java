package com.example.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher {
	private static final String TAG = "StatusActivity";
	TextView textCount;
	EditText editText;
	Button updateButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		// Find views
		editText = (EditText)findViewById(R.id.editText);
		updateButton = (Button)findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this);
		
		textCount = (TextView)findViewById(R.id.textCount);
		setTextCount(140);
		
		editText.addTextChangedListener(this);
		
		((YambaApplication)getApplication()).getTwitter();
		
		Log.i(TAG, "onCreated");
	}

	// Asynchronously posts to twitter
	class PostToTwitter extends AsyncTask<String, Integer, String> {
		// Called to initiate the background activity
		@Override
		protected String doInBackground(String... statuses) {
			try {
				Twitter twitter = ((YambaApplication)getApplication()).getTwitter();
				if (twitter == null) {
					final String tw_is_null = "Twitter connection info not initialized";
					Log.d(TAG, tw_is_null);
					return tw_is_null;
				}
				Twitter.Status status = twitter.updateStatus(statuses[0]);
				return status.text;
			} catch (TwitterException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
				return "Failed to post";
			}
		}
		
		// Called when there's status to be updated
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// Not used in this case
		}
		
		// Called once the background activity has completed
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}
	}

	// Called when button is clicked
	public void onClick(View v) {
		Log.d(TAG, "onClicked");

		String status = editText.getText().toString();
		// Old Yamba's way
		// twitter.setStatus(status);
		new PostToTwitter().execute(status);
	}
	
	private void setTextCount(int count) {
		textCount.setText(Integer.toString(count));
		if (count <= 0)
			textCount.setTextColor(Color.RED);
		else if (count <= 10)
			textCount.setTextColor(Color.YELLOW);
		else
			textCount.setTextColor(Color.GREEN);
	}
	
	// TextWatcher methods
	public void afterTextChanged(Editable statusText) {
		setTextCount(140 - statusText.length());
	}
	
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}
	
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemServiceStart:
			startService(new Intent(this, UpdateService.class));
			break;
		case R.id.itemServiceStop:
			stopService(new Intent(this, UpdateService.class));
			break;
		case R.id.itemStatusViewer:
			startActivity(new Intent(this, StatusViewer.class));
			break;
		case R.id.itemTimeline:
			startActivity(new Intent(this, TimelineActivity.class));
			break;			
		}
		return true;
		
	}
	
/*
 * http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently
 * 
 * SharedPreferences keeps listeners in a WeakHashMap. 
 * This means that you cannot use an anonymous inner class as a listener, 
 * as it will become the target of garbage collection as soon as
 * you leave the current scope. 
 *  
 prefs.registerOnSharedPreferenceChangeListener(
	new SharedPreferences.OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			Log.d(TAG, "onSharedPreferenceChanged: " + key);
		}
	});
*/
	
}
