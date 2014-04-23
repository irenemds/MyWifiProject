package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainMenu extends Activity implements OnItemClickListener  {

	private ToggleButton wifiOn;
	private WifiManager  wifiManager;
	public static List<ScanResult> resultWifiList;
    ArrayList<String> wifiList;
    ArrayList<String> signalList;
	private ListView lista;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		 lista = (ListView) findViewById(R.id.List1);
		 lista.setOnItemClickListener(this);
		
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
	     
//	     registerReceiver(new BroadcastReceiver()
//	        {
//	            @Override
//	            public void onReceive(Context c, Intent intent) 
//	            {	
//                	wifiList = new ArrayList<String>();
//                	signalList = new ArrayList<String>();
//                	resultWifiList = wifiManager.getScanResults();
//                	Log.i("Redes Wifi", "\n        Number Of Wifi connections :"+resultWifiList.size()+"\n\n");
//                	for(int i = 0; i < resultWifiList.size(); i++){
//                		Log.i("Redes Wifi", resultWifiList.get(i).toString() );
//                		wifiList.add(resultWifiList.get(i).SSID);
//                  		// TODO Mostrar potencias de señal recibidas en el ListView
//                		
//                		signalList.add(Integer.toString(resultWifiList.get(i).level));
//                		Log.i("Potencia de red", Integer.toString(resultWifiList.get(i).level));
//                	}
//                	CustomAdapter adapter = new CustomAdapter(c, resultWifiList);
//                    lista.setAdapter(adapter);
////                    lista.setOnItemClickListener(this);
//
//	            }
//	        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
/*	      registerReceiver (new BroadcastReceiver() {
	    	  
	    	  @Override
	    	  public void onReceive(Context c, Intent intent) 
	          {
	    		  wifiOn.setChecked(wifiManager.isWifiEnabled()); 
	    	  }
	      }, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)); */
	     
	     wifiManager.startScan();

	    }


	public BroadcastReceiver scanResultReceiver = new BroadcastReceiver() {
        
   	 @Override
        
   	 public void onReceive(Context context, Intent intent) {
   		 Bundle extras = intent.getExtras();
   		    ArrayList<ScanResult> scanResults = extras.getParcelableArrayList("ScanResults");
   		    CustomAdapter adapter = new CustomAdapter(context, scanResults);
   		    lista.setAdapter(adapter);
   	 }
    };
    

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int posicion, long id) {
		Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (posicion + 1) + ": " + resultWifiList.get(posicion), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
		
	}
	public void changeWifiList(	List<ScanResult> newResults){
		resultWifiList = newResults;
	}

	
}