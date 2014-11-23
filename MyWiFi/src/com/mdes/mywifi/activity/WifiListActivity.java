package com.mdes.mywifi.activity;

import java.util.List;

import org.achartengine.ChartFactory;

import com.mdes.mywifi.CustomAdapter;
import com.mdes.mywifi.HiloWifi;
import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.R.id;
import com.mdes.mywifi.R.layout;
import com.mdes.mywifi.chart.DynamicGraphActivity;
import com.mdes.mywifi.chart.FrequencyGraphActivity;
import com.mdes.mywifi.chart.PieGraphActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class WifiListActivity extends Activity implements OnItemClickListener {

	public static WifiManager wifiManager;

	public static List<ScanResult> resultWifiList;
	private ListView lista;
	private Intent intent;
	public HiloWifi hiloWifi;
	private Wifi wifiClick;
	public static WifiMap wifiMap;
	public static boolean isThread;
	private Button lineGraph;
	private Button PieGraph;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver(); 

	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("INFO", "Comienza WifiList");
		setContentView(R.layout.main_menu);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		wifiMap = new WifiMap();
		lineGraph = (Button) findViewById(R.id.lineGraph);
		PieGraph = (Button) findViewById(R.id.PieGraph);
		lista = (ListView) findViewById(R.id.List1);
		lista.setOnItemClickListener(this);
		currentActivityReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				CustomAdapter adapter = new CustomAdapter(getApplicationContext(), resultWifiList);
				lista.setAdapter(adapter);
				wifiMap.getRepresentable();
			}
		};

		createThread();

		lineGraph.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				intent = new Intent(WifiListActivity.this, DynamicGraphActivity.class);
				startActivity(intent);
			}

		});	

	}

	@Override
	protected void onPause() {
		super.onPause();
		//TODO unregister Receiver
		unregisterReceivers();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceivers();
	}

	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		//	Al pulsar sobre un elemento de la lista se abre una nueva actividad en la que se muestra 
		//	información sobre ella.	
		Log.i("INFO", "Se ha hecho click en: "
				+ resultWifiList.get(position).SSID);
		wifiClick = wifiMap.getWifi(resultWifiList.get(position).SSID);
		if(resultWifiList.get(position).SSID.equals(HiloWifi.currentAP.getSSID())){
			intent = new Intent(this, CurrentAPActivity.class);
		}
		else
		{
			intent = new Intent(this, NetInfoActivity.class);			
		}
		//	Añadir como extra la información a mostrar.	
		intent.putExtra("SSID", wifiClick.getSSID());
		startActivity(intent);
	}

	public WifiManager getWifiManager() {
		return wifiManager;
	}



	public void updateValues (List<ScanResult> results){
		resultWifiList = results;
	}

	public void saveLevel(){
		if (wifiManager.isWifiEnabled()==true && resultWifiList != null){
			wifiMap.putValue(resultWifiList);
		}else{
			wifiMap.putZeros();
		}

	}


	//Función para crear hilo comprobando que no exista uno previo
	public void createThread(){
		if (!isThread){
			hiloWifi = new HiloWifi(this);
			hiloWifi.start();
			isThread = true;
		}
	}

	public void stopThread(){
		if (isThread){
			hiloWifi.setBucleOff();
			isThread = false;
		}
	}
	public WifiListActivity getWifiList(){
		return this;
	}

	public void pieGraphHandler (View view)
	{
		intent = new Intent(WifiListActivity.this, PieGraphActivity.class);
		startActivity(intent);
	}

	public void frecGraphHandler (View view)
	{
		intent = new Intent(WifiListActivity.this, FrequencyGraphActivity.class);
		startActivity(intent);
	}
	
	private void registerReceivers(){
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		registerReceiver(currentActivityReceiver, new IntentFilter("com.mdes.mywifi.timer"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wifinotfound"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wififound"));
	}

	private void unregisterReceivers(){
		unregisterReceiver(wifiReceiver);
		unregisterReceiver(wifiNotFoundReceiver);
		unregisterReceiver(currentActivityReceiver);
	}

}