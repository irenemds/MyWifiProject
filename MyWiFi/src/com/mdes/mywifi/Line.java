package com.mdes.mywifi;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class Line {

	private GraphicalView view;
	
	private TimeSeries dataset; 
	private static XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	
	private XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be used to customize line 1
	private static XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
	
	public Line(Wifi wifi)
	{
		dataset = new TimeSeries(wifi.getSSID()); 
		mDataset.addSeries(dataset);
		
		
		renderer.setColor(Color.MAGENTA);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		
		// Enable Zoom
		mRenderer.setZoomButtonsVisible(true);
//		mRenderer.setXTitle("Day #");
		mRenderer.setYTitle("Potencia [dBm]");
		
		// Add single renderer to multiple renderer
		mRenderer.addSeriesRenderer(renderer);	
	}
	
	public GraphicalView getView(Context context) 
	{
		view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
		return view;
	}
	
	public void addNewPoints(Point p)
	{
		dataset.add(p.getX(), p.getY());
	}
	
}