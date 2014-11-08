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
	private XYSeriesRenderer renderer = new XYSeriesRenderer();
	
	public Line(String wifi)
	{
		dataset = new TimeSeries(wifi); 
		MultipleGraph.mDataset.addSeries(dataset);
		
		
		renderer.setColor(Color.MAGENTA);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		
		// Enable Zoom
		MultipleGraph.mRenderer.setZoomButtonsVisible(true);
//		mRenderer.setXTitle("Day #");
		MultipleGraph.mRenderer.setYTitle("Potencia [dBm]");
		
		// Add single renderer to multiple renderer
		MultipleGraph.mRenderer.addSeriesRenderer(renderer);	
	}
	
	public void addNewPoints(Point p)
	{
		dataset.add(p.getX(), p.getY());
	}
	
}