package com.mdes.mywifi;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.mdes.mywifi.chart.LinkSpeedGraphActivity;
import com.mdes.mywifi.chart.MultipleGraph;

import android.content.Context;
import android.graphics.Color;

public class Line {

	private GraphicalView view;
	public static int lineNumber;
	private TimeSeries dataset; 
	private XYSeriesRenderer renderer = new XYSeriesRenderer();
	private int[] colors = {Color.MAGENTA, Color.WHITE, Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW};
	
	public Line(String wifi)
	{
		dataset = new TimeSeries(wifi); 
		MultipleGraph.mDataset.addSeries(dataset);
		lineNumber++;
		setColor();
		renderer.setFillPoints(true);
		renderer.setLineWidth(4);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setDisplayChartValues(true);
		renderer.setDisplayChartValuesDistance(10);

		// Add single renderer to multiple renderer
		MultipleGraph.mRenderer.addSeriesRenderer(renderer);	
	}

	public Line()
	{
		dataset = new TimeSeries(HiloWifi.currentAP.getSSID()); 
		LinkSpeedGraphActivity.mDataset.addSeries(dataset);
		setColor();
		renderer.setFillPoints(true);
		renderer.setLineWidth(4);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setDisplayChartValues(true);
		renderer.setDisplayChartValuesDistance(10);

		// Add single renderer to multiple renderer
		LinkSpeedGraphActivity.mRenderer.addSeriesRenderer(renderer);	
	}
	
	
	public void addNewPoints(Point p)
	{
		dataset.add(p.getX(), p.getY());
	}

	public void setColor(){
		int lineNumberAux = lineNumber;
		if(lineNumberAux > colors.length-1){
			lineNumberAux = lineNumber-colors.length;
		}
		renderer.setColor(colors[lineNumber]);
	}
}