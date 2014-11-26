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

import com.mdes.mywifi.R;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;

public class FrequencyGraphActivity extends Activity {

	public static GraphicalView view;
	public static XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	public static XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private GraphicalView mChartView;
	private Context c;
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//		try{
		registerReceivers();
		rendererSetUp();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		view = ChartFactory.getLineChartView(this, FrequencyGraphActivity.mDataset, FrequencyGraphActivity.mRenderer);		
		setContentView(view);
		currentActivityReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				rendererSetUp();
				mRenderer.setYAxisMax((WifiMap.getMaxLevel()/10)*10+10);
				mRenderer.setYAxisMin(-120);
				view = ChartFactory.getLineChartView(c, FrequencyGraphActivity.mDataset, FrequencyGraphActivity.mRenderer);		
				setContentView(view);
				view.repaint();
			}

		};
		//		}catch (Exception e){
			//			e.printStackTrace();
		//			LogManager lm = new LogManager(e);
		//		}
	}

	protected void onStart() {
		super.onStart();
		c = this;
		rendererSetUp();
		view = ChartFactory.getLineChartView(this, mDataset, mRenderer);		
		setContentView(view);
		registerReceivers();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}

	private void rendererSetUp(){
		mRenderer.setClickEnabled(false);
		mRenderer.setShowGrid(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 50, 30, 15, 20 });
		mRenderer.setXAxisMax(12);
		mRenderer.setXAxisMin(0);
		mRenderer.setYLabels(11);
		mRenderer.setXLabels(11);
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