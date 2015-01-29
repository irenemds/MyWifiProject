package com.mdes.mywifi.activity;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mdes.mywifi.CustomAdapter;
import com.mdes.mywifi.HiloWifi;
import com.mdes.mywifi.LogManager;
import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.chart.DynamicGraphActivity;
import com.mdes.mywifi.chart.FrequencyGraphActivity;
import com.mdes.mywifi.chart.PieGraphActivity;


/**
 * La actividad WifiListActivity muestra en un ListView todas las redes 
 * disponibles con su nivel de potencia recibida.
 * A partir de esta actividad se puede acceder a las demás
 * a través de los botones inferiores del Layout.
 */

public class WifiListActivity extends Activity implements OnItemClickListener {

	public static WifiManager wifiManager;
	
	private ListView lista;
	private Intent intent;

	public HiloWifi hiloWifi;
	private Wifi wifiClick;
	
	private int index;
	private int offset;
	/**
	 *  Lista de redes obtenidas en el último escaneo, se muestran en ListView
	 */
	public static List<ScanResult> resultWifiList;
	
	//  -- BroadCastReceivers --
	private BroadcastReceiver currentActivityReceiver;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver(); 
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try{

			super.onCreate(savedInstanceState);

			//Activar pantalla completa			
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			//Asigna el layout a visualizar
			setContentView(R.layout.main_menu);
			
			//Inicia servicio controlador de Wifi
			wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			
			//Se inicia el hilo, este será el que realice los escaneos periódicos
			hiloWifi = new HiloWifi(this);
			hiloWifi.createThread();
			
			//Inicializa ListView y asigna onItemClickListener
			lista = (ListView) findViewById(R.id.List1);
			lista.setOnItemClickListener(this);
			
			//Inicializa el receiver propio de la actividad actual
			/**
			 * Este BroadcastReceiver se encarga de actualizar las redes del ListView,
			 * manteniendo la posición de la barra de scroll y
			 * actualiza el array de redes "representables".
			 * 
			 */
			currentActivityReceiver = new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {
					try{
					getScrollPosition();
					CustomAdapter adapter = new CustomAdapter(getApplicationContext(), resultWifiList);
					lista.setAdapter(adapter);
					WifiMap.getRepresentableKey();
					setScrollPosition();
					}catch (NullPointerException e){
						e.printStackTrace();
						LogManager lm = new LogManager(e);
					}
				}

			};

		}catch(Exception e){
			LogManager lm = new LogManager(e);
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
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
		wifiClick = WifiMap.wifiMap.get(resultWifiList.get(position).BSSID);

		intent = new Intent(this, NetInfoActivity.class);
		intent.putExtra("BSSID", resultWifiList.get(position).BSSID);
		Log.i("BSSID","METO EXTRA BSSID"+resultWifiList.get(position).BSSID);
		startActivity(intent);
	}

	public WifiManager getWifiManager() {
		return wifiManager;
	}

	public void updateValues (List<ScanResult> results){
		resultWifiList = results;
		saveLevel();
	}

	/**
	 * Llama a la función que guarda los nuevos valores de os objetos wifi
	 * si está activo el wifi y hay resultados del escaneo,
	 * si esto no se cumple llama a la función que actualiza los valores
	 * de los objetos Wifi con valores "cero"
	 */	
	public void saveLevel(){
		if (wifiManager.isWifiEnabled()==true && resultWifiList != null){
			WifiMap.putValue(resultWifiList);
		}else{
			WifiMap.reset();
		}

	}

	// 	-- Button Handlers --
	
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
	
	public void lineGraphHandler (View view)
	{
		intent = new Intent(WifiListActivity.this, DynamicGraphActivity.class);
		startActivity(intent);
	}
	//Guarda en las variables index y offset los valores necesarios
	//para reestablecer la posición del scroll.
	private void getScrollPosition(){
		index = lista.getFirstVisiblePosition();	//primera posición visible (puede estar cortada)
		View v = lista.getChildAt(0);
		offset = (v == null) ? 0 : v.getTop();		//offset respecto a dicha posición
	}

	//Establece la posición del scroll con los valores conseguidos
	private void setScrollPosition(){
		lista.setSelectionFromTop(index, offset);	
	}
	
	//Registra todos los receivers que emplea la actividad
	private void registerReceivers(){
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		registerReceiver(currentActivityReceiver, new IntentFilter("com.mdes.mywifi.timer"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wifinotfound"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wififound"));
	}

	//Elimina el registro de todos los receivers que emplea la actividad
	private void unregisterReceivers(){
		unregisterReceiver(wifiReceiver);
		unregisterReceiver(wifiNotFoundReceiver);
		unregisterReceiver(currentActivityReceiver);
	}

}