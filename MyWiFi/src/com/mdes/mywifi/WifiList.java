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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class WifiList extends Activity implements OnItemClickListener {

	private WifiManager wifiManager;
	public static List<ScanResult> resultWifiList;
	private ListView lista;
	private Intent intent;
	public HiloWifi hiloWifi;
	private LevelList levelList;
	public int contador = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("INFO", "Comienza ejecución");
		setContentView(R.layout.main_menu);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		lista = (ListView) findViewById(R.id.List1);
		lista.setOnItemClickListener(this);
		
		levelList = new LevelList();
		//Comprobar estado inicial de Wifi, si esta desactivado mostrar dialogo
		if (wifiManager.isWifiEnabled() == false)
		{  
			wifiAlertDialog(this);
		}else{
		// El wifi está activado, lanzar hilo
			hiloWifi = new HiloWifi(this, levelList, contador);
			hiloWifi.start();
			contador++;
		}
			
		registerReceiver(new BroadcastReceiver() {

			// Receiver para modificar estado ToggleButton
			
			@Override
			public void onReceive(Context c, Intent intent) {
				
				int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);		
					switch(extraWifiState){
					case WifiManager.WIFI_STATE_DISABLED:
						Log.i("INFO", "Broadcast -  Wifi off");
						hiloWifi.setBucleOff();	
			  			wifiAlertDialog(c);
						break;
						
					case WifiManager.WIFI_STATE_ENABLED:
						Log.i("INFO", "Broadcast -  Wifi on, lanza hilo");							
						hiloWifi = new HiloWifi(WifiList.this, levelList, contador);
						contador++;
//						hiloWifi.start();
						break;	
						
					case WifiManager.WIFI_STATE_UNKNOWN:
						Log.i("INFO", "Broadcast -  Wifi desconocido");
						break;
					}
			}
		}, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

	}
	@Override
	protected void onPause() {
		Log.i("DESCONEXION","onPause");
		super.onPause();
		hiloWifi.setBucleOff();
		//TODO unregister Receiver
	}
	
	protected void onDestroy() {
		Log.i("DESCONEXION","onDestroy");
		super.onDestroy();
		hiloWifi.setBucleOff();
		//TODO unregister Receiver
	}
	
	protected void onStop() {
		Log.i("DESCONEXION","onStop");
		super.onStop();
		hiloWifi.setBucleOff();
		//TODO unregister Receiver
	}

	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		Log.i("INFO", "Se ha hecho click en: "
				+ resultWifiList.get(position).SSID);
		intent = new Intent(this, NetInfo.class);
		Log.i("INFO", "Creado intent");
		intent.putExtra("Red", resultWifiList.get(position).SSID);
		hiloWifi.setBucleOff();
		Log.i("INFO", "Creado el Extra");
		startActivity(intent);

	}

	public WifiManager getWifiManager() {
		return wifiManager;
	}

	public ListView getLista() {
		return lista;
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
	
	public void updateValues (List<ScanResult> results, LevelList levels){
		resultWifiList = results;
		levelList = levels;
	}
}