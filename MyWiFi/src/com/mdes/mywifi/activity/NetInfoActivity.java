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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdes.mywifi.HelpDialog;
import com.mdes.mywifi.LogManager;
import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiLevelImage;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.chart.DialGraphActivity;

/**
 * En esta actividad se muestra información del punto de acceso seleccionado
 * por el usuario en la actividad WifiListActivity.
 *
 */
public class NetInfoActivity extends Activity {

	private Wifi wifi;
	private TextView SSID;
	private TextView CAP;
	private TextView FREQ;
	private TextView BSSID;
	private TextView CHAN;
	private TextView LEVEL;
	private ImageView IMAGE;


	ActionBar actionBar = null;
	private HelpDialog helpDialog;

	private BroadcastReceiver currentActivityReceiver;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	private GraphicalView mChartView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.net_info);

			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);

			SSID = (TextView) findViewById(R.id.SSID);
			CAP = (TextView) findViewById(R.id.CAP);
			FREQ = (TextView) findViewById(R.id.FREQ);
			BSSID = (TextView) findViewById(R.id.BSSID);
			CHAN = (TextView) findViewById(R.id.CHAN);
			LEVEL = (TextView) findViewById(R.id.LEVEL);
			IMAGE = (ImageView) findViewById(R.id.container);

			//Recibe SSID como extra, a partir de él consigue toda la información.
			Bundle extras = getIntent().getExtras();
			wifi = WifiMap.getWifi(extras.getString("SSID"));

			SSID.setText(wifi.getSSID());
			BSSID.setText(wifi.getBSSID());
			CAP.setText(wifi.getCap());
			FREQ.setText(Integer.toString(wifi.getFreq())+ " MHz");
			CHAN.setText(Integer.toString(wifi.getChannel()));
			IMAGE.setImageResource(WifiLevelImage.getWifiLevelImage(wifi.getLastLevel()));
			//BroadCastReceiver para manejar evento de tiempo,mostrar valores actualizados.
			currentActivityReceiver = new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {
					if(wifi.isRepresentable()){
						LEVEL.setText(Integer.toString(wifi.getLastLevel()));
						IMAGE.setImageResource(WifiLevelImage.getWifiLevelImage(wifi.getLastLevel()));
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


			case R.id.medidor:
				setResult(Activity.RESULT_OK);
				Intent intent = new Intent(NetInfoActivity.this, DialGraphActivity.class);
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
		//		if (mChartView == null) {
		//		    LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		//		    mChartView = levelDialGraph.execute(this);
		//		    layout.addView(mChartView, new LayoutParams
		//		(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		//		    ...
		//		  } else {
		//		    mChartView.repaint();
		//		  }
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