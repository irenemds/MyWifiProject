package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

public class MainMenu extends Activity {

	private ToggleButton wifiOn;
	private WifiManager  wifiManager;
	List<ScanResult> resultWifiList;
    ArrayList<String> wifiList;
    ArrayList<String> signalList;
	private ListView lista;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		 lista = (ListView) findViewById(R.id.List1);
		
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
	     //BroadcastReceiver para mostrar redes 
	     registerReceiver(new BroadcastReceiver()
	        {
	            @Override
	            public void onReceive(Context c, Intent intent) 
	            {
	            	resultWifiList = wifiManager.getScanResults();
                	wifiList = new ArrayList<String>();
                	signalList = new ArrayList<String>();
                	resultWifiList = wifiManager.getScanResults();
                	Log.i("Redes Wifi", "\n        Number Of Wifi connections :"+resultWifiList.size()+"\n\n");
                	for(int i = 0; i < resultWifiList.size(); i++){
                		String[] content = null;
                		String normal = resultWifiList.get(i).toString();
                   		content = normal.split(" ");
                		Log.i("Redes Wifi", resultWifiList.get(i).toString() );
                		Log.i("Potencia de red", content[7]);
                		wifiList.add(content[1].replace(",", " "));
                		
                		// TODO Mostrar potencias de señal recibidas en el ListView
                		
                		signalList.add(content[7].replace(","," "));
                	}
                	final StableArrayAdapter adapter = new StableArrayAdapter(c,
	                       android.R.layout.simple_list_item_1, wifiList);

                	lista.setAdapter(adapter);
	            }
	        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
/*	      registerReceiver (new BroadcastReceiver() {
	    	  
	    	  @Override
	    	  public void onReceive(Context c, Intent intent) 
	          {
	    		  wifiOn.setChecked(wifiManager.isWifiEnabled()); 
	    	  }
	      }, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)); */
	     
	     wifiManager.startScan();

	    }
	
}