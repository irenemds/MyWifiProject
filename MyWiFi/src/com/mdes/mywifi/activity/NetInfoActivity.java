package com.mdes.mywifi.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.achartengine.GraphicalView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mdes.mywifi.HelpDialog;
import com.mdes.mywifi.HiloWifi;
import com.mdes.mywifi.LogManager;
import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiLevelImage;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.chart.DialGraphActivity;
import com.mdes.mywifi.chart.LinkSpeedGraphActivity;

/**
 * En esta actividad se muestra información del punto de acceso seleccionado
 * por el usuario en la actividad WifiListActivity.
 *
 */
public class NetInfoActivity extends Activity {

	private ListView lista;
	
	private Wifi wifi;
	private TextView SSID;
	private ImageView IMAGE;
	private Button buttonp;
	private Button buttonv;


	ActionBar actionBar = null;
	private HelpDialog helpDialog;

	private BroadcastReceiver currentActivityReceiver;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	private GraphicalView mChartView;
	
	private String[] infoText;
	private String[] descText;
	
	private int index;
	private int offset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.net_info);

			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);
			infoText = new String[1];
			descText = new String[1];

			IMAGE = (ImageView) findViewById(R.id.container);
		    lista = (ListView) findViewById(R.id.List2);
		    SSID = (TextView) findViewById(R.id.SSID);
		    buttonp = (Button) findViewById(R.id.buttonp);
		    buttonv = (Button) findViewById(R.id.buttonv);
		
			Bundle extras = getIntent().getExtras();
			wifi = WifiMap.getWifi(extras.getString("SSID"));
			  
			SSID.setText(wifi.getSSID());
			IMAGE.setImageResource(WifiLevelImage.getWifiLevelImage(wifi.getLastLevel()));
			getInfo();
			
			//BroadCastReceiver para manejar evento de tiempo,mostrar valores actualizados.
			currentActivityReceiver = new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {
					if(wifi.isRepresentable()){			
						IMAGE.setImageResource(WifiLevelImage.getWifiLevelImage(wifi.getLastLevel()));
						try{
							getScrollPosition();
							getInfo();
							setScrollPosition();
							}catch (NullPointerException e){
								e.printStackTrace();
								LogManager lm = new LogManager(e);
							}
					}
					else{
						finish();
					}
				}

			};

		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}		
	}

	/*	---------- Menu ----------- */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.netinfo, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()) {
			case R.id.guardar:
				File root = Environment.getExternalStorageDirectory();
				File file = new File(root +"/"+ wifi.getSSID() + ".txt");
				Log.i("INFO","root "+ root);
				FileWriter filewriter;
				try {
					filewriter = new FileWriter(file);
					BufferedWriter out = new BufferedWriter(filewriter);
					out.write(WifiMap.getCSV(wifi.getSSID()));
					out.close();
				} catch (IOException e) {
					LogManager lm = new LogManager(e);
					e.printStackTrace();
				}

				setResult(Activity.RESULT_OK);
				return true;

			case R.id.ayuda:
				setResult(Activity.RESULT_CANCELED);
				String text = "Se muestra la información principal de la red seleccionada";
				helpDialog = new HelpDialog(this, text);
				return true;

			default:
				return super.onOptionsItemSelected(item);
			}
		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
			return super.onOptionsItemSelected(item);
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
	
	public void graphHandler (View view)
	{
		Intent intent = new Intent(NetInfoActivity.this, LinkSpeedGraphActivity.class);
		intent.putExtra("SSID", wifi.getSSID());
		startActivity(intent);
	}

	public void potenciometro (View view)
	{
		Intent intent = new Intent(NetInfoActivity.this, DialGraphActivity.class);
		intent.putExtra("SSID", wifi.getSSID());
		startActivity(intent);
	}
	
	public void getInfo(){
		String[] values;
		if (wifi.getSSID().equals(HiloWifi.currentAP.getSSID()))
		{
	        values = new String[] { "BSSID: "+ wifi.getBSSID(), 
	                "Frecuencia: " +Integer.toString(wifi.getFreq())+ " MHz",
	                "Canal: " + Integer.toString(wifi.getChannel()),
	                "Número de repetidores: " + Integer.toString(wifi.getAntennas()), 
	                "Propiedades: " + wifi.getCap(),
	                "MAC: " + HiloWifi.currentAP.getMAC(),
	                "IP: " + HiloWifi.currentAP.getIPString()            
	               };
	        buttonv.setEnabled(true);
		}
		else{
	        values = new String[] { "BSSID: "+ wifi.getBSSID(), 
	                "Frecuencia: " +Integer.toString(wifi.getFreq())+ " MHz",
	                "Canal: " + Integer.toString(wifi.getChannel()),
	                "Número de repetidores: " + Integer.toString(wifi.getAntennas()), 
	                "Propiedades: " + wifi.getCap(),
	                "MAC: - - - - - ",
	                "IP: - - - - - "
	               };	
	        buttonv.setEnabled(false);
		}
		lista.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, values));
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

}