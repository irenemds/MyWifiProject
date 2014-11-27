package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.R.layout;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.mdes.mywifi.LogManager;
import com.mdes.mywifi.R;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;

public class PieGraphActivity extends Activity {
	private DefaultRenderer renderer = new DefaultRenderer();
	private CategorySeries series = new CategorySeries("Pie Graph");
	private static GraphicalView view;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver(); 
	private int[] values;
	int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.GRAY, Color.WHITE,
			Color.LTGRAY, Color.GREEN, Color.BLUE, Color.MAGENTA};
	private LinearLayout layout;
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	protected void onCreate(Bundle savedInstanceState) {
		try{ 
			registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
			super.onCreate(savedInstanceState);
			setContentView(R.layout.xy_chart);
			layout = (LinearLayout) findViewById(R.id.chart);
			getValues();
			renderer.setChartTitle("Redes por canal");
			renderer.setChartTitleTextSize(27);
			renderer.setStartAngle(180);
			renderer.setDisplayValues(true);
			renderer.setBackgroundColor(Color.BLACK);
			renderer.setLabelsTextSize(20);
			renderer.setApplyBackgroundColor(true);
			renderer.setMargins(new int[] { 50, 40, 10, 30 });
			renderer.setLegendTextSize(20);
			
			currentActivityReceiver= new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {	
					getValues();
					onResume();
					view.repaint();
				}

			};
		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}

	protected void onStart() {
		try{
			super.onStart();
			view = ChartFactory.getPieChartView(this, series, renderer);
			setContentView(view);
		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}

	private void getValues(){
//		try{
			renderer = new DefaultRenderer();
			series = new CategorySeries("Pie Graph");
			values = WifiMap.getChannelAP();
			int k = 0;
			for (int i = 0; i<values.length; i++) {
				Log.i("INFO","LONG "+ i);
				if(values[i] > 0){
					
					Log.i("INFO","VALOR "+ Integer.toString(i+1) + ": "+values[i]);
					series.add("Canal " + i+1, values[i]);
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					r.setColor(colors[i]);
					renderer.addSeriesRenderer(r);
			}}
//			int i = 0;
//			while (i < k) {
//				SimpleSeriesRenderer r = new SimpleSeriesRenderer();
//				r.setColor(colors[i]);
//				renderer.addSeriesRenderer(r);
//				i++;
//			}
			view = ChartFactory.getPieChartView(this, series, renderer);
			layout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

//		}catch(Exception e){
//			e.printStackTrace();
//			LogManager lm = new LogManager(e);
//		}
	}


	protected void onResume() {
		registerReceivers();
		try{
			super.onResume();
			registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
			if (view == null) {
				layout = (LinearLayout) findViewById(R.id.chart);
				view = ChartFactory.getPieChartView(this, series, renderer);
				renderer.setClickEnabled(true);
				layout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
			} else {
				view.repaint();
			}

		}catch(Exception e){
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
