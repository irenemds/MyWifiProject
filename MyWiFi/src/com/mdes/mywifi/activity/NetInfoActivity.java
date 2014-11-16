package com.mdes.mywifi.activity;

import java.util.ArrayList;
import java.util.List;

import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.R.id;
import com.mdes.mywifi.R.layout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class NetInfoActivity extends Activity {
	
	private Wifi wifi;
	private TextView SSID;
	private TextView CAP;
	private TextView FREQ;
	private TextView BSSID;
	private TextView CHAN;
	private TextView LEVEL;
	private ImageView level;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_info);
		
		SSID = (TextView) findViewById(R.id.SSID);
		CAP = (TextView) findViewById(R.id.CAP);
		FREQ = (TextView) findViewById(R.id.FREQ);
		BSSID = (TextView) findViewById(R.id.BSSID);
		CHAN = (TextView) findViewById(R.id.CHAN);
		LEVEL = (TextView) findViewById(R.id.LEVEL);
		
		Bundle extras = getIntent().getExtras();
		wifi = WifiListActivity.wifiMap.getWifi(extras.getString("SSID"));
		
		SSID.setText(wifi.getSSID());
		BSSID.setText(wifi.getBSSID());
		CAP.setText(wifi.getCap());
		FREQ.setText(Integer.toString(wifi.getFreq())+ " MHz");
		CHAN.setText(Integer.toString(wifi.getChannel()));
		registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				LEVEL.setText(Integer.toString(wifi.getLastLevel()));		
			}
			
		}, new IntentFilter("com.mdes.mywifi.timer"));
	}

}