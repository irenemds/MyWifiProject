package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			
			//Quitar título de la actividad y pantalla completa
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setContentView(R.layout.xy_chart);
			
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

	protected void onStart() {
		super.onStart();
		view = ChartFactory.getLineChartView(this, MultipleGraph.mDataset, MultipleGraph.mRenderer);		
		setContentView(view);
	}

	@Override
	protected void onPause() {
		super.onPause();
	unRegistrerReceivers();

	}
	@Override
	protected void onResume() {
		try{
			super.onResume();
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
