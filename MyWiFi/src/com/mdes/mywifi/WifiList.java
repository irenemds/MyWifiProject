package com.mdes.mywifi;

import java.util.ArrayList;
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
import android.widget.Toast;

public class WifiList extends Activity implements OnItemClickListener {

	private WifiManager wifiManager;
	public static List<ScanResult> resultWifiList;
	ArrayList<String> wifiList;
	ArrayList<String> signalList;
	private ListView lista;
	private Intent intent;
	public CustomAdapter adapter;
	public HiloWifi hiloWifi;
//	private WifiState wifiState;
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("INFO", "Comienza ejecución");
		setContentView(R.layout.main_menu);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		lista = (ListView) findViewById(R.id.List1);
//		lista.setOnItemClickListener(this);
		
//		wifiState = new WifiState(this);
		
		//Comprobar estado inicial de Wifi, si esta desactivado mostrar dialogo
		if (wifiManager.isWifiEnabled() == false)
		{  
			wifiAlertDialog(this);

		} 

		// EL wifi está activado, lanzar hilo
//		hiloWifi = new HiloWifi(this);
//		hiloWifi.start();


		registerReceiver(new BroadcastReceiver() {

			// Receiver para modificar estado ToggleButton
			
			@Override
			public void onReceive(Context c, Intent intent) {
				
				int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				
					switch(extraWifiState){
					case WifiManager.WIFI_STATE_DISABLED:
					
						wifiAlertDialog(c);
						Log.i("INFO", "Broadcast -  Wifi off");
						break;
					case WifiManager.WIFI_STATE_UNKNOWN:
						Log.i("INFO", "Broadcast -  Wifi desconocido");
						break;
					}
			}
		}, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

	}
//
//	public HiloWifi getHiloWifi() {
//		return hiloWifi;
//	}

	
//	public void setHiloWifi(HiloWifi hiloWifi) {
//		this.hiloWifi = hiloWifi;
//		hiloWifi.start();
//	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// timer.stop();
	}

	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) {
//		Log.i("INFO", "Se ha hecho click en: "
//				+ resultWifiList.get(position).SSID);
//		intent = new Intent(this, InfoRed.class);
//		Log.i("INFO", "Creado intent");
//		intent.putExtra("Red", resultWifiList.get(position).SSID);
//		Log.i("INFO", "Creado el Extra");
//		startActivity(intent);

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
		.setNegativeButton("Activar",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				wifiManager.setWifiEnabled(true);
			}
		})
		.setPositiveButton("Salir",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				WifiList.this.finish();
			}
		});
		
		// crear AlertDialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// mostrar
		alertDialog.show();
	}
	
}