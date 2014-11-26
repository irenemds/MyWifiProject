package com.mdes.mywifi.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdes.mywifi.CurrentAP;
import com.mdes.mywifi.HelpDialog;
import com.mdes.mywifi.HiloWifi;
import com.mdes.mywifi.LogManager;
import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.chart.DialGraphActivity;
import com.mdes.mywifi.chart.LinkSpeedGraphActivity;

public class CurrentAPActivity extends Activity {

	private Wifi wifi;
	private TextView SSID;
	private TextView CAP;
	private TextView FREQ;
	private TextView BSSID;
	private TextView CHAN;
	private TextView LEVEL;
	private ImageView level;
	ActionBar actionBar = null;
	private HelpDialog helpDialog;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private TextView MAC;
	private TextView velocidad;
	private TextView IP;

	private CurrentAP currentAP;
	private BroadcastReceiver currentApReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			registerReceivers();
			super.onCreate(savedInstanceState);
			setContentView(R.layout.currentapinfo);
			registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);

			SSID = (TextView) findViewById(R.id.SSID);
			CAP = (TextView) findViewById(R.id.CAP);
			FREQ = (TextView) findViewById(R.id.FREQ);
			BSSID = (TextView) findViewById(R.id.BSSID);
			CHAN = (TextView) findViewById(R.id.CHAN);
			LEVEL = (TextView) findViewById(R.id.LEVEL);
			LEVEL = (TextView) findViewById(R.id.LEVEL);
			MAC = (TextView) findViewById(R.id.MAC);
			velocidad = (TextView) findViewById(R.id.velocidad);
			IP = (TextView) findViewById(R.id.IP);

			currentAP = HiloWifi.currentAP;
			Bundle extras = getIntent().getExtras();
			wifi = WifiMap.getWifi(extras.getString("SSID"));

			SSID.setText(wifi.getSSID());
			BSSID.setText(wifi.getBSSID());
			CAP.setText(wifi.getCap());
			FREQ.setText(Integer.toString(wifi.getFreq())+ " MHz");
			CHAN.setText(Integer.toString(wifi.getChannel()));
			CHAN.setText(Integer.toString(wifi.getChannel()));
			MAC.setText(currentAP.getMAC());
			IP.setText(currentAP.getIPString());


			currentApReceiver = new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {
					try{
						
						if(HiloWifi.currentAP != null && wifi != null){
							if(HiloWifi.currentAP.getBSSID().equals(wifi.getBSSID())){
								LEVEL.setText(Integer.toString(WifiMap.getWifi(wifi.getSSID()).getLastLevel()));
								velocidad.setText(Integer.toString(HiloWifi.currentAP.getLastLinkSpeed())+" Mbps");
							}else{
								Intent mainIntent = new Intent().setClass(
										CurrentAPActivity.this, WifiListActivity.class);
								//						unregisterReceiver(wifiReceiver);
								startActivity(mainIntent);
								finish();
							}
						}
					}catch (Exception e) {
						Log.e("Conexion AP", "Se ha perdido conexión actual.");
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
				helpDialog = new HelpDialog(this);
				return true;

			case R.id.medidor:
				setResult(Activity.RESULT_OK);
				Intent intent = new Intent(CurrentAPActivity.this, DialGraphActivity.class);
				intent.putExtra("SSID", wifi.getSSID());
				startActivity(intent);
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

	public void graphHandler (View view)
	{
		Intent intent = new Intent(CurrentAPActivity.this, LinkSpeedGraphActivity.class);
		intent.putExtra("SSID", wifi.getSSID());
		startActivity(intent);
	}

	private void registerReceivers(){
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		registerReceiver(currentApReceiver, new IntentFilter("com.mdes.mywifi.timer"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wifinotfound"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wififound"));
	}

	private void unregisterReceivers(){
		unregisterReceiver(wifiReceiver);
		unregisterReceiver(wifiNotFoundReceiver);
		unregisterReceiver(currentApReceiver);
	}
}