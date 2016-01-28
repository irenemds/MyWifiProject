package com.mdes.mywifi.chart;
/**
 * Esta clase se emplea para centralizar el control de las gráficas lineales de la aplicación.
 *
 */
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

import com.mdes.mywifi.generic.Line;
import com.mdes.mywifi.wifi.Wifi;
import com.mdes.mywifi.wifi.WifiMap;

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
		mRenderer.setAxisTitleTextSize(25);
	    mRenderer.setLabelsTextSize(25);
		mRenderer.setYTitle("Potencia [dBm]");
	    mRenderer.setLegendTextSize(20);
	    mRenderer.setMargins(new int[] { 50, 40, 10, 30 });
	    mRenderer.setPointSize(5);
	    mRenderer.setZoomEnabled(false);
	    mRenderer.setPanEnabled(false);

		
	}
	
	public static void createGraph(){
		for( int i = 0; i < WifiMap.representableArray.length; i++){
			String BSSID = WifiMap.representableArray[i];
			Wifi wifi = WifiMap.wifiMap.get(BSSID);
			addSingleLine(wifi);	
		}
		
	}
	
	public static void addFreqLine(Wifi wifi){
		BandWidthLine bwLine = wifi.getBwLine();
		bwLine.setShown(true);
		FrequencyGraphActivity.mDataset.addSeries(bwLine.getDataset());
		FrequencyGraphActivity.mRenderer.addSeriesRenderer(bwLine.getRenderer());	
	}
	
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
