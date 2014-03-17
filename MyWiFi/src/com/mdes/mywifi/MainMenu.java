package com.mdes.mywifi;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainMenu extends Activity {

	private ToggleButton wifiOn;
	private WifiManager  wifiManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		//ToggleButton
		wifiOn = (ToggleButton)findViewById(R.id.wifiOn);
		wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		wifiOn.setChecked(wifiManager.isWifiEnabled()); 
		
		//Listener
	     wifiOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

	            @Override
	            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	                wifiManager.setWifiEnabled(isChecked);
	            }

	        });
	}


}
	

