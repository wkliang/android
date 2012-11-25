package com.example.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class StatusActivity extends Activity 
	implements OnClickListener, TextWatcher {
	private static final String TAG = "StatusActivity";
	TextView textCount;
	EditText editText;
	Button updateButton;
	Twitter twitter;
	
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
		
		twitter = new Twitter("student", "password");
		twitter.setAPIRootUrl("http://yamba.marakana.com/api");
	}

	// Asynchronously posts to twitter
	class PostToTwitter extends AsyncTask<String, Integer, String> {
		// Called to initiate the background activity
		@Override
		protected String doInBackground(String... statuses) {
			try {
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
		String status = editText.getText().toString();
		// Old Yamba's way
		// twitter.setStatus(status);
		new PostToTwitter().execute(status);
		Log.d(TAG, "onClicked");
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
		getMenuInflater().inflate(R.menu.activity_status, menu);
		return true;
	}

}
