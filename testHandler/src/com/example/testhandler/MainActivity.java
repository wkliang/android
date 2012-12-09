package com.example.testhandler;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/*
 * 利用Handler更新Android UI的另一种方法 
 * http://rayleung.iteye.com/blog/435147
 */
public class MainActivity extends Activity implements SensorEventListener {
	class MyView extends View {
		private float x;

		public MyView(Context context) {
			super(context);
			reset();
			Log.d("MyView", "constructed");
		}
		public void update() {
			postInvalidate();
		}
		public void reset() {
			x = 0;
		}
		public void setDirection(float direction) {
			// invalidate();	// request to be redrawn
		}
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			int h = this.getHeight();
			int w = this.getWidth();
			x += 1;
			// Log.d("onDraw", "x="+x+", w="+w+", h="+h);
			
			Paint mPaint = new Paint();
			mPaint.setColor(Color.BLUE);
			mPaint.setAntiAlias(true);
			canvas.drawCircle(x, 40, 40, mPaint); // .drawRect(x, 40, x+40, 80, mPaint);
		}
	}
	
	private MyView mView;
	private Handler mHandler;
	private Runnable mRun;
	private SensorManager sensorManager;
	private Sensor sensor;
	private boolean running = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set full screen view
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mView = new MyView(this);
		mHandler = new Handler();
		mRun = new Runnable() {
			public void run() {
				mView.postInvalidate(); // update();
				if (running)
					mHandler.postDelayed(mRun, 5);
			}
		};
		
		// Got sensor and sensor manager
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		setContentView(mView);	// setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// if (mView != null)
		// mView.reset();
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		running = true;
		mHandler.post(mRun);
	}
	
	@Override 
	public void onPause() {
		super.onPause();
		running = false;
		sensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		mView.reset();
    	return true;
    }

	// Ignore accuracy changes
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	// Listen to sensor and provide output
	public void onSensorChanged(SensorEvent event) {
		// Log.d("Compass", "Got sensor event:" + event.values[0]);
		mView.setDirection(event.values[0]);
	}
}
