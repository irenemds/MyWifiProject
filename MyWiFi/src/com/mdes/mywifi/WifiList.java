package com.mdes.mywifi;

import java.util.List;

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

public class WifiList extends Activity implements OnItemClickListener {

	private WifiManager wifiManager;
	
	public static List<ScanResult> resultWifiList;
	private ListView lista;
	private Intent intent;
	public HiloWifi hiloWifi;
	private Wifi wifiClick;
	public static WifiMap wifiMap;
	public static boolean isThread;
	private Button lineGraph;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("INFO", "Comienza WifiList");
		setContentView(R.layout.main_menu);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		wifiMap = new WifiMap();
		lineGraph = (Button) findViewById(R.id.lineGraph);
		lista = (ListView) findViewById(R.id.List1);
		lista.setOnItemClickListener(this);
		
		//Comprobar estado inicial de Wifi, si esta desactivado mostrar dialogo
		if (wifiManager.isWifiEnabled() == false)
		{  
			wifiAlertDialog(this);
		}else{
		// El wifi está activado, lanzar hilo
			createThread();
		}
		registerReceiver(new wifiChangeReceiver(this), new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		lineGraph.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//		    	DynamicGraphActivity dyn = new DynamicGraphActivity();
//		    	Intent intent = dyn.getIntent(WifiList.this);
				intent = new Intent(WifiList.this, DynamicGraphActivity.class);
		        startActivity(intent);
			}
			
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		stopThread();
		//TODO unregister Receiver
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		createThread();
		registerReceiver(new wifiChangeReceiver(this), new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
	}
	
	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) {
//	Al pulsar sobre un elemento de la lista se abre una nueva actividad en la que se muestra 
//	información sobre ella.	
		Log.i("INFO", "Se ha hecho click en: "
				+ resultWifiList.get(position).SSID);
		wifiClick = wifiMap.getWifi(resultWifiList.get(position).SSID);
		intent = new Intent(this, NetInfo.class);
//	Añadir como extra la información a mostrar.	
		intent.putExtra("SSID", wifiClick.getSSID());
		startActivity(intent);
	}

	public WifiManager getWifiManager() {
		return wifiManager;
	}
	
	public void wifiAlertDialog(Context c){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);

		alertDialogBuilder.setTitle("Wifi desactivado");

		// Opciones: encender Wifi o Salir de la aplicación.
		alertDialogBuilder
		.setMessage("Es necesario activar el Wifi para usar esta aplicación")
		.setCancelable(false)
		.setPositiveButton("Activar",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				wifiManager.setWifiEnabled(true);
			}
		})
		.setNegativeButton("Salir",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				WifiList.this.finish();
			}
		});
		
		// crear AlertDialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// mostrar
		alertDialog.show();
	}
	
	public void updateValues (List<ScanResult> results){
		resultWifiList = results;
	}
	
public void saveLevel(){
	wifiMap.putValue(resultWifiList);
}

public void saveZeros(){
	wifiMap.putValue(resultWifiList);
}

//Función para crear hilo comprobando que no exista uno previo
public void createThread(){
	if (!isThread){
		hiloWifi = new HiloWifi(this);
		hiloWifi.start();
		isThread = true;
		registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				CustomAdapter adapter = new CustomAdapter(getApplicationContext(), resultWifiList);
				lista.setAdapter(adapter);
				wifiMap.getRepresentableKey();
			}
			
		}, new IntentFilter("com.mdes.mywifi.timer"));
	}
}

public void stopThread(){
	if (isThread){
		hiloWifi.setBucleOff();
		isThread = false;
	}
}
public WifiList getWifiList(){
	return this;
}
}