package com.mdes.mywifi.chart;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

import com.mdes.mywifi.BandWidthLine;
import com.mdes.mywifi.Line;
import com.mdes.mywifi.Wifi;
import com.mdes.mywifi.WifiMap;

public class MultipleGraph {

	public static GraphicalView view;
	public static XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	public static XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	

	public MultipleGraph(){
		mRenderer.setYAxisMax(0);
		mRenderer.setYAxisMin(-100);
		mRenderer.setXAxisMin(1);
		mRenderer.setXAxisMax(16);
		mRenderer.setClickEnabled(false);
		mRenderer.setShowGrid(true);
	    mRenderer.setApplyBackgroundColor(true);
	    mRenderer.setBackgroundColor(Color.BLACK);
	    mRenderer.setAxisTitleTextSize(20);;
		mRenderer.setAxisTitleTextSize(20);
	    mRenderer.setLabelsTextSize(20);
		mRenderer.setYTitle("Potencia [dBm]");
	    mRenderer.setLegendTextSize(20);
	    mRenderer.setMargins(new int[] { 50, 40, 10, 30 });
	    mRenderer.setPointSize(5);

		
	}
	
	public static void createGraph(){
		for( int i = 0; i < WifiMap.representableArray.length; i++){
			String BSSID = WifiMap.representableArray[i];
			Wifi wifi = WifiMap.wifiMap.get(BSSID);
			addSingleLine(wifi);	
		}
		
	}

//	public static void createFreqGraph(){
//Log.i("BW", "Representar: "+WifiMap.representableArray.length);
//		for( int i = 0; i < WifiMap.representableArray.length; i++){
//			String BSSID = WifiMap.representableArray[i];
//			Wifi wifi = WifiMap.wifiMap.get(BSSID);		
//			addFreqLine(wifi);	
//		}
//		
//	}
	
	public static void addFreqLine(Wifi wifi){
		BandWidthLine bwLine = wifi.getBwLine();
		bwLine.setShown(true);
		FrequencyGraphActivity.mDataset.addSeries(bwLine.getDataset());
		FrequencyGraphActivity.mRenderer.addSeriesRenderer(bwLine.getRenderer());	
	}
//	
//	public static void deleteFreqLine(Wifi wifi){
//		BandWidthLine bwLine = wifi.getBwLine();
//		bwLine.setShown(false);
//		FrequencyGraphActivity.mDataset.removeSeries(bwLine.getDataset());
//		FrequencyGraphActivity.mRenderer.removeSeriesRenderer(bwLine.getRenderer());	
//	}
	
//	public static void deleteFreqGraph(){
//		FrequencyGraphActivity.mDataset.clear();
//		FrequencyGraphActivity.mRenderer.removeAllRenderers();
//		WifiMap.resetLines();
//}
	
	public static void deleteGraph(){ 
			mDataset.clear();
			mRenderer.removeAllRenderers();
			WifiMap.resetLines();
	}
	
	public static void addSingleLine(Wifi wifi){
		Line line = wifi.getLine();
		line.setShown(true);
		mDataset.addSeries(line.getDataset());
		mRenderer.addSeriesRenderer(line.getRenderer());	
	}
}
