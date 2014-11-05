package com.mdes.mywifi;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class LevelGraph {
//	
//	private WifiMap wifiMap;
//	
////	public LevelGraph(WifiMap wifiMap){
////		this.wifiMap = wifiMap;
////	}
//	
//	public Intent getIntent(Context context){
//		
////		String[] claves = wifiMap.getKeys();
////		for (int i = 0; i < claves.length; i++){
////			//TODO PARA QUE FUNCIONE CON MAS DE UNA RED CAMBIAR 0 POR i
////			wifiMap.getWifi(claves[0]);
////		}
//		
//		int[] xValues = { 1, 2, 3, 4};
//		int[] yValues = {1, 1, 1, 1};
//		
//		TimeSeries series = new TimeSeries("Line1");
//		for (int i = 0; i < xValues.length; i++){
//			series.add(xValues[i], yValues[i]);
//		}
//		//collects all your series under one object
//		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
//		dataSet.addSeries(series);
//		//dataSet.addSeries(series2); ....
//		
//		//Propiedades de las líneas
//		//cada renderer para una serie -> una colleccion de renderers
//		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
//		XYSeriesRenderer renderer = new XYSeriesRenderer();
//		mRenderer.addSeriesRenderer(renderer);
//		
//		Intent intent = ChartFactory.getLineChartIntent(context, dataSet, mRenderer, "Line Graph Title");
//		return intent;
//	}
//}
///////////////////////////////////////////////////////////////////////////

	public Intent getIntent(Context context) {
		
		// Our first data
		int[] x = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }; // x values!
		int[] y =  { 30, 34, 45, 57, 77, 89, 100, 111 ,123 ,145 }; // y values!
		TimeSeries series = new TimeSeries("Line1"); 
		for( int i = 0; i < x.length; i++)
		{
			series.add(x[i], y[i]);
		}
		
		// Our second data
		int[] x2 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }; // x values!
		int[] y2 =  { 145, 123, 111, 100, 89, 77, 57, 45, 34, 30}; // y values!
		TimeSeries series2 = new TimeSeries("Line2"); 
		for( int i = 0; i < x2.length; i++)
		{
			series2.add(x2[i], y2[i]);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		dataset.addSeries(series2);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
		XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be used to customize line 1
		XYSeriesRenderer renderer2 = new XYSeriesRenderer(); // This will be used to customize line 2
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.addSeriesRenderer(renderer2);
		
		// Customization time for line 1!
		renderer.setColor(Color.WHITE);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		// Customization time for line 2!
		renderer2.setColor(Color.YELLOW);
		renderer2.setPointStyle(PointStyle.DIAMOND);
		renderer2.setFillPoints(true);
		
		Intent intent = ChartFactory.getLineChartIntent(context, dataset, mRenderer, "Line Graph Title");
		return intent;
		
	}

}
