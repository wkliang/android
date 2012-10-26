package com.github.wkliang.android.hello;

import android.app.Activity;
import android.os.Bundle;

import android.view.Menu;
import android.widget.TextView;

public class helloAndroid extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	TextView tv = new TextView(this);
	tv.setText("你好，安桌椅！");
	setContentView(tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }
}
