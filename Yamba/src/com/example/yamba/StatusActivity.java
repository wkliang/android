package com.example.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity 
	implements OnClickListener, TextWatcher, LocationListener {
	private static final String TAG = "StatusActivity";
	private static final long LOCATION_MIN_TIME = 3600000; // One hour
	private static final float LOCATION_MIN_DISTANCE = 1000; // One kilometer
	TextView textCount;
	EditText editText;
	Button updateButton;
	LocationManager locationManager;
	Location location;
	String provider;
	
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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(TAG, "onResume");
		// Setup location information
		try {
		provider = yamba.getProvider();
		if (!YambaApplication.LOCATION_PROVIDER_NONE.equals(provider)) {
			Log.d(TAG, "b4 getSystemService(LOCATION_SERVICE)");
			locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		}
		if (locationManager != null) {
			Log.d(TAG, "b4 getLastKnownLocation");
			location = locationManager.getLastKnownLocation(provider);
			Log.d(TAG, "b4 requestLocationUpdates");
			locationManager.requestLocationUpdates(provider,
					LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
		} else {
			Log.d(TAG, "loc is null");
		}
		} finally {
			Log.d(TAG, "finally");
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
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
				if (location == null) {
					Log.d(TAG, "PostToTwitter loc is null");
				} else {
					double latlong[] = {location.getLatitude(), location.getLongitude()};
					Log.d(TAG, "lat:" + latlong[0] + ", long:" + latlong[1] + ".");
					twitter.setMyLocation(latlong);
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
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled");
		if (this.provider.equals(provider)) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled");
		if (this.provider.equals(provider)) {
			locationManager.requestLocationUpdates(provider,
					LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "onStatusChanged");
	}

/*
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
*/
	
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
