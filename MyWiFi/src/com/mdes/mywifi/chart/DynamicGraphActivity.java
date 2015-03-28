package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mdes.mywifi.HelpDialog;
import com.mdes.mywifi.WifiThread;
import com.mdes.mywifi.LogManager;
import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;

public class DynamicGraphActivity extends Activity {

	private static GraphicalView view;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private GraphicalView mChartView;
	private BroadcastReceiver graphReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();
	
	ActionBar actionBar = null;
	private HelpDialog helpDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			
			//Quitar título de la actividad y pantalla completa
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setContentView(R.layout.xy_chart);
			
			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);
			
			graphReceiver = new BroadcastReceiver(){
				@Override
				public void onReceive(Context context, Intent intent) {
					MultipleGraph.mRenderer.setYAxisMax((WifiMap.getMaxLevel()/10)*10+10);
					MultipleGraph.mRenderer.setYAxisMin(-100);
					if(Wifi.contador > 15){
						MultipleGraph.mRenderer.setXAxisMax(Wifi.contador);
						MultipleGraph.mRenderer.setXAxisMin(Wifi.contador-15);
					}			
					view.repaint();
				}
			};
			
			registerReceivers();
			
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}
	
	/*	---------- Menu ----------- */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.help_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()) {
			
			case R.id.ayuda:
				setResult(Activity.RESULT_CANCELED);
				String text = "Esta gráfica muestra la comparativa en tiempo real de la potencia recibida de"
						+ "cada punt de acceso detectado en los escaneos realizados.";
				helpDialog = new HelpDialog(this,"Ayuda", text);
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

	protected void onStart() {
		super.onStart();
		view = ChartFactory.getLineChartView(this, MultipleGraph.mDataset, MultipleGraph.mRenderer);		
		setContentView(view);
	}

	@Override
	protected void onPause() {
		super.onPause();
		WifiThread.isGraph = false;
		unRegistrerReceivers();
		MultipleGraph.deleteGraph();
	}
	@Override
	protected void onResume() {
		try{
			super.onResume();
			WifiThread.isGraph = true;
			MultipleGraph.createGraph();
			if (mChartView == null) {
				LinearLayout layout = new LinearLayout(this);
				layout.setOrientation(LinearLayout.VERTICAL);
				mChartView = ChartFactory.getLineChartView(this, MultipleGraph.mDataset, MultipleGraph.mRenderer);
				layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
			} else {
				mChartView.repaint();
			}
			registerReceivers();
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}

	private void registerReceivers(){
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		registerReceiver(graphReceiver, new IntentFilter("com.mdes.mywifi.timer"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wifinotfound"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wififound"));
	}

	private void unRegistrerReceivers(){
		unregisterReceiver(wifiReceiver);
		unregisterReceiver(wifiNotFoundReceiver);
		unregisterReceiver(graphReceiver);
	}
	
}
