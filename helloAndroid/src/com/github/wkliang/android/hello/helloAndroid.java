package com.github.wkliang.android.hello;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import android.preference.PreferenceManager;

import android.telephony.TelephonyManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class helloAndroid extends Activity
	implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "helloAndroid";
    SharedPreferences prefs;
    StringBuilder strBuilder;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        strBuilder = new StringBuilder();

        setContentView(R.layout.main);

	// TextView tv = new TextView(this);
	// TextView tv = (TextView)findViewById(R.id.helloTextView);
	// tv.setTextSize(20);
	// tv.setText("你好，安桌椅！" + stringFromJNI() + "\n");
	// tv.append(getPhoneInformation() + "\n");
	// tv.append(getWifiInformation() + "\n");
	// setContentView(tv);

        Button btn = (Button)findViewById(R.id.buttonHello);
        btn.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		TextView tv = (TextView)findViewById(R.id.helloTextView);
        		tv.setTextSize(16);
        		strBuilder.append("你好，安桌椅！");
        		strBuilder.append(stringFromJNI() + "\n");
        		strBuilder.append(getPhoneInformation() + "\n");
        		strBuilder.append(getWifiInformation() + "\n");
        		getSmsInPhone(strBuilder);
        		tv.setText(strBuilder.toString());
        	}
        });

    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy(); 	
    	Log.i(TAG, "DESTROYED");
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i(TAG, "RESUME");
    }
    
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	Log.d(TAG, "onSharedPreferenceChanged:"+key);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu; this adds item to the action bar if it is presents.
    	getMenuInflater().inflate(R.menu.main, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.id_settings:
    		startActivity(new Intent(this, helloPrefs.class));
    		break;
    	case R.id.id_email:
    		sendEmail();
    		break;
    	case R.id.id_quit:
    		Log.d(TAG, "-=-=-=-=- QUIT -=-=-=-=-");
    		finish(); // end application ?
    		break;
    	} 
    	return true;
    }

// ref: http://stackoverflow.com/questions/2197741/how-to-send-email-from-my-android-application
// following one is using low level protocol to email
// http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a
    private void sendEmail() {
    	Intent i = new Intent(Intent.ACTION_SEND);
    	i.setType("message/rfc822");
    	i.putExtra(Intent.EXTRA_EMAIL,
    		new String[] {prefs.getString("emailRecipient","nobody")});
    	i.putExtra(Intent.EXTRA_SUBJECT, prefs.getString("emailSubject","title"));
    	i.putExtra(Intent.EXTRA_TEXT, strBuilder.toString());
    	try {
    		startActivity(Intent.createChooser(i, "send mail"));
    	} catch (android.content.ActivityNotFoundException ex) {
    	}
    }

    private String getWifiInformation() {
    	String str = "";

    	WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wi = wm.getConnectionInfo();

    	str += "MacAddr: " + wi.getMacAddress() + "\n";
    	str += "BSSID: " + wi.getBSSID() + "\n";
    	str += "SSID: " + wi.getSSID() + "\n";
    	str += "IpAddr: " + wi.getIpAddress() + "\n";

    	return str;
    }

    private String getPhoneInformation() {
    	String str = "";
    	TelephonyManager tm =  (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	str += "DeviceId(IMEI): " + tm.getDeviceId() + "\n";
    	str += "DeviceSoftwareVersion: " + tm.getDeviceSoftwareVersion() + "\n";
    	str += "Line1Number: " + tm.getLine1Number() + "\n";
    	str += "NetworkCountryIso: " + tm.getNetworkCountryIso() + "\n";

    	String networkOperator = tm.getNetworkOperator();
    	String networkOperatorName = tm.getNetworkOperatorName();
    	str += "NetworkOperator: "+networkOperator+","+networkOperatorName+"\n";

    	String[] networkTypeArray = {"UNKNOWN","GPRS","EDGE","UMTS","CDMA","EVDO 0","EVDO A","1xRTT","HSDPA","HSUPA","HSPA"};
    	Integer networkType = tm.getNetworkType();
    	str += "networkType: " + ((networkType >= networkTypeArray.length) ? networkType : networkTypeArray[networkType]) + "\n";

    	String[] phoneTypeArray = {"NONE","GSM","CDMA"};
    	Integer phoneType = tm.getPhoneType();
    	str += "phoneType: " + ((phoneType >= phoneTypeArray.length) ? phoneType : phoneTypeArray[phoneType]) + "\n" ;

    	str += "SimCountryIso: " + tm.getSimCountryIso() + "\n";
    	str += "SimOperator: " + tm.getSimOperator() + "\n";
    	str += "SimOperatorName: " + tm.getSimOperatorName() + "\n";
    	str += "SimSerialNumber: " + tm.getSimSerialNumber() + "\n";
    	str += "SimState: " + tm.getSimState() + "\n";

    	str += "Roaming: " + (tm.isNetworkRoaming() ? "yes" : "no") + "\n";
    	str += "SubscriberId(IMSI): " + tm.getSubscriberId() + "\n";
    	str += "VoiceMailNumber: " + tm.getVoiceMailNumber() + "\n";

    	return str;
    }

    // ref: http://blog.csdn.net/sunboy_2050/article/details/7328321
    private void getSmsInPhone(StringBuilder sb) {
    	final String SMS_URI = "content://sms/";
    	try {
    		Uri uri = Uri.parse(SMS_URI);
    		String[] projection = new String[] {"_id", "address", "person", "body", "date", "type"};
    		Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");
    		if (!cur.moveToFirst()) {
    			sb.append("no SMS content...\n\n");
    		} else do {
    			sb.append(new SimpleDateFormat("yyy-MM-dd hh:mm:ss").format(
						cur.getLong(cur.getColumnIndex("date"))));
    			switch(cur.getInt(cur.getColumnIndex("type"))) {
    			case 1 : 
    				sb.append(" rx<<");
    				break;
    			case 2 :
    				sb.append(" tx>>");
    				break;
    			default :
    				sb.append(" null:");
    			};
				sb.append(cur.getString(cur.getColumnIndex("address"))+",");
    			sb.append(cur.getInt(cur.getColumnIndex("person"))+",");
    			sb.append(cur.getString(cur.getColumnIndex("body"))+".\n");
    		} while (cur.moveToNext());
    		if (!cur.isClosed()) {
    			cur.close();
    			cur = null;
    		}
    		
    	} catch (SQLiteException ex) {
    		Log.d(TAG, ex.getMessage());
    	}
    }
    
    public native String stringFromJNI();
    static {
    	System.loadLibrary("my-jni");
    }
}
