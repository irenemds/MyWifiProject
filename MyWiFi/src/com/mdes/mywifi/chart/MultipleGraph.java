package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.mdes.mywifi.WifiMap;

import android.content.Context;
import android.graphics.Color;

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
	    mRenderer.setAxisTitleTextSize(20);
	    mRenderer.setChartTitle("Gráfica de Potencias");
	    mRenderer.setChartTitleTextSize(27);
		mRenderer.setAxisTitleTextSize(20);
	    mRenderer.setLabelsTextSize(20);
		mRenderer.setYTitle("Potencia [dBm]");
	    mRenderer.setLegendTextSize(20);
	    mRenderer.setMargins(new int[] { 50, 40, 10, 30 });
	    mRenderer.setPointSize(5);

		
	}
	
}
