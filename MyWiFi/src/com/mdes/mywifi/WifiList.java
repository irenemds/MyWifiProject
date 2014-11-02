package com.mdes.mywifi;

import java.util.HashMap;
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
	private Wifi wifiClick;
//	private LevelList levelList;
	public HashMap<String,Wifi> wifiMap;
	public static boolean isThread;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("INFO", "Comienza ejecución");
		setContentView(R.layout.main_menu);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		lista = (ListView) findViewById(R.id.List1);
		lista.setOnItemClickListener(this);
		
		wifiMap = new HashMap<String, Wifi>();
		//Comprobar estado inicial de Wifi, si esta desactivado mostrar dialogo
		if (wifiManager.isWifiEnabled() == false)
		{  
			wifiAlertDialog(this);
		}else{
		// El wifi está activado, lanzar hilo
			createThread();
		}
		registerReceiver(new wifiChangeReceiver(this), new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("DESCONEXION","onPause");
		stopThread();
		//TODO unregister Receiver
	}
	
	@Override
	protected void onResume() {
		Log.i("RECONEXION","OnResume");
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
		wifiClick = wifiMap.get(resultWifiList.get(position).SSID);
		intent = new Intent(this, NetInfo.class);
//	Añadir como extra la información a mostrar.	
		intent.putExtra("SSID", wifiClick.getSSID());
		intent.putExtra("BSSID", wifiClick.getBSSID());
		intent.putExtra("CAP", wifiClick.getCap());
		intent.putExtra("FREQ", wifiClick.getFreq());
		intent.putExtra("CHAN", wifiClick.getChannel());
//  Al cambiar de actividad parar el hilo (Cambiar a mensaje broadcast para no tener que pararlo)
		stopThread();
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
	
	public void updateValues (List<ScanResult> results){
		resultWifiList = results;
	}
	
public void saveLevel(ScanResult scanResult){
	//Comprobar si la red ya existe en el HashMap
	//Si no existe
	if (!wifiMap.containsKey(scanResult.SSID)){
		Log.i("INFO", scanResult.SSID + " no existía en el HashMap");
		//Crear nuevo objeto de la clase Wifi con ella
		Wifi wifi = new Wifi(scanResult);
		wifiMap.put(scanResult.SSID, wifi);
		Log.i("INFO", scanResult.SSID + " guardado en el HashMap");
	}
	else
	{	
		Log.i("INFO", scanResult.SSID + " ya existía en el HashMap");
		wifiMap.get(scanResult.SSID).saveLevel(scanResult.level);
		
	}
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
				getLista().setAdapter(adapter);
				
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