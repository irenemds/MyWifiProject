/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.DialRenderer.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.mdes.mywifi.R;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;

/**
 * Budget demo pie chart.
 */
public class DialGraphActivity extends Activity  {

	Wifi wifi;
	private GraphicalView view;
	private CategorySeries category;
	private DialRenderer renderer;
	private SimpleSeriesRenderer r;

	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);

		registerReceivers();

		Bundle extras = getIntent().getExtras();
		wifi = WifiMap.getWifi(extras.getString("SSID"));

		setUp();

		currentActivityReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				if (wifi.isRepresentable()){
					setUp();
					view = ChartFactory.getDialChartView(DialGraphActivity.this, category, renderer);		
					setContentView(view);
					view.repaint();
				}else{
					finish();
				}
			}

		};
		registerReceivers();

		view = ChartFactory.getDialChartView(this, category, renderer);		
		setContentView(view);
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

	private void setUp(){

		category = new CategorySeries(wifi.getSSID());
		category.add("Actual", wifi.getLastLevel());
		category.add("Mínimo", wifi.getMinLevel());
		category.add("Máximo", wifi.getMaxLevel());
		renderer = new DialRenderer();
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(20);
		renderer.setMargins(new int[] {50, 40, 15, 30});
		r = new SimpleSeriesRenderer();
		r.setColor(Color.BLUE);
		renderer.addSeriesRenderer(r);
		r = new SimpleSeriesRenderer();
		r.setColor(Color.MAGENTA);
		renderer.addSeriesRenderer(r);
		r = new SimpleSeriesRenderer();
		r.setColor(Color.GREEN);
		renderer.addSeriesRenderer(r);
		renderer.setLabelsTextSize(20);
		renderer.setLabelsColor(Color.WHITE);
		renderer.setShowLabels(true);
		renderer.setVisualTypes(new DialRenderer.Type[] {Type.ARROW, Type.NEEDLE, Type.NEEDLE});
		renderer.setMinValue(-120);
		renderer.setMaxValue(-20);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}
}
