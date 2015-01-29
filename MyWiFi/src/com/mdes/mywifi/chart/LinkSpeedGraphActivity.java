package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.mdes.mywifi.HiloWifi;
import com.mdes.mywifi.LogManager;
import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;

public class LinkSpeedGraphActivity extends Activity{

	public static GraphicalView view;
	public static XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	public static XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private GraphicalView mChartView;
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{

			registerReceivers();

			mRenderer.setClickEnabled(false);
			mRenderer.setShowGrid(true);
			mRenderer.setApplyBackgroundColor(true);
			mRenderer.setBackgroundColor(Color.BLACK);
			mRenderer.setAxisTitleTextSize(16);
			mRenderer.setChartTitleTextSize(27);
			mRenderer.setLabelsTextSize(20);
			mRenderer.setAxisTitleTextSize(20);
			mRenderer.setChartTitle("Velocidad de enlace");
			mRenderer.setLegendTextSize(20);
			mRenderer.setYTitle("Velocidad [Mbps]");
			mRenderer.setMargins(new int[] { 50, 40, 10, 30 });
			mRenderer.setPointSize(5);

			super.onCreate(savedInstanceState);
			
			//Quitar título de la actividad y pantalla completa
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setContentView(R.layout.xy_chart);

			currentActivityReceiver = new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {
					Bundle extras = getIntent().getExtras();
					if(HiloWifi.currentAP.getSSID().equals(extras.getString("SSID"))){
						LinkSpeedGraphActivity.mRenderer.setYAxisMax(100);
						LinkSpeedGraphActivity.mRenderer.setYAxisMin(0);
						if(Wifi.contador > 15){
							LinkSpeedGraphActivity.mRenderer.setXAxisMax(Wifi.contador);
							LinkSpeedGraphActivity.mRenderer.setXAxisMin(Wifi.contador-15);
						}			
						view.repaint();
					}

					else{
						finish();
					}}
			};
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}

	protected void onStart() {
		super.onStart();
		view = ChartFactory.getLineChartView(this, LinkSpeedGraphActivity.mDataset, LinkSpeedGraphActivity.mRenderer);		
		setContentView(view);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}
	@Override
	protected void onResume() {
		registerReceivers();
		try{
			super.onResume();
			if (mChartView == null) {
				LinearLayout layout = new LinearLayout(this);
				layout.setOrientation(LinearLayout.VERTICAL);
				mChartView = ChartFactory.getLineChartView(this, LinkSpeedGraphActivity.mDataset, LinkSpeedGraphActivity.mRenderer);
				LinkSpeedGraphActivity.mRenderer.setClickEnabled(true);
				LinkSpeedGraphActivity.mRenderer.setSelectableBuffer(10);
				layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
			} else {
				mChartView.repaint();
			}
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
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