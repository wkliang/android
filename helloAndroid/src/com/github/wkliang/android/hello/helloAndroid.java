package com.github.wkliang.android.hello;

import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;

import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import android.content.Context;
import android.content.Intent;

import android.telephony.TelephonyManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class helloAndroid extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
		tv.setTextSize(20);
		tv.setText("你好，安桌椅！" + stringFromJNI() + "\n");
	    }
	});

	btn = (Button)findViewById(R.id.buttonPhone);
	btn.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		TextView tv = (TextView)findViewById(R.id.helloTextView);
		tv.setTextSize(14);
		tv.setText(getPhoneInformation() + "\n");
	    }
	});

	btn = (Button)findViewById(R.id.buttonWifi);
	btn.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		TextView tv = (TextView)findViewById(R.id.helloTextView);
		tv.setTextSize(16);
		tv.setText(getWifiInformation() + "\n");
	    }
	});

	btn = (Button)findViewById(R.id.buttonIntent);
	btn.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		// Uri uri = Uri.parse("http://www.google.com/");
		// Intent i = new Intent(Intent.ACTION_VIEW,uri);
		// startActivity(i);
		sendEmail();
	    }
	});

    }

// ref: http://stackoverflow.com/questions/2197741/how-to-send-email-from-my-android-application
// following one is using low level protocol to email
// http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a
    private void sendEmail() {
	Intent i = new Intent(Intent.ACTION_SEND);
	i.setType("message/rfc822");
	i.putExtra(Intent.EXTRA_EMAIL, new String[] {"admin@google.com"});
	i.putExtra(Intent.EXTRA_SUBJECT, "subject title");
	i.putExtra(Intent.EXTRA_TEXT, "mail body");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }
    public native String stringFromJNI();
    static {
	System.loadLibrary("my-jni");
    }
}
